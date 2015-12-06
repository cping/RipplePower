package org.ripple.power.hft;

import org.ripple.power.hft.def.HPeriod;
import org.ripple.power.hft.def.IHStatistics;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;

public class RSIEntrySignalGenerator implements EntrySignalGenerator {

	private Core core = new Core();

	private double lowRSI;

	private double highRSI;

	// 建议250
	private int dataSetSize;

	// 建议14
	private int period;

	public RSIEntrySignalGenerator() {

	}

	public RSIEntrySignalGenerator(double lowRSI, double highRSI,
			int dataSetSize, int period) {
		super();
		this.lowRSI = lowRSI;
		this.highRSI = highRSI;
		this.dataSetSize = dataSetSize;
		this.period = period;
	}

	@Override
	public double generateSignal(IHStatistics stat) {
		double[] close = stat.history(dataSetSize, HPeriod.Day)
				.getClosingPrice();

		double[] result = computeRSI(close, period);

		double currentRSI = result[result.length - 1];

		if (currentRSI > highRSI) {
			return -1;
		} else if (currentRSI < lowRSI) {
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * rsi计算结果与input数据量大小有很大关系， input越大计算结果越有参考意义，rsi才是平滑曲线
	 *
	 * @param input
	 *            一般是250个元素
	 * @param period
	 *            建议14
	 * @return
	 */
	public double[] computeRSI(double[] input, int period) {
		MInteger begin = new MInteger();
		MInteger length = new MInteger();
		double[] out = new double[input.length - period];

		core.rsi(0, input.length - 1, input, period, begin, length, out);
		return out;
	}
}
