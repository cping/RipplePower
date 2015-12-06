package org.ripple.power.hft;

import org.ripple.power.hft.computer.MAComputer;
import org.ripple.power.hft.def.HPeriod;
import org.ripple.power.hft.def.IHStatistics;

public class VBEntrySignalGenerator implements EntrySignalGenerator {

	public static final int VMT_PRICE_CHANGE_STD = 1;

	public static final int VMT_PRICE_STD = 2;

	public static final int VMT_ATR = 3;

	public static final int RVT_PREVIOUS_CLOSE = 1;

	public static final int RVT_CURRENT_OPEN = 2;

	public static final int RVT_CLOSE_MA = 3;

	private int period;

	private int refType;

	private int vmType;

	private double volatilityMultiplier;

	public VBEntrySignalGenerator(int period, int refType, int vmType, double volatilityMultiplier) {
		super();
		this.period = period;
		this.refType = refType;
		this.vmType = vmType;
		this.volatilityMultiplier = volatilityMultiplier;
	}

	@Override
	public double generateSignal(IHStatistics stat) {

		double[] close = stat.history(period, HPeriod.Day).getClosingPrice();
		double[] open = stat.history(period, HPeriod.Day).getOpeningPrice();
		double[] high = stat.history(period, HPeriod.Day).getHighPrice();
		double[] low = stat.history(period, HPeriod.Day).getLowPrice();

		double refValue = computeReferenceValue(open, close, refType);
		double volatilityMeasure = computeVolatilityMeasure(open, close, high, low, vmType);

		double upperTrigger = refValue + volatilityMultiplier * volatilityMeasure;
		double lowerTrigger = refValue - volatilityMultiplier * volatilityMeasure;

		double todayClose = close[close.length - 1];

		if (todayClose > upperTrigger) {
			return 1;
		}
		else if (todayClose < lowerTrigger) {
			return -1;
		} else {
			return 0;
		}
	}

	public double computeVolatilityMeasure(double[] open, double[] close, double[] high,
			double[] low, int volatilityMeasureType) {
		switch (volatilityMeasureType) {
		case VMT_PRICE_CHANGE_STD:
			return VolatilityDayComputer.computePriceChangeSTD(open, close);
		case VMT_PRICE_STD:
			return VolatilityDayComputer.computePriceSTD(close);
		case VMT_ATR:
			return VolatilityDayComputer.computeATR(close, high, low);
		default:
			throw new RuntimeException("unsupported VolatilityMeasureType " + volatilityMeasureType);
		}
	}

	public double computeReferenceValue(double[] open, double[] close, int referenceValueType) {
		switch (referenceValueType) {
		case RVT_PREVIOUS_CLOSE:
			return close[close.length - 2];
		case RVT_CURRENT_OPEN:
			return open[open.length - 1];
		case RVT_CLOSE_MA:
			int maPeriod = 5;
			double[] sma = MAComputer.computeMA(close, maPeriod, MAComputer.MA_TYPE_SMA);
			return sma[sma.length - 1];
		default:
			throw new RuntimeException("unsupported ReferenceValueType " + referenceValueType);
		}
	}

}