package org.ripple.power.txns.btc;

/**
 * This exception is thrown when an error is detected while processing a transaction script
 */
public class ScriptException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Creates a new exception with a detail message
     *
     * @param       message         Detail message
     */
    public ScriptException(String message) {
        super(message);
    }

    /**
     * Creates a new exception with a detail message and cause
     *
     * @param       message         Detail message
     * @param       t               Caught exception
     */
    public ScriptException(String message, Throwable t) {
        super(message, t);
    }
}
