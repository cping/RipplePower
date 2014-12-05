package org.ripple.power.helper;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import org.ripple.power.config.LSystem;

public class HelperWindow implements WindowListener {

	private static HelperWindow _instance;

	private static ArrayList<Object> _objs = new ArrayList<Object>(10);

	public static ArrayList<Object> list() {
		return new ArrayList<Object>(_objs);
	}

	public static boolean addObject(Object o) {
		return _objs.add(o);
	}

	public static boolean removeObject(Object o) {
		return _objs.remove(o);
	}

	public static HelperWindow get() {
		if (_instance == null) {
			_instance = new HelperWindow();
		}
		return _instance;
	}

	private void update() {
		if (LSystem.applicationMain != null) {
			LSystem.applicationMain.repaint();
			LSystem.applicationMain.getContentPane().revalidate();
			LSystem.applicationMain.getContentPane().repaint();
		}
	}

	@Override
	public void windowOpened(WindowEvent e) {
		_objs.add(e.getSource());
		update();
	}

	@Override
	public void windowClosing(WindowEvent e) {
		_objs.remove(e.getSource());
		update();
	}

	@Override
	public void windowClosed(WindowEvent e) {
		_objs.remove(e.getSource());
		update();
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
