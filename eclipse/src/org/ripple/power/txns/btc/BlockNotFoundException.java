package org.ripple.power.txns.btc;


public class BlockNotFoundException extends WalletException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    public BlockNotFoundException(String message) {
        super(message);
    }

    public BlockNotFoundException(String message, Sha256Hash blockHash) {
        super(message, blockHash);
    }

    public BlockNotFoundException(String message, Exception t) {
        super(message, t);
    }

    public BlockNotFoundException(String message, Sha256Hash blockHash, Throwable t) {
        super(message, blockHash, t);
    }
}
