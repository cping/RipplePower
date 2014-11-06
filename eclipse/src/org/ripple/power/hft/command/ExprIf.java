package org.ripple.power.hft.command;

import java.util.Vector;

import org.ripple.power.hft.command.script.ScriptException;
import org.ripple.power.hft.command.script.Script;
import org.ripple.power.hft.command.script.ScriptContext;
import org.ripple.power.hft.command.script.ScriptExpr;
import org.ripple.power.hft.command.script.ScriptObject;

public class ExprIf extends ScriptExpr {

	private ScriptExpr condition;
	private ScriptExpr block;
	
	public Vector<Object> elseifConditions;
	public Vector<Object> elseifBlocks;
	
	public ScriptExpr elseBlock;
	
	public ExprIf(ScriptExpr condition, ScriptExpr block) {
		opType = Script.TT_IF;
		this.condition = condition;
		this.block = block;
		elseifConditions = new Vector<Object>(5);
		elseifBlocks = new Vector<Object>(5);
	}
	
	public ExprIf(ScriptExpr condition, ScriptExpr block, ScriptExpr elseBlock) {
		opType = Script.TT_IF;
		this.condition = condition;
		this.block = block;
		this.elseBlock = elseBlock;
		elseifConditions = new Vector<Object>(5);
		elseifBlocks = new Vector<Object>(5);
	}
	
	public ScriptObject eval(ScriptContext context) throws ScriptException {
		if(condition.eval(context) == ScriptObject.FSTRUE) {
			return this.block.eval(context);
		}
		if(elseifBlocks != null && elseifBlocks.size() > 0) {
			for(int i = 0; i < elseifBlocks.size(); i++) {
				if(((ScriptExpr)elseifConditions.elementAt(i)).eval(context) == ScriptObject.FSTRUE) {
					return ((ScriptExpr)elseifBlocks.elementAt(i)).eval(context);
				}
			}
		}
		if(elseBlock != null) {
			return elseBlock.eval(context);
		}
		return ScriptObject.FSNULL;
	}
	public String toString() {
		StringBuffer result = new StringBuffer("(If: \n\t[condition \n\t");
		result.append(condition);
		result.append("]\n\t[Block \n\t");
		result.append(block);
		result.append("]\n\t");
		if(elseifBlocks != null && elseifBlocks.size() > 0) {
			for(int i = 0; i < elseifBlocks.size(); i++) {
				result.append("[Elseif: \n\t");
				result.append(elseifConditions.elementAt(i));
				result.append("\n\t");
				result.append(elseifBlocks.elementAt(i));
				result.append("\n]");
			}
		}
		if(elseBlock != null) {
			result.append("[Else: \n\t");
			result.append(elseBlock);
			result.append("\n]");
		}
		return result.toString();
	}

}
