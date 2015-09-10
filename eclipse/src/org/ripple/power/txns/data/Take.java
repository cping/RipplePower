package org.ripple.power.txns.data;

import org.json.JSONObject;
import org.ripple.power.config.LSystem;
import org.ripple.power.txns.IssuedCurrency;

public class Take {

	public String value;
	public String currency;
	public String issuer;

	public Take(String v, String c, String i) {
		this.value = v;
		this.currency = c;
		this.issuer = i;
	}

	public Take(String c, String i) {
		this.currency = c;
		this.issuer = i;
	}

	public Take(String v) {
		this.value = v;
		this.currency = LSystem.nativeCurrency;
	}

	public Take() {
		this.currency = LSystem.nativeCurrency;
	}

	public void from(Object obj) {
		if (obj != null) {
			if (obj instanceof JSONObject) {
				JSONObject take = ((JSONObject) obj);
				this.value = take.optString("value");
				this.currency = take.optString("currency");
				this.issuer = take.optString("issuer");
			} else {
				this.value = (String) obj;
				this.currency = LSystem.nativeCurrency;
				this.issuer = "Ripple Labs";
			}
		}
	}

	public JSONObject getJSON() {
		if (currency == null) {
			currency = LSystem.nativeCurrency.toUpperCase();
		}
		JSONObject obj = new JSONObject();
		if (LSystem.nativeCurrency.equalsIgnoreCase(this.currency)) {
			obj.put("currency",this.currency.toUpperCase());
		} else {
			obj.put("currency", this.currency.toUpperCase());
			obj.put("issuer", this.issuer);
		}
		return obj;
	}

	public IssuedCurrency getIssuedCurrency() {
		if (LSystem.nativeCurrency.equalsIgnoreCase(currency)) {
			return new IssuedCurrency(value, true);
		} else {
			return new IssuedCurrency(value, issuer, currency);
		}
	}

	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (!(o instanceof Take)) {
			return false;
		}
		Take take = (Take) o;
		int count = 0;
		if ((take.currency == this.currency)
				|| (take.currency != null && take.currency
						.equals(this.currency))) {
			count++;
		}
		if ((take.issuer == this.issuer)
				|| (take.issuer != null && take.issuer.equals(this.issuer))) {
			count++;
		}
		if ((take.value == this.value)
				|| (take.value != null && take.value.equals(this.value))) {
			count++;
		}
		return count == 3;
	};

	@Override
	public String toString() {
		if (LSystem.nativeCurrency.equalsIgnoreCase(currency)) {
			return currency.toUpperCase();
		} else {
			return currency.toUpperCase() + '+' + issuer;
		}
	}

}
