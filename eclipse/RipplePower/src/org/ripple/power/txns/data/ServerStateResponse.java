package org.ripple.power.txns.data;

import org.json.JSONObject;
import org.ripple.power.txns.Const;

public class ServerStateResponse {

	public Result result = new Result();
	public String success;
	public String rippled_server_url;

	public double getLastFee() {
		State s = result.state;
		double fee = (double) s.validated_ledger.base_fee_xrp * s.load_factor
				/ s.load_base;
		return fee / Const.DROPS_IN_XRP;
	}

	public void from(JSONObject obj) {
		if (obj != null) {
			success =  obj.optString("success");
			rippled_server_url =  obj.optString("rippled_server_url");
			JSONObject rippled_server_status = obj.optJSONObject("rippled_server_status");
			result.state.from(rippled_server_status);
		}
	}

}
