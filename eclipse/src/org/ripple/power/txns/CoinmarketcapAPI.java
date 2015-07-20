package org.ripple.power.txns;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.json.JSONArray;
import org.ripple.power.utils.HttpRequest;

public class CoinmarketcapAPI {

	public static GregorianCalendar getCoinmarketcapCalendar(int offsetDay) {
		GregorianCalendar cal = new GregorianCalendar(
				TimeZone.getTimeZone("GMT"));
		if (offsetDay != 0) {
			cal.add(Calendar.DATE, offsetDay);
		}
		cal.set(Calendar.HOUR, 10);
		cal.set(Calendar.SECOND, 9);
		cal.set(Calendar.MINUTE, 4);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}

	public static JSONArray getCoinmarketcapVolume(String coinName, String model) {
		return getCoinmarketcap(String.format(
				"http://coinmarketcap.com/datapoints/%s/volume", coinName),
				model);
	}

	public static JSONArray getCoinmarketcapUSD(String coinName, String model) {
		return getCoinmarketcap(String.format(
				"http://coinmarketcap.com/datapoints/%s/price_usd", coinName),
				model);
	}

	public static JSONArray getCoinmarketcapBTC(String coinName, String model) {
		return getCoinmarketcap(String.format(
				"http://coinmarketcap.com/datapoints/%s/price_btc", coinName),
				model);
	}

	public static JSONArray getCoinmarketcapMarketCap(String coinName,
			String model) {
		return getCoinmarketcap(
				String.format(
						"http://coinmarketcap.com/datapoints/%s/market_cap_by_available_supply",
						coinName), model);
	}

	public static JSONArray getCoinmarketcapVolume(String coinName, int day) {
		return getCoinmarketcap(String.format(
				"http://coinmarketcap.com/datapoints/%s/volume", coinName), day);
	}

	public static JSONArray getCoinmarketcapUSD(String coinName, int day) {
		return getCoinmarketcap(String.format(
				"http://coinmarketcap.com/datapoints/%s/price_usd", coinName),
				day);
	}

	public static JSONArray getCoinmarketcapBTC(String coinName, int day) {
		return getCoinmarketcap(String.format(
				"http://coinmarketcap.com/datapoints/%s/price_btc", coinName),
				day);
	}

	public static JSONArray getCoinmarketcapMarketCap(String coinName, int day) {
		return getCoinmarketcap(
				String.format(
						"http://coinmarketcap.com/datapoints/%s/market_cap_by_available_supply",
						coinName), day);
	}

	public static JSONArray getCoinmarketcap(String url, int day) {
		long startTime = getCoinmarketcapCalendar(-day).getTimeInMillis();
		long endTime = getCoinmarketcapCalendar(0).getTimeInMillis();
		try {
			HttpRequest request = HttpRequest.get(url
					+ String.format("/%s/%s/", startTime, endTime));
			request.acceptGzipEncoding();
			if (request.ok()) {
				request.uncompress(true);
				return new JSONArray(request.body());
			}
		} catch (Exception ex) {
			return null;
		}
		return null;
	}

	public static JSONArray getCoinmarketcap(String url, String model) {
		long startTime = -1;
		long endTime = getCoinmarketcapCalendar(0).getTimeInMillis();
		switch (model) {
		case "1d":
			startTime = getCoinmarketcapCalendar(-1).getTimeInMillis();
			break;
		case "7d":
			startTime = getCoinmarketcapCalendar(-7).getTimeInMillis();
			break;
		case "1m":
			startTime = getCoinmarketcapCalendar(-30).getTimeInMillis();
			break;
		case "3m":
			startTime = getCoinmarketcapCalendar(-(30 * 3)).getTimeInMillis();
			break;
		case "1y":
			startTime = getCoinmarketcapCalendar(-365).getTimeInMillis();
			break;
		default:
			break;
		}
		if (startTime == -1) {
			startTime = endTime;
		}
		try {
			HttpRequest request = HttpRequest.get(url
					+ String.format("/%s/%s/", startTime, endTime));
			request.acceptGzipEncoding();
			if (request.ok()) {
				request.uncompress(true);
				return new JSONArray(request.body());
			}
		} catch (Exception ex) {
			return null;
		}
		return null;
	}

}
