package org.ripple.power.txns.btc;

import java.math.BigInteger;
import java.security.KeyException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Wallet {
    
    protected static final Map<Integer, Sha256Hash> checkpoints = new HashMap<>();
    static {
        checkpoints.put(50000,
                        new Sha256Hash("000000001aeae195809d120b5d66a39c83eb48792e068f8ea1fea19d84a4278a"));
        checkpoints.put(75000,
                        new Sha256Hash("00000000000ace2adaabf1baf9dc0ec54434db11e9fd63c1819d8d77df40afda"));
        checkpoints.put(100000,
                        new Sha256Hash("000000000003ba27aa200b1cecaad478d2b00432346c3f1f3986da1afd33e506"));
        checkpoints.put(125000,
                        new Sha256Hash("00000000000042391c3620056af66ca9ad7cb962424a9b34611915cebb9e1a2a"));
        checkpoints.put(150000,
                        new Sha256Hash("0000000000000a3290f20e75860d505ce0e948a1d1d846bec7e39015d242884b"));
        checkpoints.put(175000,
                        new Sha256Hash("00000000000006b975c097e9a5235de03d9024ddb205fd24dfcd508403fa907c"));
        checkpoints.put(200000,
                        new Sha256Hash("000000000000034a7dedef4a161fa058a2d67a173a90155f3a2fe6fc132e0ebf"));
        checkpoints.put(225000,
                        new Sha256Hash("000000000000013d8781110987bf0e9f230e3cc85127d1ee752d5dd014f8a8e1"));
        checkpoints.put(250000,
                        new Sha256Hash("000000000000003887df1f29024b06fc2200b55f8af8f35453d7be294df2d214"));
        checkpoints.put(275000,
                        new Sha256Hash("00000000000000044750d80a0d3f3e307e54e8802397ae840d91adc28068f5bc"));
        checkpoints.put(300000,
                        new Sha256Hash("000000000000000082ccf8f1557c5d40b21edabb18d2d691cfbf87118bac7254"));
        checkpoints.put(325000,
                        new Sha256Hash("00000000000000000409695bce21828b31a7143fa35fcab64670dd337a71425d"));
    }

    protected final Object lock = new Object();

    protected Sha256Hash chainHead;

    protected int chainHeight;

    protected BigInteger chainWork;
    
    protected final String dataPath;

    public Wallet(String dataPath) throws WalletException {
        this.dataPath = dataPath;
    }

    public int getChainHeight() {
        return chainHeight;
    }

    public Sha256Hash getChainHead() {
        return chainHead;
    }

    public BigInteger getChainWork() {
        return chainWork;
    }

    public abstract int getRescanHeight(long rescanTime) throws WalletException;

    public abstract Sha256Hash getBlockHash(int blockHeight) throws WalletException;

    public abstract List<Sha256Hash> getChainList(int startHeight, Sha256Hash stopBlock) throws WalletException;

    public abstract void storeAddress(Address address) throws WalletException;

    public abstract void setAddressLabel(Address address) throws WalletException;

    public abstract void deleteAddress(Address address) throws WalletException;

    public abstract List<Address> getAddressList() throws WalletException;

    public abstract void storeKey(ECKey key) throws WalletException;

    public abstract void setKeyLabel(ECKey key) throws WalletException;

    public abstract List<ECKey> getKeyList() throws KeyException, WalletException;

    public abstract boolean isNewBlock(Sha256Hash blockHash) throws WalletException;

    public abstract void storeHeader(StoredHeader storedHeader) throws WalletException;

    public abstract void updateMatches(BlockHeader header) throws WalletException;

    public abstract StoredHeader getHeader(Sha256Hash blockHash) throws WalletException;

    public abstract StoredHeader getChildHeader(Sha256Hash parentHash) throws WalletException;

    public abstract boolean isNewTransaction(Sha256Hash txHash) throws WalletException;

    public abstract void storeReceiveTx(ReceiveTransaction receiveTx) throws WalletException;

    public abstract void setTxSpent(Sha256Hash txHash, int txIndex, boolean isSpent) throws WalletException;

    public abstract void setTxSafe(Sha256Hash txHash, int txIndex, boolean inSafe) throws WalletException;

    public abstract void setReceiveTxDelete(Sha256Hash txHash, int txIndex, boolean isDeleted) throws WalletException;

    public abstract List<ReceiveTransaction> getReceiveTxList() throws WalletException;

    public abstract void storeSendTx(SendTransaction sendTx) throws WalletException;

    public abstract void setSendTxDelete(Sha256Hash txHash, boolean isDeleted) throws WalletException;

    public abstract SendTransaction getSendTx(Sha256Hash txHash) throws WalletException;

    public abstract List<SendTransaction> getSendTxList() throws WalletException;

    public abstract int getTxDepth(Sha256Hash txHash) throws WalletException;

    public abstract void deleteTransactions(long rescanTime) throws WalletException;

    public abstract List<StoredHeader> getJunction(Sha256Hash chainHash)
                                throws BlockNotFoundException, WalletException;

    public abstract void setChainHead(List<StoredHeader> chainList) throws WalletException, VerificationException;

    public abstract void close();
}
