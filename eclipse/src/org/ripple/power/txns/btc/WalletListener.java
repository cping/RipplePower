package org.ripple.power.txns.btc;

public interface WalletListener {

    public void addChainBlock(StoredHeader blockHeader);

    public void txUpdated();

    public void rescanCompleted();
}