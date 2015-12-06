package org.ripple.power.hft.def;

public interface IHTransactionFactory {
	
	IHOrderQuantityPicker buy(String idOrSymbol);

	IHOrderQuantityPicker sell(String idOrSymbol);
	
}
