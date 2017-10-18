package org.ripple.power.timer;

import java.util.Calendar;
import java.util.Date;

public class SpreadsheetDate extends SerialDate {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final int serial;

	private final int day;

	private final int month;

	private final int year;

	public SpreadsheetDate(final int day, final int month, final int year) {

		if ((year >= 1900) && (year <= 9999)) {
			this.year = year;
		} else {
			throw new IllegalArgumentException("The 'year' argument must be in range 1900 to 9999.");
		}

		if ((month >= MonthConstants.JANUARY) && (month <= MonthConstants.DECEMBER)) {
			this.month = month;
		} else {
			throw new IllegalArgumentException("The 'month' argument must be in the range 1 to 12.");
		}

		if ((day >= 1) && (day <= SerialDate.lastDayOfMonth(month, year))) {
			this.day = day;
		} else {
			throw new IllegalArgumentException("Invalid 'day' argument.");
		}

		this.serial = calcSerial(day, month, year);

	}

	public SpreadsheetDate(final int serial) {

		if ((serial >= SERIAL_LOWER_BOUND) && (serial <= SERIAL_UPPER_BOUND)) {
			this.serial = serial;
		} else {
			throw new IllegalArgumentException("SpreadsheetDate: Serial must be in range 2 to 2958465.");
		}

		final int days = this.serial - SERIAL_LOWER_BOUND;

		final int overestimatedYYYY = 1900 + (days / 365);
		final int leaps = SerialDate.leapYearCount(overestimatedYYYY);
		final int nonleapdays = days - leaps;

		int underestimatedYYYY = 1900 + (nonleapdays / 365);

		if (underestimatedYYYY == overestimatedYYYY) {
			this.year = underestimatedYYYY;
		} else {
			int ss1 = calcSerial(1, 1, underestimatedYYYY);
			while (ss1 <= this.serial) {
				underestimatedYYYY = underestimatedYYYY + 1;
				ss1 = calcSerial(1, 1, underestimatedYYYY);
			}
			this.year = underestimatedYYYY - 1;
		}

		final int ss2 = calcSerial(1, 1, this.year);

		int[] daysToEndOfPrecedingMonth = AGGREGATE_DAYS_TO_END_OF_PRECEDING_MONTH;

		if (isLeapYear(this.year)) {
			daysToEndOfPrecedingMonth = LEAP_YEAR_AGGREGATE_DAYS_TO_END_OF_PRECEDING_MONTH;
		}

		int mm = 1;
		int sss = ss2 + daysToEndOfPrecedingMonth[mm] - 1;
		while (sss < this.serial) {
			mm = mm + 1;
			sss = ss2 + daysToEndOfPrecedingMonth[mm] - 1;
		}
		this.month = mm - 1;

		this.day = this.serial - ss2 - daysToEndOfPrecedingMonth[this.month] + 1;

	}

	public int toSerial() {
		return this.serial;
	}

	public Date toDate() {
		final Calendar calendar = Calendar.getInstance();
		calendar.set(getYYYY(), getMonth() - 1, getDayOfMonth(), 0, 0, 0);
		return calendar.getTime();
	}

	public int getYYYY() {
		return this.year;
	}

	public int getMonth() {
		return this.month;
	}

	public int getDayOfMonth() {
		return this.day;
	}

	public int getDayOfWeek() {
		return (this.serial + 6) % 7 + 1;
	}

	public boolean equals(final Object object) {

		if (object instanceof SerialDate) {
			final SerialDate s = (SerialDate) object;
			return (s.toSerial() == this.toSerial());
		} else {
			return false;
		}

	}

	public int hashCode() {
		return toSerial();
	}

	public int compare(final SerialDate other) {
		return this.serial - other.toSerial();
	}

	public int compareTo(final Object other) {
		return compare((SerialDate) other);
	}

	public boolean isOn(final SerialDate other) {
		return (this.serial == other.toSerial());
	}

	public boolean isBefore(final SerialDate other) {
		return (this.serial < other.toSerial());
	}

	public boolean isOnOrBefore(final SerialDate other) {
		return (this.serial <= other.toSerial());
	}

	public boolean isAfter(final SerialDate other) {
		return (this.serial > other.toSerial());
	}

	public boolean isOnOrAfter(final SerialDate other) {
		return (this.serial >= other.toSerial());
	}

	public boolean isInRange(final SerialDate d1, final SerialDate d2) {
		return isInRange(d1, d2, SerialDate.INCLUDE_BOTH);
	}

	public boolean isInRange(final SerialDate d1, final SerialDate d2, final int include) {
		final int s1 = d1.toSerial();
		final int s2 = d2.toSerial();
		final int start = Math.min(s1, s2);
		final int end = Math.max(s1, s2);

		final int s = toSerial();
		if (include == SerialDate.INCLUDE_BOTH) {
			return (s >= start && s <= end);
		} else if (include == SerialDate.INCLUDE_FIRST) {
			return (s >= start && s < end);
		} else if (include == SerialDate.INCLUDE_SECOND) {
			return (s > start && s <= end);
		} else {
			return (s > start && s < end);
		}
	}

	private int calcSerial(final int d, final int m, final int y) {
		final int yy = ((y - 1900) * 365) + SerialDate.leapYearCount(y - 1);
		int mm = SerialDate.AGGREGATE_DAYS_TO_END_OF_PRECEDING_MONTH[m];
		if (m > MonthConstants.FEBRUARY) {
			if (SerialDate.isLeapYear(y)) {
				mm = mm + 1;
			}
		}
		final int dd = d;
		return yy + mm + dd + 1;
	}
}
