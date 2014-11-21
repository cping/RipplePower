package org.ripple.power.txns;

import java.util.ArrayList;

import org.json.JSONObject;

public class TransactionTx {

	public String account;

	public String meda;
	
	public String signingPubKey;

	public String txnSignature;
	
	public IssuedCurrency currency;
	
	public IssuedCurrency sendMax;

	public String fee;

	public String mode;

	public String trusted;

	public IssuedCurrency get;

	public IssuedCurrency pay;

	public long destinationTag;

	public String invoiceID;

	public long flags = 0;

	public long offersSequence;

	public long sequence;

	public String date;

	public long date_number;
	
	public ArrayList<Memo> memos = new ArrayList<Memo>(10);

	public String hash;

	public boolean isPartialPayment;
	
	public String flagsName = "Unkown";
	
	public String clazz;

	public long inLedger;

	public long ledgerIndex;

	public String counterparty;

	public static class Memo {
		public String memo_type;
		public String memo_data;
		public String memo_format;
		public long memo_date;

		public Memo(JSONObject obj, long date) {
			if (obj.has("Memo")) {
				JSONObject memo = obj.getJSONObject("Memo");
				if (memo.has("MemoType")) {
					memo_type = memo.getString("MemoType");
				}
				if (memo.has("MemoData")) {
					memo_data = memo.getString("MemoData");
				}
				if (memo.has("MemoFormat")) {
					memo_format = memo.getString("MemoFormat");
				}
			}
			this.memo_date = date;
		}
	}

}
