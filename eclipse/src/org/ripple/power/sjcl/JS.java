package org.ripple.power.sjcl;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.ripple.power.collection.ArrayUtils;

public class JS {

	public static Number OR(Number a, Number b) {
		if (a.longValue() > 0) {
			return a;
		}
		if (b.longValue() > 0) {
			return b;
		}
		return a;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] splice(T[] array, int index, int num, T... ts)
			throws Exception {
		if (!ts.getClass().equals(array.getClass())) {
			throw new Exception("Wrong Array types");
		}
		if (num > array.length - index) {
			throw new Exception("Unable to cut the string");
		}
		if (num == 0) {
			index++;
		}
		T[] firstPart = (T[]) Array.newInstance(array[0].getClass(), index);
		T[] secondPart = (T[]) Array.newInstance(array[0].getClass(),
				array.length - index - num);
		System.arraycopy(array, 0, firstPart, 0, firstPart.length);
		System.arraycopy(array, index + num, secondPart, 0, secondPart.length);
		return JS.concatenateArrays(firstPart, ts, secondPart);
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] concatenateArrays(T[]... ts) {
		List<T> list = new ArrayList<T>();
		for (T[] s : ts) {
			list.addAll(Arrays.asList(s));
		}
		return list.toArray((T[]) Array.newInstance(ts[0].getClass()
				.getComponentType(), 0));
	}

	public static <T> String[] convertArraytoStringArray(T[] array) {
		String[] newStrs = new String[array.length];
		for (int i = 0; i < newStrs.length; i++) {
			newStrs[i] = array[i].toString();
		}
		return newStrs;
	}

	public static int[] splice(int[] ints, int index, int num, int... ts)
			throws Exception {
		return JS.objectToPrim(JS.splice(ArrayUtils.toObject(ints), index, num,
				ArrayUtils.toObject(ts)));
	}

	public static long[] splice(long[] longs, int index, int num, long... ts)
			throws Exception {
		return JS.objectToPrim(JS.splice(ArrayUtils.toObject(longs), index,
				num, ArrayUtils.toObject(ts)));
	}

	public static short[] splice(short[] shorts, int index, int num,
			short... ts) throws Exception {
		return JS.objectToPrim(JS.splice(ArrayUtils.toObject(shorts), index,
				num, ArrayUtils.toObject(ts)));
	}

	public static double[] splice(double[] doubles, int index, int num,
			double... ts) throws Exception {
		return JS.objectToPrim(JS.splice(ArrayUtils.toObject(doubles), index,
				num, ArrayUtils.toObject(ts)));
	}

	public static float[] splice(float[] floats, int index, int num,
			float... ts) throws Exception {
		return JS.objectToPrim(JS.splice(ArrayUtils.toObject(floats), index,
				num, ArrayUtils.toObject(ts)));
	}

	public static char[] splice(char[] chars, int index, int num, char... ts)
			throws Exception {
		return JS.objectToPrim(JS.splice(ArrayUtils.toObject(chars), index,
				num, ArrayUtils.toObject(ts)));
	}

	public static boolean[] splice(boolean[] booleans, int index, int num,
			boolean... ts) throws Exception {
		return JS.objectToPrim(JS.splice(ArrayUtils.toObject(booleans), index,
				num, ArrayUtils.toObject(ts)));
	}

	public static byte[] splice(byte[] bytes, int index, int num, byte... ts)
			throws Exception {
		return JS.objectToPrim(JS.splice(ArrayUtils.toObject(bytes), index,
				num, ArrayUtils.toObject(ts)));
	}

	public static int[] objectToPrim(Integer[] is) {
		int[] integers = new int[is.length];
		for (int i = 0; i < is.length; i++) {
			integers[i] = is[i].intValue();
		}
		return integers;
	}

	public static long[] objectToPrim(Long[] ls) {
		long[] longs = new long[ls.length];
		for (int i = 0; i < ls.length; i++) {
			longs[i] = ls[i].longValue();
		}
		return longs;
	}

	public static short[] objectToPrim(Short[] ss) {
		short[] shorts = new short[ss.length];
		for (int i = 0; i < ss.length; i++) {
			shorts[i] = ss[i].shortValue();
		}
		return shorts;
	}

	public static double[] objectToPrim(Double[] ds) {
		double[] doubles = new double[ds.length];
		for (int i = 0; i < ds.length; i++) {
			doubles[i] = ds[i].doubleValue();
		}
		return doubles;
	}

	public static float[] objectToPrim(Float[] fs) {
		float[] floats = new float[fs.length];
		for (int i = 0; i < fs.length; i++) {
			floats[i] = fs[i].floatValue();
		}
		return floats;
	}

	public static boolean[] objectToPrim(Boolean[] bs) {
		boolean[] booleans = new boolean[bs.length];
		for (int i = 0; i < bs.length; i++) {
			booleans[i] = bs[i].booleanValue();
		}
		return booleans;
	}

	public static char[] objectToPrim(Character[] cs) {
		char[] chars = new char[cs.length];
		for (int i = 0; i < cs.length; i++) {
			chars[i] = cs[i].charValue();
		}
		return chars;
	}

	public static byte[] objectToPrim(Byte[] bys) {
		byte[] bytes = new byte[bys.length];
		for (int i = 0; i < bys.length; i++) {
			bytes[i] = bys[i].byteValue();
		}
		return bytes;
	}

	public static long MOVE_LeftShift(Number v, int pos) {
		return (int) (v.intValue() << pos);
	}

	public static long MOVE_RightUShift(Number v, int pos) {
		if (pos == 0) {
			String bin = Long.toBinaryString(v.longValue());
			if (bin.length() > 31) {
				bin = bin.substring(bin.length() - 32, bin.length());
			} else {
				return (v.intValue());
			}
			return new BigInteger(bin, 2).longValue();
		}
		return (v.intValue() >>> pos);
	}

	public static long MOVE_RightShift(Number v, int pos) {
		if (pos == 0) {
			String bin = Long.toBinaryString(v.longValue());
			if (bin.length() > 31) {
				bin = bin.substring(bin.length() - 32, bin.length());
			} else {
				return (v.intValue());
			}
			return new BigInteger(bin, 2).longValue();
		}
		return (v.intValue() >> pos);
	}

	public static long get(long v) {
		return (int) v;
	}

}
