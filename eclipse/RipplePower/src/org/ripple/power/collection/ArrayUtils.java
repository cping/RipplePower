package org.ripple.power.collection;

public class ArrayUtils {

	public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

	public static final Class<?>[] EMPTY_CLASS_ARRAY = new Class[0];

	public static final String[] EMPTY_STRING_ARRAY = new String[0];

	public static final long[] EMPTY_LONG_ARRAY = new long[0];

	public static final Long[] EMPTY_LONG_OBJECT_ARRAY = new Long[0];

	public static final int[] EMPTY_INT_ARRAY = new int[0];

	public static final Integer[] EMPTY_INTEGER_OBJECT_ARRAY = new Integer[0];

	public static final short[] EMPTY_SHORT_ARRAY = new short[0];

	public static final Short[] EMPTY_SHORT_OBJECT_ARRAY = new Short[0];

	public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

	public static final Byte[] EMPTY_BYTE_OBJECT_ARRAY = new Byte[0];

	public static final double[] EMPTY_DOUBLE_ARRAY = new double[0];

	public static final Double[] EMPTY_DOUBLE_OBJECT_ARRAY = new Double[0];

	public static final float[] EMPTY_FLOAT_ARRAY = new float[0];

	public static final Float[] EMPTY_FLOAT_OBJECT_ARRAY = new Float[0];

	public static final boolean[] EMPTY_BOOLEAN_ARRAY = new boolean[0];

	public static final Boolean[] EMPTY_BOOLEAN_OBJECT_ARRAY = new Boolean[0];

	public static final char[] EMPTY_CHAR_ARRAY = new char[0];

	public static final Character[] EMPTY_CHARACTER_OBJECT_ARRAY = new Character[0];

	public static final int INDEX_NOT_FOUND = -1;

	public static Character[] toObject(char[] array) {
		if (array == null) {
			return null;
		} else if (array.length == 0) {
			return EMPTY_CHARACTER_OBJECT_ARRAY;
		}
		final Character[] result = new Character[array.length];
		for (int i = 0; i < array.length; i++) {
			result[i] = Character.valueOf(array[i]);
		}
		return result;
	}

	public static long[] toPrimitive(Long[] array) {
		if (array == null) {
			return null;
		} else if (array.length == 0) {
			return EMPTY_LONG_ARRAY;
		}
		final long[] result = new long[array.length];
		for (int i = 0; i < array.length; i++) {
			result[i] = array[i].longValue();
		}
		return result;
	}

	public static long[] toPrimitive(Long[] array, long valueForNull) {
		if (array == null) {
			return null;
		} else if (array.length == 0) {
			return EMPTY_LONG_ARRAY;
		}
		final long[] result = new long[array.length];
		for (int i = 0; i < array.length; i++) {
			Long b = array[i];
			result[i] = (b == null ? valueForNull : b.longValue());
		}
		return result;
	}

	public static Long[] toObject(long[] array) {
		if (array == null) {
			return null;
		} else if (array.length == 0) {
			return EMPTY_LONG_OBJECT_ARRAY;
		}
		final Long[] result = new Long[array.length];
		for (int i = 0; i < array.length; i++) {
			result[i] = Long.valueOf(array[i]);
		}
		return result;
	}

	public static int[] toPrimitive(Integer[] array) {
		if (array == null) {
			return null;
		} else if (array.length == 0) {
			return EMPTY_INT_ARRAY;
		}
		final int[] result = new int[array.length];
		for (int i = 0; i < array.length; i++) {
			result[i] = array[i].intValue();
		}
		return result;
	}

	public static int[] toPrimitive(Integer[] array, int valueForNull) {
		if (array == null) {
			return null;
		} else if (array.length == 0) {
			return EMPTY_INT_ARRAY;
		}
		final int[] result = new int[array.length];
		for (int i = 0; i < array.length; i++) {
			Integer b = array[i];
			result[i] = (b == null ? valueForNull : b.intValue());
		}
		return result;
	}

	public static Integer[] toObject(int[] array) {
		if (array == null) {
			return null;
		} else if (array.length == 0) {
			return EMPTY_INTEGER_OBJECT_ARRAY;
		}
		final Integer[] result = new Integer[array.length];
		for (int i = 0; i < array.length; i++) {
			result[i] = Integer.valueOf(array[i]);
		}
		return result;
	}

	public static short[] toPrimitive(Short[] array) {
		if (array == null) {
			return null;
		} else if (array.length == 0) {
			return EMPTY_SHORT_ARRAY;
		}
		final short[] result = new short[array.length];
		for (int i = 0; i < array.length; i++) {
			result[i] = array[i].shortValue();
		}
		return result;
	}

	public static short[] toPrimitive(Short[] array, short valueForNull) {
		if (array == null) {
			return null;
		} else if (array.length == 0) {
			return EMPTY_SHORT_ARRAY;
		}
		final short[] result = new short[array.length];
		for (int i = 0; i < array.length; i++) {
			Short b = array[i];
			result[i] = (b == null ? valueForNull : b.shortValue());
		}
		return result;
	}

