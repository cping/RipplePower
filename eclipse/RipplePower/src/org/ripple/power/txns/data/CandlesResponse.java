package org.ripple.power.txns.data;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ripple.power.config.LSystem;

public class CandlesResponse {

	public String startTime;
	public String endTime;
	public String base;
	public String counter;
	public String timeIncrement;
	public List<Candle> results = new ArrayList<Candle>(
			LSystem.DEFAULT_MAX_CACHE_SIZE);

	public void from(Object obj) {
		if (obj != null) {
			if (obj instanceof JSONObject) {
				JSONObject result = (JSONObject) obj;
				this.startTime = result.optString("startTime");
				this.endTime = result.optString("endTime");
				this.base = result.optString("base");
				this.counter = result.optString("counter");
				this.timeIncrement = result.optString("timeIncrement");
				JSONArray arrays = result.optJSONArray("results");
				if (arrays != null) {
					int size = arrays.length();
					for (int i = 0; i < size; i++) {
						JSONObject candle = arrays.getJSONObject(i);
						Candle can = new Candle();
						can.from(candle);
						results.add(can);
					}

				}
			}
		}
	}
}
