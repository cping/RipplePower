package org.ripple.power.hft;

public class VHFComputer {

	public double[] computeVHF(double[] close, int period) {
		double[] out = new double[close.length - period + 1];

		for (int i = 0; i < out.length; i++) {
			double lowestClose = computeLowest(close, period, i);
			double highestClose = computeHighest(close, period, i);
			double verticalDist = highestClose - lowestClose;
			double accumulatedDist = computeAccumulatedDist(close, period, i);
			out[i] = verticalDist / accumulatedDist;
		}

		return out;
	}

	private double computeLowest(double[] input, int period, int start) {
		double currentLowest = input[start];
		for (int i = start; i < start + period; i++) {
			currentLowest = Math.min(currentLowest, input[i]);
		}
		return currentLowest;
	}

	private double computeHighest(double[] input, int period, int start) {
		double currentHighest = input[start];
		for (int i = start; i < start + period; i++) {
			currentHighest = Math.max(currentHighest, input[i]);
		}
		return currentHighest;
	}

	private double computeAccumulatedDist(double[] input, int period, int start) {
		double sum = 0;
		for (int i = start + 1; i < start + period; i++) {
			sum = sum + Math.abs(input[i] - input[i - 1]);
		}
		return sum;
	}
}
