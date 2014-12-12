package org.ripple.power.nodejs.mini;

import java.io.Reader;
import java.io.StringReader;

import org.ripple.power.ui.UIRes;
import org.ripple.power.utils.FileUtils;

public class NodeJSRuntime {

	private final JavascriptRuntime runtime;

	public NodeJSRuntime(final JavascriptRuntime runtime) throws Exception {
		this.runtime = runtime;
		runtime.registerGlobal("nodejs", new NodeJSAPI(runtime));
		runtime.registerGlobal("BufferUtil", new BufferUtil(runtime));
		final String code = FileUtils.readAsText(UIRes
				.getStream("script/nodejs/nodejs_global.js"));
		run(code, "nodejs_global.js");
	}

	public Object run(final Reader code, final String fileName)
			throws Exception {
		return this.runtime.run(code, fileName, null, null);
	}

	public Object run(final String code, final String fileName)
			throws Exception {
		return this.runtime.run(new StringReader(code), fileName, null, null);
	}
}
