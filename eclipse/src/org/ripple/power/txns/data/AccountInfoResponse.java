package org.ripple.power.txns.data;

import org.json.JSONObject;

public class AccountInfoResponse {
	public int id;
	public String status;
	public String type;
	public AccountResult result = new AccountResult();

	public void from(JSONObject obj) {
		if (obj != null) {
			this.id = obj.optInt("id");
			this.type = obj.optString("type");
			this.status = obj.optString("status");
			this.result.from(obj.optJSONObject("result"));
		}
	}
}
