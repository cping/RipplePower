package org.ripple.power.hft.def;

public interface IHStatisticsUpdateHandler {
	
	void handle (IHStatisticsGroup stats, IHInfoPacks info, IHTransactionFactory trans);
	
}
