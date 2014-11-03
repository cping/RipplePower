package org.ripple.power.hft;

public abstract class TraderBase {

	public static boolean equals(double value, double other) {
		return equals(value, other, 0.01);
	}

	public static boolean equals(double value, double other, double delta) {
		return Math.abs(value - other) < delta;
	}

	public static int suggestInterval(float madnessCoef) {
		return suggestInterval(madnessCoef, 2000, 11000);
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
