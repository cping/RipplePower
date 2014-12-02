package org.ripple.power.nodejs;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.UniqueTag;
import org.ripple.power.nodejs.RhinoEngine.ContextRunner;

public class RhinoUtils {

	private static Scriptable _standardScope = null;

	public static synchronized final Scriptable buildStandScope() {
		return (Scriptable) runWithCtx(new ContextRunner() {
			public Object run(Context cx) {
				if (null == _standardScope) {
					_standardScope = cx.initStandardObjects();
				}
				Scriptable blankScope = cx.newObject(_standardScope);
				blankScope.setPrototype(_standardScope);
				blankScope.setParentScope(null);
				return blankScope;
			}
		});
	}

	public static final Object runWithCtx(ContextRunner runner) {
		Context cx = null;
		try {
			cx = Context.enter();
			Object res = runner.run(cx);
			return res;
		} finally {
			if (null != cx) {
				Context.exit();
			}
		}
	}

	public static final Object wrapJavaObj(Object javaObj, Scriptable scope) {
		return Context.javaToJS(javaObj, scope);
	}

	public static final Object unwrapJsObj(Object jsObj, Scriptable scope) {
		Object val = jsObj;
		if (val instanceof NativeJavaObject) {
			val = ((NativeJavaObject) val).unwrap();
		} else if (val instanceof NativeArray) {
			NativeArray na = ((NativeArray) val);
			long len = na.getLength();
			Object[] array = new Object[new Long(len).intValue()];
			for (int i = 0; i < array.length; i++) {
				Object obj = na.get(i, scope);
				array[i] = unwrapJsObj(obj, scope);
			}
			val = array;
		} else if (isNullNativeObject(val)) {
			val = null;
		}
		return val;
	}

	public static final RetrieveResult retrieveNative(String varName,
			Scriptable scope) {
		String[] objList = varName.split("\\.");
		Scriptable thisObject = scope;
		for (int i = 0; i < objList.length; i++) {
			if (i < objList.length - 1) {
				Object obj = ScriptableObject.getProperty(thisObject,
						objList[i]);
				if (isNullNativeObject(obj)) {
					return new RetrieveResult(null, null);
				}
				thisObject = (Scriptable) wrapJavaObj(obj, thisObject);
				if (isNullNativeObject(thisObject)) {
					return new RetrieveResult(null, null);
				}
			} else {
				Object jsObj = ScriptableObject.getProperty(thisObject,
						objList[i]);
				return new RetrieveResult(thisObject, jsObj);
			}
		}
		return null;
	}

	private static boolean isNullNativeObject(Object val) {
		if (val instanceof UniqueTag) {
			if (UniqueTag.NOT_FOUND.equals(val)) {
				return true;
			} else if (UniqueTag.NULL_VALUE.equals(val)) {
				return true;
			}
		} else if (val instanceof Undefined) {
			return true;
		}
		return false;
	}

	public static final String buildUUID() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}

	public static String MD5(String source) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] array = md.digest(source.getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
						.substring(1, 3));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public static class RetrieveResult {
		private Scriptable thisObj;
		private Object jsProp;

		private RetrieveResult(Scriptable thisObj, Object jsProp) {
			this.thisObj = thisObj;
			this.jsProp = jsProp;
		}

		public Scriptable getThisObj() {
			return thisObj;
		}

		public Object getJsProp() {
			return jsProp;
		}
	}
}
