package org.ripple.power.hft;

import java.util.Calendar;

public class Tick {
	public String symbol;
	public float price;
	public float volume;
	public Calendar timestamp;

	public Tick(String symbol, float price, float volume, Calendar timestamp) {
		super();
		this.symbol = symbol;
		this.price = price;
		this.volume = volume;
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		String s = "*** TICK *** \n";
		s += "Symbol: " + symbol + "\n";
		s += "Price: " + price + "\n";
		s += "Volume: " + volume + "\n";
		s += "Timestamp: " + timestamp.getTime().toString();
		return s;
	}

}
