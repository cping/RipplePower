package org.ripple.power.txns;

public class AccountLine {

	public String issuer;
	public String currency;
	public String amount;
	public String limit_peer;
	
	public AccountLine(){
		
	}

	public AccountLine(String issuer, String currency, String amount) {
		this.issuer = issuer;
		this.currency = currency;
		this.amount = amount;
	}

}
