package org.ripple.power.txns;

import java.util.ArrayList;
import java.util.HashMap;

public class AccountInfo {

	public long marker = 0;

	public int count = 0;

	public boolean error;

	public String address;

	public String balance;

	public String faceURL;

	public int sequence = 0;

	public String domain;

	public String fee;

	public int txPreLgrSeq = 0;

	public ArrayList<AccountInfo> accountlinks = new ArrayList<AccountInfo>(10);

	public ArrayList<AccountLine> lines = new ArrayList<AccountLine>(10);

	public ArrayList<AccountLine> zero_lines = new ArrayList<AccountLine>(10);
	public ArrayList<TransactionTx> transactions = new ArrayList<TransactionTx>(
			100);
	public ArrayList<BookOffer> bookOffers = new ArrayList<BookOffer>(100);
	public int cntTrust = 0;

	public HashMap<String, Double> debt = new HashMap<String, Double>(10);
	public HashMap<String, Long> debtCount = new HashMap<String, Long>(10);
	public HashMap<String, Integer> trustCount = new HashMap<String, Integer>(
			10);

	public AccountInfo copy(AccountInfo info) {

		marker = info.marker;
		count = info.count;
		error = info.error;
		address = info.address;
		balance = info.balance;
		faceURL = info.faceURL;
		sequence = info.sequence;
		domain = info.domain;
		fee = info.fee;
		txPreLgrSeq = info.txPreLgrSeq;

		accountlinks.clear();
		accountlinks.addAll(info.accountlinks);

		lines.clear();
		lines.addAll(info.lines);

		zero_lines.clear();
		zero_lines.addAll(info.zero_lines);

		transactions.clear();
		transactions.addAll(info.transactions);

		bookOffers.clear();
		bookOffers.addAll(info.bookOffers);

		debt.clear();
		debt.putAll(info.debt);

		debtCount.clear();
		debtCount.putAll(info.debtCount);

		trustCount.clear();
		trustCount.putAll(info.trustCount);

		cntTrust = info.cntTrust;

		return this;

	}

	public ArrayList<TransactionTx> getTxs(String flag) {
		ArrayList<TransactionTx> list = new ArrayList<TransactionTx>();
		if (transactions != null) {
			for (TransactionTx tx : transactions) {
				if (tx.clazz.equalsIgnoreCase(flag)) {
					list.add(tx);
				}
			}
		}
		return list;
	}

}
