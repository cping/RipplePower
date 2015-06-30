package org.ripple.power.txns.btc;

public class KeyCrypterException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public KeyCrypterException(String s) {
        super(s);
    }

    public KeyCrypterException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
