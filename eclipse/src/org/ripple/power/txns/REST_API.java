package org.ripple.power.txns;

import org.json.JSONObject;
import org.ripple.power.utils.HttpRequest;

//does not include payment of class REST API, feel safe low......
public class REST_API {

	private final static String page = "https://api.ripple.com/v1/";

	public static JSONObject balances(String address) {
		try {
			HttpRequest request = HttpRequest.get(page + "accounts/" + address
					+ "/balances");
			if (request.ok()) {
				return new JSONObject(request.body());
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	public static JSONObject settings(String address) {
		try {
			HttpRequest request = HttpRequest.get(page + "accounts/" + address
					+ "/settings");
			if (request.ok()) {
				return new JSONObject(request.body());
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	public static JSONObject server() {
		try {
			HttpRequest request = HttpRequest.get(page + "server");
			if (request.ok()) {
				return new JSONObject(request.body());
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	public static JSONObject server_connected() {
		try {
			HttpRequest request = HttpRequest.get(page + "server/connected");
			if (request.ok()) {
				return new JSONObject(request.body());
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	public static JSONObject uuid() {
		try {
			HttpRequest request = HttpRequest.get(page + "uuid");
			if (request.ok()) {
				return new JSONObject(request.body());
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	public static JSONObject findPaths(String address, String destination,
			String limit) {
		try {
			HttpRequest request = HttpRequest.get(page + "accounts/" + address
					+ "/payments/paths/" + destination + "/" + limit);
			if (request.ok()) {
				return new JSONObject(request.body());
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	public static JSONObject findXRPPaths(String address, String destination) {
		return findPaths(address, destination, "1+XRP");
	}
}
