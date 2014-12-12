package org.ripple.power.nodejs.mini;

import org.mozilla.javascript.NativeArray;

public class RhinoArray implements JavascriptArray {

	private final NativeArray	list;

	public RhinoArray(final NativeArray list) {
		this.list = list;
	}

	@Override
	public Object get(final int i) {
		return this.list.get(i);
	}

	@Override
	public Object getMirror() {
		return this.list;
	}

	@Override
	public int length() {
		return this.list.size();
	}

}
