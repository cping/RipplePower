package org.ripple.power.txns;

import java.util.ArrayList;
import java.util.HashMap;

public class AccountInfo {

	public int count;
	
	public boolean error;

	public String address;

	public String balance;

	public String faceURL;

	public int sequence = 0;

	public String domain;

	public String fee;

	public int txPreLgrSeq = 0;

	public ArrayList<AccountLine> lines = new ArrayList<AccountLine>(100);
	
	public ArrayList<BookOffer> bookOffers = new ArrayList<BookOffer>(100);
	public int cntTrust = 0;

	public HashMap<String, Double> debt = new HashMap<String, Double>(10);
	public HashMap<String, Long> debtCount = new HashMap<String, Long>(10);
	public HashMap<String, Integer> trustCount = new HashMap<String, Integer>(
			10);
}
