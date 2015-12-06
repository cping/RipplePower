package org.ripple.power.txns;

import org.ripple.power.config.LSystem;

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

	public String toString() {
		StringBuilder sbr = new StringBuilder();
		sbr.append("startTime:").append(startTime).append(LSystem.LS);
		sbr.append("baseVolume:").append(baseVolume).append(LSystem.LS);
		sbr.append("counterVolume:").append(counterVolume).append(LSystem.LS);
		sbr.append("count:").append(count).append(LSystem.LS);
		sbr.append("open:").append(open).append(LSystem.LS);
		sbr.append("high:").append(high).append(LSystem.LS);
		sbr.append("low:").append(low).append(LSystem.LS);
		sbr.append("close:").append(close).append(LSystem.LS);
		sbr.append("vwap:").append(vwap).append(LSystem.LS);
		sbr.append("openTime:").append(openTime).append(LSystem.LS);
		sbr.append("closeTime:").append(closeTime).append(LSystem.LS);
		sbr.append("partial:").append(partial);
		return sbr.toString();
	}

	public long getStartTime() {
		return System.currentTimeMillis();
	}

}
