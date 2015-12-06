package org.ripple.power.timer;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Quarter extends RegularTimer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int FIRST_QUARTER = 1;

	public static final int LAST_QUARTER = 4;

	public static final int[] FIRST_MONTH_IN_QUARTER = { 0,
			MonthConstants.JANUARY, MonthConstants.APRIL, MonthConstants.JULY,
			MonthConstants.OCTOBER };

	public static final int[] LAST_MONTH_IN_QUARTER = { 0,
			MonthConstants.MARCH, MonthConstants.JUNE,
			MonthConstants.SEPTEMBER, MonthConstants.DECEMBER };

	private short year;

	private byte quarter;

	private long firstMillisecond;

	private long lastMillisecond;

	public Quarter() {
		this(new Date());
	}

	public Quarter(int quarter, int year) {
		if ((quarter < FIRST_QUARTER) || (quarter > LAST_QUARTER)) {
			throw new IllegalArgumentException("Quarter outside valid range.");
		}
		this.year = (short) year;
		this.quarter = (byte) quarter;
		peg(Calendar.getInstance());
	}

	public Quarter(int quarter, Year year) {
		if ((quarter < FIRST_QUARTER) || (quarter > LAST_QUARTER)) {
			throw new IllegalArgumentException("Quarter outside valid range.");
		}
		this.year = (short) year.getYear();
		this.quarter = (byte) quarter;
		peg(Calendar.getInstance());
	}

	public Quarter(Date time) {
		this(time, TimeZone.getDefault());
	}

	public Quarter(Date time, TimeZone zone) {
		this(time, zone, Locale.getDefault());
	}

	public Quarter(Date time, TimeZone zone, Locale locale) {
		Calendar calendar = Calendar.getInstance(zone, locale);
		calendar.setTime(time);
		int month = calendar.get(Calendar.MONTH) + 1;
		this.quarter = (byte) SerialDate.monthCodeToQuarter(month);
		this.year = (short) calendar.get(Calendar.YEAR);
		peg(calendar);
	}

	public int getQuarter() {
		return this.quarter;
	}

	public Year getYear() {
		return new Year(this.year);
	}

	public int getYearValue() {
		return this.year;
	}

	@Override
	public long getFirstMillisecond() {
		return this.firstMillisecond;
	}

	@Override
	public long getLastMillisecond() {
		return this.lastMillisecond;
	}

	@Override
	public void peg(Calendar calendar) {
		this.firstMillisecond = getFirstMillisecond(calendar);
		this.lastMillisecond = getLastMillisecond(calendar);
	}

	@Override
	public RegularTimer previous() {
		Quarter result;
		if (this.quarter > FIRST_QUARTER) {
			result = new Quarter(this.quarter - 1, this.year);
		} else {
			if (this.year > 1900) {
				result = new Quarter(LAST_QUARTER, this.year - 1);
			} else {
				result = null;
			}
		}
		return result;
	}

	@Override
	public RegularTimer next() {
		Quarter result;
		if (this.quarter < LAST_QUARTER) {
			result = new Quarter(this.quarter + 1, this.year);
		} else {
			if (this.year < 9999) {
				result = new Quarter(FIRST_QUARTER, this.year + 1);
			} else {
				result = null;
			}
		}
		return result;
	}

	@Override
	public long getSerialIndex() {
		return this.year * 4L + this.quarter;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj != null) {
			if (obj instanceof Quarter) {
				Quarter target = (Quarter) obj;
				return (this.quarter == target.getQuarter() && (this.year == target
						.getYearValue()));
			} else {
				return false;
			}
		} else {
			return false;
		}

	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 37 * result + this.quarter;
		result = 37 * result + this.year;
		return result;
	}

	@Override
	public int compareTo(Object o1) {

		int result;

		if (o1 instanceof Quarter) {
			Quarter q = (Quarter) o1;
			result = this.year - q.getYearValue();
			if (result == 0) {
				result = this.quarter - q.getQuarter();
			}
		}

		else if (o1 instanceof RegularTimer) {
			result = 0;
		}

		else {
			result = 1;
		}

		return result;

	}

	@Override
	public String toString() {
		return "Q" + this.quarter + "/" + this.year;
	}

	@Override
	public long getFirstMillisecond(Calendar calendar) {
		int month = Quarter.FIRST_MONTH_IN_QUARTER[this.quarter];
		calendar.set(this.year, month - 1, 1, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();
	}

	@Override
	public long getLastMillisecond(Calendar calendar) {
		int month = Quarter.LAST_MONTH_IN_QUARTER[this.quarter];
		int eom = SerialDate.lastDayOfMonth(month, this.year);
		calendar.set(this.year, month - 1, eom, 23, 59, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar.getTimeInMillis();
	}

	public static Quarter parseQuarter(String s) {
		int i = s.indexOf("Q");
		if (i == -1) {
			throw new RuntimeException("Missing Q.");
		}

		if (i == s.length() - 1) {
			throw new RuntimeException("Q found at end of string.");
		}

		String qstr = s.substring(i + 1, i + 2);
		int quarter = Integer.parseInt(qstr);
		String remaining = s.substring(0, i) + s.substring(i + 2, s.length());

		remaining = remaining.replace('/', ' ');
		remaining = remaining.replace(',', ' ');
		remaining = remaining.replace('-', ' ');

		Year year = Year.parseYear(remaining.trim());
		Quarter result = new Quarter(quarter, year);
		return result;

	}

}
