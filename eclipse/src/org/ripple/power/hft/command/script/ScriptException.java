package org.ripple.power.hft.command.script;

public class ScriptException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String msg;

	public ScriptException() {
		super((String) null);
	};

	public ScriptException(String message) {
		msg = message;
	}

	public String toString() {
		return msg;
	}
}
