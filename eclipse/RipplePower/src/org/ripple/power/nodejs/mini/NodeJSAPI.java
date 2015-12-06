package org.ripple.power.nodejs.mini;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.ripple.power.ui.UIRes;

public class NodeJSAPI {

	private final JavascriptRuntime runtime;
	private final Map<String, Object> loaded;
	private final ReentrantLock lock = new ReentrantLock();

	public NodeJSAPI(final JavascriptRuntime runtime) {
		super();
		this.runtime = runtime;
		this.loaded = new HashMap<String, Object>();
	}

	public Object find_module(final String mod) throws Exception {
		final JavascriptObject m = this.runtime.newMap();
		if (!mod.endsWith(".js")) {
			final URL result = UIRes.getUrl("script/nodejs/" + mod + ".js");
			if (result != null) {
				m.put("id", mod);
				m.put("resolve", mod);
				m.put("filename", result.toString());
				m.put("loaded", this.loaded.containsKey(result.toString()));
				return m.getMirror();
			}
		}
		return null;
	}

	public Object run_module(final Object module) throws Exception {
		this.lock.lock();
		try {
			final JavascriptObject object = this.runtime.toObject(module);
			final String filename = (String) object.get("filename");
			final Object exports = object.get("exports");
			final Object result = this.loaded.get(filename);
			if (result != null) {
				return result;
			}
			this.runtime.run(
					new InputStreamReader(new URL(filename).openStream()),
					filename, module, exports);
			this.loaded.put(filename, exports);
			return exports;
		} finally {
			this.lock.unlock();
		}
	}

	public OutputStream stderr() {
		return System.err;
	}

	public OutputStream stdout() {
		return System.out;
	}

}
