package org.ripple.power.ui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JDialog;

import org.ripple.power.helper.Paramaters;

public class JSonLog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int count;

	private static JSonLog instance = null;

	public synchronized static JSonLog get() {
		if (instance == null) {
			instance = new JSonLog();
		}
		if (!instance.isVisible()) {
			instance.setVisible(true);
		}
		return instance;
	}

	public JSonLog() {
		super(Paramaters.getContainer(), "RPC", false);
		setLocation(getInsets().left + 20, getInsets().top + 260);
		setPreferredSize(new Dimension(300, 300));
		setResizable(false);
		setBackground(Color.black);

		lConsole = new JConsole();
		add(lConsole);

		pack();
		setVisible(true);
	}

	private JConsole lConsole;

	public void print(String line) {
		if (lConsole != null) {
			if (count > 2000) {
				lConsole.clear();
				count = 0;
			}
			lConsole.uiprint(line);
			count++;
		}
	}

	public void println() {
		if (lConsole != null) {
			if (count > 2000) {
				lConsole.clear();
				count = 0;
			}
			lConsole.uiprint("\n");
			count++;
		}
	}

	public void println(String line) {
		if (lConsole != null) {
			if (count > 2000) {
				lConsole.clear();
				count = 0;
			}
			lConsole.uiprint(line + "\n");
			count++;
		}
	}
}
