package org.ripple.power.hft;

public interface OrderCxR extends Order {

	public int getSize();

	public String getOrderId();

	public double getLimitPrice();
}
