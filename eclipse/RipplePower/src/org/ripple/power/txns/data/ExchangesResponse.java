package org.ripple.power.txns.data;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class ExchangesResponse {

	public boolean result;
	public int count;
	public ArrayList<Exchange> exchanges = new ArrayList<Exchange>(20);

	public void from(JSONObject obj) {
		if (obj != null) {
			this.result = obj.optBoolean("result");
			this.count = obj.optInt("count");
			JSONArray array = obj.optJSONArray("exchanges");
			if (array != null) {
				int size = array.length();
				for (int i = 0; i < size; i++) {
					Exchange exchange = new Exchange();
					exchange.from(array.get(i));
					exchanges.add(exchange);
				}
			}
		}
	}
}
