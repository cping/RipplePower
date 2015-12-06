package org.ripple.power.txns.btc;

import java.math.BigInteger;


public class ReceiveTransaction extends BlockTransaction {

    private int txIndex;

    private byte[] scriptBytes;

    private boolean isSpent;

    private boolean isChange;

    private boolean isCoinBase;

    private boolean inSafe;

    public ReceiveTransaction(Sha256Hash normID, Sha256Hash txHash, int txIndex, long txTime,
                            Sha256Hash blockHash, Address address, BigInteger value, byte[] scriptBytes,
                            boolean isChange, boolean isCoinBase) {
        this(normID, txHash, txIndex, txTime, blockHash, address, value, scriptBytes,
                            false, isChange, isCoinBase, false);
    }

    public ReceiveTransaction(Sha256Hash normID, Sha256Hash txHash, int txIndex, long txTime,
                                Sha256Hash blockHash, Address address, BigInteger value, byte[] scriptBytes,
                                boolean isSpent, boolean isChange, boolean isCoinBase, boolean inSafe) {
        super(normID, txHash, txTime, blockHash, address, value);
        this.txIndex = txIndex;
        this.scriptBytes = scriptBytes;
        this.isSpent = isSpent;
        this.isChange = isChange;
        this.isCoinBase = isCoinBase;
        this.inSafe = inSafe;
    }

    public int getTxIndex() {
        return txIndex;
    }

    public byte[] getScriptBytes() {
        return scriptBytes;
    }

    public boolean isSpent() {
        return isSpent;
    }

    public void setSpent(boolean isSpent) {
        this.isSpent = isSpent;
    }

    public boolean isChange() {
        return isChange;
    }

    public boolean isCoinBase() {
        return isCoinBase;
    }

    public boolean inSafe() {
        return inSafe;
    }

    public void setSafe(boolean inSafe) {
        this.inSafe = inSafe;
    }
}
