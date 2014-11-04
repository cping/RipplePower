package org.ripple.power.ui;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;


import org.ripple.power.config.LSystem;
import org.ripple.power.ui.graphics.geom.Point;

public class RPJSonLog {

	private int count;

	private static RPJSonLog instance = null;

	public synchronized static RPJSonLog get() {
		if (instance == null) {
			instance = new RPJSonLog();
		}else if(instance.isClose()){
			instance._tool.close();
			instance = new RPJSonLog();
		}
		return instance;
	}

	private RPPushTool _tool;
	
	public boolean isClose(){
		return _tool.isClose();
	}
	
	public void setVisible(boolean v) {
		_tool.setVisible(v);
	}

	public boolean isVisible() {
		return _tool.isVisible();
	}
	
	public RPJSonLog() {
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(
				LSystem.applicationMain.getGraphicsConfiguration());
		Dimension panSize = new Dimension(180, 220);
		lConsole = new JConsole();
		lConsole.setPreferredSize(panSize);
		lConsole.setSize(panSize);
		_tool = RPPushTool
				.pop(new Point(20, size.getHeight()),
						(int) (screenInsets.bottom + lConsole.getHeight() + (RPPushTool.TITLE_SIZE)+260),
						"RPC",lConsole);
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
