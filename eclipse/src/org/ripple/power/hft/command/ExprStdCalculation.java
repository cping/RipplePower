package org.ripple.power.hft.command;

import org.ripple.power.hft.command.script.RuntimeError;
import org.ripple.power.hft.command.script.Script;
import org.ripple.power.hft.command.script.ScriptContext;
import org.ripple.power.hft.command.script.ScriptExpr;
import org.ripple.power.hft.command.script.ScriptObject;
import org.ripple.power.utils.MathUtils;

public class ExprStdCalculation extends ExprBinaryOp {
	
	public ExprStdCalculation(int op){
		super(op);
	}
	
	public ExprStdCalculation(ScriptExpr[] operands, int op) {
		super(operands, op);
	}
	
	public ScriptObject eval(ScriptContext context) {
		ScriptObject lVal = operands[0].eval(context), rVal = operands[1].eval(context);

		if (lVal.type == ScriptObject.T_INT && rVal.type == ScriptObject.T_INT){
            long lint = lVal.getInt(), rint = rVal.getInt();
            switch(opType) {
	            case Script.TT_PLUS: return new ScriptObject(lint + rint);
	            case Script.TT_MINUS: return new ScriptObject(lint - rint);
	            case Script.TT_MULT: return new ScriptObject(lint * rint);
	            case Script.TT_DIV: return new ScriptObject(lVal.getDouble() / rint);
	            case Script.TT_MOD: return new ScriptObject(lint % rint);
	            case Script.TT_EXP: return new ScriptObject(MathUtils.pow(lint,rint));
	            default: throw new RuntimeError("Unrecognized mixable operator for int&int :" + opType);
            }
        } else if ((lVal.type == ScriptObject.T_DOUBLE || lVal.type == ScriptObject.T_INT) &&
                   (rVal.type == ScriptObject.T_DOUBLE || rVal.type == ScriptObject.T_INT)){
            double ldouble = lVal.getDouble(), rdouble = rVal.getDouble();
            switch(opType) {
	            case Script.TT_PLUS: return new ScriptObject(ldouble + rdouble);
	            case Script.TT_MINUS: return new ScriptObject(ldouble - rdouble);
	            case Script.TT_MULT: return new ScriptObject(ldouble * rdouble);
	            case Script.TT_DIV: return new ScriptObject(ldouble / rdouble);
	            case Script.TT_MOD: return new ScriptObject(ldouble % rdouble);
	            case Script.TT_EXP: return new ScriptObject(Math.pow(ldouble, rdouble));
	            default: throw new RuntimeError("Unrecognized mixable operator for double&double :" + opType);
            }
        } else if (lVal.type == ScriptObject.T_STRING && rVal.type == ScriptObject.T_STRING){
            switch(opType) {
	            case Script.TT_PLUS: return new ScriptObject(lVal.toString()+rVal.toString());
	            default: throw new RuntimeError("Unrecognized mixable operator for string&string :" + opType);
        }
        } else {
        	throw new RuntimeError("Type Mismatch for mixable operator w/ op " + opType);
        }
	}
	public String toString() {
		switch(opType) {
			case Script.TT_PLUS:
				exprName = "(Addition";break;
			case Script.TT_MINUS:
				exprName = "(Substraction"; break;
			case Script.TT_MULT:
				exprName = "(Multiplication"; break;
			case Script.TT_DIV: 
				exprName = "(Division"; break;
			case Script.TT_MOD: 
				exprName = "(Mod"; break;
			case Script.TT_EXP: 
				exprName = "(Power function"; break;
		}
		return exprName + ":\n\t" + operands[0] + "\n\t" + operands[1] + "\n)";
	}

}
