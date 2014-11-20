package org.ripple.power.database;

public class Reductor {

	private String charset;
	private int maxPwLength;
	private int minPwLength;

	public Reductor(String charset, int minPwLength, int maxPwLength) {
		this.charset = charset;
		this.maxPwLength = maxPwLength;
		this.minPwLength = minPwLength;
	}

	public Reductor(String charset) {
		this(charset, 0, 64);
	}

	public byte[] reduce(byte[] hash, int functionNr) {
		byte pwLength = (byte) ((functionNr) % (maxPwLength - minPwLength + 1) + minPwLength);
		return reduce(hash, functionNr, pwLength);
	}

	public byte[] reduce(byte[] hash, int functionNr, byte pwLength) {
		byte[] result = new byte[pwLength];
		for (int i = 0; i < pwLength; i++) {
			hash[i] ^= functionNr;
			result[i] = (byte) (Math.abs(hash[i]) % charset.length());
			hash[i] ^= functionNr;
		}
		return result;
	}
}
