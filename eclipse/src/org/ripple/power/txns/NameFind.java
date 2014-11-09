package org.ripple.power.txns;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;
import org.ripple.power.utils.HttpRequest;

public class NameFind {

	private static HashMap<String, String> _caches = new HashMap<String, String>(
			10);

	public static String getAddress(String name) throws Exception {
		String result = _caches.get(name);
		if (result == null) {
			HttpRequest request = HttpRequest
					.get("https://id.ripple.com/v1/user/" + name);
			if (request.ok()) {
				String jsonResult = request.body();
				JSONObject obj = new JSONObject(jsonResult);
				result = obj.getString("address");
			}
		}
		if (result == null) {
			name = name.replace("~", "");
			if (Gateway.getAddress(name) != null) {
				ArrayList<Gateway.Item> items = Gateway.getAddress(name).accounts;
				if (items.size() > 0) {
					result = items.get(0).address;
				}
			}
		}
		return result;
	}

}
