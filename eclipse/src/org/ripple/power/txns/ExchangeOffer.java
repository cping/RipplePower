package org.ripple.power.txns;

import java.math.BigDecimal;

import org.json.JSONException;
import org.json.JSONObject;

public class ExchangeOffer {
	
	public long sequenceNumber;
	public IssuedCurrency takerGets;
	public IssuedCurrency takerPays;

	public void copyFrom(JSONObject jsonOffer) {
		try {
			sequenceNumber =  jsonOffer.getLong("seq");
			takerGets = jsonToDenominatedAmount(jsonOffer.get("taker_gets"));
			takerPays = jsonToDenominatedAmount(jsonOffer.get("taker_pays"));
		} catch (JSONException e) {
		}
	}

	private IssuedCurrency jsonToDenominatedAmount(Object jsonDenominatedAmount) {
		if(jsonDenominatedAmount instanceof JSONObject){
			IssuedCurrency amount = new IssuedCurrency();
			amount.copyFrom((JSONObject) jsonDenominatedAmount);
			return amount;
		}
		else{
			return new IssuedCurrency(new BigDecimal((String) jsonDenominatedAmount));
		}
	}
}
