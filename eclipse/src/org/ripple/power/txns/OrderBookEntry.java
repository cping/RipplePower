package org.ripple.power.txns;

import org.json.JSONObject;

public class OrderBookEntry {

	public String account;
	public IssuedCurrency takerGetsAmount;
	public IssuedCurrency takerPaysAmount;

	public void copyFrom(JSONObject jsonOrderBookEntry) {
		takerPaysAmount = new IssuedCurrency();
		takerPaysAmount.copyFrom(jsonOrderBookEntry.get("TakerPays"));
		takerGetsAmount = new IssuedCurrency();
		takerGetsAmount.copyFrom(jsonOrderBookEntry.get("TakerGets"));
		account = jsonOrderBookEntry.getString("Account");
	}

}
