package org.ripple.power.hft;

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
}
