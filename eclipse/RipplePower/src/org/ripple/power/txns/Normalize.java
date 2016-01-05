package org.ripple.power.txns;

import org.json.JSONObject;

public class Normalize {

	public double amount;

	public double converted;

	public double rate;

	public JSONObject json;

	public void copyFrom(JSONObject result) {
		this.json = result;
		if (result != null) {
			amount = json.optDouble("amount");
			converted = json.optDouble("converted");
			rate = json.optDouble("rate");
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
