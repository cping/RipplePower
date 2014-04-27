package org.address.ripple;

public class RipplePublicGeneratorAddress extends RippleIdentifier {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RipplePublicGeneratorAddress(byte[] payloadBytes) {
		super(payloadBytes, 41);
	}

}
