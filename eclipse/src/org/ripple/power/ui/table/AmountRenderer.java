package org.ripple.power.ui.table;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;


public final class AmountRenderer extends DefaultTableCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AmountRenderer() {
		super();
		setHorizontalAlignment(JLabel.RIGHT);
	}

	@Override
	public void setValue(Object value) {
		if (value == null) {
			setText("0.000000");
			return;
		}
		String text = (String) value;

		if (text.charAt(0) == '-' || text.equals("0.000000")
				|| text.equals("0")) {
			setForeground(Color.red);
		}else{
			setForeground(Color.blue);
		}
		setText(text);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		if (isSelected) {
			setForeground(Color.WHITE);
		} else {
			setForeground(Color.BLACK);
		}
		return super.getTableCellRendererComponent(table, value, isSelected,
				hasFocus, row, column);
	}
}
