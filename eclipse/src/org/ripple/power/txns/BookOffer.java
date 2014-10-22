package org.ripple.power.txns;

public class BookOffer {

	public IssuedCurrency buy;
	public IssuedCurrency sell;

	public long sequence;
	
	public BookOffer(IssuedCurrency buy, IssuedCurrency sell,long seq) {
		this.buy = buy;
		this.sell = sell;
		this.sequence = seq;
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
