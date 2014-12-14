package org.ripple.power.hft;

import java.util.ArrayList;
import java.util.List;

import com.tictactec.ta.lib.Core;

public class HistoryOperator {

	public static interface HistoryOperatorListener {
		public void change(String name, Object k, Object v);
	}

	private HistoryOperatorListener listener;
	private Object value;
	private Function function;
	private Type type;
	private int day;
	private int skipDay;

	public enum Function {
		Max, Min, Average, MeanDeviation, RSI, EMA, MFI, MACD, MACDSignal, MACDHist
	}

	public enum Type {
		PrevPrice, OpenPrice, HighPrice, LowPrice, LastPrice, TypicalPrice, Volume,Issued
	}

	static boolean equals(Object oldValue, Object newValue) {
		if (oldValue == null) {
			return newValue == null;
		}
		return oldValue.equals(newValue);
	}

	public HistoryOperator() {
		this.day = 1;
		this.skipDay = 0;
		this.value = null;
		this.function = Function.Max;
		this.type = Type.OpenPrice;
	}

	protected Object calculate() {
		return value;
	}

	public int getDay() {
		return day;
	}

	public int getSkipDay() {
		return skipDay;
	}

	private void firePropertyChange(String name, Object k, Object v) {
		if (listener != null) {
			listener.change(name, k, v);
		}
	}

	public void setDay(int day) {
		if (day < 0)
			return;

		int oldDay = this.day;
		this.day = day;

		if (oldDay != this.day) {
			this.firePropertyChange("attribute", oldDay + "d "
					+ getSkipDayAsString(skipDay) + this.function + " "
					+ this.type, this.day + "d " + getSkipDayAsString(skipDay)
					+ this.function + " " + this.type);
		}
	}

	public void setSkipDay(int skipDay) {
		if (skipDay < 0) {
			return;
		}

		int oldSkipDay = this.skipDay;
		this.skipDay = skipDay;

		if (oldSkipDay != this.skipDay) {
			this.firePropertyChange("attribute", this.day + "d "
					+ getSkipDayAsString(oldSkipDay) + this.function + " "
					+ this.type, this.day + "d " + getSkipDayAsString(skipDay)
					+ this.function + " " + this.type);
		}
	}

	public void setFunction(Function function) {
		Function oldFunction = this.function;
		this.function = function;

		if (equals(oldFunction, this.function) == false) {
			this.firePropertyChange("attribute", this.day + "d "
					+ getSkipDayAsString(skipDay) + oldFunction + " "
					+ this.type, this.day + "d " + getSkipDayAsString(skipDay)
					+ this.function + " " + this.type);
		}

	}

	public Function getFunction() {
		return this.function;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		Type oldType = this.type;
		this.type = type;

		if (equals(oldType, this.type) == false) {
			this.firePropertyChange("attribute", this.day + "d "
					+ getSkipDayAsString(skipDay) + this.function + " "
					+ oldType, this.day + "d " + getSkipDayAsString(skipDay)
					+ this.function + " " + this.type);
		}
	}

	private double average(List<Double> values) {
		final int size = values.size();
		assert (size >= 0);
		Double sum = 0.0;
		for (Double v : values) {
			sum = sum + v;
		}
		return sum / size;
	}

	public int getRequiredHistorySize() {
		if (this.function == Function.EMA) {
			Core core = new Core();
			int lookback = core.emaLookback(day);
			return ((lookback + 1) << 2);
		} else if (function == Function.RSI) {
			Core core = new Core();
			int lookback = core.rsiLookback(day);
			return ((lookback + 1) << 2);
		} else if (this.function == Function.MFI) {
			Core core = new Core();
			int lookback = core.mfiLookback(day);
			return ((lookback + 1) << 2);
		} else if (this.function == Function.MACD) {
			Core core = new Core();
			int lookback = core.macdFixLookback(day);
			return ((lookback + 1) << 2);
		} else if (this.function == Function.MACDSignal) {
			Core core = new Core();
			int lookback = core.macdFixLookback(day);
			return ((lookback + 1) << 2);
		} else if (this.function == Function.MACDHist) {
			Core core = new Core();
			int lookback = core.macdFixLookback(day);
			return ((lookback + 1) << 2);
		} else {
			return day;
		}
	}

