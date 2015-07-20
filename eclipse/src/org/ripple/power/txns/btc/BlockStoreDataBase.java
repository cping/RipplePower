package org.ripple.power.txns.btc;

import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ripple.power.Helper;
import org.ripple.power.config.LSystem;
import org.ripple.power.utils.FileUtils;

public class BlockStoreDataBase extends BlockStore {

	/** Settings table definition */
	public static final String Settings_Table = "CREATE TABLE IF NOT EXISTS Settings ("
			+ "schema_name            VARCHAR(32)     NOT NULL," + // Schema
																	// name
			"schema_version         INTEGER         NOT NULL)"; // Schema
																// version

	/** Blocks table definition */
	public static final String Blocks_Table = "CREATE TABLE IF NOT EXISTS Blocks ("
			+ "db_id              IDENTITY," + // Database identity
			"block_hash_index   BIGINT          NOT NULL," + // Block hash index
			"block_hash         BINARY          NOT NULL," + // Block hash
			"prev_hash_index    BIGINT          NOT NULL," + // Previous hash
																// index
			"prev_hash          BINARY          NOT NULL," + // Previous hash
			"timestamp          BIGINT          NOT NULL," + // Block timestamp
			"block_height       INTEGER         NOT NULL," + // Block height or
																// -1
			"chain_work         BINARY          NOT NULL," + // Cumulative chain
																// work
			"on_hold            BOOLEAN         NOT NULL," + // Block is held
			"file_number        INTEGER         NOT NULL," + // Block file
																// number
			"file_offset        INTEGER         NOT NULL," + // Block offset
																// within block
																// file
			"header             BINARY          NOT NULL)"; // Block header
	public static final String Blocks_IX1 = "CREATE INDEX IF NOT EXISTS Blocks_IX1 on Blocks(block_hash_index)";
	public static final String Blocks_IX2 = "CREATE INDEX IF NOT EXISTS Blocks_IX2 ON Blocks(prev_hash_index)";
	public static final String Blocks_IX3 = "CREATE INDEX IF NOT EXISTS Blocks_IX3 ON Blocks(block_height)";

	/** TxOutputs table definition */
	public static final String TxOutputs_Table = "CREATE TABLE IF NOT EXISTS TxOutputs ("
			+ "db_id              IDENTITY," + // Database identity
			"tx_hash_index      BIGINT          NOT NULL," + // Transaction hash
																// index
			"tx_hash            BINARY          NOT NULL," + // Transaction hash
			"tx_index           SMALLINT        NOT NULL," + // Output index
			"block_hash         BINARY          NOT NULL," + // Block hash
			"block_height       INTEGER         NOT NULL," + // Block height
																// when output
																// spent
			"time_spent         BIGINT          NOT NULL," + // Time when output
																// spent
			"is_coinbase        BOOLEAN         NOT NULL," + // Coinbase
																// transaction
			"value              BIGINT          NOT NULL," + // Value
			"script_bytes       BINARY          NOT NULL)"; // Script bytes
	public static final String TxOutputs_IX1 = "CREATE INDEX IF NOT EXISTS TxOutputs_IX1 ON TxOutputs(tx_hash_index)";

	/** TxSpentOutputs table definition */
	public static final String TxSpentOutputs_Table = "CREATE TABLE IF NOT EXISTS TxSpentOutputs ("
			+ "time_spent         BIGINT          NOT NULL," + // Time when
																// output spent
			"db_id              BIGINT          NOT NULL " + // Referenced spent
																// output
			"REFERENCES TxOutputs(db_id) ON DELETE CASCADE)";
	public static final String TxSpentOutputs_IX1 = "CREATE INDEX IF NOT EXISTS TxSpentOutputs_IX1 ON TxSpentOutputs(time_spent)";

	/** Alerts table definition */
	public static final String Alerts_Table = "CREATE TABLE IF NOT EXISTS Alerts ("
			+ "alert_id           INTEGER         NOT NULL " + // Alert
																// identifier
			"PRIMARY KEY," + "is_cancelled       BOOLEAN         NOT NULL," + // Alert
																				// cancelled
			"payload            BINARY          NOT NULL," + // Payload
			"signature          BINARY          NOT NULL)"; // Signature

	/** Addresses table definitions */
	private static final String Addresses_Table = "CREATE TABLE IF NOT EXISTS Addresses ("
			+ "db_id                    IDENTITY," // Row identity
			+ "address                  BINARY NOT NULL," // Bitcoin address
			+ "label                    VARCHAR)"; // Associated label or null

	/** Keys table definitions */
	private static final String Keys_Table = "CREATE TABLE IF NOT EXISTS Keys ("
			+ "db_id                    IDENTITY," // Row identity
			+ "public_key               BINARY NOT NULL," // Public key
			+ "private_key              BINARY NOT NULL," // Encrypted private
															// key
			+ "timestamp                BIGINT NOT NULL," // Time key created
			+ "label                    VARCHAR," // Associated label or null
			+ "is_change                BOOLEAN NOT NULL)"; // Is a change key

	/** Received table definitions */
	private static final String Received_Table = "CREATE TABLE IF NOT EXISTS Received ("
			+ "db_id                    IDENTITY," // Row identity
			+ "tx_hash_index            BIGINT NOT NULL," // Transaction hash
															// index
			+ "tx_hash                  BINARY NOT NULL," // Transaction hash
			+ "tx_index                 SMALLINT NOT NULL," // Transaction
															// output index
			+ "norm_hash                BINARY NOT NULL," // Normalized
															// transaction hash
			+ "timestamp                BIGINT NOT NULL," // Transaction
															// timestamp
			+ "block_hash               BINARY," // Block containing the
													// transaction or null
			+ "address                  BINARY NOT NULL," // Recipient address
			+ "value                    BIGINT NOT NULL," // Transaction value
			+ "script_bytes             BINARY NOT NULL," // Transaction output
															// script bytes
			+ "is_spent                 BOOLEAN NOT NULL," // Transaction output
															// is spent
			+ "is_change                BOOLEAN NOT NULL," // Address is a
															// change address
			+ "in_safe                  BOOLEAN NOT NULL," // Transaction output
															// is in the safe
			+ "is_coinbase              BOOLEAN NOT NULL," // Transaction is
															// coinbase
															// transaction
			+ "is_deleted               BOOLEAN NOT NULL)"; // Transaction
															// output is deleted
	private static final String Received_IX1 = "CREATE INDEX IF NOT EXISTS Received_IX1 ON Received(tx_hash_index)";

	/** Sent table definitions */
	private static final String Sent_Table = "CREATE TABLE IF NOT EXISTS Sent ("
			+ "db_id                    IDENTITY," // Row identity
			+ "tx_hash_index            BIGINT NOT NULL," // Transaction hash
															// index
			+ "tx_hash                  BINARY NOT NULL," // Transaction hash
			+ "norm_hash                BINARY NOT NULL," // Normalized
															// transaction hash
			+ "timestamp                BIGINT NOT NULL," // Transaction
															// timestamp
			+ "block_hash               BINARY," // Block containing the
													// transaction or null
			+ "address                  BINARY NOT NULL," // Recipient address
			+ "value                    BIGINT NOT NULL," // Transaction value
			+ "fee                      BIGINT NOT NULL," // Transaction fee
			+ "is_deleted               BOOLEAN NOT NULL," // Transaction is
															// deleted
			+ "tx_data                  BINARY NOT NULL)"; // Transaction data
	private static final String Sent_IX1 = "CREATE UNIQUE INDEX IF NOT EXISTS Sent_IX1 ON Sent(tx_hash_index)";
	/** Database schema name */
	public static final String schemaName = LSystem.applicationName
			+ " Block Store";

	/** Database schema version */
	public static final int schemaVersion = 102;

	/** Per-thread database connection */
	private final ThreadLocal<Connection> threadConnection = new ThreadLocal<>();

	/** List of all database connections */
	private final List<Connection> allConnections = Collections
			.synchronizedList(new ArrayList<Connection>());

	/** Database connection URL */
	private final String connectionURL;

	/**
	 * Create a BlockStore
	 *
	 * @param dataPath
	 *            Application data path
	 * @throws BlockStoreException
	 *             Unable to initialize the database
	 */
	public BlockStoreDataBase(String dataPath) throws BlockStoreException {
		super(dataPath);
		long maxMemory = Runtime.getRuntime().maxMemory() / (1024 * 1024);
		long dbCacheSize;
		if (maxMemory < 256)
			dbCacheSize = 64;
		else if (maxMemory < 384)
			dbCacheSize = 128;
		else
			dbCacheSize = 256;
		String databasePath = dataPath.replace('\\', '/');
		connectionURL = String.format("jdbc:h2:%s/Database/bitcoin;"
				+ "DB_CLOSE_ON_EXIT=FALSE;MVCC=TRUE;CACHE_SIZE=%d",
				databasePath, dbCacheSize * 1024);
		BTCLoader.info("Database connection URL: " + connectionURL);
		//
		// Load the JDBC driver
		//
		try {
			Class.forName("org.h2.Driver");
		} catch (ClassNotFoundException exc) {
			BTCLoader.error("Unable to load the JDBC driver", exc);
			throw new BlockStoreException("Unable to load the JDBC driver", exc);
		}
		//
		// Initialize the database
		//
		if (tableExists("Settings")) {
			getSettings();
		} else {
			createTables();
			initTables();
		}
	}

	/**
	 * Close the database
	 */
	@Override
	public void close() {
		int index = 0;
		for (Connection conn : allConnections) {
			index++;
			try {
				conn.close();
				BTCLoader.info(String.format("Database connection %d closed",
						index));
			} catch (SQLException exc) {
				BTCLoader.error(String.format(
						"SQL error while closing connection %d", index), exc);
			}
		}
		allConnections.clear();
	}

	/**
	 * Get the database connection for the current thread
	 *
	 * @return Connection for the current thread
	 * @throws BlockStoreException
	 *             Unable to obtain a database connection
	 */
	private Connection getConnection() throws BlockStoreException {
		Connection conn;
		synchronized (lock) {
			try {
				conn = threadConnection.get();
				if (conn == null || conn.isClosed()) {
					threadConnection.set(DriverManager.getConnection(
							connectionURL, "SCRIPTERRON", "Bitcoin"));
					conn = threadConnection.get();
					allConnections.add(conn);
					BTCLoader.info(String.format(
							"Database connection %d created",
							allConnections.size()));
				}
			} catch (SQLException exc) {
				BTCLoader.error(String.format(
						"Unable to connect to SQL database %s", connectionURL),
						exc);
				throw new BlockStoreException(
						"Unable to connect to SQL database");
			}
		}
		return conn;
	}

	/**
	 * Rollback the current transaction and turn auto commit back on
	 *
	 * @param stmt
	 *            Statement to be closed or null
	 */
	private void rollback(AutoCloseable... stmts) {
		try {
			Connection conn = getConnection();
			for (AutoCloseable stmt : stmts)
				if (stmt != null)
					stmt.close();
			conn.rollback();
			conn.setAutoCommit(true);
		} catch (Exception exc) {
			BTCLoader.error("Unable to rollback transaction", exc);
		}
	}

	/**
	 * Get the hash index for a SHA-256 hash
	 *
	 * @param hash
	 *            SHA-256 hash
	 * @return Hash index
	 */
	private long getHashIndex(Sha256Hash hash) {
		byte[] bytes = hash.getBytes();
		return (((long) bytes[24] & 0xffL) << 56)
				| (((long) bytes[25] & 0xffL) << 48)
				| (((long) bytes[26] & 0xffL) << 40)
				| (((long) bytes[27] & 0xffl) << 32)
				| (((long) bytes[28] & 0xffL) << 24)
				| (((long) bytes[29] & 0xffL) << 16)
				| (((long) bytes[30] & 0xffL) << 8)
				| ((long) bytes[31] & 0xffL);
	}

