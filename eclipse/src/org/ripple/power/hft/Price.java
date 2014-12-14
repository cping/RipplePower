package org.ripple.power.hft;

public class Price {
	
	private long priceId;
	private long timeStamp;
	private Double closePrice, openPrice, lowPrice, highPrice, lastClosePrice;
	private long totalTradedQuantity;

	public Price(long timeStamp, Double closePrice, Double openPrice,
			Double lowPrice, Double highPrice, Double lastClosePrice,
			long totalTradedQuantity) {
		this.timeStamp = timeStamp;
		this.closePrice = closePrice;
		this.openPrice = openPrice;
		this.lowPrice = lowPrice;
		this.highPrice = highPrice;
		this.lastClosePrice = lastClosePrice;
		this.totalTradedQuantity = totalTradedQuantity;
	}

	public long getTotalTradedQuantity() {
		return totalTradedQuantity;
	}

	public void setTotalTradedQuantity(long totalTradedQuantity) {
		this.totalTradedQuantity = totalTradedQuantity;
	}

	public long getPriceId() {
		return priceId;
	}

	public void setPriceId(long priceId) {
		this.priceId = priceId;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public Double getClosePrice() {
		return closePrice;
	}

	public void setClosePrice(Double closePrice) {
		this.closePrice = closePrice;
	}

	public Double getOpenPrice() {
		return openPrice;
	}

	public void setOpenPrice(Double openPrice) {
		this.openPrice = openPrice;
	}

	public Double getLowPrice() {
		return lowPrice;
	}

	public void setLowPrice(Double lowPrice) {
		this.lowPrice = lowPrice;
	}

	public Double getHighPrice() {
		return highPrice;
	}

	public void setHighPrice(Double highPrice) {
		this.highPrice = highPrice;
	}

	public Double getLastClosePrice() {
		return lastClosePrice;
	}

	public void setLastClosePrice(Double lastClosePrice) {
		this.lastClosePrice = lastClosePrice;
	}

	public Price() {
	}

	@Override
	public String toString() {
		return "Price{" + "priceId=" + priceId + ", timeStamp=" + timeStamp
				+ ", closePrice=" + closePrice + ", openPrice=" + openPrice
				+ ", lowPrice=" + lowPrice + ", highPrice=" + highPrice
				+ ", lastClosePrice=" + lastClosePrice
				+ ", totalTradedQuantity=" + totalTradedQuantity + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Price))
			return false;
		Price price = (Price) o;
		if (priceId != price.priceId)
			return false;
		if (timeStamp != price.timeStamp)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		int result = (int) (priceId ^ (priceId >>> 32));
		result = (int) (31 * result + timeStamp);
		return result;
	}
}