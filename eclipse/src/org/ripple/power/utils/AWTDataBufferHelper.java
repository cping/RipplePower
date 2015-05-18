package org.ripple.power.utils;

import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferDouble;
import java.awt.image.DataBufferFloat;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;

public class AWTDataBufferHelper {

	public static Object getData(final DataBuffer db) {
		if (db instanceof DataBufferByte) {
			return ((DataBufferByte) db).getData();
		} else if (db instanceof DataBufferUShort) {
			return ((DataBufferUShort) db).getData();
		} else if (db instanceof DataBufferShort) {
			return ((DataBufferShort) db).getData();
		} else if (db instanceof DataBufferInt) {
			return ((DataBufferInt) db).getData();
		} else if (db instanceof DataBufferFloat) {
			return ((DataBufferFloat) db).getData();
		} else if (db instanceof DataBufferDouble) {
			return ((DataBufferDouble) db).getData();
		} else {
			throw new RuntimeException("Not found DataBuffer class !");
		}
	}

	public static int[] getDataInt(final DataBuffer db) {
		return ((DataBufferInt) db).getData();
	}

	public static byte[] getDataByte(final DataBuffer db) {
		return ((DataBufferByte) db).getData();
	}

	public static short[] getDataShort(final DataBuffer db) {
		return ((DataBufferShort) db).getData();
	}

	public static short[] getDataUShort(final DataBuffer db) {
		return ((DataBufferUShort) db).getData();
	}

	public static double[] getDataDouble(final DataBuffer db) {
		return ((DataBufferDouble) db).getData();
	}

	public static float[] getDataFloat(final DataBuffer db) {
		return ((DataBufferFloat) db).getData();
	}
}
