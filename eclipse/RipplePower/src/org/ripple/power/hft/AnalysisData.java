package org.ripple.power.hft;

import java.util.LinkedList;
import java.util.List;

import org.ripple.power.collection.ArrayUtils;
import org.ripple.power.txns.data.Candle;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;

public class AnalysisData {

	public static int getCash(int cash, int leverage, int max) {
		if (cash < 0) {
			return 0;
		} else if (cash > max) {
			cash = max;
		}

		if (leverage > max) {
			leverage = max;
		} else if (leverage < 0) {
			leverage = max;
		}

		cash = (cash * leverage) - ((cash * leverage) / 50);
		if (cash > max) {
			cash = max;
		}

		return cash;
	}

	public static int getQuantity(int maxCash, double price,
			int traderPercentage, int maxQuantity) {
		if (maxCash == 0) {
			return ((maxQuantity * traderPercentage) / 100);
		}

		else if (price == 0.0) {
			return ((maxQuantity * traderPercentage) / 100);
		}

		else if (maxQuantity == 0) {
			return ((int) (maxCash / price));
		}

		else if (traderPercentage < 0) {
			traderPercentage = 0;
		} else if (traderPercentage > 100) {
			traderPercentage = 100;
		}

		return ((int) (maxCash / price) <= ((maxQuantity * traderPercentage) / 100)) ? ((int) (maxCash / price))
				: ((maxQuantity * traderPercentage) / 100);
	}

	private static double pdf(double x) {
		return Math.exp(-x * x / 2) / Math.sqrt(2 * Math.PI);
	}

	public static double cdf(double z) {
		if (z < -15.0) {
			return 0.0;
		} else if (z > 15.0) {
			return 1.0;
		}

		double sum = 0.0, term = z;
		for (int i = 3; sum + term != sum; i += 2) {
			sum = sum + term;
			term = term * z * z / i;
		}

		return 0.5 + sum * pdf(z);
	}

	public static double dcdf(double z) {
		double epislon = 0.00001;
		return (cdf(z - epislon) - cdf(z + epislon))
				/ ((z - epislon) - (z + epislon));
	}

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

