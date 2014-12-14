package org.ripple.power.hft;

import java.util.List;

import org.ripple.power.collection.ArrayUtils;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;

public class AnalysisData {

	public static Double createEMA(java.util.List<Double> values, int period) {
		if (period <= 0) {
			throw new IllegalArgumentException("period must be greater than 0");
		}
		final int size = values.size();
		final Core core = new Core();
		final int allocationSize = size - core.emaLookback(period);
		if (allocationSize <= 0) {
			return null;
		}
		final double[] output = new double[allocationSize];
		final MInteger outBegIdx = new MInteger();
		final MInteger outNbElement = new MInteger();
		double[] _values = ArrayUtils
				.toPrimitive(values.toArray(new Double[0]));
		core.ema(0, values.size() - 1, _values, period, outBegIdx,
				outNbElement, output);
		return output[outNbElement.value - 1];
	}

	public static Double createMFI(List<Double> highs, List<Double> lows,
			List<Double> closes, List<Long> volumes, int period) {
		if (period <= 0) {
			throw new IllegalArgumentException("period must be greater than 0");
		}
		if (highs.size() != lows.size() || highs.size() != closes.size()
				|| highs.size() != volumes.size()) {
			throw new IllegalArgumentException("input list must be same size");
		}

		final int size = highs.size();
		final Core core = new Core();
		final int allocationSize = size - core.mfiLookback(period);
		if (allocationSize <= 0) {
			return null;
		}
		final double[] output = new double[allocationSize];
		final MInteger outBegIdx = new MInteger();
		final MInteger outNbElement = new MInteger();
		double[] _highs = ArrayUtils.toPrimitive(highs.toArray(new Double[0]));
		double[] _lows = ArrayUtils.toPrimitive(lows.toArray(new Double[0]));
		double[] _closes = ArrayUtils
				.toPrimitive(closes.toArray(new Double[0]));
		long[] _volumes = ArrayUtils.toPrimitive(volumes.toArray(new Long[0]));
		double[] dv = new double[_volumes.length];
		for (int i = 0; i < dv.length; i++) {
			dv[i] = _volumes[i];
		}
		core.mfi(0, _highs.length - 1, _highs, _lows, _closes, dv, period,
				outBegIdx, outNbElement, output);

		return output[outNbElement.value - 1];
	}

	public static Double createRSI(List<Double> values, int period) {
		if (period <= 0) {
			throw new IllegalArgumentException("period must be greater than 0");
		}
		final int size = values.size();
		final Core core = new Core();
		final int allocationSize = size - core.rsiLookback(period);
		if (allocationSize <= 0) {
			return null;
		}

		final double[] output = new double[allocationSize];
		final MInteger outBegIdx = new MInteger();
		final MInteger outNbElement = new MInteger();
		double[] _values = ArrayUtils
				.toPrimitive(values.toArray(new Double[0]));
		core.rsi(0, values.size() - 1, _values, period, outBegIdx,
				outNbElement, output);

		return output[outNbElement.value - 1];
	}

	public static double getTypicalPrice(double high, double low, double last) {
		return (high + low + last) / 3.0d;
	}

	public static double getTypicalPrice(Coin coin) {
		return (coin.getHighPrice() + coin.getLowPrice() + coin.getLastPrice()) / 3.0d;
	}

	public static MACD.Result createMACDFix(List<Double> values, int period) {
		if (period <= 0) {
			throw new IllegalArgumentException("period must be greater than 0");
		}
		final int size = values.size();
		final Core core = new Core();
		final int allocationSize = size - core.macdFixLookback(period);
		if (allocationSize <= 0) {
			return null;
		}
		final double[] outMACD = new double[allocationSize];
		final double[] outMACDSignal = new double[allocationSize];
		final double[] outMACDHist = new double[allocationSize];
		final MInteger outBegIdx = new MInteger();
		final MInteger outNbElement = new MInteger();
		double[] _values = ArrayUtils
				.toPrimitive(values.toArray(new Double[0]));

		core.macdFix(0, values.size() - 1, _values, period, outBegIdx,
				outNbElement, outMACD, outMACDSignal, outMACDHist);

		return MACD.Result.newInstance(outMACD[outNbElement.value - 1],
				outMACDSignal[outNbElement.value - 1],
				outMACDHist[outNbElement.value - 1]);
	}

}
