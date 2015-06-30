package org.ripple.power.txns.btc;

import java.math.BigInteger;

/**
 * SignInput represents a transaction output that is being spent as part of
 * a new transaction.  It contains the key needed to sign the transaction as
 * well as the transaction output hash and index.
 */
public class SignedInput {

    /** Connected transaction output */
    private final OutPoint outPoint;

    /** Transaction output value */
    private final BigInteger value;

    /** Transaction output script */
    private final byte[] scriptBytes;

    /** Key associated with the transaction output */
    private final ECKey key;

    /**
     * Creates a new SignedInput
     *
     * @param       key                 Key to sign the transaction
     * @param       outPoint            Connected transaction output
     * @param       value               Transaction output value
     * @param       scriptBytes         Transaction output script bytes
     */
    public SignedInput(ECKey key, OutPoint outPoint, BigInteger value, byte[] scriptBytes) {
        this.key = key;
        this.outPoint = outPoint;
        this.value = value;
        this.scriptBytes = scriptBytes;
    }

    /**
     * Returns the key
     *
     * @return                          Key to sign the transaction
     */
    public ECKey getKey() {
        return key;
    }

    /**
     * Returns the connected transaction outpoint
     *
     * @return                          Transaction outpoint
     */
    public OutPoint getOutPoint() {
        return outPoint;
    }

    /**
     * Returns the transaction output value
     *
     * @return                          Transaction output value
     */
    public BigInteger getValue() {
        return value;
    }

    /**
     * Returns the transaction output script
     *
     * @return                          Transaction output script
     */
    public byte[] getScriptBytes() {
        return scriptBytes;
    }
}
