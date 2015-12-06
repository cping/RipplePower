package org.ripple.power.timer;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Week extends RegularTimer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int FIRST_WEEK_IN_YEAR = 1;

	public static final int LAST_WEEK_IN_YEAR = 53;

	private short year;

	private byte week;

	private long firstMillisecond;

	private long lastMillisecond;

	public Week() {
		this(new Date());
	}

	public Week(int week, int year) {
		if ((week < FIRST_WEEK_IN_YEAR) && (week > LAST_WEEK_IN_YEAR)) {
			throw new IllegalArgumentException(
					"The 'week' argument must be in the range 1 - 53.");
		}
		this.week = (byte) week;
		this.year = (short) year;
		peg(Calendar.getInstance());
	}

	public Week(int week, Year year) {
		if ((week < FIRST_WEEK_IN_YEAR) && (week > LAST_WEEK_IN_YEAR)) {
			throw new IllegalArgumentException(
					"The 'week' argument must be in the range 1 - 53.");
		}
		this.week = (byte) week;
		this.year = (short) year.getYear();
		peg(Calendar.getInstance());
	}

	public Week(Date time) {
		this(time, TimeZone.getDefault(), Locale.getDefault());
	}

	public Week(Date time, TimeZone zone) {
		this(time, zone, Locale.getDefault());
	}

	public Week(Date time, TimeZone zone, Locale locale) {
		Calendar calendar = Calendar.getInstance(zone, locale);
		calendar.setTime(time);

		int tempWeek = calendar.get(Calendar.WEEK_OF_YEAR);
		if (tempWeek == 1 && calendar.get(Calendar.MONTH) == Calendar.DECEMBER) {
			this.week = 1;
			this.year = (short) (calendar.get(Calendar.YEAR) + 1);
		} else {
			this.week = (byte) Math.min(tempWeek, LAST_WEEK_IN_YEAR);
			int yyyy = calendar.get(Calendar.YEAR);
			if (calendar.get(Calendar.MONTH) == Calendar.JANUARY
					&& this.week >= 52) {
				yyyy--;
			}
			this.year = (short) yyyy;
		}
		peg(calendar);
	}

	public Year getYear() {
		return new Year(this.year);
	}

	public int getYearValue() {
		return this.year;
	}

	public int getWeek() {
		return this.week;
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
		Week result;
		if (this.week != FIRST_WEEK_IN_YEAR) {
			result = new Week(this.week - 1, this.year);
		} else {
			if (this.year > 1900) {
				int yy = this.year - 1;
				Calendar prevYearCalendar = Calendar.getInstance();
				prevYearCalendar.set(yy, Calendar.DECEMBER, 31);
				result = new Week(
						prevYearCalendar
								.getActualMaximum(Calendar.WEEK_OF_YEAR),
						yy);
			} else {
				result = null;
			}
		}
		return result;

	}

	@Override
	public RegularTimer next() {

		Week result;
		if (this.week < 52) {
			result = new Week(this.week + 1, this.year);
		} else {
			Calendar calendar = Calendar.getInstance();
			calendar.set(this.year, Calendar.DECEMBER, 31);
			int actualMaxWeek = calendar
					.getActualMaximum(Calendar.WEEK_OF_YEAR);
			if (this.week < actualMaxWeek) {
				result = new Week(this.week + 1, this.year);
			} else {
				if (this.year < 9999) {
					result = new Week(FIRST_WEEK_IN_YEAR, this.year + 1);
				} else {
					result = null;
				}
			}
		}
		return result;

	}

	@Override
	public long getSerialIndex() {
		return this.year * 53L + this.week;
	}

	@Override
	public long getFirstMillisecond(Calendar calendar) {
		Calendar c = (Calendar) calendar.clone();
		c.clear();
		c.set(Calendar.YEAR, this.year);
		c.set(Calendar.WEEK_OF_YEAR, this.week);
		c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
		c.set(Calendar.HOUR, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTimeInMillis();
	}

	@Override
	public long getLastMillisecond(Calendar calendar) {
		Calendar c = (Calendar) calendar.clone();
		c.clear();
		c.set(Calendar.YEAR, this.year);
		c.set(Calendar.WEEK_OF_YEAR, this.week + 1);
		c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
		c.set(Calendar.HOUR, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTimeInMillis() - 1;
	}

	@Override
	public String toString() {
		return "Week " + this.week + ", " + this.year;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Week)) {
			return false;
		}
		Week that = (Week) obj;
		if (this.week != that.week) {
			return false;
		}
		if (this.year != that.year) {
			return false;
		}
		return true;

	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 37 * result + this.week;
		result = 37 * result + this.year;
		return result;
	}

	@Override
	public int compareTo(Object o1) {

		int result;

		if (o1 instanceof Week) {
			Week w = (Week) o1;
			result = this.year - w.getYear().getYear();
			if (result == 0) {
				result = this.week - w.getWeek();
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

	public static Week parseWeek(String s) {

		Week result = null;
		if (s != null) {

			s = s.trim();

			int i = Week.findSeparator(s);
			if (i != -1) {
				String s1 = s.substring(0, i).trim();
				String s2 = s.substring(i + 1, s.length()).trim();

				Year y = Week.evaluateAsYear(s1);
				int w;
				if (y != null) {
					w = Week.stringToWeek(s2);
					if (w == -1) {
						throw new RuntimeException("Can't evaluate the week.");
					}
					result = new Week(w, y);
				} else {
					y = Week.evaluateAsYear(s2);
					if (y != null) {
						w = Week.stringToWeek(s1);
						if (w == -1) {
							throw new RuntimeException(
									"Can't evaluate the week.");
						}
						result = new Week(w, y);
					} else {
						throw new RuntimeException("Can't evaluate the year.");
					}
				}

			} else {
				throw new RuntimeException("Could not find separator.");
			}

		}
		return result;

	}

	private static int findSeparator(String s) {

		int result = s.indexOf('-');
		if (result == -1) {
			result = s.indexOf(',');
		}
		if (result == -1) {
			result = s.indexOf(' ');
		}
		if (result == -1) {
			result = s.indexOf('.');
		}
		return result;
	}

	private static Year evaluateAsYear(String s) {

		Year result = null;
		try {
			result = Year.parseYear(s);
		} catch (RuntimeException e) {

		}
		return result;

	}

	private static int stringToWeek(String s) {

		int result = -1;
		s = s.replace('W', ' ');
		s = s.trim();
		try {
			result = Integer.parseInt(s);
			if ((result < 1) || (result > LAST_WEEK_IN_YEAR)) {
				result = -1;
			}
		} catch (NumberFormatException e) {

		}
		return result;

	}

}
