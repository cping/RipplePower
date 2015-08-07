package org.ripple.power.hft;

public class TrendStatisticComputer {

	/**
	 * 
	 * 返回double[3][input.length - period + 1]数组，[0]是TrendStatistic, [1]是高于范围的概率,
	 * [2]是低于范围的概率, [1] + [2] = [0]
	 *
	 * [1]持续高表示上行趋势, [2]持续高表示下行趋势
	 *
	 * @param high
	 * @param low
	 * @param close
	 * @param period
	 * @return
	 */
	public double[][] computeTrendStatistic(double[] high, double[] low, double[] close, int period) {
		int resultLength = high.length - (period - 1);
		double[] ts = new double[resultLength];
		double[] higherP = new double[resultLength];
		double[] lowerP = new double[resultLength];

		double[][] result = new double[3][resultLength];
		result[0] = ts;
		result[1] = higherP;
		result[2] = lowerP;

		for (int j = 0; j < resultLength; j++) {
			int higher = 0;
			int lower = 0;
			double currentClose = close[(period - 1) + j];

			for (int i = j; i < j + period; i++) {
				if (currentClose < low[i]) {
					lower++;
				} else if (currentClose > high[i]) {
					higher++;
				}
			}

			int outside = higher + lower;
			higherP[j] = Double.valueOf(higher) / period;
			lowerP[j] = Double.valueOf(lower) / period;
			ts[j] = Double.valueOf(outside) / period;
		}

		return result;
	}

}
