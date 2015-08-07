package org.ripple.power.hft.def;

public interface IHStatistics {

	/**
	 * 返回一个 IHInstrument 对象，它表示这次更新是针对哪一个IOC
	 * 
	 * @return
	 */
	IHInstrument getInstrument();

	/**
	 * 返回最新的成交价
	 * 
	 * @return
	 */
	double getLastPrice();

	/**
	 * 返回当前最高成交价。在每日回测中，它总是返回当日的最高成交价。
	 * 
	 * @return
	 */
	double getHighPrice();

	/**
	 * 返回当前最低成交价。在每日回测中，它总是返回当日的最低成交价。
	 * 
	 * @return
	 */
	double getLowPrice();

	/**
	 * 返回开盘价。
	 * 
	 * @return
	 */
	double getOpeningPrice();

	/**
	 * 返回收盘价。
	 * 
	 * @return
	 */
	double getClosingPrice();

	/**
	 * 返回当日总交易的总数。
	 * 
	 * @return
	 */
	double getTurnoverVolume();

	/**
	 * vwap是volume weighted average price（成交量加权平均价）的简称，以后都会用“vwap”来代表这个概念。
	 * 
	 * @param numTicks
	 * @param tickPeriod
	 * @return
	 */
	double vwap(int numTicks, HPeriod tickPeriod);

	/**
	 * 这个方法返回指定区间的移动平均值。mavg是moving average的简写，我们之后也会用mavg来代表这个概念
	 * 
	 * @param numTicks
	 * @param tickPeriod
	 * @return
	 */
	double mavg(int numTicks, HPeriod tickPeriod);

	/**
	 * 用来访问曾经收到过的行情，这个方法是非常便捷的。目前我们只支持由当前时间开始倒数numTicks个时间段，并且包括当前时间。目前只支持每日回测，
	 * 所以HPeriod只能取HPeriod
	 * .Day。它返回了一个历史行情包，含有IHStatistics的各个属性值，但是没有vwap或mavg这些需要计算才能产生的值.
	 * 
	 * @param numTicks
	 * @param tickPeriod
	 * @return
	 */
	IHStatisticsHistory history(int numTicks, HPeriod tickPeriod);
}
