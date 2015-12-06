package org.ripple.power.hft;

import org.ripple.power.hft.def.HPeriod;
import org.ripple.power.hft.def.IHStatistics;

public class MomentumEntrySignalGenerator implements EntrySignalGenerator {

	private int period;

	public MomentumEntrySignalGenerator(int period) {
		super();
		this.period = period;
	}

	public double generateSignal(IHStatistics stat) {
		// 储存进去对应的数组
		double[] close = stat.history(period, HPeriod.Day).getClosingPrice();

		// 计算周期momentum
		double[] momentum = computeMomentum(close, period);

		// 计算当期momentum
		double currentMomentum = momentum[0];

		// 简单起见信号强度绝对值统一设置成1
		if (currentMomentum > 0) {
			return 1;
		}
		// 当期close值是channel低值
		else if (currentMomentum < 0) {
			return -1;
		}
		// 什么也不做
		else {
			return 0;
		}
	}

	public double[] computeMomentum(double[] input, int period) {
		double[] out = new double[input.length - period + 1];

		for (int i = period - 1; i < input.length; i++) {
			double start = input[i - (period - 1)];
			double end = input[i];
			out[i - (period - 1)] = end - start;
		}

		return out;
	}
}
