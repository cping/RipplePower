package org.ripple.power.txns;

public class AccountLine {

	String issuer;
	String currency;
	String amount;
	String limit_peer;
	private IssuedCurrency _currency;

	public AccountLine() {

	}

	public AccountLine(String issuer, String currency, String amount) {
		this.issuer = issuer;
		this.currency = currency;
		this.amount = amount;
	}

	public IssuedCurrency get() {
		if (_currency == null) {
			_currency = new IssuedCurrency(amount, issuer, currency);
		}
		return _currency;
	}

	public String getIssuer() {
		return issuer;
	}

	public String getCurrency() {
		return currency;
	}

	public String getAmount() {
		return amount;
	}

	public String getLimitpeer() {
		return limit_peer;
	}

}
