package org.ripple.power.nodejs;

import org.ripple.power.collection.ArrayMap;
import org.ripple.power.ui.UIRes;
import org.ripple.power.utils.FileUtils;

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

	private static RhinoEngine _engine;

	public JSCall(String file) {
		this._fileName = file;
	}

	private synchronized void initScript() throws Exception {
		if (_engine == null) {
			_engine = new RhinoEngine(JSCall.class.getName());
			String src = FileUtils.readAsText(UIRes.getStream(_fileName));
			_engine.eval(_fileName, src);
		}
	}

	public static RhinoEngine getRootEngine() {
		return _engine;
	}

	public void resetEngine() {
		_engine = null;
	}

	public Object function(String name, Object... par) throws Exception {
		initScript();
		return _engine.invoke(name, par);
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
		_engine.addObject(key, o);
	}

	public void remove(String key) throws Exception {
		initScript();
		_engine.removeObject(key);
	}

	public RhinoEngine buildEngine(String name, RhinoEngine prototype) {
		if (null == prototype) {
			prototype = _engine;
		}
		return new RhinoEngine(name, prototype);
	}

	public RhinoEngine buildEngine(String name) {
		return buildEngine(name, null);
	}
}
