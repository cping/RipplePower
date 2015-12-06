package org.ripple.power.txns.data;

import org.json.JSONObject;

public class CancelOrderResponse {

	public CancelResult result;
	public String status;
	public String type;
	public int id;

	public void from(JSONObject obj) {
		if (obj != null) {
			this.id = obj.optInt("id");
			this.type = obj.optString("type");
			this.status = obj.optString("status");
			this.result.from(obj.optJSONObject("result"));
		}
	}

}