	public void calculate(CoinList list) {
		if (day <= 0 || skipDay < 0) {
			Object oldValue = this.value;
			this.value = null;
			if (equals(oldValue, value) == false) {
				this.firePropertyChange("value", oldValue, this.value);
			}
			return;
		}

		List<Coin> stocks = new ArrayList<Coin>();
		List<Double> values = new ArrayList<Double>();

		List<Double> highs = new ArrayList<Double>();
		List<Double> lows = new ArrayList<Double>();
		List<Double> closes = new ArrayList<Double>();

		List<Long> volumes = new ArrayList<Long>();

		final int size = list.size();

		final int start = Math.max(0, size - getRequiredHistorySize());

		for (int i = Math.max(0, start - skipDay), ei = Math.max(0, size
				- skipDay); i < ei; i++) {
			final long timestamp = list.getTimestamp(i);
			final Coin stock = list.getCoinTime(timestamp);
			stocks.add(stock);
		}

		if (this.function == Function.MFI) {
			for (Coin stock : stocks) {
				values.add(AnalysisData.getTypicalPrice(stock));
				highs.add(stock.getHighPrice());
				lows.add(stock.getLowPrice());
				closes.add(stock.getLastPrice());
				volumes.add(stock.getVolume());
			}
		} else {
			switch (type) {
			case PrevPrice:
				for (Coin stock : stocks) {
					values.add(stock.getPrevPrice());
				}
				break;

			case OpenPrice:
				for (Coin stock : stocks) {
					values.add(stock.getOpenPrice());
				}
				break;

			case HighPrice:
				for (Coin stock : stocks) {
					values.add(stock.getHighPrice());
				}
				break;

			case LowPrice:
				for (Coin stock : stocks) {
					values.add(stock.getLowPrice());
				}
				break;

			case LastPrice:
				for (Coin stock : stocks) {
					values.add(stock.getLastPrice());
				}
				break;

			case TypicalPrice:
				for (Coin stock : stocks) {
					values.add(AnalysisData.getTypicalPrice(stock));
				}
				break;

			case Volume:
				for (Coin stock : stocks) {
					values.add(new Double(stock.getVolume()));
				}
				break;
			case Issued:
				values.add(new Double(list.getIssuedCount()));
				break;

			default:
				assert (false);
			}
		}

		final int dataSize = values.size();

		if (dataSize == 0) {
			Object oldValue = this.value;
			this.value = null;
			if (equals(oldValue, value) == false) {
				this.firePropertyChange("value", oldValue, this.value);
			}
			return;
		}

		Double v = function == Function.Min ? Double.MAX_VALUE : 0.0;
		double tmp_v = v;

		switch (function) {
		case Max:
			for (Double _value : values) {
				tmp_v = Math.max(tmp_v, _value);
			}
			v = tmp_v;
			break;

		case Min:
			for (Double _value : values) {
				tmp_v = Math.min(tmp_v, _value);
			}
			v = tmp_v;
			break;

		case Average:
			v = average(values);
			break;

		case MeanDeviation:
			double average = 0;
			for (Double _value : values) {
				average = average + _value;
			}
			average = average / (double) dataSize;
			for (Double _value : values) {
				tmp_v = tmp_v + Math.abs(_value - average);
			}
			tmp_v = tmp_v / (double) dataSize;
			v = tmp_v;
			break;

		case RSI:
			v = AnalysisData.createRSI(values, day);
			break;

		case EMA:
			v = AnalysisData.createEMA(values, day);
			break;

		case MFI:
			v = AnalysisData.createMFI(highs, lows, closes, volumes, day);
			break;

		case MACD:
			v = AnalysisData.createMACDFix(values, day).outMACD;
			break;

		case MACDSignal:
			v = AnalysisData.createMACDFix(values, day).outMACDSignal;
			break;

		case MACDHist:
			v = AnalysisData.createMACDFix(values, day).outMACDHist;
			break;

		default:
			assert (false);
		}

		Object oldValue = this.value;

		this.value = v;

		if (equals(oldValue, value) == false) {
			this.firePropertyChange("value", oldValue, this.value);
		}
	}

	private String getSkipDayAsString(int skipDay) {
		if (skipDay <= 0) {
			return "";
		}
		return "(-" + skipDay + ") ";
	}

}
