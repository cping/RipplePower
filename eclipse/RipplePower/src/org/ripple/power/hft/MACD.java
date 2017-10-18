package org.ripple.power.hft;

public class MACD {

	public static class Period {
		public final int fastPeriod;
		public final int slowPeriod;
		public final int period;

		private Period(int fastPeriod, int slowPeriod, int period) {
			this.fastPeriod = fastPeriod;
			this.slowPeriod = slowPeriod;
			this.period = period;
		}

		public static Period newInstance(int fastPeriod, int slowPeriod, int period) {
			return new Period(fastPeriod, slowPeriod, period);
		}

		@Override
		public int hashCode() {
			int result = 17;
			result = 31 * result + fastPeriod;
			result = 31 * result + slowPeriod;
			result = 31 * result + period;
			return result;
		}

		@Override
		public boolean equals(Object o) {
			if (o == this) {
				return true;
			}

			if (!(o instanceof Period)) {
				return false;
			}

			Period _period = (Period) o;
			return this.fastPeriod == _period.fastPeriod && this.slowPeriod == _period.slowPeriod
					&& this.period == _period.period;
		}
	}

	public static class Result {
		public final double outMACD;
		public final double outMACDSignal;
		public final double outMACDHist;

		private Result(double outMACD, double outMACDSignal, double outMACDHist) {
			this.outMACD = outMACD;
			this.outMACDSignal = outMACDSignal;
			this.outMACDHist = outMACDHist;
		}

		public static Result newInstance(double outMACD, double outMACDSignal, double outMACDHist) {
			return new Result(outMACD, outMACDSignal, outMACDHist);
		}

		@Override
		public int hashCode() {
			int result = 17;
			long _outMACD = Double.doubleToLongBits(outMACD);
			long _outMACDSignal = Double.doubleToLongBits(outMACDSignal);
			long _outMACDHist = Double.doubleToLongBits(outMACDHist);
			result = 31 * result + (int) (_outMACD ^ (_outMACD >>> 32));
			result = 31 * result + (int) (_outMACDSignal ^ (_outMACDSignal >>> 32));
			result = 31 * result + (int) (_outMACDHist ^ (_outMACDHist >>> 32));
			return result;
		}

		@Override
		public boolean equals(Object o) {
			if (o == this) {
				return true;
			}

			if (!(o instanceof Result)) {
				return false;
			}

			Result macdResult = (Result) o;
			return this.outMACD == macdResult.outMACD && this.outMACDSignal == macdResult.outMACDSignal
					&& this.outMACDHist == macdResult.outMACDHist;
		}
	}
}
