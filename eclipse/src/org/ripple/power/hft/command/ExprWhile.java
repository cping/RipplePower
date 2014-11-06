package org.ripple.power.hft.command;

import org.ripple.power.hft.command.script.ScriptException;
import org.ripple.power.hft.command.script.Script;
import org.ripple.power.hft.command.script.ScriptContext;
import org.ripple.power.hft.command.script.ScriptExpr;
import org.ripple.power.hft.command.script.ScriptObject;


public class ExprWhile extends ScriptExpr {

	private ScriptExpr exitCondition;
	private ScriptExpr loopBlock;
	
	public ExprWhile(ScriptExpr exitCondition, ScriptExpr loopBlock) {
		opType = Script.TT_WHILE;
		this.exitCondition = exitCondition;
		this.loopBlock = loopBlock;
	}
	
	public ScriptObject eval(ScriptContext context) throws ScriptException {
		try {
		ScriptObject condition = exitCondition.eval(context);
		ScriptObject returnVal = ScriptObject.FSNULL;
		
		while (condition == ScriptObject.FSTRUE) {
			returnVal = loopBlock.eval(context);
			condition = exitCondition.eval(context);
		}
		return returnVal;
		
		}
		catch(ScriptException err) {
			throw err;
		}
	}
	
	public String toString() {
		return "(While:\n\t" + exitCondition + "\n\t" + loopBlock + "\n)";
	}

}