	public static Short[] toObject(short[] array) {
		if (array == null) {
			return null;
		} else if (array.length == 0) {
			return EMPTY_SHORT_OBJECT_ARRAY;
		}
		final Short[] result = new Short[array.length];
		for (int i = 0; i < array.length; i++) {
			result[i] = Short.valueOf(array[i]);
		}
		return result;
	}

	public static byte[] toPrimitive(Byte[] array) {
		if (array == null) {
			return null;
		} else if (array.length == 0) {
			return EMPTY_BYTE_ARRAY;
		}
		final byte[] result = new byte[array.length];
		for (int i = 0; i < array.length; i++) {
			result[i] = array[i].byteValue();
		}
		return result;
	}

	public static byte[] toPrimitive(Byte[] array, byte valueForNull) {
		if (array == null) {
			return null;
		} else if (array.length == 0) {
			return EMPTY_BYTE_ARRAY;
		}
		final byte[] result = new byte[array.length];
		for (int i = 0; i < array.length; i++) {
			Byte b = array[i];
			result[i] = (b == null ? valueForNull : b.byteValue());
		}
		return result;
	}

	public static Byte[] toObject(byte[] array) {
		if (array == null) {
			return null;
		} else if (array.length == 0) {
			return EMPTY_BYTE_OBJECT_ARRAY;
		}
		final Byte[] result = new Byte[array.length];
		for (int i = 0; i < array.length; i++) {
			result[i] = Byte.valueOf(array[i]);
		}
		return result;
	}

	public static double[] toPrimitive(Double[] array) {
		if (array == null) {
			return null;
		} else if (array.length == 0) {
			return EMPTY_DOUBLE_ARRAY;
		}
		final double[] result = new double[array.length];
		for (int i = 0; i < array.length; i++) {
			result[i] = array[i].doubleValue();
		}
		return result;
	}

	public static double[] toPrimitive(Double[] array, double valueForNull) {
		if (array == null) {
			return null;
		} else if (array.length == 0) {
			return EMPTY_DOUBLE_ARRAY;
		}
		final double[] result = new double[array.length];
		for (int i = 0; i < array.length; i++) {
			Double b = array[i];
			result[i] = (b == null ? valueForNull : b.doubleValue());
		}
		return result;
	}

	public static Double[] toObject(double[] array) {
		if (array == null) {
			return null;
		} else if (array.length == 0) {
			return EMPTY_DOUBLE_OBJECT_ARRAY;
		}
		final Double[] result = new Double[array.length];
		for (int i = 0; i < array.length; i++) {
			result[i] = Double.valueOf(array[i]);
		}
		return result;
	}

	public static float[] toPrimitive(Float[] array) {
		if (array == null) {
			return null;
		} else if (array.length == 0) {
			return EMPTY_FLOAT_ARRAY;
		}
		final float[] result = new float[array.length];
		for (int i = 0; i < array.length; i++) {
			result[i] = array[i].floatValue();
		}
		return result;
	}

	public static float[] toPrimitive(Float[] array, float valueForNull) {
		if (array == null) {
			return null;
		} else if (array.length == 0) {
			return EMPTY_FLOAT_ARRAY;
		}
		final float[] result = new float[array.length];
		for (int i = 0; i < array.length; i++) {
			Float b = array[i];
			result[i] = (b == null ? valueForNull : b.floatValue());
		}
		return result;
	}

	public static Float[] toObject(float[] array) {
		if (array == null) {
			return null;
		} else if (array.length == 0) {
			return EMPTY_FLOAT_OBJECT_ARRAY;
		}
		final Float[] result = new Float[array.length];
		for (int i = 0; i < array.length; i++) {
			result[i] = Float.valueOf(array[i]);
		}
		return result;
	}

	public static boolean[] toPrimitive(Boolean[] array) {
		if (array == null) {
			return null;
		} else if (array.length == 0) {
			return EMPTY_BOOLEAN_ARRAY;
		}
		final boolean[] result = new boolean[array.length];
		for (int i = 0; i < array.length; i++) {
			result[i] = array[i].booleanValue();
		}
		return result;
	}

	public static boolean[] toPrimitive(Boolean[] array, boolean valueForNull) {
		if (array == null) {
			return null;
		} else if (array.length == 0) {
			return EMPTY_BOOLEAN_ARRAY;
		}
		final boolean[] result = new boolean[array.length];
		for (int i = 0; i < array.length; i++) {
			Boolean b = array[i];
			result[i] = (b == null ? valueForNull : b.booleanValue());
		}
		return result;
	}

	public static Boolean[] toObject(boolean[] array) {
		if (array == null) {
			return null;
		} else if (array.length == 0) {
			return EMPTY_BOOLEAN_OBJECT_ARRAY;
		}
		final Boolean[] result = new Boolean[array.length];
		for (int i = 0; i < array.length; i++) {
			result[i] = (array[i] ? Boolean.TRUE : Boolean.FALSE);
		}
		return result;
	}
}
