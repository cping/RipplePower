package org.ripple.power.txns.data;

import org.json.JSONObject;
import org.ripple.power.config.LSystem;
import org.ripple.power.txns.IssuedCurrency;

public class AccountOffersCurrency {

	public double amount;
	public String currency;
	public String issuer;

	public AccountOffersCurrency(double v, String c, String i) {
		this.amount = v;
		this.currency = c;
		this.issuer = i;
	}

	public AccountOffersCurrency(String c, String i) {
		this.currency = c;
		this.issuer = i;
	}

	public AccountOffersCurrency(double v) {
		this.amount = v;
		this.currency = LSystem.nativeCurrency;
	}

	public AccountOffersCurrency() {
	}

	public void from(Object obj) {
		if (obj != null) {
			if (obj instanceof JSONObject) {
				JSONObject take = ((JSONObject) obj);
				this.amount = take.optDouble("value");
				this.currency = take.optString("currency");
				this.issuer = take.optString("issuer");
			} else {
				this.amount = Double.parseDouble((String) obj);
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
			return new IssuedCurrency(String.valueOf(amount), true);
		} else {
			return new IssuedCurrency(String.valueOf(amount), issuer, currency);
		}
	}

	public Take getTake() {
		if (LSystem.nativeCurrency.equalsIgnoreCase(currency)) {
			return new Take(String.valueOf(amount));
		} else {
			return new Take(String.valueOf(amount), currency, issuer);
		}
	}
}
