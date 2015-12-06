package org.ripple.power.ui.table;

import java.awt.Color;
import javax.swing.JTable;

public interface RowColorModel {
	public Color getBackground(int row, boolean selected, JTable table);

	public Color getForeground(int row, boolean selected, JTable table);
}