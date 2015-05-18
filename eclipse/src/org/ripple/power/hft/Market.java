package org.ripple.power.hft;

public class Market {
	protected final Quote theBidQuote;
	protected final Quote theAskQuote;

	public Market(Quote aBid, Quote aAsk) {
		theBidQuote = aBid;
		theAskQuote = aAsk;
	}

	public Quote getBid() {
		return theBidQuote;
	}

	public Quote getAsk() {
		return theAskQuote;
	}
}
