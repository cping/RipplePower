package org.ripple.power.utils;

import java.util.Arrays;

import org.ripple.power.Helper;

public class Base58Coder {

	public static final String ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
	private static final int[] INDEXES = new int[128];
	static {
		for (int i = 0; i < INDEXES.length; i++) {
			INDEXES[i] = -1;
		}
		for (int i = 0; i < ALPHABET.length(); i++) {
			INDEXES[ALPHABET.charAt(i)] = i;
		}
	}

	public static boolean isValidChecksum(String address) {
		try {
			decodeChecked(address);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private static byte[] decode(String input) throws Exception {
		if (input.length() == 0) {
			return new byte[0];
		}
		byte[] input58 = new byte[input.length()];
		for (int i = 0; i < input.length(); ++i) {
			char c = input.charAt(i);
			int digit58 = -1;
			if (c >= 0 && c < 128) {
				digit58 = INDEXES[c];
			}
			if (digit58 < 0) {
				throw new Exception("Illegal character " + c + " at " + i);
			}
			input58[i] = (byte) digit58;
		}
		int zeroCount = 0;
		while (zeroCount < input58.length && input58[zeroCount] == 0) {
			++zeroCount;
		}
		byte[] temp = new byte[input.length()];
		int j = temp.length;
		int startAt = zeroCount;
		while (startAt < input58.length) {
			byte mod = divmod256(input58, startAt);
			if (input58[startAt] == 0) {
				++startAt;
			}
			temp[--j] = mod;
		}
		while (j < temp.length && temp[j] == 0) {
			++j;
		}
		return copyOfRange(temp, j - zeroCount, temp.length);
	}

	private static byte[] decodeChecked(String input) throws Exception {
		byte tmp[] = decode(input);
		if (tmp.length < 4){
			throw new Exception("Input too short");
		}
		byte[] bytes = copyOfRange(tmp, 0, tmp.length - 4);
		byte[] checksum = copyOfRange(tmp, tmp.length - 4, tmp.length);
		tmp = Helper.doubleDigest(bytes);
		byte[] hash = copyOfRange(tmp, 0, 4);
		if (!Arrays.equals(checksum, hash)){
			throw new Exception("Checksum does not validate");
		}
		return bytes;
	}

	private static byte divmod256(byte[] number58, int startAt) {
		int remainder = 0;
		for (int i = startAt; i < number58.length; i++) {
			int digit58 = (int) number58[i] & 0xFF;
			int temp = remainder * 58 + digit58;
			number58[i] = (byte) (temp / 256);
			remainder = temp % 256;
		}
		return (byte) remainder;
	}

	private static byte[] copyOfRange(byte[] source, int from, int to) {
		byte[] range = new byte[to - from];
		System.arraycopy(source, from, range, 0, range.length);
		return range;
	}

}
