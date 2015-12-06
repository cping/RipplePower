package org.ripple.power.txns.btc;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.security.KeyException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.ripple.power.Helper;
import org.ripple.power.config.LSystem;
import org.ripple.power.utils.FileUtils;

public abstract class BlockStore {

	/** Maximum block file size */
	protected static final long MAX_BLOCK_FILE_SIZE = 256 * 1024 * 1024;

	/**
	 * Maximum age (seconds) of spent transactions in the transaction outputs
	 * table
	 */
	protected static final long MAX_TX_AGE = 2 * 24 * 60 * 60;

	/** Block chain checkpoints */
	protected static final Map<Integer, Sha256Hash> checkpoints = new HashMap<Integer, Sha256Hash>();
	static {
		checkpoints
				.put(50000,
						new Sha256Hash(
								"000000001aeae195809d120b5d66a39c83eb48792e068f8ea1fea19d84a4278a"));
		checkpoints
				.put(75000,
						new Sha256Hash(
								"00000000000ace2adaabf1baf9dc0ec54434db11e9fd63c1819d8d77df40afda"));
		checkpoints
				.put(100000,
						new Sha256Hash(
								"000000000003ba27aa200b1cecaad478d2b00432346c3f1f3986da1afd33e506"));
		checkpoints
				.put(125000,
						new Sha256Hash(
								"00000000000042391c3620056af66ca9ad7cb962424a9b34611915cebb9e1a2a"));
		checkpoints
				.put(150000,
						new Sha256Hash(
								"0000000000000a3290f20e75860d505ce0e948a1d1d846bec7e39015d242884b"));
		checkpoints
				.put(175000,
						new Sha256Hash(
								"00000000000006b975c097e9a5235de03d9024ddb205fd24dfcd508403fa907c"));
		checkpoints
				.put(200000,
						new Sha256Hash(
								"000000000000034a7dedef4a161fa058a2d67a173a90155f3a2fe6fc132e0ebf"));
		checkpoints
				.put(225000,
						new Sha256Hash(
								"000000000000013d8781110987bf0e9f230e3cc85127d1ee752d5dd014f8a8e1"));
		checkpoints
				.put(250000,
						new Sha256Hash(
								"000000000000003887df1f29024b06fc2200b55f8af8f35453d7be294df2d214"));
		checkpoints
				.put(275000,
						new Sha256Hash(
								"00000000000000044750d80a0d3f3e307e54e8802397ae840d91adc28068f5bc"));
		checkpoints
				.put(300000,
						new Sha256Hash(
								"000000000000000082ccf8f1557c5d40b21edabb18d2d691cfbf87118bac7254"));
		checkpoints
				.put(325000,
						new Sha256Hash(
								"00000000000000000409695bce21828b31a7143fa35fcab64670dd337a71425d"));
	}

	/** Database update lock */
	protected final Object lock = new Object();

	/** Application data path */
	protected String dataPath;

	/** Chain update time */
	protected long chainTime;

	/** Chain head */
	protected Sha256Hash chainHead;

	/** Block preceding the chain head */
	protected Sha256Hash prevChainHead;

	/** Target difficulty */
	protected long targetDifficulty;

	/** Current chain height */
	protected int chainHeight;

	/** Current chain work */
	protected BigInteger chainWork;

	/** Current block file number */
	protected int blockFileNumber;

	/** Compressed block inflater */
	protected final Inflater inflater = new Inflater();

