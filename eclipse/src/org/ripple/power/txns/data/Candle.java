package org.ripple.power.txns.data;

import org.json.JSONObject;

public class Candle {
	public String startTime;
	public String openTime;
	public String closeTime;
	public double baseVolume;
	public double counterVolume;
	public int count;
	public double open;
	public double high;
	public double low;
	public double close;
	public double vwap;

	public String getStartTime() {
		return startTime;
	}

	public boolean isPartial() {
		return false;
	}

	public void from(JSONObject obj) {
		if (obj != null) {
			this.startTime = obj.optString("startTime");
			this.openTime = obj.optString("openTime");
			this.closeTime = obj.optString("closeTime");
			this.baseVolume = obj.optDouble("baseVolume");
			this.counterVolume = obj.optDouble("counterVolume");
			this.count = obj.optInt("count");
			this.open = obj.optDouble("open");
			this.high = obj.optDouble("high");
			this.low = obj.optDouble("low");
			this.close = obj.optDouble("close");
			this.vwap = obj.optDouble("vwap");
		}
	}
}
