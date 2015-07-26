package org.ripple.power.txns;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;
import org.ripple.power.utils.HttpRequest;

public class NameFind {

	// test
	// https://id.staging.ripple.com/v1
	private final static String page = "https://id.ripple.com/v1";

	private static HashMap<String, String> _caches = new HashMap<String, String>(
			10);

	public static String getAddress(String name) throws Exception {
		String result = _caches.get(name);
		if (result == null) {
			if (name.startsWith("~")) {
				name = name.substring(1, name.length());
			}
			String web = page + "/user/" + name;
			result = HttpRequest.getHttps(web);
			if (result != null) {
				JSONObject obj = new JSONObject(result);
				result = obj.optString("address", null);
				if (result != null) {
					_caches.put(name, result);
				}
			}
		}
		if (result == null) {
			if (name.startsWith("~")) {
				name = name.substring(1, name.length());
			}
			if (Gateway.getAddress(name) != null) {
				ArrayList<Gateway.Item> items = Gateway.getAddress(name).accounts;
				if (items.size() > 0) {
					result = items.get(0).address;
					if (result != null) {
						_caches.put(name, result);
					}
				}
			}
		}
		return result;
	}

	public static String getName(String address) throws Exception {
		String result = _caches.get(address);
		if (result == null) {
			result = HttpRequest.getHttps(page + "/authinfo?username="
					+ address);
			if (result != null) {
				JSONObject obj = new JSONObject(result);
				result = obj.getString("username");
				if (result != null) {
					_caches.put(address, result);
				}
			}
		}
		return result;
	}
}
