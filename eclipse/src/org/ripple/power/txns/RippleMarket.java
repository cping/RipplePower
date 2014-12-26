package org.ripple.power.txns;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ripple.power.collection.ArrayMap;
import org.ripple.power.config.LSystem;
import org.ripple.power.utils.HttpRequest;

import com.ripple.core.coretypes.RippleDate;

public class RippleMarket {

	public static class RippleItem {

		public String startTime;
		public double baseVolume;
		public double counterVolume;
		public double count;
		public double open;
		public double high;
		public double low;
		public double close;
		public double vwap;
		public String openTime;
		public String closeTime;
		public boolean partial;
	}

	// 用于获取指定类型的历史交易记录（比如24小时交易等）
	private final static String CHARTS_URL = "http://api.ripplecharts.com/api/";

	private final static SimpleDateFormat dateformat = new SimpleDateFormat(
			"yyyy-MM-dd");

	private static ArrayList<RippleItem> jsonToItems(Object o){
		if (o != null && o instanceof JSONArray) {
			JSONArray arrays = (JSONArray) o;
			ArrayList<RippleMarket.RippleItem> list = new ArrayList<RippleMarket.RippleItem>(
					arrays.length() - 1);
			for (int i = 0; i < arrays.length(); i++) {
				RippleItem item = new RippleItem();
				JSONArray obj = arrays.getJSONArray(i);
				int idx = 0;
				item.startTime = obj.getString(idx++);
				item.baseVolume = obj.getDouble(idx++);
				item.counterVolume = obj.getDouble(idx++);
				item.count = obj.getDouble(idx++);
				item.open = obj.getDouble(idx++);
				item.high = obj.getDouble(idx++);
				item.low = obj.getDouble(idx++);
				item.close = obj.getDouble(idx++);
				item.vwap = obj.getDouble(idx++);
				item.openTime = obj.getString(idx++);
				item.closeTime = obj.getString(idx++);
				item.partial = obj.getBoolean(idx++);
				list.add(item);
			}
			return list;
		}
		return null;
	}

	public static Object offers_exercised24hour(IssuedCurrency issued) {
		return offers_exercised24hour(LSystem.nativeCurrency, issued.currency,
				issued.issuer.toString());
	}

	public static ArrayList<RippleItem> offers_exercised24hour_items(
			IssuedCurrency issued) {
		Object o = offers_exercised24hour(LSystem.nativeCurrency,
				issued.currency, issued.issuer.toString());
		return jsonToItems(o);
	}
	
	public static Object offers_exercised24hour(String basecur, String cur,
			String issuer) {
		Calendar calone = Calendar.getInstance();
		calone.add(Calendar.DATE, -1);
		String yesterday = dateformat.format(calone.getTime());
		Calendar caltwo = Calendar.getInstance();
		caltwo.setTime(RippleDate.now());
		String day = dateformat.format(caltwo.getTime());
		return offers_exercised(basecur, cur, issuer, yesterday, day, true);
	}

	public static Object offers_exercisedYear(IssuedCurrency issued) {
		return offers_exercisedYear(LSystem.nativeCurrency, issued.currency,
				issued.issuer.toString());
	}
	
	public static ArrayList<RippleItem> offers_exercisedYear_items(
			IssuedCurrency issued) {
		Object o = offers_exercisedYear(LSystem.nativeCurrency,
				issued.currency, issued.issuer.toString());
		return jsonToItems(o);
	}
	
	public static Object offers_exercisedYear(String basecur, String cur,
			String issuer) {
		Calendar calone = Calendar.getInstance();
		calone.add(Calendar.DATE, -365);
		String yesterday = dateformat.format(calone.getTime());
		Calendar caltwo = Calendar.getInstance();
		caltwo.setTime(RippleDate.now());
		String day = dateformat.format(caltwo.getTime());
		return offers_exercised(basecur, cur, issuer, yesterday, day, false);
	}

	public static Object offers_exercisedMonth(IssuedCurrency issued) {
		return offers_exercisedMonth(LSystem.nativeCurrency, issued.currency,
				issued.issuer.toString());
	}

