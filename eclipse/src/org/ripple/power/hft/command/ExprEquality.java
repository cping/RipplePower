package org.ripple.power.hft.command;

import org.ripple.power.hft.command.script.Script;
import org.ripple.power.hft.command.script.ScriptContext;
import org.ripple.power.hft.command.script.ScriptExpr;
import org.ripple.power.hft.command.script.ScriptObject;

public class ExprEquality extends ExprBinaryOp {

	public ExprEquality(ScriptExpr[] operands, int op) {
		super(operands, op);
	}
	
	public ExprEquality(int op){
		super(op);
	}

	public ScriptObject eval(ScriptContext context) {
		ScriptObject lVal = operands[0].eval(context), rVal = operands[1].eval(context);
		boolean result = lVal.equals(rVal);
		result = (opType == Script.TT_LNEQ) ? !result : result;
		return (result) ? ScriptObject.FSTRUE : ScriptObject.FSFALSE;
	}
	
	public String toString() {
		switch(opType) {
			case Script.TT_LEQ:
				exprName = "(Equality";break;
			case Script.TT_LNEQ:
				exprName = "(Non-Equality"; break;
		}
		return exprName + ":\n\t" + operands[0] + "\n\t" +operands[1] + "\n)";
	}

}
