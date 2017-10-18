package org.ripple.power.txns.data;

import org.json.JSONObject;

public class Exchange {

	public double base_amount;
	public double counter_amount;
	public int rate;
	public String base_currency;
	public String base_issuer;
	public String buyer;
	public String counter_currency;
	public String executed_time;
	public long ledger_index;
	public String seller;
	public String taker;
	public String tx_hash;
	public String tx_type;

	public void from(Object obj) {
		if (obj != null && obj instanceof JSONObject) {
			JSONObject result = (JSONObject) obj;
			this.base_amount = result.optDouble("base_amount");
			this.counter_amount = result.optDouble("counter_amount");
			this.rate = result.optInt("rate");
			this.base_currency = result.optString("base_currency");
			this.base_issuer = result.optString("base_issuer");
			this.buyer = result.optString("buyer");
			this.counter_currency = result.optString("counter_currency");
			this.executed_time = result.optString("executed_time");
			this.ledger_index = result.optLong("ledger_index");
			this.seller = result.optString("seller");
			this.taker = result.optString("taker");
			this.tx_hash = result.optString("tx_hash");
			this.tx_type = result.optString("tx_type");
		}
	}
}
