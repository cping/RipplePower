package org.ripple.power.txns.btc;

import java.io.EOFException;

public class StoredTransaction {

    /** Serialized transaction */
    private final byte[] txData;

    /** Transaction hash */
    private final Sha256Hash hash;

    /** Parent transaction hash */
    private Sha256Hash parentHash;

    /** Time when transaction was broadcast */
    private final long txTimeStamp;

    /**
     * Creates a new stored transaction
     *
     * @param       tx                  Transaction
     */
    public StoredTransaction(Transaction tx) {
        hash = tx.getHash();
        txData = tx.getBytes();
        txTimeStamp = System.currentTimeMillis()/1000;
    }

    /**
     * Return the transaction
     *
     * @return      Transaction
     */
    public Transaction getTransaction() {
        SerializedBuffer txBuffer = new SerializedBuffer(txData);
        Transaction tx;
        try {
            tx = new Transaction(txBuffer);
        } catch (EOFException | VerificationException exc) {
            throw new RuntimeException("Unable to get transaction: "+exc.getMessage());
        }
        return tx;
    }

    /**
     * Returns the transaction hash
     *
     * @return      Transaction hash
     */
    public Sha256Hash getHash() {
        return hash;
    }

    /**
     * Returns the parent transaction hash.  The parent is a transaction whose output is
     * being spent by this transaction.  This is used when tracking orphan transactions.
     *
     * @return      Parent transaction hash or null if there is no parent
     */
    public Sha256Hash getParent() {
        return parentHash;
    }

    /**
     * Sets the parent transaction hash
     *
     * @param       parentHash          Parent transaction hash
     */
    public void setParent(Sha256Hash parentHash) {
        this.parentHash = parentHash;
    }

    /**
     * Returns the serialized transaction data
     *
     * @return      Serialized byte stream
     */
    public byte[] getBytes() {
        return txData;
    }

    /**
     * Returns the transaction timestamp
     *
     * @return      Time when transaction was broadcast
     */
    public long getTimeStamp() {
        return txTimeStamp;
    }
}
