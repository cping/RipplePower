package org.ripple.power.txns.data;

import org.ripple.power.txns.Const;

public class OrderData {
	public String engine_result;
	public int engine_result_code;
	public String engine_result_message;
	public String tx_blob;
	public NOR_TxJson tx_json;

	ResponseKind getResponseKind() {

		if (0 == engine_result_code) {
			return ResponseKind.Success;
		}
		if (Const.OkResultCodes.contains(engine_result)) {
			return ResponseKind.NonCriticalError;
		}
		return ResponseKind.FatalError;

	}
}
