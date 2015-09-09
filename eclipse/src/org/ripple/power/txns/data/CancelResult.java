package org.ripple.power.txns.data;

import org.json.JSONObject;
import org.ripple.power.txns.Const;

public class CancelResult {

	public String engine_result;
	public int engine_result_code;
	public String engine_result_message;
	public String tx_blob;
	public TxJson tx_json = new TxJson();

	public boolean getResultOK() {
		return Const.OkResultCodes.contains(engine_result);
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
