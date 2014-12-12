package org.ripple.power.nodejs.mini;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class RhinoObject implements JavascriptObject {

	private final Scriptable	map;

	public RhinoObject(final Scriptable obj) {
		this.map = obj;
	}

	@Override
	public Object get(final String key) {
		return ScriptableObject.getProperty(this.map, key);
	}

	@Override
	public Object getMirror() {
		return this.map;
	}

	@Override
	public void put(final String key, final Object value) {
		ScriptableObject.putProperty(this.map, key, value);
	}

}
