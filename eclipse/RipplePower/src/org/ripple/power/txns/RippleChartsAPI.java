package org.ripple.power.txns;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.ripple.power.txns.data.Take;
import org.ripple.power.txns.data.TotalNetworkValueResponse;
import org.ripple.power.txns.data.TransactionStatsResponse;
import org.ripple.power.utils.DateUtils;
import org.ripple.power.utils.HttpRequest;
import org.ripple.power.utils.MathUtils;

import com.ripple.core.coretypes.RippleDate;

public class RippleChartsAPI {

	public static enum Model {
		ALL, YEAR, MONTH, WEEK, DAY, HOUR, MINUTE, SECOND
	}

	private static String CHARTS_URL = "https://data.ripple.com/v2/";

	public static void setDataAPI_URL(String url) {
		CHARTS_URL = url;
	}

	public static String getDataAPI_URL() {
		return CHARTS_URL;
	}

	private final static SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

	private final static SimpleDateFormat iso8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);

	static {
		iso8601.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	public static String getUTCTimeString() {
		StringBuffer UTCTimeBuffer = new StringBuffer();
		// 本地时间：
		Calendar cal = Calendar.getInstance();
		// 时间偏移量：
		int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);
		// 取得夏令时差：
		int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);
		// 从本地时间里扣除这些差量，即可以取得UTC时间：
		cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		cal.add(Calendar.HOUR_OF_DAY, -1);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		UTCTimeBuffer.append(MathUtils.addZeros(year, 4)).append("-").append(MathUtils.addZeros(month, 2)).append("-")
				.append(MathUtils.addZeros(day, 2));
		UTCTimeBuffer.append("T").append(MathUtils.addZeros(hour, 2)).append(":").append(MathUtils.addZeros(minute, 2));
		return UTCTimeBuffer.toString();

	}

	private static ArrayList<RippleItem> jsonToItems(Object o) {
		if (o != null && o instanceof JSONArray) {
			JSONArray arrays = (JSONArray) o;
			ArrayList<RippleItem> list = new ArrayList<RippleItem>(arrays.length() - 1);
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

	public static Object offers_exercised24hour(Take issued) {
		return offers_exercised24hour(RippleDefault.BASE, issued);
	}

	public static ArrayList<RippleItem> offers_exercised24hour_items(String currency, String issuer) {
		Object o = offers_exercised24hour(RippleDefault.BASE, new Take(currency, issuer));
		return jsonToItems(o);
	}

	public static ArrayList<RippleItem> offers_exercised24hour_items(Take issued) {
		Object o = offers_exercised24hour(RippleDefault.BASE, issued);
		return jsonToItems(o);
	}

	public static Object offers_exercised24hour(Take basecur, Take counter) {
		Calendar calone = DateUtils.getUTCCalendar();
		calone.add(Calendar.DATE, -1);
		String yesterday = dateformat.format(calone.getTime());
		Calendar caltwo = DateUtils.getUTCCalendar();
		caltwo.setTime(RippleDate.now());
		String day = dateformat.format(caltwo.getTime());
		return offers_exercised(basecur, counter, yesterday, day, true);
	}

	public static Object offers_exercisedYear(Take issued) {
		return offers_exercisedYear(RippleDefault.BASE, issued);
	}

	public static ArrayList<RippleItem> offers_exercisedYear_items(String currency, String issuer) {
		Object o = offers_exercisedYear(RippleDefault.BASE, new Take(currency, issuer));
		return jsonToItems(o);
	}

	public static ArrayList<RippleItem> offers_exercisedYear_items(Take issued) {
		Object o = offers_exercisedYear(RippleDefault.BASE, issued);
		return jsonToItems(o);
	}

	public static Object offers_exercisedYear(Take basecur, Take counter) {
		Calendar calone = DateUtils.getUTCCalendar();
		calone.add(Calendar.DATE, -365);
		String yesterday = dateformat.format(calone.getTime());
		Calendar caltwo = DateUtils.getUTCCalendar();
		caltwo.setTime(RippleDate.now());
		String day = dateformat.format(caltwo.getTime());
		return offers_exercised(basecur, counter, yesterday, day, false);
	}

	public static Object offers_exercisedMonth(Take issued) {
		return offers_exercisedMonth(RippleDefault.BASE, issued);
	}

	public static ArrayList<RippleItem> offers_exercisedMonth_items(String currency, String issuer) {
		Object o = offers_exercisedMonth(RippleDefault.BASE, new Take(currency, issuer));
		return jsonToItems(o);
	}

	public static ArrayList<RippleItem> offers_exercisedMonth_items(Take issued) {
		Object o = offers_exercisedMonth(RippleDefault.BASE, issued);
		return jsonToItems(o);
	}

	public static Object offers_exercisedMonth(Take basecur, Take counter) {
		Calendar calone = DateUtils.getUTCCalendar();
		calone.add(Calendar.DATE, -30);
		String yesterday = dateformat.format(calone.getTime());
		Calendar caltwo = DateUtils.getUTCCalendar();
		caltwo.setTime(RippleDate.now());
		String day = dateformat.format(caltwo.getTime());
		return offers_exercised(basecur, counter, yesterday, day, false);
	}

	public static Object offers_exercisedWeek(Take issued) {
		return offers_exercisedWeek(RippleDefault.BASE, issued);
	}

	public static ArrayList<RippleItem> offers_exercisedWeek_items(String currency, String issuer) {
		Object o = offers_exercisedWeek(RippleDefault.BASE, new Take(currency, issuer));
		return jsonToItems(o);
	}

	public static ArrayList<RippleItem> offers_exercisedWeek_items(Take issued) {
		Object o = offers_exercisedWeek(RippleDefault.BASE, issued);
		return jsonToItems(o);
	}

	public static Object offers_exercisedWeek(Take basecur, Take counter) {
		Calendar calone = DateUtils.getUTCCalendar();
		calone.add(Calendar.DATE, -7);
		String yesterday = dateformat.format(calone.getTime());
		Calendar caltwo = DateUtils.getUTCCalendar();
		caltwo.setTime(RippleDate.now());
		String day = dateformat.format(caltwo.getTime());
		return offers_exercised(basecur, counter, yesterday, day, false);
	}

	public static Object offers_exercised(Take basecur, Take counter, String start, String end, boolean hour) {
		JSONObject data = new JSONObject();
		data.put("base", basecur.getJSON());
		data.put("counter", counter.getJSON());
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
		HttpRequest request = HttpRequest.post(CHARTS_URL + "historicalMetrics");
		JSONObject data = new JSONObject();
		JSONObject exchange = new JSONObject();
		exchange.put("currency", curreny.toUpperCase());
		exchange.put("issuer", issuer);
		Calendar calone = DateUtils.getUTCCalendar();
		calone.add(Calendar.DATE, -1);
		String yesterday = dateformat.format(calone.getTime());
		Calendar caltwo = DateUtils.getUTCCalendar();
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

	public static Object getExchangeRates(final String basecur, final String currency, final String issuer) {
		Calendar now = DateUtils.getUTCCalendar();
		String startTime = iso8601.format(new Date(now.getTimeInMillis() - LSystem.DAY));
		String endTime = iso8601.format(now.getTime());
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

	public static ArrayList<RippleItem> getExchangeRateItems(final String currency, final String issuer) {
		Object o = getExchangeRates(LSystem.nativeCurrency, currency, issuer);
		if (o != null && o instanceof JSONArray) {
			JSONArray arrays = (JSONArray) o;
			ArrayList<RippleItem> list = new ArrayList<RippleItem>(arrays.length() - 1);
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
								item.startTime = obj.getString((int) entry.getValue());
								break;
							case "baseVolume":
								item.baseVolume = obj.getDouble((int) entry.getValue());
								break;
							case "counterVolume":
								item.counterVolume = obj.getDouble((int) entry.getValue());
								break;
							case "count":
								item.count = obj.getDouble((int) entry.getValue());
								break;
							case "open":
								item.open = obj.getDouble((int) entry.getValue());
								break;
							case "high":
								item.high = obj.getDouble((int) entry.getValue());
								break;
							case "low":
								item.low = obj.getDouble((int) entry.getValue());
								break;
							case "close":
								item.close = obj.getDouble((int) entry.getValue());
								break;
							case "vwap":
								item.vwap = obj.getDouble((int) entry.getValue());
								break;
							case "openTime":
								item.openTime = obj.getString((int) entry.getValue());
								break;
							case "closeTime":
								item.openTime = obj.getString((int) entry.getValue());
								break;
							case "partial":
								item.partial = obj.getBoolean((int) entry.getValue());
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

	public static ArrayList<Double> getExchangeRates(Take curreny) {
		ArrayList<Take> issueds = new ArrayList<Take>();
		issueds.add(curreny);
		return getExchangeRates(RippleDefault.BASE, issueds);
	}

	public static ArrayList<Double> getExchangeRates(Take base, Take counter) {
		ArrayList<Take> issueds = new ArrayList<Take>();
		issueds.add(counter);
		return getExchangeRates(base, issueds);
	}

	public static ArrayList<Double> getExchangeRates(Take basecur, ArrayList<Take> counters) {
		ArrayList<Double> lists = new ArrayList<Double>();
		if (counters != null) {
			for (Take counter : counters) {
				String query = CHARTS_URL + "exchanges/" + basecur + "/" + counter + "?descending=true&&limit=10&start="
						+ getUTCTimeString();
				HttpRequest request = HttpRequest.get(query);
				try {
					if (request.ok()) {
						String result = request.body();

						if (result != null && result.length() > 0) {
							JSONObject json = new JSONObject(result);
							JSONArray array = json.optJSONArray("exchanges");
							if (array != null) {

								for (int i = 0; i < array.length(); i++) {
									JSONObject obj = array.getJSONObject(i);

									if (obj != null) {
										lists.add(obj.getDouble("rate"));
									}
								}
							}
						}
					}

				} catch (Exception e) {
				}
			}
		}

		return lists;
	}

	public static double getXRPto(ArrayList<Take> list) {
		double a = -1, b = -1;
		ArrayList<Double> result = getExchangeRates(RippleDefault.BASE, list);
		Double[] arrays = (Double[]) result.toArray(new Double[0]);
		Arrays.sort(arrays);
		if (arrays.length > 0 && arrays.length == 1) {
			a = arrays[0];
		} else {
			a = arrays[1];
			b = arrays[arrays.length - 1];
		}
		double real = Math.min(a, b);
		return real;
	}

	public static double getXRPtoUSD() {
		return getXRPto(RippleDefault.findCurrency("USD"));
	}

	public static double getXRPtoBTC() {
		return getXRPto(RippleDefault.findCurrency("BTC"));
	}

	public static double getXRPtoJPY() {
		return getXRPto(RippleDefault.findCurrency("JPY"));
	}

	public static double getXRPtoCNY() {
		return getXRPto(RippleDefault.findCurrency("CNY"));
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
		Calendar calone = DateUtils.getUTCCalendar();
		Calendar rippleDate = DateUtils.getUTCCalendar();
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

	public static CandlesResponse getTradeStatistics(String currency, String issuer, long time) {
		return getTradeStatistics(RippleDefault.BASE, new Take(currency, issuer), time);
	}

	public static CandlesResponse getTradeStatistics(Take basecur, Take counter, long time) {
		return getTradeStatistics(basecur, counter, time, 15);
	}

	public static CandlesResponse getTradeStatistics(Take basecur, Take counter, long time, int limit) {
		Calendar now = DateUtils.getUTCCalendar();
		String endTime = iso8601.format(now.getTime());
		String startTime = endTime;
		if (time > 0) {
			startTime = iso8601.format(new Date(now.getTimeInMillis() - time));
		} else {
			startTime = iso8601.format(new Date(now.getTimeInMillis() - LSystem.DAY));
		}
		JSONObject data = new JSONObject();
		data.put("base", basecur.getJSON());
		data.put("counter", counter.getJSON());
		data.put("startTime", startTime);
		data.put("endTime", endTime);
		data.put("limit", limit);
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

	public static AccountOffersResponse account_offers_exercised(String account, long time, int limit) {
		Calendar now = DateUtils.getUTCCalendar();
		String endTime = iso8601.format(now.getTime());
		String startTime = endTime;
		if (time > 0) {
			startTime = iso8601.format(new Date(now.getTimeInMillis() - time));
		} else {
			startTime = iso8601.format(new Date(now.getTimeInMillis() - LSystem.DAY));
		}
		JSONObject data = new JSONObject();
		data.put("account", account);
		data.put("startTime", startTime);
		data.put("endTime", endTime);
		data.put("offset", 0);
		data.put("limit", limit);
		data.put("format", "json");
		HttpRequest request = HttpRequest.post(CHARTS_URL + "account_offers_exercised");
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

	public static MarketsRespone top_markets(Take exchange, long time) {
		Calendar now = DateUtils.getUTCCalendar();
		String startTime = iso8601.format(new Date(now.getTimeInMillis() - time));
		JSONObject data = new JSONObject();
		data.put("startTime", startTime);
		data.put("exchange", exchange.getJSON());
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

	public static TotalNetworkValueResponse total_network_value(Take exchange) {
		return total_network_value(exchange, LSystem.DAY);
	}

	public static TotalNetworkValueResponse total_network_value(Take exchange, long time) {
		Calendar now = DateUtils.getUTCCalendar();
		String timer = iso8601.format(new Date(now.getTimeInMillis() - time));
		JSONObject data = new JSONObject();
		data.put("time", timer);
		data.put("exchange", exchange.getJSON());
		HttpRequest request = HttpRequest.post(CHARTS_URL + "total_network_value");
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

	public static TotalNetworkValueResponse total_value_sent(Take exchange) {
		return total_value_sent(exchange, Model.MONTH, LSystem.YEAR);
	}

	public static TotalNetworkValueResponse total_value_sent(Take exchange, Model model, long time) {
		Calendar now = DateUtils.getUTCCalendar();
		String timer = iso8601.format(new Date(now.getTimeInMillis() - time));
		JSONObject data = new JSONObject();
		data.put("startTime", timer);
		data.put("exchange", exchange.getJSON());
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
		Calendar calone = DateUtils.getUTCCalendar();
		calone.add(Calendar.DATE, -1);
		String startTime = iso8601.format(calone.getTime());
		Calendar caltwo = DateUtils.getUTCCalendar();
		caltwo.setTime(RippleDate.now());
		String endTime = iso8601.format(caltwo.getTime());
		return transaction_stats(startTime, endTime, limit, Model.HOUR);
	}

	public static Object transaction_statsWeek(int limit) {
		Calendar calone = DateUtils.getUTCCalendar();
		calone.add(Calendar.DATE, -7);
		String startTime = iso8601.format(calone.getTime());
		Calendar caltwo = DateUtils.getUTCCalendar();
		caltwo.setTime(RippleDate.now());
		String endTime = iso8601.format(caltwo.getTime());
		return transaction_stats(startTime, endTime, limit, Model.WEEK);
	}

	public static Object transaction_statsMonth(int limit) {
		Calendar calone = DateUtils.getUTCCalendar();
		calone.add(Calendar.MONTH, -1);
		String startTime = iso8601.format(calone.getTime());
		Calendar caltwo = DateUtils.getUTCCalendar();
		caltwo.setTime(RippleDate.now());
		String endTime = iso8601.format(caltwo.getTime());
		return transaction_stats(startTime, endTime, limit, Model.MONTH);
	}

	public static Object transaction_statsYear(int limit) {
		Calendar calone = DateUtils.getUTCCalendar();
		calone.add(Calendar.YEAR, -1);
		String startTime = iso8601.format(calone.getTime());
		Calendar caltwo = DateUtils.getUTCCalendar();
		caltwo.setTime(RippleDate.now());
		String endTime = iso8601.format(caltwo.getTime());
		return transaction_stats(startTime, endTime, limit, Model.YEAR);
	}

	public static TransactionStatsResponse transaction_stats(String startTime, String endTime, int limit, Model model) {
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
		HttpRequest request = HttpRequest.post(CHARTS_URL + "transaction_stats");
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