	public static ArrayList<RippleItem> offers_exercisedMonth_items(
			IssuedCurrency issued) {
		Object o = offers_exercisedMonth(LSystem.nativeCurrency,
				issued.currency, issued.issuer.toString());
		return jsonToItems(o);
	}
	
	public static Object offers_exercisedMonth(String basecur, String cur,
			String issuer) {
		Calendar calone = Calendar.getInstance();
		calone.add(Calendar.DATE, -30);
		String yesterday = dateformat.format(calone.getTime());
		Calendar caltwo = Calendar.getInstance();
		caltwo.setTime(RippleDate.now());
		String day = dateformat.format(caltwo.getTime());
		return offers_exercised(basecur, cur, issuer, yesterday, day, false);
	}

	public static Object offers_exercisedWeek(IssuedCurrency issued) {
		return offers_exercisedWeek(LSystem.nativeCurrency, issued.currency,
				issued.issuer.toString());
	}
	
	public static ArrayList<RippleItem> offers_exercisedWeek_items(
			IssuedCurrency issued) {
		Object o = offers_exercisedWeek(LSystem.nativeCurrency,
				issued.currency, issued.issuer.toString());
		return jsonToItems(o);
	}
	
	public static Object offers_exercisedWeek(String basecur, String cur,
			String issuer) {
		Calendar calone = Calendar.getInstance();
		calone.add(Calendar.DATE, -7);
		String yesterday = dateformat.format(calone.getTime());
		Calendar caltwo = Calendar.getInstance();
		caltwo.setTime(RippleDate.now());
		String day = dateformat.format(caltwo.getTime());
		return offers_exercised(basecur, cur, issuer, yesterday, day, false);
	}

	public static Object offers_exercised(String basecur, String cur,
			String issuer, String start, String end, boolean hour) {
		JSONObject data = new JSONObject();
		JSONObject base = new JSONObject();
		base.put("currency", basecur);
		JSONObject counter = new JSONObject();
		counter.put("currency", cur);
		counter.put("issuer", issuer);
		data.put("base", base);
		data.put("counter", counter);
		data.put("startTime", start);
		data.put("endTime", end);
		if (hour) {
			data.put("timeIncrement", "minute");
			data.put("timeMultiple", 5);
		}
		HttpRequest request = HttpRequest.post(CHARTS_URL + "offers_exercised");
		String result = request.send(data);
		if (result.startsWith("[")) {
			return new JSONArray(result);
		} else if (result.startsWith("{")) {
			return new JSONObject(result);
		} else {
			return result;
		}
	}

	public static Object total_network_value() {
		HttpRequest request = HttpRequest.post(CHARTS_URL
				+ "total_network_value");
		String result = request.body();
		if (result.startsWith("[")) {
			return new JSONArray(result);
		} else if (result.startsWith("{")) {
			return new JSONObject(result);
		} else {
			return result;
		}
	}

	public static Object top_markets() {
		HttpRequest request = HttpRequest.post(CHARTS_URL + "top_markets");
		String result = request.body();
		if (result.startsWith("[")) {
			return new JSONArray(result);
		} else if (result.startsWith("{")) {
			return new JSONObject(result);
		} else {
			return result;
		}
	}

	public static Object total_value_sent() {
		HttpRequest request = HttpRequest.post(CHARTS_URL + "total_value_sent");
		String result = request.body();
		if (result.startsWith("[")) {
			return new JSONArray(result);
		} else if (result.startsWith("{")) {
			return new JSONObject(result);
		} else {
			return result;
		}
	}

	public static Object historicalMetrics(String cur, String issuer) {
		HttpRequest request = HttpRequest
				.post(CHARTS_URL + "historicalMetrics");
		JSONObject data = new JSONObject();
		JSONObject exchange = new JSONObject();
		exchange.put("currency", cur);
		exchange.put("issuer", issuer);
		Calendar calone = Calendar.getInstance();
		calone.add(Calendar.DATE, -1);
		String yesterday = dateformat.format(calone.getTime());
		Calendar caltwo = Calendar.getInstance();
		caltwo.setTime(RippleDate.now());
		String day = dateformat.format(caltwo.getTime());
		data.put("exchange", exchange);
		data.put("startTime", yesterday);
		data.put("endTime", day);
		data.put("timeIncrement", "day");
		data.put("metric", "topMarkets");
		String result = request.send(data);
		if (result.startsWith("[")) {
			return new JSONArray(result);
		} else if (result.startsWith("{")) {
			return new JSONObject(result);
		} else {
			return result;
		}
	}

