package org.ripple.power.hft;

import org.ripple.power.hft.computer.MACDComputer;
import org.ripple.power.hft.def.HPeriod;
import org.ripple.power.hft.def.IHStatistics;
import org.ripple.power.hft.def.IHStatisticsHistory;

public class MACDCenterlineEntrySignalGenerator extends MACDComputer implements
		EntrySignalGenerator {

	/**
	 * >= slowPeriod + 1
	 */
	private int period;

	private int fastPeriod;

	private int slowPeriod;

	private int signalPeriod;

	@Override
	public double generateSignal(IHStatistics stat) {
		IHStatisticsHistory history = stat.history(period, HPeriod.Day);
		double[] close = history.getClosingPrice();

		double[][] result = computeMACD(close, fastPeriod, slowPeriod,
				signalPeriod);

		double[] values = result[0];

		int current = values.length - 1;
		int yesterday = values.length - 2;

		double currentMACD = values[current];
		double yesterdayMACD = values[yesterday];

		// macd上升，穿过0，买入信号
		if (currentMACD > 0 && yesterdayMACD < 0) {
			return 1;
		}
		// madc下降，穿过0，卖出信号
		else if (currentMACD < 0 && yesterdayMACD > 0) {
			return -1;
		} else {
			return 0;
		}
	}

}
