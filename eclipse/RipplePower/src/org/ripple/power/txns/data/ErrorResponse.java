package org.ripple.power.txns.data;

import org.json.JSONObject;

public class ErrorResponse {

	public String error;
	public int error_code;
	public String error_message;
	public int id;
	public Object request;
	public String status;
	public String type;

	public boolean isCritical() {
		// "tooBusy"
		if (8 == error_code || 9 == error_code) {
			return false;
		}
		// "Ripple not synced to Ripple Network"
		if ("noNetwork".equalsIgnoreCase(error) || 6 == error_code) {
			return false;
		}
		// "Fee of 123 exceeds the requested tx limit 100". Unexplained error,
		// need to do some research. So far try to resolve by ignoring.
		if ("highFee".equalsIgnoreCase(error)) {
			return false;
		}
		// "Current ledger is unavailable."
		if ("noCurrent".equalsIgnoreCase(error)) {
			return false;
		}
		return true;
	}

	public void form(JSONObject obj) {
		if (obj != null) {
			this.id = obj.optInt("id");
			this.error = obj.optString("error");
			this.error_message = obj.optString("error_message");
			this.request = obj.opt("request");
			this.status = obj.optString("status");
			this.type = obj.optString("type");
		}
	}

}
