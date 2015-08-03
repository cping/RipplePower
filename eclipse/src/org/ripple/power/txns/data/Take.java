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
		JSONObject obj = new JSONObject();
		if (LSystem.nativeCurrency.equalsIgnoreCase(this.currency)) {
			obj.put("currency", LSystem.nativeCurrency.toUpperCase());
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
}
