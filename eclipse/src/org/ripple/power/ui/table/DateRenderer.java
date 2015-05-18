package org.ripple.power.ui.table;

import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;

public final class DateRenderer extends DefaultTableCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DateRenderer() {
		super();
		setHorizontalAlignment(JLabel.CENTER);
	}

	@Override
	public void setValue(Object value) {
		if (value == null) {
			setText(new String());
			return;
		}
		setText((String) value);
	}
}
