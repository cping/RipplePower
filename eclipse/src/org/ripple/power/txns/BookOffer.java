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

}
