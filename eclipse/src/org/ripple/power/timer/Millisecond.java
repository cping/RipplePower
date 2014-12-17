package org.ripple.power.timer;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Millisecond extends RegularTimer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int FIRST_MILLISECOND_IN_SECOND = 0;

	public static final int LAST_MILLISECOND_IN_SECOND = 999;

	private Day day;

	private byte hour;

	private byte minute;

	private byte second;

	private int millisecond;

	private long firstMillisecond;

	public Millisecond() {
		this(new Date());
	}

	public Millisecond(int millisecond, Second second) {
		this.millisecond = millisecond;
		this.second = (byte) second.getSecond();
		this.minute = (byte) second.getMinute().getMinute();
		this.hour = (byte) second.getMinute().getHourValue();
		this.day = second.getMinute().getDay();
		peg(Calendar.getInstance());
	}

	public Millisecond(int millisecond, int second, int minute, int hour,
			int day, int month, int year) {

		this(millisecond, new Second(second, minute, hour, day, month, year));

	}

	public Millisecond(Date time) {
		this(time, TimeZone.getDefault(), Locale.getDefault());
	}

	public Millisecond(Date time, TimeZone zone) {
		this(time, zone, Locale.getDefault());
	}

	public Millisecond(Date time, TimeZone zone, Locale locale) {
		Calendar calendar = Calendar.getInstance(zone, locale);
		calendar.setTime(time);
		this.millisecond = calendar.get(Calendar.MILLISECOND);
		this.second = (byte) calendar.get(Calendar.SECOND);
		this.minute = (byte) calendar.get(Calendar.MINUTE);
		this.hour = (byte) calendar.get(Calendar.HOUR_OF_DAY);
		this.day = new Day(time, zone, locale);
		peg(calendar);
	}

	public Second getSecond() {
		return new Second(this.second, this.minute, this.hour,
				this.day.getDayOfMonth(), this.day.getMonth(),
				this.day.getYear());
	}

	public long getMillisecond() {
		return this.millisecond;
	}

	@Override
	public long getFirstMillisecond() {
		return this.firstMillisecond;
	}

	@Override
	public long getLastMillisecond() {
		return this.firstMillisecond;
	}

	@Override
	public void peg(Calendar calendar) {
		this.firstMillisecond = getFirstMillisecond(calendar);
	}

	@Override
	public RegularTimer previous() {
		RegularTimer result = null;
		if (this.millisecond != FIRST_MILLISECOND_IN_SECOND) {
			result = new Millisecond(this.millisecond - 1, getSecond());
		} else {
			Second previous = (Second) getSecond().previous();
			if (previous != null) {
				result = new Millisecond(LAST_MILLISECOND_IN_SECOND, previous);
			}
		}
		return result;
	}

	@Override
	public RegularTimer next() {
		RegularTimer result = null;
		if (this.millisecond != LAST_MILLISECOND_IN_SECOND) {
			result = new Millisecond(this.millisecond + 1, getSecond());
		} else {
			Second next = (Second) getSecond().next();
			if (next != null) {
				result = new Millisecond(FIRST_MILLISECOND_IN_SECOND, next);
			}
		}
		return result;
	}

	@Override
	public long getSerialIndex() {
		long hourIndex = this.day.getSerialIndex() * 24L + this.hour;
		long minuteIndex = hourIndex * 60L + this.minute;
		long secondIndex = minuteIndex * 60L + this.second;
		return secondIndex * 1000L + this.millisecond;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Millisecond)) {
			return false;
		}
		Millisecond that = (Millisecond) obj;
		if (this.millisecond != that.millisecond) {
			return false;
		}
		if (this.second != that.second) {
			return false;
		}
		if (this.minute != that.minute) {
			return false;
		}
		if (this.hour != that.hour) {
			return false;
		}
		if (!this.day.equals(that.day)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 37 * result + this.millisecond;
		result = 37 * result + getSecond().hashCode();
		return result;
	}

	@Override
	public int compareTo(Object obj) {
		int result;
		long difference;
		if (obj instanceof Millisecond) {
			Millisecond ms = (Millisecond) obj;
			difference = getFirstMillisecond() - ms.getFirstMillisecond();
			if (difference > 0) {
				result = 1;
			} else {
				if (difference < 0) {
					result = -1;
				} else {
					result = 0;
				}
			}
		} else if (obj instanceof RegularTimer) {
			RegularTimer rtp = (RegularTimer) obj;
			final long thisVal = this.getFirstMillisecond();
			final long anotherVal = rtp.getFirstMillisecond();
			result = (thisVal < anotherVal ? -1 : (thisVal == anotherVal ? 0
					: 1));
		} else {
			result = 1;
		}

		return result;
	}

	@Override
	public long getFirstMillisecond(Calendar calendar) {
		int year = this.day.getYear();
		int month = this.day.getMonth() - 1;
		int d = this.day.getDayOfMonth();
		calendar.clear();
		calendar.set(year, month, d, this.hour, this.minute, this.second);
		calendar.set(Calendar.MILLISECOND, this.millisecond);
		return calendar.getTimeInMillis();
	}

	@Override
	public long getLastMillisecond(Calendar calendar) {
		return getFirstMillisecond(calendar);
	}

}
