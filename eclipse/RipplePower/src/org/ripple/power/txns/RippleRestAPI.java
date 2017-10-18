package org.ripple.power.txns;

import org.json.JSONObject;
import org.ripple.power.txns.data.ServerStateResponse;
import org.ripple.power.utils.HttpRequest;

public class RippleRestAPI {

	public static String test_url = "https://api.altnet.rippletest.net:5990/";
	private final static String page = "https://api.ripple.com/v1/";

	private static JSONObject open_rest(String url) throws Exception {
		HttpRequest request = HttpRequest.get(url);
		boolean ok = false;
		try {
			ok = request.ok();
		} catch (Throwable ex) {
		}
		if (ok) {
			return new JSONObject(request.body());
		} else {
			String result = HttpRequest.fix_ssl_open(url);
			if (result == null) {
				return null;
			}
			return new JSONObject(result);
		}
	}

	public static JSONObject balances(String address) {
		try {
			String url = page + "accounts/" + address + "/balances";
			return open_rest(url);
		} catch (Exception e) {
			return null;
		}
	}

	public static JSONObject settings(String address) {
		try {
			String url = page + "accounts/" + address + "/settings";
			return open_rest(url);
		} catch (Exception e) {
			return null;
		}
	}

	public static JSONObject server() {
		try {
			String url = page + "server";
			return open_rest(url);
		} catch (Exception e) {
			return null;
		}
	}

	public static JSONObject server_connected() {
		try {
			String url = page + "server/connected";
			return open_rest(url);
		} catch (Exception e) {
			return null;
		}
	}

	public static JSONObject uuid() {
		try {
			String url = page + "uuid";
			return open_rest(url);
		} catch (Exception e) {
			return null;
		}
	}

	public static JSONObject findPaths(String address, String destination, String limit) {
		try {
			String url = page + "accounts/" + address + "/payments/paths/" + destination + "/" + limit;
			return open_rest(url);
		} catch (Exception e) {
			return null;
		}
	}

	public static JSONObject findXRPPaths(String address, String destination) {
		return findPaths(address, destination, "1+XRP");
	}

	public static JSONObject fee() {
		try {
			String url = page + "transaction-fee";
			return open_rest(url);
		} catch (Exception e) {
			return null;
		}
	}

	public static JSONObject payments(String address) {
		try {
			String url = page + "accounts/" + address + "/payments";
			return open_rest(url);
		} catch (Exception e) {
			return null;
		}
	}

	public static ServerStateResponse getServerState() {
		ServerStateResponse state = new ServerStateResponse();
		JSONObject obj = server();
		if (obj != null) {
			state.from(obj);
		}
		return state;
	}
}
