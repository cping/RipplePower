package org.ripple.power.ui;

import javax.swing.*;

import org.ripple.power.nodejs.JSPlugin;
import org.ripple.power.nodejs.JSPluginTool;
import org.ripple.power.nodejs.SimplePluginManager;
import org.ripple.power.ui.todo.RPTodoUI;

public class RPTodoFrame extends JSPluginTool {

	private RPTodoUI mainFrame;

	public RPTodoFrame(RPTodoUI frame) {
		this.mainFrame = frame;
	}

	public RPTodoUI getUI() {
		return mainFrame;
	}

	public void launch() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				mainFrame.initUI();
			}
		});
	}

	public static void get() {
		RPTodoFrame todo = new RPTodoFrame(new RPTodoUI("Ripple Encrypt Todo"));
		todo.initEnv();
		JSPlugin system = SimplePluginManager.getInstance().getPlugin("system");
		system.putValueToContext("app", todo);
		system.execute("main");
	}
}
