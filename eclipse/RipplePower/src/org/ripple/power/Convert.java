package org.ripple.power;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Arrays;

public final class Convert {

	private static final char[] hexChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
			'f' };

	public static final BigInteger two64 = new BigInteger("18446744073709551616");

	private Convert() {
	} // never

	public static byte[] parseHexString(String hex) {
		if (hex == null) {
			return null;
		}
		byte[] bytes = new byte[hex.length() / 2];
		for (int i = 0; i < bytes.length; i++) {
			int char1 = hex.charAt(i * 2);
			char1 = char1 > 0x60 ? char1 - 0x57 : char1 - 0x30;
			int char2 = hex.charAt(i * 2 + 1);
			char2 = char2 > 0x60 ? char2 - 0x57 : char2 - 0x30;
			if (char1 < 0 || char2 < 0 || char1 > 15 || char2 > 15) {
				throw new NumberFormatException("Invalid hex number: " + hex);
			}
			bytes[i] = (byte) ((char1 << 4) + char2);
		}
		return bytes;
	}

	public static String toHexString(byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		char[] chars = new char[bytes.length * 2];
		for (int i = 0; i < bytes.length; i++) {
			chars[i * 2] = hexChars[((bytes[i] >> 4) & 0xF)];
			chars[i * 2 + 1] = hexChars[(bytes[i] & 0xF)];
		}
		return String.valueOf(chars);
	}

	public static String toUnsignedLong(long objectId) {
		if (objectId >= 0) {
			return String.valueOf(objectId);
		}
		BigInteger id = BigInteger.valueOf(objectId).add(two64);
		return id.toString();
	}

	public static String toUnsignedLong(Long objectId) {
		return toUnsignedLong(nullToZero(objectId));
	}

	public static Long parseUnsignedLong(String number) {
		if (number == null) {
			return null;
		}
		BigInteger bigInt = new BigInteger(number.trim());
		if (bigInt.signum() < 0 || bigInt.compareTo(two64) != -1) {
			throw new IllegalArgumentException("overflow: " + number);
		}
		return zeroToNull(bigInt.longValue());
	}

	public static long parseLong(Object o) {
		if (o == null) {
			return 0;
		} else if (o instanceof Long) {
			return ((Long) o);
		} else if (o instanceof String) {
			return Long.parseLong((String) o);
		} else {
			throw new IllegalArgumentException("Not a long: " + o);
		}
	}

	public static Long parseAccountId(String account) {
		if (account == null) {
			return null;
		}
		account = account.toUpperCase();
		if (account.startsWith("NXT-")) {
			return zeroToNull(Crypto.rsDecode(account.substring(4)));
		} else {
			return parseUnsignedLong(account);
		}
	}

	public static String rsAccount(Long accountId) {
		return "NXT-" + Crypto.rsEncode(nullToZero(accountId));
	}

	public static Long fullHashToId(byte[] hash) {
		if (hash == null || hash.length < 8) {
			throw new IllegalArgumentException("Invalid hash: " + Arrays.toString(hash));
		}
		BigInteger bigInteger = new BigInteger(1,
				new byte[] { hash[7], hash[6], hash[5], hash[4], hash[3], hash[2], hash[1], hash[0] });
		return bigInteger.longValue();
	}

	public static Long fullHashToId(String hash) {
		if (hash == null) {
			return null;
		}
		return fullHashToId(Convert.parseHexString(hash));
	}

	public static Long zeroToNull(long l) {
		return l == 0 ? null : l;
	}

	public static long nullToZero(Long l) {
		return l == null ? 0 : l;
	}

	public static int nullToZero(Integer i) {
		return i == null ? 0 : i;
	}

	public static String emptyToNull(String s) {
		return s == null || s.length() == 0 ? null : s;
	}

	public static String nullToEmpty(String s) {
		return s == null ? "" : s;
	}

	public static byte[] emptyToNull(byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		for (byte b : bytes) {
			if (b != 0) {
				return bytes;
			}
		}
		return null;
	}

	public static byte[] toBytes(String s) {
		try {
			return s.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e.toString(), e);
		}
	}

	public static String toString(byte[] bytes) {
		try {
			return new String(bytes, "UTF-8").trim().intern();
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e.toString(), e);
		}
	}

	public static String truncate(String s, String replaceNull, int limit, boolean dots) {
		return s == null ? replaceNull
				: s.length() > limit ? (s.substring(0, dots ? limit - 3 : limit) + (dots ? "..." : "")) : s;
	}

	// overflow checking based on
	// https://www.securecoding.cert.org/confluence/display/java/NUM00-J.+Detect+or+prevent+integer+overflow
	public static long safeAdd(long left, long right) throws ArithmeticException {
		if (right > 0 ? left > Long.MAX_VALUE - right : left < Long.MIN_VALUE - right) {
			throw new ArithmeticException("Integer overflow");
		}
		return left + right;
	}

	public static long safeSubtract(long left, long right) throws ArithmeticException {
		if (right > 0 ? left < Long.MIN_VALUE + right : left > Long.MAX_VALUE + right) {
			throw new ArithmeticException("Integer overflow");
		}
		return left - right;
	}

	public static long safeMultiply(long left, long right) throws ArithmeticException {
		if (right > 0 ? left > Long.MAX_VALUE / right || left < Long.MIN_VALUE / right
				: (right < -1 ? left > Long.MIN_VALUE / right || left < Long.MAX_VALUE / right
						: right == -1 && left == Long.MIN_VALUE)) {
			throw new ArithmeticException("Integer overflow");
		}
		return left * right;
	}

	public static long safeDivide(long left, long right) throws ArithmeticException {
		if ((left == Long.MIN_VALUE) && (right == -1)) {
			throw new ArithmeticException("Integer overflow");
		}
		return left / right;
	}

	public static long safeNegate(long a) throws ArithmeticException {
		if (a == Long.MIN_VALUE) {
			throw new ArithmeticException("Integer overflow");
		}
		return -a;
	}

	public static long safeAbs(long a) throws ArithmeticException {
		if (a == Long.MIN_VALUE) {
			throw new ArithmeticException("Integer overflow");
		}
		return Math.abs(a);
	}

}
