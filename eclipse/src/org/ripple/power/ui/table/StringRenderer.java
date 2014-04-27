package org.ripple.power.ui.table;

import java.awt.Color;

import javax.swing.table.DefaultTableCellRenderer;

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
			setForeground(Color.red);
		}else{
			setForeground(Color.blue);
		}
		setText(text);
	}
}

