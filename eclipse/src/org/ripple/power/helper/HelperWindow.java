package org.ripple.power.helper;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class HelperWindow implements WindowListener {

	private static HelperWindow _instance;

	public static HelperWindow get() {
		if (_instance == null) {
			_instance = new HelperWindow();
		}
		return _instance;
	}

	@Override
	public void windowOpened(WindowEvent e) {
			HelperDialog.hideSystem();
	}

	@Override
	public void windowClosing(WindowEvent e) {
		HelperDialog.showSystem();
	}

	@Override
	public void windowClosed(WindowEvent e) {
		HelperDialog.showSystem();
	}

	@Override
	public void windowIconified(WindowEvent e) {

	}

	@Override
	public void windowDeiconified(WindowEvent e) {

	}

	@Override
	public void windowDeactivated(WindowEvent e) {

	}

	@Override
	public void windowActivated(WindowEvent e) {

	}

}
