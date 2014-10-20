package org.ripple.power.txns;

import org.json.JSONObject;
import org.ripple.power.utils.HttpRequest;

public class NameFind {

	public static String getAddress(String name) throws Exception {
		HttpRequest request = HttpRequest.get("https://id.ripple.com/v1/user/"
				+ name);
		String jsonResult = request.body();
		JSONObject obj = new JSONObject(jsonResult);
		return obj.getString("address");
	}

}
