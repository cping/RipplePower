package org.ripple.power.hft.command;

import org.ripple.power.hft.command.script.RuntimeError;
import org.ripple.power.hft.command.script.Script;
import org.ripple.power.hft.command.script.ScriptContext;
import org.ripple.power.hft.command.script.ScriptExpr;
import org.ripple.power.hft.command.script.ScriptObject;

public class ExprStdComparison extends ExprBinaryOp {
	
	public ExprStdComparison(ScriptExpr[] operands, int op) {
		super(operands, op);
	}
	
	public ExprStdComparison(int op) {
		super(op);
	}

	public ScriptObject eval(ScriptContext context) {
		ScriptObject lVal = operands[0].eval(context), rVal = operands[1].eval(context);

		if((lVal.type == ScriptObject.T_INT || lVal.type == ScriptObject.T_DOUBLE) &&
		   (rVal.type == ScriptObject.T_INT || rVal.type == ScriptObject.T_DOUBLE)) {
			
			double ldouble = lVal.getDouble(), rdouble = rVal.getDouble();
			switch(opType) {
	            case Script.TT_LLS: return (ldouble < rdouble) ? ScriptObject.FSTRUE : ScriptObject.FSFALSE;
	            case Script.TT_LLSE: return (ldouble <= rdouble) ? ScriptObject.FSTRUE : ScriptObject.FSFALSE;
	            case Script.TT_LGR: return (ldouble > rdouble) ? ScriptObject.FSTRUE : ScriptObject.FSFALSE;
	            case Script.TT_DIV: return (ldouble >= rdouble) ? ScriptObject.FSTRUE : ScriptObject.FSFALSE;
	            default: throw new RuntimeError("Unrecognized mixable comparison for numbers: " + opType);
			}
		} else if(lVal.type == ScriptObject.T_STRING || rVal.type == ScriptObject.T_STRING) {
			int condition = lVal.toString().compareTo(rVal.toString());
			switch(opType) {
	            case Script.TT_LLS: return (condition < 0) ? ScriptObject.FSTRUE : ScriptObject.FSFALSE;
	            case Script.TT_LLSE: return (condition <= 0) ? ScriptObject.FSTRUE : ScriptObject.FSFALSE;
	            case Script.TT_LGR: return (condition > 0) ? ScriptObject.FSTRUE : ScriptObject.FSFALSE;
	            case Script.TT_DIV: return (condition >= 0) ? ScriptObject.FSTRUE : ScriptObject.FSFALSE;
	            default: throw new RuntimeError("Unrecognized mixable comparison for string&string: " + opType);
			}
        } else {
        	throw new RuntimeError("Type Mismatch for mixable comparison w/ op " + opType);
        }
	}
	
	public String toString() {
		switch(opType) {
			case Script.TT_LLS:
				exprName = "(Less";break;
			case Script.TT_LLSE:
				exprName = "(LessEqual"; break;
			case Script.TT_LGR:
				exprName = "(Larger"; break;
			case Script.TT_LGRE: 
				exprName = "(LargerEqual"; break;
		}
		return exprName + ":\n\t" + operands[0] + "\n\t" + operands[1] + "\n)";
	}

}
