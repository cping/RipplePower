package org.ripple.power.ui.view;

import javax.swing.JTextArea;

import org.ripple.power.ui.graphics.LColor;

public class RPTextArea extends JTextArea {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RPTextArea(String name) {
		super(name);
		setCaretColor(LColor.white);
		setBackground(new LColor(70, 70, 70));
		setForeground(LColor.white);
		setEditable(false);
		setLineWrap(true);
	}

	public RPTextArea() {
		this(null);
	}

}