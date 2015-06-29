package org.ripple.power.ui;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;


public class BTCPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private BTCTopPanel panel;

	public BTCPanel() {
		super(new BorderLayout());
		setOpaque(true);
		setBackground(UIConfig.background);
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		panel = new BTCTopPanel();
		add(panel);
		repaint();
	}
	
	public void stop(){
		panel.stop();
	}

}
