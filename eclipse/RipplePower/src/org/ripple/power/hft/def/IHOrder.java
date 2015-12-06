package org.ripple.power.hft.def;

public interface IHOrder {

	IHInstrument getInstrument();

	double getPrice();

	double getFilledShares();

	double getFilledLots();

}
