package org.ripple.power.ui;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.ripple.power.ui.graphics.LColor;

public class RPLabel extends JLabel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RPLabel(String name) {
		super(name);
		setBackground(new LColor(70, 70, 70));
		setForeground(LColor.white);
	}

	public RPLabel(ImageIcon icon) {
		super(icon);
		setBackground(new LColor(70, 70, 70));
		setForeground(LColor.white);
	}

	public RPLabel() {
		this("");
	}

}
