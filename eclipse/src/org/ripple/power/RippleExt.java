package org.ripple.power;

import java.math.BigInteger;

public class RippleExt {
	public static final char[] DEFAULT_ALPHABET = "rpshnaf39wBUDNEGHJKLM4PQRST7VWXYZ2bcdeCg65jkm8oFqi1tuvAxyz"
			.toCharArray();
	private static final BigInteger BASE = BigInteger.valueOf(58);

	public static String encodeRipple(byte[] input) {
		BigInteger bi = new BigInteger(1, input);
		StringBuilder s = new StringBuilder();
		while (bi.compareTo(BASE) >= 0) {
			BigInteger mod = bi.mod(BASE);
			s.insert(0, DEFAULT_ALPHABET[mod.intValue()]);
			bi = bi.subtract(mod).divide(BASE);
		}
		s.insert(0, DEFAULT_ALPHABET[bi.intValue()]);
		for (byte anInput : input) {
			if (anInput == 0) {
				s.insert(0, DEFAULT_ALPHABET[0]);
			} else {
				break;
			}
		}
		return s.toString();
	}

	public static byte[] decodeRipple(String input) {
		byte[] bytes = decodeToBigIntegerRipple(input).toByteArray();
		boolean stripSignByte = bytes.length > 1 && bytes[0] == 0
				&& bytes[1] < 0;
		int leadingZeros = 0;
		for (int i = 0; input.charAt(i) == DEFAULT_ALPHABET[0]; i++) {
			leadingZeros++;
		}
		byte[] tmp = new byte[bytes.length - (stripSignByte ? 1 : 0)
				+ leadingZeros];
		System.arraycopy(bytes, stripSignByte ? 1 : 0, tmp, leadingZeros,
				tmp.length - leadingZeros);
		return tmp;
	}

	private static int index(char[] strings, char id) {
		for (int i = 0; i < strings.length; i++) {
			if (strings[i] == id) {
				return i;
			}
		}
		return -1;
	}

	public static BigInteger decodeToBigIntegerRipple(String input) {
		BigInteger bi = BigInteger.valueOf(0);
		for (int i = input.length() - 1; i >= 0; i--) {
			int alphaIndex = index(DEFAULT_ALPHABET, input.charAt(i));
			if (alphaIndex == -1) {
				throw new RuntimeException("Illegal character "
						+ input.charAt(i) + " at " + i);
			}
			bi = bi.add(BigInteger.valueOf(alphaIndex).multiply(
					BASE.pow(input.length() - 1 - i)));
		}
		return bi;
	}
}
