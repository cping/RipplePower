package org.ripple.power.txns.data;

import org.json.JSONObject;
import org.ripple.power.txns.Const;

public class OrderData {
	public String engine_result;
	public int engine_result_code;
	public String engine_result_message;
	public String tx_blob;
	public NOR_TxJson tx_json = new NOR_TxJson();

	public ResponseKind getResponseKind() {
		if (0 == engine_result_code) {
			return ResponseKind.Success;
		}
		if (Const.OkResultCodes.contains(engine_result)) {
			return ResponseKind.NonCriticalError;
		}
		return ResponseKind.FatalError;
	}

	public void from(JSONObject obj) {
		if (obj != null) {
			this.engine_result = obj.optString("engine_result");
			this.engine_result_code = obj.optInt("engine_result_code");
			this.engine_result_message = obj.optString("engine_result_message");
			this.tx_blob = obj.optString("tx_blob");
			this.tx_json.from(obj.optJSONObject("tx_json"));
		}
	}
}
