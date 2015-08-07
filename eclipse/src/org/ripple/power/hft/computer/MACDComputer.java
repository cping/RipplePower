package org.ripple.power.hft.computer;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;

public class MACDComputer {

	private Core core = new Core();

	/**
	 * 计算macd
	 * 
	 * @param close
	 * @param fastPeriod
	 * @param slowPeriod
	 * @param signalPeriod
	 * @return 2维数组, [0]macd值, [1]signal值, [2]histogram
	 */
	public double[][] computeMACD(double[] close, int fastPeriod,
			int slowPeriod, int signalPeriod) {
		MInteger begin = new MInteger();
		MInteger length = new MInteger();
		int resultLegnth = close.length - (slowPeriod - 1) - (signalPeriod - 1);
		double[] values = new double[resultLegnth];
		double[] signals = new double[resultLegnth];
		double[] histograms = new double[resultLegnth];

		double[][] result = new double[3][resultLegnth];
		result[0] = values;
		result[1] = signals;
		result[2] = histograms;

		core.macd(0, close.length - 1, close, fastPeriod, slowPeriod,
				signalPeriod, begin, length, values, signals, histograms);

		return result;
	}

}
