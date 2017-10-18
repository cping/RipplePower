package org.ripple.power.txns;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

import org.json.JSONObject;
import org.ripple.power.CoinUtils;
import org.ripple.power.RippleAddress;
import org.ripple.power.config.LSystem;
import org.ripple.power.txns.data.Take;
import org.ripple.power.utils.BigNumber;
import org.ripple.power.utils.StringUtils;

import com.ripple.core.coretypes.AccountID;
import com.ripple.core.coretypes.Amount;
import com.ripple.core.coretypes.Currency;
import com.ripple.core.coretypes.Issue;

public class IssuedCurrency extends RippleDefault {

	boolean isHighNodeIssuer(BigNumber finalBalance, BigNumber previousBalance, BigNumber highLimit,
			BigNumber lowLimit) {
		if (finalBalance.isPositive()) {
			return true;
		} else if (finalBalance.isNegative()) {
			return false;
		} else if (previousBalance.isPositive()) {
			return true;
		} else if (previousBalance.isNegative()) {
			return false;
		} else if (lowLimit.isZero() && highLimit.isPositive()) {
			return false;
		} else if (highLimit.isZero() && lowLimit.isPositive()) {
			return true;
		} else {
			return false;
		}
	}

	// enter an account with a trust line containing XAU (-0.5%pa) -> hex:
	private final static String XAU_05PA = "0158415500000000C1F76FF6ECB0BAC600000000";

	public BigDecimal amount;
	public RippleAddress issuer;
	public String currency = LSystem.nativeCurrency.toUpperCase();
	public static final int MIN_SCALE = -96;
	public static final int MAX_SCALE = 80;

	public Object tag;

	public IssuedCurrency() {
	}

	public IssuedCurrency(String amountStr) {
		this(amountStr, false);
	}

	public IssuedCurrency(String amountStr, boolean update) {
		if (amountStr.toLowerCase().indexOf(LSystem.nativeCurrency) != -1) {
			amountStr = StringUtils.replaceIgnoreCase(amountStr, LSystem.nativeCurrency, "").trim();
			int idx = amountStr.indexOf('/');
			if (idx != -1) {
				amountStr = amountStr.substring(0, idx);
			}
			if (update) {
				amountStr = CurrencyUtils.getValueToRipple(amountStr);
			}
		}
		if (amountStr.indexOf('/') == -1) {
			amount = new BigDecimal(amountStr).stripTrailingZeros();
		} else {
			String[] split = org.ripple.power.utils.StringUtils.split(amountStr, "/");
			amount = new BigDecimal(split[0]).stripTrailingZeros();
			currency = split[1];
			if ("XAU (-0.5%pa)".equals(currency) || "XAU(-0.5%pa)".equals(currency)) {
				this.currency = XAU_05PA;
			} else if (currency.length() > 3 && AccountFind.is256hash(currency)) {
				byte[] buffer = CoinUtils.fromHex(currency);
				this.currency = CoinUtils.toHex(buffer);
			}
			issuer = new RippleAddress(split[2]);
			int oldScale = amount.scale();
			if (oldScale < MIN_SCALE || oldScale > MAX_SCALE) {
				int newScale = MAX_SCALE - (amount.precision() - amount.scale());
				if (newScale < MIN_SCALE || newScale > MAX_SCALE) {
					throw new RuntimeException("newScale " + newScale + " is out of range");
				}
				amount = amount.setScale(newScale);
			}
		}
	}

	public IssuedCurrency(String issuerStr, String currencyStr) {
		this("0", new RippleAddress(issuerStr), currencyStr);
	}

	public IssuedCurrency(String amountStr, String issuerStr, String currencyStr) {
		this(amountStr, new RippleAddress(issuerStr), currencyStr);
	}

	public IssuedCurrency(String amountStr, RippleAddress issuer, String currencyStr) {
		this(new BigDecimal(amountStr).stripTrailingZeros(), issuer, currencyStr);
	}

	public IssuedCurrency(IssuedCurrency cur) {
		this(cur.toString());
	}

