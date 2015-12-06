package org.ripple.power.txns.data;

import org.json.JSONObject;
import org.ripple.power.txns.Const;

public class AccountData {
	public String Account;
	public String Balance;
	public long Flags;
	public String LedgerEntryType;
	public int OwnerCount;
	public String PreviousTxnID;
	public String EmailHash;
	public long PreviousTxnLgrSeq;
	public long Sequence;
	public String index;
	public long TransferRate;
	public String MessageKey;
	public String AccountTxnID;
	public String urlgravatar;
	public String Domain;

	public double getBalanceXrp() {
		long drops = Long.parseLong(Balance);
		return (double) drops / Const.DROPS_IN_XRP;
	}

	public void from(JSONObject obj) {
		if (obj != null) {
			this.Account = obj.optString("Account");
			this.OwnerCount = obj.optInt("OwnerCount");
			this.EmailHash = obj.optString("EmailHash");
			this.PreviousTxnLgrSeq = obj.optLong("PreviousTxnLgrSeq");
			this.index = obj.optString("index");
			this.PreviousTxnID = obj.optString("PreviousTxnID");
			this.Flags = obj.optLong("Flags");
			this.Sequence = obj.optLong("Sequence");
			this.TransferRate = obj.optLong("TransferRate");
			this.MessageKey = obj.optString("MessageKey");
			this.AccountTxnID = obj.optString("AccountTxnID");
			this.urlgravatar = obj.optString("urlgravatar");
			this.LedgerEntryType = obj.optString("LedgerEntryType");
			this.Domain = obj.optString("Domain");
			this.Balance = obj.optString("Balance");
		}
	}
}
