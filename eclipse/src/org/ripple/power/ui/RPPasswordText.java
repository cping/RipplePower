package org.ripple.power.ui;

import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.ripple.power.ui.graphics.LColor;

public class RPPasswordText extends JPasswordField{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public RPPasswordText(){
		setCaretColor(LColor.WHITE);
		setBackground(new LColor(70, 70, 70));
		setForeground(LColor.WHITE);
		setHorizontalAlignment(JTextField.LEFT); 
		setAlignmentX(0);
	}

}

