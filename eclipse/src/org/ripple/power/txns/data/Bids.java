package org.ripple.power.txns.data;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ripple.power.config.LSystem;

public class Bids {
	public long ledger_current_index;
	public List<Bid> offers = new ArrayList<Bid>(LSystem.DEFAULT_MAX_CACHE_SIZE);
	public boolean validated;

	public void from(JSONObject obj) {
		if (obj != null) {
			this.ledger_current_index = obj.optLong("ledger_current_index");
			this.validated = obj.optBoolean("validated");
			JSONArray offers_array = obj.optJSONArray("offers");
			if (offers_array != null) {
				int size = offers_array.length();
				for (int i = 0; i < size; i++) {
					JSONObject offer = offers_array.getJSONObject(i);
					Bid bid = new Bid();
					bid.from(offer);
					offers.add(bid);
				}
			}
		}
	}
}
