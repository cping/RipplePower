package org.ripple.power.nodejs;

public interface JavascriptObject  extends JavascriptMirror {

	public Object get(String key);

	public void put(String key, Object value);
}
