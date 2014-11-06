package org.ripple.power.hft.command;

import org.ripple.power.hft.command.script.Script;
import org.ripple.power.hft.command.script.ScriptContext;
import org.ripple.power.hft.command.script.ScriptExpr;
import org.ripple.power.hft.command.script.ScriptObject;

public class ExprAssign extends ScriptExpr {

	private ScriptExpr lhand, rhand;
	private String lvar;
	private boolean isGlobal;
	
	public ExprAssign(ScriptExpr lhand, ScriptExpr rhand) {
		opType = Script.TT_EQ;
		this.lhand = lhand;
		this.rhand = rhand;
		isGlobal = false;
	}
	public ExprAssign(String var, ScriptExpr rhand) {
		opType = Script.TT_EQ;
		this.lvar = var;
		this.rhand = rhand;
		isGlobal = false;
	}
	
	public ExprAssign(String var, ScriptExpr rhand, boolean isGlobal) {
		opType = Script.TT_EQ;
		this.lvar = var;
		this.rhand = rhand;
		this.isGlobal = isGlobal;
	}
	
	public ScriptObject eval(ScriptContext context) {
		ScriptObject rhandVal = rhand.eval(context);
		if(isGlobal) {
			context.setGlobalVar(lvar, rhandVal);
		}
		else {
			context.setVar(lvar, rhandVal);
		}
		return rhandVal;
	}
	
	public ScriptExpr getLhand() {
		return lhand;
	}
	
	public void setLhand(ScriptExpr lhand) {
		this.lhand = lhand;
	}
	
	public String toString() {
		return "(Assign: \n\t" + lvar + "\n\t" + rhand + "\n)";
	}


}
