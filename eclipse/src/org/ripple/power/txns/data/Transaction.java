package org.ripple.power.txns.data;

import org.json.JSONObject;

public class Transaction {


	public String hash;
	public String date;
	public long ledger_index;
	public TxJson tx = new TxJson();
	public String meta;
	
	public void from(Object obj) {
		if (obj != null&&obj instanceof JSONObject) {
			JSONObject result = (JSONObject)obj;
			this.hash = result.optString("hash");
			this.date = result.optString("date");
			this.ledger_index = result.optLong("ledger_index");
			tx.from(result.optJSONObject("tx"));
			this.meta = result.optString("meta");
		}
	}


}
