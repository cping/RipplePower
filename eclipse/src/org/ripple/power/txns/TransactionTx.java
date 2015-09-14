package org.ripple.power.txns;

import java.util.ArrayList;

import org.json.JSONObject;
import org.ripple.power.txns.data.Meta;

public class TransactionTx {

	public static class AffectedNode {

		public String name;

		public String ledgerEntryType;

		public String previousTxnID;

		public String ledgerIndex;

		public String regularKey;

		public String takerGetsIssuer;

		public String takerPaysIssuer;

		public String takerPaysCurrency;

		public String takerGetsCurrency;

		public String exchangeRate;

		public long previousTxnLgrSeq;

		public IssuedCurrency balance;

		public IssuedCurrency highLimit;

		public IssuedCurrency lowLimit;

		public long transferRate;

		public IssuedCurrency takerGets;

		public IssuedCurrency takerPays;

		public long flags;

		public boolean sell;

		public String sellOrBuy;

		public String txid;

		public long sequence;

		public String account;

		public String owner;

		public String rootIndex;

		public String indexPrevious;

		public String indexNext;

		public long ownerCount;

		public String getBalance() {
			return balance == null ? null : balance.toString();
		}

		public boolean isCompleted() {
			return "DeletedNode".equalsIgnoreCase(name);
		}

	}

	public String account;

	public String metaString;

	public Meta meta;
	
	public String signingPubKey;

	public String txnSignature;

	public String destination;

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

	public ArrayList<TransactionTx.AffectedNode> affectedNodeList = new ArrayList<TransactionTx.AffectedNode>(
			200);

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
