package org.ripple.power.txns;

public class RippleItem {

	public String startTime;
	public double baseVolume;
	public double counterVolume;
	public double count;
	public double open;
	public double high;
	public double low;
	public double close;
	public double vwap;
	public String openTime;
	public String closeTime;
	public boolean partial;

	public long getStartTime() {
		return System.currentTimeMillis();
	}

}