	/**
	 * Compacts the database tables
	 *
	 * All database connections will be closed and the database will be deleted
	 * and recreated. This means the database should be compacted only in
	 * single-thread maintenance mode.
	 *
	 * @throws BlockStoreException
	 *             Unable to compact database
	 */
	@Override
	public void compactDatabase() throws BlockStoreException {
		File backupFile = new File(String.format("%s%sDatabase%sbackup.sql.gz",
				dataPath, LSystem.FS, LSystem.FS));
		File databaseFile = new File(
				String.format("%s%sDatabase%sbitcoin.mv.db", dataPath,
						LSystem.FS, LSystem.FS));
		File lockFile = new File(String.format("%s%sDatabase%sbitcoin.lock.db",
				dataPath, LSystem.FS, LSystem.FS));
		Connection conn = getConnection();
		ResultSet r;
		//
		// Delete spent transaction outputs before creating the backup script
		//
		long ageLimit = Math.max(chainTime - MAX_TX_AGE, 0);
		int deletedCount = 0;
		BTCLoader.info("Deleting spent transaction outputs");
		try (PreparedStatement s = conn
				.prepareStatement("DELETE FROM TxOutputs WHERE db_id IN "
						+ "(SELECT db_id FROM TxSpentOutputs WHERE time_spent<? LIMIT 2000)")) {
			int count;
			do {
				s.setLong(1, ageLimit);
				count = s.executeUpdate();
				deletedCount += count;
				if (deletedCount % 100000 == 0)
					BTCLoader.info(String.format(
							"Deleted %,d spent transaction outputs",
							deletedCount));
			} while (count > 0);
		} catch (SQLException exc) {
			BTCLoader.error("Unable to delete spent transaction outputs", exc);
			throw new BlockStoreException(
					"Unable to delete spent transaction outputs");
		}
		BTCLoader.info(String.format("%,d spent transaction outputs deleted",
				deletedCount));
		//
		// Create the SQL backup script
		//
		try {
			BTCLoader.info("Creating the SQL backup script");
			if (backupFile.exists())
				backupFile.delete();
			try (Statement s = conn.createStatement()) {
				r = s.executeQuery(String.format(
						"SCRIPT TO '%s' COMPRESSION GZIP CHARSET 'UTF-8'",
						backupFile.getPath()));
				while (r.next())
					BTCLoader.debug(r.getString(1));
				r.close();
			}
			BTCLoader.info("SQL backup script created");
		} catch (SQLException exc) {
			BTCLoader.error("Unable to create the SQL backup script", exc);
			throw new BlockStoreException(
					"Unable to create the SQL backup script");
		}
		//
		// Close and delete the database
		//
		close();
		threadConnection.set(null);
		BTCLoader.info("Database closed");
		if (lockFile.exists()) {
			BTCLoader.info("Waiting for database lock to be released");
			try {
				while (lockFile.exists())
					Thread.sleep(1000);
			} catch (InterruptedException exc) {
				BTCLoader.error("Interrupted while waiting on database lock");
				throw new BlockStoreException(
						"Interrupted while waiting on database lock");
			}
			BTCLoader.info("Database lock released");
		}
		databaseFile.delete();
		BTCLoader.info("Database deleted");
		//
		// Create the new database
		//
		BTCLoader.info("Creating database from SQL backup script");
		conn = getConnection();
		try (Statement s = conn.createStatement()) {
			int count = s.executeUpdate(String.format(
					"RUNSCRIPT FROM '%s' COMPRESSION GZIP CHARSET 'UTF-8'",
					backupFile.getPath()));
			BTCLoader.debug(String.format("%d database updates completed",
					count));
		} catch (SQLException exc) {
			BTCLoader.error("Unable to create database from SQL backup script",
					exc);
			throw new BlockStoreException(
					"Unable to create database from SQL backup script");
		}
		BTCLoader.info("Database created");
	}

	/**
	 * Check if the block is already in the database
	 *
	 * @param blockHash
	 *            The block to check
	 * @return TRUE if this is a new block
	 * @throws BlockStoreException
	 *             Unable to check the block status
	 */
	@Override
	public boolean isNewBlock(Sha256Hash blockHash) throws BlockStoreException {
		boolean isNewBlock;
		Connection conn = getConnection();
		try (PreparedStatement s = conn
				.prepareStatement("SELECT 1 FROM Blocks "
						+ "WHERE block_hash_index=? AND block_hash=?")) {
			s.setLong(1, getHashIndex(blockHash));
			s.setBytes(2, blockHash.getBytes());
			ResultSet r = s.executeQuery();
			isNewBlock = !r.next();
		} catch (SQLException exc) {
			BTCLoader
					.error(String.format(
							"Unable to check block status\n  Block %s",
							blockHash), exc);
			throw new BlockStoreException("Unable to check block status",
					blockHash);
		}
		return isNewBlock;
	}

	/**
	 * Check if the alert is already in our database
	 *
	 * @param alertID
	 *            Alert identifier
	 * @return TRUE if this is a new alert
	 * @throws BlockStoreException
	 *             Unable to get the alert status
	 */
	@Override
	public boolean isNewAlert(int alertID) throws BlockStoreException {
		boolean isNewAlert;
		Connection conn = getConnection();
		try (PreparedStatement s = conn
				.prepareStatement("SELECT 1 FROM Alerts WHERE alert_id=?")) {
			s.setInt(1, alertID);
			ResultSet r = s.executeQuery();
			isNewAlert = !r.next();
		} catch (SQLException exc) {
			BTCLoader.error(String.format(
					"Unable to check alert status for %d", alertID), exc);
			throw new BlockStoreException("Unable to check alert status");
		}
		return isNewAlert;
	}

	/**
	 * Return a list of all alerts in the database
	 *
	 * @return List of all alerts
	 * @throws BlockStoreException
	 *             Unable to get alerts from database
	 */
	@Override
	public List<Alert> getAlerts() throws BlockStoreException {
		List<Alert> alertList = new LinkedList<>();
		Connection conn = getConnection();
		try (Statement s = conn.createStatement()) {
			ResultSet r = s
					.executeQuery("SELECT is_cancelled,payload,signature FROM Alerts ORDER BY alert_id ASC");
			while (r.next()) {
				boolean isCancelled = r.getBoolean(1);
				byte[] payload = r.getBytes(2);
				byte[] signature = r.getBytes(3);
				Alert alert = new Alert(payload, signature);
				alert.setCancel(isCancelled);
				alertList.add(alert);
			}
		} catch (IOException | SQLException exc) {
			BTCLoader.error("Unable to build alert list", exc);
			throw new BlockStoreException("Unable to build alert list");
		}
		return alertList;
	}

	/**
	 * Store an alert in the database
	 *
	 * @param alert
	 *            The alert
	 * @throws BlockStoreException
	 *             Unable to store the alert
	 */
	@Override
	public void storeAlert(Alert alert) throws BlockStoreException {
		Connection conn = getConnection();
		try (PreparedStatement s = conn
				.prepareStatement("INSERT INTO Alerts "
						+ "(alert_id,is_cancelled,payload,signature) VALUES(?,false,?,?)")) {
			s.setInt(1, alert.getID());
			s.setBytes(2, alert.getPayload());
			s.setBytes(3, alert.getSignature());
			s.executeUpdate();
		} catch (SQLException exc) {
			BTCLoader.error(
					String.format("Unable to store alert %d", alert.getID()),
					exc);
			throw new BlockStoreException("Unable to store alert");
		}
	}

	/**
	 * Cancel an alert
	 *
	 * @param alertID
	 *            The alert identifier
	 * @throws BlockStoreException
	 *             Unable to update the alert
	 */
	@Override
	public void cancelAlert(int alertID) throws BlockStoreException {
		Connection conn = getConnection();
		try (PreparedStatement s = conn
				.prepareStatement("UPDATE Alerts SET is_cancelled=true WHERE alert_id=?")) {
			s.setInt(1, alertID);
			s.executeUpdate();
		} catch (SQLException exc) {
			BTCLoader.error(
					String.format("Unable to cancel alert %d", alertID), exc);
			throw new BlockStoreException("Unable to cancel alert");
		}
	}

	/**
	 * Check if the block is on the block chain
	 *
	 * @param blockHash
	 *            The block to check
	 * @return TRUE if the block is on the block chain
	 * @throws BlockStoreException
	 *             Unable to get the block status
	 */
	@Override
	public boolean isOnChain(Sha256Hash blockHash) throws BlockStoreException {
		boolean onChain;
		Connection conn = getConnection();
		try (PreparedStatement s = conn
				.prepareStatement("SELECT block_height from Blocks "
						+ "WHERE block_hash_index=? AND block_hash=?")) {
			s.setLong(1, getHashIndex(blockHash));
			s.setBytes(2, blockHash.getBytes());
			ResultSet r = s.executeQuery();
			onChain = (r.next() && r.getInt(1) >= 0);
		} catch (SQLException exc) {
			BTCLoader
					.error(String.format(
							"Unable to check block status\n  Block %s",
							blockHash), exc);
			throw new BlockStoreException("Unable to check block status",
					blockHash);
		}
		return onChain;
	}

	/**
	 * Return a block stored in the database. The returned block represents the
	 * block data sent over the wire and does not include any information about
	 * the block location within the block chain.
	 *
	 * @param blockHash
	 *            Block hash
	 * @return The block or null if the block is not found
	 * @throws BlockStoreException
	 *             Unable to get block from database
	 */
	@Override
	public Block getBlock(Sha256Hash blockHash) throws BlockStoreException {
		Block block = null;
		Connection conn = getConnection();
		try (PreparedStatement s = conn
				.prepareStatement("SELECT file_number,file_offset FROM Blocks "
						+ "WHERE block_hash_index=? AND block_hash=?")) {
			s.setLong(1, getHashIndex(blockHash));
			s.setBytes(2, blockHash.getBytes());
			ResultSet r = s.executeQuery();
			if (r.next()) {
				int fileNumber = r.getInt(1);
				int fileOffset = r.getInt(2);
				block = getBlock(fileNumber, fileOffset);
			}
		} catch (SQLException exc) {
			BTCLoader
					.error(String.format("Unable to get block\n  Block %s",
							blockHash), exc);
			throw new BlockStoreException("Unable to get block", blockHash);
		}
		return block;
	}

	/**
	 * Returns the block hash for the block stored at the specified height.
	 *
	 * @param height
	 *            Chain height
	 * @return The block hash or null if the block is not found
	 * @throws BlockStoreException
	 *             Unable to get block from database
	 */
	@Override
	public Sha256Hash getBlockId(int height) throws BlockStoreException {
		Sha256Hash blockHash = null;
		Connection conn = getConnection();
		try (PreparedStatement s = conn
				.prepareStatement("SELECT block_hash FROM Blocks WHERE block_height=?")) {
			s.setInt(1, height);
			ResultSet r = s.executeQuery();
			if (r.next())
				blockHash = new Sha256Hash(r.getBytes(1));
		} catch (SQLException exc) {
			BTCLoader
					.error(String
							.format("Unable to get block hash from database: Height %d",
									height), exc);
			throw new BlockStoreException(
					"Unable to get block hash from database");
		}
		return blockHash;
	}

	/**
	 * Return a block stored in the database. The returned block contains the
	 * basic block plus information about its current location within the block
	 * chain.
	 *
	 * @param blockHash
	 *            The block hash
	 * @return The stored block or null if the block is not found
	 * @throws BlockStoreException
	 *             Unable to get block from database
	 */
	@Override
	public StoredBlock getStoredBlock(Sha256Hash blockHash)
			throws BlockStoreException {
		StoredBlock storedBlock = null;
		Connection conn = getConnection();
		try (PreparedStatement s = conn
				.prepareStatement("SELECT block_height,chain_work,on_hold,"
						+ "file_number,file_offset FROM Blocks "
						+ "WHERE block_hash_index=? AND block_hash=?")) {
			s.setLong(1, getHashIndex(blockHash));
			s.setBytes(2, blockHash.getBytes());
			ResultSet r = s.executeQuery();
			if (r.next()) {
				int blockHeight = r.getInt(1);
				BigInteger blockWork = new BigInteger(r.getBytes(2));
				boolean onHold = r.getBoolean(3);
				int fileNumber = r.getInt(4);
				int fileOffset = r.getInt(5);
				Block block = getBlock(fileNumber, fileOffset);
				if (block != null)
					storedBlock = new StoredBlock(block, blockWork,
							blockHeight, (blockHeight >= 0), onHold);
			}
		} catch (SQLException exc) {
			BTCLoader
					.error(String.format("Unable to get block\n  Block %s",
							blockHash), exc);
			throw new BlockStoreException("Unable to get block", blockHash);
		}
		return storedBlock;
	}

	/**
	 * Return the child block for the specified block. If the block has multiple
	 * children, the child block that is on the chain will be returned.
	 *
	 * @param blockHash
	 *            The block hash
	 * @return The stored block or null if the block is not found
	 * @throws BlockStoreException
	 *             Unable to get block
	 */
	@Override
	public StoredBlock getChildStoredBlock(Sha256Hash blockHash)
			throws BlockStoreException {
		StoredBlock childStoredBlock = null;
		Connection conn = getConnection();
		try (PreparedStatement s = conn
				.prepareStatement("SELECT block_height,chain_work,on_hold,file_number,file_offset "
						+ "FROM Blocks WHERE prev_hash_index=? AND prev_hash=?")) {
			s.setLong(1, getHashIndex(blockHash));
			s.setBytes(2, blockHash.getBytes());
			ResultSet r = s.executeQuery();
			while (r.next()) {
				int blockHeight = r.getInt(1);
				if (blockHeight < 0 && childStoredBlock != null)
					continue;
				BigInteger blockWork = new BigInteger(r.getBytes(2));
				boolean onHold = r.getBoolean(3);
				int fileNumber = r.getInt(4);
				int fileOffset = r.getInt(5);
				Block block = getBlock(fileNumber, fileOffset);
				if (block != null)
					childStoredBlock = new StoredBlock(block, blockWork,
							blockHeight, (blockHeight >= 0), onHold);
				if (blockHeight >= 0)
					break;
			}
		} catch (SQLException exc) {
			BTCLoader.error(String.format(
					"Unable to get child block\n  Block %s", blockHash), exc);
			throw new BlockStoreException("Unable to get child block",
					blockHash);
		}
		return childStoredBlock;
	}

