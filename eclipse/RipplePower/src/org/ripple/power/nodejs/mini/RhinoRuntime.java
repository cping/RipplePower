package org.ripple.power.nodejs.mini;

import java.io.Reader;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class RhinoRuntime implements JavascriptRuntime {

	final Context jsContext = Context.enter();
	private final ScriptableObject global;

	public RhinoRuntime() {
		this.global = this.jsContext.initStandardObjects();
	}

	private Object convert(final Object val) {
		final Object list = toArray(val);
		if (list != null) {
			return list;
		}
		return val;
	}

	@Override
	public JavascriptObject newMap() {
		return new RhinoObject(this.jsContext.newObject(this.global));
	}

	@Override
	public void registerGlobal(final String name, final Object value) {
		ScriptableObject.putProperty(this.global, name, value);
	}

	@Override
	public Object run(final Reader code, final String fileName,
			final Object module, final Object export) throws Exception {
		final Scriptable object = this.jsContext.newObject(this.global);
		object.setPrototype(this.global);
		object.setParentScope(null);
		ScriptableObject.putProperty(object, "module", module);
		ScriptableObject.putProperty(object, "exports", export);
		ScriptableObject.putProperty(object, "global", this.global);
		return convert(this.jsContext.evaluateReader(object, code,
				fileName != null ? fileName : "<run>", 1, null));
	}

	@Override
	public JavascriptArray toArray(final Object val) {
		if (val instanceof NativeArray) {
			return new RhinoArray((NativeArray) val);
		}
		return null;
	}

	@Override
	public JavascriptObject toObject(final Object object) {
		return new RhinoObject((Scriptable) object);
	}

}
