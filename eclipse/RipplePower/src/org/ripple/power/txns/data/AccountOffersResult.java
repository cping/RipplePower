package org.ripple.power.txns.data;

import org.json.JSONObject;

public class AccountOffersResult {

	public AccountOffersCurrency base = new AccountOffersCurrency();
	public AccountOffersCurrency counter = new AccountOffersCurrency();
	public String type;
	public double rate;
	public String counterparty;
	public String time;
	public String txHash;
	public long ledgerIndex;

	public void from(JSONObject obj) {
		if (obj != null) {
			this.base.from(obj.opt("base"));
			this.counter.from(obj.opt("counter"));
			this.type = obj.optString("type");
			this.rate = obj.optDouble("rate");
			this.counterparty = obj.optString("counterparty");
			this.time = obj.optString("time");
			this.txHash = obj.optString("txHash");
			this.ledgerIndex = obj.optLong("ledgerIndex");
		}
	}

}
