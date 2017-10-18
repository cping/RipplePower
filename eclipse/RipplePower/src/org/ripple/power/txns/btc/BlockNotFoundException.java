package org.ripple.power.txns.btc;

public class BlockNotFoundException extends BlockStoreException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new exception with a detail message
	 *
	 * @param message
	 *            Detail message
	 */
	public BlockNotFoundException(String message) {
		super(message);
	}

	/**
	 * Creates a new exception with a detail message and a causing block
	 *
	 * @param message
	 *            Detail message
	 * @param blockHash
	 *            Block hash
	 */
	public BlockNotFoundException(String message, Sha256Hash blockHash) {
		super(message, blockHash);
	}

	/**
	 * Creates a new exception with a detail message and cause
	 *
	 * @param message
	 *            Detail message
	 * @param t
	 *            Caught exception
	 */
	public BlockNotFoundException(String message, Exception t) {
		super(message, t);
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
	public BlockNotFoundException(String message, Sha256Hash blockHash, Throwable t) {
		super(message, blockHash, t);
	}
}
