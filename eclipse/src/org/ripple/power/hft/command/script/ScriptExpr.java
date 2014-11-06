package org.ripple.power.hft.command.script;


public abstract class ScriptExpr {

	public int index;
	
	public int opType;
	
	protected static String exprName;
	
	public static final ScriptExpr FSNOP = new ScriptExpr(Script.TT_EOL) {
		public ScriptObject eval(ScriptContext context) {
			return ScriptObject.FSNULL;
		}
		public String toString () {
			return "(No Operation)";
		}
	};
	
	
	public abstract String toString();
	public abstract ScriptObject eval(ScriptContext context) throws ScriptException;
	public ScriptExpr (int opType) {
		this.opType = opType;
	}
	public ScriptExpr () {
	}
}
