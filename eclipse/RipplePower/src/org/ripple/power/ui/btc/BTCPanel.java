package org.ripple.power.ui.btc;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;

import org.ripple.power.ui.UIConfig;
import org.ripple.power.ui.view.RoundedPanel;

public class BTCPanel extends RoundedPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private BTCCmdPanel panel;

	public BTCPanel() {
		super(new BorderLayout());
		setOpaque(true);
		setBackground(UIConfig.background);
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		panel = new BTCCmdPanel();
		add(panel);
	}

	public void stop() {
		panel.stop();
	}

	public void start() {
		panel.start();
	}

}
