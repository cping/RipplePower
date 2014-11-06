package org.ripple.power.hft.command;

import org.ripple.power.hft.command.script.RuntimeError;
import org.ripple.power.hft.command.script.Script;
import org.ripple.power.hft.command.script.ScriptContext;
import org.ripple.power.hft.command.script.ScriptExpr;
import org.ripple.power.hft.command.script.ScriptObject;

public class ExprLogic extends ExprBinaryOp {
	
	public ExprLogic(ScriptExpr[] operands, int op) {
		super(operands, op);
	}
	
	public ExprLogic(int op) {
		super(op);
	}

	public ScriptObject eval(ScriptContext context) {
		ScriptObject lVal = operands[0].eval(context), rVal = operands[1].eval(context);
		
		if (lVal.type == ScriptObject.T_INT && rVal.type == ScriptObject.T_INT){
			boolean lb = lVal.getInt()!=0, rb = rVal.getInt()!=0;
			switch(opType) {
            case Script.TT_LAND: return (lb && rb) ? ScriptObject.FSTRUE : ScriptObject.FSFALSE;
            case Script.TT_LOR: return (lb || rb) ? ScriptObject.FSTRUE : ScriptObject.FSFALSE;
            default: throw new RuntimeError("Unrecognized  logical operator for int&int :" + opType);
			}
		}
		else {
        	throw new RuntimeError("Type Mismatch for mixable operator w/ op " + opType);
        }
	}
	
	public String toString() {
		switch(opType) {
			case Script.TT_LAND:
				exprName = "(AND";break;
			case Script.TT_LOR:
				exprName = "(OR"; break;
		}
		return exprName + ":\n\t" + operands[0]+ "\n\t" + operands[1] + "\n)";
	}

}
