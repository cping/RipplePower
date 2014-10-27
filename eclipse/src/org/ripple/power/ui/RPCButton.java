package org.ripple.power.ui;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.ripple.power.ui.graphics.LColor;


class RPCButton extends JButton {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public RPCButton() {
		super();
		setForeground(new LColor(255, 255, 255));
		setBackground(new LColor(18, 18, 18));
		setContentAreaFilled(true);
		setFocusPainted(false);
		Border line = BorderFactory.createLineBorder(new LColor(80, 80, 80));
		Border empty = new EmptyBorder(4, 4, 4, 4);
		CompoundBorder border = new CompoundBorder(line, empty);
		setBorder(border);
		
		getModel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				ButtonModel model = (ButtonModel) e.getSource();
				if (model.isRollover()) {
					setBackground(new LColor(120, 20, 20));
				} else if (model.isArmed() || model.isPressed()) {
					setBackground(new LColor(0, 0, 0));
				} else if (model.isSelected()) {
					setBackground(new LColor(0, 0, 0));
				} else {
					setBackground(new LColor(18, 18, 18));
				}
			}
		});
	}
}