	public IssuedCurrency(BigDecimal amount, RippleAddress issuer, String currencyStr) {
		int oldScale = amount.scale();
		if (oldScale < MIN_SCALE || oldScale > MAX_SCALE) {
			int newScale = MAX_SCALE - (amount.precision() - amount.scale());
			if (newScale < MIN_SCALE || newScale > MAX_SCALE) {
				throw new RuntimeException("newScale " + newScale + " is out of range");
			}
			amount = amount.setScale(newScale);
		}
		this.amount = amount;
		this.issuer = issuer;
		if ("XAU (-0.5%pa)".equals(currencyStr) || "XAU(-0.5%pa)".equals(currencyStr)) {
			this.currency = XAU_05PA;
		} else if (currencyStr.length() > 3 && AccountFind.is256hash(currencyStr)) {
			byte[] buffer = CoinUtils.fromHex(currencyStr);
			this.currency = CoinUtils.toHex(buffer);
		} else {
			this.currency = currencyStr;
		}
	}

	public IssuedCurrency(BigDecimal xrpAmount) {
		this.amount = xrpAmount;
	}

	public IssuedCurrency(int xrpAmount) {
		this(BigDecimal.valueOf(xrpAmount));
	}

	public boolean isNative() {
		return issuer == null || LSystem.nativeCurrency.equalsIgnoreCase(currency);
	}

	public boolean isNegative() {
		return amount.signum() == -1;
	}

	public String toGatewayString() {
		if (issuer == null || currency == null) {
			return amount.movePointLeft(6).stripTrailingZeros().toPlainString() + " XRP";
		}
		Gateway gateway = Gateway.getGateway(issuer.toString());
		if (gateway == null) {
			return amount.stripTrailingZeros().toPlainString() + "/" + currency + "/" + issuer.toString();
		} else {
			return amount.stripTrailingZeros().toPlainString() + "/" + currency + "/" + gateway.name;
		}
	}

	@Override
	public String toString() {
		if (issuer == null || currency == null) {
			return amount.movePointLeft(6).stripTrailingZeros().toPlainString() + " XRP";
		}
		return amount.stripTrailingZeros().toPlainString() + "/" + currency + "/" + issuer;
	}

	public BigDecimal scale(float s) {
		if (amount == null) {
			return null;
		}
		return amount = new BigDecimal(LSystem.getNumber(amount.multiply(BigDecimal.valueOf(s)), false));
	}

	public void copyFrom(JSONObject jsonDenomination) {
		if (jsonDenomination == null) {
			return;
		}
		String issuerStr = jsonDenomination.optString("issuer");
		if (!StringUtils.isEmpty(issuerStr)) {
			issuer = new RippleAddress(issuerStr);
		}
		String currencyStr = jsonDenomination.optString("currency");
		if (XAU_05PA.equals(currencyStr)) {
			currency = "XAU (-0.5%pa)";
		} else if (currencyStr.length() > 3 && AccountFind.is256hash(currencyStr)) {
			byte[] buffer = CoinUtils.fromHex(currencyStr);
			try {
				currency = new String(buffer, LSystem.encoding);
			} catch (UnsupportedEncodingException e) {
				currency = new String(buffer);
			}
		} else {
			currency = currencyStr;
		}
		String amountStr = LSystem.getNumberShort(jsonDenomination.optString("value", "0"));
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
		result = prime * result + ((currency == null) ? 0 : currency.hashCode());
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

	public Amount getAmount() {
		if (issuer != null && currency != null) {
			return new Amount(amount, Currency.fromString(currency), AccountID.fromAddress(issuer.toString()));
		}
		return new Amount(amount);
	}

	public Issue getIssue() {
		if (issuer != null && currency != null) {
			return new Issue(Currency.fromString(currency), AccountID.fromAddress(issuer.toString()));
		}
		return null;
	}

	public JSONObject getJSON() {
		if (currency == null) {
			currency = LSystem.nativeCurrency.toUpperCase();
		}
		JSONObject obj = new JSONObject();
		if (LSystem.nativeCurrency.equalsIgnoreCase(currency)) {
			obj.put("currency", currency.toUpperCase());
		} else {
			obj.put("currency", currency.toUpperCase());
			obj.put("issuer", issuer.toString());
		}
		return obj;
	}

	public Take getTake() {
		if (currency == null) {
			currency = LSystem.nativeCurrency.toUpperCase();
		}
		Take take = null;
		if (LSystem.nativeCurrency.equalsIgnoreCase(currency)) {
			take = new Take(amount.toString());
		} else {
			take = new Take(amount.toString(), currency, issuer.toString());
		}
		return take;
	}

}
