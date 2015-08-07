package org.ripple.power.hft.computer;

import java.util.Arrays;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;

public class ADXComputer {

	private Core core = new Core();

	/**
	 * 
	 * 计算adx
	 *
	 * @param close
	 *            至少150元素,150 periods are required to absorb the smoothing
	 *            techniques
	 * @param high
	 * @param low
	 * @param period
	 *            建议14
	 * @return
	 */
	public double[] computeADX(double[] close, double[] high, double[] low, int period) {
		MInteger begin = new MInteger();
		MInteger length = new MInteger();
		double[] out = new double[close.length - period * 2 + 1];

		core.adx(0, close.length - 1, high, low, close, period, begin, length, out);
		return out;
	}

	/**
	 * 根据输入的adx数组判断趋势是否在形成当中
	 * 
	 * @param adx
	 * @param trendingGrayTH
	 *            判断趋势正在形成 adx的下限值, 比如20-25一般是趋势模糊的值
	 * @Param trendingTH 强趋势下限值, 一般25
	 * @param lookbackPeriod
	 *            从最后adx元素起, 往前取多少个元素做趋势形成判断
	 * 
	 * @return
	 */
	public boolean adxTrendForming(double[] adx, double trendingGrayTH, double trendingTH,
			int lookbackPeriod) {
		double[] adxSubset = Arrays.copyOfRange(adx, adx.length - lookbackPeriod, adx.length);
		double adxSum = 0;
		double directionTH = 0.5;

		// adx均值>trendingGrayTH, 总体向上, 有adx>trendingTH 3者满足2个当作形成中
		boolean hasAdxOverTH = false;
		double directionIndex = 0;
		for (int i = 0; i < adxSubset.length - 1; i++) {
			adxSum += adxSubset[i];

			if (adxSubset[i] > trendingTH) {
				hasAdxOverTH = true;
			}

			if (adxSubset[i + 1] > adxSubset[i]) {
				directionIndex++;
			}
		}
		boolean avgOverTH = adxSum / lookbackPeriod >= trendingGrayTH;
		boolean hasUpDirection = directionIndex / lookbackPeriod > directionTH;
		boolean adxTrendBoosting = adxTrendBoosting(adxSubset, 1);

		int satisfied = 0;
		if (hasAdxOverTH) {
			satisfied++;
		}
		if (avgOverTH) {
			satisfied++;
		}
		if (hasUpDirection) {
			satisfied++;
		}
		if (adxTrendBoosting) {
			satisfied++;
		}
		return satisfied >= 2;
	}

	/**
	 * 判断adx是否快速上升中
	 * 
	 * @param adx
	 * @param coeffTH
	 * @return
	 */
	public boolean adxTrendBoosting(double[] adx, double coeffTH) {
		double min = adx[0];
		for (double adxValue : adx) {
			min = Math.min(min, adxValue);
		}

		// normalize
		WeightedObservedPoints points = new WeightedObservedPoints();
		for (int i = 0; i < adx.length; i++) {
			points.add(i, adx[i] - min);
		}

		// 1阶拟合
		PolynomialCurveFitter fitter = PolynomialCurveFitter.create(1);
		return fitter.fit(points.toList())[1] >= coeffTH;
	}

}
