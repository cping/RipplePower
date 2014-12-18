package org.ripple.power.hft;

public class SimpleOrder implements Order{
    
	double bid;
    double ask;

    String id;
    
    SimpleOrder(double bid, double ask) {
        this.bid = bid;
        this.ask = ask;
        this.id = String.valueOf(System.currentTimeMillis());
    }

	@Override
	public String getOrderId() {
		return id;
	}
    
}
