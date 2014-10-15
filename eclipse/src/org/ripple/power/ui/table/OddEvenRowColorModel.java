package org.ripple.power.ui.table;

import java.awt.Color;
import javax.swing.JTable;

public class OddEvenRowColorModel implements RowColorModel {
	Color odd = Color.WHITE;
	Color even = new Color(241, 245, 250);
	Color fore = Color.BLACK;

	public OddEvenRowColorModel() {
	}

	public OddEvenRowColorModel(Color odd, Color even) {
		this.odd = odd;
		this.even = even;
	}

	public Color getBackground(int rowIndex, boolean selected, JTable table) {
		if (selected) {
			return table.getSelectionBackground();
		} else {
			return rowIndex % 2 == 1 ? odd : even;
		}
	}

	public Color getForeground(int row, boolean selected, JTable table) {
		if (selected) {
			return table.getSelectionForeground();
		} else {
			return fore;
		}
	}
}