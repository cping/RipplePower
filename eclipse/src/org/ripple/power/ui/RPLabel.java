package org.ripple.power.ui;

import java.awt.Color;

import javax.swing.JLabel;

public class RPLabel extends JLabel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RPLabel(String name){
		super(name);
		setBackground(new Color(70, 70, 70));
		setForeground(Color.WHITE);
	}
	
	public RPLabel(){
		setBackground(new Color(70, 70, 70));
		setForeground(Color.WHITE);
	}

}