	/** Compressed block deflater */
	protected final Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);

	/**
	 * Creates a BlockStore
	 *
	 * @param dataPath
	 *            Application data path
	 */
	public BlockStore(String dataPath) {
		this.dataPath = dataPath;
		//
		// Create the Blocks subdirectory if it doesn't exist
		//
		File blocksDir = new File(dataPath + LSystem.FS + "Blocks");
		if (!blocksDir.exists()) {
			try {
				FileUtils.makedirs(blocksDir);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Closes the database
	 */
	public void close() {
	}

	/**
	 * Compacts the database tables
	 *
	 * @throws BlockStoreException
	 *             Unable to compact database
	 */
	public void compactDatabase() throws BlockStoreException {
	}

	/**
	 * Returns the block hash for the current chain head
	 *
	 * @return Chain head block hash
	 */
	public Sha256Hash getChainHead() {
		return chainHead;
	}

	/**
	 * Returns the current chain height
	 *
	 * @return Current chain height
	 */
	public int getChainHeight() {
		return chainHeight;
	}

	/**
	 * Returns the current target difficulty as a BigInteger
	 *
	 * @return Target difficulty
	 */
	public BigInteger getTargetDifficulty() {
		return Helper.decodeCompactBits(targetDifficulty);
	}

	/**
	 * Returns the current chain work
	 *
	 * @return Current chain work
	 */
	public BigInteger getChainWork() {
		return chainWork;
	}

	/**
	 * Checks if the block is already in our database
	 *
	 * @param blockHash
	 *            The block to check
	 * @return TRUE if this is a new block
	 * @throws BlockStoreException
	 *             Unable to check the block status
	 */
	public abstract boolean isNewBlock(Sha256Hash blockHash)
			throws BlockStoreException;

	/**
	 * Checks if the alert is already in our database
	 *
	 * @param alertID
	 *            Alert identifier
	 * @return TRUE if this is a new alert
	 * @throws BlockStoreException
	 *             Unable to get the alert status
	 */
	public abstract boolean isNewAlert(int alertID) throws BlockStoreException;

	/**
	 * Returns a list of all alerts in the database
	 *
	 * @return List of all alerts
	 * @throws BlockStoreException
	 *             Unable to get alerts from database
	 */
	public abstract List<Alert> getAlerts() throws BlockStoreException;

	/**
	 * Stores an alert in the database
	 *
	 * @param alert
	 *            The alert
	 * @throws BlockStoreException
	 *             Unable to store the alert
	 */
	public abstract void storeAlert(Alert alert) throws BlockStoreException;

	/**
	 * Cancels an alert
	 *
	 * @param alertID
	 *            The alert identifier
	 * @throws BlockStoreException
	 *             Unable to update the alert
	 */
	public abstract void cancelAlert(int alertID) throws BlockStoreException;

	/**
	 * Checks if the block is on the main chain
	 *
	 * @param blockHash
	 *            The block to check
	 * @return TRUE if the block is on the main chain
	 * @throws BlockStoreException
	 *             Unable to get the block status
	 */
	public abstract boolean isOnChain(Sha256Hash blockHash)
			throws BlockStoreException;

	/**
	 * Returns a block that was stored in the database. The returned block
	 * represents the block data sent over the wire and does not include any
	 * information about the block location within the block chain.
	 *
	 * @param blockHash
	 *            Block hash
	 * @return The block or null if the block is not found
	 * @throws BlockStoreException
	 *             Unable to get block from database
	 */
	public abstract Block getBlock(Sha256Hash blockHash)
			throws BlockStoreException;

	/**
	 * Returns the block hash for the block stored at the specified height.
	 *
	 * @param height
	 *            Chain height
	 * @return The block hash or null if the block is not found
	 * @throws BlockStoreException
	 *             Unable to get block from database
	 */
	public abstract Sha256Hash getBlockId(int height)
			throws BlockStoreException;

	/**
	 * Returns a block that was stored in the database. The returned block
	 * contains the basic block plus information about its current location
	 * within the block chain.
	 *
	 * @param blockHash
	 *            The block hash
	 * @return The stored block or null if the block is not found
	 * @throws BlockStoreException
	 *             Unable to get block from database
	 */
	public abstract StoredBlock getStoredBlock(Sha256Hash blockHash)
			throws BlockStoreException;

	/**
	 * Returns the child block for the specified block
	 *
	 * @param blockHash
	 *            The block hash
	 * @return The stored block or null if the block is not found
	 * @throws BlockStoreException
	 *             Unable to get block
	 */
	public abstract StoredBlock getChildStoredBlock(Sha256Hash blockHash)
			throws BlockStoreException;

	/**
	 * Returns the block status for recent blocks
	 *
	 * @param maxCount
	 *            The maximum number of blocks to be returned
	 * @return A list of BlockStatus objects
	 * @throws BlockStoreException
	 *             Unable to get block status
	 */
	public abstract List<BlockStatus> getBlockStatus(int maxCount)
			throws BlockStoreException;

	/**
	 * Check if this is a new transaction
	 *
	 * @param txHash
	 *            Transaction hash
	 * @return TRUE if the transaction is not in the database
	 * @throws BlockStoreException
	 *             Unable to check transaction status
	 */
	public abstract boolean isNewTransaction(Sha256Hash txHash)
			throws BlockStoreException;

	/**
	 * Returns the transaction depth. A depth of 0 indicates the transaction is
	 * not in a block on the current chain.
	 *
	 * @param txHash
	 *            Transaction hash
	 * @return Transaction depth
	 * @throws BlockStoreException
	 *             Unable to get transaction depth
	 */
	public abstract int getTxDepth(Sha256Hash txHash)
			throws BlockStoreException;

	/**
	 * Returns the requested transaction output
	 *
	 * @param outPoint
	 *            Transaction outpoint
	 * @return Transaction output or null if the transaction is not found
	 * @throws BlockStoreException
	 *             Unable to get transaction output status
	 */
	public abstract StoredOutput getTxOutput(OutPoint outPoint)
			throws BlockStoreException;

	/**
	 * Returns the outputs for the specified transaction
	 *
	 * @param txHash
	 *            Transaction hash
	 * @return Stored output list
	 * @throws BlockStoreException
	 *             Unable to get transaction outputs
	 */
	public abstract List<StoredOutput> getTxOutputs(Sha256Hash txHash)
			throws BlockStoreException;

	/**
	 * Deletes spent transaction outputs that are older than the maximum
	 * transaction age
	 *
	 * @return The number of deleted outputs
	 * @throws BlockStoreException
	 *             Unable to delete spent transaction outputs
	 */
	public abstract int deleteSpentTxOutputs() throws BlockStoreException;

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
	public abstract List<InventoryItem> getChainList(Sha256Hash startBlock,
			Sha256Hash stopBlock) throws BlockStoreException;

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
	public abstract List<InventoryItem> getChainList(int startHeight,
			Sha256Hash stopBlock) throws BlockStoreException;

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
	public abstract List<BlockHeader> getHeaderList(Sha256Hash startBlock,
			Sha256Hash stopBlock) throws BlockStoreException;

	/**
	 * Releases a held block for processing
	 *
	 * @param blockHash
	 *            Block hash
	 * @throws BlockStoreException
	 *             Unable to release the block
	 */
	public abstract void releaseBlock(Sha256Hash blockHash)
			throws BlockStoreException;

	/**
	 * Stores a block in the database
	 *
	 * @param storedBlock
	 *            Block to be stored
	 * @throws BlockStoreException
	 *             Unable to store the block
	 */
	public abstract void storeBlock(StoredBlock storedBlock)
			throws BlockStoreException;

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
	public abstract List<StoredBlock> getJunction(Sha256Hash chainHash)
			throws BlockNotFoundException, BlockStoreException,
			ChainTooLongException;

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
	public abstract void setChainHead(List<StoredBlock> chainList)
			throws BlockStoreException, VerificationException;

	/**
	 * Returns a block that was stored in one of the block files
	 *
	 * An uncompressed block has the following format: Bytes 0-3: Magic number
	 * Bytes 4-7: Block length Bytes 8-n: Block data
	 *
	 * A compressed block has the following format: Bytes 0-3: Magic number
	 * Bytes 4-7: Compressed data length with the high-order bit set to 1 Bytes
	 * 8-11: Uncompressed data length Bytes 12-n: Compressed block data
	 *
	 * @param fileNumber
	 *            The block file number
	 * @param fileOffset
	 *            The block offset within the file
	 * @return The requested block or null if the block is not found
	 * @throws BlockStoreException
	 *             Unable to read the block data
	 */
	protected Block getBlock(int fileNumber, int fileOffset)
			throws BlockStoreException {
		if (fileNumber < 0) {
			throw new BlockStoreException(String.format(
					"Invalid file number %d", fileNumber));
		}
		Block block = null;
		File blockFile = new File(String.format("%s%sBlocks%sblk%05d.dat",
				dataPath, LSystem.FS, LSystem.FS, fileNumber));
		if (!blockFile.exists()) {
			BTCLoader.info(String.format("Block file %d does not exist", fileNumber));
			return null;
		}
		try {
			try (RandomAccessFile inFile = new RandomAccessFile(blockFile, "r")) {
				//
				// Read the block prefix
				//
				inFile.seek(fileOffset);
				byte[] bytes = new byte[8];
				int count = inFile.read(bytes);
				if (count != 8) {
					BTCLoader.error(String
							.format("End-of-data reading from block file %d, offset %d",
									fileNumber, fileOffset));
					throw new BlockStoreException("Unable to read block file");
				}
				long magic = Helper.readUint32LE(bytes, 0);
				long length = Helper.readUint32LE(bytes, 4);
				if (magic != NetParams.MAGIC_NUMBER) {
					BTCLoader.error(String
							.format("Magic number %X is incorrect in block file %d, offset %d",
									magic, fileNumber, fileOffset));
					throw new BlockStoreException("Incorrect block file format");
				}
				if ((length & 0x80000000L) != 0) {
					//
					// Read the compressed block data
					//
					length &= 0x7fffffffL;
					byte[] compressedData = new byte[(int) length + 4];
					count = inFile.read(compressedData);
					if (count != length + 4) {
						BTCLoader.error(String
								.format("End-of-data reading compressed block from file %d, offset %d",
										fileNumber, fileOffset));
						throw new BlockStoreException(
								"Unable to read block file");
					}
					length = Helper.readUint32LE(compressedData, 0);
					byte[] blockData = new byte[(int) length];
					synchronized (inflater) {
						inflater.reset();
						inflater.setInput(compressedData, 4,
								compressedData.length - 4);
						count = inflater.inflate(blockData);
						if (count != length || !inflater.finished()) {
							BTCLoader.error(String
									.format("Incomplete compressed block read from file %d, offset %d",
											fileNumber, fileOffset));
							throw new BlockStoreException(
									"Unable to read block file");
						}
					}
					block = new Block(blockData, 0, (int) length, false);
				} else {
					byte[] blockData = new byte[(int) length];
					count = inFile.read(blockData);
					if (count != length) {
						BTCLoader.error(String
								.format("End-of-data reading uncompressed block from file %d, offset %d",
										fileNumber, fileOffset));
						throw new BlockStoreException(
								"Unable to read block file");
					}
					block = new Block(blockData, 0, (int) length, false);
				}
			}
		} catch (DataFormatException | IOException | VerificationException exc) {
			BTCLoader.error(String.format("Unable to read block file %d, offset %d",
					fileNumber, fileOffset), exc);
			throw new BlockStoreException("Unable to read block file");
		}
		return block;
	}

	protected int[] storeBlock(Block block) throws BlockStoreException {
		int[] blockLocation = new int[2];
		try {
			byte[] blockData = block.getBytes();
			byte[] compressedData = new byte[blockData.length + 128];
			int offset = 0;
			synchronized (deflater) {
				deflater.reset();
				deflater.setInput(blockData);
				deflater.finish();
				while (true) {
					int count = deflater.deflate(compressedData, offset,
							compressedData.length - offset);
					offset += count;
					if (deflater.finished()){
						break;
					}
					compressedData = Arrays
							.copyOf(compressedData,
									offset
											+ (blockData.length
													- (int) deflater
															.getBytesRead() + 256));
				}
			}
			String fileName = String.format("%s%sBlocks%sblk%05d.dat",
					dataPath, LSystem.FS, LSystem.FS,
					blockFileNumber);
			File blockFile = new File(fileName);
			if(!blockFile.exists()){
				FileUtils.makedirs(blockFile);
			}
			long filePosition = blockFile.length();
			if (filePosition >= MAX_BLOCK_FILE_SIZE) {
				blockFileNumber++;
				filePosition = 0;
				blockFile = new File(fileName);
				if (blockFile.exists()){
					blockFile.delete();
				}
			}
			try (RandomAccessFile outFile = new RandomAccessFile(blockFile,
					"rws")) {
				outFile.seek(filePosition);
				byte[] bytes = new byte[12];
				Helper.uint32ToByteArrayLE(NetParams.MAGIC_NUMBER, bytes, 0);
				Helper.uint32ToByteArrayLE((long) offset | 0x80000000L, bytes, 4);
				Helper.uint32ToByteArrayLE(blockData.length, bytes, 8);
				outFile.write(bytes);
				outFile.write(compressedData, 0, offset);
				blockLocation[0] = blockFileNumber;
				blockLocation[1] = (int) filePosition;
			}
		} catch (IOException exc) {
			BTCLoader.error(String.format("Unable to write to block file %d",
					blockFileNumber), exc);
			throw new BlockStoreException("Unable to write to block file");
		}
		return blockLocation;
	}

	/**
	 * Truncate a block file to recover from a database error
	 *
	 * @param fileLocation
	 *            The file location returned by storeBlock()
	 */
	protected void truncateBlockFile(int[] fileLocation) {
		File blockFile = new File(String.format("%s%sBlocks%sblk%05d.dat",
				dataPath, LSystem.FS, LSystem.FS,
				fileLocation[0]));
		try {
			//
			// If the block is stored at the beginning of the file, just delete
			// the file
			// and decrement the block number. Otherwise, truncate the file.
			if (fileLocation[1] == 0) {
				blockFile.delete();
				blockFileNumber--;
			} else {
				try (RandomAccessFile outFile = new RandomAccessFile(blockFile,
						"rws")) {
					outFile.getChannel().truncate(fileLocation[1]);
				}
			}
		} catch (IOException exc) {
			BTCLoader.error(String.format("Unable to truncate block file %d",
					fileLocation[0]), exc);
		}
	}
	

	public abstract void setAddressLabel(Address address) throws BlockStoreException;

	public abstract void storeAddress(Address address) throws BlockStoreException;

	public abstract void deleteAddress(Address address) throws BlockStoreException ;

	public abstract List<Address> getAddressList() throws BlockStoreException ;

	public abstract void storeKey(ECKey key) throws BlockStoreException;
	
	public abstract void setKeyLabel(ECKey key) throws BlockStoreException;
	
	public abstract List<ECKey> getKeyList() throws KeyException, BlockStoreException;
	

	public abstract void storeReceiveTx(ReceiveTransaction receiveTx)
			throws BlockStoreException;

	public abstract void setTxSpent(Sha256Hash txHash, int txIndex, boolean isSpent)
			throws BlockStoreException;

	public abstract void setTxSafe(Sha256Hash txHash, int txIndex, boolean inSafe)
			throws BlockStoreException;

	public abstract void setReceiveTxDelete(Sha256Hash txHash, int txIndex,
			boolean isDeleted) throws BlockStoreException ;

	public abstract List<ReceiveTransaction> getReceiveTxList() throws BlockStoreException;

	public abstract void storeSendTx(SendTransaction sendTx) throws BlockStoreException;

	public abstract void setSendTxDelete(Sha256Hash txHash, boolean isDeleted)
			throws BlockStoreException;

	public abstract SendTransaction getSendTx(Sha256Hash txHash) throws BlockStoreException;

	public abstract List<SendTransaction> getSendTxList() throws BlockStoreException;
	
	public abstract void deleteTransactions(long rescanTime) throws BlockStoreException;
	
	public abstract StoredHeader getHeader(Sha256Hash blockHash) throws BlockStoreException ;
	
	public abstract StoredHeader getChildHeader(Sha256Hash parentHash)
			throws BlockStoreException ;
	
	public abstract void updateMatches(BlockHeader header) throws BlockStoreException;
	
	public abstract int getRescanHeight(long rescanTime) throws BlockStoreException;

	public abstract Sha256Hash getBlockHash(int blockHeight) throws BlockStoreException;
	
	public abstract List<StoredHeader> getJunctionHeader(Sha256Hash chainHash)
			throws BlockNotFoundException, BlockStoreException ;
	
	public abstract void setChainStoredHead(List<StoredHeader> chainList)
			throws BlockStoreException, VerificationException;
	
	public abstract void storeHeader(StoredHeader storedHeader) throws BlockStoreException ;
}