package org.ripple.power.hft;

public class BOT_SET {

	public double operative_amount;
	public double min_volume;
	public double max_volume;
	public String gateway_address;
	public String currency_code;
	public boolean cleanup_zombies;
	public double minDifference;
	public double minPriceUpdate;

	public boolean arbitrage = false;
	public String baseCurrency;
	public String baseGateway;

	public String arbCurrency;
	public String arbGateway;

	public String parity;
	public String arbFactor;
	public int intervalMs;

}
