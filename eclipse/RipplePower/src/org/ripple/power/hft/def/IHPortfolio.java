package org.ripple.power.hft.def;

public interface IHPortfolio {

	double getInitialCash();

	double getAvailableCash();

	double getTotalReturn();

	double getDailyReturn();

	double getMarketValue();

	double getPortfolioValue();

	double getProfitAndLoss();

	double getAnnualizedAvgReturns();

	double getDividendReceivable();

}
