package org.ripple.power.txns;

public class BookOffer {

	public IssuedCurrency buy;
	public IssuedCurrency sell;

	public long sequence;

	public long flags;

	public BookOffer(IssuedCurrency buy, IssuedCurrency sell, long seq,
			long flags) {
		this.buy = buy;
		this.sell = sell;
		this.sequence = seq;
		this.flags = flags;
	}

	public BookOffer(IssuedCurrency buy, IssuedCurrency sell, long flags) {
		this(buy, sell, System.currentTimeMillis(), flags);
	}

	public BookOffer(IssuedCurrency buy, IssuedCurrency sell) {
		this(buy, sell, System.currentTimeMillis(), 0);
	}

	public String toString() {
		return buy + " swap " + sell;
	}

	public IssuedCurrency getBuy() {
		return buy;
	}

	public void setBuy(IssuedCurrency buy) {
		this.buy = buy;
	}

	public IssuedCurrency getSell() {
		return sell;
	}

	public void setSell(IssuedCurrency sell) {
		this.sell = sell;
	}

}
