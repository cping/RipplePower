package org.address.ripple;


public class RippleSeedAddress extends RippleIdentifier {
	private static final long serialVersionUID = 1845189349528742766L;

	public RippleSeedAddress(byte[] payloadBytes) {
		super(payloadBytes, 33);
	}
	
	public RippleSeedAddress(String stringID) {
		super(stringID);
	}

	public RipplePrivateKey getPrivateKey(int accountNumber) {
		RippleGenerator generator = new RippleGenerator(payloadBytes);
		RipplePrivateKey signingPrivateKey = generator.getAccountPrivateKey(accountNumber);
		return signingPrivateKey;
	}

	public RippleAddress getPublicRippleAddress() {
		return getPrivateKey(0).getPublicKey().getAddress();
	}
}