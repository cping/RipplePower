package org.ripple.power.nodejs.mini;

public interface JavascriptObject extends JavascriptMirror {

	public Object get(String key);

	public void put(String key, Object value);
}
