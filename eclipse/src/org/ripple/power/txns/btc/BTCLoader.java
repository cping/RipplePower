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
import java.net.UnknownHostException;
import java.security.KeyException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.LogManager;

import javax.swing.JOptionPane;

import org.ripple.power.config.LSystem;
import org.ripple.power.ui.UIRes;
import org.ripple.power.ui.errors.ErrorLog;
import org.ripple.power.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BTCLoader {
    // Thread group
    private static ThreadGroup threadGroup;
    
    // Message handler
    private static MessageHandler messageHandler;


    // Worker threads
    private static final List<Thread> threads = new ArrayList<>(5);
    
	public static final Logger log = LoggerFactory
			.getLogger("org.ripple.power.btc");

	public static boolean testNetwork = true;

	public static String propFileName = "btcstatus.properties";
	
	public static File propFile;

	public static Properties properties;

	// 1 Satoshi = 0.00000001 BTC
	private static final BigDecimal SATOSHI = new BigDecimal("100000000");

	// Minimum supported protocol level (we require SPV support)
	public static final int MIN_PROTOCOL_VERSION = 70001;

	// Default network port
	public static final int DEFAULT_PORT = 8333;

	// Genesis block bytes
	public static byte[] GENESIS_BLOCK_BYTES;

	// Minimum transaction fee
	public static final BigInteger MIN_TX_FEE = new BigInteger("1000", 10);

	// Dust transaction value
	public static final BigInteger DUST_TRANSACTION = new BigInteger("546", 10);

	// Maximum ban score before a peer is disconnected
	public static final int MAX_BAN_SCORE = 100;

	// Coinbase transaction maturity
	public static final int COINBASE_MATURITY = 120;

	// Transaction maturity
	public static final int TRANSACTION_CONFIRMED = 6;

	// Short-term lock object
	public static final Object lock = new Object();

	// Message handler queue
	public static final ArrayBlockingQueue<Message> messageQueue = new ArrayBlockingQueue<>(
			50);

	// Database handler queue
	public static final ArrayBlockingQueue<Object> databaseQueue = new ArrayBlockingQueue<>(
			50);

	// Peer addresses
	public static final List<PeerAddress> peerAddresses = new LinkedList<PeerAddress>();

	// Peer address map
	public static final Map<PeerAddress, PeerAddress> peerMap = new HashMap<PeerAddress, PeerAddress>();

	// Completed messages
	public static final List<Message> completedMessages = new ArrayList<>(50);

	// List of peer requests that are waiting to be sent
	public static final List<PeerRequest> pendingRequests = new ArrayList<>(50);

	// List of peer requests that are waiting for a response
	public static final List<PeerRequest> processedRequests = new ArrayList<>(
			50);

	// Network handler
	public static NetworkHandler networkHandler;

	// Database handler
	public static DatabaseHandler databaseHandler;

	// Inventory handler
	public static MessageListener messageListener;

	// Wallet database
	public static Wallet wallet;

	// Bloom filter
	public static BloomFilter bloomFilter;

	// Key list
	public static List<ECKey> keys;

	// Change key
	public static ECKey changeKey;

	// Address list
	public static List<Address> addresses;

	// Network chain height
	public static int networkChainHeight;

	// Loading block chain
	public static boolean loadingChain = false;

	// Wallet passphrase
	public static String passPhrase;

	private static PeerAddress[] peerAddressesArray;

	protected static void info(String message) {
		log.info(message);
	}

	protected static void debug(String message) {
		log.debug(message);
	}

	protected static void debug(String message, Throwable thr) {
		log.debug(message, thr);
	}

	protected static void warn(String message) {
		log.warn(message);
	}

	protected static void warn(String message, Throwable thr) {
		log.warn(message, thr);
	}

	protected static void error(String message) {
		log.error(message);
	}

	protected static void error(String message, Throwable thr) {
		log.error(message, thr);
	}

	public static void start(String[] cmds) {
		try {
			String dataPath = LSystem.getBitcionDirectory();
			if (cmds.length != 0) {
				processArguments(cmds);
			}
			if (testNetwork) {
				dataPath = dataPath + LSystem.FS + "TestNet";
			}
			passPhrase = LSystem.getAppPassword();
			File dirFile = new File(dataPath);
			if (!dirFile.exists()) {
				FileUtils.makedirs(dirFile);
			}
			File logFile = new File(dataPath + LSystem.FS + "btclog.properties");
			if (logFile.exists()) {
				FileInputStream inStream = new FileInputStream(logFile);
				LogManager.getLogManager().readConfiguration(inStream);
			}
			BriefLogFormatter.init();
			processConfig();
			if (testNetwork && peerAddressesArray == null) {
				throw new IllegalArgumentException(
						"You must specify at least one peer for the test network");
			}
			String genesisName;
			if (testNetwork) {
				genesisName = "res/GenesisBlockTest.dat";
			} else {
				genesisName = "res/GenesisBlockProd.dat";
			}
	        try (InputStream stream = UIRes.getStream(genesisName)) {
                if (stream == null){
                    throw new IOException("Genesis block resource not found");
                }
                GENESIS_BLOCK_BYTES = new byte[stream.available()];
                stream.read(GENESIS_BLOCK_BYTES);
            }
			 propFile = new File(dataPath+LSystem.FS+propFile);
	            properties = new Properties();
	            if (propFile.exists()) {
	                try (FileInputStream in = new FileInputStream(propFile)) {
	                    properties.load(in);
	                }
	            }
	            NetParams.configure(testNetwork, MIN_PROTOCOL_VERSION, 0);
	    
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	  protected static void startup() {
	        try {
	            if (passPhrase == null || passPhrase.length() == 0) {
	                passPhrase = JOptionPane.showInputDialog("Enter the wallet passphrase");
	                if (passPhrase == null || passPhrase.length() == 0){
	                    LSystem.exit();
	                }
	            }
	            wallet = new WalletDataBase(LSystem.getBitcionDirectory());
	            //
	            // Get the address and key lists
	            //
	            addresses = wallet.getAddressList();
	            keys = wallet.getKeyList();
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
	                wallet.storeKey(changeKey);
	                keys.add(changeKey);
	            }
	            //
	            // Create our bloom filter
	            //
	            int elementCount = keys.size()*2 + 15;
	            BloomFilter filter = new BloomFilter(elementCount);
	            for(ECKey key:keys){
	                filter.insert(key.getPubKey());
	                filter.insert(key.getPubKeyHash());
	            }
	            
	            bloomFilter = filter;
	            //
	            // Create our inventory handler
	            //
	            messageListener = new WalletMessageListener();
	            //
	            // Start the worker threads
	            //
	            // DatabaseListener - 1 thread
	            // NetworkListener - 1 thread
	            // MessageHandler - 1 thread
	            //
	            threadGroup = new ThreadGroup("Workers");

	            databaseHandler = new DatabaseHandler();
	            Thread thread = new Thread(threadGroup, databaseHandler);
	            thread.start();
	            threads.add(thread);

	            networkHandler = new NetworkHandler(peerAddressesArray);
	            thread = new Thread(threadGroup, networkHandler);
	            thread.start();
	            threads.add(thread);

	            messageHandler = new MessageHandler();
	            thread = new Thread(threadGroup, messageHandler);
	            thread.start();
	            threads.add(thread);
	            newGUI();
	        } catch (KeyException exc) {
	            log.error("The wallet passphrase is not correct", exc);
	            JOptionPane.showMessageDialog(null, "The wallet passphrase is not correct",
	                                          "Error", JOptionPane.ERROR_MESSAGE);
	            shutdown();
	        } catch (Exception exc) {
	            ErrorLog.logException("Exception while starting wallet services", exc);
	            shutdown();
	        }
	    }
	  
	  private static void newGUI(){
		  
	  }
	  
	  //close bitcoin server
	  public static void shutdown() {
	        networkHandler.shutdown();
	        databaseHandler.shutdown();
	        messageHandler.shutdown();
	        try {
	            log.info("Waiting for worker threads to stop");
	            for (Thread thread : threads)
	                thread.join(2*60*1000);
	            log.info("Worker threads have stopped");
	        } catch (InterruptedException exc) {
	            log.info("Interrupted while waiting for threads to stop");
	        }
	        wallet.close();
	        saveProperties();
	    }

	public static void saveProperties() {
		try {
			try (FileOutputStream out = new FileOutputStream(propFileName)) {
				properties.store(out, "BitcoinWallet Properties");
			}
		} catch (Exception exc) {
			ErrorLog.logException(
					"Exception while saving application properties", exc);
		}
	}

	private static void processArguments(String[] cmds)
			throws UnknownHostException {
		if (cmds[0].equalsIgnoreCase("TEST")) {
			testNetwork = true;
		} else if (!cmds[0].equalsIgnoreCase("PROD")) {
			throw new IllegalArgumentException(
					"Valid options are PROD and TEST");
		}
		if (cmds.length > 1) {
			throw new IllegalArgumentException(
					"Unrecognized command line parameter");
		}
	}

	private static void processConfig() throws IOException,
			IllegalArgumentException, UnknownHostException {
		File configFile = new File(LSystem.getBitcionDirectory() + LSystem.FS
				+ "BitcoinWallet.conf");
		if (!configFile.exists()) {
			return;
		}
		List<PeerAddress> addressList = new ArrayList<PeerAddress>(5);
		try (BufferedReader in = new BufferedReader(new FileReader(configFile))) {
			String line;
			while ((line = in.readLine()) != null) {
				line = line.trim();
				if (line.length() == 0 || line.charAt(0) == '#') {
					continue;
				}
				int sep = line.indexOf('=');
				if (sep < 1) {
					throw new IllegalArgumentException(String.format(
							"Invalid configuration option: %s", line));
				}
				String option = line.substring(0, sep).trim().toLowerCase();
				String value = line.substring(sep + 1).trim();
				switch (option) {
				case "connect":
					PeerAddress addr = new PeerAddress(value);
					addressList.add(addr);
					break;
				case "passphrase":
					BTCLoader.passPhrase = value;
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
