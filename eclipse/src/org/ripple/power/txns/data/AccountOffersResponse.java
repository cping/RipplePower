package org.ripple.power.txns.data;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ripple.power.config.LSystem;

public class AccountOffersResponse {

	public String account;
	public String startTime;
	public String endTime;
	public List<AccountOffersResult> results = new ArrayList<AccountOffersResult>(
			LSystem.DEFAULT_MAX_CACHE_SIZE);

	public void from(Object obj) {
		if (obj != null) {
			if (obj instanceof JSONObject) {
				JSONObject result = (JSONObject)obj;
				this.account = result.optString("account");
				this.startTime = result.optString("startTime");
				this.endTime = result.optString("endTime");
				JSONArray arrays = result.optJSONArray("results");
				if (arrays != null) {
					int size = arrays.length();
					for (int i = 0; i < size; i++) {
						AccountOffersResult currency = new AccountOffersResult();
						currency.from(arrays.getJSONObject(i));
						results.add(currency);
					}
				}
			}
		}
	}
}
