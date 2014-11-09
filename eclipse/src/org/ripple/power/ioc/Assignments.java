
package org.ripple.power.ioc;

public class Assignments {

	/**
	 * 分配键值对用
	 */
	private Object keyValue;

	private Object valValue;

	public Assignments(Object key, Object value) {
		this.setKey(key);
		this.setValue(value);
	}

	public Object getKey() {
		return keyValue;
	}

	public String getKeyString() {
		return keyValue == null ? "" : keyValue.toString();
	}

	public void setKey(Object key) {
		this.keyValue = key;
	}

	public String getValueString() {
		return valValue == null ? "" : valValue.toString();
	}

	public Object getValue() {
		return valValue;
	}

	public void setValue(Object value) {
		this.valValue = value;
	}

}
