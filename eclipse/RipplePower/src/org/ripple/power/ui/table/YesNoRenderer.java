package org.ripple.power.ui.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.io.Serializable;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

public class YesNoRenderer extends JCheckBox implements TableCellRenderer, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected static Border noFocusBorder = new EmptyBorder(4, 4, 4, 4);

	private Color unselectedForeground;

	private Color unselectedBackground;

	public YesNoRenderer() {
		super();
		setHorizontalAlignment(SwingConstants.CENTER);
		setOpaque(true);
		setBorder(noFocusBorder);
	}

	public void setForeground(Color c) {
		super.setForeground(c);
		unselectedForeground = c;
	}

	public void setBackground(Color c) {
		super.setBackground(c);
		unselectedBackground = c;
	}

	public void updateUI() {
		super.updateUI();
		setForeground(null);
		setBackground(null);
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		if (isSelected) {
			super.setForeground(table.getSelectionForeground());
			super.setBackground(table.getSelectionBackground());
		} else {
			super.setForeground((unselectedForeground != null) ? unselectedForeground : table.getForeground());
			super.setBackground((unselectedBackground != null) ? unselectedBackground : table.getBackground());
		}

		setFont(table.getFont());

		if (hasFocus) {
			setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));

			if (table.isCellEditable(row, column)) {
				super.setForeground(UIManager.getColor("Table.focusCellForeground"));
				super.setBackground(UIManager.getColor("Table.focusCellBackground"));
			}
		} else {
			setBorder(noFocusBorder);
		}
		setValue(value);
		Color back = getBackground();
		boolean colorMatch = (back != null) && (back.equals(table.getBackground())) && table.isOpaque();
		setOpaque(!colorMatch);
		return this;
	}

	public void validate() {
	}

	public void revalidate() {
	}

	public void repaint(long tm, int x, int y, int width, int height) {
	}

	public void repaint(Rectangle r) {
	}

	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		if (propertyName.equals("text")) {
			super.firePropertyChange(propertyName, oldValue, newValue);
		}
	}

	public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
	}

	protected void setValue(Object value) {
		boolean selected = false;
		if (value instanceof Boolean) {
			selected = ((Boolean) value).booleanValue();
		}
		if (value instanceof String) {
			selected = "ok".equalsIgnoreCase((String) value) || "yes".equalsIgnoreCase((String) value)
					|| "true".equalsIgnoreCase((String) value);
		}
		setSelected(selected);
	}

}
