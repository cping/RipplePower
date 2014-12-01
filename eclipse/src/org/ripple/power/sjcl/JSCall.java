package org.ripple.power.sjcl;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.ripple.power.collection.ArrayMap;
import org.ripple.power.ui.UIRes;

public class JSCall {

	private static ArrayMap _instances = new ArrayMap(10);

	public static JSCall get(String file) {
		synchronized (JSCall.class) {
			JSCall call = (JSCall) _instances.get(file.toLowerCase());
			if (call == null) {
				call = new JSCall(file);
				_instances.put(file.toLowerCase(), call);
			}
			return call;
		}
	}

	private String _fileName;

	private String _fileContext;

	private static ScriptEngine _engine = null;

	public JSCall(String file) {
		this._fileName = file;
	}

	private synchronized void initScript() throws Exception {
		if (_engine == null) {
			ScriptEngineManager factory = new ScriptEngineManager();
			_engine = factory.getEngineByName("js");
			_engine.put("engine", _engine);
			if (_fileContext == null) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(UIRes.getStream(_fileName)));
				StringBuilder sbr = new StringBuilder();
				String line = null;
				for (; (line = reader.readLine()) != null;) {
					line = line.trim();
					if (line.length() > 0) {
						if (!line.startsWith("\\")) {
							sbr.append(line);
							sbr.append("\n");
						}
					}
				}
				reader.close();
				_fileContext = sbr.toString();
			}
			_engine.eval(_fileContext);
		}
	}

	public void resetEngine() {
		_engine = null;
	}

	public void resetScript() {
		_fileContext = null;
	}

	public Object function(String name, Object... par) throws Exception {
		initScript();
		Invocable inv = (Invocable) _engine;
		return inv.invokeFunction(name, par);
	}

	public Object key(String name) throws Exception {
		initScript();
		return _engine.get(name);
	}

	public Object eval(String text) throws Exception {
		initScript();
		return _engine.eval(text);
	}

	public void put(String key, Object o) throws Exception {
		initScript();
		_engine.put(key, o);
	}

	public void setBindings(Bindings bindings, int scope) throws Exception {
		initScript();
		_engine.setBindings(bindings, scope);
	}

	public Bindings getBindings(int scope) throws Exception {
		initScript();
		return _engine.getBindings(scope);
	}

}
