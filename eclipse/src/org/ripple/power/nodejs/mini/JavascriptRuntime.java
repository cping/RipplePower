package org.ripple.power.nodejs.mini;

import java.io.Reader;

public interface JavascriptRuntime {

	public JavascriptObject newMap();

	public void registerGlobal(String name, Object value);

	public Object run(Reader code, String fileName, Object module, Object exports) throws Exception;

	public JavascriptArray toArray(Object array);

	public JavascriptObject toObject(Object object);
	
}
