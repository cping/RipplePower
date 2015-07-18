package org.ripple.power.txns.btc;

import java.io.IOException;
import java.math.BigInteger;

import org.ripple.power.Helper;

/**
 * Network-specific parameters
 */
public class NetParams {

    /** Protocol version */
    public static final int PROTOCOL_VERSION = 70002;

    /** Minimum acceptable protocol version */
    public static int MIN_PROTOCOL_VERSION = 60001;

    /** Peer provides network services */
    public static final long NODE_NETWORK = 1;

    /** Our supported services */
    public static long SUPPORTED_SERVICES = 0;

    /** Production network magic number */
    public static final long MAGIC_NUMBER_PRODNET = 0xd9b4bef9L;

    /** Test network magic number */
    public static final long MAGIC_NUMBER_TESTNET = 0xdab5bffaL;

    /** Magic number */
    public static long MAGIC_NUMBER = MAGIC_NUMBER_PRODNET;

    /** Production network address version */
    public static final int ADDRESS_VERSION_PRODNET = 0;

    /** Test network address version */
    public static final int ADDRESS_VERSION_TESTNET = 111;

    /** Address version */
    public static int ADDRESS_VERSION = ADDRESS_VERSION_PRODNET;

    /** Production network dumped private key version */
    public static final int DUMPED_PRIVATE_KEY_VERSION_PRODNET = 128;

    /** Test network dumped private key version */
    public static final int DUMPED_PRIVATE_KEY_VERSION_TESTNET = 239;

    /** Dumped private key version */
    public static int DUMPED_PRIVATE_KEY_VERSION = DUMPED_PRIVATE_KEY_VERSION_PRODNET;

    /** Production network maximum target difficulty */
    public static final long MAX_DIFFICULTY_PRODNET = 0x1d00ffffL;

    /** Test network maximum target difficulty */
    public static final long MAX_DIFFICULTY_TESTNET = 0x207fffffL;

    /** Maximum target difficulty (represents least amount of work) */
    public static long MAX_TARGET_DIFFICULTY = MAX_DIFFICULTY_PRODNET;

    /** Proof-of-work limit */
    public static BigInteger PROOF_OF_WORK_LIMIT = Helper.decodeCompactBits(MAX_DIFFICULTY_PRODNET);

    /** Maximum clock drift in seconds */
    public static final long ALLOWED_TIME_DRIFT = 2 * 60 * 60;

    /** Production network genesis block */
    public static final String GENESIS_BLOCK_PRODNET =
                    "000000000019d6689c085ae165831e934ff763ae46a2a6c172b3f1b60a8ce26f";

    /** Test network genesis block */
    public static final String GENESIS_BLOCK_TESTNET =
                    "0f9188f13cb7b2c71f2a335e3a4fc328bf5beb436012afca590b1a11466e2206";

    /** Genesis block hash */
    public static String GENESIS_BLOCK_HASH = GENESIS_BLOCK_PRODNET;

    /** Production network genesis block time */
    public static final long GENESIS_TIME_PRODNET = 0x495fab29L;

    /** Test network genesis block time */
    public static final long GENESIS_TIME_TESTNET = 1296688602L;

    /** Genesis block time */
    public static long GENESIS_BLOCK_TIME = GENESIS_TIME_PRODNET;

    /** Maximum block size */
    public static final int MAX_BLOCK_SIZE = 1*1024*1024;

    /** Maximum message size */
    public static final int MAX_MESSAGE_SIZE = 2*1024*1024;

    /** Maximum amount of money in the Bitcoin system */
    public static final BigInteger MAX_MONEY = new BigInteger("2100000000000000", 10);

    /**
     * Configure the network parameters
     *
     * The configure() method must be called before using any of the BitcoinCore
     * library routines.
     *
     * @param       testNetwork             TRUE for the test network, FALSE for the production network
     * @param       minProtocolVersion      Minimum supported protocol version
     * @param       supportedServices       Supported services
     * @throws      ClassNotFoundException  org.ScripterRon.BitcoinCore.NetParams class not found
     * @throws      IOException             Unable read application properties
     */
    public static void configure(boolean testNetwork, int minProtocolVersion, long supportedServices)
                                            throws ClassNotFoundException, IOException {
        //
        // Initialize data arreas for the desired network
        //
        if (testNetwork) {
            MAGIC_NUMBER = MAGIC_NUMBER_TESTNET;
            ADDRESS_VERSION = ADDRESS_VERSION_TESTNET;
            DUMPED_PRIVATE_KEY_VERSION = DUMPED_PRIVATE_KEY_VERSION_TESTNET;
            GENESIS_BLOCK_HASH = GENESIS_BLOCK_TESTNET;
            GENESIS_BLOCK_TIME = GENESIS_TIME_TESTNET;
            MAX_TARGET_DIFFICULTY = MAX_DIFFICULTY_TESTNET;
        } else {
            MAGIC_NUMBER = MAGIC_NUMBER_PRODNET;
            ADDRESS_VERSION = ADDRESS_VERSION_PRODNET;
            DUMPED_PRIVATE_KEY_VERSION = DUMPED_PRIVATE_KEY_VERSION_PRODNET;
            GENESIS_BLOCK_HASH = GENESIS_BLOCK_PRODNET;
            GENESIS_BLOCK_TIME = GENESIS_TIME_PRODNET;
            MAX_TARGET_DIFFICULTY = MAX_DIFFICULTY_PRODNET;
        }
        PROOF_OF_WORK_LIMIT = Helper.decodeCompactBits(MAX_TARGET_DIFFICULTY);
  
        MIN_PROTOCOL_VERSION = Math.max(MIN_PROTOCOL_VERSION, minProtocolVersion);
        SUPPORTED_SERVICES = supportedServices;
    }
}
