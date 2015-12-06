package org.ripple.power.txns.data;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ripple.power.config.LSystem;

public class TotalNetworkValue {
	public String currency;
	public String issuer;
	public String name;
	public List<String> hotwallets = new ArrayList<String>(10);
	public double amount;
	public double rate;
	public double convertedAmount;

	public double getAmount() {
		return Double.parseDouble(LSystem.getNumberShort(amount));
	}

	public double getConvertedAmount() {
		return Double.parseDouble(LSystem.getNumberShort(convertedAmount));
	}

	public void from(JSONObject obj) {
		if (obj != null) {
			this.currency = obj.optString("currency");
			this.issuer = obj.optString("issuer");
			this.name = obj.optString("name");
			this.amount = obj.optDouble("amount");
			this.convertedAmount = obj.optDouble("convertedAmount");
			this.rate = obj.optDouble("rate");
			JSONArray arrays = obj.optJSONArray("hotwallets");
			if (arrays != null) {
				int size = arrays.length();
				for (int i = 0; i < size; i++) {
					String hotwallet = arrays.getString(i);
					hotwallets.add(hotwallet);
				}
			}
		}
	}

}
