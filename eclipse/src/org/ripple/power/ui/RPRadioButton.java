package org.ripple.power.ui;

import java.awt.Color;

import javax.swing.JRadioButton;

import org.ripple.power.config.LSystem;

public class RPRadioButton extends JRadioButton{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public RPRadioButton(){
		setBackground(LSystem.dialogbackground);
		setForeground(Color.WHITE);
	}
}
