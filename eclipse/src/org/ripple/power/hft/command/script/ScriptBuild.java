package org.ripple.power.hft.command.script;

import org.address.collection.ArrayMap;

public class ScriptBuild {
	
	public static final ArrayMap vars = new ArrayMap() {{
		// type constants
		put("int",		new ScriptObject(ScriptObject.T_INT));
		put("double",	new ScriptObject(ScriptObject.T_DOUBLE));
		put("string",	new ScriptObject(ScriptObject.T_STRING));
		put("function",	new ScriptObject(ScriptObject.T_FUNC));
		put("array",	new ScriptObject(ScriptObject.T_ARRAY));
		put("undef",	new ScriptObject(ScriptObject.T_UNDEF));
		
		// constants
		put("LN2",		new ScriptObject(6931471805599453D));
		put("LN10",		new ScriptObject(2.302585092994046D));
		put("LOG2E",	new ScriptObject(1.4426950408889634D));
		put("LOG10E",	new ScriptObject(0.4342944819032518D));
		put("SQRT1_2",	new ScriptObject(2.718281828459045D));
		put("SQRT2",	new ScriptObject(1.4142135623730951D));
		put("e",  		new ScriptObject(Math.E));
		put("pi",		new ScriptObject(Math.PI));
		
		// basic functions
		put("abs",		new BuiltinFuncBase("abs") {
			protected double call(double params[]) {
				return Math.abs(params[0]);
			}
		});
		put("ceil",		new BuiltinFuncBase("ceil") {
			protected double call(double params[]) {
				return Math.ceil(params[0]);
			}
		});
		put("floor",	new BuiltinFuncBase("floor") {
			protected double call(double params[]) {
				return Math.floor(params[0]);
			}
		});
		put("sqrt",		new BuiltinFuncBase("sqrt") {
			protected double call(double params[]) {
				return Math.sqrt(params[0]);
			}
		});
		put("sin",		new BuiltinFuncBase("sin") {
			protected double call(double params[]) {
				return Math.sin(params[0]);
			}
		});
		put("cos",		new BuiltinFuncBase("cos") {
			protected double call(double params[]) {
				return Math.cos(params[0]);
			}
		});
		put("tan",		new BuiltinFuncBase("tan") {
			protected double call(double params[]) {
				return Math.tan(params[0]);
			}
		});
		put("toDeg",	new BuiltinFuncBase("toDeg") {
			protected double call(double params[]) {
				return Math.toDegrees(params[0]);
			}
		});
		put("toRad",	new BuiltinFuncBase("toRad") {
			protected double call(double params[]) {
				return Math.toRadians(params[0]);
			}
		});
		
		// extended functions
		put("exp",		new BuiltinFuncBase("exp") {
			protected double call(double params[]) {
				return Math.exp(params[0]);
			}
		});
		put("ln",		new BuiltinFuncBase("ln") {
			protected double call(double params[]) {
				return Math.exp(params[0]);
			}
		});
		put("log",		new BuiltinFuncBase("log") {
			protected double call(double params[]) {
				return Math.log10(params[0]);
			}
		});
		put("asin",		new BuiltinFuncBase("asin") {
			protected double call(double params[]) {
				return Math.asin(params[0]);
			}
		});
		put("acos",		new BuiltinFuncBase("acos") {
			protected double call(double params[]) {
				return Math.acos(params[0]);
			}
		});
		put("atan",		new BuiltinFuncBase("atan") {
			protected double call(double params[]) {
				return Math.atan(params[0]);
			}
		});
		put("atan2",		new BuiltinFuncBase("atan2") {
			protected double call(double params[]) {
				return Math.atan2(params[0], params[1]);
			}
		});
		put("random",		new BuiltinFuncBase("random") {
			protected double call(double params[]) {
				return  Math.random();
			}
		});
		
	}};
}

abstract class BuiltinFuncBase extends ScriptFunction {
		protected final String funcName;
		protected abstract double call(double params[]);
		
		public String toString() {
			return "(Function: " + funcName + " [builtin] )";
		}
		public BuiltinFuncBase(String funcName) {
			super(null, null, null);
			this.funcName = funcName;
			context = null;
		}
		
		public ScriptObject call(ScriptObject params[]) throws RuntimeError {
			double[] vars = new double[params.length];
			for (int i = 0; i < params.length; i++) {
				if(params[i].type == ScriptObject.T_DOUBLE || params[i].type == ScriptObject.T_INT) {
					vars[i] = params[i].getDouble();
				}
				else {
					throw new RuntimeError("builtin math functions only accept int or double");
				}
			}
			return new ScriptObject(call(vars));
		}
}
