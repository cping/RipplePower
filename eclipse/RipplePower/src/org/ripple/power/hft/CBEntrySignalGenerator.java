package org.ripple.power.hft;

import org.ripple.power.hft.def.HPeriod;
import org.ripple.power.hft.def.IHStatistics;

public class CBEntrySignalGenerator implements EntrySignalGenerator {

	private int period;

	public CBEntrySignalGenerator(int period) {
		super();
		this.period = period;
	}

	public double generateSignal(IHStatistics stat) {
		// 储存进去对应的数组
		double[] close = stat.history(period, HPeriod.Day).getClosingPrice();

		// 计算周期channel
		double[][] channel = computeChannel(close, period);
		double[] channelHighest = channel[0];
		double[] channelLowest = channel[1];

		// 计算当期close
		double currentClose = close[close.length - 1];

		// 当期close值是channel高值
		// 简单起见信号强度绝对值统一设置成1
		if (currentClose == channelHighest[0]) {
			return 1;
		}
		// 当期close值是channel低值
		else if (currentClose == channelLowest[0]) {
			return -1;
		}
		// 什么也不做
		else {
			return 0;
		}
	}

	/**
	 * 计算channel
	 *
	 * @param input
	 * @param period
	 * @return 2维数组, [0]是highest数组, [1]是lowest数组
	 */
	public double[][] computeChannel(double[] input, int period) {
		int resultLength = input.length - period + 1;
		double[][] out = new double[2][resultLength];
		double[] outHighest = new double[resultLength];
		double[] outLowest = new double[resultLength];
		out[0] = outHighest;
		out[1] = outLowest;

		// 初始化
		double currentLowest = input[0];
		double currentHighest = input[0];

		for (int i = 0; i < input.length; i++) {
			// 超过channel周期, 简化为取上一段channel的第一个值, 如果是最大/小值, 重新计算, 否则取当前元素与最大/小值比较
			if (i > period) {
				double dropValue = input[i - period - 1];

				// 重算最小值
				if (dropValue == currentLowest) {
					currentLowest = this.computeLowest(input, period, i
							- period);
				} else {
					currentLowest = Math.min(currentLowest, input[i]);
				}

				// 重算最大值
				if (dropValue == currentHighest) {
					currentHighest = this.computeHighest(input, period, i
							- period);
				} else {
					currentHighest = Math.max(currentHighest, input[i]);
				}

				outHighest[i - period] = currentHighest;
				outLowest[i - period] = currentLowest;
			}
			// 未到channel周期, 取当前所有元素的最大/小值
			else {
				currentLowest = Math.min(currentLowest, input[i]);
				currentHighest = Math.max(currentHighest, input[i]);

				if (i == period) {
					outHighest[0] = currentHighest;
					outLowest[0] = currentLowest;
				}
			}
		}

		return out;
	}

	private double computeLowest(double[] input, int period, int start) {
		double currentLowest = input[start];
		for (int i = start; i < start + period; i++) {
			currentLowest = Math.min(currentLowest, input[i]);
		}
		return currentLowest;
	}

	private double computeHighest(double[] input, int period, int start) {
		double currentHighest = input[start];
		for (int i = start; i < start + period; i++) {
			currentHighest = Math.max(currentHighest, input[i]);
		}
		return currentHighest;
	}
}
