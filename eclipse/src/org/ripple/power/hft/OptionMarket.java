package org.ripple.power.hft;

public class OptionMarket extends Market {
	private final OptionType theType;
	private final double theStrike;
	private final double thePrice;
	private final double theChange;
	private final int theQuantity;
	private final int theOpenInterest;
	private final long theExpiry;

	public OptionMarket(OptionType aType, double aStrike,
			OptionQuote aBidQuote, OptionQuote aAskQuote, double aPrice,
			double aChange, int aQuantity, int aOpenInterest, long aExpiry) {
		super(aBidQuote, aAskQuote);
		theType = aType;
		theStrike = aStrike;
		thePrice = aPrice;
		theChange = aChange;
		theQuantity = aQuantity;
		theOpenInterest = aOpenInterest;
		theExpiry = aExpiry;
	}

	public OptionQuote getBid() {
		return (OptionQuote) theBidQuote;
	}

	public OptionQuote getAsk() {
		return (OptionQuote) theAskQuote;
	}

	public double getStrike() {
		return theStrike;
	}

	public OptionType getType() {
		return theType;
	}

	public double getPrice() {
		return thePrice;
	}

	public double getChange() {
		return theChange;
	}

	public int getQuantity() {
		return theQuantity;
	}

	public int getOpenInterest() {
		return theOpenInterest;
	}

	public long getExpiry() {
		return theExpiry;
	}
}
