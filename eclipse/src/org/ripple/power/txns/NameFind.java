package org.ripple.power.txns;

import java.util.HashMap;

import org.json.JSONObject;
import org.ripple.power.utils.HttpRequest;

public class NameFind {

	private static HashMap<String, String> _caches = new HashMap<String, String>(
			10);

	public static String getAddress(String name) throws Exception {
		String result = _caches.get(name);
		if (result == null) {
			String jsonResult = HttpRequest.get("https://id.ripple.com/v1/user/"
					+ name).body();
			JSONObject obj = new JSONObject(jsonResult);
			result = obj.getString("address");
		}
		return result;
	}

}
