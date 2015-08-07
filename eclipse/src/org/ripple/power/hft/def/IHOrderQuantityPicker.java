package org.ripple.power.hft.def;

public interface IHOrderQuantityPicker {

	IHOrderBuilderBase shares(double numShares);

	IHOrderBuilderBase lots(double numLots);

	IHOrderBuilderBase percent(double percent);

}
