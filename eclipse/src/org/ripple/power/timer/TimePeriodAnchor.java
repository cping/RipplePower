package org.ripple.power.timer;

import java.io.ObjectStreamException;
import java.io.Serializable;

public class TimePeriodAnchor implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final TimePeriodAnchor START = new TimePeriodAnchor(
			"TimePeriodAnchor.START");

	public static final TimePeriodAnchor MIDDLE = new TimePeriodAnchor(
			"TimePeriodAnchor.MIDDLE");

	public static final TimePeriodAnchor END = new TimePeriodAnchor(
			"TimePeriodAnchor.END");

	private String name;

	private TimePeriodAnchor(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}
		if (!(obj instanceof TimePeriodAnchor)) {
			return false;
		}

		TimePeriodAnchor position = (TimePeriodAnchor) obj;
		if (!this.name.equals(position.name)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return this.name.hashCode();
	}

	private Object readResolve() throws ObjectStreamException {
		if (this.equals(TimePeriodAnchor.START)) {
			return TimePeriodAnchor.START;
		} else if (this.equals(TimePeriodAnchor.MIDDLE)) {
			return TimePeriodAnchor.MIDDLE;
		} else if (this.equals(TimePeriodAnchor.END)) {
			return TimePeriodAnchor.END;
		}
		return null;
	}

}
