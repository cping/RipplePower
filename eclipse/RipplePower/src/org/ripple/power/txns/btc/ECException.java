package org.ripple.power.txns.btc;

/**
 * An ECException is thrown if an error occurs in an elliptic curve
 * cryptographic function
 */
public class ECException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new exception with a detail message
	 *
	 * @param msg
	 *            Detail message
	 */
	public ECException(String msg) {
		super(msg);
	}

	/**
	 * Creates a new exception with a detail message and cause
	 *
	 * @param msg
	 *            Detail message
	 * @param t
	 *            Caught exception
	 */
	public ECException(String msg, Throwable t) {
		super(msg, t);
	}
}
