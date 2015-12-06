package org.ripple.power.txns.data;

import org.json.JSONObject;

public class NewOrderResponse {
	public OrderData result = new OrderData();
	public String status;
	public String type;
	public int id;
	
	public void from(JSONObject obj) {
		if (obj != null) {
			this.id = obj.optInt("id");
            this.status = obj.optString("status");
            this.type = obj.optString("type");
            this.result.from(obj.optJSONObject("result"));
		}
	}
	
}
