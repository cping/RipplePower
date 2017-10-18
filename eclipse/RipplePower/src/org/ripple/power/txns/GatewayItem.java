package org.ripple.power.txns;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class GatewayItem {

	public String name;

	public String account;

	public boolean featured;

	public ArrayList<String> assets = new ArrayList<String>(10);

	public String startdate;

	public JSONObject json;

	public void copyFrom(JSONObject result) {
		this.json = result;
		if (result != null) {
			name = result.optString("name");
			account = result.optString("account");
			featured = result.optBoolean("featured");
			JSONArray arrays = result.optJSONArray("assets");
			if (arrays != null && arrays.length() > 0) {
				for (int i = 0; i < arrays.length(); i++) {
					assets.add(arrays.optString(i));
				}
			}
			startdate = result.optString("start_date");
		}
	}

	@Override
	public String toString() {
		if (json == null) {
			return null;
		}
		return json.toString();
	}
}
