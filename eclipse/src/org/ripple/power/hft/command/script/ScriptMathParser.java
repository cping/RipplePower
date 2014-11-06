package org.ripple.power.hft.command.script;

import org.address.collection.ArrayList;
import org.address.collection.ArrayMap;
import org.ripple.power.hft.command.ExprBinaryOp;
import org.ripple.power.hft.command.ExprNegate;

public class ScriptMathParser {

	private static final ArrayMap opPrio = new ArrayMap() {{
		// logic calc
		put(new Integer(Script.TT_LOR),new Integer(1)); 
		put(new Integer(Script.TT_LAND),new Integer(2));
		
		// logic cmp
		put(new Integer(Script.TT_LEQ),new Integer(3));
		put(new Integer(Script.TT_LNEQ),new Integer(3));
		put(new Integer(Script.TT_LGR),new Integer(3));
		put(new Integer(Script.TT_LGRE),new Integer(3));
		put(new Integer(Script.TT_LLS),new Integer(3));
		put(new Integer(Script.TT_LLSE),new Integer(3));
		
		// math calc
		put(new Integer(Script.TT_PLUS),new Integer(4));
		put(new Integer(Script.TT_MINUS),new Integer(4));
		put(new Integer(Script.TT_MULT),new Integer(5));
		put(new Integer(Script.TT_DIV),new Integer(5));
		put(new Integer(Script.TT_MOD),new Integer(5));
		put(new Integer(Script.TT_EXP),new Integer(6));
	}};

	private final int parenWeight = 10;
	
	private ArrayList tokens;
	private ArrayList prios;
	
	ScriptMathParser () {
		tokens = new ArrayList(10);
		prios = new ArrayList(16);

	}
	
	private int getPrio(int op){
		return ((Integer)opPrio.get(new Integer(op))).intValue();
	}
	
	public void add(ScriptExpr value) {
		tokens.add(value);
	}
	
	public void add(ScriptExpr op, boolean inParen) {
		int prio = getPrio(op.opType) + (inParen ? parenWeight : 0);
		tokens.add(op);
		
		ArrayList prioObject = (ArrayList) prios.get(prio);
		
		if(prioObject == null) {
			prioObject = new ArrayList(5);
			prios.set(prio,prioObject);
		}
		
		prioObject.add(new Integer(tokens.size()-1));
	}
	
	private ScriptExpr opLeftPop(int start) {
		ScriptExpr value = null;
		for(int i = start-1; i >=0; i--) {
			value = (ScriptExpr) tokens.get(i);
			if(value != null) {
				tokens.set(i,null);
				return value;
			}
		}
		return value;
	}
	
	private ScriptExpr opRightPop(int start) {
		ScriptExpr value = null;
		for(int i = start+1; i < tokens.size(); i++) {
			value = (ScriptExpr) tokens.get(i);
			if(value != null) {
				tokens.set( i,null);
				return value;
			}
		}
		return value;
	}
	public ScriptExpr parse() throws ParseError {
		ScriptExpr finalExpr = ScriptExpr.FSNOP;
		for(int i = prios.size()-1; i > 1; i--) {
			ArrayList ops = (ArrayList) prios.get(i);
			if(ops == null) continue;
			
			int lastIndex = 0;
			for(int j = 0; j < ops.size(); j++) {
				int opIndex = ((Integer) ops.get(j)).intValue();
				ExprBinaryOp op = (ExprBinaryOp) tokens.get(opIndex);
				
				op.operands[0] = opLeftPop(opIndex);
				op.operands[1] = opRightPop(opIndex);
				
				if(op.operands[0] == null) {
					if (op.opType == Script.TT_MINUS) {
						ExprNegate negate = new ExprNegate(op.operands[1]);
						tokens.set(opIndex,negate);
						lastIndex = opIndex;
						finalExpr = negate;
						continue;
					}
					else {
						throw new ParseError("Op " + op + " must have left argument");
					}
				}
				lastIndex = opIndex;
				finalExpr = op;
			}
			finalExpr.index = lastIndex;
		}
		return finalExpr;
	}
}
