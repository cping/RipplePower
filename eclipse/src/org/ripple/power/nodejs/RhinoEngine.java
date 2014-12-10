package org.ripple.power.nodejs;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.ripple.power.nodejs.RhinoUtils.RetrieveResult;

public class RhinoEngine {

	public static class CleanScope {
		public static void clean(ScriptableObject scope) {
			scope.delete("Packages");
			scope.delete("java");
			scope.delete("javax");
			scope.delete("org");
			scope.delete("com");
			scope.delete("edu");
			scope.delete("getClass");
			scope.delete("JavaAdapter");
			scope.delete("JavaImporter");
			scope.delete("Continuation");
			scope.delete("JavaImporter");
			scope.delete("XML");
			scope.delete("XMLList");
			scope.delete("Namespace");
			scope.delete("QName");
		}
	}

	public static interface JSEnvironment {
		Scriptable createScope(Context cx) throws Exception;
	}

	public static interface ContextRunner {
		public Object run(Context cx);
	}

	public static class Prototype extends BaseFunction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public Object call(Context cx, Scriptable scope, Scriptable thisObj,
				Object[] args) {
			if (args.length == 0) {
				return Scriptable.NOT_FOUND;
			} else if (args.length == 1) {
				Scriptable object = (Scriptable) args[0];
				return object.getPrototype();
			} else {
				Scriptable object = (Scriptable) args[0];
				Scriptable prototype = (Scriptable) args[1];
				Scriptable old = object.getPrototype();
				object.setPrototype(prototype);
				return old;
			}
		}

	}

	public class Quit extends BaseFunction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public Object call(Context cx, Scriptable scope, Scriptable thisObj,
				Object[] args) {
			System.exit(0);
			return null;
		}

	}

	private String _engineName;

	private Scriptable _scope;

	public RhinoEngine(String name) {
		this(name, null);
	}

	public RhinoEngine(String name, RhinoEngine prototype) {
		if (null == name) {
			_engineName = RhinoUtils.buildUUID();
		} else {
			_engineName = name;
		}
		this._scope = RhinoUtils.buildStandScope();
		if (null != prototype && this._scope != prototype._scope) {
			this._scope.setPrototype(prototype._scope);
		}
	}

	public String getEngineName() {
		return _engineName;
	}

	public void addObject(final String varName, final Object object) {
		RhinoUtils.runWithCtx(new ContextRunner() {
			public Object run(Context cx) {
				Object wrapper = RhinoUtils.wrapJavaObj(object, _scope);
				ScriptableObject.putProperty(_scope, varName, wrapper);
				return ScriptableObject.getProperty(_scope, varName);
			}
		});
	}
	
	public void removeObject(final String varName) {
		RhinoUtils.runWithCtx(new ContextRunner() {
			public Object run(Context cx) {
				ScriptableObject.deleteProperty(_scope, varName);
				return null;
			}
		});
	}

	public Object get(final String valName) {
		return RhinoUtils.runWithCtx(new ContextRunner() {
			public Object run(Context cx) {
				Object jsObj = RhinoUtils.retrieveNative(valName, _scope)
						.getJsProp();
				return RhinoUtils.unwrapJsObj(jsObj, _scope);
			}
		});
	}

	public Object eval(final String srcName, final String src) {
		Object jsObj = RhinoUtils.runWithCtx(new ContextRunner() {
			public Object run(Context cx) {
				String name = srcName;
				if (null == name) {
					name = RhinoUtils.MD5(src);
				}
				return cx.evaluateString(_scope, src, name, 1, null);
			}
		});
		return RhinoUtils.unwrapJsObj(jsObj, _scope);
	}

	public Object eval(String src) {
		return eval(null, src);
	}

	public Object invoke(final String functionName, final Object[] args) {
		Object jsObj = RhinoUtils.runWithCtx(new ContextRunner() {
			public Object run(Context cx) {
				Object[] funcParams = args;
				if (null == funcParams) {
					funcParams = new Object[] {};
				}
				RetrieveResult thisAndFunc = RhinoUtils.retrieveNative(
						functionName, _scope);
				Function f = (Function) thisAndFunc.getJsProp();
				for (int i = 0; i < funcParams.length; i++) {
					funcParams[i] = RhinoUtils.wrapJavaObj(funcParams[i],
							_scope);
				}
				Object o = f.call(cx, _scope, thisAndFunc.getThisObj(),
						funcParams);
				return o;
			}
		});
		return RhinoUtils.unwrapJsObj(jsObj, _scope);
	}
}
