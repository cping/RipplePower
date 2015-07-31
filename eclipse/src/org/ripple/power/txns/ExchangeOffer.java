package org.ripple.power.txns;

import org.json.JSONObject;

public class ExchangeOffer {

	public long sequenceNumber;
	public IssuedCurrency takerGets;
	public IssuedCurrency takerPays;

	public void copyFrom(JSONObject jsonOffer) {
		sequenceNumber = jsonOffer.getLong("seq");
		takerGets = CurrencyUtils.getIssuedCurrency(jsonOffer.get("taker_gets"));
		takerPays = CurrencyUtils.getIssuedCurrency(jsonOffer.get("taker_pays"));
	}

}
