package org.ripple.power.txns.btc;

/**
 * AddressFormatException is thrown if an invalid Bitcoin address is detected.  An
 * address is invalid if it is not 20-bytes, has an incorrect version, or the
 * checksum is not valid.
 */
public class AddressFormatException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Creates a new exception with a detail message
     *
     * @param       msg             Detail message
     */
    public AddressFormatException(String msg) {
        super(msg);
    }

    /**
     * Creates a new exception with a detail message and cause
     *
     * @param       msg             Detail message
     * @param       t               Caught exception
     */
    public AddressFormatException(String msg, Throwable t) {
        super(msg, t);
    }
}
