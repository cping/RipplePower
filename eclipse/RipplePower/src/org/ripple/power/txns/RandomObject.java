package org.ripple.power.txns;

import org.json.JSONObject;

public class RandomObject {

	public String random;

	public void copyFrom(JSONObject jsonCommandResult) {
		random = jsonCommandResult.getString("random");
	}
}
