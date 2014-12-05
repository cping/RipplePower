package org.ripple.power.utils;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;

import org.spongycastle.util.encoders.Hex;

public class ByteUtils {

	public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

	private ByteUtils() {
	}

	public static byte[] reverse(final byte[] x) {
		int i, j;
		final int n;
		if ((n = x.length) > 0) {
			final byte[] ret = new byte[n];
			for (i = 0, j = n - 1; j >= 0;)
				ret[i++] = x[j--];
			return ret;
		}
		return x;
	}

	public static short leb2short(final byte[] x, final int offset) {
		return (short) ((x[offset] & 0xFF) | (x[offset + 1] << 8));
	}

	public static short beb2short(final byte[] x, final int offset) {
		return (short) ((x[offset] << 8) | (x[offset + 1] & 0xFF));
	}

	public static short leb2short(final InputStream is) throws IOException {
		return (short) ((readByte(is) & 0xFF) | (readByte(is) << 8));
	}

	public static short beb2short(final InputStream is) throws IOException {
		return (short) ((readByte(is) << 8) | (readByte(is) & 0xFF));
	}

	public static int leb2int(final byte[] x, final int offset) {
		return (x[offset] & 0xFF) | ((x[offset + 1] & 0xFF) << 8)
				| ((x[offset + 2] & 0xFF) << 16) | (x[offset + 3] << 24);
	}

	public static int beb2int(final byte[] x, final int offset) {
		return (x[offset] << 24) | ((x[offset + 1] & 0xFF) << 16)
				| ((x[offset + 2] & 0xFF) << 8) | (x[offset + 3] & 0xFF);
	}

	public static int leb2int(final InputStream is) throws IOException {
		return (readByte(is) & 0xFF) | ((readByte(is) & 0xFF) << 8)
				| ((readByte(is) & 0xFF) << 16) | (readByte(is) << 24);
	}

	public static int beb2int(final InputStream is) throws IOException {
		return (readByte(is) << 24) | ((readByte(is) & 0xFF) << 16)
				| ((readByte(is) & 0xFF) << 8) | (readByte(is) & 0xFF);
	}

	public static int leb2int(final byte[] x, final int offset, final int n)
			throws IndexOutOfBoundsException, IllegalArgumentException {
		switch (n) {
		case 1:
			return x[offset] & 0xFF;
		case 2:
			return (x[offset] & 0xFF) | ((x[offset + 1] & 0xFF) << 8);
		case 3:
			return (x[offset] & 0xFF) | ((x[offset + 1] & 0xFF) << 8)
					| ((x[offset + 2] & 0xFF) << 16);
		case 4:
			return (x[offset] & 0xFF) | ((x[offset + 1] & 0xFF) << 8)
					| ((x[offset + 2] & 0xFF) << 16) | (x[offset + 3] << 24);
		default:
			throw new IllegalArgumentException("No bytes specified");
		}
	}

	public static long leb2long(final byte[] x, final int offset, final int n)
			throws IndexOutOfBoundsException, IllegalArgumentException {
		switch (n) {
		case 1:
			return x[offset] & 0xFFL;
		case 2:
			return (x[offset] & 0xFFL) | ((x[offset + 1] & 0xFFL) << 8);
		case 3:
			return (x[offset] & 0xFFL) | ((x[offset + 1] & 0xFFL) << 8)
					| ((x[offset + 2] & 0xFFL) << 16);
		case 4:
			return (x[offset] & 0xFFL) | ((x[offset + 1] & 0xFFL) << 8)
					| ((x[offset + 2] & 0xFFL) << 16)
					| ((x[offset + 3] & 0xFFL) << 24);
		case 5:
			return (x[offset] & 0xFFL) | ((x[offset + 1] & 0xFFL) << 8)
					| ((x[offset + 2] & 0xFFL) << 16)
					| ((x[offset + 3] & 0xFFL) << 24)
					| ((x[offset + 4] & 0xFFL) << 32);
		case 6:
			return (x[offset] & 0xFFL) | ((x[offset + 1] & 0xFFL) << 8)
					| ((x[offset + 2] & 0xFFL) << 16)
					| ((x[offset + 3] & 0xFFL) << 24)
					| ((x[offset + 4] & 0xFFL) << 32)
					| ((x[offset + 5] & 0xFFL) << 40);
		case 7:
			return (x[offset] & 0xFFL) | ((x[offset + 1] & 0xFFL) << 8)
					| ((x[offset + 2] & 0xFFL) << 16)
					| ((x[offset + 3] & 0xFFL) << 24)
					| ((x[offset + 4] & 0xFFL) << 32)
					| ((x[offset + 5] & 0xFFL) << 40)
					| ((x[offset + 6] & 0xFFL) << 48);
		case 8:
			return (x[offset] & 0xFFL) | ((x[offset + 1] & 0xFFL) << 8)
					| ((x[offset + 2] & 0xFFL) << 16)
					| ((x[offset + 3] & 0xFFL) << 24)
					| ((x[offset + 4] & 0xFFL) << 32)
					| ((x[offset + 5] & 0xFFL) << 40)
					| ((x[offset + 6] & 0xFFL) << 48)
					| ((long) x[offset + 7] << 56);
		default:
			throw new IllegalArgumentException("No bytes specified");
		}
	}

