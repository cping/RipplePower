package org.ripple.power.sjcl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class JSObject extends JSValue<HashMap<Object, Object>> implements
		Map<Object, Object> {

	public JSObject() {
		super(JSType.OBJECT, new HashMap<Object, Object>());
	}

	public JSObject(Object value) {
		this();
		put("object_key", value);
	}

	public void clear() {
		getValue().clear();
	}

	public boolean containsKey(final Object key) {
		return getValue().containsKey(key);
	}

	public boolean containsValue(final Object value) {
		return getValue().containsValue(value);
	}

	public Set<Entry<Object, Object>> entrySet() {
		return getValue().entrySet();
	}

	@Override
	public boolean equals(final Object o) {
		return getValue().equals(o);
	}

	public Object get(final Object key) {
		return getValue().get(key);
	}

	@Override
	public int hashCode() {
		return getValue().hashCode();
	}

	public boolean isEmpty() {
		return getValue().isEmpty();
	}

	public Object js(final Object e) {
		if (null == e) {
			return null;
		}
		Object o = e;
		if (!(e instanceof JSValue<?>)) {
			if (e instanceof String) {
				o = new JSString((String) e);
			} else if (e instanceof Number) {
				o = new JSNumber((Number) e);
			} else if (e instanceof Boolean) {
				o = new JSBool((Boolean) e);
			}
		}
		return o;
	}

	public Set<Object> keySet() {
		return getValue().keySet();
	}

	public JSObject put(final Object key, final Object value) {
		getValue().put(new JSString(String.valueOf(key)), js(value));
		return this;
	}

	public void putAll(final Map<? extends Object, ? extends Object> m) {
		getValue().putAll(m);
	}

	public Object remove(final Object key) {
		return getValue().remove(key);
	}

	public int size() {
		return getValue().size();
	}

	public Collection<Object> values() {
		return getValue().values();
	}

}
