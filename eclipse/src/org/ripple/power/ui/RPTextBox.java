package org.ripple.power.ui;

import javax.swing.JTextField;

import org.ripple.power.ui.graphics.LColor;

public class RPTextBox extends JTextField{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public RPTextBox(){
		setCaretColor(LColor.WHITE);
		setBackground(new LColor(70, 70, 70));
		setForeground(LColor.WHITE);
		setHorizontalAlignment(JTextField.LEFT); 
		setAlignmentX(0);
	}

}
