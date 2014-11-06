package org.ripple.power.hft.command.script;

public class RetException extends ScriptException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ScriptObject retValue;

	public RetException(ScriptObject retValue) {
		this.retValue = retValue;
	}
}
