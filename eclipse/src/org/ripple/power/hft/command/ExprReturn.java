package org.ripple.power.hft.command;

import org.ripple.power.hft.command.script.RetException;
import org.ripple.power.hft.command.script.RuntimeError;
import org.ripple.power.hft.command.script.ScriptException;
import org.ripple.power.hft.command.script.Script;
import org.ripple.power.hft.command.script.ScriptContext;
import org.ripple.power.hft.command.script.ScriptExpr;
import org.ripple.power.hft.command.script.ScriptObject;

public class ExprReturn extends ScriptExpr {

	private ScriptExpr returnExpr;
	
	public ExprReturn(ScriptExpr returnExpr) {
		opType = Script.TT_RETURN;
		this.returnExpr = returnExpr;
	}
	
	public ScriptObject eval(ScriptContext context) throws ScriptException {
		ScriptObject returnVal = ScriptObject.FSNULL;
		try {
			returnVal = returnExpr.eval(context);
		}
		catch(RuntimeError err) {
			throw err;
		}
		throw new RetException(returnVal);
	}
	public String toString() {
		return "(Return:\n\t" + returnExpr + "\n)";
	}

}
