package org.address.ripple;

import org.address.NativeSupport;
import org.spongycastle.math.ec.ECPoint;

public class RipplePublicKey extends RippleIdentifier {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RipplePublicKey(byte[] publicKeyBytes) {
		super(publicKeyBytes, 35);
		if (publicKeyBytes.length != 33) {
			throw new RuntimeException(
					"The public key must be of length 33 bytes was of length "
							+ publicKeyBytes.length);
		}
	}

	private RippleAddress _address;

	public RippleAddress getAddress() {
		if (_address == null) {
			_address = new RippleAddress(
					NativeSupport.toRipemd160Sha256(payloadBytes));
		}
		return _address;
	}

	public ECPoint getPublicPoint() {
		return RippleGenerator.SECP256K1_PARAMS.getCurve().decodePoint(
				payloadBytes);
	}
}
