package org.ripple.power.hft.computer;

import java.util.Arrays;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;


/**
 * 极值点计算器, 用来辅助判断局部顶或底
 *
 */
public class ExtremumComputer {

	/**
	 * 寻找input[input.length - lookbackPeriod] 到 input[input.length -
	 * 1]这些点之间有没极值点,
	 * 
	 * 返回结果int[2] result, result[0]表示是否有极值, -1表示有极小值, 1表示有极大值, 0表示没有
	 * 
	 * 如果result[0] != 0, input[input.length - result[1] - 1])是极值
	 *
	 * @param input
	 * @param lookbackPeriod
	 *            取>=5的奇数, 因为目前算法是取中间点对两边的点分别做二次曲线拟合, 两边点数量最好对称
	 * @param variancePercent
	 *            lookbackPeriod期间内最小值与最大值变动比例要超过这个百分比, 否则忽略极值
	 * @return
	 */
	public int[] findExtremum(double[] input, int lookbackPeriod, double variancePercent) {
		double[] samplePoints = Arrays.copyOfRange(input, input.length - lookbackPeriod,
				input.length);
		int centerIndex = (lookbackPeriod - 1) / 2;

		// 把x,y正则化到0-1之间
		double yMin = samplePoints[0];
		double yMax = samplePoints[0];

		for (double samplePoint : samplePoints) {
			yMin = Math.min(yMin, samplePoint);
			yMax = Math.max(yMax, samplePoint);
		}

		// 如果振幅过小,忽略
		if (yMax / yMin < 1 + variancePercent) {
			return new int[] { 0, 0 };
		} else {
			// 正则化
			double[] normalizedSamplePoints = new double[samplePoints.length];
			for (int i = 0; i < samplePoints.length; i++) {
				normalizedSamplePoints[i] = (samplePoints[i] - yMin) / (yMax - yMin);
			}

			// 以中心点分割，分两段做1阶线性拟合, 两段斜率符号要相反
			if (this.isLinearCoeffDiff(normalizedSamplePoints, centerIndex)) {
				// 插值拟合导数校验
				double[] derivatives = this.splineDerivatives(normalizedSamplePoints);

				double firstHalfCoeff = derivatives[centerIndex - 1];
				double secondHalfCoeff = derivatives[centerIndex + 1];

				int[] result = new int[2];
				result[0] = 0;
				// 同向, 非极值
				if (firstHalfCoeff * secondHalfCoeff < 0) {
					// 比较相邻值
					if (firstHalfCoeff > 0 && secondHalfCoeff < 0) {
						if (samplePoints[centerIndex] >= samplePoints[centerIndex - 1]
								&& samplePoints[centerIndex] >= samplePoints[centerIndex + 1]) {
							result[0] = 1;
							result[1] = centerIndex;
						}
					} else {
						if (samplePoints[centerIndex] <= samplePoints[centerIndex - 1]
								&& samplePoints[centerIndex] <= samplePoints[centerIndex + 1]) {
							result[0] = -1;
							result[1] = centerIndex;
						}
					}
				}

				return result;
			} else {
				return new int[] { 0, 0 };
			}
		}
	}

	private boolean isLinearCoeffDiff(double[] input, int centerIndex) {
		double xStep = 2.0 / (input.length + 1);

		WeightedObservedPoints firstHalf = new WeightedObservedPoints();
		WeightedObservedPoints secondHalf = new WeightedObservedPoints();

		for (int i = 0; i < input.length; i++) {
			if (i <= centerIndex) {
				firstHalf.add(i * xStep, input[i]);
			}

			if (i >= centerIndex) {
				secondHalf.add((i - centerIndex) * xStep, input[i]);
			}
		}

		PolynomialCurveFitter fitter = PolynomialCurveFitter.create(1);

		double[] firstHalfCoeffs = fitter.fit(firstHalf.toList());
		double[] secondHalfCoeffs = fitter.fit(secondHalf.toList());

		double firstHalfCoeff = firstHalfCoeffs[1];
		double secondHalfCoeff = secondHalfCoeffs[1];

		return firstHalfCoeff * secondHalfCoeff < 0;
	}

	/**
	 * 计算分段插值拟合的导数值
	 *
	 * @param input
	 * @return
	 */
	private double[] splineDerivatives(double[] input) {
		double xStep = 1.0 / input.length;

		double[] x = new double[input.length];
		double[] y = new double[input.length];

		for (int i = 0; i < input.length; i++) {
			x[i] = i * xStep;
			y[i] = input[i];
		}

		SplineInterpolator fitter = new SplineInterpolator();
		PolynomialSplineFunction func = fitter.interpolate(x, y);

		double[] derivatives = new double[input.length];
		for (int i = 0; i < derivatives.length; i++) {
			derivatives[i] = func.derivative().value(x[i]);
		}

		return derivatives;
	}

}