	public static Object getExchangeRates(final String currency,
			final String issuer) {
		SimpleDateFormat iso8601 = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
		iso8601.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date now = new Date();
		String startTime = iso8601
				.format(new Date(now.getTime() - LSystem.DAY));
		String endTime = iso8601.format(now);
		JSONObject data = new JSONObject();
		data.put("startTime", startTime);
		data.put("endTime", endTime);
		data.put("timeIncrement", "all");
		data.put("timeMultiple", 1);
		data.put("descending", false);
		JSONObject base = new JSONObject();
		base.put("currency", LSystem.nativeCurrency);
		JSONObject trade = new JSONObject();
		trade.put("currency", currency);
		trade.put("issuer", issuer);
		data.put("base", base);
		data.put("counter", trade);
		HttpRequest request = HttpRequest.post(CHARTS_URL + "offers_exercised");
		String result = request.send(data);
		if (result.startsWith("[")) {
			return new JSONArray(result);
		} else if (result.startsWith("{")) {
			return new JSONObject(result);
		} else {
			return result;
		}
	}

	public static ArrayList<RippleItem> getExchangeRateItems(
			final String currency, final String issuer) {
		Object o = RippleMarket.getExchangeRates(currency, issuer);
		if (o != null && o instanceof JSONArray) {
			JSONArray arrays = (JSONArray) o;
			ArrayList<RippleMarket.RippleItem> list = new ArrayList<RippleMarket.RippleItem>(
					arrays.length() - 1);
			ArrayMap names = null;
			for (int i = 0; i < arrays.length(); i++) {
				if (i == 0) {
					JSONArray obj = arrays.getJSONArray(i);
					final int size = obj.length();
					if (names == null) {
						names = new ArrayMap(size);
					}
					try {
						for (int j = 0; j < size; j++) {
							names.put(obj.getString(j), j);
						}
					} catch (Exception ex) {
						names = null;
					}
				} else {
					RippleItem item = new RippleItem();
					JSONArray obj = arrays.getJSONArray(i);
					if (names == null) {
						int idx = 0;
						item.startTime = obj.getString(idx++);
						item.baseVolume = obj.getDouble(idx++);
						item.counterVolume = obj.getDouble(idx++);
						item.count = obj.getDouble(idx++);
						item.open = obj.getDouble(idx++);
						item.high = obj.getDouble(idx++);
						item.low = obj.getDouble(idx++);
						item.close = obj.getDouble(idx++);
						item.vwap = obj.getDouble(idx++);
						item.openTime = obj.getString(idx++);
						item.closeTime = obj.getString(idx++);
						item.partial = obj.getBoolean(idx++);
					} else {
						for (int j = 0; j < names.size(); j++) {
							ArrayMap.Entry entry = names.getEntry(j);
							switch (entry.getKey().toString()) {
							case "startTime":
								item.startTime = obj.getString((int) entry
										.getValue());
								break;
							case "baseVolume":
								item.baseVolume = obj.getDouble((int) entry
										.getValue());
								break;
							case "counterVolume":
								item.counterVolume = obj.getDouble((int) entry
										.getValue());
								break;
							case "count":
								item.count = obj.getDouble((int) entry
										.getValue());
								break;
							case "open":
								item.open = obj.getDouble((int) entry
										.getValue());
								break;
							case "high":
								item.high = obj.getDouble((int) entry
										.getValue());
								break;
							case "low":
								item.low = obj
										.getDouble((int) entry.getValue());
								break;
							case "close":
								item.close = obj.getDouble((int) entry
										.getValue());
								break;
							case "vwap":
								item.vwap = obj.getDouble((int) entry
										.getValue());
								break;
							case "openTime":
								item.openTime = obj.getString((int) entry
										.getValue());
								break;
							case "closeTime":
								item.openTime = obj.getString((int) entry
										.getValue());
								break;
							case "partial":
								item.partial = obj.getBoolean((int) entry
										.getValue());
								break;
							default:
								break;
							}
						}
					}
					list.add(item);
				}
			}
			return list;
		}
		return null;
	}

