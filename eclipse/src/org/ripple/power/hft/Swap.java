package org.ripple.power.hft;

public class Swap {

	public long startTime;
	public long openTime;
	public long closeTime;
	public double baseVolume;
	public double counterVolume;
	public int count;
	public double open;
	public double high;
	public double low;
	public double close;
	public double vwap;
	public boolean partial;

	public long getStartTime() {
		return System.currentTimeMillis();
	}

}
