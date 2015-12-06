package org.ripple.power.speed;

public class ByteRange {

	private static final String RANGE_PROPERTY_PREFIX = "bytes=";
	private int initByte;
	private int endByte;

	public ByteRange(int initByte, int endByte) {
		this.initByte = initByte;
		this.endByte = endByte;
	}

	public ByteRange(String byteRange) {
		String[] ranges = byteRange.split("-");
		if (ranges.length == 2) {
			try {
				initByte = Integer.parseInt(ranges[0]);
				endByte = Integer.parseInt(ranges[1]);
			} catch (NumberFormatException e) {
				throw new NumberFormatException("Incorrect range");
			}
		} else {
			throw new NumberFormatException("Incorrect range");
		}
	}

	public int getInitByte() {
		return initByte;
	}

	public int getEndByte() {
		return endByte;
	}

	public int getRangeSize() {
		return (endByte - initByte);
	}

	public String getRangeProperty() {
		if (endByte > 0) {
			return RANGE_PROPERTY_PREFIX + initByte + "-" + endByte;
		} else {
			return RANGE_PROPERTY_PREFIX + initByte + "-";
		}
	}

	@Override
	public String toString() {
		return "init: " + initByte + " end: " + endByte;
	}
}
