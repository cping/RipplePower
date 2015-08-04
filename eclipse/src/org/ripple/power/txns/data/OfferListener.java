package org.ripple.power.txns.data;

import java.util.List;

public interface OfferListener {

	public void bids(List<Bid> offers);
	
	public void asks(List<Ask> offers);
}
