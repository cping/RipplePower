/*
 * Copyright 2013 Valentyn Kolesnikov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ripple.power.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;

public final class BigUtils {
	private static final int SCALE = 18;
	public static long ITER = 1000;
	public static MathContext context = new MathContext(100);
	private static final int ROUNDING_MODE = BigDecimal.ROUND_HALF_EVEN;
	public static BigDecimal PI_DIV_180 = new BigDecimal(
			"3.1415926535897932384626433832795").divide(
			BigDecimal.valueOf(180), 32, BigDecimal.ROUND_HALF_UP);
	public static BigDecimal PI_DIV_200 = new BigDecimal(
			"3.1415926535897932384626433832795").divide(
			BigDecimal.valueOf(200), 32, BigDecimal.ROUND_HALF_UP);
	public static BigDecimal EPS = BigDecimal.ONE.scaleByPowerOfTen(-100);

	public static final BigInteger TWO = new BigInteger("2");
	public static final BigInteger THREE = TWO.add(BigInteger.ONE);
	public static final BigInteger FOUR = TWO.add(TWO);
	public static final BigInteger SEVEN = FOUR.add(THREE);
	public static final BigInteger EIGHT = FOUR.add(FOUR);

	private BigUtils() {
	}

	static BigInteger inverseMod(BigInteger a, BigInteger b) {
		BigInteger b0 = b, t, q;
		BigInteger x0 = BigInteger.ZERO, x1 = BigInteger.ONE;
		if (b.equals(BigInteger.ONE))
			return BigInteger.ONE;
		while (a.subtract(BigInteger.ONE).signum() > 0) {
			q = a.divide(b);
			t = b;
			b = a.mod(b);
			a = t;
			t = x0;
			x0 = x1.subtract(q.multiply(x0));
			x1 = t;
		}
		if (x1.signum() < 0)
			x1 = x1.add(b0);
		return x1;
	}

	static BigInteger mulmod(BigInteger a, BigInteger b, BigInteger p) {
		BigInteger r = BigInteger.ZERO;
		while (b.compareTo(BigInteger.ZERO) > 0) {
			if (!b.and(BigInteger.ONE).equals(BigInteger.ZERO)) {
				r = addmod(r, a, p);
			}
			b = b.shiftRight(1);
			a = addmod(a, a, p);
		}
		return r;
	}

	static BigInteger addmod(BigInteger a, BigInteger b, BigInteger p) {
		if (p.subtract(b).compareTo(a) > 0) {
			return a.add(b);
		} else {
			return a.add(b).subtract(p);
		}
	}

	static BigInteger submod(BigInteger a, BigInteger b, BigInteger p) {
		if (a.compareTo(b) >= 0) {
			return a.subtract(b);
		} else {
			return p.subtract(b).add(a);
		}
	}

	static BigInteger powmod(BigInteger a, BigInteger e, BigInteger p) {
		BigInteger r = BigInteger.ONE;
		while (e.compareTo(BigInteger.ZERO) > 0) {
			if (!e.and(BigInteger.ONE).equals(BigInteger.ZERO)) {
				r = mulmod(r, a, p);
			}
			e = e.shiftRight(1);
			a = mulmod(a, a, p);
		}
		return r;
	}

	static int Jacobi(BigInteger m, BigInteger n) {
		if (m.compareTo(n) >= 0) {
			m = m.mod(n);
			return Jacobi(m, n);
		}
		if (n.equals(BigInteger.ONE) || m.equals(BigInteger.ONE)) {
			return 1;
		}
		if (m.equals(BigInteger.ZERO)) {
			return 0;
		}
		int twoCount = 0;
		while (m.mod(TWO) == BigInteger.ZERO) {
			twoCount++;
			m = m.divide(TWO);
		}
		int J2n = n.mod(EIGHT).equals(BigInteger.ONE)
				|| n.mod(EIGHT).equals(SEVEN) ? 1 : -1;
		int rule8multiplier = (twoCount % 2 == 0) ? 1 : J2n;
		int tmp = Jacobi(n, m);
		int rule6multiplier = n.mod(FOUR).equals(BigInteger.ONE)
				|| m.mod(FOUR).equals(BigInteger.ONE) ? 1 : -1;
		return tmp * rule6multiplier * rule8multiplier;
	}

	static int eulerCriterion(BigInteger p, BigInteger a) {
		BigInteger exponent = (p.subtract(BigInteger.ONE)).divide(TWO);
		BigInteger x = a.modPow(exponent, p);
		if (x.equals(BigInteger.ZERO) || x.equals(BigInteger.ONE)) {
			return x.intValue();
		}
		BigInteger y = x.add(BigInteger.ONE).mod(p);
		return (y.equals(BigInteger.ZERO)) ? -1 : 2;
	}

	/**
	 * Compute the square root of x to a given scale, x >= 0. Use Newton's
	 * algorithm.
	 * 
	 * @param x
	 *            the value of x
	 * @return the result value
	 */
	public static BigDecimal sqrt(BigDecimal x) {
		// Check that x >= 0.
		if (x.signum() < 0) {
			throw new ArithmeticException("x < 0");
		}

		// n = x*(10^(2*SCALE))
		BigInteger n = x.movePointRight(SCALE << 1).toBigInteger();

		// The first approximation is the upper half of n.
		int bits = (n.bitLength() + 1) >> 1;
		BigInteger ix = n.shiftRight(bits);
		BigInteger ixPrev;

		// Loop until the approximations converge
		// (two successive approximations are equal after rounding).
		do {
			ixPrev = ix;

			// x = (x + n/x)/2
			ix = ix.add(n.divide(ix)).shiftRight(1);

			Thread.yield();
		} while (ix.compareTo(ixPrev) != 0);

		return new BigDecimal(ix, SCALE);
	}

	/**
	 * Compute the integral root of x to a given scale, x >= 0. Use Newton's
	 * algorithm.
	 * 
	 * @param x
	 *            the value of x
	 * @param index
	 *            the integral root value
	 * @param scale
	 *            the desired scale of the result
	 * @return the result value
	 */
	public static BigDecimal intRoot(BigDecimal x, long index, int scale) {
		// Check that x >= 0.
		if (x.signum() < 0) {
			throw new IllegalArgumentException("x < 0");
		}

		int sp1 = scale + 1;
		BigDecimal n = x;
		BigDecimal i = BigDecimal.valueOf(index);
		BigDecimal im1 = BigDecimal.valueOf(index - 1);
		BigDecimal tolerance = BigDecimal.valueOf(5).movePointLeft(sp1);
		BigDecimal xPrev;

		// The initial approximation is x/index.
		x = x.divide(i, scale, BigDecimal.ROUND_HALF_EVEN);

		// Loop until the approximations converge
		// (two successive approximations are equal after rounding).
		do {
			// x^(index-1)
			BigDecimal xToIm1 = intPower(x, index - 1, sp1);

			// x^index
			BigDecimal xToI = x.multiply(xToIm1).setScale(sp1,
					BigDecimal.ROUND_HALF_EVEN);

			// n + (index-1)*(x^index)
			BigDecimal numerator = n.add(im1.multiply(xToI)).setScale(sp1,
					BigDecimal.ROUND_HALF_EVEN);

			// (index*(x^(index-1))
			BigDecimal denominator = i.multiply(xToIm1).setScale(sp1,
					BigDecimal.ROUND_HALF_EVEN);

			// x = (n + (index-1)*(x^index)) / (index*(x^(index-1)))
			xPrev = x;
			x = numerator.divide(denominator, sp1, BigDecimal.ROUND_DOWN);

			Thread.yield();
		} while (x.subtract(xPrev).abs().compareTo(tolerance) > 0);

		return x;
	}

	/**
	 * Compute the natural logarithm of x to a given scale, x > 0.
	 */
	public static BigDecimal ln(BigDecimal x, int scale) {
		// Check that x > 0.
		if (x.signum() <= 0) {
			throw new IllegalArgumentException("x <= 0");
		}

		// The number of digits to the left of the decimal point.
		int magnitude = x.toString().length() - x.scale() - 1;

		if (magnitude < 3) {
			return lnNewton(x, scale);
		}

		// Compute magnitude*ln(x^(1/magnitude)).
		else {

			// x^(1/magnitude)
			BigDecimal root = intRoot(x, magnitude, scale);

			// ln(x^(1/magnitude))
			BigDecimal lnRoot = lnNewton(root, scale);

			// magnitude*ln(x^(1/magnitude))
			return BigDecimal.valueOf(magnitude).multiply(lnRoot)
					.setScale(scale, BigDecimal.ROUND_HALF_EVEN);
		}
	}

	/**
	 * Compute the natural logarithm of x to a given scale, x > 0. Use Newton's
	 * algorithm.
	 */
	private static BigDecimal lnNewton(BigDecimal x, int scale) {
		int sp1 = scale + 1;
		BigDecimal n = x;
		BigDecimal term;

		// Convergence tolerance = 5*(10^-(scale+1))
		BigDecimal tolerance = BigDecimal.valueOf(5).movePointLeft(sp1);

		// Loop until the approximations converge
		// (two successive approximations are within the tolerance).
		do {

			// e^x
			BigDecimal eToX = exp(x, sp1);

			// (e^x - n)/e^x
			term = eToX.subtract(n).divide(eToX, sp1, BigDecimal.ROUND_DOWN);

			// x - (e^x - n)/e^x
			x = x.subtract(term);

			Thread.yield();
		} while (term.compareTo(tolerance) > 0);

		return x.setScale(scale, BigDecimal.ROUND_HALF_EVEN);
	}

	public static BigDecimal cosine(BigDecimal x) {

		BigDecimal currentValue = BigDecimal.ONE;
		BigDecimal lastVal = currentValue.add(BigDecimal.ONE);
		BigDecimal xSquared = x.multiply(x);
		BigDecimal numerator = BigDecimal.ONE;
		BigDecimal denominator = BigDecimal.ONE;
		int i = 0;

		while (lastVal.compareTo(currentValue) != 0) {
			lastVal = currentValue;

			int z = 2 * i + 2;

			denominator = denominator.multiply(BigDecimal.valueOf(z));
			denominator = denominator.multiply(BigDecimal.valueOf(z - 1));
			numerator = numerator.multiply(xSquared);

			BigDecimal term = numerator.divide(denominator, SCALE + 5,
					ROUNDING_MODE);

			if (i % 2 == 0) {
				currentValue = currentValue.subtract(term);
			} else {
				currentValue = currentValue.add(term);
			}
			i++;
		}

		return currentValue;
	}

	public static BigDecimal sine(BigDecimal x) {
		BigDecimal lastVal = x.add(BigDecimal.ONE);
		BigDecimal currentValue = x;
		BigDecimal xSquared = x.multiply(x);
		BigDecimal numerator = x;
		BigDecimal denominator = BigDecimal.ONE;
		int i = 0;

		while (lastVal.compareTo(currentValue) != 0) {
			lastVal = currentValue;

			int z = 2 * i + 3;

			denominator = denominator.multiply(BigDecimal.valueOf(z));
			denominator = denominator.multiply(BigDecimal.valueOf(z - 1));
			numerator = numerator.multiply(xSquared);

			BigDecimal term = numerator.divide(denominator, SCALE + 5,
					ROUNDING_MODE);

			if (i % 2 == 0) {
				currentValue = currentValue.subtract(term);
			} else {
				currentValue = currentValue.add(term);
			}

			i++;
		}
		return currentValue;
	}

	public static BigDecimal tangent(BigDecimal x) {

		BigDecimal sin = sine(x);
		BigDecimal cos = cosine(x);

		return sin.divide(cos, SCALE, BigDecimal.ROUND_HALF_UP);
	}

	public static BigDecimal log10(BigDecimal b) {
		final int NUM_OF_DIGITS = SCALE + 2;
		// need to add one to get the right number of dp
		// and then add one again to get the next number
		// so I can round it correctly.

		MathContext mc = new MathContext(NUM_OF_DIGITS, RoundingMode.HALF_EVEN);
		// special conditions:
		// log(-x) -> exception
		// log(1) == 0 exactly;
		// log of a number lessthan one = -log(1/x)
		if (b.signum() <= 0) {
			throw new ArithmeticException("log of a negative number! (or zero)");
		} else if (b.compareTo(BigDecimal.ONE) == 0) {
			return BigDecimal.ZERO;
		} else if (b.compareTo(BigDecimal.ONE) < 0) {
			return (log10((BigDecimal.ONE).divide(b, mc))).negate();
		}

		StringBuilder sb = new StringBuilder();
		// number of digits on the left of the decimal point
		int leftDigits = b.precision() - b.scale();

		// so, the first digits of the log10 are:
		sb.append(leftDigits - 1).append(".");

		// this is the algorithm outlined in the webpage
		int n = 0;
		while (n < NUM_OF_DIGITS) {
			b = (b.movePointLeft(leftDigits - 1)).pow(10, mc);
			leftDigits = b.precision() - b.scale();
			sb.append(leftDigits - 1);
			n++;
		}

		BigDecimal ans = new BigDecimal(sb.toString());

		// Round the number to the correct number of decimal places.
		ans = ans.round(new MathContext(ans.precision() - ans.scale() + SCALE,
				RoundingMode.HALF_EVEN));
		return ans;
	}

	public static BigDecimal cuberoot(BigDecimal b) {
		// Specify a math context with 40 digits of precision.

		MathContext mc = new MathContext(40);

		BigDecimal x = new BigDecimal("1", mc);

		// Search for the cube root via the Newton-Raphson loop. Output each //
		// successive iteration's value.

		for (int i = 0; i < ITER; i++) {
			x = x.subtract(
					x.pow(3, mc)
							.subtract(b, mc)
							.divide(new BigDecimal("3", mc).multiply(
									x.pow(2, mc), mc), mc), mc);
		}
		return x;
	}

	public static BigDecimal pow(BigDecimal savedValue, BigDecimal value) {
		BigDecimal result = null;
		result = exp(ln(savedValue, 32).multiply(value), 32);
		return result;
	}

	/**
	 * Compute x^exponent to a given scale. Uses the same algorithm as class
	 * numbercruncher.mathutils.IntPower.
	 * 
	 * @param x
	 *            the value x
	 * @param exponent
	 *            the exponent value
	 * @param scale
	 *            the desired scale of the result
	 * @return the result value
	 */
	public static BigDecimal intPower(BigDecimal x, long exponent, int scale) {
		// If the exponent is negative, compute 1/(x^-exponent).
		if (exponent < 0) {
			return BigDecimal.valueOf(1).divide(intPower(x, -exponent, scale),
					scale, BigDecimal.ROUND_HALF_EVEN);
		}

		BigDecimal power = BigDecimal.valueOf(1);

		// Loop to compute value^exponent.
		while (exponent > 0) {

			// Is the rightmost bit a 1?
			if ((exponent & 1) == 1) {
				power = power.multiply(x).setScale(scale,
						BigDecimal.ROUND_HALF_EVEN);
			}

			// Square x and shift exponent 1 bit to the right.
			x = x.multiply(x).setScale(scale, BigDecimal.ROUND_HALF_EVEN);
			exponent >>= 1;

			Thread.yield();
		}

		return power;
	}

	/**
	 * Compute e^x to a given scale. Break x into its whole and fraction parts
	 * and compute (e^(1 + fraction/whole))^whole using Taylor's formula.
	 * 
	 * @param x
	 *            the value of x
	 * @param scale
	 *            the desired scale of the result
	 * @return the result value
	 */
	public static BigDecimal exp(BigDecimal x, int scale) {
		// e^0 = 1
		if (x.signum() == 0) {
			return BigDecimal.valueOf(1);
		}

		// If x is negative, return 1/(e^-x).
		else if (x.signum() == -1) {
			return BigDecimal.valueOf(1).divide(exp(x.negate(), scale), scale,
					BigDecimal.ROUND_HALF_EVEN);
		}

		// Compute the whole part of x.
		BigDecimal xWhole = x.setScale(0, BigDecimal.ROUND_DOWN);

		// If there isn't a whole part, compute and return e^x.
		if (xWhole.signum() == 0) {
			return expTaylor(x, scale);
		}

		// Compute the fraction part of x.
		BigDecimal xFraction = x.subtract(xWhole);

		// z = 1 + fraction/whole
		BigDecimal z = BigDecimal.valueOf(1).add(
				xFraction.divide(xWhole, scale, BigDecimal.ROUND_HALF_EVEN));

		// t = e^z
		BigDecimal t = expTaylor(z, scale);

		BigDecimal maxLong = BigDecimal.valueOf(Long.MAX_VALUE);
		BigDecimal result = BigDecimal.valueOf(1);

		// Compute and return t^whole using intPower().
		// If whole > Long.MAX_VALUE, then first compute products
		// of e^Long.MAX_VALUE.
		while (xWhole.compareTo(maxLong) >= 0) {
			result = result.multiply(intPower(t, Long.MAX_VALUE, scale))
					.setScale(scale, BigDecimal.ROUND_HALF_EVEN);
			xWhole = xWhole.subtract(maxLong);

			Thread.yield();
		}
		return result.multiply(intPower(t, xWhole.longValue(), scale))
				.setScale(scale, BigDecimal.ROUND_HALF_EVEN);
	}

	/**
	 * Compute e^x to a given scale by the Taylor series.
	 * 
	 * @param x
	 *            the value of x
	 * @param scale
	 *            the desired scale of the result
	 * @return the result value
	 */
	private static BigDecimal expTaylor(BigDecimal x, int scale) {
		BigDecimal factorial = BigDecimal.valueOf(1);
		BigDecimal xPower = x;
		BigDecimal sumPrev;

		// 1 + x
		BigDecimal sum = x.add(BigDecimal.valueOf(1));

		// Loop until the sums converge
		// (two successive sums are equal after rounding).
		int i = 2;
		do {
			// x^i
			xPower = xPower.multiply(x).setScale(scale,
					BigDecimal.ROUND_HALF_EVEN);

			// i!
			factorial = factorial.multiply(BigDecimal.valueOf(i));

			// x^i/i!
			BigDecimal term = xPower.divide(factorial, scale,
					BigDecimal.ROUND_HALF_EVEN);

			// sum = sum + x^i/i!
			sumPrev = sum;
			sum = sum.add(term);

			++i;
			Thread.yield();
		} while (sum.compareTo(sumPrev) != 0);

		return sum;
	}

	public static BigDecimal asin(BigDecimal val) {
		return BigDecimal.valueOf(Math.asin(val.doubleValue()));
	}

	public static BigDecimal acos(BigDecimal val) {
		return BigDecimal.valueOf(Math.acos(val.doubleValue()));
	}

	public static BigDecimal atan(BigDecimal val) {
		return BigDecimal.valueOf(Math.atan(val.doubleValue()));
	}

	public static float getMean(ArrayList<?> list) {
		try {
			float sum = 0f;
			for (Object i : list) {
				if (i instanceof Float)
					sum += (float) i;
				else if (i instanceof Integer)
					sum += (int) i;
			}
			float mean = sum / (float) list.size();
			return mean;
		} catch (Exception e) {
			e.printStackTrace();
			return -1f;
		}
	}

	public static float getStandardDeviation(ArrayList<?> list) {
		try {
			if (list.size() <= 0) {
				return -1f;
			}
			float sum = 0f;
			for (Object i : list) {
				if (i instanceof Float) {
					sum += (float) i;
				}
				if (i instanceof Integer) {
					sum += (int) i;
				}
			}
			float mean = sum / list.size();

			// Deviations
			ArrayList<Float> deviations = new ArrayList<Float>();
			for (Object i : list) {
				if (i instanceof Float) {
					deviations.add((float) i - mean);
				}
				if (i instanceof Integer) {
					deviations.add((int) i - mean);
				}
			}

			// Deviation Squares
			ArrayList<Float> deviationSquares = new ArrayList<Float>();
			for (Float f : deviations) {
				float square = f * f;
				deviationSquares.add(square);
			}

			// Sum of Deviation Squares
			float sumds = 0f;
			for (Float f : deviationSquares) {
				sumds += f;
			}

			float d = sumds / (float) (deviationSquares.size() - 1);
			float stdev = (float) Math.sqrt(d);

			return stdev;
		} catch (Exception e) {
			e.printStackTrace();
			return -1f;
		}
	}

	public static ArrayList<Float> removePositives(ArrayList<?> list) {
		ArrayList<Float> rlist = new ArrayList<Float>();
		try {
			for (Object i : list) {
				float n = (float) i;
				if (n <= 0)
					rlist.add(n);
				else {
					rlist.add(0f);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rlist;
	}

	public static float getMaxDrawdownPercent(ArrayList<?> list) {
		try {
			float samplePortfolioValue = 1000f;
			float drawdownBase = samplePortfolioValue;
			float maxDrawdown = 0f;
			float lowestValue = samplePortfolioValue;
			float highestValue = samplePortfolioValue;
			for (Object i : list) {
				samplePortfolioValue *= (1f + ((float) i / 100f));
				if (samplePortfolioValue < drawdownBase) {
					lowestValue = samplePortfolioValue;
					float thisDrawdown = 1f - (lowestValue / drawdownBase);
					if (thisDrawdown > maxDrawdown) {
						maxDrawdown = thisDrawdown;
					}
				}
				if (samplePortfolioValue > highestValue) {
					highestValue = samplePortfolioValue;
					drawdownBase = highestValue;
				}
			}
			return -maxDrawdown * 100f;
		} catch (Exception e) {
			e.printStackTrace();
			return -1f;
		}
	}

	public static float getMedian(ArrayList<?> list) {
		try {
			ArrayList<Float> flist = new ArrayList<Float>();
			ArrayList<Integer> ilist = new ArrayList<Integer>();
			for (Object i : list) {
				if (i instanceof Float) {
					flist.add((float) i);
				} else if (i instanceof Integer) {
					ilist.add((int) i);
				}
			}
			if (flist.size() > 0) {
				Collections.sort(flist);
				if (flist.size() % 2 == 1) {
					return flist.get((flist.size() + 1) / 2 - 1);
				} else {
					float lower = flist.get(flist.size() / 2 - 1);
					float upper = flist.get(flist.size() / 2);
					return (lower + upper) / 2f;
				}
			} else if (ilist.size() > 0) {
				Collections.sort(ilist);
				if (ilist.size() % 2 == 1) {
					return ilist.get((ilist.size() + 1) / 2 - 1);
				} else {
					float lower = ilist.get(ilist.size() / 2 - 1);
					float upper = ilist.get(ilist.size() / 2);
					return (lower + upper) / 2f;
				}
			} else {
				return -1f;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -1f;
		}
	}

	/**
	 * Note: Taking the geomean of percents vs. their multiplier equivalents is
	 * not the same. Example: (5, 7, -4) != (1.05, 1.07, .96)
	 * 
	 * But it's the best you can do when negative numbers are in the mix. Just
	 * note it.
	 *
	 * @param list
	 * @return
	 */
	public static float getGeoMean(ArrayList<?> list) {
		try {
			float logSum = 0f;
			for (Object i : list) {
				if (i instanceof Float) {
					float n = (float) i;
					n = 1f + (n / 100f);

					double log = Math.log10(n);
					logSum += log;
				} else if (i instanceof Integer) {
					float n = (int) i;
					n = 1f + (n / 100f);

					double log = Math.log10(n);
					logSum += log;
				}
			}
			float meanLog = logSum / (float) list.size();
			float geoMean = (float) Math.pow(10, meanLog);
			float answer = (geoMean - 1f) * 100f;
			return answer;
		} catch (Exception e) {
			e.printStackTrace();
			return -1f;
		}
	}

	public static float getWinPercent(ArrayList<?> list) {
		try {
			int numWinners = 0;
			for (Object i : list) {
				if (i instanceof Float)
					if ((float) i > 0)
						numWinners++;
					else if (i instanceof Integer)
						if ((int) i > 0)
							numWinners++;
			}
			float winPercent = numWinners / (float) list.size() * 100;
			return winPercent;
		} catch (Exception e) {
			e.printStackTrace();
			return -1f;
		}
	}

	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	public static boolean isFloat(String s) {
		try {
			Float.parseFloat(s);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

}
