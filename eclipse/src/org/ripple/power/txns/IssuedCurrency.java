package org.ripple.power.txns;

import java.math.BigDecimal;

import org.address.ripple.RippleAddress;
import org.json.JSONObject;

public class IssuedCurrency {
	public BigDecimal amount;
	public RippleAddress issuer;
	public String currency;
	public static final int MIN_SCALE = -96;
	public static final int MAX_SCALE = 80;

	public Object tag;

	public IssuedCurrency() {
	}

	public IssuedCurrency(String amountStr) {
		if (amountStr.indexOf("XRP") != -1) {
			amountStr = amountStr.replace("XRP", "").trim();
		}
		if (amountStr.indexOf('/') == -1) {
			amount = new BigDecimal(amountStr).stripTrailingZeros();
		} else {
			String[] split = org.ripple.power.utils.StringUtils.split(
					amountStr, "/");
			amount = new BigDecimal(split[0]).stripTrailingZeros();
			currency = split[1];
			issuer = new RippleAddress(split[2]);
			int oldScale = amount.scale();
			if (oldScale < MIN_SCALE || oldScale > MAX_SCALE) {
				int newScale = MAX_SCALE
						- (amount.precision() - amount.scale());
				if (newScale < MIN_SCALE || newScale > MAX_SCALE) {
					throw new RuntimeException("newScale " + newScale
							+ " is out of range");
				}
				amount = amount.setScale(newScale);
			}
		}
	}

	public IssuedCurrency(String amountStr, String issuerStr, String currencyStr) {
		this(amountStr, new RippleAddress(issuerStr), currencyStr);
	}

	public IssuedCurrency(String amountStr, RippleAddress issuer,
			String currencyStr) {
		this(new BigDecimal(amountStr).stripTrailingZeros(), issuer,
				currencyStr);
	}

	public IssuedCurrency(IssuedCurrency cur) {
		this(cur.toString());
	}

	public IssuedCurrency(BigDecimal amount, RippleAddress issuer,
			String currencyStr) {
		int oldScale = amount.scale();
		if (oldScale < MIN_SCALE || oldScale > MAX_SCALE) {
			int newScale = MAX_SCALE - (amount.precision() - amount.scale());
			if (newScale < MIN_SCALE || newScale > MAX_SCALE) {
				throw new RuntimeException("newScale " + newScale
						+ " is out of range");
			}
			amount = amount.setScale(newScale);
		}
		this.amount = amount;
		this.issuer = issuer;
		this.currency = currencyStr;
	}

	public IssuedCurrency(BigDecimal xrpAmount) {
		this.amount = xrpAmount;
	}

	public IssuedCurrency(int xrpAmount) {
		this(BigDecimal.valueOf(xrpAmount));
	}
	
	public boolean isNative() {
		return issuer == null;
	}

	public boolean isNegative() {
		return amount.signum() == -1;
	}

	public String toGatewayString() {
		if (issuer == null || currency == null) {
			return amount.movePointLeft(6).stripTrailingZeros().toPlainString()
					+ " XRP";
		}
		Gateway gateway = Gateway.getGateway(issuer.toString());
		if (gateway == null) {
			return amount.stripTrailingZeros().toPlainString() + "/" + currency
					+ "/" + issuer.toString();
		} else {
			return amount.stripTrailingZeros().toPlainString() + "/" + currency
					+ "/" + gateway.name;
		}
	}

	@Override
	public String toString() {
		if (issuer == null || currency == null) {
			return amount.movePointLeft(6).stripTrailingZeros().toPlainString()
					+ " XRP";
		}
		return amount.stripTrailingZeros().toPlainString() + "/" + currency
				+ "/" + issuer;
	}

	public void copyFrom(JSONObject jsonDenomination) {
		issuer = new RippleAddress(((String) jsonDenomination.get("issuer")));
		String currencyStr = ((String) jsonDenomination.get("currency"));
		currency = currencyStr;

		String amountStr = (String) jsonDenomination.get("value");
		amount = new BigDecimal(amountStr);
	}

	public void copyFrom(Object jsonObject) {
		if (jsonObject instanceof String) {
			amount = new BigDecimal((String) jsonObject);
		} else {
			copyFrom((JSONObject) jsonObject);
		}
	}

	public Object toJSON() {
		if (currency == null) {
			return amount.toString();
		} else {
			JSONObject jsonThis = new JSONObject();
			jsonThis.put("value", amount.toString());
			jsonThis.put("issuer", issuer.toString());
			jsonThis.put("currency", currency);
			return jsonThis;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result
				+ ((currency == null) ? 0 : currency.hashCode());
		result = prime * result + ((issuer == null) ? 0 : issuer.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IssuedCurrency other = (IssuedCurrency) obj;
		if (amount == null) {
			if (other.amount != null)
				return false;
		} else if (amount.compareTo(other.amount) != 0)
			return false;
		if (currency == null) {
			if (other.currency != null)
				return false;
		} else if (!currency.equals(other.currency))
			return false;
		if (issuer == null) {
			if (other.issuer != null)
				return false;
		} else if (!issuer.equals(other.issuer))
			return false;
		return true;
	}
}
