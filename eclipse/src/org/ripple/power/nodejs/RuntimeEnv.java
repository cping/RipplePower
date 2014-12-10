package org.ripple.power.nodejs;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class RuntimeEnv {
	private static ScriptEngineManager manager;
	private static ScriptEngine engine;

	static {
		manager = new ScriptEngineManager();
		engine = manager.getEngineByName("JavaScript");
	}

	public static ScriptEngine getScriptEngine() {
		return engine;
	}

	public static Invocable getInvocableEngine() {
		return (Invocable) engine;
	}
}
