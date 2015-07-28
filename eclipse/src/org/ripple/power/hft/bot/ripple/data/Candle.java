package org.ripple.power.hft.bot.ripple.data;

public class Candle {
	String startTime;
	String openTime;
	String closeTime;
	double baseVolume;
	double counterVolume;
	int count;
	double open;
	double high;
	double low;
	double close;
	double vwap;

	String getStartTime() {
		return startTime;
	}

	boolean isPartial() {
		return false;
	}
}
