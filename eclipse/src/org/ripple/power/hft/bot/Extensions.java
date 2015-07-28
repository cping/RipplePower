package org.ripple.power.hft.bot;

import java.util.List;

public class Extensions {

	static final double def_delta = 0.0000001;
	static final int def_minInterval = 2000;
	static final int def_maxInterval = 11000;

	public static boolean eq(double value, double other, double delta) {
		return Math.abs(value - other) < delta;
	}

	public static boolean eq(double value, double other) {
		return eq(value, other, def_delta);
	}

	public static List<Object> takeLast(List<Object> source, int count) {
		int realCount = source.size();
		if (realCount < count) {
			throw new IndexOutOfBoundsException("Not enough elements");
		}
		return source.subList(0, realCount - count);
	}

	public static int suggestInterval(float madnessCoef) {
		return suggestInterval(madnessCoef, def_minInterval, def_maxInterval);
	}

	public static int suggestInterval(float madnessCoef, int minInterval,
			int maxInterval) {
		if (madnessCoef <= 0.0f) {
			return maxInterval;
		}
		if (madnessCoef >= 1.0f) {
			return minInterval;
		}
		return (int) (minInterval + ((1.0f - madnessCoef) * (maxInterval - minInterval)));
	}

	public static double suggestWallVolume(float madnessCoef, double minVolume,
			double maxVolue) {
		if (madnessCoef <= 0.0f) {
			return minVolume;
		}
		if (madnessCoef >= 1.0f) {
			return maxVolue;
		}
		return (minVolume + (madnessCoef * (maxVolue - minVolume)));
	}
}
