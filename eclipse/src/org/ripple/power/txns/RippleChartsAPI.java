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
import org.ripple.power.txns.data.AccountOffersResponse;
import org.ripple.power.txns.data.CandlesResponse;
import org.ripple.power.txns.data.MarketsRespone;
import org.ripple.power.txns.data.TotalNetworkValueResponse;
import org.ripple.power.txns.data.TransactionStatsResponse;
import org.ripple.power.utils.HttpRequest;

import com.ripple.core.coretypes.RippleDate;

public class RippleChartsAPI {

	public static enum Model {
		ALL, YEAR, MONTH, WEEK, DAY, HOUR, MINUTE, SECOND
	}

	private static String CHARTS_URL = "http://api.ripplecharts.com/api/";

	public static void setDataAPI_URL(String url) {
		CHARTS_URL = url;
	}

	public static String getDataAPI_URL() {
		return CHARTS_URL;
	}

	private final static SimpleDateFormat dateformat = new SimpleDateFormat(
			"yyyy-MM-dd");

	private final static SimpleDateFormat iso8601 = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);

	static {
		iso8601.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	private static ArrayList<RippleItem> jsonToItems(Object o) {
		if (o != null && o instanceof JSONArray) {
			JSONArray arrays = (JSONArray) o;
			ArrayList<RippleItem> list = new ArrayList<RippleItem>(
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
		return offers_exercised24hour(IssuedCurrency.BASE, issued);
	}

	public static ArrayList<RippleItem> offers_exercised24hour_items(
			String currency, String issuer) {
		Object o = offers_exercised24hour(IssuedCurrency.BASE,
				new IssuedCurrency(issuer, currency));
		return jsonToItems(o);
	}

	public static ArrayList<RippleItem> offers_exercised24hour_items(
			IssuedCurrency issued) {
		Object o = offers_exercised24hour(IssuedCurrency.BASE, issued);
		return jsonToItems(o);
	}

	public static Object offers_exercised24hour(IssuedCurrency basecur,
			IssuedCurrency counter) {
		Calendar calone = Calendar.getInstance();
		calone.add(Calendar.DATE, -1);
		String yesterday = dateformat.format(calone.getTime());
		Calendar caltwo = Calendar.getInstance();
		caltwo.setTime(RippleDate.now());
		String day = dateformat.format(caltwo.getTime());
		return offers_exercised(basecur, counter, yesterday, day, true);
	}

	public static Object offers_exercisedYear(IssuedCurrency issued) {
		return offers_exercisedYear(IssuedCurrency.BASE, issued);
	}

	public static ArrayList<RippleItem> offers_exercisedYear_items(
			String currency, String issuer) {
		Object o = offers_exercisedYear(IssuedCurrency.BASE,
				new IssuedCurrency(issuer, currency));
		return jsonToItems(o);
	}

	public static ArrayList<RippleItem> offers_exercisedYear_items(
			IssuedCurrency issued) {
		Object o = offers_exercisedYear(IssuedCurrency.BASE, issued);
		return jsonToItems(o);
	}

	public static Object offers_exercisedYear(IssuedCurrency basecur,
			IssuedCurrency counter) {
		Calendar calone = Calendar.getInstance();
		calone.add(Calendar.DATE, -365);
		String yesterday = dateformat.format(calone.getTime());
		Calendar caltwo = Calendar.getInstance();
		caltwo.setTime(RippleDate.now());
		String day = dateformat.format(caltwo.getTime());
		return offers_exercised(basecur, counter, yesterday, day, false);
	}

	public static Object offers_exercisedMonth(IssuedCurrency issued) {
		return offers_exercisedMonth(IssuedCurrency.BASE, issued);
	}

	public static ArrayList<RippleItem> offers_exercisedMonth_items(
			String currency, String issuer) {
		Object o = offers_exercisedMonth(IssuedCurrency.BASE,
				new IssuedCurrency(issuer, currency));
		return jsonToItems(o);
	}

	public static ArrayList<RippleItem> offers_exercisedMonth_items(
			IssuedCurrency issued) {
		Object o = offers_exercisedMonth(IssuedCurrency.BASE, issued);
		return jsonToItems(o);
	}

	public static Object offers_exercisedMonth(IssuedCurrency basecur,
			IssuedCurrency counter) {
		Calendar calone = Calendar.getInstance();
		calone.add(Calendar.DATE, -30);
		String yesterday = dateformat.format(calone.getTime());
		Calendar caltwo = Calendar.getInstance();
		caltwo.setTime(RippleDate.now());
		String day = dateformat.format(caltwo.getTime());
		return offers_exercised(basecur, counter, yesterday, day, false);
	}

	public static Object offers_exercisedWeek(IssuedCurrency issued) {
		return offers_exercisedWeek(IssuedCurrency.BASE, issued);
	}

	public static ArrayList<RippleItem> offers_exercisedWeek_items(
			String currency, String issuer) {
		Object o = offers_exercisedWeek(IssuedCurrency.BASE,
				new IssuedCurrency(issuer, currency));
		return jsonToItems(o);
	}

	public static ArrayList<RippleItem> offers_exercisedWeek_items(
			IssuedCurrency issued) {
		Object o = offers_exercisedWeek(IssuedCurrency.BASE, issued);
		return jsonToItems(o);
	}

	public static Object offers_exercisedWeek(IssuedCurrency basecur,
			IssuedCurrency counter) {
		Calendar calone = Calendar.getInstance();
		calone.add(Calendar.DATE, -7);
		String yesterday = dateformat.format(calone.getTime());
		Calendar caltwo = Calendar.getInstance();
		caltwo.setTime(RippleDate.now());
		String day = dateformat.format(caltwo.getTime());
		return offers_exercised(basecur, counter, yesterday, day, false);
	}

	public static Object offers_exercised(IssuedCurrency basecur,
			IssuedCurrency counter, String start, String end, boolean hour) {
		JSONObject data = new JSONObject();
		data.put("base", basecur.getBase());
		data.put("counter", counter.getBase());
		data.put("startTime", start);
		data.put("endTime", end);
		if (hour) {
			data.put("timeIncrement", "minute");
			data.put("timeMultiple", 5);
		}
		data.put("format", "json");
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

	public static Object historicalMetrics(String curreny, String issuer) {
		HttpRequest request = HttpRequest
				.post(CHARTS_URL + "historicalMetrics");
		JSONObject data = new JSONObject();
		JSONObject exchange = new JSONObject();
		exchange.put("currency", curreny.toUpperCase());
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

	public static Object getExchangeRates(final String basecur,
			final String currency, final String issuer) {
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
		base.put("currency", basecur.toUpperCase());
		JSONObject trade = new JSONObject();
		trade.put("currency", currency);
		trade.put("issuer", issuer.toUpperCase());
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
		Object o = getExchangeRates(LSystem.nativeCurrency, currency, issuer);
		if (o != null && o instanceof JSONArray) {
			JSONArray arrays = (JSONArray) o;
			ArrayList<RippleItem> list = new ArrayList<RippleItem>(
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

	public static Object getExchange(IssuedCurrency curreny) {
		ArrayList<IssuedCurrency> issueds = new ArrayList<IssuedCurrency>();
		issueds.add(curreny);
		return getExchange(IssuedCurrency.BASE, issueds);
	}

	public static Object getExchange(IssuedCurrency basecur,
			ArrayList<IssuedCurrency> counters) {
		HttpRequest request = HttpRequest.post(CHARTS_URL + "exchange_rates");
		JSONObject obj = new JSONObject();
		JSONArray arrays = new JSONArray();
		if (counters != null) {
			for (IssuedCurrency counter : counters) {
				JSONObject item = new JSONObject();
				item.put("base", basecur.getBase());
				item.put("counter", counter.getBase());
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
		Object result = getExchange(IssuedCurrency.BASE, list);
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

	/**
	 * all ripple account count
	 * 
	 * @return
	 */
	public static Object getAccountsCreatedAll() {
		return getAccountsCreated(0, Model.ALL);
	}

	public static Object getAccountsCreated(int count, Model model) {
		Calendar calone = Calendar.getInstance();
		Calendar rippleDate = Calendar.getInstance();
		rippleDate.setTime(RippleDate.now());
		JSONObject obj = new JSONObject();
		switch (model) {
		case ALL:
			obj.put("startTime", "2013-01-01T00:00:00.000Z");
			obj.put("endTime", dateformat.format(rippleDate.getTime()));
			obj.put("timeIncrement", "all");
			break;
		case DAY:
			calone.add(Calendar.DATE, -count);
			obj.put("startTime", dateformat.format(calone.getTime()));
			obj.put("endTime", dateformat.format(rippleDate.getTime()));
			obj.put("timeIncrement", "day");
			break;
		case WEEK:
			calone.add(Calendar.DATE, -(count * 7));
			obj.put("startTime", dateformat.format(calone.getTime()));
			obj.put("endTime", dateformat.format(rippleDate.getTime()));
			obj.put("timeIncrement", "week");
			break;
		case MONTH:
			calone.add(Calendar.MONTH, -count);
			obj.put("startTime", dateformat.format(calone.getTime()));
			obj.put("endTime", dateformat.format(rippleDate.getTime()));
			obj.put("timeIncrement", "week");
			break;
		case YEAR:
			calone.add(Calendar.YEAR, -count);
			obj.put("startTime", dateformat.format(calone.getTime()));
			obj.put("endTime", dateformat.format(rippleDate.getTime()));
			obj.put("timeIncrement", "day");
			break;
		default:
			break;
		}
		obj.put("descending", "true");
		obj.put("reduce", "true");
		HttpRequest request = HttpRequest.post(CHARTS_URL + "accounts_created");
		String result = request.send(obj);
		if (result != null && result.indexOf("\"") != -1) {
			if (result.startsWith("[")) {
				return new JSONArray(result);
			} else if (result.startsWith("{")) {
				return new JSONObject(result);
			} else {
				return result;
			}
		}
		return result;
	}

	public static CandlesResponse getTradeStatistics(String currency,
			String issuer, long time) {
		return getTradeStatistics(IssuedCurrency.BASE, new IssuedCurrency(
				issuer, currency), time);
	}

	public static CandlesResponse getTradeStatistics(IssuedCurrency basecur,
			IssuedCurrency counter, long time) {
		Date now = new Date();
		String endTime = iso8601.format(now);
		String startTime = endTime;
		if (time > 0) {
			startTime = iso8601.format(new Date(now.getTime() - time));
		} else {
			startTime = iso8601.format(new Date(now.getTime() - LSystem.DAY));
		}
		JSONObject data = new JSONObject();
		data.put("base", basecur.getBase());
		data.put("counter", counter.getBase());
		data.put("startTime", startTime);
		data.put("endTime", endTime);
		data.put("timeIncrement", "minute");
		data.put("timeMultiple", 5);
		data.put("format", "json");
		HttpRequest request = HttpRequest.post(CHARTS_URL + "offers_exercised");
		String result = request.send(data);
		CandlesResponse candles = new CandlesResponse();
		if (result != null && result.indexOf("\"") != -1) {
			if (result.startsWith("[")) {
				candles.from(new JSONArray(result));
			} else if (result.startsWith("{")) {
				candles.from(new JSONObject(result));
			}
		}
		return candles;
	}

	public static AccountOffersResponse account_offers_exercised(
			String account, long time, int limit) {
		Date now = new Date();
		String endTime = iso8601.format(now);
		String startTime = endTime;
		if (time > 0) {
			startTime = iso8601.format(new Date(now.getTime() - time));
		} else {
			startTime = iso8601.format(new Date(now.getTime() - LSystem.DAY));
		}
		JSONObject data = new JSONObject();
		data.put("account", account);
		data.put("startTime", startTime);
		data.put("endTime", endTime);
		data.put("offset", 0);
		data.put("limit", limit);
		data.put("format", "json");
		HttpRequest request = HttpRequest.post(CHARTS_URL
				+ "account_offers_exercised");
		String result = request.send(data);
		AccountOffersResponse accountOffers = new AccountOffersResponse();
		if (result != null && result.indexOf("\"") != -1) {
			if (result.startsWith("[")) {
				accountOffers.from(new JSONArray(result));
			} else if (result.startsWith("{")) {
				accountOffers.from(new JSONObject(result));
			}
		}
		return accountOffers;
	}

	public static MarketsRespone top_markets(IssuedCurrency exchange, long time) {
		Date now = new Date();
		String startTime = iso8601.format(new Date(now.getTime() - time));
		JSONObject data = new JSONObject();
		data.put("startTime", startTime);
		data.put("exchange", exchange.getBase());
		data.put("interval", "week");
		HttpRequest request = HttpRequest.post(CHARTS_URL + "top_markets");
		String result = request.send(data);
		MarketsRespone topMarkets = new MarketsRespone();
		if (result != null && result.indexOf("\"") != -1) {
			if (result.startsWith("[")) {
				topMarkets.from(new JSONArray(result));
			} else if (result.startsWith("{")) {
				topMarkets.from(new JSONObject(result));
			}
		}
		return topMarkets;
	}

	public static TotalNetworkValueResponse total_network_value(
			IssuedCurrency exchange) {
		return total_network_value(exchange, LSystem.DAY);
	}

	public static TotalNetworkValueResponse total_network_value(
			IssuedCurrency exchange, long time) {
		Date now = new Date();
		String timer = iso8601.format(new Date(now.getTime() - time));
		JSONObject data = new JSONObject();
		data.put("time", timer);
		data.put("exchange", exchange.getBase());
		HttpRequest request = HttpRequest.post(CHARTS_URL
				+ "total_network_value");
		String result = request.send(data);
		TotalNetworkValueResponse totalNetwork = new TotalNetworkValueResponse();
		if (result != null && result.indexOf("\"") != -1) {
			if (result.startsWith("[")) {
				totalNetwork.from(new JSONArray(result));
			} else if (result.startsWith("{")) {
				totalNetwork.from(new JSONObject(result));
			}
		}
		return totalNetwork;
	}

	public static TotalNetworkValueResponse total_value_sent(
			IssuedCurrency exchange) {
		return total_value_sent(exchange, Model.MONTH, LSystem.YEAR);
	}

	public static TotalNetworkValueResponse total_value_sent(
			IssuedCurrency exchange, Model model, long time) {
		Date now = new Date();
		String timer = iso8601.format(new Date(now.getTime() - time));
		JSONObject data = new JSONObject();
		data.put("startTime", timer);
		data.put("exchange", exchange.getBase());
		switch (model) {
		case DAY:
			data.put("interval", "day");
			break;
		case WEEK:
			data.put("interval", "week");
			break;
		case MONTH:
			data.put("interval", "month");
			break;
		default:
			data.put("interval", "day");
			break;
		}
		HttpRequest request = HttpRequest.post(CHARTS_URL + "total_value_sent");
		String result = request.send(data);
		TotalNetworkValueResponse totalNetwork = new TotalNetworkValueResponse();
		if (result != null && result.indexOf("\"") != -1) {
			if (result.startsWith("[")) {
				totalNetwork.from(new JSONArray(result));
			} else if (result.startsWith("{")) {
				totalNetwork.from(new JSONObject(result));
			}
		}
		return totalNetwork;
	}

	public static Object transaction_stats24hour(int limit) {
		Calendar calone = Calendar.getInstance();
		calone.add(Calendar.DATE, -1);
		String startTime = iso8601.format(calone.getTime());
		Calendar caltwo = Calendar.getInstance();
		caltwo.setTime(RippleDate.now());
		String endTime = iso8601.format(caltwo.getTime());
		return transaction_stats(startTime, endTime, limit, Model.HOUR);
	}

	public static Object transaction_statsWeek(int limit) {
		Calendar calone = Calendar.getInstance();
		calone.add(Calendar.DATE, -7);
		String startTime = iso8601.format(calone.getTime());
		Calendar caltwo = Calendar.getInstance();
		caltwo.setTime(RippleDate.now());
		String endTime = iso8601.format(caltwo.getTime());
		return transaction_stats(startTime, endTime, limit, Model.WEEK);
	}

	public static Object transaction_statsMonth(int limit) {
		Calendar calone = Calendar.getInstance();
		calone.add(Calendar.MONTH, -1);
		String startTime = iso8601.format(calone.getTime());
		Calendar caltwo = Calendar.getInstance();
		caltwo.setTime(RippleDate.now());
		String endTime = iso8601.format(caltwo.getTime());
		return transaction_stats(startTime, endTime, limit, Model.MONTH);
	}

	public static Object transaction_statsYear(int limit) {
		Calendar calone = Calendar.getInstance();
		calone.add(Calendar.YEAR, -1);
		String startTime = iso8601.format(calone.getTime());
		Calendar caltwo = Calendar.getInstance();
		caltwo.setTime(RippleDate.now());
		String endTime = iso8601.format(caltwo.getTime());
		return transaction_stats(startTime, endTime, limit, Model.YEAR);
	}

	public static TransactionStatsResponse transaction_stats(String startTime,
			String endTime, int limit, Model model) {
		JSONObject data = new JSONObject();
		data.put("startTime", startTime);
		data.put("endTime", endTime);
		if (model != null) {
			data.put("timeIncrement", model.toString().toLowerCase());
		}
		data.put("descending", true);
		data.put("reduce", false);
		data.put("limit", limit);
		data.put("offset", 0);
		data.put("format", "json");
		HttpRequest request = HttpRequest
				.post(CHARTS_URL + "transaction_stats");
		String result = request.send(data);
		TransactionStatsResponse transactionStats = new TransactionStatsResponse();
		if (result != null && result.indexOf("\"") != -1) {
			if (result.startsWith("[")) {
				transactionStats.from(new JSONArray(result));
			} else if (result.startsWith("{")) {
				transactionStats.from(new JSONObject(result));
			}
		}
		return transactionStats;
	}
}
