package org.ripple.power.txns.data;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ripple.power.config.LSystem;

public class TransactionStatsResponse {
	public String startTime;
	public String endTime;
	public String timeIncrement;
	public List<TransactionStats> results = new ArrayList<TransactionStats>(LSystem.DEFAULT_MAX_CACHE_SIZE);

	public void from(Object obj) {
		if (obj != null) {
			if (obj instanceof JSONObject) {
				JSONObject result = (JSONObject) obj;
				this.startTime = result.optString("startTime");
				this.endTime = result.optString("endTime");
				this.timeIncrement = result.optString("timeIncrement");
				JSONArray arrays = result.optJSONArray("results");
				if (arrays != null) {
					int size = arrays.length();
					for (int i = 0; i < size; i++) {
						TransactionStats transactionStats = new TransactionStats();
						transactionStats.from(arrays.getJSONObject(i));
						results.add(transactionStats);
					}
				}
			}
		}
	}
}
