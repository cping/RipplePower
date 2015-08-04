package org.ripple.power.txns.data;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ripple.power.config.LSystem;
import org.ripple.power.txns.IssuedCurrency;

public class MarketsRespone {

	public String rowkey;
	public List<MarketComponent> components = new ArrayList<MarketComponent>(
			LSystem.DEFAULT_MAX_CACHE_SIZE);
	public long count;
	public String startTime;
	public String endTime;
	public IssuedCurrency exchange = new IssuedCurrency();
	public double exchangeRate;
	public double total;

	public void from(Object obj) {
		if (obj != null) {
			if (obj instanceof JSONObject) {
				JSONObject result = (JSONObject) obj;
				this.rowkey = result.optString("rowkey");
				this.count = result.optLong("count");
				this.startTime = result.optString("startTime");
				this.endTime = result.optString("endTime");
				this.exchange.copyFrom(result.opt("exchange"));
				this.exchangeRate = result.optDouble("exchangeRate");
				this.total = result.optDouble("total");
				JSONArray arrays = result.optJSONArray("components");
				if (arrays != null) {
					int size = arrays.length();
					for (int i = 0; i < size; i++) {
						MarketComponent marketComponent = new MarketComponent();
						marketComponent.from(arrays.getJSONObject(i));
						components.add(marketComponent);
					}
				}
			}
		}
	}
}
