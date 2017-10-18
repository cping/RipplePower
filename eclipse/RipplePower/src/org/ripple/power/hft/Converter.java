package org.ripple.power.hft;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ripple.power.utils.DateUtils;

public class Converter {

	public static Bar ticksToBar(List<Tick> ticks, Calendar barStart, Calendar barEnd, BAR_SIZE barSize,
			float previousClose, boolean partial) {
		Bar bar = null;
		String symbol = "";
		try {
			int numTrades = ticks.size();
			float volumeSum = 0;
			float priceVolumeSum = 0;
			int tradeNumber = 1;
			float open = 0;
			float close = 0;
			float high = 0;
			float low = 10000000;

			for (Tick tick : ticks) {
				symbol = tick.symbol;
				if (tradeNumber == 1) {
					open = tick.price;
				}
				if (tradeNumber == numTrades) {
					close = tick.price;
				}
				if (tick.price > high) {
					high = tick.price;
				}
				if (tick.price < low) {
					low = tick.price;
				}

				float priceVolume = tick.price * tick.volume;
				volumeSum += tick.volume;
				priceVolumeSum += priceVolume;
				tradeNumber++;
			}

			if (previousClose == 0) {
				previousClose = open;
			}

			float vwap = priceVolumeSum / volumeSum;
			float change = close - previousClose;
			float gap = open - previousClose;

			if (numTrades == 0) {
				open = previousClose;
				close = previousClose;
				high = previousClose;
				low = previousClose;
				vwap = previousClose;
				change = 0;
				gap = 0;
			}

			bar = new Bar(symbol, open, close, high, low, vwap, volumeSum, numTrades, change, gap, barStart, barEnd,
					barSize, partial);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bar;
	}

	public static long difference(Calendar c1, Calendar c2, int unit) {
		differenceCheckUnit(unit);
		Map<Integer, Long> unitEstimates = differenceGetUnitEstimates();
		Calendar first = (Calendar) c1.clone();
		Calendar last = (Calendar) c2.clone();
		long difference = c2.getTimeInMillis() - c1.getTimeInMillis();
		long unitEstimate = unitEstimates.get(unit).longValue();
		long increment = (long) Math.floor((double) difference / (double) unitEstimate);
		increment = Math.max(increment, 1);
		long total = 0;
		while (increment > 0) {
			add(first, unit, increment);
			if (first.after(last)) {
				add(first, unit, increment * -1);
				increment = (long) Math.floor(increment / 2);
			} else {
				total += increment;
			}
		}
		return total;
	}

	private static Map<Integer, Long> differenceGetUnitEstimates() {
		Map<Integer, Long> unitEstimates = new HashMap<Integer, Long>();
		unitEstimates.put(Calendar.YEAR, 1000l * 60 * 60 * 24 * 365);
		unitEstimates.put(Calendar.MONTH, 1000l * 60 * 60 * 24 * 30);
		unitEstimates.put(Calendar.DAY_OF_MONTH, 1000l * 60 * 60 * 24);
		unitEstimates.put(Calendar.HOUR_OF_DAY, 1000l * 60 * 60);
		unitEstimates.put(Calendar.MINUTE, 1000l * 60);
		unitEstimates.put(Calendar.SECOND, 1000l);
		unitEstimates.put(Calendar.MILLISECOND, 1l);
		return unitEstimates;
	}

	private static void differenceCheckUnit(int unit) {
		List<Integer> validUnits = new ArrayList<Integer>();
		validUnits.add(Calendar.YEAR);
		validUnits.add(Calendar.MONTH);
		validUnits.add(Calendar.DAY_OF_MONTH);
		validUnits.add(Calendar.HOUR_OF_DAY);
		validUnits.add(Calendar.MINUTE);
		validUnits.add(Calendar.SECOND);
		validUnits.add(Calendar.MILLISECOND);
		if (!validUnits.contains(unit)) {
			throw new RuntimeException(
					"CalendarUtils.difference one of these units Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND.");
		}
	}

	public static void add(Calendar c, int unit, long increment) {
		while (increment > Integer.MAX_VALUE) {
			c.add(unit, Integer.MAX_VALUE);
			increment -= Integer.MAX_VALUE;
		}
		c.add(unit, (int) increment);
	}

	public static Calendar getBarStart(Calendar c, BAR_SIZE barSize) {
		Calendar periodStart = DateUtils.getUTCCalendar();
		periodStart.setTime(c.getTime());
		periodStart.set(Calendar.SECOND, 0);
		periodStart.set(Calendar.MILLISECOND, 0);
		Calendar periodEnd = DateUtils.getUTCCalendar();
		periodEnd.setTime(periodStart.getTime());

		try {
			int unroundedMinute = 0;
			int unroundedHour = 0;
			int remainder = 0;
			switch (barSize) {
			case BAR_1M:
				periodEnd.add(Calendar.MINUTE, 1);
				break;
			case BAR_2M:
				unroundedMinute = periodStart.get(Calendar.MINUTE);
				remainder = unroundedMinute % 2;
				periodStart.add(Calendar.MINUTE, -remainder);
				periodEnd.setTime(periodStart.getTime());
				periodEnd.add(Calendar.MINUTE, 2);
				break;
			case BAR_5M:
				unroundedMinute = periodStart.get(Calendar.MINUTE);
				remainder = unroundedMinute % 5;
				periodStart.add(Calendar.MINUTE, -remainder);
				periodEnd.setTime(periodStart.getTime());
				periodEnd.add(Calendar.MINUTE, 5);
				break;
			case BAR_10M:
				unroundedMinute = periodStart.get(Calendar.MINUTE);
				remainder = unroundedMinute % 10;
				periodStart.add(Calendar.MINUTE, -remainder);
				periodEnd.setTime(periodStart.getTime());
				periodEnd.add(Calendar.MINUTE, 10);
				break;
			case BAR_15M:
				unroundedMinute = periodStart.get(Calendar.MINUTE);
				remainder = unroundedMinute % 15;
				periodStart.add(Calendar.MINUTE, -remainder);
				periodEnd.setTime(periodStart.getTime());
				periodEnd.add(Calendar.MINUTE, 15);
				break;
			case BAR_30M:
				unroundedMinute = periodStart.get(Calendar.MINUTE);
				remainder = unroundedMinute % 30;
				periodStart.add(Calendar.MINUTE, -remainder);
				periodEnd.setTime(periodStart.getTime());
				periodEnd.add(Calendar.MINUTE, 30);
				break;
			case BAR_1H:
				periodStart.set(Calendar.MINUTE, 0);
				periodEnd.setTime(periodStart.getTime());
				periodEnd.add(Calendar.HOUR_OF_DAY, 1);
				break;
			case BAR_2H:
				periodStart.set(Calendar.MINUTE, 0);
				unroundedHour = periodStart.get(Calendar.HOUR_OF_DAY);
				remainder = unroundedHour % 2;
				periodStart.add(Calendar.HOUR_OF_DAY, -remainder);
				periodEnd.setTime(periodStart.getTime());
				periodEnd.add(Calendar.HOUR_OF_DAY, 2);
				break;
			case BAR_4H:
				periodStart.set(Calendar.MINUTE, 0);
				unroundedHour = periodStart.get(Calendar.HOUR_OF_DAY);
				remainder = unroundedHour % 4;
				periodStart.add(Calendar.HOUR_OF_DAY, -remainder);
				periodEnd.setTime(periodStart.getTime());
				periodEnd.add(Calendar.HOUR_OF_DAY, 4);
				break;
			case BAR_6H:
				periodStart.set(Calendar.MINUTE, 0);
				unroundedHour = periodStart.get(Calendar.HOUR_OF_DAY);
				remainder = unroundedHour % 6;
				periodStart.add(Calendar.HOUR_OF_DAY, -remainder);
				periodEnd.setTime(periodStart.getTime());
				periodEnd.add(Calendar.HOUR_OF_DAY, 6);
				break;
			case BAR_8H:
				periodStart.set(Calendar.MINUTE, 0);
				unroundedHour = periodStart.get(Calendar.HOUR_OF_DAY);
				remainder = unroundedHour % 8;
				periodStart.add(Calendar.HOUR_OF_DAY, -remainder);
				periodEnd.setTime(periodStart.getTime());
				periodEnd.add(Calendar.HOUR_OF_DAY, 8);
				break;
			case BAR_12H:
				periodStart.set(Calendar.MINUTE, 0);
				unroundedHour = periodStart.get(Calendar.HOUR_OF_DAY);
				remainder = unroundedHour % 12;
				periodStart.add(Calendar.HOUR_OF_DAY, -remainder);
				periodEnd.setTime(periodStart.getTime());
				periodEnd.add(Calendar.HOUR_OF_DAY, 12);
				break;
			case BAR_1D:
				periodStart.set(Calendar.MINUTE, 0);
				periodStart.set(Calendar.HOUR_OF_DAY, 0);
				periodEnd.setTime(periodStart.getTime());
				periodEnd.add(Calendar.HOUR_OF_DAY, 24);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return periodStart;
	}

	public static Calendar getBarEnd(Calendar c, BAR_SIZE barSize) {
		Calendar periodStart = DateUtils.getUTCCalendar();
		periodStart.setTime(c.getTime());
		periodStart.set(Calendar.SECOND, 0);
		periodStart.set(Calendar.MILLISECOND, 0);
		Calendar periodEnd = DateUtils.getUTCCalendar();
		periodEnd.setTime(periodStart.getTime());

		try {
			int unroundedMinute = 0;
			int unroundedHour = 0;
			int remainder = 0;
			switch (barSize) {
			case BAR_1M:
				periodEnd.add(Calendar.MINUTE, 1);
				break;
			case BAR_2M:
				unroundedMinute = periodStart.get(Calendar.MINUTE);
				remainder = unroundedMinute % 2;
				periodStart.add(Calendar.MINUTE, -remainder);
				periodEnd.setTime(periodStart.getTime());
				periodEnd.add(Calendar.MINUTE, 2);
				break;
			case BAR_5M:
				unroundedMinute = periodStart.get(Calendar.MINUTE);
				remainder = unroundedMinute % 5;
				periodStart.add(Calendar.MINUTE, -remainder);
				periodEnd.setTime(periodStart.getTime());
				periodEnd.add(Calendar.MINUTE, 5);
				break;
			case BAR_10M:
				unroundedMinute = periodStart.get(Calendar.MINUTE);
				remainder = unroundedMinute % 10;
				periodStart.add(Calendar.MINUTE, -remainder);
				periodEnd.setTime(periodStart.getTime());
				periodEnd.add(Calendar.MINUTE, 10);
				break;
			case BAR_15M:
				unroundedMinute = periodStart.get(Calendar.MINUTE);
				remainder = unroundedMinute % 15;
				periodStart.add(Calendar.MINUTE, -remainder);
				periodEnd.setTime(periodStart.getTime());
				periodEnd.add(Calendar.MINUTE, 15);
				break;
			case BAR_30M:
				unroundedMinute = periodStart.get(Calendar.MINUTE);
				remainder = unroundedMinute % 30;
				periodStart.add(Calendar.MINUTE, -remainder);
				periodEnd.setTime(periodStart.getTime());
				periodEnd.add(Calendar.MINUTE, 30);
				break;
			case BAR_1H:
				periodStart.set(Calendar.MINUTE, 0);
				periodEnd.setTime(periodStart.getTime());
				periodEnd.add(Calendar.HOUR_OF_DAY, 1);
				break;
			case BAR_2H:
				periodStart.set(Calendar.MINUTE, 0);
				unroundedHour = periodStart.get(Calendar.HOUR_OF_DAY);
				remainder = unroundedHour % 2;
				periodStart.add(Calendar.HOUR_OF_DAY, -remainder);
				periodEnd.setTime(periodStart.getTime());
				periodEnd.add(Calendar.HOUR_OF_DAY, 2);
				break;
			case BAR_4H:
				periodStart.set(Calendar.MINUTE, 0);
				unroundedHour = periodStart.get(Calendar.HOUR_OF_DAY);
				remainder = unroundedHour % 4;
				periodStart.add(Calendar.HOUR_OF_DAY, -remainder);
				periodEnd.setTime(periodStart.getTime());
				periodEnd.add(Calendar.HOUR_OF_DAY, 4);
				break;
			case BAR_6H:
				periodStart.set(Calendar.MINUTE, 0);
				unroundedHour = periodStart.get(Calendar.HOUR_OF_DAY);
				remainder = unroundedHour % 6;
				periodStart.add(Calendar.HOUR_OF_DAY, -remainder);
				periodEnd.setTime(periodStart.getTime());
				periodEnd.add(Calendar.HOUR_OF_DAY, 6);
				break;
			case BAR_8H:
				periodStart.set(Calendar.MINUTE, 0);
				unroundedHour = periodStart.get(Calendar.HOUR_OF_DAY);
				remainder = unroundedHour % 8;
				periodStart.add(Calendar.HOUR_OF_DAY, -remainder);
				periodEnd.setTime(periodStart.getTime());
				periodEnd.add(Calendar.HOUR_OF_DAY, 8);
				break;
			case BAR_12H:
				periodStart.set(Calendar.MINUTE, 0);
				unroundedHour = periodStart.get(Calendar.HOUR_OF_DAY);
				remainder = unroundedHour % 12;
				periodStart.add(Calendar.HOUR_OF_DAY, -remainder);
				periodEnd.setTime(periodStart.getTime());
				periodEnd.add(Calendar.HOUR_OF_DAY, 12);
				break;
			case BAR_1D:
				periodStart.set(Calendar.MINUTE, 0);
				periodStart.set(Calendar.HOUR_OF_DAY, 0);
				periodEnd.setTime(periodStart.getTime());
				periodEnd.add(Calendar.HOUR_OF_DAY, 24);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return periodEnd;
	}

	public static boolean areSame(Calendar c1, Calendar c2) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd hh:mm:ss");
			String c1s = sdf.format(c1.getTime());
			String c2s = sdf.format(c2.getTime());
			if (c1s.equals(c2s)) {
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}
	}

}
