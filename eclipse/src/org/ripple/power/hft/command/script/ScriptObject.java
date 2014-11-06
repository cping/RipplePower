package org.ripple.power.hft.command.script;

import java.util.Hashtable;

public class ScriptObject {

	public static final int T_INT = 8001;
	public static final int T_DOUBLE = 8002;
	public static final int T_STRING = 8003;
	public static final int T_FUNC = 8004;
	public static final int T_ARRAY = 8005;
	public static final int T_NULL = 8006;
	public static final int T_UNDEF = 8007;

	public Object value;
	public int type;

	private static final Hashtable<Integer, String> typeStr = new Hashtable<Integer, String>() {
		private static final long serialVersionUID = 1L;

		{
			put(new Integer(T_INT), "Integer");
			put(new Integer(T_DOUBLE), "Double");
			put(new Integer(T_STRING), "String");
			put(new Integer(T_FUNC), "Function");
			put(new Integer(T_ARRAY), "Array");
			put(new Integer(T_NULL), "Null");
			put(new Integer(T_UNDEF), "Undefined");
		}
	};

	public static ScriptObject FSNULL = new ScriptObject(null, T_NULL);
	public static ScriptObject FSUNDEF = new ScriptObject(null, T_UNDEF);
	public static ScriptObject FSTRUE = new ScriptObject(1);
	public static ScriptObject FSFALSE = new ScriptObject(0);

	public ScriptObject(Object value, int type) {
		if (type > 9000) {
			type -= 1000;
		}
		this.type = type;
		this.value = value;
	}

	public ScriptObject(double value) {
		this.type = T_DOUBLE;
		this.value = new Double(value);
	}

	public ScriptObject(long value) {
		this.type = T_INT;
		this.value = new Long(value);
	}

	public ScriptObject(String value) {
		this.type = T_STRING;
		this.value = value;
	}

	public ScriptObject() {

	}

	public long getInt() throws RuntimeError {
		if (type == T_INT) {
			return ((Long) value).longValue();
		} else if (type == T_DOUBLE) {
			return ((Double) value).longValue();
		} else {
			throw new RuntimeError("Type Mismatch: can't convert value to Int");
		}
	}

	public double getDouble() throws RuntimeError {
		if (type == T_INT) {
			return ((Long) value).doubleValue();
		} else if (type == T_DOUBLE) {
			return ((Double) value).doubleValue();
		} else {
			throw new RuntimeError(
					"Type Mismatch: can't convert value to Double");
		}
	}

	public String getString() throws RuntimeError {
		if (type == T_STRING) {
			return (String) value;
		} else {
			throw new RuntimeError("Type Mismatch: value is not a String");
		}
	}

	public String toString() {
		return "(" + typeStr.get(new Integer(type))
				+ (value == null ? ")" : ": " + value.toString() + ")");
	}

	public boolean equals(ScriptObject another) {
		if (this.type == T_NULL || this.type == T_UNDEF) {
			return (another.type == this.type) ? true : false;
		}
		if (another.type == this.type) {
			return this.value.equals(another.value);
		}
		return false;
	}

}
