package org.ripple.power.txns.data;

import org.json.JSONObject;

public class AccountResult {
	public AccountData account_data = new AccountData();
	public long ledger_current_index;
	public boolean validated;

	public void from(JSONObject obj) {
		if (obj != null) {
			this.validated = obj.optBoolean("validated");
			this.ledger_current_index = obj.optLong("ledger_current_index");
			this.account_data.from(obj.optJSONObject("account_data"));
		}
	}
}
