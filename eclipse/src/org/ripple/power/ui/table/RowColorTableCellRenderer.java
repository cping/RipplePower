package org.ripple.power.ui.table;

import java.awt.Component;
import java.awt.Font;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.ripple.power.config.LSystem;
import org.ripple.power.txns.AccountFind;
import org.ripple.power.ui.graphics.LColor;
import org.ripple.power.utils.GraphicsUtils;

public class RowColorTableCellRenderer extends JLabel implements
		TableCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final Font defFont;
	
	private final Font font = GraphicsUtils.getFont(Font.SANS_SERIF,1,14);
	
	public RowColorTableCellRenderer() {
		super();
		setOpaque(true);
		defFont = new Font(getFont().getName(), Font.PLAIN, 14);
		setFont(defFont);
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		if (table instanceof RowColorModel) {
			RowColorModel model = (RowColorModel) table;
			setBackground(model.getBackground(row, isSelected, table));
			setForeground(model.getForeground(row, isSelected, table));
			if(value instanceof String){
				String address = (String)value;
				if(AccountFind.isRippleAddress(address)){
					setFont(font);
				}else{
					setFont(defFont);
				}
			}
		}
		if (value instanceof Icon) {
			setIcon((Icon) value);
		} else {
			setIcon(null);
		}
		setText(value == null ? "" : value.toString());
		return this;
	}
}