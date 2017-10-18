package org.ripple.power.timer;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class HourTwo extends RegularTimer implements Serializable {

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

	public HourTwo(Date date) {
		this.date = date;
		cal = Calendar.getInstance();
		cal.setTime(date);
		peg(cal);
		cal.setTimeInMillis(getFirstMillisecond());
		hour = (byte) cal.get(Calendar.HOUR_OF_DAY);
		day = new Day(date);
	}

	private long calcStart(Calendar cal) {
		long start;
		cal.setTime(date);
		if (0 <= cal.get(Calendar.HOUR_OF_DAY) && cal.get(Calendar.HOUR_OF_DAY) <= 1) {
			cal = getStartDate(cal, 0);
		} else if (2 <= cal.get(Calendar.HOUR_OF_DAY) && cal.get(Calendar.HOUR_OF_DAY) <= 3) {
			cal = getStartDate(cal, 2);
		} else if (4 <= cal.get(Calendar.HOUR_OF_DAY) && cal.get(Calendar.HOUR_OF_DAY) <= 5) {
			cal = getStartDate(cal, 4);
		} else if (6 <= cal.get(Calendar.HOUR_OF_DAY) && cal.get(Calendar.HOUR_OF_DAY) <= 7) {
			cal = getStartDate(cal, 6);
		} else if (8 <= cal.get(Calendar.HOUR_OF_DAY) && cal.get(Calendar.HOUR_OF_DAY) <= 9) {
			cal = getStartDate(cal, 8);
		} else if (10 <= cal.get(Calendar.HOUR_OF_DAY) && cal.get(Calendar.HOUR_OF_DAY) <= 11) {
			cal = getStartDate(cal, 10);
		} else if (12 <= cal.get(Calendar.HOUR_OF_DAY) && cal.get(Calendar.HOUR_OF_DAY) <= 13) {
			cal = getStartDate(cal, 12);
		} else if (14 <= cal.get(Calendar.HOUR_OF_DAY) && cal.get(Calendar.HOUR_OF_DAY) <= 15) {
			cal = getStartDate(cal, 14);
		} else if (16 <= cal.get(Calendar.HOUR_OF_DAY) && cal.get(Calendar.HOUR_OF_DAY) <= 17) {
			cal = getStartDate(cal, 16);
		} else if (18 <= cal.get(Calendar.HOUR_OF_DAY) && cal.get(Calendar.HOUR_OF_DAY) <= 19) {
			cal = getStartDate(cal, 18);
		} else if (20 <= cal.get(Calendar.HOUR_OF_DAY) && cal.get(Calendar.HOUR_OF_DAY) <= 21) {
			cal = getStartDate(cal, 20);
		} else {
			cal = getStartDate(cal, 22);
		}
		start = cal.getTimeInMillis();
		return start;
	}

	private long calcEnd(Calendar cal) {
		long end;
		cal.setTime(date);
		if (0 <= cal.get(Calendar.HOUR_OF_DAY) && cal.get(Calendar.HOUR_OF_DAY) <= 1) {
			cal = getEndDate(cal, 1);
		} else if (2 <= cal.get(Calendar.HOUR_OF_DAY) && cal.get(Calendar.HOUR_OF_DAY) <= 3) {
			cal = getEndDate(cal, 3);
		} else if (4 <= cal.get(Calendar.HOUR_OF_DAY) && cal.get(Calendar.HOUR_OF_DAY) <= 5) {
			cal = getEndDate(cal, 5);
		} else if (6 <= cal.get(Calendar.HOUR_OF_DAY) && cal.get(Calendar.HOUR_OF_DAY) <= 7) {
			cal = getEndDate(cal, 7);
		} else if (8 <= cal.get(Calendar.HOUR_OF_DAY) && cal.get(Calendar.HOUR_OF_DAY) <= 9) {
			cal = getEndDate(cal, 9);
		} else if (10 <= cal.get(Calendar.HOUR_OF_DAY) && cal.get(Calendar.HOUR_OF_DAY) <= 11) {
			cal = getEndDate(cal, 11);
		} else if (12 <= cal.get(Calendar.HOUR_OF_DAY) && cal.get(Calendar.HOUR_OF_DAY) <= 13) {
			cal = getEndDate(cal, 13);
		} else if (14 <= cal.get(Calendar.HOUR_OF_DAY) && cal.get(Calendar.HOUR_OF_DAY) <= 15) {
			cal = getEndDate(cal, 15);
		} else if (16 <= cal.get(Calendar.HOUR_OF_DAY) && cal.get(Calendar.HOUR_OF_DAY) <= 17) {
			cal = getEndDate(cal, 17);
		} else if (18 <= cal.get(Calendar.HOUR_OF_DAY) && cal.get(Calendar.HOUR_OF_DAY) <= 19) {
			cal = getEndDate(cal, 19);
		} else if (20 <= cal.get(Calendar.HOUR_OF_DAY) && cal.get(Calendar.HOUR_OF_DAY) <= 21) {
			cal = getEndDate(cal, 21);
		} else {
			cal = getEndDate(cal, 23);
		}
		end = cal.getTimeInMillis();
		return end;
	}

	private Calendar getStartDate(Calendar cal, int hour) {
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}

	private Calendar getEndDate(Calendar cal, int hour) {
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, 59);
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
		long hourIndex = this.day.getSerialIndex() * (long) (24 / 2) + (long) (this.hour / 2);
		return hourIndex;
	}

	@Override
	public RegularTimer next() {
		cal.setTime(date);
		cal.add(Calendar.MINUTE, 30);
		return new HourTwo(cal.getTime());
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
		return new HourTwo(cal.getTime());
	}

	public int compareTo(Object obj) {
		int result;
		if (obj instanceof HourTwo) {
			HourTwo h2 = (HourTwo) obj;
			if (this.getFirstMillisecond() == h2.getFirstMillisecond()
					&& this.getLastMillisecond() == h2.getLastMillisecond()) {
				result = 0;
			} else {
				result = (int) (this.getLastMillisecond() - h2.getLastMillisecond());
			}
		} else if (obj instanceof RegularTimer) {
			result = 0;
		} else {
			result = 1;
		}

		return result;
	}
}