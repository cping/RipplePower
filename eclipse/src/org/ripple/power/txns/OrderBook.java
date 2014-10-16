package org.ripple.power.txns;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OrderBook extends ArrayList<OrderBookEntry> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void copyFrom(JSONObject jsonCommandResult) {
		try {
			JSONArray jsonOffers = jsonCommandResult.getJSONArray("offers");
			for (int i = 0; i < jsonOffers.length(); i++) {
				OrderBookEntry bookEntry = new OrderBookEntry();
				bookEntry.copyFrom((JSONObject) jsonOffers.get(i));
				add(bookEntry);
			}
		} catch (JSONException e) {
		}
	}

}
