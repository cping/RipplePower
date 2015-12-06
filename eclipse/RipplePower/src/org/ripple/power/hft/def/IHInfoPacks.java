package org.ripple.power.hft.def;

public interface IHInfoPacks {

	IHInstrument instrument(String idOrSymbol);

	IHPortfolio portfolio();

	IHPosition position(IHInstrument instrument);

	IHPosition position(String idOrSymbol);
	
	IHRuntime runtime();
}
