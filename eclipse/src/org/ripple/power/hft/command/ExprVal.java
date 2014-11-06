package org.ripple.power.hft.command;

import org.ripple.power.hft.command.script.ScriptContext;
import org.ripple.power.hft.command.script.ScriptExpr;
import org.ripple.power.hft.command.script.ScriptObject;

public class ExprVal extends ScriptExpr {

	protected ScriptObject val;
	protected String valKey;
	
	public ExprVal(ScriptObject val) {
		this.val = val;
	}
	public ExprVal(String key) {
		this.valKey = key;
	}

	public String toString() {
		if(val != null) {
			return "(Val:\t" + val + ")";
		}
		else {
			return "(Val:\t" + valKey + ")";
		}
	}

	public ScriptObject getVal() {
		return val;
	}
	
	public String getVarName() {
		return valKey;
	}
	
	public ScriptObject eval(ScriptContext context) {
		if(val != null) {
			return val;
		}
		else if (valKey != null) {
			return context.getVar(valKey);
		}
		return ScriptObject.FSNULL;
	}

}
