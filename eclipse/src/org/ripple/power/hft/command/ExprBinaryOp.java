package org.ripple.power.hft.command;

import org.ripple.power.hft.command.script.RuntimeError;
import org.ripple.power.hft.command.script.ScriptExpr;

public abstract class ExprBinaryOp extends ScriptExpr {

	public ScriptExpr[] operands;

	public ExprBinaryOp(ScriptExpr[] operands, int op) {
		this.operands = operands;
		opType = op;
		if(operands.length > 2) {
			throw new RuntimeError("Too many arguments for StdCalculation: expected 2, got " + operands.length);
		}
	}
	
	public ExprBinaryOp(int op) {
		operands = new ScriptExpr[2];
		opType = op;
	}
}
