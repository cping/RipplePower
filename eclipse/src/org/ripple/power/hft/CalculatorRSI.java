package org.ripple.power.hft;

import java.util.List;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

public class CalculatorRSI {
	private Core core = new Core();
	private int rsiPeriod;
	int noOutEle;
	double result;

	public enum RSIState {
		ABOVE_THRESHOLD, BETWEEN_THRESHOLD, BELOW_THRESHOLD;
	}

	public RetCode calcRSI(InstrumentAo instrumentAo, List<Double> resultList) {
		noOutEle = instrumentAo.getPriceList().size();
		int endIndex = instrumentAo.getPriceList().size() - 1;
		int startIndex = endIndex - noOutEle + 1;
		double[] outResult = new double[endIndex + 1];
		double[] closePriceInput = new double[endIndex + 1];
		MInteger strtOutIndex = new MInteger();
		strtOutIndex.value = startIndex;
		MInteger outNb = new MInteger();
		outNb.value = noOutEle;
		for (int index = 0; index <= endIndex; index++) {
			closePriceInput[index] = instrumentAo.getPriceList().get(index)
					.getClosePrice();
		}
		RetCode retCode = core.rsi(startIndex, endIndex, closePriceInput,
				rsiPeriod, strtOutIndex, outNb, outResult);
		for (int i = 0; i <= endIndex; i++) {
			resultList.add(outResult[i]);
		}
		return retCode;
	}

	public CalculatorRSI(int noOutEle) {
		rsiPeriod = 14;
		this.noOutEle = noOutEle;
	}

	public RSIState getRSIState(Double result) {
		if (result > 65) {
			return RSIState.ABOVE_THRESHOLD;
		} else if (result > 35) {
			return RSIState.BETWEEN_THRESHOLD;
		} else {
			return RSIState.BELOW_THRESHOLD;
		}
	}

	public void setNoOutEle(int noOutEle) {
		this.noOutEle = noOutEle;
	}
}