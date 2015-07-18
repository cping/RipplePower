package org.ripple.power.txns.btc;


public class WalletException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    private Sha256Hash hash = Sha256Hash.ZERO_HASH;

    public WalletException(String msg) {
        super(msg);
    }

    public WalletException(String msg, Sha256Hash hash) {
        super(msg);
        this.hash = hash;
    }

    public WalletException(String msg, Throwable t) {
        super(msg, t);
    }

    public WalletException(String msg, Sha256Hash hash, Throwable t) {
        super(msg, t);
        this.hash = hash;
    }

    public Sha256Hash getHash() {
        return hash;
    }
}
