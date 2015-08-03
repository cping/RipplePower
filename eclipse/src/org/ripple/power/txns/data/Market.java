package org.ripple.power.txns.data;

import java.util.ArrayList;
import java.util.List;

import org.ripple.power.config.LSystem;

public class Market {
	public List<Ask> Asks = new ArrayList<Ask>(LSystem.DEFAULT_MAX_CACHE_SIZE);
	public List<Bid> Bids = new ArrayList<Bid>(LSystem.DEFAULT_MAX_CACHE_SIZE);
}
