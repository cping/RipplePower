package org.ripple.power.txns;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ripple.power.config.LSystem;
import org.ripple.power.txns.OfferPrice.OfferFruit;
import org.ripple.power.utils.HttpRequest;

import com.ripple.core.coretypes.RippleDate;

public class RippleMarket {

	// 用于获取指定类型的历史交易记录（比如24小时交易等）
	private final static String CHARTS_URL = "http://api.ripplecharts.com/api/";

	public ArrayList<OfferFruit> buys = new ArrayList<OfferFruit>(100);

	public ArrayList<OfferFruit> sells = new ArrayList<OfferFruit>(100);

	private final static SimpleDateFormat dateformat = new SimpleDateFormat(
			"yyyy-MM-dd");

	public static Object offers_exercised24hour(IssuedCurrency issued) {
		return offers_exercised24hour(LSystem.nativeCurrency, issued.currency,
				issued.issuer.toString());
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

}
