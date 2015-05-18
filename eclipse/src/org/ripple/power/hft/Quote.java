package org.ripple.power.hft;

public class Quote {
	private Side theSide;
	private int theQuantity;
	private double thePrice;

	public Quote(Side aSide, int aQuantity, double aPrice) {
		theSide = aSide;
		theQuantity = aQuantity;
		thePrice = aPrice;
	}

	public Side getSide() {
		return theSide;
	}

	public int getQuantity() {
		return theQuantity;
	}

	public double getPrice() {
		return thePrice;
	}
}
