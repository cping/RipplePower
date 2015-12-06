package org.ripple.power.txns.data;

import org.json.JSONObject;

public class AccountResponse {

	public String result;
	public String address;
	public String parent;
	public double initial_balance;
	public String inception;
	public long ledger_index;
	public String tx_hash;

	public void from(JSONObject obj) {
		if (obj != null) {
			this.result = obj.optString("result");
			JSONObject account = obj.optJSONObject("account");
			if (account != null) {
				this.address = account.optString("address");
				this.parent = account.optString("parent");
				this.inception = account.optString("inception");
				this.tx_hash = account.optString("tx_hash");
				this.initial_balance = account.optDouble("initial_balance");
				this.ledger_index = account.optLong("ledger_index");
			}
		}
	}
}
