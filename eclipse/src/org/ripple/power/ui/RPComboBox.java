package org.ripple.power.ui;

import java.awt.Color;

import javax.swing.JComboBox;

public class RPComboBox extends JComboBox<Object>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RPComboBox(){
		setBackground(new Color(70, 70, 70));
		setForeground(Color.WHITE);
	}
	
	public void setItemModel(Object[] args){
		setModel(new javax.swing.DefaultComboBoxModel<Object>(
				args));
	}
}