	/**
	 * Return the block status for recent blocks
	 *
	 * @param maxCount
	 *            The maximum number of blocks to be returned
	 * @return A list of BlockStatus objects
	 * @throws BlockStoreException
	 *             Unable to get block status
	 */
	@Override
	public List<BlockStatus> getBlockStatus(int maxCount)
			throws BlockStoreException {
		List<BlockStatus> blockList = new LinkedList<>();
		Connection conn = getConnection();
		try (PreparedStatement s = conn
				.prepareStatement("SELECT block_hash,timestamp,block_height,on_hold,header FROM Blocks "
						+ "WHERE block_height>=? ORDER BY timestamp DESC")) {
			s.setInt(1, Math.max(chainHeight - maxCount - 1, 0));
			ResultSet r = s.executeQuery();
			while (r.next()) {
				Sha256Hash blockHash = new Sha256Hash(r.getBytes(1));
				long timeStamp = r.getLong(2);
				int blockHeight = r.getInt(3);
				boolean onHold = r.getBoolean(4);
				byte[] header = r.getBytes(5);
				int version = ((int) header[0] & 255)
						| (((int) header[1] & 255) << 8)
						| (((int) header[2] & 255) << 16)
						| (((int) header[3] & 255) << 24);
				BlockStatus status = new BlockStatus(blockHash, timeStamp,
						blockHeight, version, (blockHeight >= 0), onHold);
				blockList.add(status);
			}
		} catch (SQLException exc) {
			BTCLoader.error("Unable to get block status", exc);
			throw new BlockStoreException("Unable to get block status");
		}
		return blockList;
	}

	/**
	 * Check if this is a new transaction
	 *
	 * @param txHash
	 *            Transaction hash
	 * @return TRUE if the transaction is not in the database
	 * @throws BlockStoreException
	 *             Unable to check transaction status
	 */
	@Override
	public boolean isNewTransaction(Sha256Hash txHash)
			throws BlockStoreException {
		boolean isNew;
		Connection conn = getConnection();
		try (PreparedStatement s = conn
				.prepareStatement("SELECT 1 FROM TxOutputs "
						+ "WHERE tx_hash_index=? AND tx_hash=? LIMIT 1")) {
			s.setLong(1, getHashIndex(txHash));
			s.setBytes(2, txHash.getBytes());
			ResultSet r = s.executeQuery();
			isNew = !r.next();
		} catch (SQLException exc) {
			BTCLoader.error(String.format(
					"Unable to get transaction status\n  Tx %s",
					txHash.toString()), exc);
			throw new BlockStoreException("Unable to get transaction status");
		}
		return isNew;
	}

	/**
	 * Return the transaction depth. A depth of 0 indicates the transaction is
	 * not in a block on the current chain.
	 *
	 * @param txHash
	 *            Transaction hash
	 * @return Transaction depth
	 * @throws BlockStoreException
	 *             Unable to get transaction depth
	 */
	@Override
	public int getTxDepth(Sha256Hash txHash) throws BlockStoreException {
		int txDepth = 0;
		Connection conn = getConnection();
		try (PreparedStatement s1 = conn
				.prepareStatement("SELECT block_height from Blocks "
						+ "WHERE block_hash_index=? AND block_hash=?");
				PreparedStatement s2 = conn
						.prepareStatement("SELECT block_hash FROM TxOutputs "
								+ "WHERE tx_hash_index=? AND tx_hash=?")) {
			s2.setLong(1, getHashIndex(txHash));
			s2.setBytes(2, txHash.getBytes());
			ResultSet r = s2.executeQuery();
			if (r.next()) {
				Sha256Hash blockHash = new Sha256Hash(r.getBytes(1));
				r.close();
				s1.setLong(1, getHashIndex(blockHash));
				s1.setBytes(2, blockHash.getBytes());
				r = s1.executeQuery();
				if (r.next()) {
					int height = r.getInt(1);
					if (height >= 0)
						txDepth = chainHeight - height + 1;
				}
			}
		} catch (SQLException exc) {
			BTCLoader.error(String.format(
					"Unable to get transaction depth\n  Tx %s", txHash), exc);
			throw new BlockStoreException("Unable to get transaction depth");
		}
		return txDepth;
	}

	/**
	 * Return the requested transaction output
	 *
	 * @param outPoint
	 *            Transaction outpoint
	 * @return Transaction output or null if the transaction is not found
	 * @throws BlockStoreException
	 *             Unable to get transaction output status
	 */
	@Override
	public StoredOutput getTxOutput(OutPoint outPoint)
			throws BlockStoreException {
		StoredOutput output = null;
		Connection conn = getConnection();
		try (PreparedStatement s = conn
				.prepareStatement("SELECT time_spent,value,script_bytes,block_height,is_coinbase "
						+ "FROM TxOutputs WHERE tx_hash_index=? AND tx_hash=? AND tx_index=?")) {
			s.setLong(1, getHashIndex(outPoint.getHash()));
			s.setBytes(2, outPoint.getHash().getBytes());
			s.setShort(3, (short) outPoint.getIndex());
			ResultSet r = s.executeQuery();
			if (r.next()) {
				long timeSpent = r.getLong(1);
				BigInteger value = BigInteger.valueOf(r.getLong(2));
				byte[] scriptBytes = r.getBytes(3);
				int blockHeight = r.getInt(4);
				boolean isCoinbase = r.getBoolean(5);
				output = new StoredOutput(outPoint.getIndex(), value,
						scriptBytes, isCoinbase, (timeSpent != 0), blockHeight);
			}
		} catch (SQLException exc) {
			BTCLoader.error(String.format(
					"Unable to get transaction output\n  Tx %s : Index %d",
					outPoint.getHash(), outPoint.getIndex()), exc);
			throw new BlockStoreException("Unable to get transaction output");
		}
		return output;
	}

	/**
	 * Returns the outputs for the specified transaction
	 *
	 * @param txHash
	 *            Transaction hash
	 * @return Stored output list
	 * @throws BlockStoreException
	 *             Unable to get transaction outputs
	 */
	@Override
	public List<StoredOutput> getTxOutputs(Sha256Hash txHash)
			throws BlockStoreException {
		List<StoredOutput> outputList = new LinkedList<>();
		Connection conn = getConnection();
		try (PreparedStatement s = conn
				.prepareStatement("SELECT tx_index,time_spent,value,script_bytes,block_height,is_coinbase "
						+ "FROM TxOutputs WHERE tx_hash_index=? AND tx_hash=? ORDER BY tx_index ASC")) {
			s.setLong(1, getHashIndex(txHash));
			s.setBytes(2, txHash.getBytes());
			ResultSet r = s.executeQuery();
			while (r.next()) {
				int txIndex = r.getShort(1);
				long timeSpent = r.getLong(2);
				BigInteger value = BigInteger.valueOf(r.getLong(3));
				byte[] scriptBytes = r.getBytes(4);
				int blockHeight = r.getInt(5);
				boolean isCoinbase = r.getBoolean(6);
				StoredOutput output = new StoredOutput(txIndex, value,
						scriptBytes, isCoinbase, (timeSpent != 0), blockHeight);
				outputList.add(output);
			}
		} catch (SQLException exc) {
			BTCLoader.error(String.format(
					"Unable to get transaction outputs\n  Tx %s", txHash), exc);
			throw new BlockStoreException("Unable to get transaction outputs",
					txHash);
		}
		return outputList;
	}

	/**
	 * Deletes spent transaction outputs that are older than the maximum
	 * transaction age
	 *
	 * @return The number of deleted outputs
	 * @throws BlockStoreException
	 *             Unable to delete spent transaction outputs
	 */
	@Override
	public int deleteSpentTxOutputs() throws BlockStoreException {
		Connection conn = getConnection();
		long ageLimit = Math.max(chainTime - MAX_TX_AGE, 0);
		int deletedCount = 0;
		//
		// Delete spent outputs in increments of 1000 to reduce the time that
		// other
		// transactions are locked out of the database
		//
		synchronized (lock) {
			BTCLoader.info("Deleting spent transaction outputs");
			try (PreparedStatement s = conn
					.prepareStatement("DELETE FROM TxOutputs WHERE db_id IN "
							+ "(SELECT db_id FROM TxSpentOutputs WHERE time_spent<? LIMIT 1000)")) {
				s.setLong(1, ageLimit);
				deletedCount = s.executeUpdate();
			} catch (SQLException exc) {
				BTCLoader.error(String.format(
						"Unable to delete spent transaction outputs", exc));
				throw new BlockStoreException(
						"Unable to delete spent transaction outputs");
			}
			BTCLoader.info(String.format(
					"Deleted %d spent transaction outputs", deletedCount));
		}
		return deletedCount;
	}

	/**
	 * Returns the chain list from the block following the start block up to the
	 * stop block. A maximum of 500 blocks will be returned. The list will start
	 * with the genesis block if the start block is not found.
	 *
	 * @param startBlock
	 *            The start block
	 * @param stopBlock
	 *            The stop block
	 * @return Block inventory list
	 * @throws BlockStoreException
	 *             Unable to get blocks from database
	 */
	@Override
	public List<InventoryItem> getChainList(Sha256Hash startBlock,
			Sha256Hash stopBlock) throws BlockStoreException {
		//
		// Get the block height for the start block
		//
		int blockHeight = 0;
		Connection conn = getConnection();
		try (PreparedStatement s = conn
				.prepareStatement("SELECT block_height FROM Blocks "
						+ "WHERE block_hash_index=? AND block_hash=?")) {
			s.setLong(1, getHashIndex(startBlock));
			s.setBytes(2, startBlock.getBytes());
			ResultSet r = s.executeQuery();
			if (r.next())
				blockHeight = Math.max(r.getInt(1), 0);
		} catch (SQLException exc) {
			BTCLoader.error(String.format(
					"Unable to get start block\n  Block %s", startBlock), exc);
			throw new BlockStoreException("Unable to get start block",
					startBlock);
		}
		//
		// If we found the start block, we will start at the block following it.
		// Otherwise,
		// we will start with the block following the genesis block.
		//
		return getChainList(blockHeight, stopBlock);
	}

	/**
	 * Returns the chain list from the block following the start block up to the
	 * stop block. A maximum of 500 blocks will be returned.
	 *
	 * @param startHeight
	 *            Start block height
	 * @param stopBlock
	 *            Stop block
	 * @return Block inventory list
	 * @throws BlockStoreException
	 *             Unable to get blocks from database
	 */
	@Override
	public List<InventoryItem> getChainList(int startHeight,
			Sha256Hash stopBlock) throws BlockStoreException {
		List<InventoryItem> chainList = new LinkedList<>();
		//
		// Get the chain list starting at the block following the start block
		// and continuing
		// for a maximum of 500 blocks.
		//
		Connection conn = getConnection();
		try (PreparedStatement s = conn
				.prepareStatement("SELECT block_hash FROM Blocks "
						+ "WHERE block_height>? AND block_height<=? ORDER BY block_height ASC")) {
			s.setInt(1, startHeight);
			s.setInt(2, startHeight + 500);
			ResultSet r = s.executeQuery();
			while (r.next()) {
				Sha256Hash blockHash = new Sha256Hash(r.getBytes(1));
				chainList.add(new InventoryItem(InventoryItem.INV_BLOCK,
						blockHash));
				if (blockHash.equals(stopBlock))
					break;
			}
		} catch (SQLException exc) {
			BTCLoader.error("Unable to get the chain list", exc);
			throw new BlockStoreException("Unable to get the chain list");
		}
		return chainList;
	}

	/**
	 * Returns the header list from the block following the start block up to
	 * the stop block. A maximum of 2000 blocks will be returned. The list will
	 * start with the genesis block if the start block is not found.
	 *
	 * @param startBlock
	 *            The start block
	 * @param stopBlock
	 *            The stop block
	 * @return Block header list (empty list if one or more blocks not found)
	 * @throws BlockStoreException
	 *             Unable to get data from the database
	 */
	@Override
	public List<BlockHeader> getHeaderList(Sha256Hash startBlock,
			Sha256Hash stopBlock) throws BlockStoreException {
		List<BlockHeader> headerList = new LinkedList<>();
		//
		// Get the start block
		//
		int blockHeight = 0;
		try {
			Connection conn = getConnection();
			ResultSet r;
			try (PreparedStatement s = conn
					.prepareStatement("SELECT block_height FROM Blocks "
							+ "WHERE block_hash_index=? AND block_hash=?")) {
				s.setLong(1, getHashIndex(startBlock));
				s.setBytes(2, startBlock.getBytes());
				r = s.executeQuery();
				if (r.next())
					blockHeight = Math.max(r.getInt(1), 0);
			}
			//
			// If we found the start block, we will start at the block following
			// it. Otherwise,
			// we will start at the block following the genesis block.
			//
			try (PreparedStatement s = conn
					.prepareStatement("SELECT header,block_height FROM Blocks "
							+ "WHERE block_height>? AND block_height<=? ORDER BY block_height ASC")) {
				s.setInt(1, blockHeight);
				s.setInt(2, blockHeight + 2000);
				r = s.executeQuery();
				while (r.next())
					headerList.add(new BlockHeader(r.getBytes(1), false));
			}
		} catch (EOFException | SQLException | VerificationException exc) {
			BTCLoader.error("Unable to get header list", exc);
			throw new BlockStoreException("Unable to get header list");
		}
		return headerList;
	}

