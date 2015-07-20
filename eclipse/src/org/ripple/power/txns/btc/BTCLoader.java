package org.ripple.power.txns.btc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.LogManager;

import org.ripple.power.config.LSystem;
import org.ripple.power.ui.UIRes;
import org.ripple.power.ui.errors.ErrorLog;
import org.ripple.power.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BTCLoader {

	/** Application properties file */
	private static File propFile;

	/** Peer addresses file */
	private static File peersFile;

	public static boolean rpcOpen = false;

	public static boolean loadingChain = false;

	/** Test network */
	public static boolean testNetwork = false;

	public static Properties properties;
	/** Host name */
	private static String hostName;

	/** Listen port */
	public static int listenPort = 8333;

	/** Maximum number of connections */
	private static int maxConnections = 32;

	/** Maximum number of outbound connections */
	private static int maxOutbound = 8;

	/** RPC port */
	private static int rpcPort = 8332;

	/** RPC allowed hosts */
	private static final List<InetAddress> rpcAllowIp = new ArrayList<>();

	/** RPC user */
	private static String rpcUser = "";

	/** RPC password */
	private static String rpcPassword = "";

	/** Create bootstrap files */
	private static boolean createBootstrap = false;

	/** Compact database */
	private static boolean compactDatabase = false;

	/** Load block chain */
	private static boolean loadBlockChain = false;

	/** Retry block */
	private static boolean retryBlock = false;

	/** Bypass block verification */
	private static boolean verifyBlocks = true;

	/** Block chain data directory for load */
	private static String blockChainPath;

	/** Starting block number */
	private static int startBlock;

	/** Stop block number */
	private static int stopBlock;

	/** Retry block hash */
	private static Sha256Hash retryHash;

	/** Peer addresses */
	private static PeerAddress[] peerAddressesArray;

	/** Peer blacklist */
	private static List<NetworkHandler.BlacklistEntry> peerBlacklist = new ArrayList<>();

	/** Thread group */
	private static ThreadGroup threadGroup;

	/** Worker threads */
	private static final List<Thread> threads = new ArrayList<>(5);

	// Short-term lock object
	public static final Object lock = new Object();

	/** Database listener */
	public static DatabaseHandler databaseHandler;

	/** Message handler */
	private static MessageHandler messageHandler;

	/** RPC handler */
	private static RpcHandler rpcHandler;

	/** Application shutdown started */
	public static boolean shutdown = true;

	// 1 Satoshi = 0.00000001 BTC
	private static final BigDecimal SATOSHI = new BigDecimal("100000000");

	// Minimum transaction fee
	public static final BigInteger MIN_TX_FEE = new BigInteger("1000", 10);

	// Dust transaction value
	public static final BigInteger DUST_TRANSACTION = new BigInteger("546", 10);
	// Address list
	public static List<Address> addresses;

	// Bloom filter
	public static BloomFilter bloomFilter;

	// Key list
	public static List<ECKey> keys;

	// Change key
	public static ECKey changeKey;

	// Transaction maturity
	public static final int TRANSACTION_CONFIRMED = 6;

	/** Minimum protocol version */
	public static final int MIN_PROTOCOL_VERSION = 60001;

	/** Genesis block bytes */
	public static byte[] GENESIS_BLOCK_BYTES;

	/** Default network port */
	public static final int DEFAULT_PORT = 8333;

	/** Coinbase transaction maturity */
	public static final int COINBASE_MATURITY = 100;

	/** Minimum transaction relay fee */
	public static final BigInteger MIN_TX_RELAY_FEE = new BigInteger("1000", 10);

	/** Maximum free transaction size */
	public static final int MAX_FREE_TX_SIZE = 10000;

	/** Maximum ban score before a peer is disconnected */
	public static final int MAX_BAN_SCORE = 100;

	/** Maximum peer address age (seconds) */
	public static final int MAX_PEER_ADDRESS_AGE = 2 * 60 * 60;

	/** Block store */
	public static BlockStore blockStore;

	/** Block chain */
	public static BlockChain blockChain;

	/** Network handler */
	public static NetworkHandler networkHandler;

	/** Network message handler */
	public static NetworkMessageListener networkMessageListener;

	/** Local listen address */
	public static PeerAddress listenAddress;

	/** Number of blocks received */
	public static final AtomicLong blocksReceived = new AtomicLong();

	/** Number of blocks sent */
	public static final AtomicLong blocksSent = new AtomicLong();

	/** Number of filtered blocks sent */
	public static final AtomicLong filteredBlocksSent = new AtomicLong();

	/** Number of transactions received */
	public static final AtomicLong txReceived = new AtomicLong();

	/** Number of transactions sent */
	public static final AtomicLong txSent = new AtomicLong();

	/** Number of transactions rejected */
	public static final AtomicLong txRejected = new AtomicLong();

	/** Network chain height */
	public static int networkChainHeight;

	/**
	 * List of peer requests that are waiting to be sent - synchronized on
	 * pendingRequests
	 */
	public static final List<PeerRequest> pendingRequests = new LinkedList<>();

	/**
	 * List of peer requests that are waiting for a response - synchronized on
	 * pendingRequests
	 */
	public static final List<PeerRequest> processedRequests = new LinkedList<>();

	/**
	 * Map of transactions in the memory pool (txHash, tx) - synchronized on
	 * txMap
	 */
	public static final Map<Sha256Hash, StoredTransaction> txMap = new HashMap<>(
			250);

	/** Map of recent transactions (txHash, txHash) - synchronized on txMap */
	public static final Map<Sha256Hash, Sha256Hash> recentTxMap = new HashMap<>(
			250);

	/**
	 * Map of orphan transactions (parentTxHash, orphanTxList) - synchronized on
	 * txMap
	 */
	public static final Map<Sha256Hash, List<StoredTransaction>> orphanTxMap = new HashMap<>(
			250);

	/**
	 * Map of recent spent outputs (Outpoint. spendingTxHash) - synchronized on
	 * txMap
	 */
	public static final Map<OutPoint, Sha256Hash> spentOutputsMap = new HashMap<>(
			250);

	/** List of Bloom filters - synchronized on bloomFilters */
	public static final List<BloomFilter> bloomFilters = new LinkedList<>();

	/** Database handler message queue */
	public static final LinkedBlockingQueue<Object> databaseQueue = new LinkedBlockingQueue<>();

	/** Message handler message queue */
	public static final LinkedBlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>(
			250);

	/** Peer addresses - synchronized on peerAddresses */
	public static final List<PeerAddress> peerAddresses = new LinkedList<>();

	/** Peer address map - synchronized on peerAddresses */
	public static final Map<PeerAddress, PeerAddress> peerMap = new HashMap<>(
			250);

	/** Completed messages */
	public static final ConcurrentLinkedQueue<Message> completedMessages = new ConcurrentLinkedQueue<>();

	/** Alert list */
	public static final List<Alert> alerts = new ArrayList<Alert>();

	public static final Logger log = LoggerFactory
			.getLogger("org.ripple.power.btc");

	public static void info(String message) {
		if (testNetwork) {
			log.info(message);
		}
	}

	public static void debug(String message) {
		if (testNetwork) {
			log.debug(message);
		}
	}

	public static void debug(String message, Throwable thr) {
		if (testNetwork) {
			log.debug(message, thr);
		}
	}

	public static void warn(String message) {
		if (testNetwork) {
			log.warn(message);
		}
	}

	public static void warn(String message, Throwable thr) {
		if (testNetwork) {
			log.warn(message, thr);
		}
	}

	public static void error(String message) {
		if (testNetwork) {
			log.error(message);
		}
	}

	public static void error(String message, Throwable thr) {
		if (testNetwork) {
			log.error(message, thr);
		}
	}

	public static void start(String[] cmds) {
		try {
			shutdown = false;
			String dataPath = LSystem.getBitcionDirectory();
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					shutdown();
				}
			});
			String pString = System.getProperty("bitcoin.verify.blocks");
			if (pString != null && pString.equals("0")) {
				verifyBlocks = false;
			}
			if (cmds.length != 0) {
				processArguments(cmds);
			}
			if (testNetwork) {
				dataPath = dataPath + LSystem.FS + "TestNet";
			}
			File dirFile = new File(dataPath);
			if (!dirFile.exists()) {
				FileUtils.makedirs(dirFile);
			}
			File logFile = new File(dataPath + LSystem.FS
					+ "logging.properties");
			if (logFile.exists()) {
				FileInputStream inStream = new FileInputStream(logFile);
				LogManager.getLogManager().readConfiguration(inStream);
			}
			BriefLogFormatter.init();
			processConfig();
			if (testNetwork && peerAddressesArray == null && maxOutbound != 0) {
				throw new IllegalArgumentException(
						"You must specify at least one peer for the test network");
			}
			String genesisName = (testNetwork ? "GenesisBlockTest.dat"
					: "GenesisBlockProd.dat");
			try (InputStream classStream = UIRes.getStream(genesisName)) {
				if (classStream == null) {
					throw new IOException("Genesis block resource not found");
				}
				BTCLoader.GENESIS_BLOCK_BYTES = new byte[classStream
						.available()];
				classStream.read(BTCLoader.GENESIS_BLOCK_BYTES);
			}
			propFile = new File(dataPath + LSystem.FS + "btc.properties");
			properties = new Properties();
			if (propFile.exists()) {
				try (FileInputStream in = new FileInputStream(propFile)) {
					properties.load(in);
				}
			}
			//
			// Initialize the Bitcoin consensus library
			//
			BitcoinConsensus.init();
			//
			// Initialize the BitcoinCore library
			//
			NetParams.configure(testNetwork, BTCLoader.MIN_PROTOCOL_VERSION,
					NetParams.NODE_NETWORK);

			blockStore = new BlockStoreDataBase(dataPath);
			//
			// Compact the database
			//
			if (compactDatabase) {
				blockStore.compactDatabase();
				shutdown();
			}
			//
			// Create the block chain
			//
			blockChain = new BlockChain(verifyBlocks);

			//
			// Retry a held block and then exit
			//
			if (retryBlock) {
				StoredBlock storedBlock = blockStore.getStoredBlock(retryHash);
				if (storedBlock != null) {
					if (!storedBlock.isOnChain()) {
						blockChain.updateBlockChain(storedBlock);
					} else {
						log.error(String.format(
								"Block is already on the chain\n  Block %s",
								retryHash.toString()));
					}
				} else {
					log.error(String.format("Block not found\n  Block %s",
							retryHash.toString()));
				}
				shutdown();
			}
			//
			// Load the block chain from disk and then exit
			//
			if (loadBlockChain) {
				LoadBlockChain.load(blockChainPath, startBlock, stopBlock);
				shutdown();
			}
			//
			// Create the bootstrap files and then exit
			//
			if (createBootstrap) {
				CreateBootstrap.process(blockChainPath, startBlock, stopBlock);
				shutdown();
			}
			//
			// Get the peer addresses
			//
			peersFile = new File(String.format("%s%speers.dat", dataPath,
					LSystem.FS));
			if (peersFile.exists() && peersFile.length() > 0) {
				byte[] fileBuffer = new byte[(int) peersFile.length()];
				try (FileInputStream inStream = new FileInputStream(peersFile)) {
					inStream.read(fileBuffer);
				}
				SerializedBuffer inBuffer = new SerializedBuffer(fileBuffer);
				while (inBuffer.available() > 0) {
					PeerAddress peerAddress = new PeerAddress(inBuffer);
					BTCLoader.peerAddresses.add(peerAddress);
					BTCLoader.peerMap.put(peerAddress, peerAddress);
				}
			}

			//
			// Get the address and key lists
			//
			addresses = blockStore.getAddressList();
			keys = blockStore.getKeyList();
			//
			// Locate the change key and create it if we don't have one yet
			//
			for (ECKey key : keys) {
				if (key.isChange()) {
					changeKey = key;
					break;
				}
			}
			if (changeKey == null) {
				ECKey changeKey = new ECKey();
				changeKey.setLabel("<Change>");
				changeKey.setChange(true);
				blockStore.storeKey(changeKey);
				keys.add(changeKey);
			}
			//
			// Create our bloom filter
			//
			int elementCount = keys.size() * 2 + 15;
			BloomFilter filter = new BloomFilter(elementCount);
			for (ECKey key : keys) {
				filter.insert(key.getPubKey());
				filter.insert(key.getPubKeyHash());
			}

			bloomFilter = filter;

			//
			// Start the worker threads
			//
			threadGroup = new ThreadGroup("Workers");

			databaseHandler = new DatabaseHandler();
			Thread thread = new Thread(threadGroup, databaseHandler,
					"Database Handler");
			thread.start();
			threads.add(thread);

			BTCLoader.networkMessageListener = new NetworkMessageListener();
			BTCLoader.networkHandler = new NetworkHandler(maxConnections,
					maxOutbound, hostName, listenPort, peerAddressesArray,
					peerBlacklist);
			thread = new Thread(threadGroup, BTCLoader.networkHandler,
					"Network Handler");
			thread.start();
			threads.add(thread);

			messageHandler = new MessageHandler();
			thread = new Thread(threadGroup, messageHandler, "Message Handler");
			thread.start();
			threads.add(thread);

			if (rpcOpen) {
				//
				// Start the RPC handler
				//
				rpcHandler = new RpcHandler(rpcPort, rpcAllowIp, rpcUser,
						rpcPassword);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void shutdown() {
		if (shutdown) {
			return;
		}
		shutdown = true;
		log.info(LSystem.applicationName + " shutdown started");
		if (!threads.isEmpty()) {
			try {
				if (BTCLoader.networkHandler != null) {
					BTCLoader.networkHandler.shutdown();
				}
				if (databaseHandler != null) {
					databaseHandler.shutdown();
				}
				if (messageHandler != null) {
					messageHandler.shutdown();
				}
				if (rpcHandler != null) {
					rpcHandler.shutdown();
				}
				for (Thread thread : threads) {
					thread.join(120000);
				}
			} catch (InterruptedException exc) {
			}
		}

		if (blockStore != null) {
			blockStore.close();
		}
		if (!BTCLoader.peerAddresses.isEmpty()) {
			try {
				try (FileOutputStream outStream = new FileOutputStream(
						peersFile)) {
					int peerCount = 0;
					for (PeerAddress peerAddress : BTCLoader.peerAddresses) {
						if (!peerAddress.isStatic()) {
							outStream.write(peerAddress.getBytes());
							peerCount++;
							if (peerCount >= 50)
								break;
						}
					}
				}
			} catch (IOException exc) {
				log.error("Unable to save peer addresses", exc);
			}
		}
		if (propFile != null) {
			saveProperties();
		}
		log.info("JavaBitcoin shutdown completed");
		if (LogManager.getLogManager() instanceof LogManagerOverride)
			((LogManagerOverride) LogManager.getLogManager()).logShutdown();

	}

	public static void saveProperties() {
		try {
			try (FileOutputStream out = new FileOutputStream(propFile)) {
				properties.store(out, "JavaBitcoin Properties");
			}
		} catch (Exception exc) {
			ErrorLog.logException(
					"Exception while saving application properties", exc);
		}
	}

	private static void processArguments(String[] args)
			throws UnknownHostException {
		//
		// PROD indicates we should use the production network
		// TEST indicates we should use the test network
		// LOAD indicates we should load the block chain from the reference
		// client data directory
		// RETRY indicates we should retry a block that is currently held
		// MIGRATE indicate we should migrate a LevelDB database to an H2
		// database
		//
		switch (args[0].toLowerCase()) {
		case "bootstrap":
			createBootstrap = true;
			if (args.length < 2)
				throw new IllegalArgumentException(
						"Specify PROD or TEST with the BOOTSTRAP option");
			if (args[1].equalsIgnoreCase("TEST")) {
				testNetwork = true;
			} else if (!args[1].equalsIgnoreCase("PROD")) {
				throw new IllegalArgumentException(
						"Specify PROD or TEST after the BOOTSTRAP option");
			}
			if (args.length < 3) {
				throw new IllegalArgumentException(
						"You must specify the bootstrap directory");
			}
			blockChainPath = args[2];
			if (args.length > 3) {
				startBlock = Integer.parseInt(args[3]);
				if (startBlock < 0)
					throw new IllegalArgumentException(
							"Start height is less than 0");
			} else {
				startBlock = 0;
			}
			if (args.length > 4) {
				stopBlock = Integer.parseInt(args[4]);
				if (stopBlock < startBlock)
					throw new IllegalArgumentException(
							"Stop height is less than start height");
			} else {
				stopBlock = Integer.MAX_VALUE;
			}
			if (args.length > 5)
				throw new IllegalArgumentException(
						"Unrecognized command line parameter");
			break;
		case "compact":
			compactDatabase = true;
			if (args.length < 2)
				throw new IllegalArgumentException(
						"Specify PROD or TEST with the COMPACT option");
			if (args[1].equalsIgnoreCase("TEST")) {
				testNetwork = true;
			} else if (!args[1].equalsIgnoreCase("PROD")) {
				throw new IllegalArgumentException(
						"Specify PROD or TEST after the COMPACT option");
			}
			if (args.length > 2)
				throw new IllegalArgumentException(
						"Unrecognized command line parameter");
			break;
		case "load":
			loadBlockChain = true;
			if (args.length < 2)
				throw new IllegalArgumentException(
						"Specify PROD or TEST with the LOAD option");
			if (args[1].equalsIgnoreCase("TEST")) {
				testNetwork = true;
			} else if (!args[1].equalsIgnoreCase("PROD")) {
				throw new IllegalArgumentException(
						"Specify PROD or TEST after the LOAD option");
			}
			if (args.length > 2) {
				blockChainPath = args[2];
			} else {
				blockChainPath = LSystem.getBitcionDirectory();
			}
			if (args.length > 3) {
				startBlock = Integer.parseInt(args[3]);
				if (startBlock < 0)
					throw new IllegalArgumentException(
							"Start block is less than 0");
			} else {
				startBlock = 0;
			}
			if (args.length > 4) {
				stopBlock = Integer.parseInt(args[4]);
				if (stopBlock < startBlock)
					throw new IllegalArgumentException(
							"Stop block is less than start block");
			} else {
				stopBlock = Integer.MAX_VALUE;
			}
			if (args.length > 5)
				throw new IllegalArgumentException(
						"Unrecognized command line parameter");
			break;
		case "retry":
			retryBlock = true;
			if (args.length < 3)
				throw new IllegalArgumentException(
						"Specify PROD or TEST followed by the block hash");
			if (args[1].equalsIgnoreCase("TEST")) {
				testNetwork = true;
			} else if (!args[1].equalsIgnoreCase("PROD")) {
				throw new IllegalArgumentException(
						"Specify PROD or TEST after the RETRY option");
			}
			retryHash = new Sha256Hash(args[2]);
			if (args.length > 3)
				throw new IllegalArgumentException(
						"Unrecognized command line parameter");
			break;
		case "test":
			testNetwork = true;
			if (args.length > 1)
				throw new IllegalArgumentException(
						"Unrecognized command line parameter");
			break;
		case "prod":
			if (args.length > 1) {
				throw new IllegalArgumentException(
						"Unrecognized command line parameter");
			}
			break;
		default:
			throw new IllegalArgumentException(
					"Unrecognized command line parameter");
		}
	}

	/**
	 * Process the configuration file
	 *
	 * @throws IllegalArgumentException
	 *             Invalid configuration option
	 * @throws IOException
	 *             Unable to read configuration file
	 * @throws UnknownHostException
	 *             Invalid peer address specified
	 */
	private static void processConfig() throws IOException,
			IllegalArgumentException, UnknownHostException {
		//
		// Use the defaults if there is no configuration file
		//
		File configFile = new File(LSystem.getBitcionDirectory() + LSystem.FS
				+ "rpbitcoin.conf");
		if (!configFile.exists()) {
			return;
		}
		//
		// Process the configuration file
		//
		List<PeerAddress> addressList = new ArrayList<>(5);
		try (BufferedReader in = new BufferedReader(new FileReader(configFile))) {
			String line;
			while ((line = in.readLine()) != null) {
				line = line.trim();
				if (line.length() == 0 || line.charAt(0) == '#')
					continue;
				int sep = line.indexOf('=');
				if (sep < 1)
					throw new IllegalArgumentException(String.format(
							"Invalid configuration option: %s", line));
				String option = line.substring(0, sep).trim().toLowerCase();
				String value = line.substring(sep + 1).trim();
				switch (option) {
				case "blacklistpeer":
					sep = value.indexOf('/');
					InetAddress blacklistAddr;
					int mask;
					if (sep < 0 || sep == value.length() - 1) {
						blacklistAddr = InetAddress.getByName(value);
						mask = -1;
					} else if (sep == 0) {
						throw new IllegalArgumentException(
								"Invalid blacklist address: " + value);
					} else {
						blacklistAddr = InetAddress.getByName(value.substring(
								0, sep));
						mask = Integer.parseInt(value.substring(sep + 1));
					}
					peerBlacklist.add(new NetworkHandler.BlacklistEntry(
							blacklistAddr, mask));
					log.info(value + " added to peer blacklist");
					break;
				case "connect":
					PeerAddress addr = new PeerAddress(value);
					addressList.add(addr);
					break;
				case "hostname":
					hostName = value;
					break;
				case "maxconnections":
					maxConnections = Integer.parseInt(value);
					break;
				case "maxoutbound":
					maxOutbound = Integer.parseInt(value);
					break;
				case "port":
					listenPort = Integer.parseInt(value);
					break;
				case "rpcallowip":
					InetAddress[] inetAddrs = InetAddress.getAllByName(value);
					rpcAllowIp.addAll(Arrays.asList(inetAddrs));
					rpcOpen = true;
					break;
				case "rpcpassword":
					rpcPassword = value;
					rpcOpen = true;
					break;
				case "rpcport":
					rpcPort = Integer.parseInt(value);
					rpcOpen = true;
					break;
				case "rpcuser":
					rpcUser = value;
					rpcOpen = true;
					break;
				default:
					throw new IllegalArgumentException(String.format(
							"Invalid configuration option: %s", line));
				}
			}
		}
		if (!addressList.isEmpty()) {
			peerAddressesArray = addressList
					.toArray(new PeerAddress[addressList.size()]);
		}
	}

	public static BigInteger stringToSatoshi(String value)
			throws NumberFormatException {
		if (value == null) {
			throw new IllegalArgumentException("No string value provided");
		}
		if (value.isEmpty()) {
			return BigInteger.ZERO;
		}
		BigDecimal decValue = new BigDecimal(value);
		return decValue.multiply(SATOSHI).toBigInteger();
	}

	public static String satoshiToString(BigInteger value) {
		BigInteger bvalue = value;
		boolean negative = bvalue.compareTo(BigInteger.ZERO) < 0;
		if (negative) {
			bvalue = bvalue.negate();
		}
		BigDecimal dvalue = new BigDecimal(bvalue, 8);
		String formatted = dvalue.toPlainString();
		int decimalPoint = formatted.indexOf(".");
		int toDelete = 0;
		for (int i = formatted.length() - 1; i > decimalPoint + 4; i--) {
			if (formatted.charAt(i) == '0') {
				toDelete++;
			} else {
				break;
			}
		}
		String text = (negative ? "-" : "")
				+ formatted.substring(0, formatted.length() - toDelete);
		return text;
	}

	public static void dumpData(String text, byte[] data) {
		dumpData(text, data, 0, data.length);
	}

	public static void dumpData(String text, byte[] data, int length) {
		dumpData(text, data, 0, length);
	}

	public static void dumpData(String text, byte[] data, int offset, int length) {
		StringBuilder outString = new StringBuilder(512);
		outString.append(text);
		outString.append("\n");
		for (int i = 0; i < length; i++) {
			if (i % 32 == 0)
				outString.append(String.format(" %14X  ", i));
			else if (i % 4 == 0)
				outString.append(" ");
			outString.append(String.format("%02X", data[offset + i]));
			if (i % 32 == 31)
				outString.append("\n");
		}
		if (length % 32 != 0) {
			outString.append("\n");
		}
		info(outString.toString());
	}

}
