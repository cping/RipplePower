package org.ripple.power.ui;

import java.awt.Color;

import javax.swing.JTextArea;

public class RPTextArea extends JTextArea{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RPTextArea(String name){
		super(name);
		setCaretColor(Color.WHITE);
		setBackground(new Color(70, 70, 70));
		setForeground(Color.WHITE);
	}

	public RPTextArea(){
		setCaretColor(Color.WHITE);
		setBackground(new Color(70, 70, 70));
		setForeground(Color.WHITE);
	}

}