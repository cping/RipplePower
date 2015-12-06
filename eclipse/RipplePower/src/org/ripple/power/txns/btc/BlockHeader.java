package org.ripple.power.txns.btc;

import java.io.EOFException;
import java.math.BigInteger;
import java.util.List;

import org.ripple.power.Helper;

/**
 * BlockHeader contains the block header.  It is used to validate transactions sent to us
 * and to determine if a transaction has become unconfirmed because the block containing it
 * is no longer on the block chain.
 *
 * <p>The block header has the following format:</p>
 * <pre>
 *   Size           Field               Description
 *   ====           =====               ===========
 *   4 bytes        Version             The block version number
 *   32 bytes       PrevBlockHash       The hash of the preceding block in the chain
 *   32 byte        MerkleRoot          The Merkle root for the transactions in the block
 *   4 bytes        Time                The time the block was mined
 *   4 bytes        Difficulty          The target difficulty
 *   4 bytes        Nonce               The nonce used to generate the required hash
 *</pre>
 */
public class BlockHeader implements ByteSerializable {

    /** Block header length */
    public static final int HEADER_SIZE = 80;

    /** The number that is one greater than the largest representable SHA-256 hash */
    public static final BigInteger LARGEST_HASH = BigInteger.ONE.shiftLeft(256);

    /** Block version */
    private final int version;

    /** Block hash */
    private final Sha256Hash blockHash;

    /** Previous block hash */
    private final Sha256Hash prevHash;

    /** Time block was mined */
    private final long blockTime;

    /** Merkle root */
    private final Sha256Hash merkleRoot;

    /** Target difficulty */
    private final long targetDifficulty;

    /** Nonce */
    private final int nonce;

    /** Matched transactions */
    private List<Sha256Hash> matches;

    /**
     * Create a new BlockHeader
     *
     * @param       version             Block version
     * @param       blockHash           Block hash
     * @param       prevHash            Previous block hash
     * @param       blockTime           Time block was mined (seconds since Unix epoch)
     * @param       targetDifficulty    Target difficulty
     * @param       merkleRoot          Merkle root
     * @param       nonce               Block nonce
     */
    public BlockHeader(int version, Sha256Hash blockHash, Sha256Hash prevHash, long blockTime,
                                        long targetDifficulty, Sha256Hash merkleRoot, int nonce) {
        this.version = version;
        this.blockHash = blockHash;
        this.prevHash = prevHash;
        this.blockTime = blockTime;
        this.targetDifficulty = targetDifficulty;
        this.merkleRoot = merkleRoot;
        this.nonce = nonce;
    }

    /**
     * Create a new BlockHeader
     *
     * @param       version             Block version
     * @param       blockHash           Block hash
     * @param       prevHash            Previous block hash
     * @param       blockTime           Time block was mined (seconds since Unix epoch)
     * @param       targetDifficulty    Target difficulty
     * @param       merkleRoot          Merkle root
     * @param       nonce               Block nonce
     * @param       matches             Matching transactions
     */
    public BlockHeader(int version, Sha256Hash blockHash, Sha256Hash prevHash, long blockTime,
                                long targetDifficulty, Sha256Hash merkleRoot, int nonce,
                                List<Sha256Hash> matches) {
        this.version = version;
        this.blockHash = blockHash;
        this.prevHash = prevHash;
        this.blockTime = blockTime;
        this.targetDifficulty = targetDifficulty;
        this.merkleRoot = merkleRoot;
        this.nonce = nonce;
        this.matches = matches;
    }

    /**
     * Create a BlockHeader from the serialized block header
     *
     * @param       bytes                   Serialized data
     * @param       doVerify                TRUE to verify the block header structure
     * @throws      EOFException            Serialized data is too short
     * @throws      VerificationException   Block verification failed
     */
    public BlockHeader(byte[] bytes, boolean doVerify) throws EOFException, VerificationException {
        this(new SerializedBuffer(bytes), doVerify);
    }

