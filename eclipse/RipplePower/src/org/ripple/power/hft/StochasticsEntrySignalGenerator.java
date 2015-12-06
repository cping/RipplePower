package org.ripple.power.hft;

import org.ripple.power.hft.def.HPeriod;
import org.ripple.power.hft.def.IHStatistics;
import org.ripple.power.hft.def.IHStatisticsHistory;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MAType;
import com.tictactec.ta.lib.MInteger;

public class StochasticsEntrySignalGenerator implements EntrySignalGenerator {

	private Core core = new Core();

	private int period;

	private int maPeriod;

	private double oversold;

	private double overbought;

	@Override
	public double generateSignal(IHStatistics stat) {
		IHStatisticsHistory history = stat.history(period + 1, HPeriod.Day);
		double[] close = history.getClosingPrice();
		double[] high = history.getHighPrice();
		double[] low = history.getLowPrice();

		double[][] result = computeSlowStochastic(close, high, low, period,
				maPeriod);
		double[] slowK = result[0];
		double[] slowD = result[1];

		double lastSlowK = slowK[result.length - 2];
		double lastSlowD = slowD[result.length - 2];

		double currentSlowK = slowK[result.length - 1];
		double currentSlowD = slowD[result.length - 1];

		// 计算当天的SlowK与SlowD的差值
		double previousDelta = lastSlowK - lastSlowD;
		double delta = currentSlowK - currentSlowD;
		double slowKDelta = currentSlowK - lastSlowK;

		// slowK上升, 从下方穿过slowD, slowK > overSold, 买入信号
		if (slowKDelta > 0 && previousDelta < 0 && delta > 0
				&& currentSlowK > oversold) {
			return 1;
		}
		// slowK下降, 从上方穿过slowD, slowK < overbought, 卖出信号
		else if (slowKDelta < 0 && previousDelta > 0 && delta < 0
				&& currentSlowK < overbought) {
			return -1;
		} else {
			return 0;
		}
	}

	/**
	 * 计算SlowStochastic
	 *
	 * @param close
	 * @param high
	 * @param low
	 * @param period
	 * @param maPeriod
	 * @return 2维数组, [0]是slowK, [1]是slowD
	 */
	public double[][] computeSlowStochastic(double[] close, double[] high,
			double[] low, int period, int maPeriod) {
		MInteger begin = new MInteger();
		MInteger length = new MInteger();
		int resultLegnth = close.length - period + 1;
		double[] slowK = new double[resultLegnth];
		double[] slowD = new double[resultLegnth];
		double[][] result = new double[2][resultLegnth];
		result[0] = slowK;
		result[1] = slowD;

		core.stoch(0, close.length - 1, high, low, close, period, maPeriod,
				MAType.Sma, maPeriod, MAType.Sma, begin, length, slowK, slowD);

		return result;
	}
}