	/**
	 * Releases a held block for processing
	 *
	 * @param blockHash
	 *            Block hash
	 * @throws BlockStoreException
	 *             Unable to release the block
	 */
	@Override
	public void releaseBlock(Sha256Hash blockHash) throws BlockStoreException {
		Connection conn = getConnection();
		try (PreparedStatement s = conn
				.prepareStatement("UPDATE Blocks SET on_hold=false "
						+ "WHERE block_hash_index=? AND block_hash=?")) {
			s.setLong(1, getHashIndex(blockHash));
			s.setBytes(2, blockHash.getBytes());
			s.executeUpdate();
		} catch (SQLException exc) {
			BTCLoader
					.error(String.format(
							"Unable to release held block\n  Block %s",
							blockHash), exc);
			throw new BlockStoreException("Unable to release held block");
		}
	}

	/**
	 * Stores a block in the database
	 *
	 * @param storedBlock
	 *            Block to be stored
	 * @throws BlockStoreException
	 *             Unable to store the block
	 */
	@Override
	public void storeBlock(StoredBlock storedBlock) throws BlockStoreException {
		Block block = storedBlock.getBlock();
		synchronized (lock) {
			//
			// Add the block to the current block file
			//
			int[] fileLocation = storeBlock(block);
			Connection conn = getConnection();
			try (PreparedStatement s1 = conn
					.prepareStatement("INSERT INTO Blocks "
							+ "(block_hash_index,block_hash,prev_hash_index,prev_hash,"
							+ "block_height,timestamp,chain_work,on_hold,file_number,file_offset,header) "
							+ "VALUES(?,?,?,?,?,?,?,?,?,?,?)")) {
				//
				// Store the block in the Blocks table
				//
				s1.setLong(1, getHashIndex(block.getHash()));
				s1.setBytes(2, block.getHash().getBytes());
				s1.setLong(3, getHashIndex(block.getPrevBlockHash()));
				s1.setBytes(4, block.getPrevBlockHash().getBytes());
				s1.setInt(5, storedBlock.isOnChain() ? storedBlock.getHeight()
						: -1);
				s1.setLong(6, block.getTimeStamp());
				s1.setBytes(7, storedBlock.getChainWork().toByteArray());
				s1.setBoolean(8, storedBlock.isOnHold());
				s1.setInt(9, fileLocation[0]);
				s1.setInt(10, fileLocation[1]);
				s1.setBytes(11, block.getHeaderBytes());
				s1.executeUpdate();
			} catch (SQLException exc) {
				BTCLoader.error(String.format(
						"Unable to store block in database\n  Block %s",
						storedBlock.getHash()), exc);
				rollback();
				truncateBlockFile(fileLocation);
				throw new BlockStoreException(
						"Unable to store block in database");
			}
		}
	}

	/**
	 * Locates the junction where the chain represented by the specified block
	 * joins the current block chain. The returned list starts with the junction
	 * block and contains all blocks in the chain leading to the specified
	 * block. The StoredBlock object for the junction block will not contain a
	 * Block object while the StoredBlock objects for the blocks in the new
	 * chain will contain Block objects.
	 *
	 * A BlockNotFoundException will be thrown if the chain cannot be resolved
	 * because a block is missing. The caller should get the block from a peer,
	 * store it in the database and then retry.
	 *
	 * A ChainTooLongException will be thrown if the block chain is getting too
	 * big. The caller should restart the chain resolution closer to the
	 * junction block and then work backwards toward the original block.
	 *
	 * @param chainHash
	 *            The block hash of the chain head
	 * @return List of blocks in the chain leading to the new head
	 * @throws BlockNotFoundException
	 *             A block in the chain was not found
	 * @throws BlockStoreException
	 *             Unable to get blocks from the database
	 * @throws ChainTooLongException
	 *             The block chain is too long
	 */
	@Override
	public List<StoredBlock> getJunction(Sha256Hash chainHash)
			throws BlockNotFoundException, BlockStoreException,
			ChainTooLongException {
		List<StoredBlock> chainList = new LinkedList<>();
		boolean onChain = false;
		Sha256Hash blockHash = chainHash;
		Block block;
		StoredBlock chainStoredBlock;
		synchronized (lock) {
			//
			// If this block immediately follows the current chain head, we
			// don't need
			// to search the database. Just create a StoredBlock and add it to
			// the beginning
			// of the chain list.
			//
			if (chainHead.equals(blockHash)) {
				chainStoredBlock = new StoredBlock(chainHead, prevChainHead,
						chainWork, chainHeight);
				chainList.add(0, chainStoredBlock);
			} else {
				//
				// Starting with the supplied block, follow the previous hash
				// values until
				// we reach a block which is on the block chain. This block is
				// the junction
				// block. We will throw a ChainTooLongException if the chain
				// exceeds 144 blocks
				// (1 days worth). The caller should call this method again
				// starting with the
				// last block found to build a sub-segment of the chain.
				//
				PreparedStatement s1;
				try {
					Sha256Hash prevHash;
					boolean onHold;
					int fileNumber;
					int fileOffset;
					int blockHeight;
					BigInteger blockWork;
					Connection conn = getConnection();
					ResultSet r;
					s1 = conn
							.prepareStatement("SELECT prev_hash,on_hold,chain_work,block_height,file_number,file_offset "
									+ "FROM Blocks WHERE block_hash_index=? AND block_hash=?");
					while (!onChain) {
						s1.setLong(1, getHashIndex(blockHash));
						s1.setBytes(2, blockHash.getBytes());
						r = s1.executeQuery();
						if (r.next()) {
							prevHash = new Sha256Hash(r.getBytes(1));
							onHold = r.getBoolean(2);
							blockWork = new BigInteger(r.getBytes(3));
							blockHeight = r.getInt(4);
							fileNumber = r.getInt(5);
							fileOffset = r.getInt(6);
							onChain = (blockHeight >= 0);
							r.close();
							if (!onChain) {
								if (chainList.size() >= 144)
									throw new ChainTooLongException(
											"Chain length too long", blockHash);
								block = getBlock(fileNumber, fileOffset);
								if (block == null) {
									BTCLoader
											.error(String
													.format("Chain block file %d is not available\n  Block %s",
															fileNumber,
															blockHash));
									throw new BlockNotFoundException(
											"Unable to resolve block chain",
											blockHash);
								}
								chainStoredBlock = new StoredBlock(block,
										BigInteger.ZERO, -1, false, onHold);
								blockHash = block.getPrevBlockHash();
							} else {
								chainStoredBlock = new StoredBlock(blockHash,
										prevHash, blockWork, blockHeight);
							}
							chainList.add(0, chainStoredBlock);
						} else {
							BTCLoader.debug(String.format(
									"Chain block is not available\n  Block %s",
									blockHash));
							throw new BlockNotFoundException(
									"Unable to resolve block chain", blockHash);
						}
					}
				} catch (SQLException exc) {
					BTCLoader.error("Unable to locate junction block", exc);
					throw new BlockStoreException(
							"Unable to locate junction block", blockHash);
				}
			}
		}
		return chainList;
	}

	/**
	 * Changes the chain head and updates all blocks from the junction block up
	 * to the new chain head. The junction block is the point where the current
	 * chain and the new chain intersect. A VerificationException will be thrown
	 * if a block in the new chain is for a checkpoint block and the block hash
	 * doesn't match the checkpoint hash.
	 *
	 * @param chainList
	 *            List of all chain blocks starting with the junction block up
	 *            to and including the new chain head
	 * @throws BlockStoreException
	 *             Unable to update the database
	 * @throws VerificationException
	 *             Chain verification failed
	 */
	@Override
	public void setChainHead(List<StoredBlock> chainList)
			throws BlockStoreException, VerificationException {
		//
		// See if we have reached a checkpoint. If we have, the new block at
		// that height
		// must match the checkpoint block.
		//
		for (StoredBlock storedBlock : chainList) {
			if (storedBlock.getBlock() == null)
				continue;
			Sha256Hash checkHash = checkpoints.get(Integer.valueOf(storedBlock
					.getHeight()));
			if (checkHash != null) {
				if (checkHash.equals(storedBlock.getHash())) {
					BTCLoader.info(String.format(
							"New chain head at height %d matches checkpoint",
							storedBlock.getHeight()));
				} else {
					BTCLoader
							.error(String
									.format("New chain head at height %d does not match checkpoint",
											storedBlock.getHeight()));
					throw new VerificationException(
							"Checkpoint verification failed",
							RejectMessage.REJECT_CHECKPOINT,
							storedBlock.getHash());
				}
			}
		}
		//
		// Make the new block the chain head
		//
		StoredBlock storedBlock = chainList.get(chainList.size() - 1);
		synchronized (lock) {
			Sha256Hash blockHash = null;
			Block block;
			Sha256Hash txHash;
			PreparedStatement s1 = null;
			PreparedStatement s2 = null;
			PreparedStatement s3 = null;
			PreparedStatement s4 = null;
			PreparedStatement s5 = null;
			PreparedStatement s6 = null;
			try {
				Connection conn = getConnection();
				conn.setAutoCommit(false);
				ResultSet r;
				//
				// The ideal case is where the new block links to the current
				// chain head.
				// If this is not the case, we need to remove all blocks from
				// the block
				// chain following the junction block.
				//
				if (!chainHead.equals(storedBlock.getPrevBlockHash())) {
					s1 = conn
							.prepareStatement("SELECT file_number,file_offset FROM Blocks "
									+ "WHERE block_hash_index=? AND block_hash=?");
					s2 = conn
							.prepareStatement("DELETE FROM TxOutputs WHERE tx_hash_index=? AND tx_hash=?");
					s3 = conn
							.prepareStatement("UPDATE TxOutputs SET time_spent=0 "
									+ "WHERE tx_hash_index=? AND tx_hash=? AND tx_index=?");
					s4 = conn
							.prepareStatement("UPDATE Blocks SET block_height=-1 "
									+ "WHERE block_hash_index=? AND block_hash=?");
					Sha256Hash junctionHash = chainList.get(0).getHash();
					blockHash = chainHead;
					//
					// Process each block starting at the current chain head and
					// working backwards
					// until we reach the junction block
					//
					while (!blockHash.equals(junctionHash)) {
						//
						// Get the block from the Blocks database
						//
						s1.setLong(1, getHashIndex(blockHash));
						s1.setBytes(2, blockHash.getBytes());
						r = s1.executeQuery();
						if (!r.next()) {
							BTCLoader
									.error(String
											.format("Chain block not found in Blocks database\n  Block %s",
													blockHash));
							throw new BlockStoreException(
									"Chain block not found in Blocks database");
						}
						int fileNumber = r.getInt(1);
						int fileOffset = r.getInt(2);
						block = getBlock(fileNumber, fileOffset);
						if (block == null) {
							BTCLoader
									.error(String
											.format("Chain block file %d is not available\n  Block %s",
													fileNumber, blockHash));
							throw new BlockStoreException(
									"Chain block is not available");
						}
						//
						// Process each transaction in the block
						//
						List<Transaction> txList = block.getTransactions();
						for (Transaction tx : txList) {
							txHash = tx.getHash();
							//
							// Delete the transaction from the TxOutputs table
							//
							s2.setLong(1, getHashIndex(txHash));
							s2.setBytes(2, txHash.getBytes());
							s2.executeUpdate();
							//
							// Update spent outputs to indicate they have not
							// been spent. We
							// need to ignore inputs for coinbase transactions
							// since they are
							// not used for spending coins. It is also possible
							// that a transaction
							// in the block spends an output from another
							// transaction in the block,
							// in which case the output will not be found since
							// we have already
							// deleted all of the block transactions.
							//
							if (tx.isCoinBase()) {
								continue;
							}
							List<TransactionInput> txInputs = tx.getInputs();
							for (TransactionInput txInput : txInputs) {
								OutPoint op = txInput.getOutPoint();
								Sha256Hash outHash = op.getHash();
								int outIndex = op.getIndex();
								s3.setLong(1, getHashIndex(outHash));
								s3.setBytes(2, outHash.getBytes());
								s3.setShort(3, (short) outIndex);
								s3.executeUpdate();
							}
						}
						//
						// Update the block status in the Blocks table
						//
						s4.setLong(1, getHashIndex(blockHash));
						s4.setBytes(2, blockHash.getBytes());
						s4.executeUpdate();
						BTCLoader.networkChainHeight--;
						BTCLoader.info(String.format(
								"Block removed from block chain\n  Block %s",
								blockHash));
						//
						// Advance to the block before this block
						//
						blockHash = block.getPrevBlockHash();
					}
				}
				//
				// Now add the new blocks to the block chain starting with the
				// block following the junction block
				//
				s1 = conn.prepareStatement("SELECT tx_index FROM TxOutputs "
						+ "WHERE tx_hash_index=? AND tx_hash=? LIMIT 1");
				s2 = conn
						.prepareStatement("INSERT INTO TxOutputs (tx_hash_index,tx_hash,tx_index,block_hash,"
								+ "block_height,time_spent,value,script_bytes,is_coinbase) VALUES(?,?,?,?,0,0,?,?,?)");
				s3 = conn
						.prepareStatement("UPDATE TxOutputs SET time_spent=?,block_height=? WHERE db_id=?");
				s4 = conn
						.prepareStatement("UPDATE Blocks SET block_height=?,chain_work=? "
								+ "WHERE block_hash_index=? AND block_hash=?");
				s5 = conn
						.prepareStatement("INSERT INTO TxSpentOutputs (time_spent,db_id) VALUES(?,?)");
				s6 = conn.prepareStatement("SELECT db_id FROM TxOutputs "
						+ "WHERE tx_hash_index=? AND tx_hash=? AND tx_index=?");
				for (int i = 1; i < chainList.size(); i++) {
					storedBlock = chainList.get(i);
					block = storedBlock.getBlock();
					blockHash = block.getHash();
					int blockHeight = storedBlock.getHeight();
					BigInteger blockWork = storedBlock.getChainWork();
					List<Transaction> txList = block.getTransactions();
					//
					// Add the block transactions to the TxOutputs table and
					// update the
					// spent status for transaction outputs referenced by the
					// transactions
					// in this block.
					//
					// Unfortunately, before BIP 30 was implemented, there were
					// several
					// cases where a block contained the same coinbase
					// transaction. So
					// we need to check the TxOutputs table first to make sure
					// the transaction
					// output is not already in the table for a coinbase
					// transaction. We will
					// allow a duplicate coinbase transaction if it is in a
					// block before 250,000.
					//
					// Some transactions contain a text message as one of the
					// outputs with the
					// associated script set to OP_RETURN (which means the
					// output can never be spent).
					// We will check for this case and set the transaction
					// output as spent.
					//
					for (Transaction tx : txList) {
						txHash = tx.getHash();
						boolean processOutputs = true;
						s1.setLong(1, getHashIndex(txHash));
						s1.setBytes(2, txHash.getBytes());
						r = s1.executeQuery();
						if (r.next()) {
							r.close();
							if (!tx.isCoinBase()
									|| storedBlock.getHeight() >= 250000) {
								BTCLoader.error(String.format(
										"Height %d: Transaction outputs already in TxOutputs\n"
												+ "  Block %s\n  Tx %s",
										storedBlock.getHeight(),
										block.getHashAsString(), txHash));
								throw new VerificationException(
										"Transaction outputs already in TxOutputs",
										RejectMessage.REJECT_DUPLICATE, txHash);
							}
							processOutputs = false;
						} else {
							r.close();
						}
						if (processOutputs) {
							List<TransactionOutput> txOutputs = tx.getOutputs();
							for (TransactionOutput txOutput : txOutputs) {
								if (txOutput.isSpendable()) {
									s2.setLong(1, getHashIndex(txHash));
									s2.setBytes(2, txHash.getBytes());
									s2.setShort(3, (short) txOutput.getIndex());
									s2.setBytes(4, blockHash.getBytes());
									s2.setLong(5, txOutput.getValue()
											.longValue());
									s2.setBytes(6, txOutput.getScriptBytes());
									s2.setBoolean(7, tx.isCoinBase());
									s2.executeUpdate();
								}
							}
						}
						//
						// Connect transaction inputs to transaction outputs and
						// mark them spent.
						//
						// We need to ignore inputs for coinbase transactions
						// since they are not
						// used for spending coins.
						//
						if (tx.isCoinBase())
							continue;
						List<TransactionInput> txInputs = tx.getInputs();
						for (TransactionInput txInput : txInputs) {
							OutPoint op = txInput.getOutPoint();
							Sha256Hash outHash = op.getHash();
							int outIndex = op.getIndex();
							s6.setLong(1, getHashIndex(outHash));
							s6.setBytes(2, outHash.getBytes());
							s6.setShort(3, (short) outIndex);
							r = s6.executeQuery();
							if (!r.next()) {
								BTCLoader
										.error(String
												.format("Transaction output not found\n  Tx %s",
														tx.getHashAsString()));
								throw new BlockStoreException(
										"Transaction output not found");
							}
							long dbId = r.getLong(1);
							s3.setLong(1, block.getTimeStamp());
							s3.setInt(2, blockHeight);
							s3.setLong(3, dbId);
							s3.executeUpdate();
							s5.setLong(1, block.getTimeStamp());
							s5.setLong(2, dbId);
							s5.executeUpdate();
						}
					}
					//
					// Update the block status in the Blocks database
					//
					s4.setInt(1, blockHeight);
					s4.setBytes(2, blockWork.toByteArray());
					s4.setLong(3, getHashIndex(blockHash));
					s4.setBytes(4, blockHash.getBytes());
					s4.executeUpdate();
					BTCLoader
							.info(String
									.format("Block added to block chain at height %d\n  Block %s",
											blockHeight,
											block.getHashAsString()));
				}
				//
				// Commit the changes
				//
				conn.commit();
				conn.setAutoCommit(true);
				//
				// Update chain values for the new chain
				//
				storedBlock = chainList.get(chainList.size() - 1);
				chainTime = storedBlock.getBlock().getTimeStamp();
				chainHead = storedBlock.getHash();
				prevChainHead = storedBlock.getPrevBlockHash();
				chainHeight = storedBlock.getHeight();
				chainWork = storedBlock.getChainWork();
				targetDifficulty = storedBlock.getBlock().getTargetDifficulty();
			} catch (SQLException exc) {
				BTCLoader.error("Unable to update block chain", exc);
				rollback(s1, s2, s3, s4);
				throw new BlockStoreException("Unable to update block chain",
						blockHash);
			}
		}
	}

