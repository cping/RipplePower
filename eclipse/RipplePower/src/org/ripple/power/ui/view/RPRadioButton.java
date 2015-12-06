package org.ripple.power.ui.view;

import javax.swing.JRadioButton;

import org.ripple.power.ui.UIConfig;
import org.ripple.power.ui.graphics.LColor;

public class RPRadioButton extends JRadioButton {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RPRadioButton() {
		setBackground(UIConfig.dialogbackground);
		setForeground(LColor.WHITE);
	}
}
