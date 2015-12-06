package org.ripple.power.hft.def;

public interface IHInstrument {

	String getOrderBookID();

	String getSymbol();

	String getAbbrevSymbol();

	double getRoundLot();
}
