package org.ripple.power.hft;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MAType;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;
import java.util.List;

public class CalculatorBollingerBands {

	private Core core = new Core();

	public RetCode calcBollBands(InstrumentAo instrumentAo, int noOutEle,
			List<Double> upperBandList, List<Double> middleBandList,
			List<Double> lowerBandList) {
		if (noOutEle == Integer.MAX_VALUE) {
			noOutEle = instrumentAo.getPriceList().size();
		}
		int endIndex = instrumentAo.getPriceList().size() - 1;
		int startIndex = endIndex - noOutEle + 1;
		int optInTimePeriod = 20;
		double optInNbDevUp = 2;
		double optInNbDevDn = 2;
		MAType optInMAType = MAType.Sma;
		MInteger outBegIdx = new MInteger();
		outBegIdx.value = startIndex;
		MInteger outNBElement = new MInteger();
		outNBElement.value = noOutEle;
		double outRealUpperBand[] = new double[endIndex + 1];
		double outRealMiddleBand[] = new double[endIndex + 1];
		double outRealLowerBand[] = new double[endIndex + 1];
		double[] closePriceInput = new double[endIndex + 1];
		for (int index = 0; index <= endIndex; index++) {
			closePriceInput[index] = instrumentAo.getPriceList().get(index)
					.getClosePrice();
		}
		RetCode retCode = core.bbands(startIndex, endIndex, closePriceInput,
				optInTimePeriod, optInNbDevUp, optInNbDevDn, optInMAType,
				outBegIdx, outNBElement, outRealUpperBand, outRealMiddleBand,
				outRealLowerBand);
		// boolean wtf = true;
		for (int i = 0; i <= endIndex; i++) {
			upperBandList.add(outRealUpperBand[i]);
			middleBandList.add(outRealMiddleBand[i]);
			lowerBandList.add(outRealLowerBand[i]);
			/*
			 * if (!(outRealLowerBand[i] == outRealMiddleBand[i] &&
			 * outRealLowerBand[i] == outRealMiddleBand[i])) { wtf = false; }
			 */
		}
		return retCode;
	}

	public enum BBandState {
		ABOVE_UPPER_THRESHOLD, AT_UPPER_THRESHOLD, ABOVE_MEAN_THRESHOLD, BELOW_MEAN_THRESHOLD, AT_LOWER_THRESHOLD, BELOW_LOWER_THRESHOLD;
	}

	public BBandState getBBandState(Double upperBand, Double middleBand,
			Double lowerBand, Double actValue) {
		if (actValue > upperBand) {
			return BBandState.ABOVE_UPPER_THRESHOLD;
		} else if (actValue == upperBand) {
			return BBandState.AT_UPPER_THRESHOLD;
		} else if (actValue > middleBand) {
			return BBandState.ABOVE_MEAN_THRESHOLD;
		} else if (actValue > lowerBand) {
			return BBandState.BELOW_MEAN_THRESHOLD;
		} else if (actValue == lowerBand) {
			return BBandState.AT_LOWER_THRESHOLD;
		} else {
			return BBandState.BELOW_LOWER_THRESHOLD;
		}
	}

}
