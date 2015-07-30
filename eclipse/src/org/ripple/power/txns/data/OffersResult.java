package org.ripple.power.txns.data;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ripple.power.config.LSystem;

public class OffersResult {
	public String account;
	public long ledger_current_index;
	public boolean validated;
	public List<Offer> offers = new ArrayList<Offer>(
			LSystem.DEFAULT_MAX_CACHE_SIZE);

	public void from(JSONObject obj) {
		if (obj != null) {
			offers.clear();
			this.account = obj.optString("account");
			this.ledger_current_index = obj.optLong("ledger_current_index");
			this.validated = obj.optBoolean("validated");
			JSONArray offers_array = obj.optJSONArray("offers");
			if (offers_array != null) {
				int size = offers_array.length();
				for (int i = 0; i < size; i++) {
					JSONObject offer_object = offers_array.getJSONObject(i);
					if (offer_object != null) {
						Offer offer = new Offer();
						offer.from(offer_object);
						offers.add(offer);
					}
				}
			}
		}
	}
}
