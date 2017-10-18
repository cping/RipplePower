package org.ripple.power.timer;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Day extends RegularTimer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	protected static final DateFormat DATE_FORMAT_SHORT = DateFormat.getDateInstance(DateFormat.SHORT);

	protected static final DateFormat DATE_FORMAT_MEDIUM = DateFormat.getDateInstance(DateFormat.MEDIUM);

	protected static final DateFormat DATE_FORMAT_LONG = DateFormat.getDateInstance(DateFormat.LONG);

	private SerialDate serialDate;

	private long firstMillisecond;

	private long lastMillisecond;

	public Day() {
		this(new Date());
	}

	public Day(int day, int month, int year) {
		this.serialDate = SerialDate.createInstance(day, month, year);
		peg(Calendar.getInstance());
	}

	public Day(SerialDate serialDate) {
		this.serialDate = serialDate;
		peg(Calendar.getInstance());
	}

	public Day(Date time) {
		this(time, TimeZone.getDefault(), Locale.getDefault());
	}

	public Day(Date time, TimeZone zone) {
		this(time, zone, Locale.getDefault());
	}

	public Day(Date time, TimeZone zone, Locale locale) {
		Calendar calendar = Calendar.getInstance(zone, locale);
		calendar.setTime(time);
		int d = calendar.get(Calendar.DAY_OF_MONTH);
		int m = calendar.get(Calendar.MONTH) + 1;
		int y = calendar.get(Calendar.YEAR);
		this.serialDate = SerialDate.createInstance(d, m, y);
		peg(calendar);
	}

	public SerialDate getSerialDate() {
		return this.serialDate;
	}

	public int getYear() {
		return this.serialDate.getYYYY();
	}

	public int getMonth() {
		return this.serialDate.getMonth();
	}

	public int getDayOfMonth() {
		return this.serialDate.getDayOfMonth();
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
		Day result;
		int serial = this.serialDate.toSerial();
		if (serial > SerialDate.SERIAL_LOWER_BOUND) {
			SerialDate yesterday = SerialDate.createInstance(serial - 1);
			return new Day(yesterday);
		} else {
			result = null;
		}
		return result;
	}

	@Override
	public RegularTimer next() {
		Day result;
		int serial = this.serialDate.toSerial();
		if (serial < SerialDate.SERIAL_UPPER_BOUND) {
			SerialDate tomorrow = SerialDate.createInstance(serial + 1);
			return new Day(tomorrow);
		} else {
			result = null;
		}
		return result;
	}

	@Override
	public long getSerialIndex() {
		return this.serialDate.toSerial();
	}

	@Override
	public long getFirstMillisecond(Calendar calendar) {
		int year = this.serialDate.getYYYY();
		int month = this.serialDate.getMonth();
		int day = this.serialDate.getDayOfMonth();
		calendar.clear();
		calendar.set(year, month - 1, day, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();
	}

	@Override
	public long getLastMillisecond(Calendar calendar) {
		int year = this.serialDate.getYYYY();
		int month = this.serialDate.getMonth();
		int day = this.serialDate.getDayOfMonth();
		calendar.clear();
		calendar.set(year, month - 1, day, 23, 59, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar.getTimeInMillis();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Day)) {
			return false;
		}
		Day that = (Day) obj;
		if (!this.serialDate.equals(that.getSerialDate())) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return this.serialDate.hashCode();
	}

	@Override
	public int compareTo(Object o1) {
		int result;
		if (o1 instanceof Day) {
			Day d = (Day) o1;
			result = -d.getSerialDate().compare(this.serialDate);
		} else if (o1 instanceof RegularTimer) {
			result = 0;
		} else {
			result = 1;
		}

		return result;
	}

	@Override
	public String toString() {
		return this.serialDate.toString();
	}

	public static Day parseDay(String s) {
		try {
			return new Day(Day.DATE_FORMAT.parse(s));
		} catch (ParseException e1) {
			try {
				return new Day(Day.DATE_FORMAT_SHORT.parse(s));
			} catch (ParseException e2) {

			}
		}
		return null;
	}

}
