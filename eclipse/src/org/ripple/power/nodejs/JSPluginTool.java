package org.ripple.power.nodejs;

import java.util.List;


public abstract class JSPluginTool {

	private static String parseScriptName(String name) {
		String scriptName;
		int slash = name.lastIndexOf("/");
		if (slash < 0) {
			scriptName = name.substring(0);
		} else {
			scriptName = name.substring(slash + 1);
		}
		int dot = scriptName.lastIndexOf(".");
		if (dot >= 0) {
			scriptName = scriptName.substring(0, dot);
		}
		return scriptName;
	}

	public void activePlugin(String scriptFile) {
		JSPlugin newPlugin = new SimplePlugin(scriptFile,
				parseScriptName(scriptFile), parseScriptName(scriptFile));
		SimplePluginManager.getInstance().install(newPlugin);
	}

	public List<JSPlugin> getPluginList() {
		return SimplePluginManager.getInstance().listPlugins();
	}

	public void initEnv() {
		JSPluginManager pManager = SimplePluginManager.getInstance();
		JSPlugin system = new SimplePlugin("scripts/system.js", "system",
				"system initialize");
		pManager.install(system);
	}

}
