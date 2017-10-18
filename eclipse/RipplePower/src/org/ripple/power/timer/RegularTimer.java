package org.ripple.power.timer;

import java.lang.reflect.Constructor;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public abstract class RegularTimer implements TimePeriod, MonthConstants {

	public static RegularTimer createInstance(Class<?> c, Date millisecond, TimeZone zone) {
		RegularTimer result = null;
		try {
			Constructor<?> constructor = c.getDeclaredConstructor(new Class[] { Date.class, TimeZone.class });
			result = (RegularTimer) constructor.newInstance(new Object[] { millisecond, zone });
		} catch (Exception e) {

		}
		return result;
	}

	public static Class<?> downsize(Class<?> c) {
		if (c.equals(Year.class)) {
			return Quarter.class;
		} else if (c.equals(Quarter.class)) {
			return Month.class;
		} else if (c.equals(Week.class)) {
			return Week.class;
		} else if (c.equals(Month.class)) {
			return Day.class;
		} else if (c.equals(Day.class)) {
			return Hour.class;
		} else if (c.equals(Hour.class)) {
			return Minute.class;
		} else if (c.equals(Minute.class)) {
			return Second.class;
		} else if (c.equals(Second.class)) {
			return Millisecond.class;
		} else {
			return Millisecond.class;
		}
	}

	public abstract RegularTimer previous();

	public abstract RegularTimer next();

	public abstract long getSerialIndex();

	public static final TimeZone DEFAULT_TIME_ZONE = TimeZone.getDefault();

	public static final Calendar WORKING_CALENDAR = Calendar.getInstance(DEFAULT_TIME_ZONE);

	public abstract void peg(Calendar calendar);

	@Override
	public Date getStart() {
		return new Date(getFirstMillisecond());
	}

	@Override
	public Date getEnd() {
		return new Date(getLastMillisecond());
	}

	public abstract long getFirstMillisecond();

	public long getFirstMillisecond(TimeZone zone) {
		Calendar calendar = Calendar.getInstance(zone);
		return getFirstMillisecond(calendar);
	}

	public abstract long getFirstMillisecond(Calendar calendar);

	public abstract long getLastMillisecond();

	public long getLastMillisecond(TimeZone zone) {
		Calendar calendar = Calendar.getInstance(zone);
		return getLastMillisecond(calendar);
	}

	public abstract long getLastMillisecond(Calendar calendar);

	public long getMiddleMillisecond() {
		long m1 = getFirstMillisecond();
		long m2 = getLastMillisecond();
		return m1 + (m2 - m1) / 2;
	}

	public long getMiddleMillisecond(TimeZone zone) {
		Calendar calendar = Calendar.getInstance(zone);
		long m1 = getFirstMillisecond(calendar);
		long m2 = getLastMillisecond(calendar);
		return m1 + (m2 - m1) / 2;
	}

	public long getMiddleMillisecond(Calendar calendar) {
		long m1 = getFirstMillisecond(calendar);
		long m2 = getLastMillisecond(calendar);
		return m1 + (m2 - m1) / 2;
	}

	public long getMillisecond(TimePeriodAnchor anchor, Calendar calendar) {
		if (anchor.equals(TimePeriodAnchor.START)) {
			return getFirstMillisecond(calendar);
		} else if (anchor.equals(TimePeriodAnchor.MIDDLE)) {
			return getMiddleMillisecond(calendar);
		} else if (anchor.equals(TimePeriodAnchor.END)) {
			return getLastMillisecond(calendar);
		} else {
			throw new IllegalStateException("Unrecognised anchor: " + anchor);
		}
	}

	@Override
	public String toString() {
		return String.valueOf(getStart());
	}

}
