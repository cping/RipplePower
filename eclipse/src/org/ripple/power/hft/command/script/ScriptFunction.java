package org.ripple.power.hft.command.script;

import java.util.Hashtable;

import org.address.collection.ArrayList;

public class ScriptFunction extends ScriptObject {

	public ScriptExpr body;
	public ArrayList paramNames;
	protected ScriptContext context;

	public Hashtable<?, ?> localVars;

	public ScriptFunction(ScriptContext parent) {
		super(null, T_FUNC);
		paramNames = new ArrayList(4);
		context = new ScriptContext(parent);
	}

	public ScriptFunction(ScriptExpr body, ArrayList paramNames, ScriptContext parent) {
		super(null, T_FUNC);
		this.body = body;
		this.paramNames = paramNames;
		context = new ScriptContext(parent);
	}

	public ScriptObject call(ScriptObject[] params) throws ScriptException {
		for (int i = 0; i < params.length; i++) {
			String name = (String) paramNames.get(i);
			ScriptObject value = (ScriptObject) params[i];
			context.setVar(name, value);
		}
		try {
			return body.eval(context);
		} catch (RetException retException) {
			return retException.retValue;
		} catch (ScriptException generalError) {
			throw generalError;
		}
	}

	public String toString() {
		return "(Function: " + paramNames + ")";
	}
}
