package org.ripple.power.ui.table;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;

public final class DateRenderer extends DefaultTableCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final GregorianCalendar cal;

	public DateRenderer() {
		super();
		setHorizontalAlignment(JLabel.CENTER);
		cal = new GregorianCalendar();
	}

	@Override
	public void setValue(Object value) {
		if (value == null) {
			setText(new String());
			return;
		}

		if (!(value instanceof Date)) {
			if (value instanceof String) {
				setText((String) value);
			}
			return;
		}
		cal.setTime((Date) value);
		setText(String.format("%02d/%02d/%04d %02d:%02d", cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH),
				cal.get(Calendar.YEAR), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE)));
	}
}
