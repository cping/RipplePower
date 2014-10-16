package org.ripple.power.txns;

import org.json.JSONException;
import org.json.JSONObject;

public class TrustLine {

	public String otherAccount;
	public String balance;
	public String currency;
	public String limit;
	public String limit_peer;
	public long quality_in;
	public long quality_out;

	public void copyFrom(JSONObject jsonTrustLine) {
		try {
			otherAccount = jsonTrustLine.getString("account");
			balance = jsonTrustLine.getString("balance");
			currency = jsonTrustLine.getString("currency");
			limit = jsonTrustLine.getString("limit");
			limit_peer = jsonTrustLine.getString("limit_peer");
			quality_in = jsonTrustLine.getLong("quality_in");
			quality_out = jsonTrustLine.getLong("quality_out");
		} catch (JSONException e) {
		}
	}

}
