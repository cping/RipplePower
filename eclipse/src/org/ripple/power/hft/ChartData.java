package org.ripple.power.hft;

public class ChartData {
	private final double prevPrice;
	private final double openPrice;
	private final double lastPrice;
	private final double highPrice;
	private final double lowPrice;
	private final long volume;

	private final long timestamp;

	private ChartData(double prevPrice, double openPrice, double lastPrice,
			double highPrice, double lowPrice, long volume, long timestamp) {
		this.prevPrice = prevPrice;
		this.openPrice = openPrice;
		this.lastPrice = lastPrice;
		this.highPrice = highPrice;
		this.lowPrice = lowPrice;
		this.volume = volume;
		this.timestamp = timestamp;
	}

	public static ChartData newInstance(double prevPrice, double openPrice,
			double lastPrice, double highPrice, double lowPrice, long volume,
			long timestamp) {
		return new ChartData(prevPrice, openPrice, lastPrice, highPrice,
				lowPrice, volume, timestamp);
	}

	public double getPrevPrice() {
		return prevPrice;
	}

	public double getOpenPrice() {
		return openPrice;
	}

	public double getLastPrice() {
		return lastPrice;
	}

	public double getHighPrice() {
		return highPrice;
	}

	public double getLowPrice() {
		return lowPrice;
	}

	public long getVolume() {
		return volume;
	}

	public long getTimestamp() {
		return timestamp;
	}
}