	/**
	 * Checks if a table exists
	 *
	 * @param table
	 *            Table name
	 * @return TRUE if the table exists
	 * @throws BlockStoreException
	 *             Unable to access the database server
	 */
	private boolean tableExists(String table) throws BlockStoreException {
		boolean tableExists;
		Connection conn = getConnection();
		try (Statement s = conn.createStatement()) {
			s.executeQuery("SELECT 1 FROM " + table + " WHERE 1 = 2");
			tableExists = true;
		} catch (SQLException exc) {
			tableExists = false;
		}
		return tableExists;
	}

	/**
	 * Create the database tables
	 *
	 * @throws BlockStoreException
	 *             Unable to create database tables
	 */
	private void createTables() throws BlockStoreException {
		Connection conn = getConnection();
		try (Statement s = conn.createStatement()) {
			//
			// Create the tables
			//
			s.executeUpdate(Settings_Table);
			s.executeUpdate(TxOutputs_Table);
			s.executeUpdate(TxOutputs_IX1);
			s.executeUpdate(TxSpentOutputs_Table);
			s.executeUpdate(TxSpentOutputs_IX1);
			s.executeUpdate(Blocks_Table);
			s.executeUpdate(Blocks_IX1);
			s.executeUpdate(Blocks_IX2);
			s.executeUpdate(Blocks_IX3);
			s.executeUpdate(Alerts_Table);
			s.executeUpdate(Received_Table);
			s.executeUpdate(Received_IX1);
			s.executeUpdate(Sent_Table);
			s.executeUpdate(Sent_IX1);
			s.executeUpdate(Addresses_Table);
			s.executeUpdate(Keys_Table);
			BTCLoader.info("SQL database tables created");
			//
			// We are creating a new database, so delete any existing block
			// files
			//
			File dirFile = new File(String.format("%s%sBlocks", dataPath,
					LSystem.FS));
			if (dirFile.exists()) {
				File[] fileList = dirFile.listFiles();
				for (File file : fileList){
					file.delete();
				}
			}
		} catch (SQLException exc) {
			BTCLoader.error("Unable to create SQL database tables", exc);
			throw new BlockStoreException(
					"Unable to create SQL database tables");
		}
	}

	private boolean existsBlock = false;

	/**
	 * Initialize the tables
	 *
	 * @throws BlockStoreException
	 *             Unable to initialize the database tables
	 */
	private void initTables() throws BlockStoreException {
		Connection conn = getConnection();
		try {
			conn.setAutoCommit(false);
			//
			// Initialize the block chain with the genesis block
			//
			Block genesisBlock = new Block(BTCLoader.GENESIS_BLOCK_BYTES, 0,
					BTCLoader.GENESIS_BLOCK_BYTES.length, false);
			chainHead = genesisBlock.getHash();
			prevChainHead = Sha256Hash.ZERO_HASH;
			chainHeight = 0;
			chainWork = BigInteger.ONE;
			targetDifficulty = NetParams.MAX_TARGET_DIFFICULTY;
			blockFileNumber = 0;
			chainTime = genesisBlock.getTimeStamp();
			//
			// Initialize the Settings table
			//
			try (PreparedStatement s = conn
					.prepareStatement("INSERT INTO Settings (schema_name,schema_version) VALUES(?,?)")) {
				s.setString(1, schemaName);
				s.setInt(2, schemaVersion);
				s.executeUpdate();
			}
			//
			// Add the genesis block to the Blocks table
			//
			try (PreparedStatement s = conn
					.prepareStatement("INSERT INTO Blocks(block_hash_index,block_hash,prev_hash_index,prev_hash,"
							+ "block_height,timestamp,chain_work,on_hold,"
							+ "file_number,file_offset,header) VALUES(?,?,?,?,0,?,?,false,0,0,?)")) {
				s.setLong(1, getHashIndex(chainHead));
				s.setBytes(2, chainHead.getBytes());
				s.setLong(3, getHashIndex(prevChainHead));
				s.setBytes(4, prevChainHead.getBytes());
				s.setLong(5, chainTime);
				s.setBytes(6, chainWork.toByteArray());
				s.setBytes(7, genesisBlock.getHeaderBytes());
				s.executeUpdate();
			}
			//
			// Copy the genesis block as the initial block file
			//
			File blockFile = new File(String.format("%s%sBlocks%sblk00000.dat",
					dataPath, LSystem.FS, LSystem.FS));
			if (!existsBlock) {
				existsBlock = blockFile.exists();
				if (!existsBlock) {
					FileUtils.makedirs(blockFile);
				}
			}
			try (FileOutputStream outFile = new FileOutputStream(blockFile)) {
				byte[] prefixBytes = new byte[8];
				Helper.uint32ToByteArrayLE(NetParams.MAGIC_NUMBER, prefixBytes,
						0);
				Helper.uint32ToByteArrayLE(
						BTCLoader.GENESIS_BLOCK_BYTES.length, prefixBytes, 4);
				outFile.write(prefixBytes);
				outFile.write(BTCLoader.GENESIS_BLOCK_BYTES);
			}
			//
			// All done - commit the updates
			//
			conn.commit();
			conn.setAutoCommit(true);
			BTCLoader.info(String.format(
					"Database initialized with schema version %d.%d",
					schemaVersion / 100, schemaVersion % 100));
		} catch (IOException | SQLException | VerificationException exc) {
			BTCLoader.error("Unable to initialize the database tables", exc);
			rollback();
			throw new BlockStoreException(
					"Unable to initialize the database tables");
		}
	}

	/**
	 * Get the initial database settings
	 *
	 * @throws BlockStoreException
	 *             Unable to get the initial values
	 */
	private void getSettings() throws BlockStoreException {
		Connection conn = getConnection();
		ResultSet r;
		int version = 0;
		try {
			//
			// Get the initial values from the Settings table
			//
			try (PreparedStatement s = conn
					.prepareStatement("SELECT schema_version FROM Settings "
							+ "WHERE schema_name=?")) {
				s.setString(1, schemaName);
				r = s.executeQuery();
				if (!r.next())
					throw new BlockStoreException("Incorrect database schema");
				version = r.getInt(1);
				if (version > schemaVersion)
					throw new BlockStoreException(String.format(
							"Schema version %d.%d is not supported",
							version / 100, version % 100));
				switch (version) {
				case 100:
					dbUpgrade100(conn); // Upgrade from Version 1.00 to 1.01
				case 101:
					dbUpgrade101(conn); // Upgrade from Version 1.01 to 1.02
					break;
				}
				version = schemaVersion;
			}
			//
			// Get the current chain values from the chain head block
			//
			try (Statement s = conn.createStatement()) {
				r = s.executeQuery("SELECT block_hash,prev_hash,block_height,chain_work,timestamp,"
						+ "file_number,file_offset "
						+ "FROM Blocks WHERE block_height=(SELECT MAX(block_height) FROM Blocks)");
				if (!r.next())
					throw new BlockStoreException(
							"Unable to get chain head block");
				chainHead = new Sha256Hash(r.getBytes(1));
				prevChainHead = new Sha256Hash(r.getBytes(2));
				chainHeight = r.getInt(3);
				chainWork = new BigInteger(r.getBytes(4));
				chainTime = r.getLong(5);
				int fileNumber = r.getInt(6);
				int fileOffset = r.getInt(7);
				Block block = getBlock(fileNumber, fileOffset);
				if (block == null) {
					BTCLoader
							.error(String
									.format("Unable to get block from block file %d, offset %d\n  %s",
											fileNumber, fileOffset, chainHead));
					throw new BlockStoreException(
							"Unable to get block from block file", chainHead);
				}
				targetDifficulty = block.getTargetDifficulty();
			}
			//
			// Get the cuurrent block file number
			//
			File blockDir = new File(String.format("%s%sBlocks", dataPath,
					LSystem.FS));
			String[] fileList = blockDir.list();
			for (String fileName : fileList) {
				int sep = fileName.lastIndexOf('.');
				if (sep >= 0 && fileName.substring(0, 3).equals("blk")
						&& fileName.substring(sep).equals(".dat"))
					blockFileNumber = Math.max(blockFileNumber,
							Integer.parseInt(fileName.substring(3, sep)));
			}
			BigInteger networkDifficulty = NetParams.PROOF_OF_WORK_LIMIT
					.divide(Helper.decodeCompactBits(targetDifficulty));
			BTCLoader
					.info(String
							.format("Database opened with schema version %d.%d\n"
									+ "  Chain height %,d, Target difficulty %s, Block File number %d\n"
									+ "  Chain head %s",
									version / 100,
									version % 100,
									chainHeight,
									Helper.numberToShortString(networkDifficulty),
									blockFileNumber, chainHead));
		} catch (SQLException exc) {
			BTCLoader.error("Unable to get initial table settings", exc);
			throw new BlockStoreException(
					"Unable to get initial table settings");
		}
	}

