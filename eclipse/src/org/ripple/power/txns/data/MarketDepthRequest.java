package org.ripple.power.txns.data;

public class MarketDepthRequest {
	public int id;
	public String command = "book_offers";
	public Take taker_pays;
	public Take taker_gets;
	public int limit = 15;
}
