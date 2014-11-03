package org.ripple.power.hft;

public abstract class TraderBase {

	public static boolean equals(double value, double other) {
		return equals(value, other, 0.1);
	}

	public static boolean equals(double value, double other, double delta) {
		return Math.abs(value - other) < delta;
	}

	public static long suggestInterval(double madnessCoef) {
		return suggestInterval(madnessCoef, 2000, 11000);
	}

	public static long suggestInterval(double madnessCoef, long minInterval,
			long maxInterval) {
		if (madnessCoef <= 0.0f) {
			return maxInterval;
		}
		if (madnessCoef >= 1.0f) {
			return minInterval;
		}
		return (long) (minInterval + ((1.0f - madnessCoef) * (maxInterval - minInterval)));
	}

	public static double suggestWallVolume(double minVolume,
			double maxVolume) {
		return Math.min(minVolume, maxVolume);
	}


}
