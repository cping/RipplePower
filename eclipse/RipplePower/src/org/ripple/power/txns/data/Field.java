package org.ripple.power.txns.data;

import org.json.JSONObject;

public class Field {

	public long Flags;
	public long Sequence;
	public Take Balance = new Take();
	public String Account;
	public String PreviousTxnID;
	public int OwnerCount;
	public Take LowLimit = new Take();
	public Take HighLimit = new Take();
	public String RootIndex;
	public String HighNode;
	public String LowNode;

	public void from(JSONObject obj) {
		if (obj != null) {
			this.Flags = obj.optLong("Flags");
			this.RootIndex = obj.optString("RootIndex");
			this.PreviousTxnID = obj.optString("PreviousTxnID");
			this.LowNode = obj.optString("LowNode");
			this.HighNode = obj.optString("HighNode");
			this.Sequence = obj.optLong("Sequence");
			this.Balance.from(obj.opt("Balance"));
			this.Account = obj.optString("Account");
			this.OwnerCount = obj.optInt("OwnerCount");
			this.LowLimit.from(obj.opt("LowLimit"));
			this.HighLimit.from(obj.opt("HighLimit"));
		}
	}

}
