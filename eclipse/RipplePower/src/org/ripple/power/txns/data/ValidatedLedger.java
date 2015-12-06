package org.ripple.power.txns.data;

import org.json.JSONObject;

public class ValidatedLedger {
	public long base_fee_xrp;
	public String hash;
	public int reserve_base_xrp;
	public int reserve_inc_xrp;
	public int seq;
	public int age;

	public void from(JSONObject validated_ledger) {
		if (validated_ledger != null) {
			this.base_fee_xrp = validated_ledger.optLong("base_fee_xrp");
			this.reserve_base_xrp = validated_ledger.optInt("reserve_base_xrp");
			this.age = validated_ledger.optInt("age");
			this.hash = validated_ledger.optString("hash");
			this.reserve_inc_xrp = validated_ledger.optInt("reserve_inc_xrp");
			this.seq = validated_ledger.optInt("seq");
		}
	}
}