	public static Object markettraders() {
		HttpRequest request = HttpRequest.post(CHARTS_URL + "markettraders");
		String result = request.body();
		if (result.startsWith("[")) {
			return new JSONArray(result);
		} else if (result.startsWith("{")) {
			return new JSONObject(result);
		} else {
			return result;
		}
	}

	public static Object accountscreated() {
		HttpRequest request = HttpRequest.post(CHARTS_URL + "accountscreated");
		String result = request.body();
		if (result.startsWith("[")) {
			return new JSONArray(result);
		} else if (result.startsWith("{")) {
			return new JSONObject(result);
		} else {
			return result;
		}
	}

	public static Object getExchange(IssuedCurrency cur) {
		ArrayList<IssuedCurrency> issueds = new ArrayList<IssuedCurrency>();
		issueds.add(cur);
		return getExchange(issueds);
	}

	public static Object getExchange(ArrayList<IssuedCurrency> curs) {
		HttpRequest request = HttpRequest.post(CHARTS_URL + "exchange_rates");
		JSONObject obj = new JSONObject();
		JSONArray arrays = new JSONArray();
		if (curs != null) {
			for (IssuedCurrency currency : curs) {
				JSONObject item = new JSONObject();
				JSONObject base = new JSONObject();
				base.put("currency", currency.currency);
				base.put("issuer", currency.issuer.toString());
				JSONObject counter = new JSONObject();
				counter.put("currency", LSystem.nativeCurrency);
				item.put("base", base);
				item.put("counter", counter);
				arrays.put(item);
			}
		}
		obj.put("pairs", arrays);
		String result = request.send(obj);
		if (result.startsWith("[")) {
			return new JSONArray(result);
		} else if (result.startsWith("{")) {
			return new JSONObject(result);
		} else {
			return result;
		}
	}

	public static double getXRPto(ArrayList<IssuedCurrency> list) {
		double a = -1, b = -1;
		Object result = RippleMarket.getExchange(list);
		if (result != null) {
			if (result instanceof JSONArray) {
				a = ((JSONArray) result).getJSONObject(0).getDouble("rate");
				b = ((JSONArray) result).getJSONObject(1).getDouble("rate");
			} else if (result instanceof JSONObject) {
				a = ((JSONObject) result).getDouble("rate");
			}
		}
		double real = Math.min(a, b);
		return 1d / real;
	}

	public static double getXRPtoUSD() {
		ArrayList<IssuedCurrency> list = new ArrayList<IssuedCurrency>(2);
		list.add(IssuedCurrency.SNAPSWAP_USD);
		list.add(IssuedCurrency.BITSTAMP_USD);
		return getXRPto(list);
	}

	public static double getXRPtoBTC() {
		ArrayList<IssuedCurrency> list = new ArrayList<IssuedCurrency>(2);
		list.add(IssuedCurrency.SNAPSWAP_BTC);
		list.add(IssuedCurrency.BITSTAMP_BTC);
		return getXRPto(list);
	}

	public static double getXRPtoJPY() {
		ArrayList<IssuedCurrency> list = new ArrayList<IssuedCurrency>(2);
		list.add(IssuedCurrency.RTJ_JPY);
		list.add(IssuedCurrency.TOKYOJPY_JPY);
		return getXRPto(list);
	}

	public static double getXRPtoCNY() {
		ArrayList<IssuedCurrency> list = new ArrayList<IssuedCurrency>(2);
		list.add(IssuedCurrency.RIPPLECHINA_CNY);
		list.add(IssuedCurrency.RIPPLECN_CNY);
		return getXRPto(list);
	}
}