	public static double getTypicalPrice(Candle candle) {
		return (candle.high + candle.low + candle.close) / 3.0d;
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


	public static double MILLISECONDS_PER_YEAR = 1000 * 60 * 60 * 24 * 365;

	public static double convertTimeToExpiry(long aTimeToExpiry) {
		return aTimeToExpiry / MILLISECONDS_PER_YEAR;
	}

	public static double getd1(double aUnderlyingPrice, double aStrikePrice,
			double aAnnualRate, double aVolatility, double myTimeToExpiry) {
		return (Math.log(aUnderlyingPrice / aStrikePrice) + (aAnnualRate + aVolatility
				* aVolatility / 2)
				* myTimeToExpiry)
				/ (aVolatility * Math.sqrt(myTimeToExpiry));
	}

	public static double getd2(double aVolatility, double myTimeToExpiry,
			double d1) {
		return d1 - aVolatility * Math.sqrt(myTimeToExpiry);
	}

	public static double getCallOptionPrice(double aUnderlyingPrice,
			double aStrikePrice, double aAnnualRate, double aVolatility,
			long aTimeToExpiry) {
		double myTimeToExpiry = convertTimeToExpiry(aTimeToExpiry);
		double d1 = getd1(aUnderlyingPrice, aStrikePrice, aAnnualRate,
				aVolatility, myTimeToExpiry);
		double d2 = getd2(aVolatility, myTimeToExpiry, d1);

		return aUnderlyingPrice * AnalysisData.cdf(d1) - aStrikePrice
				* Math.exp(-aAnnualRate * myTimeToExpiry)
				* AnalysisData.cdf(d2);
	}

	public static double getPutOptionPrice(double aUnderlyingPrice,
			double aStrikePrice, double aAnnualRate, double aVolatility,
			long aTimeToExpiry) {
		double myTimeToExpiry = convertTimeToExpiry(aTimeToExpiry);
		double d1 = getd1(aUnderlyingPrice, aStrikePrice, aAnnualRate,
				aVolatility, myTimeToExpiry);
		double d2 = getd2(aVolatility, myTimeToExpiry, d1);

		return (1 - AnalysisData.cdf(d2)) * aStrikePrice
				* Math.exp(-aAnnualRate * myTimeToExpiry)
				- (1 - AnalysisData.cdf(d1)) * aUnderlyingPrice;
	}


	public static double getCallDelta(double aUnderlyingPrice,
			double aStrikePrice, double aAnnualRate, double aVolatility,
			long aTimeToExpiry) {
		double myTimeToExpiry = convertTimeToExpiry(aTimeToExpiry);
		double d1 = getd1(aUnderlyingPrice, aStrikePrice, aAnnualRate,
				aVolatility, myTimeToExpiry);

		return AnalysisData.cdf(d1);
	}

	public static double getPutDelta(double aUnderlyingPrice,
			double aStrikePrice, double aAnnualRate, double aVolatility,
			long aTimeToExpiry) {
		return getCallDelta(aUnderlyingPrice, aStrikePrice, aAnnualRate,
				aVolatility, aTimeToExpiry) - 1;
	}

	public static double getGamma(double aUnderlyingPrice,
			double aStrikePrice, double aAnnualRate, double aVolatility,
			long aTimeToExpiry) {
		double myTimeToExpiry = convertTimeToExpiry(aTimeToExpiry);
		double d1 = getd1(aUnderlyingPrice, aStrikePrice, aAnnualRate,
				aVolatility, myTimeToExpiry);

		return AnalysisData.dcdf(d1)
				/ (aUnderlyingPrice * aVolatility * Math.sqrt(myTimeToExpiry));
	}


	public static double getVega(double aUnderlyingPrice, double aStrikePrice,
			double aAnnualRate, double aVolatility, long aTimeToExpiry) {
		double myTimeToExpiry = convertTimeToExpiry(aTimeToExpiry);
		double d1 = getd1(aUnderlyingPrice, aStrikePrice, aAnnualRate,
				aVolatility, myTimeToExpiry);

		return AnalysisData.dcdf(d1) * aUnderlyingPrice
				* Math.sqrt(myTimeToExpiry);
	}

	public static double getCallTheta(double aUnderlyingPrice,
			double aStrikePrice, double aAnnualRate, double aVolatility,
			long aTimeToExpiry) {
		double myTimeToExpiry = convertTimeToExpiry(aTimeToExpiry);

		double d1 = getd1(aUnderlyingPrice, aStrikePrice, aAnnualRate,
				aVolatility, myTimeToExpiry);
		double d2 = getd2(aVolatility, myTimeToExpiry, d1);

		double firstTerm = -aUnderlyingPrice * AnalysisData.dcdf(d1)
				* aVolatility / ((2 * Math.sqrt(myTimeToExpiry)));

		double secondTerm = aAnnualRate * aStrikePrice
				* Math.exp(-aAnnualRate * myTimeToExpiry)
				* AnalysisData.cdf(d2);

		return firstTerm - secondTerm;
	}

	public static double getPutTheta(double aUnderlyingPrice,
			double aStrikePrice, double aAnnualRate, double aVolatility,
			long aTimeToExpiry) {
		double myTimeToExpiry = convertTimeToExpiry(aTimeToExpiry);

		double d1 = getd1(aUnderlyingPrice, aStrikePrice, aAnnualRate,
				aVolatility, myTimeToExpiry);
		double d2 = getd2(aVolatility, myTimeToExpiry, d1);

		double firstTerm = -aUnderlyingPrice * AnalysisData.dcdf(d1)
				* aVolatility / ((2 * Math.sqrt(myTimeToExpiry)));

		double secondTerm = aAnnualRate * aStrikePrice
				* Math.exp(-aAnnualRate * myTimeToExpiry)
				* AnalysisData.cdf(-d2);

		return firstTerm + secondTerm;
	}


	public static double getCallRho(double aUnderlyingPrice,
			double aStrikePrice, double aAnnualRate, double aVolatility,
			long aTimeToExpiry) {
		double myTimeToExpiry = convertTimeToExpiry(aTimeToExpiry);

		double d1 = getd1(aUnderlyingPrice, aStrikePrice, aAnnualRate,
				aVolatility, myTimeToExpiry);
		double d2 = getd2(aVolatility, myTimeToExpiry, d1);

		return aStrikePrice * myTimeToExpiry
				* Math.exp(-aAnnualRate * myTimeToExpiry)
				* AnalysisData.cdf(d2);
	}

	public static double getPutRho(double aUnderlyingPrice,
			double aStrikePrice, double aAnnualRate, double aVolatility,
			long aTimeToExpiry) {
		double myTimeToExpiry = convertTimeToExpiry(aTimeToExpiry);

		double d1 = getd1(aUnderlyingPrice, aStrikePrice, aAnnualRate,
				aVolatility, myTimeToExpiry);
		double d2 = getd2(aVolatility, myTimeToExpiry, d1);

		return -aStrikePrice * myTimeToExpiry
				* Math.exp(-aAnnualRate * myTimeToExpiry)
				* AnalysisData.cdf(-d2);

	}


	public static List<Integer> negativeWeightCycle(double[][] adjacenyMatrix,
			int source) throws IllegalArgumentException {
		if (adjacenyMatrix.length == 0
				|| adjacenyMatrix.length != adjacenyMatrix[0].length) {
			throw new IllegalArgumentException(
					"Adjaceny Matrix is not a square matrix!");
		}

		int[] predecessors = new int[adjacenyMatrix.length];
		double[] distance = new double[adjacenyMatrix.length];
		double[][] logValMat = createLogValueMatrix(adjacenyMatrix);

		for (int j = 0; j < adjacenyMatrix.length; j++) {
			distance[j] = Double.MAX_VALUE;
		}
		distance[source] = 0;

		for (int i = 0; i < logValMat.length - 1; i++) {
			relaxEdges(logValMat, distance, predecessors);
		}

		return findNegativeWeightCycle(logValMat, distance, predecessors);
	}

	private static double[][] createLogValueMatrix(double[][] adjacenyMatrix) {
		double[][] logValMat = adjacenyMatrix.clone();
		for (int i = 0; i < adjacenyMatrix.length; i++) {
			for (int j = 0; j < adjacenyMatrix[0].length; j++) {
				double weight = adjacenyMatrix[i][j];
				if (weight > 0) {
					logValMat[i][j] = -Math.log(weight);
				} else {
					logValMat[i][j] = Double.MAX_VALUE;
				}
			}
		}
		return logValMat;
	}

	private static void relaxEdges(double[][] logValMat, double[] distance,
			int[] predecessors) {

		for (int v = 0; v < logValMat.length; v++) {
			for (int j = 0; j < logValMat.length; j++) {
				double weight = logValMat[v][j];
				if (weight < Double.MAX_VALUE) {
					if (weight + distance[v] < distance[j]) {
						distance[j] = distance[v] + weight;
						predecessors[j] = v;
					}
				}
			}
		}
	}

	private static List<Integer> findNegativeWeightCycle(double[][] logValMat,
			double[] distance, int[] predecessors) {

		for (int v = 0; v < logValMat.length; v++) {
			for (int j = 0; j < logValMat[0].length; j++) {
				double weight = logValMat[v][j];
				if (weight < Double.MAX_VALUE) {
					if (weight + distance[v] < distance[j]) {
						predecessors[j] = v;
						return createCycleFromPredecessors(predecessors, j);
					}
				}
			}
		}
		return new LinkedList<Integer>();
	}

	public static List<Integer> createCycleFromPredecessors(int[] predecessors,
			int end) {
		LinkedList<Integer> path = new LinkedList<>();
		boolean[] visited = new boolean[predecessors.length];
		int current = end;
		while (true) {
			if (visited[current]) {
				LinkedList<Integer> cycle = new LinkedList<Integer>();
				cycle.addFirst(current);
				for (Integer item : path) {
					cycle.add(item);
					if (item.intValue() == current) {
						break;
					}
				}
				return cycle;
			}
			path.addFirst(current);
			visited[current] = true;
			current = predecessors[current];
		}
	}


}