    /**
     * Create a BlockHeader from the serialized block header
     *
     * @param       inBuffer                Input buffer
     * @param       doVerify                TRUE to verify the block header structure
     * @throws      EOFException            Serialized data is too short
     * @throws      VerificationException   Block verification failed
     */
    public BlockHeader(SerializedBuffer inBuffer, boolean doVerify)
                                            throws EOFException, VerificationException {
        if (inBuffer.available() < HEADER_SIZE)
            throw new EOFException("Header is too short");
        //
        // Compute the block hash from the serialized block header
        //
        int startPosition = inBuffer.getPosition();
        blockHash = new Sha256Hash(Helper.reverseBytes(Helper.doubleDigest(inBuffer.getBytes(HEADER_SIZE))));
        inBuffer.setPosition(startPosition);
        //
        // Parse the block header
        //
        version = inBuffer.getInt();
        prevHash = new Sha256Hash(Helper.reverseBytes(inBuffer.getBytes(32)));
        merkleRoot = new Sha256Hash(Helper.reverseBytes(inBuffer.getBytes(32)));
        blockTime = inBuffer.getUnsignedInt();
        targetDifficulty = inBuffer.getUnsignedInt();
        nonce = inBuffer.getInt();
        //
        // Ensure this block does in fact represent real work done.  If the difficulty is high enough,
        // we can be fairly certain the work was done by the network.
        //
        // The block hash must be less than or equal to the target difficulty (the difficulty increases
        // by requiring an increasing number of leading zeroes in the block hash)
        //
        if (doVerify) {
            BigInteger target = Helper.decodeCompactBits(targetDifficulty);
            if (target.signum() <= 0 || target.compareTo(NetParams.PROOF_OF_WORK_LIMIT) > 0)
                throw new VerificationException("Target difficulty is not valid",
                                                RejectMessage.REJECT_INVALID, blockHash);
            BigInteger hash = blockHash.toBigInteger();
            if (hash.compareTo(target) > 0)
                throw new VerificationException("Block hash is higher than target difficulty",
                                                RejectMessage.REJECT_INVALID, blockHash);
            //
            // Verify the block timestamp
            //
            long currentTime = System.currentTimeMillis()/1000;
            if (blockTime > currentTime+NetParams.ALLOWED_TIME_DRIFT)
                throw new VerificationException("Block timestamp is too far in the future",
                                                RejectMessage.REJECT_INVALID, blockHash);
        }
    }

    /**
     * Write the serialized block header to the output buffer
     *
     * @param       outBuffer           Output buffer
     * @return                          Output buffer
     */
    @Override
    public SerializedBuffer getBytes(SerializedBuffer outBuffer) {
        outBuffer.putInt(version)
                 .putBytes(Helper.reverseBytes(prevHash.getBytes()))
                 .putBytes(Helper.reverseBytes(merkleRoot.getBytes()))
                 .putUnsignedInt(blockTime)
                 .putUnsignedInt(targetDifficulty)
                 .putInt(nonce);
        return outBuffer;
    }

    /**
     * Return the serialized bytes
     *
     * @return                          Byte array
     */
    @Override
    public byte[] getBytes() {
        return getBytes(new SerializedBuffer(HEADER_SIZE)).toByteArray();
    }

    /**
     * Return the block version
     *
     * @return                          Block version
     */
    public int getVersion() {
        return version;
    }

    /**
     * Returns the block hash
     *
     * @return                          Block hash
     */
    public Sha256Hash getHash() {
        return blockHash;
    }

    /**
     * Returns the previous block hash
     *
     * @return                          Previous block hash
     */
    public Sha256Hash getPrevHash() {
        return prevHash;
    }

    /**
     * Returns the block time
     *
     * @return                          Block time
     */
    public long getBlockTime() {
        return blockTime;
    }

    /**
     * Returns the Merkle root
     *
     * @return                          Merkle root
     */
    public Sha256Hash getMerkleRoot() {
        return merkleRoot;
    }

    /**
     * Returns the target difficulty
     *
     * @return                          Target difficulty
     */
    public long getTargetDifficulty() {
        return targetDifficulty;
    }

    /**
     * Returns the block work
     *
     * @return                          Block work
     */
    public BigInteger getBlockWork() {
        BigInteger target = Helper.decodeCompactBits(targetDifficulty);
        return LARGEST_HASH.divide(target.add(BigInteger.ONE));
    }

    /**
     * Returns the block nonce
     *
     * @return                          Block nonce
     */
    public int getNonce() {
        return nonce;
    }

    /**
     * Returns the list of matched transactions or null if there are no matched transactions
     *
     * @return                          List of matched transactions
     */
    public List<Sha256Hash> getMatches() {
        return matches;
}

    /**
     * Sets the list of matched transactions
     *
     * @param       matches             List of matched transactions or null if there are no matched transactions
     */
    public void setMatches(List<Sha256Hash> matches) {
        this.matches = matches;
    }
}