	/**
	 * Upgrade the database from Version 1.00 to 1.01
	 *
	 * Due to the size of the Blocks table, we will commit each update. If an
	 * error occurs, the upgrade can be repeated without restoring the table to
	 * its original state.
	 *
	 * @param conn
	 *            Database connection
	 * @throws BlockStoreException
	 *             Unable to upgrade the database
	 */
	private void dbUpgrade100(Connection conn) throws BlockStoreException {
		try {
			BTCLoader
					.info("Upgrading the SQL database from Version 1.00 to Version 1.01");
			ResultSet r;
			//
			// Add the 'header' column to the Blocks table
			//
			Statement s1 = conn.createStatement();
			s1.executeUpdate("ALTER TABLE Blocks ADD COLUMN IF NOT EXIST header BINARY");
			//
			// Build the block list
			//
			List<Sha256Hash> blockList = new LinkedList<>();
			r = s1.executeQuery("SELECT block_hash FROM Blocks WHERE header IS NULL");
			while (r.next())
				blockList.add(new Sha256Hash(r.getBytes(1)));
			r.close();
			//
			// Add the block headers to the database
			//
			PreparedStatement s2 = conn
					.prepareStatement("SELECT file_number,file_offset FROM Blocks WHERE block_hash=?");
			PreparedStatement s3 = conn
					.prepareStatement("UPDATE Blocks SET header=? WHERE block_hash=?");
			for (Sha256Hash blockHash : blockList) {
				s2.setBytes(1, blockHash.getBytes());
				r = s2.executeQuery();
				if (!r.next()) {
					BTCLoader
							.warn(String
									.format("Block file pointer not found in database\n  Block %s",
											blockHash));
					throw new BlockStoreException(
							"Block file pointer not found in database");
				}
				int fileNumber = r.getInt(1);
				int fileOffset = r.getInt(2);
				r.close();
				Block block = getBlock(fileNumber, fileOffset);
				if (block == null) {
					BTCLoader.error(String.format(
							"Block in file %d at position %d is unavailable",
							fileNumber, fileOffset));
					throw new BlockStoreException(
							"Unable to upgrade database due to unavailable block");
				}
				s3.setBytes(1, block.getHeaderBytes());
				s3.setBytes(2, blockHash.getBytes());
				s3.executeUpdate();
			}
			//
			// Set the header column to NOT NULL and update the schema version
			//
			s1.executeUpdate("ALTER TABLE Blocks ALTER COLUMN header BINARY NOT NULL");
			s1.executeUpdate("UPDATE Settings SET schema_version=101");
			//
			// Upgrade completed
			//
			BTCLoader.info("Database upgrade to Version 1.01 completed");
		} catch (SQLException exc) {
			BTCLoader.error("Unable to upgrade database version", exc);
			throw new BlockStoreException("Unable to upgrade database version");
		}
	}

	/**
	 * Upgrade the database from Version 1.01 to 1.02
	 *
	 * Due to the size of the Blocks and TxOutputs tables, we will commit each
	 * update. If an error occurs, the upgrade can be repeated without restoring
	 * the tables to their original state.
	 *
	 * @param conn
	 *            Database connection
	 * @throws BlockStoreException
	 *             Unable to upgrade the database
	 */
	private void dbUpgrade101(Connection conn) throws BlockStoreException {
		ResultSet r;
		try {
			BTCLoader
					.info("Upgrading the SQL database from Version 1.01 to Version 1.02");
			Statement s1 = conn.createStatement();
			PreparedStatement s2;
			PreparedStatement s3;
			//
			// Delete the existing hash indexes
			//
			BTCLoader.info("Deleting existing hash indexes");
			s1.executeUpdate("DROP INDEX IF EXISTS Blocks_IX1");
			s1.executeUpdate("DROP INDEX IF EXISTS Blocks_IX2");
			s1.executeUpdate("DROP INDEX IF EXISTS TxOutputs_IX1");
			//
			// Add the new index columns
			//
			BTCLoader.info("Adding new hash index columns");
			s1.executeUpdate("ALTER TABLE Blocks ADD COLUMN IF NOT EXISTS block_hash_index BIGINT");
			s1.executeUpdate("ALTER TABLE Blocks ADD COLUMN IF NOT EXISTS prev_hash_index BIGINT");
			s1.executeUpdate("ALTER TABLE TxOutputs ADD COLUMN IF NOT EXISTS tx_hash_index BIGINT");
			//
			// Fill in the index columns for the Blocks table
			//
			BTCLoader.info("Upgrading Blocks table");
			List<Long> idList = new LinkedList<>();
			s2 = conn
					.prepareStatement("SELECT block_hash,prev_hash FROM Blocks WHERE db_id=?");
			s3 = conn
					.prepareStatement("UPDATE Blocks SET block_hash_index=?,prev_hash_index=? WHERE db_id=?");
			r = s1.executeQuery("SELECT db_id FROM Blocks WHERE block_hash_index IS NULL");
			while (r.next())
				idList.add(r.getLong(1));
			r.close();
			for (Long dbId : idList) {
				s2.setLong(1, dbId);
				r = s2.executeQuery();
				if (r.next()) {
					Sha256Hash blockHash = new Sha256Hash(r.getBytes(1));
					Sha256Hash prevHash = new Sha256Hash(r.getBytes(2));
					r.close();
					s3.setLong(1, getHashIndex(blockHash));
					s3.setLong(2, getHashIndex(prevHash));
					s3.setLong(3, dbId);
					s3.executeUpdate();
				} else {
					r.close();
				}
			}
			s2.close();
			s3.close();
			idList.clear();
			System.gc();
			//
			// Fill in the index column for the TxOutputs table
			//
			BTCLoader.info("Upgrading TxOutputs table");
			s2 = conn
					.prepareStatement("SELECT tx_hash FROM TxOutputs WHERE db_id=?");
			s3 = conn
					.prepareStatement("UPDATE TxOutputs SET tx_hash_index=? WHERE db_id=?");
			r = s1.executeQuery("SELECT db_id FROM TxOutputs WHERE tx_hash_index IS NULL");
			while (r.next())
				idList.add(r.getLong(1));
			r.close();
			for (Long dbId : idList) {
				s2.setLong(1, dbId);
				r = s2.executeQuery();
				if (r.next()) {
					Sha256Hash txHash = new Sha256Hash(r.getBytes(1));
					r.close();
					s3.setLong(1, getHashIndex(txHash));
					s3.setLong(2, dbId);
					s3.executeUpdate();
				} else {
					r.close();
				}
			}
			s2.close();
			s3.close();
			idList.clear();
			System.gc();
			//
			// Set the index columns to NOT NULL and update the schema version
			//
			BTCLoader.info("Updating the table version");
			s1.executeUpdate("ALTER TABLE Blocks ALTER COLUMN block_hash_index BIGINT NOT NULL");
			s1.executeUpdate("ALTER TABLE Blocks ALTER COLUMN prev_hash_index BIGINT NOT NULL");
			s1.executeUpdate("ALTER TABLE TxOutputs ALTER COLUMN tx_hash_index BIGINT NOT NULL");
			s1.executeUpdate("UPDATE Settings SET schema_version=102");
			//
			// Create the new hash indexes
			//
			BTCLoader.info("Creating the new hash indexes");
			s1.executeUpdate("CREATE INDEX IF NOT EXISTS Blocks_IX1 ON Blocks(block_hash_index)");
			s1.executeUpdate("CREATE INDEX IF NOT EXISTS Blocks_IX2 ON Blocks(prev_hash_index)");
			s1.executeUpdate("CREATE INDEX IF NOT EXISTS TxOutputs_IX1 ON TxOutputs(tx_hash_index)");
			//
			// Upgrade completed
			//
			BTCLoader.info("Database upgrade to Version 1.02 completed");
		} catch (SQLException exc) {
			BTCLoader.error("Unable to upgrade database version", exc);
			throw new BlockStoreException("Unable to upgrade database version");
		}
	}

	public void setAddressLabel(Address address) throws BlockStoreException {
		Connection conn = getConnection();
		try (PreparedStatement s = conn
				.prepareStatement("UPDATE Addresses SET label=? WHERE address=?")) {
			if (address.getLabel().isEmpty()) {
				s.setNull(1, Types.VARCHAR);
			} else {
				s.setString(1, address.getLabel());
			}
			s.setBytes(2, address.getHash());
			s.executeUpdate();
		} catch (SQLException exc) {
			BTCLoader.error("Unable to update address label", exc);
			throw new BlockStoreException("Unable to update address label");
		}
	}

	public void storeAddress(Address address) throws BlockStoreException {
		Connection conn = getConnection();
		try (PreparedStatement s = conn
				.prepareStatement("INSERT INTO Addresses (address,label) VALUES(?,?)")) {
			s.setBytes(1, address.getHash());
			if (address.getLabel().isEmpty())
				s.setNull(2, Types.VARCHAR);
			else
				s.setString(2, address.getLabel());
			s.executeUpdate();
		} catch (SQLException exc) {
			BTCLoader.error("Unable to store address", exc);
			throw new BlockStoreException("Unable to store address");
		}
	}

	public void deleteAddress(Address address) throws BlockStoreException {
		Connection conn = getConnection();
		try (PreparedStatement s = conn
				.prepareStatement("DELETE FROM Addresses WHERE address=?")) {
			s.setBytes(1, address.getHash());
			s.executeUpdate();
		} catch (SQLException exc) {
			BTCLoader.error("Unable to delete address", exc);
			throw new BlockStoreException("Unable to delete address");
		}
	}

	public List<Address> getAddressList() throws BlockStoreException {
		List<Address> addressList = new ArrayList<>();
		Connection conn = getConnection();
		ResultSet r;
		try (Statement s = conn.createStatement()) {
			r = s.executeQuery("SELECT address,label FROM Addresses ORDER BY label ASC NULLS FIRST");
			while (r.next()) {
				String label = r.getString(2);
				addressList.add(new Address(r.getBytes(1),
						label != null ? label : ""));
			}
		} catch (SQLException exc) {
			BTCLoader.error("Unable to get address list", exc);
			throw new BlockStoreException("Unable to get address list");
		}
		return addressList;
	}

	public void storeKey(ECKey key) throws BlockStoreException {
		Connection conn = getConnection();
		try (PreparedStatement s = conn
				.prepareStatement("INSERT INTO Keys "
						+ "(public_key,private_key,timestamp,is_change,label) VALUES(?,?,?,?,?)")) {
			EncryptedPrivateKey encPrivKey = new EncryptedPrivateKey(
					key.getPrivKey(), LSystem.getAppPassword());
			s.setBytes(1, key.getPubKey());
			s.setBytes(2, encPrivKey.getBytes());
			s.setLong(3, key.getCreationTime());
			s.setBoolean(4, key.isChange());
			if (key.getLabel().isEmpty()) {
				s.setNull(5, Types.VARCHAR);
			} else {
				s.setString(5, key.getLabel());
			}
			s.executeUpdate();
		} catch (ECException | SQLException exc) {
			BTCLoader.error("Unable to store key", exc);
			throw new BlockStoreException("Unable to store key");
		}
	}

	public void setKeyLabel(ECKey key) throws BlockStoreException {
		Connection conn = getConnection();
		try (PreparedStatement s = conn
				.prepareStatement("UPDATE Keys SET label=? WHERE public_key=?")) {
			if (key.getLabel().isEmpty()) {
				s.setNull(1, Types.VARCHAR);
			} else {
				s.setString(1, key.getLabel());
			}
			s.setBytes(2, key.getPubKey());
			s.executeUpdate();
		} catch (SQLException exc) {
			BTCLoader.error("Unable to update key label", exc);
			throw new BlockStoreException("Unable to update key label");
		}
	}

