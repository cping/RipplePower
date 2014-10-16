package org.ripple.power.txns;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class ExchangeOffers extends ArrayList<ExchangeOffer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void copyFrom(JSONObject jsonCommandResult) {
		JSONArray jsonOffers = jsonCommandResult.getJSONArray("offers");
		for (int i = 0; i < jsonOffers.length(); i++) {
			JSONObject jsonOffer = (JSONObject) jsonOffers.get(i);
			ExchangeOffer offer = new ExchangeOffer();
			offer.copyFrom(jsonOffer);
			add(offer);
		}
	}

}
