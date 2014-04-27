package org.address.ripple;


public class RippleAddress extends RippleIdentifier {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final RippleAddress RIPPLE_ROOT_ACCOUNT=new RippleAddress("rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh");
	public static final RippleAddress RIPPLE_ADDRESS_ZERO=new RippleAddress("rrrrrrrrrrrrrrrrrrrrrhoLvTp");
	public static final RippleAddress RIPPLE_ADDRESS_ONE=new RippleAddress("rrrrrrrrrrrrrrrrrrrrBZbvji");
	public static final RippleAddress RIPPLE_ADDRESS_NEUTRAL=RIPPLE_ADDRESS_ONE;
	public static final RippleAddress RIPPLE_ADDRESS_NAN=new RippleAddress("rrrrrrrrrrrrrrrrrrrn5RM1rHd");
	public static final RippleAddress RIPPLE_ADDRESS_BITSTAMP = new RippleAddress("rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B");
	public static final RippleAddress RIPPLE_ADDRESS_JRIPPLEAPI=new RippleAddress("r32fLio1qkmYqFFYkwdnsaVN7cxBwkW4cT");
	public static final RippleAddress RIPPLE_ADDRESS_PMARCHES=new RippleAddress("rEQQNvhuLt1KTYmDWmw12mPvmJD4KCtxmS");

	public RippleAddress(byte[] payloadBytes) {
		super(payloadBytes, 0);
	}

	public RippleAddress(String string) {
		super(string);
	}
}
