package org.ripple.power.txns.data;

import org.json.JSONObject;

public class Line {

	public String account;
	public String balance;
	public String currency;
	public String limit;
	public String limit_peer;
	public int quality_in;
	public int quality_out;
	public boolean no_ripple;
	public boolean peer_authorized;

	public double getBalance() {
		return Double.parseDouble(balance);
	}

	public void from(JSONObject obj) {
		if (obj != null) {
			this.no_ripple = obj.optBoolean("no_ripple");
			this.account = obj.optString("account");
			this.balance = obj.optString("balance");
			this.currency = obj.optString("currency");
			this.limit = obj.optString("limit");
			this.limit_peer = obj.optString("limit_peer");
			this.quality_in = obj.optInt("quality_in");
			this.quality_out = obj.optInt("quality_out");
			this.peer_authorized = obj.optBoolean("peer_authorized");
		}
	}
}
