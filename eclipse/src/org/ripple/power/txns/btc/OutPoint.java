package org.ripple.power.txns.btc;

import java.io.EOFException;

import org.ripple.power.Helper;

/**
 * An outpoint describes the transaction output that is connected to a transaction input.  It consists
 * of the hash for the transaction containing the output and the index of the output within the transaction.
 */
public class OutPoint implements ByteSerializable {

    /** The transaction hash */
    private final Sha256Hash txHash;

    /** The output index */
    private final int outputIndex;

    /**
     * Create a new transaction output point
     *
     * @param       txHash          Hash for the transaction output
     * @param       outputIndex     Index of the output within the transaction
     */
    public OutPoint(Sha256Hash txHash, int outputIndex) {
        this.txHash = txHash;
        this.outputIndex = outputIndex;
    }

    /**
     * Create a new transaction output point
     *
     * @param       inBuffer        Input buffer
     * @throws      EOFException    End-of-data processing input stream
     */
    public OutPoint(SerializedBuffer inBuffer) throws EOFException {
        txHash = new Sha256Hash(Helper.reverseBytes(inBuffer.getBytes(32)));
        outputIndex = inBuffer.getInt();
    }

    /**
     * Return the serialized output point
     *
     * @param       outBuffer       Output buffer
     */
    @Override
    public SerializedBuffer getBytes(SerializedBuffer outBuffer) {
        outBuffer.putBytes(Helper.reverseBytes(txHash.getBytes()))
                 .putInt(outputIndex);
        return outBuffer;
    }

    /**
     * Returns the serialized output point
     *
     * @return                      Serialized output point
     */
    @Override
    public byte[] getBytes() {
        SerializedBuffer buffer = new SerializedBuffer(36);
        return getBytes(buffer).toByteArray();
    }

    /**
     * Returns the transaction hash
     *
     * @return      Transaction hash
     */
    public Sha256Hash getHash() {
        return txHash;
    }

    /**
     * Returns the output index
     *
     * @return      Output index
     */
    public int getIndex() {
        return outputIndex;
    }

    /**
     * Returns the hash code
     *
     * @return      Hash code
     */
    @Override
    public int hashCode() {
        return txHash.hashCode() ^ (outputIndex|(outputIndex<<16));
    }

    /**
     * Compares two objects
     *
     * @param       obj             Object to compare
     * @return                      TRUE if they are equal
     */
    @Override
    public boolean equals(Object obj) {
        return (obj!=null && (obj instanceof OutPoint) &&
                txHash.equals(((OutPoint)obj).txHash) && outputIndex==((OutPoint)obj).outputIndex);
    }
}
