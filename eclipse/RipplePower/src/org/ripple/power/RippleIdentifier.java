package org.ripple.power;

import java.io.Serializable;
import java.util.Arrays;

import org.ripple.power.utils.CollectionUtils;

public class RippleIdentifier implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String humanReadableIdentifier;
	byte[] payloadBytes;
	int identifierType;

	public RippleIdentifier(final byte[] payloadBytes, int identifierType) {
		this.payloadBytes = CollectionUtils.copyOf(payloadBytes);
		this.identifierType = identifierType;
	}

	public RippleIdentifier(String stringID) {
		this.humanReadableIdentifier = stringID;
		byte[] stridBytes = RippleExt.decodeRipple(stringID);
		byte[] checksumArray = Helper.doubleDigest(stridBytes, 0,
				stridBytes.length - 4);
		if (checksumArray[0] != stridBytes[stridBytes.length - 4]
				|| checksumArray[1] != stridBytes[stridBytes.length - 3]
				|| checksumArray[2] != stridBytes[stridBytes.length - 2]
				|| checksumArray[3] != stridBytes[stridBytes.length - 1]) {
			throw new RuntimeException("Checksum failed on identifier "
					+ stringID);
		}
		payloadBytes = Arrays.copyOfRange(stridBytes, 1, stridBytes.length - 4);
		identifierType = stridBytes[0];
	}

	@Override
	public String toString() {
		if (humanReadableIdentifier == null) {
			byte[] versionPayloadChecksumBytes = new byte[1 + payloadBytes.length + 4];
			versionPayloadChecksumBytes[0] = (byte) identifierType;
			System.arraycopy(payloadBytes, 0, versionPayloadChecksumBytes, 1,
					payloadBytes.length);
			byte[] hashBytes = Helper.doubleDigest(versionPayloadChecksumBytes,
					0, 1 + payloadBytes.length);
			System.arraycopy(hashBytes, 0, versionPayloadChecksumBytes,
					1 + payloadBytes.length, 4);
			humanReadableIdentifier = RippleExt
					.encodeRipple(versionPayloadChecksumBytes);
		}
		return humanReadableIdentifier;
	}

	public byte[] getBytes() {
		return payloadBytes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(payloadBytes);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (obj instanceof String)
			return (toString().equals(obj));
		if (getClass() != obj.getClass())
			return false;
		RippleIdentifier other = (RippleIdentifier) obj;
		if (!Arrays.equals(payloadBytes, other.payloadBytes))
			return false;
		return true;
	}

}