	public static long leb2long(InputStream is) throws IOException {
		return (readByte(is) & 0xFFL) | ((readByte(is) & 0xFFL) << 8)
				| ((readByte(is) & 0xFFL) << 16)
				| ((readByte(is) & 0xFFL) << 24)
				| ((readByte(is) & 0xFFL) << 32)
				| ((readByte(is) & 0xFFL) << 40)
				| ((readByte(is) & 0xFFL) << 48) | (readByte(is) << 56);
	}

	public static int beb2int(final byte[] x, final int offset, final int n)
			throws IndexOutOfBoundsException, IllegalArgumentException {
		switch (n) {
		case 1:
			return x[offset] & 0xFF;
		case 2:
			return ((x[offset] & 0xFF) << 8) | (x[offset + 1] & 0xFF);
		case 3:
			return ((x[offset] & 0xFF) << 16) | ((x[offset + 1] & 0xFF) << 8)
					| (x[offset + 2] & 0xFF);
		case 4:
			return (x[offset] << 24) | ((x[offset + 1] & 0xFF) << 16)
					| ((x[offset + 2] & 0xFF) << 8) | (x[offset + 3] & 0xFF);
		default:
			throw new IllegalArgumentException("No bytes specified");
		}
	}

	public static void short2leb(final short x, final byte[] buf,
			final int offset) {
		buf[offset] = (byte) x;
		buf[offset + 1] = (byte) (x >> 8);
	}

	public static void short2beb(final short x, final byte[] buf,
			final int offset) {
		buf[offset] = (byte) (x >> 8);
		buf[offset + 1] = (byte) x;
	}

	public static void short2leb(final short x, final OutputStream os)
			throws IOException {
		os.write((byte) x);
		os.write((byte) (x >> 8));
	}

	public static void short2beb(final short x, final OutputStream os)
			throws IOException {
		os.write((byte) (x >> 8));
		os.write((byte) x);
	}

	public static void int2leb(final int x, final byte[] buf, final int offset) {
		buf[offset] = (byte) x;
		buf[offset + 1] = (byte) (x >> 8);
		buf[offset + 2] = (byte) (x >> 16);
		buf[offset + 3] = (byte) (x >> 24);
	}

	public static void long2beb(final long x, final byte[] buf, final int offset) {
		buf[offset] = (byte) (x >> 56);
		buf[offset + 1] = (byte) (x >> 48);
		buf[offset + 2] = (byte) (x >> 40);
		buf[offset + 3] = (byte) (x >> 32);
		buf[offset + 4] = (byte) (x >> 24);
		buf[offset + 5] = (byte) (x >> 16);
		buf[offset + 6] = (byte) (x >> 8);
		buf[offset + 7] = (byte) x;
	}

	public static void long2leb(final long x, final byte[] buf, final int offset) {
		buf[offset] = (byte) x;
		buf[offset + 1] = (byte) (x >> 8);
		buf[offset + 2] = (byte) (x >> 16);
		buf[offset + 3] = (byte) (x >> 24);
		buf[offset + 4] = (byte) (x >> 32);
		buf[offset + 5] = (byte) (x >> 40);
		buf[offset + 6] = (byte) (x >> 48);
		buf[offset + 7] = (byte) (x >> 56);
	}

