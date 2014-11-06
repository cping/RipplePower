package org.ripple.power.hft.command;

import org.address.collection.ArrayList;
import org.ripple.power.hft.command.script.RuntimeError;
import org.ripple.power.hft.command.script.ScriptException;
import org.ripple.power.hft.command.script.ScriptContext;
import org.ripple.power.hft.command.script.ScriptExpr;
import org.ripple.power.hft.command.script.ScriptFunction;
import org.ripple.power.hft.command.script.ScriptObject;

public class ExprFuncCall extends ScriptExpr {

	private ScriptExpr funcExpr;
	private ArrayList arguments;
	
	public ExprFuncCall (ScriptExpr funcExpr, ArrayList arguments) {
		this.funcExpr = funcExpr;
		this.arguments = arguments;
	}
	
	public ScriptObject eval(ScriptContext context) throws ScriptException {
		ScriptObject func = funcExpr.eval(context);
		ScriptObject params[] = new ScriptObject[arguments.size()];
		if(func.type != ScriptObject.T_FUNC) {
			throw new RuntimeError("Object "+ func + " is not a function");
		}
		for(int i = 0; i < arguments.size(); i++) {
			params[i] = ((ScriptExpr)arguments.get(i)).eval(context);
		}
		ScriptFunction fun =  (ScriptFunction)func;
		return fun.call(params);
	}
	
	public String toString() {
		return "(FuncCall: \n\t" + funcExpr + "\n\t" + arguments + "\n)";
	}

}
