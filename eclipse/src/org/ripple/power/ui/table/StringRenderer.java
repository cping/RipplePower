package org.ripple.power.ui.table;

import javax.swing.table.DefaultTableCellRenderer;

import org.ripple.power.ui.graphics.LColor;

public class StringRenderer extends DefaultTableCellRenderer {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StringRenderer(int alignment) {
        super();
        setHorizontalAlignment(alignment);
    }
	
	@Override
	public void setValue(Object value) {
		if (value == null) {
			setText("");
			return;
		}
		String text = (String) value;
		if (text.equalsIgnoreCase("none")) {
			setForeground(LColor.red.darker().darker());
		}else{
			setForeground(LColor.blue.darker().darker());
		}
		setText(text);
	}
}