	public static byte[] long2bytes(long i, int byteCount) {
		byte[] b = new byte[8];
		b[7] = (byte) (i);
		i >>>= 8;
		b[6] = (byte) (i);
		i >>>= 8;
		b[5] = (byte) (i);
		i >>>= 8;
		b[4] = (byte) (i);
		i >>>= 8;
		b[3] = (byte) (i);
		i >>>= 8;
		b[2] = (byte) (i);
		i >>>= 8;
		b[1] = (byte) (i);
		i >>>= 8;
		b[0] = (byte) (i);

		byte[] bytes = new byte[byteCount];
		System.arraycopy(b, 8 - byteCount, bytes, 0, byteCount);
		return bytes;
	}

	public static void int2beb(final int x, final byte[] buf, final int offset) {
		buf[offset] = (byte) (x >> 24);
		buf[offset + 1] = (byte) (x >> 16);
		buf[offset + 2] = (byte) (x >> 8);
		buf[offset + 3] = (byte) x;
	}

	public static void int2beb(final int x, OutputStream out, final int n)
			throws IOException {
		switch (n) {
		case 1:
			out.write((byte) x);
			break;
		case 2:
			out.write((byte) (x >> 8));
			out.write((byte) x);
			break;
		case 3:
			out.write((byte) (x >> 16));
			out.write((byte) (x >> 8));
			out.write((byte) x);
			break;
		case 4:
			out.write((byte) (x >> 24));
			out.write((byte) (x >> 16));
			out.write((byte) (x >> 8));
			out.write((byte) x);
			break;
		default:
			throw new IllegalArgumentException("invalid n: " + n);
		}
	}

	public static void int2leb(final int x, final OutputStream os)
			throws IOException {
		os.write((byte) x);
		os.write((byte) (x >> 8));
		os.write((byte) (x >> 16));
		os.write((byte) (x >> 24));
	}

	public static void int2beb(final int x, final OutputStream os)
			throws IOException {
		os.write((byte) (x >> 24));
		os.write((byte) (x >> 16));
		os.write((byte) (x >> 8));
		os.write((byte) x);
	}

	public static byte[] int2minLeb(final int x)
			throws IllegalArgumentException {
		if (x <= 0xFFFF) {
			if (x <= 0xFF) {
				if (x < 0)
					throw new IllegalArgumentException();
				return new byte[] { (byte) x };
			}
			return new byte[] { (byte) x, (byte) (x >> 8) };
		}
		if (x <= 0xFFFFFF)
			return new byte[] { (byte) x, (byte) (x >> 8), (byte) (x >> 16) };
		return new byte[] { (byte) x, (byte) (x >> 8), (byte) (x >> 16),
				(byte) (x >> 24) };
	}

	public static byte[] long2minLeb(final long x)
			throws IllegalArgumentException {
		if (x <= 0xFFFFFFFFFFFFFFL) {
			if (x <= 0xFFFFFFFFFFFFL) {
				if (x <= 0xFFFFFFFFFFL) {
					if (x <= 0xFFFFFFFFL) {
						if (x <= 0xFFFFFFL) {
							if (x <= 0xFFFFL) {
								if (x <= 0xFFL) {
									if (x < 0)
										throw new IllegalArgumentException();
									return new byte[] { (byte) x };
								}
								return new byte[] { (byte) x, (byte) (x >> 8) };
							}
							return new byte[] { (byte) x, (byte) (x >> 8),
									(byte) (x >> 16) };
						}
						return new byte[] { (byte) x, (byte) (x >> 8),
								(byte) (x >> 16), (byte) (x >> 24) };
					}
					return new byte[] { (byte) x, (byte) (x >> 8),
							(byte) (x >> 16), (byte) (x >> 24),
							(byte) (x >> 32) };
				}
				return new byte[] { (byte) x, (byte) (x >> 8),
						(byte) (x >> 16), (byte) (x >> 24), (byte) (x >> 32),
						(byte) (x >> 40) };
			}
			return new byte[] { (byte) x, (byte) (x >> 8), (byte) (x >> 16),
					(byte) (x >> 24), (byte) (x >> 32), (byte) (x >> 40),
					(byte) (x >> 48) };
		}

		return new byte[] { (byte) x, (byte) (x >> 8), (byte) (x >> 16),
				(byte) (x >> 24), (byte) (x >> 32), (byte) (x >> 40),
				(byte) (x >> 48), (byte) (x >> 56) };
	}

