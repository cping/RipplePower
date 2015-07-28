package org.ripple.power.hft.bot.ripple.data;

public class ErrorResponse {

	public String error;
	public int error_code;
	public String error_message;
	public int id;
	public Object request;
	public String status;
	public String type;
	boolean isCritical() {
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

}
