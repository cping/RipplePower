package org.ripple.power.hft.def;

/**
 * 历史交易数据回测,它们的返回值都是 double[]，数组中的最后一位一定是当前的数据。在每日回测中，假设您想要从今天起往前5天内的数据（假设“今天”是2-15-8-7星期五，
 * 那么前5天就是指2号，周一至周五的这段时间），在回测起始的第一天，您可能看到的是这样的数组：[0, 0, 0, 0,
 * 10.5]。在超出您所选的回测区间的部分，那些数字都会被设为0。而第二天，它可能会变成[0, 0, 0, 10.5,
 * 11]。数据按时间顺序排列，早的在前面。
 * 
 */
public interface IHStatisticsHistory {

	double[] getLastPrice();

	double[] getHighPrice();

	double[] getLowPrice();

	double[] getOpeningPrice();

	double[] getClosingPrice();

}
