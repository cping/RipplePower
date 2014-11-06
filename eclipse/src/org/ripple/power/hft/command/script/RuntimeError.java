package org.ripple.power.hft.command.script;

public class RuntimeError extends ScriptException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RuntimeError(String msg) {
		super(msg);
	}

	public String toString() {
        return "Runtime error:  " + msg;
    }
}
