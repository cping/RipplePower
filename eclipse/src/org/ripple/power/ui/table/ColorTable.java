package org.ripple.power.ui.table;

import java.awt.Color;
import javax.swing.JTable;
import javax.swing.table.TableModel;

public class ColorTable extends JTable implements RowColorModel {
	private static final long serialVersionUID = -3541176315715641153L;
	RowColorModel model;

	public ColorTable(TableModel model) {
		super(model);
		setDefaultRenderer(Object.class, new RowColorTableCellRenderer());
		setRowColorModel(new OddEvenRowColorModel());
	}

	public void setRowColorModel(RowColorModel model) {
		this.model = model;
	}

	public Color getBackground(int row, boolean selected, JTable table) {
		return model.getBackground(row, selected, this);
	}

	public Color getForeground(int row, boolean selected, JTable table) {
		return model.getForeground(row, selected, this);
	}
}