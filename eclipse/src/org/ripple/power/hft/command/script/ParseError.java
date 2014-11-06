package org.ripple.power.hft.command.script;

public class ParseError extends ScriptException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ParseError (String msg) {
		super(msg);
	}
    public String toString() {
        return "Parse Error:  " + msg;
    }
}