	public static byte[] int2minBeb(final int x)
			throws IllegalArgumentException {
		if (x <= 0xFFFF) {
			if (x <= 0xFF) {
				if (x < 0)
					throw new IllegalArgumentException();
				return new byte[] { (byte) x };
			}
			return new byte[] { (byte) (x >> 8), (byte) x };
		}
		if (x <= 0xFFFFFF)
			return new byte[] { (byte) (x >> 16), (byte) (x >> 8), (byte) x };
		return new byte[] { (byte) (x >> 24), (byte) (x >> 16),
				(byte) (x >> 8), (byte) x };
	}

	public static int ubyte2int(final byte x) {
		return x & 0xFF;
	}

	public static int ushort2int(final short x) {
		return x & 0xFFFF;
	}

	public static long uint2long(final int x) {
		return x & 0xFFFFFFFFL;
	}

	public static int long2int(final long l) {
		int m;
		if (l < (m = Integer.MAX_VALUE) && l > (m = Integer.MIN_VALUE))
			return (int) l;
		return m;
	}

	public static long beb2long(final byte[] x, final int offset, final int n)
			throws IndexOutOfBoundsException, IllegalArgumentException {
		switch (n) {
		case 1:
			return x[offset] & 0xFFL;
		case 2:
			return (x[offset + 1] & 0xFFL) | ((x[offset] & 0xFFL) << 8);
		case 3:
			return (x[offset + 2] & 0xFFL) | ((x[offset + 1] & 0xFFL) << 8)
					| ((x[offset] & 0xFFL) << 16);
		case 4:
			return (x[offset + 3] & 0xFFL) | ((x[offset + 2] & 0xFFL) << 8)
					| ((x[offset + 1] & 0xFFL) << 16)
					| ((x[offset] & 0xFFL) << 24);
		case 5:
			return (x[offset + 4] & 0xFFL) | ((x[offset + 3] & 0xFFL) << 8)
					| ((x[offset + 2] & 0xFFL) << 16)
					| ((x[offset + 1] & 0xFFL) << 24)
					| ((x[offset] & 0xFFL) << 32);
		case 6:
			return (x[offset + 5] & 0xFFL) | ((x[offset + 4] & 0xFFL) << 8)
					| ((x[offset + 3] & 0xFFL) << 16)
					| ((x[offset + 2] & 0xFFL) << 24)
					| ((x[offset + 1] & 0xFFL) << 32)
					| ((x[offset] & 0xFFL) << 40);
		case 7:
			return (x[offset + 6] & 0xFFL) | ((x[offset + 5] & 0xFFL) << 8)
					| ((x[offset + 4] & 0xFFL) << 16)
					| ((x[offset + 3] & 0xFFL) << 24)
					| ((x[offset + 2] & 0xFFL) << 32)
					| ((x[offset + 1] & 0xFFL) << 40)
					| ((x[offset] & 0xFFL) << 48);
		case 8:
			return (x[offset + 7] & 0xFFL) | ((x[offset + 6] & 0xFFL) << 8)
					| ((x[offset + 5] & 0xFFL) << 16)
					| ((x[offset + 4] & 0xFFL) << 24)
					| ((x[offset + 3] & 0xFFL) << 32)
					| ((x[offset + 2] & 0xFFL) << 40)
					| ((x[offset + 1] & 0xFFL) << 48)
					| ((x[offset] & 0xFFL) << 56);
		default:
			throw new IllegalArgumentException("No bytes specified");
		}
	}

	private static int readByte(InputStream is) throws IOException {
		int ret = is.read();
		if (ret == -1) {
			throw new EOFException();
		}
		return ret;
	}

	public static byte[] smallIntToByteArray(int v) {
		if (v > 65535) {
			throw new IllegalArgumentException("value is too big");
		}

		return new byte[] { (byte) ((v >>> 8) & 0xFF),
				(byte) ((v >>> 0) & 0xFF) };
	}

