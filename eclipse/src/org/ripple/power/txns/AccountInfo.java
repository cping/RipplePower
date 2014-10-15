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

	public ArrayList<TransactionTx> transactions = new ArrayList<TransactionTx>(
			100);
	public ArrayList<BookOffer> bookOffers = new ArrayList<BookOffer>(100);
	public int cntTrust = 0;

	public HashMap<String, Double> debt = new HashMap<String, Double>(10);
	public HashMap<String, Long> debtCount = new HashMap<String, Long>(10);
	public HashMap<String, Integer> trustCount = new HashMap<String, Integer>(
			10);
}
