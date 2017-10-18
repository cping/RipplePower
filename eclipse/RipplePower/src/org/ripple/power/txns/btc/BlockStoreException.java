package org.ripple.power.txns.btc;

public class BlockStoreException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** The block causing the exception */
	protected Sha256Hash blockHash;

	/**
	 * Creates a new exception with a detail message
	 *
	 * @param message
	 *            Detail message
	 */
	public BlockStoreException(String message) {
		super(message);
		blockHash = Sha256Hash.ZERO_HASH;
	}

	/**
	 * Creates a new exception with a detail message and a causing block
	 *
	 * @param message
	 *            Detail message
	 * @param blockHash
	 *            Block hash
	 */
	public BlockStoreException(String message, Sha256Hash blockHash) {
		super(message);
		this.blockHash = blockHash;
	}

	/**
	 * Creates a new exception with a detail message and cause
	 *
	 * @param message
	 *            Detail message
	 * @param t
	 *            Caught exception
	 */
	public BlockStoreException(String message, Throwable t) {
		super(message, t);
		blockHash = Sha256Hash.ZERO_HASH;
	}

	/**
	 * Creates a new exception with a detail message, causing block and causing
	 * exception
	 *
	 * @param message
	 *            Detail message
	 * @param blockHash
	 *            Block hash
	 * @param t
	 *            Caught exception
	 */
	public BlockStoreException(String message, Sha256Hash blockHash, Throwable t) {
		super(message, t);
		this.blockHash = blockHash;
	}

	/**
	 * Returns the block hash for the block causing the exception
	 *
	 * @return Block hash
	 */
	public Sha256Hash getHash() {
		return blockHash;
	}
}
