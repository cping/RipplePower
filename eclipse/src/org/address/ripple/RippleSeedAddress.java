package org.address.ripple;

import java.util.HashMap;

public class RippleSeedAddress extends RippleIdentifier {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private HashMap<Integer, RipplePrivateKey> _cache = new HashMap<Integer, RipplePrivateKey>(
			10);

	public RippleSeedAddress(final byte[] payloadBytes) {
		super(payloadBytes, 33);
	}

	public RippleSeedAddress(final String stringID) {
		super(stringID);
	}

	public String getPublicKey() {
		return getPublicRippleAddress().toString();
	}

	public String getPrivateKey() {
		return toString();
	}

	public RipplePrivateKey getPrivateKey(int accountNumber) {
		RipplePrivateKey signingPrivateKey = _cache.get(accountNumber);
		if (signingPrivateKey == null) {
			RippleGenerator generator = new RippleGenerator(payloadBytes);
			signingPrivateKey = generator.getAccountPrivateKey(accountNumber);
			_cache.put(accountNumber, signingPrivateKey);
		}
		return signingPrivateKey;
	}

	public RippleAddress getPublicRippleAddress() {
		return getPrivateKey(0).getPublicKey().getAddress();
	}
}