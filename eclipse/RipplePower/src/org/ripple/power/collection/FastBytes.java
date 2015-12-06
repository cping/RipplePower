package org.ripple.power.collection;

import java.nio.ByteOrder;

import com.google.common.primitives.Longs;
import com.google.common.primitives.UnsignedBytes;

public abstract class FastBytes {

	public static int compareTo(byte[] b1, int s1, int l1, byte[] b2, int s2,
			int l2) {
		return LexicographicalComparerHolder.BEST_COMPARER.compareTo(b1, s1,
				l1, b2, s2, l2);
	}

	private interface Comparer<T> {
		abstract public int compareTo(T buffer1, int offset1, int length1,
				T buffer2, int offset2, int length2);
	}

	private static Comparer<byte[]> lexicographicalComparerJavaImpl() {
		return LexicographicalComparerHolder.PureJavaComparer.INSTANCE;
	}

	private static class LexicographicalComparerHolder {
		static final String UNSAFE_COMPARER_NAME = LexicographicalComparerHolder.class
				.getName() + "$UnsafeComparer";

		static final Comparer<byte[]> BEST_COMPARER = getBestComparer();

		static Comparer<byte[]> getBestComparer() {
			try {
				Class<?> theClass = Class.forName(UNSAFE_COMPARER_NAME);

				@SuppressWarnings("unchecked")
				Comparer<byte[]> comparer = (Comparer<byte[]>) theClass
						.getEnumConstants()[0];
				return comparer;
			} catch (Throwable t) {
				return lexicographicalComparerJavaImpl();
			}
		}

		private enum PureJavaComparer implements Comparer<byte[]> {
			INSTANCE;

			@Override
			public int compareTo(byte[] buffer1, int offset1, int length1,
					byte[] buffer2, int offset2, int length2) {
				if (buffer1 == buffer2 && offset1 == offset2
						&& length1 == length2) {
					return 0;
				}
				int end1 = offset1 + length1;
				int end2 = offset2 + length2;
				for (int i = offset1, j = offset2; i < end1 && j < end2; i++, j++) {
					int a = (buffer1[i] & 0xff);
					int b = (buffer2[j] & 0xff);
					if (a != b) {
						return a - b;
					}
				}
				return length1 - length2;
			}
		}

		@SuppressWarnings("unused")
		private enum UnsafeComparer implements Comparer<byte[]> {
			INSTANCE;

			static final Unsafe theUnsafe;

			static final int BYTE_ARRAY_BASE_OFFSET;

			static {
				theUnsafe = new SunUnsafe();

				BYTE_ARRAY_BASE_OFFSET = theUnsafe
						.arrayBaseOffset(byte[].class);

				if (theUnsafe.arrayIndexScale(byte[].class) != 1) {
					throw new AssertionError();
				}
			}

			static final boolean littleEndian = ByteOrder.nativeOrder().equals(
					ByteOrder.LITTLE_ENDIAN);

			static boolean lessThanUnsigned(long x1, long x2) {
				return (x1 + Long.MIN_VALUE) < (x2 + Long.MIN_VALUE);
			}

			@Override
			public int compareTo(byte[] buffer1, int offset1, int length1,
					byte[] buffer2, int offset2, int length2) {
				if (buffer1 == buffer2 && offset1 == offset2
						&& length1 == length2) {
					return 0;
				}
				int minLength = Math.min(length1, length2);
				int minWords = minLength / Longs.BYTES;
				int offset1Adj = offset1 + BYTE_ARRAY_BASE_OFFSET;
				int offset2Adj = offset2 + BYTE_ARRAY_BASE_OFFSET;

				for (int i = 0; i < minWords * Longs.BYTES; i += Longs.BYTES) {
					long lw = theUnsafe.getLong(buffer1, offset1Adj + (long) i);
					long rw = theUnsafe.getLong(buffer2, offset2Adj + (long) i);
					long diff = lw ^ rw;

					if (diff != 0) {
						if (!littleEndian) {
							return lessThanUnsigned(lw, rw) ? -1 : 1;
						}
						int n = 0;
						int y;
						int x = (int) diff;
						if (x == 0) {
							x = (int) (diff >>> 32);
							n = 32;
						}

						y = x << 16;
						if (y == 0) {
							n += 16;
						} else {
							x = y;
						}

						y = x << 8;
						if (y == 0) {
							n += 8;
						}
						return (int) (((lw >>> n) & 0xFFL) - ((rw >>> n) & 0xFFL));
					}
				}

				for (int i = minWords * Longs.BYTES; i < minLength; i++) {
					int result = UnsignedBytes.compare(buffer1[offset1 + i],
							buffer2[offset2 + i]);
					if (result != 0) {
						return result;
					}
				}
				return length1 - length2;
			}
		}
	}
}
