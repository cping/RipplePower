package org.ripple.power.hft;

import java.util.Set;

public interface IExchange {
	
	Set<String> getAvailableCurs();

	double getMaxBid(String security);

	double getMinAsk(String security);

	boolean buy(String security, double price);

	boolean sell(String security, double price);
}
