package org.ripple.power.timer;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class MinuteThirty extends RegularTimer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Date date;
	private Calendar cal;
	private long firstMillisecond;
	private long lastMillisecond;

	private Day day;
	private byte hour;
	private byte minute;

	public MinuteThirty(Date date) {
		this.date = date;
		cal = Calendar.getInstance();
		cal.setTime(date);
		peg(cal);
		cal.setTimeInMillis(getFirstMillisecond());
		minute = (byte) cal.get(Calendar.MINUTE);
		hour = (byte) cal.get(Calendar.HOUR_OF_DAY);
		day = new Day(date);
	}

	private long calcStart(Calendar cal) {
		long start;
		cal.setTime(date);
		if (0 <= cal.get(Calendar.MINUTE) && cal.get(Calendar.MINUTE) <= 29) {
			cal = getStartDate(cal, 0);
		} else {
			cal = getStartDate(cal, 30);
		}
		start = cal.getTimeInMillis();
		return start;
	}

	private long calcEnd(Calendar cal) {
		long end;
		cal.setTime(date);
		if (0 <= cal.get(Calendar.MINUTE) && cal.get(Calendar.MINUTE) <= 29) {
			cal = getEndDate(cal, 29);
		} else {
			cal = getEndDate(cal, 59);
		}
		end = cal.getTimeInMillis();
		return end;
	}

	private Calendar getStartDate(Calendar cal, int minute) {
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}

	private Calendar getEndDate(Calendar cal, int minute) {
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		return cal;
	}

	@Override
	public long getFirstMillisecond() {
		return firstMillisecond;
	}

	@Override
	public long getFirstMillisecond(Calendar cal) {
		long first = this.calcStart(cal);
		return first;
	}

	@Override
	public long getLastMillisecond() {
		return lastMillisecond;
	}

	@Override
	public long getLastMillisecond(Calendar cal) {
		long last = this.calcEnd(cal);
		return last;
	}

	@Override
	public long getSerialIndex() {
		long hourIndex = this.day.getSerialIndex() * 24L + this.hour;
		return hourIndex * (long) (60 / 30) + (long) (this.minute / 30);
	}

	@Override
	public RegularTimer next() {
		cal.setTime(date);
		cal.add(Calendar.MINUTE, 30);
		return new MinuteThirty(cal.getTime());
	}

	@Override
	public void peg(Calendar cal) {
		this.firstMillisecond = getFirstMillisecond(cal);
		this.lastMillisecond = getLastMillisecond(cal);
	}

	@Override
	public RegularTimer previous() {
		cal.setTime(date);
		cal.add(Calendar.MINUTE, -30);
		return new MinuteThirty(cal.getTime());
	}

	public int compareTo(Object obj) {
		int result;
		if (obj instanceof MinuteThirty) {
			MinuteThirty m30 = (MinuteThirty) obj;
			if (this.getFirstMillisecond() == m30.getFirstMillisecond()
					&& this.getLastMillisecond() == m30.getLastMillisecond()) {
				result = 0;
			} else {
				result = (int) (this.getLastMillisecond() - m30
						.getLastMillisecond());
			}
		} else if (obj instanceof RegularTimer) {
			result = 0;
		} else {
			result = 1;
		}

		return result;
	}
}