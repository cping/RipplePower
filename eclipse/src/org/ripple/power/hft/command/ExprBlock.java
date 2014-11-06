package org.ripple.power.hft.command;

import java.util.Enumeration;
import java.util.Vector;

import org.ripple.power.hft.command.script.ScriptException;
import org.ripple.power.hft.command.script.ScriptContext;
import org.ripple.power.hft.command.script.ScriptExpr;
import org.ripple.power.hft.command.script.ScriptObject;

public class ExprBlock extends ScriptExpr {

	private Vector<ScriptExpr> exprSequence;

	public ExprBlock() {
		exprSequence = new Vector<ScriptExpr>(10);
	}

	public void addExpr(ScriptExpr expr) {
		exprSequence.addElement(expr);
	}

	public ScriptObject eval(ScriptContext context) throws ScriptException {
		ScriptObject exprValue = ScriptObject.FSNULL;
		for (Enumeration<ScriptExpr> expr = exprSequence.elements(); expr.hasMoreElements();) {
			exprValue = ((ScriptExpr) expr.nextElement()).eval(context);
		}
		return exprValue;
	}

	public String toString() {
		StringBuffer result = new StringBuffer("(Block:");
		for (Enumeration<ScriptExpr> expr = exprSequence.elements(); expr.hasMoreElements();) {
			result.append("\n\t");
			result.append(((ScriptExpr) expr.nextElement()));
		}
		result.append("\n)");
		return result.toString();
	}

}