	public List<ECKey> getKeyList() throws KeyException, BlockStoreException {
		List<ECKey> keyList = new ArrayList<>();
		Connection conn = getConnection();
		ResultSet r;
		try (Statement s = conn.createStatement()) {
			r = s.executeQuery("SELECT public_key,private_key,timestamp,is_change,label FROM Keys "
					+ "ORDER BY label ASC NULLS FIRST");
			while (r.next()) {
				byte[] pubKey = r.getBytes(1);
				EncryptedPrivateKey encPrivKey = new EncryptedPrivateKey(
						r.getBytes(2));
				ECKey key = new ECKey(encPrivKey.getPrivKey(LSystem
						.getAppPassword()), (pubKey.length == 33));
				if (!Arrays.equals(key.getPubKey(), pubKey)) {
					throw new KeyException(
							"Private key does not match public key");
				}
				key.setCreationTime(r.getLong(3));
				key.setChange(r.getBoolean(4));
				String label = r.getString(5);
				key.setLabel(label != null ? label : "");
				keyList.add(key);
			}
		} catch (EOFException | ECException | SQLException exc) {
			BTCLoader.error("Unable to get key list", exc);
			throw new BlockStoreException("Unable to get key list");
		}
		return keyList;
	}

	public void storeReceiveTx(ReceiveTransaction receiveTx)
			throws BlockStoreException {
		Connection conn = getConnection();
		try (PreparedStatement s = conn
				.prepareStatement("INSERT INTO Received "
						+ "(tx_hash_index,tx_hash,tx_index,norm_hash,timestamp,block_hash,address,"
						+ "value,script_bytes,is_spent,is_change,in_safe,is_coinbase,is_deleted) "
						+ "VALUES(?,?,?,?,?,?,?,?,?,false,?,false,?,false)")) {
			s.setLong(1, getHashIndex(receiveTx.getTxHash()));
			s.setBytes(2, receiveTx.getTxHash().getBytes());
			s.setShort(3, (short) receiveTx.getTxIndex());
			s.setBytes(4, receiveTx.getNormalizedID().getBytes());
			s.setLong(5, receiveTx.getTxTime());
			if (receiveTx.getBlockHash() == null)
				s.setNull(6, Types.BINARY);
			else
				s.setBytes(6, receiveTx.getBlockHash().getBytes());
			s.setBytes(7, receiveTx.getAddress().getHash());
			s.setLong(8, receiveTx.getValue().longValue());
			s.setBytes(9, receiveTx.getScriptBytes());
			s.setBoolean(10, receiveTx.isChange());
			s.setBoolean(11, receiveTx.isCoinBase());
			s.executeUpdate();
		} catch (SQLException exc) {
			BTCLoader.error(String.format(
					"Unable to store receive transaction output\n  Tx %s[%d]",
					receiveTx.getTxHash(), receiveTx.getTxIndex()), exc);
			throw new BlockStoreException(
					"Unable to store receive transaction output");
		}
	}

	public void setTxSpent(Sha256Hash txHash, int txIndex, boolean isSpent)
			throws BlockStoreException {
		Connection conn = getConnection();
		try (PreparedStatement s = conn
				.prepareStatement("UPDATE Received SET is_spent=? "
						+ "WHERE tx_hash_index=? AND tx_hash=? and tx_index=?")) {
			s.setBoolean(1, isSpent);
			s.setLong(2, getHashIndex(txHash));
			s.setBytes(3, txHash.getBytes());
			s.setShort(4, (short) txIndex);
			s.executeUpdate();
		} catch (SQLException exc) {
			BTCLoader.error(String.format(
					"Unable to update receive transaction output\n  Tx %s[%d]",
					txHash, txIndex), exc);
			throw new BlockStoreException(
					"Unable to update receive transaction outputs");
		}
	}

	public void setTxSafe(Sha256Hash txHash, int txIndex, boolean inSafe)
			throws BlockStoreException {
		Connection conn = getConnection();
		try (PreparedStatement s = conn
				.prepareStatement("UPDATE Received SET in_safe=? "
						+ "WHERE tx_hash_index=? AND tx_hash=? and tx_index=?")) {
			s.setBoolean(1, inSafe);
			s.setLong(2, getHashIndex(txHash));
			s.setBytes(3, txHash.getBytes());
			s.setShort(4, (short) txIndex);
			s.executeUpdate();
		} catch (SQLException exc) {
			BTCLoader.error(String.format(
					"Unable to update receive transaction output\n  Tx %s[%d]",
					txHash, txIndex), exc);
			throw new BlockStoreException(
					"Unable to update receive transaction outputs");
		}
	}

	public void setReceiveTxDelete(Sha256Hash txHash, int txIndex,
			boolean isDeleted) throws BlockStoreException {
		Connection conn = getConnection();
		try (PreparedStatement s = conn
				.prepareStatement("UPDATE Received SET is_deleted=? "
						+ "WHERE tx_hash_index=? AND tx_hash=? and tx_index=?")) {
			s.setBoolean(1, isDeleted);
			s.setLong(2, getHashIndex(txHash));
			s.setBytes(3, txHash.getBytes());
			s.setShort(4, (short) txIndex);
			s.executeUpdate();
		} catch (SQLException exc) {
			BTCLoader.error(String.format(
					"Unable to update receive transaction output\n  Tx %s[%d]",
					txHash, txIndex), exc);
			throw new BlockStoreException(
					"Unable to update receive transaction outputs");
		}
	}

	public List<ReceiveTransaction> getReceiveTxList()
			throws BlockStoreException {
		List<ReceiveTransaction> txList = new LinkedList<>();
		Map<TransactionID, ReceiveTransaction> txMap = new HashMap<TransactionID, ReceiveTransaction>();
		Connection conn = getConnection();
		ResultSet r;
		try (PreparedStatement s = conn
				.prepareStatement("SELECT tx_hash,tx_index,norm_hash,timestamp,"
						+ "block_hash,address,value,script_bytes,is_spent,is_change,in_safe,is_coinbase "
						+ "FROM Received WHERE is_deleted=false")) {
			r = s.executeQuery();
			while (r.next()) {
				Sha256Hash txHash = new Sha256Hash(r.getBytes(1));
				int txIndex = r.getShort(2);
				Sha256Hash normID = new Sha256Hash(r.getBytes(3));
				long txTime = r.getLong(4);
				byte[] bytes = r.getBytes(5);
				Sha256Hash blockHash = (bytes != null ? new Sha256Hash(bytes)
						: null);
				Address address = new Address(r.getBytes(6));
				BigInteger value = BigInteger.valueOf(r.getLong(7));
				byte[] scriptBytes = r.getBytes(8);
				boolean isSpent = r.getBoolean(9);
				boolean isChange = r.getBoolean(10);
				boolean inSafe = r.getBoolean(11);
				boolean isCoinbase = r.getBoolean(12);
				TransactionID txID = new TransactionID(txHash, txIndex);
				ReceiveTransaction tx = new ReceiveTransaction(normID, txHash,
						txIndex, txTime, blockHash, address, value,
						scriptBytes, isSpent, isChange, isCoinbase, inSafe);
				ReceiveTransaction prevTx = txMap.get(txID);
				if (blockHash != null) {
					if (prevTx != null) {
						txList.remove(prevTx);
					}
					txList.add(tx);
					txMap.put(txID, tx);
				} else if (prevTx == null) {
					txList.add(tx);
					txMap.put(txID, tx);
				}
			}
		} catch (SQLException exc) {
			BTCLoader.error("Unable to get receive transaction list", exc);
			throw new BlockStoreException(
					"Unable to get receive transaction list");
		}
		return txList;
	}

	public void storeSendTx(SendTransaction sendTx) throws BlockStoreException {
		Connection conn = getConnection();
		try (PreparedStatement s = conn
				.prepareStatement("INSERT INTO Sent "
						+ "(tx_hash_index,tx_hash,norm_hash,timestamp,block_hash,address,value,fee,"
						+ "is_deleted,tx_data) VALUES(?,?,?,?,?,?,?,?,false,?)")) {
			s.setLong(1, getHashIndex(sendTx.getTxHash()));
			s.setBytes(2, sendTx.getTxHash().getBytes());
			s.setBytes(3, sendTx.getNormalizedID().getBytes());
			s.setLong(4, sendTx.getTxTime());
			if (sendTx.getBlockHash() == null)
				s.setNull(5, Types.BINARY);
			else
				s.setBytes(5, sendTx.getBlockHash().getBytes());
			s.setBytes(6, sendTx.getAddress().getHash());
			s.setLong(7, sendTx.getValue().longValue());
			s.setLong(8, sendTx.getFee().longValue());
			s.setBytes(9, sendTx.getTxData());
			s.executeUpdate();
		} catch (SQLException exc) {
			BTCLoader.error(String.format(
					"Unable to store send transaction\n  Tx %s",
					sendTx.getTxHash()), exc);
			throw new BlockStoreException("Unable to store send transaction");
		}
	}

	public void setSendTxDelete(Sha256Hash txHash, boolean isDeleted)
			throws BlockStoreException {
		Connection conn = getConnection();
		try (PreparedStatement s = conn
				.prepareStatement("UPDATE Sent SET is_deleted=? "
						+ "WHERE tx_hash_index=? AND tx_hash=?")) {
			s.setBoolean(1, isDeleted);
			s.setLong(2, getHashIndex(txHash));
			s.setBytes(3, txHash.getBytes());
			s.executeUpdate();
		} catch (SQLException exc) {
			BTCLoader.error(String.format(
					"Unable to update send transaction\n  Tx %s", txHash), exc);
			throw new BlockStoreException("Unable to update send transaction");
		}
	}

	public SendTransaction getSendTx(Sha256Hash txHash)
			throws BlockStoreException {
		SendTransaction tx = null;
		Connection conn = getConnection();
		ResultSet r;
		try (PreparedStatement s = conn
				.prepareStatement("SELECT norm_hash,timestamp,block_hash,address,value,fee,"
						+ "tx_data FROM Sent WHERE tx_hash_index=? AND tx_hash=? AND is_deleted=false")) {
			s.setLong(1, getHashIndex(txHash));
			s.setBytes(2, txHash.getBytes());
			r = s.executeQuery();
			if (r.next()) {
				Sha256Hash normID = new Sha256Hash(r.getBytes(1));
				long txTime = r.getLong(2);
				byte[] bytes = r.getBytes(3);
				Sha256Hash blockHash = (bytes != null ? new Sha256Hash(bytes)
						: null);
				Address address = new Address(r.getBytes(4));
				BigInteger value = BigInteger.valueOf(r.getLong(5));
				BigInteger fee = BigInteger.valueOf(r.getLong(6));
				byte[] txData = r.getBytes(7);
				tx = new SendTransaction(normID, txHash, txTime, blockHash,
						address, value, fee, txData);
			}
		} catch (SQLException exc) {
			BTCLoader.error(String.format(
					"Unable to get send transaction\n  Tx %s", txHash), exc);
			throw new BlockStoreException("Unable to get send transaction");
		}
		return tx;
	}

	public List<SendTransaction> getSendTxList() throws BlockStoreException {
		List<SendTransaction> txList = new LinkedList<>();
		Map<Sha256Hash, SendTransaction> txMap = new HashMap<Sha256Hash, SendTransaction>();
		Connection conn = getConnection();
		ResultSet r;
		try (PreparedStatement s = conn
				.prepareStatement("SELECT tx_hash,norm_hash,timestamp,"
						+ "block_hash,address,value,fee,tx_data FROM Sent WHERE is_deleted=false")) {
			r = s.executeQuery();
			while (r.next()) {
				Sha256Hash txHash = new Sha256Hash(r.getBytes(1));
				Sha256Hash normID = new Sha256Hash(r.getBytes(2));
				long txTime = r.getLong(3);
				byte[] bytes = r.getBytes(4);
				Sha256Hash blockHash = (bytes != null ? new Sha256Hash(bytes)
						: null);
				Address address = new Address(r.getBytes(5));
				BigInteger value = BigInteger.valueOf(r.getLong(6));
				BigInteger fee = BigInteger.valueOf(r.getLong(7));
				byte[] txData = r.getBytes(8);
				SendTransaction tx = new SendTransaction(normID, txHash,
						txTime, blockHash, address, value, fee, txData);
				SendTransaction prevTx = txMap.get(normID);
				if (blockHash != null) {
					if (prevTx != null) {
						txList.remove(prevTx);
					}
					txList.add(tx);
					txMap.put(normID, tx);
				} else if (prevTx == null) {
					txList.add(tx);
					txMap.put(normID, tx);
				}
			}
		} catch (SQLException exc) {
			BTCLoader.error("Unable to get send transaction list", exc);
			throw new BlockStoreException("Unable to get send transaction list");
		}
		return txList;
	}

	public void deleteTransactions(long rescanTime) throws BlockStoreException {
		Connection conn = getConnection();
		try (PreparedStatement s1 = conn
				.prepareStatement("DELETE FROM Received "
						+ "WHERE timestamp>=? AND block_hash IS NOT NULL");
				PreparedStatement s2 = conn
						.prepareStatement("DELETE FROM Sent "
								+ "WHERE timestamp>=? AND block_hash IS NOT NULL")) {
			s1.setLong(1, rescanTime);
			s1.executeUpdate();
			s2.setLong(1, rescanTime);
			s2.executeUpdate();
		} catch (SQLException exc) {
			BTCLoader.error("Unable to delete rescan transactions", exc);
			throw new BlockStoreException(
					"Unable to delete rescan transactions");
		}
	}

