package org.ripple.power.hft.computer;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;

public class MAComputer {

	public static final int MA_TYPE_SMA = 1;

	public static final int MA_TYPE_EMA = 2;

	public static final int MA_TYPE_WMA = 3;

	private static Core core = new Core();

	public static double[] computeMA(double[] input, int period, int maType) {
		MInteger begin = new MInteger();
		MInteger length = new MInteger();
		double[] out = new double[input.length - period + 1];

		switch (maType) {
		case MA_TYPE_SMA:
			core.sma(0, input.length - 1, input, period, begin, length, out);
			break;
		case MA_TYPE_EMA:
			core.ema(0, input.length - 1, input, period, begin, length, out);
			break;
		case MA_TYPE_WMA:
			core.wma(0, input.length - 1, input, period, begin, length, out);
			break;
		default:
			throw new RuntimeException("unsupported MaType " + maType);
		}

		return out;
	}
}
