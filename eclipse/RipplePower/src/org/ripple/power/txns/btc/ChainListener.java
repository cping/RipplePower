package org.ripple.power.txns.btc;

public interface ChainListener {

	/**
	 * Notifies the listener when a new block is stored in the database
	 *
	 * @param storedBlock
	 *            The stored block
	 */
	public void blockStored(StoredBlock storedBlock);

	/**
	 * Notifies the listener when the block status changes
	 *
	 * @param storedBlock
	 *            The stored block
	 */
	public void blockUpdated(StoredBlock storedBlock);

	/**
	 * Notifies the listener when the chain head is updated
	 */
	public void chainUpdated();
}
