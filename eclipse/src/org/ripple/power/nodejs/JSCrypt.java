package org.ripple.power.nodejs;

//java8 NashornScript not support sjcl, so added RhinoScriptEngine
public class JSCrypt {

	private final static String file = "sjs.js";

	public static String decrypt(String key, String data) {
		JSCall call = JSCall.get(file);
		try {
			return (String) call.function("decrypt", key, data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String encrypt(String key, String data) {
		JSCall call = JSCall.get(file);
		try {
			return (String) call.function("encrypt", key, data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
