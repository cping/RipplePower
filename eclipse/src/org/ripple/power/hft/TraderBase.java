package org.ripple.power.hft;

public abstract class TraderBase {
	
    public enum TradeType
    {
        BUY,
        SELL
    }

	public static boolean equals(float value, float other) {
		return equals(value, other, 0.01f);
	}

	public static boolean equals(float value, float other, float delta) {
		return Math.abs(value - other) < delta;
	}

	public static long suggestInterval(float madnessCoef) {
		return suggestInterval(madnessCoef, 2000, 11000);
	}

	public static long suggestInterval(float madnessCoef, long minInterval,
			long maxInterval) {
		if (madnessCoef <= 0.0f) {
			return maxInterval;
		}
		if (madnessCoef >= 1.0f) {
			return minInterval;
		}
		return (long) (minInterval + ((1.0f - madnessCoef) * (maxInterval - minInterval)));
	}

	public static float suggestWallVolume(float minVolume,
			float maxVolume) {
		return Math.min(minVolume, maxVolume);
	}


}
