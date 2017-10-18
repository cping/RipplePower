package org.ripple.power.txns;

import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ripple.power.utils.DateUtils;
import org.ripple.power.utils.HttpRequest;

public class CoinmarketcapAPI {

	public static Calendar getCoinmarketcapCalendar(int offsetDay) {
		Calendar cal = DateUtils.getUTCCalendar();
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
		return getCoinmarketcap("volume_usd",
				String.format("https://graphs.coinmarketcap.com/currencies/%s/", coinName), model);
	}

	public static JSONArray getCoinmarketcapUSD(String coinName, String model) {
		return getCoinmarketcap("price_usd", String.format("https://graphs.coinmarketcap.com/currencies/%s/", coinName),
				model);
	}

	public static JSONArray getCoinmarketcapBTC(String coinName, String model) {
		return getCoinmarketcap("price_btc", String.format("https://graphs.coinmarketcap.com/currencies/%s/", coinName),
				model);
	}

	public static JSONArray getCoinmarketcapMarketCap(String coinName, String model) {
		return getCoinmarketcap("market_cap_by_available_supply",
				String.format("https://graphs.coinmarketcap.com/currencies/%s/", coinName), model);
	}

	public static JSONArray getCoinmarketcapVolume(String coinName, int day) {
		return getCoinmarketcap("volume_usd",
				String.format("https://graphs.coinmarketcap.com/currencies/%s/", coinName), day);
	}

	public static JSONArray getCoinmarketcapUSD(String coinName, int day) {
		return getCoinmarketcap("price_usd", String.format("https://graphs.coinmarketcap.com/currencies/%s/", coinName),
				day);
	}

	public static JSONArray getCoinmarketcapBTC(String coinName, int day) {
		return getCoinmarketcap("price_btc", String.format("https://graphs.coinmarketcap.com/currencies/%s/", coinName),
				day);
	}

	public static JSONArray getCoinmarketcapMarketCap(String coinName, int day) {
		return getCoinmarketcap("market_cap_by_available_supply",
				String.format("https://graphs.coinmarketcap.com/currencies/%s/", coinName), day);
	}

	public static JSONArray getCoinmarketcap(String mode, String url, int day) {
		long startTime = getCoinmarketcapCalendar(-day).getTimeInMillis();
		long endTime = getCoinmarketcapCalendar(0).getTimeInMillis();
		try {
			String page = url + String.format("/%s/%s/", startTime, endTime);
			HttpRequest request = HttpRequest.get(page);
			request.acceptGzipEncoding();
			if (request.ok()) {
				request.uncompress(true);
				String result = request.body();
				JSONObject obj = new JSONObject(result);
				return obj.optJSONArray(mode);
			}
		} catch (Exception ex) {
			return null;
		}
		return null;
	}

	public static JSONArray getCoinmarketcap(String type, String url, String model) {
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
			HttpRequest request = HttpRequest.get(url + String.format("/%s/%s/", startTime, endTime));
			request.acceptGzipEncoding();
			if (request.ok()) {
				request.uncompress(true);
				String result = request.body();
				JSONObject obj = new JSONObject(result);
				return obj.optJSONArray(type);
			}
		} catch (Exception ex) {
			return null;
		}
		return null;
	}

}
