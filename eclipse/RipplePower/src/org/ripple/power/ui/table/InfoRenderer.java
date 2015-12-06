package org.ripple.power.ui.table;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.ripple.power.ui.graphics.LColor;
import org.ripple.power.utils.GraphicsUtils;

public class InfoRenderer extends DefaultTableCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InfoRenderer(int alignment) {
		super();
		setHorizontalAlignment(alignment);
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		if (value == null) {
			setText("");
			return this;
		}
		JLabel label = (JLabel) super.getTableCellRendererComponent(table,
				value, isSelected, hasFocus, row, column);
		String text = (String) value;
		table.setRowHeight(80);
		label.setFont(GraphicsUtils.getFont(Font.SANS_SERIF, 0, 14));
		label.setText("<html>" + text + "</html>");
		label.setHorizontalAlignment(getHorizontalAlignment());
		if (hasFocus) {
			if (isSelected) {
				label.setForeground(LColor.red);
			} else {
				label.setForeground(LColor.black);
			}
		} else {
			label.setForeground(LColor.black);
		}
		return label;
	}

}
