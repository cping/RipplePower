package org.ripple.power.hft.def;

public interface IHPosition {

	double getBoughtQuantity();

	double getSoldQuantity();

	double getBoughtValue();

	double getSoldValue();

	double getNonClosedTradeQuantity();

	double getTotalOrders();

	double getTotalTrades();

}
