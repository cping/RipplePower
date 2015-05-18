package org.ripple.power.txns;

public class AccountLine {

	String issuer;
	String currency;
	String amount;
	String limit_peer;
	String limit;
	long quality_out = 0;
	long quality_in = 0;
	private IssuedCurrency _currency;

	public AccountLine() {

	}

	public AccountLine(String issuer, String currency, String amount) {
		this.issuer = issuer;
		this.currency = currency;
		this.amount = amount;
	}

	public String toString() {
		return get().toGatewayString();
	}

	public IssuedCurrency get() {
		if (_currency == null) {
			_currency = new IssuedCurrency(amount, issuer, currency);
			_currency.tag = this;
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

	public String getLimit() {
		return limit;
	}

	public long getQuality_out() {
		return quality_out;
	}

	public long getQuality_in() {
		return quality_in;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (!(o instanceof AccountLine)) {
			return false;
		}
		AccountLine line = (AccountLine) o;
		int count = 0;
		if (line.amount != null && line.amount.equals(amount)) {
			count++;
		} else if (line.amount == null && amount == null) {
			count++;
		}
		if (line.currency != null && line.currency.equals(currency)) {
			count++;
		} else if (line.currency == null && currency == null) {
			count++;
		}
		if (line.issuer != null && line.issuer.equals(issuer)) {
			count++;
		} else if (line.issuer == null && issuer == null) {
			count++;
		}
		if (line.limit != null && line.limit.equals(limit)) {
			count++;
		} else if (line.limit == null && limit == null) {
			count++;
		}
		if (line.quality_in == quality_in) {
			count++;
		}
		if (line.quality_out == quality_out) {
			count++;
		}
		return count == 6;
	}

}
