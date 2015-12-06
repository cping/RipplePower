package org.ripple.power.timer;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Hour extends RegularTimer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int FIRST_HOUR_IN_DAY = 0;

	public static final int LAST_HOUR_IN_DAY = 23;

	private Day day;

	private byte hour;

	private long firstMillisecond;

	private long lastMillisecond;

	public Hour() {
		this(new Date());
	}

	public Hour(int hour, Day day) {
		this.hour = (byte) hour;
		this.day = day;
		peg(Calendar.getInstance());
	}

	public Hour(int hour, int day, int month, int year) {
		this(hour, new Day(day, month, year));
	}

	public Hour(Date time) {
		this(time, TimeZone.getDefault(), Locale.getDefault());
	}

	public Hour(Date time, TimeZone zone) {
		this(time, zone, Locale.getDefault());
	}

	public Hour(Date time, TimeZone zone, Locale locale) {
		Calendar calendar = Calendar.getInstance(zone, locale);
		calendar.setTime(time);
		this.hour = (byte) calendar.get(Calendar.HOUR_OF_DAY);
		this.day = new Day(time, zone, locale);
		peg(calendar);
	}

	public int getHour() {
		return this.hour;
	}

	public Day getDay() {
		return this.day;
	}

	public int getYear() {
		return this.day.getYear();
	}

	public int getMonth() {
		return this.day.getMonth();
	}

	public int getDayOfMonth() {
		return this.day.getDayOfMonth();
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
		Hour result;
		if (this.hour != FIRST_HOUR_IN_DAY) {
			result = new Hour(this.hour - 1, this.day);
		} else {
			Day prevDay = (Day) this.day.previous();
			if (prevDay != null) {
				result = new Hour(LAST_HOUR_IN_DAY, prevDay);
			} else {
				result = null;
			}
		}
		return result;
	}

	@Override
	public RegularTimer next() {
		Hour result;
		if (this.hour != LAST_HOUR_IN_DAY) {
			result = new Hour(this.hour + 1, this.day);
		} else {
			Day nextDay = (Day) this.day.next();
			if (nextDay != null) {
				result = new Hour(FIRST_HOUR_IN_DAY, nextDay);
			} else {
				result = null;
			}
		}
		return result;
	}

	@Override
	public long getSerialIndex() {
		return this.day.getSerialIndex() * 24L + this.hour;
	}

	@Override
	public long getFirstMillisecond(Calendar calendar) {
		int year = this.day.getYear();
		int month = this.day.getMonth() - 1;
		int dom = this.day.getDayOfMonth();
		calendar.set(year, month, dom, this.hour, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();
	}

	@Override
	public long getLastMillisecond(Calendar calendar) {
		int year = this.day.getYear();
		int month = this.day.getMonth() - 1;
		int dom = this.day.getDayOfMonth();
		calendar.set(year, month, dom, this.hour, 59, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar.getTimeInMillis();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Hour)) {
			return false;
		}
		Hour that = (Hour) obj;
		if (this.hour != that.hour) {
			return false;
		}
		if (!this.day.equals(that.day)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "[" + this.hour + "," + getDayOfMonth() + "/" + getMonth() + "/"
				+ getYear() + "]";
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 37 * result + this.hour;
		result = 37 * result + this.day.hashCode();
		return result;
	}

	@Override
	public int compareTo(Object o1) {
		int result;
		if (o1 instanceof Hour) {
			Hour h = (Hour) o1;
			result = getDay().compareTo(h.getDay());
			if (result == 0) {
				result = this.hour - h.getHour();
			}
		} else if (o1 instanceof RegularTimer) {
			result = 0;
		} else {
			result = 1;
		}

		return result;
	}

	public static Hour parseHour(String s) {
		Hour result = null;
		s = s.trim();
		String daystr = s.substring(0, Math.min(10, s.length()));
		Day day = Day.parseDay(daystr);
		if (day != null) {
			String hourstr = s.substring(
					Math.min(daystr.length() + 1, s.length()), s.length());
			hourstr = hourstr.trim();
			int hour = Integer.parseInt(hourstr);
			if ((hour >= FIRST_HOUR_IN_DAY) && (hour <= LAST_HOUR_IN_DAY)) {
				result = new Hour(hour, day);
			}
		}

		return result;
	}

}
