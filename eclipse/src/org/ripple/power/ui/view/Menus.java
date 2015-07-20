package org.ripple.power.ui.view;

import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class Menus extends JMenu {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    public Menus(ActionListener listener, String label, String[]... items) {
        super(label);
        for (String[] item : items) {
            JMenuItem menuItem = new JMenuItem(item[0]);
            menuItem.setActionCommand(item[1]);
            menuItem.addActionListener(listener);
            add(menuItem);
        }
    }
}