	public static int byteArrayToSmallInt(byte[] arr, int offset) {
		if (arr == null || arr.length - offset < 2) {
			throw new IllegalArgumentException("Invalid arguments");
		}
		return ((arr[offset] & 0xFF) << 8) + (arr[1 + offset] & 0xFF);
	}

	public static byte[] smallIntToTripleByteArray(int v) {
		if (v > 16777215) {
			throw new IllegalArgumentException("value is too big");
		}
		return new byte[] { (byte) ((v >>> 16) & 0xFF),
				(byte) ((v >>> 8) & 0xFF), (byte) ((v >>> 0) & 0xFF) };
	}

	public static int tripleByteArrayToSmallInt(byte[] arr, int offset) {
		if (arr == null || arr.length - offset < 2) {
			throw new IllegalArgumentException("Invalid arguments");
		}
		return ((arr[offset] & 0xFF) << 16) + ((arr[1 + offset] & 0xFF) << 8)
				+ (arr[2 + offset] & 0xFF);
	}

	public static byte[] getByteArrayChecksum(byte[] arr) {
		if (arr.length > 65535) {
			throw new IllegalArgumentException("Byte array is too long");
		}
		int result = 0;
		int step = arr.length > 100 ? 16 : 1;

		for (int i = 0; i < arr.length; i += step) {
			result += (arr[i] & 0xFF);
		}

		return smallIntToTripleByteArray(result);
	}

	public static byte[] appendByteArrays(byte[] a, byte[] b) {
		byte[] c = new byte[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);

		return c;
	}

	public static byte[] appendByte(byte[] bytes, byte b) {
		byte[] result = Arrays.copyOf(bytes, bytes.length + 1);
		result[result.length - 1] = b;
		return result;
	}

	public static byte[] uuidToByteArray(UUID uuid) {
		long msb = uuid.getMostSignificantBits();
		long lsb = uuid.getLeastSignificantBits();
		byte[] buffer = new byte[16];

		for (int i = 0; i < 8; i++) {
			buffer[i] = (byte) (msb >>> 8 * (7 - i));
		}
		for (int i = 8; i < 16; i++) {
			buffer[i] = (byte) (lsb >>> 8 * (7 - i));
		}

		return buffer;
	}

	public static byte[] bigIntegerToBytes(BigInteger b, int numBytes) {
		if (b == null)
			return null;
		byte[] bytes = new byte[numBytes];
		byte[] biBytes = b.toByteArray();
		int start = (biBytes.length == numBytes + 1) ? 1 : 0;
		int length = Math.min(biBytes.length, numBytes);
		System.arraycopy(biBytes, start, bytes, numBytes - length, length);
		return bytes;
	}

	public static byte[] bigIntegerToBytes(BigInteger value) {
		if (value == null)
			return null;

		byte[] data = value.toByteArray();

		if (data.length != 1 && data[0] == 0) {
			byte[] tmp = new byte[data.length - 1];
			System.arraycopy(data, 1, tmp, 0, tmp.length);
			data = tmp;
		}
		return data;
	}

	public static int matchingNibbleLength(byte[] a, byte[] b) {
		int i = 0;
		int length = a.length < b.length ? a.length : b.length;
		while (i < length) {
			if (a[i] != b[i])
				break;
			i++;
		}
		return i;
	}

	public static byte[] longToBytes(long val) {
		return ByteBuffer.allocate(8).putLong(val).array();
	}

	public static String toHexString(byte[] data) {
		return data == null ? "" : Hex.toHexString(data);
	}

	public static byte[] calcPacketLength(byte[] msg) {
		int msgLen = msg.length;
		byte[] len = { (byte) ((msgLen >> 24) & 0xFF),
				(byte) ((msgLen >> 16) & 0xFF), (byte) ((msgLen >> 8) & 0xFF),
				(byte) ((msgLen) & 0xFF) };
		return len;
	}

	public static int byteArrayToInt(byte[] b) {
		if (b == null || b.length == 0) {
			return 0;
		}
		return new BigInteger(1, b).intValue();
	}

