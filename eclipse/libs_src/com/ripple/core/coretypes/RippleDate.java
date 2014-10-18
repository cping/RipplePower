package com.ripple.core.coretypes;

import com.ripple.core.coretypes.uint.UInt32;
import com.ripple.core.serialized.BinaryParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

//public class RippleDate extends Date implements SerializedType {
public class RippleDate extends Date {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final DateFormat YYYY_MM_DD_HHMM = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm");
	public static long RIPPLE_EPOCH_SECONDS_OFFSET = 0x386D4380;
	static {
		/**
		 * Magic constant tested and documented.
		 * 
		 * Seconds since the unix epoch from unix time (accounting leap years
		 * etc) at 1/January/2000 GMT
		 */
		GregorianCalendar cal = new GregorianCalendar(
				TimeZone.getTimeZone("GMT"));
		cal.set(2000, Calendar.JANUARY, 1, 0, 0, 0);
	}

	private RippleDate() {
		super();
	}

	private RippleDate(long milliseconds) {
		super(milliseconds);
	}

	public long secondsSinceRippleEpoch() {
		return ((this.getTime() / 1000) - RIPPLE_EPOCH_SECONDS_OFFSET);
	}

	public static RippleDate fromSecondsSinceRippleEpoch(Number seconds) {
		return new RippleDate(
				(seconds.longValue() + RIPPLE_EPOCH_SECONDS_OFFSET) * 1000);
	}

	public static RippleDate fromParser(BinaryParser parser) {
		UInt32 uInt32 = UInt32.translate.fromParser(parser);
		return fromSecondsSinceRippleEpoch(uInt32);
	}

	public static RippleDate now() {
		return new RippleDate();
	}

	public String getTimeString() {
		return YYYY_MM_DD_HHMM.format(this);
	}
}
