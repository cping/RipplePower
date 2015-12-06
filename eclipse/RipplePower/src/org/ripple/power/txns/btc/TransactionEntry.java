package org.ripple.power.txns.btc;

import java.io.EOFException;
import java.math.BigInteger;

public class TransactionEntry {

    /** Block hash for the block containing this transaction */
    private final Sha256Hash blockHash;

    /** Time when the output was spent */
    private long timeSpent;

    /** Height of block spending this output */
    private int blockHeight;

    /** Value of this output */
    private final BigInteger value;

    /** Script bytes */
    private final byte[] scriptBytes;

    /** Coinbase transaction */
    private final boolean isCoinBase;

    /**
     * Creates a new TransactionEntry
     *
     * @param       blockHash       Block containing this transaction
     * @param       value           Output value
     * @param       scriptBytes     Script bytes
     * @param       timeSpent       Time when all outputs were spent
     * @param       blockHeight     Height of block spending this output
     * @param       isCoinBase      TRUE if this is a coinbase transaction
     */
    public TransactionEntry(Sha256Hash blockHash, BigInteger value, byte[] scriptBytes,
                                    long timeSpent, int blockHeight, boolean isCoinBase) {
        this.blockHash = blockHash;
        this.timeSpent = timeSpent;
        this.value = value;
        this.scriptBytes = scriptBytes;
        this.blockHeight = blockHeight;
        this.isCoinBase = isCoinBase;
    }

    /**
     * Creates a new TransactionEntry from the serialized entry data
     *
     * @param       entryData       Serialized entry data
     * @throws      EOFException    End-of-data processing serialized data
     */
    public TransactionEntry(byte[] entryData) throws EOFException {
        SerializedBuffer inBuffer = new SerializedBuffer(entryData);
        blockHash = new Sha256Hash(inBuffer.getBytes(32));
        timeSpent = inBuffer.getVarLong();
        blockHeight = inBuffer.getVarInt();
        value = new BigInteger(inBuffer.getBytes());
        scriptBytes = inBuffer.getBytes();
        isCoinBase = inBuffer.getBoolean();
    }

    /**
     * Returns the serialized data stream
     *
     * @return      Serialized data stream
     */
    public byte[] getBytes() {
        byte[] valueData = value.toByteArray();
        SerializedBuffer outBuffer = new SerializedBuffer();
        outBuffer.putBytes(blockHash.getBytes())
                 .putVarLong(timeSpent)
                 .putVarInt(blockHeight)
                 .putVarInt(valueData.length)
                 .putBytes(valueData)
                 .putVarInt(scriptBytes.length)
                 .putBytes(scriptBytes)
                 .putBoolean(isCoinBase);
        return outBuffer.toByteArray();
    }

    /**
     * Returns the block hash
     *
     * @return      Block hash
     */
    public Sha256Hash getBlockHash() {
        return blockHash;
    }

    /**
     * Returns the output value
     *
     * @return      Output value
     */
    public BigInteger getValue() {
        return value;
    }

    /**
     * Returns the script bytes
     *
     * @return      Script bytes
     */
    public byte[] getScriptBytes() {
        return scriptBytes;
    }

    /**
     * Checks if this is a coinbase transaction
     *
     * @return      TRUE if this is a coinbase transaction
     */
    public boolean isCoinBase() {
        return isCoinBase;
    }

    /**
     * Returns the time spent
     *
     * @return      Time spent
     */
    public long getTimeSpent() {
        return timeSpent;
    }

    /**
     * Sets the time spent
     *
     * @param       timeSpent       Time spent or zero if all outputs have not been spent
     */
    public void setTimeSpent(long timeSpent) {
        this.timeSpent = timeSpent;
    }

    /**
     * Returns the height of the spending block
     *
     * @return      Block height
     */
    public int getBlockHeight() {
        return blockHeight;
    }

    /**
     * Sets the height of the spending block
     *
     * @param       blockHeight     Height of the spending block
     */
    public void setBlockHeight(int blockHeight) {
        this.blockHeight = blockHeight;
    }
}
