package org.ripple.power.txns;

public class BookOffer {

	public IssuedCurrency buy;
	public IssuedCurrency sell;

	public BookOffer(IssuedCurrency buy, IssuedCurrency sell) {
		this.buy = buy;
		this.sell = sell;
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
