package org.ripple.power.txns.data;

import org.json.JSONObject;
import org.ripple.power.config.LSystem;
import org.ripple.power.txns.IssuedCurrency;

public class MarketComponent {

	public IssuedCurrency base = new IssuedCurrency();
	public IssuedCurrency counter = new IssuedCurrency();
	public double rate;
	public long count;
	public double amount;
	public double convertedAmount;

	public void from(JSONObject obj) {
		if (obj != null) {
			this.base.copyFrom(obj.opt("base"));
			this.counter.copyFrom(obj.opt("counter"));
			this.rate = obj.optDouble("rate");
			this.count = obj.optLong("count");
			this.amount = obj.optDouble("amount");
			this.convertedAmount = obj.optDouble("convertedAmount");
		}
	}

	public double getAmount() {
		return Double.parseDouble(LSystem.getNumberShort(amount));
	}

	public double getConvertedAmount() {
		return Double.parseDouble(LSystem.getNumberShort(convertedAmount));
	}
}
