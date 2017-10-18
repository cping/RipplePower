package org.ripple.power.txns.btc;

public interface BlockStoreListener {

	public void addChainBlock(StoredHeader blockHeader);

	public void txUpdated();

	public void rescanCompleted();
}
