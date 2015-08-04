package org.ripple.power.txns.data;

import org.json.JSONObject;

public class TransactionStats {

	public String time;
	public int AccountSet;
	public int Payment;
	public int TrustSet;
	public int OfferCreate;
	public int OfferCancel;

	public void from(JSONObject obj) {
		if (obj != null) {
			this.time = obj.optString("time");
			this.OfferCancel = obj.optInt("OfferCancel");
			this.OfferCreate = obj.optInt("OfferCreate");
			this.TrustSet = obj.optInt("TrustSet");
			this.Payment = obj.optInt("Payment");
			this.AccountSet = obj.optInt("AccountSet");
		}
	}
}
