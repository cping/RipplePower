package org.ripple.power.sjcl;

public class JSCrypt {

	private final static String file = "sjs.js";

	public static String decrypt(String key, String data) {
		JSCall call = JSCall.get(file);
		try {
			return (String) call.function("decrypt", key,data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
