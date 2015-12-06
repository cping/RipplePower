package org.ripple.power.nodejs;

import org.json.JSONObject;

public class JSON {
	JSONObject defaults = new JSONObject();

	public JSON() {
		defaults.put("v", 1);
		defaults.put("iter", 1000);
		defaults.put("ks", 128);
		defaults.put("ts", 64);
		defaults.put("mode", "ccm");
		defaults.put("adata", "");
		defaults.put("cipher", "aes");
	}

}
