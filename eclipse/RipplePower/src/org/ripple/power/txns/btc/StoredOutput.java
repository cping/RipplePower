package org.ripple.power.txns.btc;

import java.math.BigInteger;

public class StoredOutput extends TransactionOutput {

	/** Coinbase transaction */
	private final boolean isCoinBase;

	/** Output spent flag */
	private boolean isSpent;

	/** Height for block spending this output */
	private int blockHeight;

	/**
	 * Creates a new stored transaction output
	 *
	 * @param txIndex
	 *            Index within the transaction output list
	 * @param value
	 *            Output value expressed in 0.00000001 BTC units
	 * @param scriptBytes
	 *            Script bytes
	 * @param isCoinBase
	 *            TRUE if this is a coinbase transaction
	 */
	public StoredOutput(int txIndex, BigInteger value, byte[] scriptBytes, boolean isCoinBase) {
		super(txIndex, value, scriptBytes);
		this.isSpent = false;
		this.isCoinBase = isCoinBase;
	}

	/**
	 * Creates a new stored transaction output
	 *
	 * @param txIndex
	 *            Index within the transaction output list
	 * @param value
	 *            Output value expressed in 0.00000001 BTC units
	 * @param scriptBytes
	 *            Script bytes
	 * @param isCoinBase
	 *            TRUE if this is a coinbase transaction
	 * @param isSpent
	 *            TRUE if the output has been spent
	 * @param blockHeight
	 *            Chain height of block spending this output
	 */
	public StoredOutput(int txIndex, BigInteger value, byte[] scriptBytes, boolean isCoinBase, boolean isSpent,
			int blockHeight) {
		super(txIndex, value, scriptBytes);
		this.isCoinBase = isCoinBase;
		this.isSpent = isSpent;
		this.blockHeight = blockHeight;
	}

	/**
	 * Checks if this is a coinbase transaction
	 *
	 * @return TRUE if this is a coinbase transaction
	 */
	public boolean isCoinBase() {
		return isCoinBase;
	}

	/**
	 * Sets the transaction output spent indicator
	 *
	 * @param isSpent
	 *            TRUE if the transaction output has been spent
	 */
	public void setSpent(boolean isSpent) {
		this.isSpent = isSpent;
	}

	/**
	 * Checks if the transaction output has been spent
	 *
	 * @return TRUE if the output has been spent
	 */
	public boolean isSpent() {
		return isSpent;
	}

	/**
	 * Set the block height for the block spending this output
	 *
	 * @param blockHeight
	 *            Block height
	 */
	public void setHeight(int blockHeight) {
		this.blockHeight = blockHeight;
	}

	/**
	 * Returns the block height for the block spending this output. The return
	 * value will be zero if the block height is not available.
	 *
	 * @return Block height or zero
	 */
	public int getHeight() {
		return blockHeight;
	}
}
