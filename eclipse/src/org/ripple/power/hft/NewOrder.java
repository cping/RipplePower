package org.ripple.power.hft;

public interface NewOrder extends Order {

	public String getSymbol();

	public int getSize();

	public String getOrderId();

	public double getLimitPrice();

}
