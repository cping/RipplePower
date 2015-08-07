package org.ripple.power.hft;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

/**
 * 只处理"日"级行情
 */
public class VolatilityDayComputer {

	public static double computePriceChangeSTD(double[] open, double[] close) {
		SummaryStatistics stats = new SummaryStatistics();
		for (int i = 0; i < open.length; i++) {
			stats.addValue(close[i] - open[i]);
		}
		return stats.getStandardDeviation();
	}

	/**
	 * 以close价计算
	 *
	 * @param close
	 * @return
	 */
	public static double computePriceSTD(double[] close) {
		SummaryStatistics stats = new SummaryStatistics();
		for (int i = 0; i < close.length; i++) {
			stats.addValue(close[i]);
		}
		return stats.getStandardDeviation();
	}

	public static double computeATR(double[] close, double[] high, double[] low) {
		int today = close.length - 1;
		int yesterday = close.length - 2;

		// Today’s high minus today’s low
		double m1 = Math.abs(high[today] - low[today]);
		// Today’s high minus yesterday’s close
		double m2 = Math.abs(high[today] - close[yesterday]);
		// Yesterday’s close minus today’s low
		double m3 = Math.abs(close[yesterday] - low[today]);

		double max12 = Math.max(m1, m2);
		return Math.max(max12, m3);
	}

}
