package org.ripple.power.txns.data;

import org.json.JSONObject;

public class TxJson {
	public String Account;
	public String Fee;
	public long Flags;
	public long Sequence;
	public long LastLedgerSequence;
	public String SigningPubKey;
	public Take TakerGets = new Take();
	public Take TakerPays = new Take();
	public String TransactionType;
	public String TxnSignature;
	public String hash;

	public void from(JSONObject obj) {
		if (obj != null) {
			this.Account = obj.optString("Account");
			this.Fee = obj.optString("Fee");
			this.Flags = obj.optLong("Flags");
			this.Sequence = obj.optLong("Sequence");
			this.LastLedgerSequence = obj.optLong("LastLedgerSequence");
			this.SigningPubKey = obj.optString("SigningPubKey");
			this.TakerGets.from(obj.opt("TakerGets"));
			this.TakerPays.from(obj.opt("TakerPays"));
			this.TransactionType = obj.optString("TransactionType");
			this.TxnSignature = obj.optString("TxnSignature");
			this.hash = obj.optString("hash");
		}
	}
}