	public static String nibblesToPrettyString(byte[] nibbles) {
		StringBuffer buffer = new StringBuffer();
		for (byte nibble : nibbles) {
			String nibleString = oneByteToHexString(nibble);
			buffer.append("\\x" + nibleString);
		}
		return buffer.toString();
	}

	public static String oneByteToHexString(byte value) {
		String retVal = Integer.toString(value & 0xFF, 16);
		if (retVal.length() == 1)
			retVal = "0" + retVal;
		return retVal;
	}

	public static int numBytes(String val) {
		BigInteger bInt = new BigInteger(val);
		int bytes = 0;

		while (!bInt.equals(BigInteger.ZERO)) {
			bInt = bInt.shiftRight(8);
			++bytes;
		}
		if (bytes == 0)
			++bytes;
		return bytes;
	}

	public static byte[] encodeValFor32Bits(Object arg) {
		byte[] data;
		if (arg.toString().trim().matches("-?\\d+(\\.\\d+)?")) {
			data = new BigInteger(arg.toString().trim()).toByteArray();
		} else if (arg.toString().trim().matches("0[xX][0-9a-fA-F]+")) {
			data = new BigInteger(arg.toString().trim().substring(2), 16)
					.toByteArray();
		} else {
			data = arg.toString().trim().getBytes();
		}
		if (data.length > 32) {
			throw new RuntimeException("values can't be more than 32 byte");
		}
		byte[] val = new byte[32];
		int j = 0;
		for (int i = data.length; i > 0; --i) {
			val[31 - j] = data[i - 1];
			++j;
		}
		return val;
	}

	public static byte[] encodeDataList(Object... args) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		for (Object arg : args) {
			byte[] val = encodeValFor32Bits(arg);
			try {
				baos.write(val);
			} catch (IOException e) {
				throw new Error("Happen something that should never happen ", e);
			}
		}
		return baos.toByteArray();
	}

	public static int firstNonZeroByte(byte[] data) {
		int firstNonZero = -1;
		int i = 0;
		for (; i < data.length; ++i) {
			if (data[i] != 0) {
				firstNonZero = i;
				break;
			}
		}
		return firstNonZero;
	}

	public static byte[] stripLeadingZeroes(byte[] data) {

		if (data == null) {
			return null;
		}

		int firstNonZero = firstNonZeroByte(data);
		int i = 0;
		for (; i < data.length; ++i) {
			if (data[i] != 0) {
				firstNonZero = i;
				break;
			}
		}
		if (i == data.length) {
			return new byte[1];
		}
		if (firstNonZero == 0) {
			return data;
		}

		byte[] result = new byte[data.length - firstNonZero];
		System.arraycopy(data, firstNonZero, result, 0, data.length
				- firstNonZero);

		return result;
	}

	public static byte[] setBit(byte[] data, int pos, int val) {
		if ((data.length * 8) - 1 < pos) {
			throw new Error("outside byte array limit, pos: " + pos);
		}
		int posByte = data.length - 1 - (pos) / 8;
		int posBit = (pos) % 8;
		byte setter = (byte) (1 << (posBit));
		byte toBeSet = data[posByte];
		byte result;
		if (val == 1) {
			result = (byte) (toBeSet | setter);
		} else {
			result = (byte) (toBeSet & ~setter);
		}
		data[posByte] = result;
		return data;
	}

	public static int getBit(byte[] data, int pos) {
		if ((data.length * 8) - 1 < pos) {
			throw new Error("outside byte array limit, pos: " + pos);
		}
		int posByte = data.length - 1 - pos / 8;
		int posBit = pos % 8;
		byte dataByte = data[posByte];
		return Math.min(1, (dataByte & (1 << (posBit))));
	}

	public static boolean increment(byte[] bytes) {
		final int startIndex = 0;
		int i;
		for (i = bytes.length - 1; i >= startIndex; i--) {
			bytes[i]++;
			if (bytes[i] != 0) {
				break;
			}
		}
		return (i >= startIndex || bytes[startIndex] != 0);
	}

	public static byte[] copyToArray(BigInteger value) {
		byte[] src = ByteUtils.bigIntegerToBytes(value);
		byte[] dest = ByteBuffer.allocate(32).array();
		System.arraycopy(src, 0, dest, dest.length - src.length, src.length);
		return dest;
	}

}