	public StoredHeader getHeader(Sha256Hash blockHash)
			throws BlockStoreException {
		StoredHeader header = null;
		Connection conn = getConnection();
		ResultSet r;
		try (PreparedStatement s = conn
				.prepareStatement("SELECT prev_hash,version,timestamp,target_difficulty,"
						+ "merkle_root,block_height,chain_work,matches FROM Headers "
						+ "WHERE block_hash_index=? AND block_hash=?")) {
			s.setLong(1, getHashIndex(blockHash));
			s.setBytes(2, blockHash.getBytes());
			r = s.executeQuery();
			if (r.next()) {
				Sha256Hash prevHash = new Sha256Hash(r.getBytes(1));
				int version = r.getInt(2);
				long timestamp = r.getLong(3);
				long targetDifficulty = r.getLong(4);
				Sha256Hash merkleRoot = new Sha256Hash(r.getBytes(5));
				int blockHeight = r.getInt(6);
				BigInteger blockWork = new BigInteger(r.getBytes(7));
				List<Sha256Hash> matches = getMatches(r.getBytes(8));
				header = new StoredHeader(version, blockHash, prevHash,
						timestamp, targetDifficulty, merkleRoot,
						blockHeight >= 0, blockHeight, blockWork, matches);
			}
		} catch (SQLException exc) {
			BTCLoader.error(String.format(
					"Unable to get block header\n  Block %s", blockHash), exc);
			throw new BlockStoreException("Unable to get block header");
		}
		return header;
	}

	public StoredHeader getChildHeader(Sha256Hash parentHash)
			throws BlockStoreException {
		StoredHeader header = null;
		Connection conn = getConnection();
		ResultSet r;
		try (PreparedStatement s = conn
				.prepareStatement("SELECT block_hash,version,timestamp,target_difficulty,"
						+ "merkle_root,block_height,chain_work,matches FROM Headers "
						+ "WHERE prev_hash_index=? AND prev_hash=?")) {
			s.setLong(1, getHashIndex(parentHash));
			s.setBytes(2, parentHash.getBytes());
			r = s.executeQuery();
			if (r.next()) {
				Sha256Hash blockHash = new Sha256Hash(r.getBytes(1));
				int version = r.getInt(2);
				long timestamp = r.getLong(3);
				long targetDifficulty = r.getLong(4);
				Sha256Hash merkleRoot = new Sha256Hash(r.getBytes(5));
				int blockHeight = r.getInt(6);
				BigInteger blockWork = new BigInteger(r.getBytes(7));
				List<Sha256Hash> matches = getMatches(r.getBytes(8));
				header = new StoredHeader(version, blockHash, parentHash,
						timestamp, targetDifficulty, merkleRoot,
						blockHeight >= 0, blockHeight, blockWork, matches);
			}
		} catch (SQLException exc) {
			BTCLoader.error(String.format(
					"Unable to get child header\n  Block %s", parentHash), exc);
			throw new BlockStoreException("Unable to get child header");
		}
		return header;
	}

	public void updateMatches(BlockHeader header) throws BlockStoreException {
		Connection conn = getConnection();
		try (PreparedStatement s = conn
				.prepareStatement("UPDATE Headers SET matches=? "
						+ "WHERE block_hash_index=? AND block_hash=?")) {
			if (header.getMatches() == null || header.getMatches().isEmpty()) {
				s.setNull(1, Types.BINARY);
			} else {
				s.setBytes(1, getMatches(header.getMatches()));
			}
			s.setLong(2, getHashIndex(header.getHash()));
			s.setBytes(3, header.getHash().getBytes());
			s.executeUpdate();
		} catch (SQLException exc) {
			BTCLoader.error(String.format(
					"Unable to update matched transactions\n  Block %s",
					header.getHash()), exc);
			throw new BlockStoreException(
					"Unable to update matched transactions");
		}
	}

	public int getRescanHeight(long rescanTime) throws BlockStoreException {
		int height = 0;
		Connection conn = getConnection();
		ResultSet r;
		try (PreparedStatement s = conn
				.prepareStatement("SELECT block_height,timestamp FROM Headers "
						+ "WHERE timestamp<? ORDER BY timestamp DESC LIMIT 1")) {
			s.setLong(1, rescanTime);
			r = s.executeQuery();
			if (r.next())
				height = r.getInt(1);
		} catch (SQLException exc) {
			BTCLoader.error("Unable to get rescan height", exc);
			throw new BlockStoreException("Unable to get rescan height");
		}
		return height;
	}

	public Sha256Hash getBlockHash(int blockHeight) throws BlockStoreException {
		Sha256Hash blockHash = null;
		Connection conn = getConnection();
		ResultSet r;
		try (PreparedStatement s = conn
				.prepareStatement("SELECT block_hash FROM Headers WHERE block_height=?")) {
			s.setInt(1, blockHeight);
			r = s.executeQuery();
			if (r.next()) {
				blockHash = new Sha256Hash(r.getBytes(1));
			}
		} catch (SQLException exc) {
			BTCLoader.error(String.format("Unable to get block at height %d",
					exc));
			throw new BlockStoreException("Unable to get block");
		}
		return blockHash;
	}

	public List<StoredHeader> getJunctionHeader(Sha256Hash chainHash)
			throws BlockNotFoundException, BlockStoreException {
		List<StoredHeader> chainList = new LinkedList<>();
		boolean onChain = false;
		Sha256Hash blockHash = chainHash;
		synchronized (lock) {
			while (!onChain) {
				StoredHeader header = getHeader(blockHash);
				if (header == null) {
					BTCLoader.debug(String.format(
							"Chain block is not available\n  Block %s",
							blockHash));
					throw new BlockNotFoundException(
							"Unable to resolve block chain", blockHash);
				}
				chainList.add(0, header);
				blockHash = header.getPrevHash();
				onChain = header.isOnChain();
			}
		}
		return chainList;
	}
	
	public void setChainStoredHead(List<StoredHeader> chainList)
			throws BlockStoreException, VerificationException {
		if (!BTCLoader.testNetwork) {
			for (StoredHeader header : chainList) {
				Sha256Hash checkHash = checkpoints.get(Integer.valueOf(header
						.getBlockHeight()));
				if (checkHash != null) {
					if (checkHash.equals(header.getHash())) {
						BTCLoader
								.info(String
										.format("New chain head at height %d matches checkpoint",
												header.getBlockHeight()));
					} else {
						BTCLoader
								.error(String
										.format("New chain head at height %d does not match checkpoint",
												header.getBlockHeight()));
						throw new VerificationException(
								"Checkpoint verification failed",
								RejectMessage.REJECT_CHECKPOINT,
								header.getHash());
					}
				}
			}
		}
		StoredHeader chainHeader = chainList.get(chainList.size() - 1);
		Connection conn = getConnection();
		ResultSet r;
		synchronized (lock) {
			StoredHeader header;
			Sha256Hash blockHash;
			Sha256Hash prevHash;
			List<Sha256Hash> txList;
			try (PreparedStatement s1 = conn
					.prepareStatement("SELECT prev_hash,matches FROM Headers "
							+ "WHERE block_hash_index=? AND block_hash=? FOR UPDATE");
					PreparedStatement s2 = conn
							.prepareStatement("UPDATE Received SET block_hash=? "
									+ "WHERE tx_hash_index=? AND tx_hash=?");
					PreparedStatement s3 = conn
							.prepareStatement("UPDATE Sent SET block_hash=? "
									+ "WHERE tx_hash_index=? AND tx_hash=?");
					PreparedStatement s4 = conn
							.prepareStatement("UPDATE Headers SET block_height=-1 "
									+ "WHERE block_hash_index=? AND block_hash=?");
					PreparedStatement s5 = conn
							.prepareStatement("UPDATE Headers SET block_height=?,chain_work=? "
									+ "WHERE block_hash_index=? AND block_hash=?")) {
				conn.setAutoCommit(false);
				if (!chainHead.equals(chainHeader.getPrevHash())) {
					Sha256Hash junctionHash = chainList.get(0).getHash();
					blockHash = chainHead;
					while (!blockHash.equals(junctionHash)) {
						s1.setLong(1, getHashIndex(blockHash));
						s1.setBytes(2, blockHash.getBytes());
						r = s1.executeQuery();
						if (!r.next()) {
							BTCLoader.error(String.format(
									"Chain block not found\n  Block %s",
									blockHash));
							throw new BlockStoreException("Chain block not found",
									blockHash);
						}
						prevHash = new Sha256Hash(r.getBytes(1));
						byte[] bytes = r.getBytes(2);
						r.close();
						if (bytes != null) {
							txList = getMatches(bytes);
							for (Sha256Hash txHash : txList) {
								s2.setNull(1, Types.BINARY);
								s2.setLong(2, getHashIndex(txHash));
								s2.setBytes(3, txHash.getBytes());
								s2.executeUpdate();
								s3.setNull(1, Types.BINARY);
								s3.setLong(2, getHashIndex(txHash));
								s3.setBytes(3, txHash.getBytes());
								s3.executeUpdate();
							}
						}
						s4.setLong(1, getHashIndex(blockHash));
						s4.setBytes(2, blockHash.getBytes());
						s4.executeUpdate();
						BTCLoader.info(String.format(
								"Block removed from block chain\n  Block %s",
								blockHash));
						blockHash = prevHash;
					}
				}
				for (int i = 1; i < chainList.size(); i++) {
					header = chainList.get(i);
					blockHash = header.getHash();
					int blockHeight = header.getBlockHeight();
					txList = header.getMatches();
					if (txList != null) {
						for (Sha256Hash txHash : txList) {
							s2.setBytes(1, blockHash.getBytes());
							s2.setLong(2, getHashIndex(txHash));
							s2.setBytes(3, txHash.getBytes());
							s2.executeUpdate();
							s3.setBytes(1, blockHash.getBytes());
							s3.setLong(2, getHashIndex(txHash));
							s3.setBytes(3, txHash.getBytes());
							s3.executeUpdate();
						}
					}
					s5.setInt(1, blockHeight);
					s5.setBytes(2, header.getChainWork().toByteArray());
					s5.setLong(3, getHashIndex(blockHash));
					s5.setBytes(4, blockHash.getBytes());
					s5.executeUpdate();
					BTCLoader
							.info(String
									.format("Block added to block chain at height %d, Difficulty %d\n  Block %s",
											blockHeight, header.getChainWork(),
											blockHash));
				}
				conn.commit();
				conn.setAutoCommit(true);
				chainHead = chainHeader.getHash();
				chainHeight = chainHeader.getBlockHeight();
				chainWork = chainHeader.getChainWork();
			} catch (SQLException exc) {
				BTCLoader.error("Unable to update block chain", exc);
				rollback();
				throw new BlockStoreException("Unable to update block chain");
			}
		}
	}
	
	public void storeHeader(StoredHeader storedHeader) throws BlockStoreException {
		Connection conn = getConnection();
		try (PreparedStatement s = conn
				.prepareStatement("INSERT INTO Headers "
						+ "(block_hash_index,block_hash,prev_hash_index,prev_hash,version,timestamp,"
						+ "target_difficulty,merkle_root,block_height,chain_work,matches) "
						+ "VALUES(?,?,?,?,?,?,?,?,?,?,?)")) {
			s.setLong(1, getHashIndex(storedHeader.getHash()));
			s.setBytes(2, storedHeader.getHash().getBytes());
			s.setLong(3, getHashIndex(storedHeader.getPrevHash()));
			s.setBytes(4, storedHeader.getPrevHash().getBytes());
			s.setInt(5, storedHeader.getVersion());
			s.setLong(6, storedHeader.getBlockTime());
			s.setLong(7, storedHeader.getTargetDifficulty());
			s.setBytes(8, storedHeader.getMerkleRoot().getBytes());
			s.setInt(9,
					storedHeader.isOnChain() ? storedHeader.getBlockHeight()
							: -1);
			s.setBytes(10, storedHeader.getChainWork().toByteArray());
			if (storedHeader.getMatches() == null
					|| storedHeader.getMatches().isEmpty()){
				s.setNull(11, Types.BINARY);
			}
			else{
				s.setBytes(11, getMatches(storedHeader.getMatches()));
			}
			s.executeUpdate();
		} catch (SQLException exc) {
			BTCLoader.error(String.format(
					"Unable to store block header\n  Block %s",
					storedHeader.getHash()), exc);
			throw new BlockStoreException("Unable to store block header");
		}
	}

	private byte[] getMatches(List<Sha256Hash> matches) {
		if (matches == null || matches.isEmpty())
			return null;
		byte[] bytes = new byte[matches.size() * 32];
		int offset = 0;
		for (Sha256Hash txHash : matches) {
			System.arraycopy(txHash.getBytes(), 0, bytes, offset, 32);
			offset += 32;
		}
		return bytes;
	}

	private List<Sha256Hash> getMatches(byte[] bytes) {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		List<Sha256Hash> matches = new ArrayList<>(bytes.length / 32);
		for (int offset = 0; offset < bytes.length; offset += 32) {
			matches.add(new Sha256Hash(bytes, offset, 32));
		}
		return matches;
	}

}
