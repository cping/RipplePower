package org.ripple.power.txns.btc;

import java.io.EOFException;
import java.math.BigInteger;

public class SendTransaction extends WalletTransaction {

    private final BigInteger fee;

    private final byte[] txData;

    public SendTransaction(Sha256Hash normID, Sha256Hash txHash, long txTime, Sha256Hash blockHash,
                            Address address, BigInteger value, BigInteger fee, byte[] txData) {
        super(normID, txHash, txTime, blockHash, address, value);
        this.fee = fee;
        this.txData = txData;
    }

    public BigInteger getFee() {
        return fee;
    }

    public byte[] getTxData() {
        return txData;
    }

    public Transaction getTransaction() throws WalletException {
        Transaction tx;
        try {
            SerializedBuffer inBuffer = new SerializedBuffer(txData);
            tx = new Transaction(inBuffer);
        } catch (EOFException | VerificationException exc) {
            throw new WalletException("Unable to deserialize transaction", exc);
        }
        return tx;
    }
}
