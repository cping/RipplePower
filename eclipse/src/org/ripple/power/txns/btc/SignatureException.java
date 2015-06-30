package org.ripple.power.txns.btc;

/**
 * A SignatureException is thrown if a message signature is not valid
 */
public class SignatureException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Creates a new exception with a detail message
     *
     * @param       msg             Detail message
     */
    public SignatureException(String msg) {
        super(msg);
    }

    /**
     * Creates a new exception with a detail message and cause
     *
     * @param       msg             Detail message
     * @param       t               Caught exception
     */
    public SignatureException(String msg, Throwable t) {
        super(msg, t);
    }
}
