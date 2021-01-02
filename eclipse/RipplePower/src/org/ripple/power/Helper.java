package org.ripple.power;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;

import org.ripple.power.utils.ByteUtils;
import org.ripple.bouncycastle.crypto.Digest;
import org.ripple.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.ripple.bouncycastle.util.encoders.Hex;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class Helper {

	private static final Map<Class<?>, Boolean> _customEquals = new ConcurrentHashMap<Class<?>, Boolean>();
	private static final Map<Class<?>, Boolean> _customHash = new ConcurrentHashMap<Class<?>, Boolean>();
	private static final Map<Class<?>, Collection<Field>> _reflectedFields = new ConcurrentHashMap<Class<?>, Collection<Field>>();

	private static final int MAX_ENTRIES = 100;

	private static MessageDigest sha256digest;

	private static SecureRandom random = new SecureRandom();

	private static BigInteger _1000_ = new BigInteger("1000");

	static {
		try {
			sha256digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	private static final int KEY_SIZE_BYTES = 32;
	private static final String SHA_256 = "SHA-256";
	private static final String UTF8 = "UTF-8";
	private static final String STATIC_SALT = "{'[R^*&843HGihp3p5l3e%g!o@t@o!mono$ ^f442Axs092aBGJZawW ]\"}";

	/** Constant -1 */
	public static final BigInteger NEGATIVE_ONE = BigInteger.valueOf(-1);

	/** Constant 1,000 */
	private static final BigInteger DISPLAY_1K = new BigInteger("1000");

	/** Constant 1,000,000 */
	private static final BigInteger DISPLAY_1M = new BigInteger("1000000");

	/** Constant 1,000,000,000 */
	private static final BigInteger DISPLAY_1G = new BigInteger("1000000000");

	/** Constant 1,000,000,000,000 */
	private static final BigInteger DISPLAY_1T = new BigInteger("1000000000000");

	/** Constant 1,000,000,000,000,000 */
	private static final BigInteger DISPLAY_1P = new BigInteger("1000000000000000");

	/** Bit masks (Low-order bit is bit 0 and high-order bit is bit 7) */
	private static final int bitMask[] = { 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80 };

	/** Instance of a SHA-256 digest which we will use as needed */
	private static MessageDigest digest;


	public static final BigInteger COIN = new BigInteger("100000000", 10);

	public static final BigInteger CENT = new BigInteger("1000000", 10);

	public static byte[] singleDigest(byte[] input) {
		return singleDigest(input, 0, input.length);
	}

	public static byte[] singleDigest(byte[] input, int offset, int length) {
		byte[] bytes;
		synchronized (digest) {
			digest.reset();
			digest.update(input, offset, length);
			bytes = digest.digest();
		}
		return bytes;
	}

	public static byte[] singleDigest(List<byte[]> inputList) {
		byte[] bytes;
		synchronized (digest) {
			digest.reset();
			for (byte[] input : inputList) {
				digest.update(input, 0, input.length);
			}
			bytes = digest.digest();
		}
		return bytes;
	}

	public static byte[] doubleDigest(List<byte[]> inputList) {
		byte[] bytes;
		synchronized (digest) {
			digest.reset();
			for (byte[] input : inputList) {
				digest.update(input, 0, input.length);
			}
			byte[] first = digest.digest();
			bytes = digest.digest(first);
		}
		return bytes;
	}

	public static byte[] doubleDigestTwoBuffers(byte[] input1, int offset1, int length1, byte[] input2, int offset2,
			int length2) {
		byte[] bytes;
		synchronized (digest) {
			digest.reset();
			digest.update(input1, offset1, length1);
			digest.update(input2, offset2, length2);
			byte[] first = digest.digest();
			bytes = digest.digest(first);
		}
		return bytes;
	}

	public static byte[] sha1Hash(byte[] input) {
		byte[] out;
		try {
			MessageDigest sDigest = MessageDigest.getInstance("SHA-1");
			out = sDigest.digest(input);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		return out;
	}

	public static byte[] hash160(byte[] input) {
		byte[] out = new byte[20];
		RIPEMD160Digest rDigest = new RIPEMD160Digest();
		rDigest.update(input, 0, input.length);
		rDigest.doFinal(out, 0);
		return out;
	}

	public static byte[] sha256Hash160(byte[] input) {
		byte[] out = new byte[20];
		synchronized (digest) {
			digest.reset();
			byte[] sha256 = digest.digest(input);
			RIPEMD160Digest rDigest = new RIPEMD160Digest();
			rDigest.update(sha256, 0, sha256.length);
			rDigest.doFinal(out, 0);
		}
		return out;
	}

	public static String bytesToHexString(byte[] bytes) {
		StringBuilder buf = new StringBuilder(bytes.length * 2);
		for (byte b : bytes) {
			String s = Integer.toString(0xFF & b, 16);
			if (s.length() < 2)
				buf.append('0');
			buf.append(s);
		}
		return buf.toString();
	}

	public static byte[] bigIntegerToBytes(BigInteger bigInteger, int numBytes) {
		if (bigInteger == null)
			return null;
		byte[] bigBytes = bigInteger.toByteArray();
		byte[] bytes = new byte[numBytes];
		int start = (bigBytes.length == numBytes + 1) ? 1 : 0;
		int length = Math.min(bigBytes.length, numBytes);
		System.arraycopy(bigBytes, start, bytes, numBytes - length, length);
		return bytes;
	}

	public static String numberToShortString(BigInteger number) {
		int scale;
		String suffix;
		BigDecimal work;
		if (number.compareTo(DISPLAY_1P) >= 0) {
			scale = 15;
			suffix = "P";
		} else if (number.compareTo(DISPLAY_1T) >= 0) {
			scale = 12;
			suffix = "T";
		} else if (number.compareTo(DISPLAY_1G) >= 0) {
			scale = 9;
			suffix = "G";
		} else if (number.compareTo(DISPLAY_1M) >= 0) {
			scale = 6;
			suffix = "M";
		} else if (number.compareTo(DISPLAY_1K) >= 0) {
			scale = 3;
			suffix = "K";
		} else {
			scale = 0;
			suffix = "";
		}
		if (scale != 0)
			work = new BigDecimal(number, scale);
		else
			work = new BigDecimal(number);

		return String.format("%3.3f%s", work.floatValue(), suffix);
	}

	public static boolean checkBitLE(byte[] data, int index) {
		return (data[index >>> 3] & bitMask[7 & index]) != 0;
	}

	public static void setBitLE(byte[] data, int index) {
		data[index >>> 3] |= bitMask[7 & index];
	}

	public static BigInteger decodeCompactBits(long compact) {
		int size = ((int) (compact >> 24)) & 0xFF;
		byte[] bytes = new byte[4 + size];
		bytes[3] = (byte) size;
		if (size >= 1)
			bytes[4] = (byte) ((compact >> 16) & 0xFF);
		if (size >= 2)
			bytes[5] = (byte) ((compact >> 8) & 0xFF);
		if (size >= 3)
			bytes[6] = (byte) (compact & 0xFF);
		return decodeMPI(bytes, true);
	}

	public static BigInteger decodeMPI(byte[] mpi, boolean hasLength) {
		byte[] buf;
		if (hasLength) {
			int length = (int) readUint32BE(mpi, 0);
			buf = new byte[length];
			System.arraycopy(mpi, 4, buf, 0, length);
		} else {
			buf = mpi;
		}
		if (buf.length == 0)
			return BigInteger.ZERO;
		boolean isNegative = (buf[0] & 0x80) == 0x80;
		if (isNegative)
			buf[0] &= 0x7f;
		BigInteger result = new BigInteger(buf);
		return isNegative ? result.negate() : result;
	}

	public static byte[] encodeMPI(BigInteger value, boolean includeLength) {
		byte[] bytes;
		if (value.equals(BigInteger.ZERO)) {
			if (!includeLength)
				bytes = new byte[] {};
			else
				bytes = new byte[] { 0x00, 0x00, 0x00, 0x00 };
		} else {
			boolean isNegative = value.signum() < 0;
			if (isNegative)
				value = value.negate();
			byte[] array = value.toByteArray();
			int length = array.length;
			if ((array[0] & 0x80) == 0x80)
				length++;
			if (includeLength) {
				bytes = new byte[length + 4];
				System.arraycopy(array, 0, bytes, length - array.length + 3, array.length);
				uint32ToByteArrayBE(length, bytes, 0);
				if (isNegative)
					bytes[4] |= 0x80;
			} else {
				if (length != array.length) {
					bytes = new byte[length];
					System.arraycopy(array, 0, bytes, 1, array.length);
				} else {
					bytes = array;
				}
				if (isNegative)
					bytes[0] |= 0x80;
			}
		}
		return bytes;
	}

	public static byte[] reverseBytes(byte[] bytes) {
		byte[] buf = new byte[bytes.length];
		for (int i = 0; i < bytes.length; i++)
			buf[i] = bytes[bytes.length - 1 - i];
		return buf;
	}

	public static byte[] reverseBytes(byte[] bytes, int offset, int length) {
		byte[] buf = new byte[length];
		for (int i = 0; i < length; i++)
			buf[i] = bytes[offset + length - 1 - i];
		return buf;
	}

	public static byte[] reverseDwordBytes(byte[] bytes, int trimLength) {
		byte[] rev = new byte[trimLength >= 0 && bytes.length > trimLength ? trimLength : bytes.length];
		for (int i = 0; i < rev.length; i += 4) {
			System.arraycopy(bytes, i, rev, i, 4);
			for (int j = 0; j < 4; j++) {
				rev[i + j] = bytes[i + 3 - j];
			}
		}
		return rev;
	}

	public static long readUint32LE(byte[] bytes, int offset) {
		return ((long) bytes[offset++] & 0x00FFL) | (((long) bytes[offset++] & 0x00FFL) << 8)
				| (((long) bytes[offset++] & 0x00FFL) << 16) | (((long) bytes[offset] & 0x00FFL) << 24);
	}

	public static long readUint32BE(byte[] bytes, int offset) {
		return (((long) bytes[offset++] & 0x00FFL) << 24) | (((long) bytes[offset++] & 0x00FFL) << 16)
				| (((long) bytes[offset++] & 0x00FFL) << 8) | ((long) bytes[offset] & 0x00FFL);
	}

	public static void uint32ToByteArrayLE(long val, byte[] out, int offset) {
		out[offset++] = (byte) val;
		out[offset++] = (byte) (val >> 8);
		out[offset++] = (byte) (val >> 16);
		out[offset] = (byte) (val >> 24);
	}

	public static void uint32ToByteArrayBE(long val, byte[] out, int offset) {
		out[offset++] = (byte) (val >> 24);
		out[offset++] = (byte) (val >> 16);
		out[offset++] = (byte) (val >> 8);
		out[offset] = (byte) val;
	}

	public static long readUint64LE(byte[] bytes, int offset) {
		return ((long) bytes[offset++] & 0x00FFL) | (((long) bytes[offset++] & 0x00FFL) << 8)
				| (((long) bytes[offset++] & 0x00FFL) << 16) | (((long) bytes[offset++] & 0x00FFL) << 24)
				| (((long) bytes[offset++] & 0x00FFL) << 32) | (((long) bytes[offset++] & 0x00FFL) << 40)
				| (((long) bytes[offset++] & 0x00FFL) << 48) | (((long) bytes[offset] & 0x00FFL) << 56);
	}

	public static void uint64ToByteArrayLE(long val, byte[] out, int offset) {
		out[offset++] = (byte) val;
		out[offset++] = (byte) (val >> 8);
		out[offset++] = (byte) (val >> 16);
		out[offset++] = (byte) (val >> 24);
		out[offset++] = (byte) (val >> 32);
		out[offset++] = (byte) (val >> 40);
		out[offset++] = (byte) (val >> 48);
		out[offset] = (byte) (val >> 56);
	}

	private static String mix(String str) {
		String result = str;
		String holder;
		int iters = str.length() / 4;
		for (int i = 0; i < iters; i++) {
			holder = mixStep(result);
			result = holder;
		}
		return result;
	}

	private static String mixStep(String str) {
		if (str == null || str.isEmpty()) {
			return "";
		}
		if (str.length() == 1) {
			return str;
		}
		if (str.length() == 2) {
			StringBuilder sb = new StringBuilder(str);
			return sb.reverse().toString();
		}
		StringBuilder sb = new StringBuilder();
		String char1 = String.valueOf(str.charAt(0));
		String char2 = String.valueOf(str.charAt(1));
		String char3 = String.valueOf(str.charAt(2));
		if ((char1.compareTo(char2) > 0) && (char1.compareTo(char3) < 0)) {
			return sb.append(mixStep(str.substring(2))).append(str.charAt(1)).append(str.charAt(0)).toString();
		} else if ((char1.compareTo(char2) > 0) && (char1.compareTo(char3) > 0)) {
			String mixReverse = (new StringBuilder(mixStep(str.substring(2)))).reverse().toString();
			return sb.append(str.charAt(1)).append(mixReverse).append(str.charAt(0)).toString();
		} else if ((char1.compareTo(char2) < 0) && (char1.compareTo(char3) > 0)) {
			return sb.append(str.charAt(0)).append(mixStep(str.substring(2))).append(str.charAt(1)).toString();
		} else if ((char1.compareTo(char2) < 0) && (char1.compareTo(char3) < 0)) {
			String mixReverse = (new StringBuilder(mixStep(str.substring(2)))).reverse().toString();
			return sb.append(str.charAt(0)).append(mixReverse).append(str.charAt(1)).toString();
		}
		return sb.append(str.charAt(1)).append(str.charAt(0)).append(mixStep(str.substring(2))).toString();
	}

	private static String deriveLongerString(String str) {
		StringBuilder sb = new StringBuilder(str);
		StringBuilder builder = new StringBuilder();
		builder.append(sb.toString().toLowerCase());
		builder.append(sb.toString().toUpperCase());
		StringBuilder result = new StringBuilder();
		result.append(sb);
		result.append(mix(builder.toString()));
		result.append(mix(builder.reverse().toString()));
		return result.toString();
	}

	public static byte[] generateRandom256() throws NoSuchAlgorithmException, InterruptedException {
		byte[] randomSeed1 = ByteUtils.longToBytes(System.nanoTime());
		byte[] randomSeed2 = (new SecureRandom()).generateSeed(KEY_SIZE_BYTES);
		byte[] bh1 = ByteUtils.concatenate(randomSeed1, randomSeed2);
		Thread.sleep(100L);
		byte[] randomSeed3 = UUID.randomUUID().toString().getBytes();
		byte[] randomSeed4 = ByteUtils.longToBytes(System.nanoTime());
		byte[] bh2 = ByteUtils.concatenate(randomSeed3, randomSeed4);
		return simpleHash256(ByteUtils.concatenate(bh1, bh2));
	}

	public static byte[] simpleHash256(byte[] msg) throws NoSuchAlgorithmException {
		MessageDigest sha256 = MessageDigest.getInstance(SHA_256);
		byte[] byteHolder1, byteHolder2;
		byteHolder1 = sha256.digest(msg);
		for (int i = 0; i < 100; i++) {
			byteHolder2 = sha256.digest(byteHolder1);
			byteHolder1 = sha256.digest(byteHolder2);
		}
		return byteHolder1;
	}

	public static byte[] hash256(String stringToMangle, String salt, int iterations)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest sha256 = MessageDigest.getInstance(SHA_256);
		StringBuilder sb = new StringBuilder();
		sb.append(deriveLongerString(stringToMangle));
		sb.append(STATIC_SALT);
		sb.append(deriveLongerString(salt));
		byte[] rawInput = sb.toString().getBytes(UTF8);
		byte[] byteHolder1 = rawInput;
		byte[] byteHolder2;
		byte[] byteHolder3;
		int numWalls = 101;
		int wallThickness = 7;
		int wallInterval = iterations / numWalls;
		int minimumIterations = 10007;
		for (int i = 0; i < Math.max(minimumIterations, iterations); i++) {
			byteHolder2 = sha256.digest(byteHolder1);
			if ((i % wallInterval) < wallThickness) {
				if ((i % 2) == 0) {
					byteHolder3 = sha256.digest(ByteUtils.concatenate(byteHolder2, rawInput));
				} else {
					byteHolder3 = sha256.digest(ByteUtils.concatenate(rawInput, byteHolder2));
				}
				byteHolder1 = sha256.digest(ByteUtils.concatenate(byteHolder2, byteHolder3));
			} else {
				byteHolder1 = sha256.digest(byteHolder2);
			}
		}
		return byteHolder1;
	}

	public static byte[] hash512(String stringToMangle, String salt, int iterations)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		byte[] hash256a = hash256(stringToMangle, salt, (iterations + 3) / 2);
		byte[] hash256b = hash256(ByteUtils.toHexString(hash256a), stringToMangle, (iterations + 1) / 2);
		return ByteUtils.concatenate(hash256a, hash256b);
	}

	public static byte[] sha256(byte[] input) {
		MessageDigest sha256digest;
			try {
				sha256digest = MessageDigest.getInstance("SHA-256");
			} catch (NoSuchAlgorithmException e) {
				throw new RuntimeException(e);
			}
		
		return sha256digest.digest(input);
	}

	public static byte[] ripemd160(byte[] message) {
		Digest digest = new RIPEMD160Digest();
		if (message != null) {
			byte[] resBuf = new byte[digest.getDigestSize()];
			digest.update(message, 0, message.length);
			digest.doFinal(resBuf, 0);
			return resBuf;
		}
		throw new NullPointerException("Can't hash a null");
	}

	public static byte[] doubleDigest(byte[] input) {
		return doubleDigest(input, 0, input.length);
	}

	public static byte[] doubleDigest(byte[] input, int offset, int length) {
		synchronized (sha256digest) {
			sha256digest.reset();
			sha256digest.update(input, offset, length);
			byte[] first = sha256digest.digest();
			return sha256digest.digest(first);
		}
	}

	public static byte[] randomPeerId() {
		byte[] peerIdBytes = new BigInteger(512, getRandom()).toByteArray();
		String peerId = null;
		if (peerIdBytes.length > 64) {
			peerId = Hex.toHexString(peerIdBytes, 1, 64);
		} else {
			peerId = Hex.toHexString(peerIdBytes);
		}
		return Hex.decode(peerId);
	}

	public static byte[] quarter(byte[] bytes) {
		byte[] hash = new byte[16];
		System.arraycopy(bytes, 0, hash, 0, 16);
		return hash;
	}

	public static byte[] quarterSha512(byte[] bytes) {
		byte[] hash = new byte[16];
		System.arraycopy(sha512(bytes), 0, hash, 0, 16);
		return hash;
	}
	public static String MODE = "SHA-512";

	public static byte[] halfSHA512(byte[] bytesToHash) {
		try {
			MessageDigest sha512Digest = MessageDigest.getInstance(MODE);
			byte[] bytesHash = sha512Digest.digest(bytesToHash);
			byte[] first256BitsOfHash = copyOf(bytesHash, 32);
			return first256BitsOfHash;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	

	public static byte[] sha512(byte[] byteArrays) {
		MessageDigest messageDigest;
		try {
			messageDigest = MessageDigest.getInstance(MODE);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		messageDigest.update(byteArrays);
		return messageDigest.digest();
	}

	public static byte[] update(byte[] input) {
		synchronized (sha256digest) {
			sha256digest.reset();
			sha256digest.update(input);
			return sha256digest.digest();
		}
	}

	public static boolean areEqual(boolean[] a, boolean[] b) {
		if (a == b) {
			return true;
		}

		if (a == null || b == null) {
			return false;
		}

		if (a.length != b.length) {
			return false;
		}

		for (int i = 0; i != a.length; i++) {
			if (a[i] != b[i]) {
				return false;
			}
		}

		return true;
	}

	public static boolean areEqual(char[] a, char[] b) {
		if (a == b) {
			return true;
		}

		if (a == null || b == null) {
			return false;
		}

		if (a.length != b.length) {
			return false;
		}

		for (int i = 0; i != a.length; i++) {
			if (a[i] != b[i]) {
				return false;
			}
		}

		return true;
	}

	public static boolean areEqual(byte[] a, byte[] b) {
		if (a == b) {
			return true;
		}

		if (a == null || b == null) {
			return false;
		}

		if (a.length != b.length) {
			return false;
		}

		for (int i = 0; i != a.length; i++) {
			if (a[i] != b[i]) {
				return false;
			}
		}

		return true;
	}

	public static boolean constantTimeAreEqual(byte[] a, byte[] b) {
		if (a == b) {
			return true;
		}

		if (a == null || b == null) {
			return false;
		}

		if (a.length != b.length) {
			return false;
		}

		int nonEqual = 0;

		for (int i = 0; i != a.length; i++) {
			nonEqual |= (a[i] ^ b[i]);
		}

		return nonEqual == 0;
	}

	public static boolean areEqual(int[] a, int[] b) {
		if (a == b) {
			return true;
		}

		if (a == null || b == null) {
			return false;
		}

		if (a.length != b.length) {
			return false;
		}

		for (int i = 0; i != a.length; i++) {
			if (a[i] != b[i]) {
				return false;
			}
		}

		return true;
	}

	public static boolean areEqual(long[] a, long[] b) {
		if (a == b) {
			return true;
		}

		if (a == null || b == null) {
			return false;
		}

		if (a.length != b.length) {
			return false;
		}

		for (int i = 0; i != a.length; i++) {
			if (a[i] != b[i]) {
				return false;
			}
		}

		return true;
	}

	public static boolean areEqual(BigInteger[] a, BigInteger[] b) {
		if (a == b) {
			return true;
		}

		if (a == null || b == null) {
			return false;
		}

		if (a.length != b.length) {
			return false;
		}

		for (int i = 0; i != a.length; i++) {
			if (!a[i].equals(b[i])) {
				return false;
			}
		}

		return true;
	}

	public static void fill(byte[] array, byte value) {
		for (int i = 0; i < array.length; i++) {
			array[i] = value;
		}
	}

	public static void fill(char[] array, char value) {
		for (int i = 0; i < array.length; i++) {
			array[i] = value;
		}
	}

	public static void fill(long[] array, long value) {
		for (int i = 0; i < array.length; i++) {
			array[i] = value;
		}
	}

	public static void fill(short[] array, short value) {
		for (int i = 0; i < array.length; i++) {
			array[i] = value;
		}
	}

	public static void fill(int[] array, int value) {
		for (int i = 0; i < array.length; i++) {
			array[i] = value;
		}
	}

	public static int hashCode(byte[] data) {
		if (data == null) {
			return 0;
		}

		int i = data.length;
		int hc = i + 1;

		while (--i >= 0) {
			hc *= 257;
			hc ^= data[i];
		}

		return hc;
	}

	public static int hashCode(char[] data) {
		if (data == null) {
			return 0;
		}

		int i = data.length;
		int hc = i + 1;

		while (--i >= 0) {
			hc *= 257;
			hc ^= data[i];
		}

		return hc;
	}

	public static int hashCode(int[][] ints) {
		int hc = 0;

		for (int i = 0; i != ints.length; i++) {
			hc = hc * 257 + hashCode(ints[i]);
		}

		return hc;
	}

	public static int hashCode(int[] data) {
		if (data == null) {
			return 0;
		}

		int i = data.length;
		int hc = i + 1;

		while (--i >= 0) {
			hc *= 257;
			hc ^= data[i];
		}

		return hc;
	}

	public static int hashCode(short[][][] shorts) {
		int hc = 0;

		for (int i = 0; i != shorts.length; i++) {
			hc = hc * 257 + hashCode(shorts[i]);
		}

		return hc;
	}

	public static int hashCode(short[][] shorts) {
		int hc = 0;

		for (int i = 0; i != shorts.length; i++) {
			hc = hc * 257 + hashCode(shorts[i]);
		}

		return hc;
	}

	public static int hashCode(short[] data) {
		if (data == null) {
			return 0;
		}

		int i = data.length;
		int hc = i + 1;

		while (--i >= 0) {
			hc *= 257;
			hc ^= (data[i] & 0xff);
		}

		return hc;
	}

	public static int hashCode(BigInteger[] data) {
		if (data == null) {
			return 0;
		}

		int i = data.length;
		int hc = i + 1;

		while (--i >= 0) {
			hc *= 257;
			hc ^= data[i].hashCode();
		}

		return hc;
	}

	public static byte[] clone(byte[] data) {
		if (data == null) {
			return null;
		}
		byte[] copy = new byte[data.length];

		System.arraycopy(data, 0, copy, 0, data.length);

		return copy;
	}

	public static byte[][] clone(byte[][] data) {
		if (data == null) {
			return null;
		}

		byte[][] copy = new byte[data.length][];

		for (int i = 0; i != copy.length; i++) {
			copy[i] = clone(data[i]);
		}

		return copy;
	}

	public static byte[][][] clone(byte[][][] data) {
		if (data == null) {
			return null;
		}

		byte[][][] copy = new byte[data.length][][];

		for (int i = 0; i != copy.length; i++) {
			copy[i] = clone(data[i]);
		}

		return copy;
	}

	public static int[] clone(int[] data) {
		if (data == null) {
			return null;
		}
		int[] copy = new int[data.length];

		System.arraycopy(data, 0, copy, 0, data.length);

		return copy;
	}

	public static short[] clone(short[] data) {
		if (data == null) {
			return null;
		}
		short[] copy = new short[data.length];

		System.arraycopy(data, 0, copy, 0, data.length);

		return copy;
	}

	public static BigInteger[] clone(BigInteger[] data) {
		if (data == null) {
			return null;
		}
		BigInteger[] copy = new BigInteger[data.length];

		System.arraycopy(data, 0, copy, 0, data.length);

		return copy;
	}

	public static byte[] copyOf(byte[] data, int newLength) {
		byte[] tmp = new byte[newLength];

		if (newLength < data.length) {
			System.arraycopy(data, 0, tmp, 0, newLength);
		} else {
			System.arraycopy(data, 0, tmp, 0, data.length);
		}

		return tmp;
	}

	public static char[] copyOf(char[] data, int newLength) {
		char[] tmp = new char[newLength];

		if (newLength < data.length) {
			System.arraycopy(data, 0, tmp, 0, newLength);
		} else {
			System.arraycopy(data, 0, tmp, 0, data.length);
		}

		return tmp;
	}

	public static int[] copyOf(int[] data, int newLength) {
		int[] tmp = new int[newLength];

		if (newLength < data.length) {
			System.arraycopy(data, 0, tmp, 0, newLength);
		} else {
			System.arraycopy(data, 0, tmp, 0, data.length);
		}

		return tmp;
	}

	public static long[] copyOf(long[] data, int newLength) {
		long[] tmp = new long[newLength];

		if (newLength < data.length) {
			System.arraycopy(data, 0, tmp, 0, newLength);
		} else {
			System.arraycopy(data, 0, tmp, 0, data.length);
		}

		return tmp;
	}

	public static BigInteger[] copyOf(BigInteger[] data, int newLength) {
		BigInteger[] tmp = new BigInteger[newLength];

		if (newLength < data.length) {
			System.arraycopy(data, 0, tmp, 0, newLength);
		} else {
			System.arraycopy(data, 0, tmp, 0, data.length);
		}

		return tmp;
	}

	public static byte[] copyOfRange(byte[] data, int from, int to) {
		int newLength = getLength(from, to);

		byte[] tmp = new byte[newLength];

		if (data.length - from < newLength) {
			System.arraycopy(data, from, tmp, 0, data.length - from);
		} else {
			System.arraycopy(data, from, tmp, 0, newLength);
		}

		return tmp;
	}

	public static int[] copyOfRange(int[] data, int from, int to) {
		int newLength = getLength(from, to);

		int[] tmp = new int[newLength];

		if (data.length - from < newLength) {
			System.arraycopy(data, from, tmp, 0, data.length - from);
		} else {
			System.arraycopy(data, from, tmp, 0, newLength);
		}

		return tmp;
	}

	public static long[] copyOfRange(long[] data, int from, int to) {
		int newLength = getLength(from, to);

		long[] tmp = new long[newLength];

		if (data.length - from < newLength) {
			System.arraycopy(data, from, tmp, 0, data.length - from);
		} else {
			System.arraycopy(data, from, tmp, 0, newLength);
		}

		return tmp;
	}

	public static BigInteger[] copyOfRange(BigInteger[] data, int from, int to) {
		int newLength = getLength(from, to);

		BigInteger[] tmp = new BigInteger[newLength];

		if (data.length - from < newLength) {
			System.arraycopy(data, from, tmp, 0, data.length - from);
		} else {
			System.arraycopy(data, from, tmp, 0, newLength);
		}

		return tmp;
	}

	private static class DualKey {
		private final Object _key1;
		private final Object _key2;

		private DualKey(Object k1, Object k2) {
			_key1 = k1;
			_key2 = k2;
		}

		public boolean equals(Object other) {
			if (other == null) {
				return false;
			}

			if (!(other instanceof DualKey)) {
				return false;
			}

			DualKey that = (DualKey) other;
			return _key1 == that._key1 && _key2 == that._key2;
		}

		public int hashCode() {
			int h1 = _key1 != null ? _key1.hashCode() : 0;
			int h2 = _key2 != null ? _key2.hashCode() : 0;
			return h1 + h2;
		}
	}

	public static boolean deepEquals(Object a, Object b) {
		Set<DualKey> visited = new HashSet<DualKey>();
		LinkedList<DualKey> stack = new LinkedList<DualKey>();
		stack.addFirst(new DualKey(a, b));
		while (!stack.isEmpty()) {
			DualKey dualKey = stack.removeFirst();
			visited.add(dualKey);

			if (dualKey._key1 == dualKey._key2) {
				continue;
			}

			if (dualKey._key1 == null || dualKey._key2 == null) {
				return false;
			}

			if (!dualKey._key1.getClass().equals(dualKey._key2.getClass())) {
				return false;
			}

			if (dualKey._key1.getClass().isArray()) {
				if (!compareArrays(dualKey._key1, dualKey._key2, stack, visited)) {
					return false;
				}
				continue;
			}

			if (dualKey._key1 instanceof SortedSet) {
				if (!compareOrderedCollection((Collection) dualKey._key1, (Collection) dualKey._key2, stack, visited)) {
					return false;
				}
				continue;
			}

			if (dualKey._key1 instanceof Set) {
				if (!compareUnorderedCollection((Collection) dualKey._key1, (Collection) dualKey._key2, stack,
						visited)) {
					return false;
				}
				continue;
			}

			if (dualKey._key1 instanceof Collection) {
				if (!compareOrderedCollection((Collection) dualKey._key1, (Collection) dualKey._key2, stack, visited)) {
					return false;
				}
				continue;
			}

			if (dualKey._key1 instanceof SortedMap) {
				if (!compareSortedMap((SortedMap) dualKey._key1, (SortedMap) dualKey._key2, stack, visited)) {
					return false;
				}
				continue;
			}

			if (dualKey._key1 instanceof Map) {
				if (!compareUnorderedMap((Map) dualKey._key1, (Map) dualKey._key2, stack, visited)) {
					return false;
				}
				continue;
			}

			if (hasCustomEquals(dualKey._key1.getClass())) {
				if (!dualKey._key1.equals(dualKey._key2)) {
					return false;
				}
				continue;
			}

			Collection<Field> fields = getDeepDeclaredFields(dualKey._key1.getClass());

			for (Field field : fields) {
				try {
					DualKey dk = new DualKey(field.get(dualKey._key1), field.get(dualKey._key2));
					if (!visited.contains(dk)) {
						stack.addFirst(dk);
					}
				} catch (Exception ignored) {
				}
			}
		}

		return true;
	}

	private static boolean compareArrays(Object array1, Object array2, LinkedList<DualKey> stack,
			Set<DualKey> visited) {
		int len = Array.getLength(array1);
		if (len != Array.getLength(array2)) {
			return false;
		}
		for (int i = 0; i < len; i++) {
			DualKey dk = new DualKey(Array.get(array1, i), Array.get(array2, i));
			if (!visited.contains(dk)) {
				stack.addFirst(dk);
			}
		}
		return true;
	}

	private static boolean compareOrderedCollection(Collection col1, Collection col2, LinkedList stack, Set visited) {

		if (col1.size() != col2.size()) {
			return false;
		}

		Iterator i1 = col1.iterator();
		Iterator i2 = col2.iterator();

		while (i1.hasNext()) {
			DualKey dk = new DualKey(i1.next(), i2.next());
			if (!visited.contains(dk)) {
				stack.addFirst(dk);
			}
		}
		return true;
	}

	private static boolean compareUnorderedCollection(Collection col1, Collection col2, LinkedList stack, Set visited) {
		if (col1.size() != col2.size()) {
			return false;
		}

		Map fastLookup = new HashMap();
		for (Object o : col2) {
			fastLookup.put(deepHashCode(o), o);
		}

		for (Object o : col1) {
			Object other = fastLookup.get(deepHashCode(o));
			if (other == null) {
				return false;
			}

			DualKey dk = new DualKey(o, other);
			if (!visited.contains(dk)) {
				stack.addFirst(dk);
			}
		}
		return true;
	}

	private static boolean compareSortedMap(SortedMap map1, SortedMap map2, LinkedList stack, Set visited) {
		if (map1.size() != map2.size()) {
			return false;
		}

		Iterator i1 = map1.entrySet().iterator();
		Iterator i2 = map2.entrySet().iterator();

		while (i1.hasNext()) {
			Map.Entry entry1 = (Map.Entry) i1.next();
			Map.Entry entry2 = (Map.Entry) i2.next();

			DualKey dk = new DualKey(entry1.getKey(), entry2.getKey());
			if (!visited.contains(dk)) {
				stack.addFirst(dk);
			}

			dk = new DualKey(entry1.getValue(), entry2.getValue());
			if (!visited.contains(dk)) {
				stack.addFirst(dk);
			}
		}
		return true;
	}

	private static boolean compareUnorderedMap(Map map1, Map map2, LinkedList stack, Set visited) {
		if (map1.size() != map2.size()) {
			return false;
		}

		Map fastLookup = new HashMap();

		for (Map.Entry entry : (Set<Map.Entry>) map2.entrySet()) {
			fastLookup.put(deepHashCode(entry.getKey()), entry);
		}

		for (Map.Entry entry : (Set<Map.Entry>) map1.entrySet()) {
			Map.Entry other = (Map.Entry) fastLookup.get(deepHashCode(entry.getKey()));
			if (other == null) {
				return false;
			}

			DualKey dk = new DualKey(entry.getKey(), other.getKey());
			if (!visited.contains(dk)) {
				stack.addFirst(dk);
			}

			dk = new DualKey(entry.getValue(), other.getValue());
			if (!visited.contains(dk)) {
				stack.addFirst(dk);
			}
		}

		return true;
	}

	public static boolean hasCustomEquals(Class c) {
		Class origClass = c;
		if (_customEquals.containsKey(c)) {
			return _customEquals.get(c);
		}

		while (!Object.class.equals(c)) {
			try {
				c.getDeclaredMethod("equals", Object.class);
				_customEquals.put(origClass, true);
				return true;
			} catch (Exception ignored) {
			}
			c = c.getSuperclass();
		}
		_customEquals.put(origClass, false);
		return false;
	}

	public static int deepHashCode(Object obj) {
		Set visited = new HashSet();
		LinkedList<Object> stack = new LinkedList<Object>();
		stack.addFirst(obj);
		int hash = 0;

		while (!stack.isEmpty()) {
			obj = stack.removeFirst();
			if (obj == null || visited.contains(obj)) {
				continue;
			}

			visited.add(obj);

			if (obj.getClass().isArray()) {
				int len = Array.getLength(obj);
				for (int i = 0; i < len; i++) {
					stack.addFirst(Array.get(obj, i));
				}
				continue;
			}

			if (obj instanceof Collection) {
				stack.addAll(0, (Collection) obj);
				continue;
			}

			if (obj instanceof Map) {
				stack.addAll(0, ((Map) obj).keySet());
				stack.addAll(0, ((Map) obj).values());
				continue;
			}

			if (hasCustomHashCode(obj.getClass())) {
				hash += obj.hashCode();
				continue;
			}

			Collection<Field> fields = getDeepDeclaredFields(obj.getClass());
			for (Field field : fields) {
				try {
					stack.addFirst(field.get(obj));
				} catch (Exception ignored) {
				}
			}
		}
		return hash;
	}

	public static boolean hasCustomHashCode(Class<?> c) {
		Class<?> origClass = c;
		if (_customHash.containsKey(c)) {
			return _customHash.get(c);
		}
		while (!Object.class.equals(c)) {
			try {
				c.getDeclaredMethod("hashCode");
				_customHash.put(origClass, true);
				return true;
			} catch (Exception ignored) {
			}
			c = c.getSuperclass();
		}
		_customHash.put(origClass, false);
		return false;
	}

	public static Collection<Field> getDeepDeclaredFields(Class<?> c) {
		if (_reflectedFields.containsKey(c)) {
			return _reflectedFields.get(c);
		}
		Collection<Field> fields = new ArrayList<Field>();
		Class<?> curr = c;

		while (curr != null) {
			try {
				Field[] local = curr.getDeclaredFields();

				for (Field field : local) {
					if (!field.isAccessible()) {
						try {
							field.setAccessible(true);
						} catch (Exception ignored) {
						}
					}

					int modifiers = field.getModifiers();
					if (!Modifier.isStatic(modifiers) && !field.getName().startsWith("this$")
							&& !Modifier.isTransient(modifiers)) {
						fields.add(field);
					}
				}
			} catch (ThreadDeath t) {
				throw t;
			} catch (Throwable ignored) {
			}

			curr = curr.getSuperclass();
		}
		_reflectedFields.put(c, fields);
		return fields;
	}

	private static int getLength(int from, int to) {
		int newLength = to - from;
		if (newLength < 0) {
			StringBuffer sb = new StringBuffer(from);
			sb.append(" > ").append(to);
			throw new IllegalArgumentException(sb.toString());
		}
		return newLength;
	}

	public static byte[] concatenate(byte[] a, byte[] b) {
		if (a != null && b != null) {
			byte[] rv = new byte[a.length + b.length];

			System.arraycopy(a, 0, rv, 0, a.length);
			System.arraycopy(b, 0, rv, a.length, b.length);

			return rv;
		} else if (b != null) {
			return clone(b);
		} else {
			return clone(a);
		}
	}

	public static byte[] concatenate(byte[] a, byte[] b, byte[] c) {
		if (a != null && b != null && c != null) {
			byte[] rv = new byte[a.length + b.length + c.length];

			System.arraycopy(a, 0, rv, 0, a.length);
			System.arraycopy(b, 0, rv, a.length, b.length);
			System.arraycopy(c, 0, rv, a.length + b.length, c.length);

			return rv;
		} else if (b == null) {
			return concatenate(a, c);
		} else {
			return concatenate(a, b);
		}
	}

	public static byte[] concatenate(byte[] a, byte[] b, byte[] c, byte[] d) {
		if (a != null && b != null && c != null && d != null) {
			byte[] rv = new byte[a.length + b.length + c.length + d.length];

			System.arraycopy(a, 0, rv, 0, a.length);
			System.arraycopy(b, 0, rv, a.length, b.length);
			System.arraycopy(c, 0, rv, a.length + b.length, c.length);
			System.arraycopy(d, 0, rv, a.length + b.length + c.length, d.length);

			return rv;
		} else if (d == null) {
			return concatenate(a, b, c);
		} else if (c == null) {
			return concatenate(a, b, d);
		} else if (b == null) {
			return concatenate(a, c, d);
		} else {
			return concatenate(b, c, d);
		}
	}

	public static String hexStringToDecimalString(String hexNum) {
		boolean match = Pattern.matches("0[xX][0-9a-fA-F]+", hexNum);
		if (!match) {
			throw new Error("The string doesn't conains hex num in form 0x.. : [" + hexNum + "]");
		}
		byte[] numberBytes = Hex.decode(hexNum.substring(2));
		return (new BigInteger(1, numberBytes)).toString();
	}

	public static String longToDateTime(long timestamp) {
		Date date = new Date(timestamp * 1000);
		DateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		return formatter.format(date);
	}

	public static ImageIcon getImageIcon(String resource) {
		URL imageURL = ClassLoader.getSystemResource(resource);
		ImageIcon image = new ImageIcon(imageURL);
		return image;
	}

	public static String getValueShortString(BigInteger number) {
		BigInteger result = number;
		int pow = 0;
		while (result.compareTo(_1000_) == 1 || result.compareTo(_1000_) == 0) {
			result = result.divide(_1000_);
			pow += 3;
		}
		return result.toString() + "·(" + "10^" + pow + ")";
	}

	public static SecureRandom getRandom() {
		return random;
	}

	public static StringBuffer getHashlistShort(List<byte[]> blockHashes) {
		StringBuffer sb = new StringBuffer();
		if (blockHashes.isEmpty()) {
			return sb.append("[]");
		}
		String firstHash = Hex.toHexString(blockHashes.get(0));
		String lastHash = Hex.toHexString(blockHashes.get(blockHashes.size() - 1));
		return sb.append(" ").append(firstHash).append("...").append(lastHash);
	}
}
