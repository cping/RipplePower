package org.ripple.power.helper;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

public class HelperWindow implements WindowListener {

	private static HelperWindow _instance;

	private static ArrayList<Object> _objs = new ArrayList<Object>(10);

	public static HelperWindow get() {
		if (_instance == null) {
			_instance = new HelperWindow();
		}
		return _instance;
	}

	@Override
	public void windowOpened(WindowEvent e) {
		if (!_objs.contains(e.getSource())) {
			_objs.add(e.getSource());
		}
		HelperDialog.hideSystem();
	}

	@Override
	public void windowClosing(WindowEvent e) {
		if (_objs.contains(e.getSource())) {
			_objs.remove(e.getSource());
		}
		HelperDialog.showSystem();
	}

	@Override
	public void windowClosed(WindowEvent e) {
		if (_objs.contains(e.getSource())) {
			_objs.remove(e.getSource());
		}
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
