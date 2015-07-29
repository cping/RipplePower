package org.ripple.power.txns.data;

public class Line {

	public String account;
	public String balance;
	public String currency;
	public String limit;
	public String limit_peer;
	public int quality_in;
	public int quality_out;
	public boolean no_ripple;

	public double getBalance() {
		return Double.parseDouble(balance);
	}
}
