package org.ripple.power.txns;

import org.json.JSONException;
import org.json.JSONObject;

public class RandomObject {

	public String random;

	public void copyFrom(JSONObject jsonCommandResult) {
		try {
			random = jsonCommandResult.getString("random");
		} catch (JSONException e) {
		}
	}
}
