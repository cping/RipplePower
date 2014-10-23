package org.ripple.power.ui;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JMenuItem;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class RPPopMenuItem extends JMenuItem{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public RPPopMenuItem(String name){
		super(name);
		setForeground(new Color(255, 255, 255));
		setBackground(new Color(18, 18, 18));
		setContentAreaFilled(true);
		setFocusPainted(false);
		Border line = BorderFactory.createLineBorder(new Color(80, 80, 80));
		Border empty = new EmptyBorder(4, 4, 4, 4);
		CompoundBorder border = new CompoundBorder(line, empty);
		setBorder(border);
		
		getModel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				ButtonModel model = (ButtonModel) e.getSource();
				if (model.isRollover()) {
					setBackground(new Color(120, 20, 20));
				} else if (model.isArmed() || model.isPressed()) {
					setBackground(new Color(0, 0, 0));
				} else if (model.isSelected()) {
					setBackground(new Color(0, 0, 0));
				} else {
					setBackground(new Color(18, 18, 18));
				}
			}
		});
	}
}
