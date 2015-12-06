package org.ripple.power.txns.btc;

public class RequestException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** The RPC error code */
    private final int errorCode;

    /**
     * Creates a new exception
     *
     * @param       errorCode       Error code
     * @param       errorMessage    Error message
     */
    public RequestException(int errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
    }

    /**
     * Returns the error code for this exception
     *
     * @return                      Error code
     */
    public int getCode() {
        return errorCode;
    }
}
