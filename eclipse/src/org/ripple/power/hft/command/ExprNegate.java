package org.ripple.power.hft.command;

import org.ripple.power.hft.command.script.RuntimeError;
import org.ripple.power.hft.command.script.ScriptException;
import org.ripple.power.hft.command.script.Script;
import org.ripple.power.hft.command.script.ScriptContext;
import org.ripple.power.hft.command.script.ScriptExpr;
import org.ripple.power.hft.command.script.ScriptObject;

public class ExprNegate extends ScriptExpr {

	private ScriptExpr var;
	
	public ExprNegate(ScriptExpr var) {
		opType = Script.TT_MINUS;
		this.var = var;
	}
	
	public ScriptObject eval(ScriptContext context) throws ScriptException {
		ScriptObject val = var.eval(context);
		if (val.type == ScriptObject.T_INT) {
			return new ScriptObject(-val.getInt());
		}
		else if(val.type == ScriptObject.T_DOUBLE) {
			return new ScriptObject(-val.getDouble());
		}
		else {
			throw new RuntimeError("argument illegal for unary -:" + var);
		}
	}
	
	public String toString() {
		return "(Negate:\n\t" + var + "\n\t" + "\n)";
	}
}
