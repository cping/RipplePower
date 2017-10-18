package org.ripple.power.txns;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.ripple.power.collection.ArrayMap;
import org.ripple.power.config.LSystem;
import org.ripple.power.config.ProxySettings;
import org.ripple.power.utils.HttpRequest;
import org.ripple.power.utils.HttpRequest.HttpRequestException;
import org.ripple.power.utils.HttpsUtils;
import org.ripple.power.utils.StringUtils;
import org.ripple.power.utils.HttpsUtils.ResponseResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ripple.core.coretypes.RippleDate;

public class OtherData {

	final static String ELEMENT_SEPARATOR = "\001";

	final static String ROW_SEPARATOR = "\002";

	final static String REGEX_TABLE = "<table.*?>\\s*?((<tr.*?>.*?</tr>)+?)\\s*?</table>";

	final static String REGEX_ROW = "<tr.*?>\\s*?(.*?)\\s*?</tr>";

	final static String REGEX_ELE = "(?:<th.*?>|<td.*?>)(?:\\s*<.*?>)*(?:&nbsp;)?(.*?)(?:&nbsp;)?(?:\\s*<.*?>)*?\\s*(?:</th>|</td>)";

	public static class LegalTenderCurrency {
		public String code;
		public String name;
		public int unit;
		public String currencyCode;
		public String country;
		public double rate;
		public double change;

		public String toHTMLString() {
			StringBuffer sbr = new StringBuffer();

			sbr.append("name:");
			sbr.append(name);
			sbr.append("<br>");

			sbr.append("unit:");
			sbr.append(unit);
			sbr.append("<br>");

			sbr.append("country:");
			sbr.append(country);
			sbr.append("<br>");

			sbr.append("rate:");
			sbr.append(rate);
			sbr.append("<br>");

			sbr.append("change");
			sbr.append(change);

			return sbr.toString();
		}

		public String toString() {
			StringBuffer sbr = new StringBuffer();

			sbr.append("name:");
			sbr.append(name);
			sbr.append(" ");

			sbr.append("unit:");
			sbr.append(unit);
			sbr.append(" ");

			sbr.append("country:");
			sbr.append(country);
			sbr.append(" ");

			sbr.append("rate:");
			sbr.append(rate);
			sbr.append(" ");

			sbr.append("change");
			sbr.append(change);

			return sbr.toString();
		}
	}

	public static class CoinmarketcapData {

		public String currency;

		public String id;

		public String name;

		public String symbol;

		public String rank;

		public String price_usd;

		public String price_btc;

		public String volume_usd_24h;

		public String market_cap_usd;

		public String available_supply;

		public String total_supply;

		public String percent_change_1h;

		public String percent_change_24h;

		public String percent_change_7d;

		public long last_updated;

		public static CoinmarketcapData from(JSONObject o) {

			if (o != null) {
				CoinmarketcapData data = new CoinmarketcapData();
				data.id = o.optString("id");
				data.name = o.optString("name");
				data.symbol = o.optString("symbol");
				data.rank = o.optString("rank");
				data.volume_usd_24h = o.optString("24h_volume_usd", o.optString("volume_usd_24h"));
				data.market_cap_usd = o.optString("market_cap_usd");
				data.available_supply = o.optString("available_supply");
				data.total_supply = o.optString("total_supply");
				data.percent_change_1h = o.optString("percent_change_1h");
				data.percent_change_24h = o.optString("percent_change_24h");
				data.percent_change_7d = o.optString("percent_change_7d");
				data.last_updated = o.optLong("last_updated");

				data.price_btc = o.optString("price_btc");
				if (data.price_btc.toLowerCase().indexOf("e") != -1) {
					data.price_btc = LSystem.getNumber(new BigDecimal(data.price_btc));
				}
				data.price_usd = o.optString("price_usd");
				if (data.price_usd.toLowerCase().indexOf("e") != -1) {
					data.price_usd = LSystem.getNumber(new BigDecimal(data.price_usd));
				}
				if (data.currency == null) {
					data.currency = "USD";
				}
				return data;
			}
			return null;
		}

		public String toHTMLString() {
			return currency + "/" + name + "<br>" + " Week:" + percent_change_7d + "%<br>" + "1hour:"
					+ percent_change_1h + "%<br>" + "24hour:" + percent_change_24h + "%<br>" + price_usd + "/"
					+ currency + "==1/" + name + "<br>" + "totalSupply:" + total_supply + "<br>"
					+ RippleDate.now().getTimeString();
		}

		public String toString() {
			return currency + "/" + name + " " + " Week:" + percent_change_7d + "% " + " 1hour:" + percent_change_1h
					+ "% " + " 24hour:" + percent_change_24h + "% " + price_usd + "/" + currency + "==1/" + name + " "
					+ "totalSupply:" + total_supply + " " + RippleDate.now().getTimeString();
		}

	}

	protected static ArrayList<Store> _storage = new ArrayList<Store>();

	protected static class Store {
		protected double price;
		protected String name;
		protected long date = 0;

		public Store(double p, String str) {
			this.price = p;
			this.name = str;
			this.date = System.currentTimeMillis();
		}

		public boolean equals(Store other) {
			return price == other.price && name.equals(other.name);
		}
	}

	private final static ArrayList<String> _coinmarketcap_limits = new ArrayList<String>(10);
	static {
		_coinmarketcap_limits.add("usd");
		_coinmarketcap_limits.add("eur");
		_coinmarketcap_limits.add("cny");
		_coinmarketcap_limits.add("cad");
		_coinmarketcap_limits.add("rub");
		_coinmarketcap_limits.add("btc");
	}

	private final static HashMap<String, String> _coinmarketcap_names = new HashMap<String, String>(10);
	static {
		_coinmarketcap_names.put("xrp", "ripple");
		_coinmarketcap_names.put("btc", "bitcoin");
		_coinmarketcap_names.put("ltc", "litecoin");
		_coinmarketcap_names.put("doge", "dog");
		_coinmarketcap_names.put("doge", "dogecoin");
		_coinmarketcap_names.put("nxt", "nxtcoin");
		_coinmarketcap_names.put("btsx", "bts");
	}

	public static double reset(String name) {
		for (Store s : _storage) {
			if (s.name.equals(name) && (System.currentTimeMillis() - s.date) <= LSystem.MINUTE) {
				return s.price;
			} else if (s.name.equals(name)) {
				_storage.remove(s);
				return -1;
			}
		}
		return -1;
	}

	public static void addStorage(Store s) {
		_storage.add(s);
		if (_storage.size() > 20) {
			_storage.remove(0);
		}
	}

	public static double getLegaltenderCurrencyToUSD(String name) {
		String result;
		try {
			result = converterMoney("USD", name);
		} catch (Exception e) {
			return -1;
		}
		return StringUtils.isEmpty(result) ? -1 : Double.parseDouble(result);
	}

	public static String getCoinmarketcapCoinToUSD(String name) {
		if (name == null) {
			return null;
		}
		try {
			name = name.toLowerCase();
			if ("usd".equals(name)) {
				return "1";
			}
			if (_coinmarketcap_names.containsKey(name)) {
				name = _coinmarketcap_names.get(name);
			}
			double ret = reset(name + "coin");
			if (ret != -1) {
				return String.valueOf(ret);
			}

			HttpRequest request = HttpRequest.get("https://api.coinmarketcap.com/v1/ticker/" + name + "/");

			if (request.ok()) {
				JSONArray a = new JSONArray(request.body());
				if (a.length() > 0) {
					JSONObject res = a.getJSONObject(0);
					if (res.has("price_usd")) {
						double result = res.getDouble("price_usd");
						double realprice = -1;
						// Prevent coinmarketcap price fixing
						if (LSystem.nativeCurrency.equalsIgnoreCase(name) || "ripple".equalsIgnoreCase(name)) {
							realprice = RippleChartsAPI.getXRPtoUSD();
						}
						if (realprice <= 0) {
							realprice = result;
						}
						result = Math.max(result, realprice);
						addStorage(new Store(result, name + "coin"));
						return LSystem.getNumber(new BigDecimal(result));
					}
				}
			}

		} catch (Exception e) {
		}
		return null;
	}

	public static CoinmarketcapData getCoinmarketcapTo(String cur, String name)
			throws HttpRequestException, JSONException, IOException {
		if (cur == null || name == null) {
			return null;
		}

		cur = cur.toLowerCase();
		name = name.toLowerCase();
		if (!_coinmarketcap_limits.contains(cur)) {
			cur = "usd";
		}
		if (_coinmarketcap_names.containsKey(name)) {
			name = _coinmarketcap_names.get(name);
		}
		CoinmarketcapData data = getTickerResult(
				String.format("https://api.coinmarketcap.com/v1/ticker/%s/?convert=%s", name, cur.toUpperCase()));
		data.currency = cur.toUpperCase();
		return data;
	}

	private final static CoinmarketcapData getTickerResult(String query) {
		HttpRequest request;
		try {
			request = HttpRequest.get(String.format(query));
			if (request.ok()) {
				JSONArray obj = new JSONArray(request.body());
				CoinmarketcapData data = CoinmarketcapData.from(obj.getJSONObject(0));
				return data;
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	public static ArrayList<CoinmarketcapData> getCoinmarketcapAllTo(int limit) throws Exception {
		if (limit <= 0) {
			limit = 1;
		}
		// https://files.coinmarketcap.com/generated/search/quick_search.json
		HttpRequest request = HttpRequest.get("https://api.coinmarketcap.com/v1/ticker/?limit=" + limit);
		request.acceptGzipEncoding();
		if (request.ok()) {
			request.uncompress(true);
			JSONArray arrays = new JSONArray(request.body());
			final int size = arrays.length();
			ArrayList<CoinmarketcapData> list = new ArrayList<CoinmarketcapData>(size);
			for (int i = 0; i < arrays.length(); i++) {
				JSONObject o = arrays.getJSONObject(i);
				CoinmarketcapData data = CoinmarketcapData.from(o);
				data.currency = "USD";
				list.add(data);

			}
			return list;
		}
		return null;
	}

	private static String FromNodeValue(Element el, String name) {
		return el.getElementsByTagName(name).item(0).getFirstChild().getNodeValue();
	}

	public static LegalTenderCurrency[] getBoiAllLegalTenderRates() throws Exception {
		DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc;
		HttpRequest request = HttpRequest.get("http://www.boi.org.il/currency.xml");
		if (request.ok()) {
			doc = dBuilder.parse(request.stream());
			NodeList currencies = doc.getElementsByTagName("CURRENCY");
			LegalTenderCurrency result[] = new LegalTenderCurrency[currencies.getLength()];
			for (int i = 0; i < currencies.getLength(); i++) {
				Element el = (Element) currencies.item(i);
				result[i] = new LegalTenderCurrency();
				result[i].name = FromNodeValue(el, "NAME");
				result[i].country = FromNodeValue(el, "COUNTRY");
				result[i].currencyCode = FromNodeValue(el, "CURRENCYCODE");
				result[i].unit = Integer.parseInt(FromNodeValue(el, ("UNIT")));
				result[i].rate = Double.parseDouble(FromNodeValue(el, ("RATE")));
				result[i].change = Double.parseDouble(FromNodeValue(el, ("CHANGE")));
			}
			return result;
		}
		return null;
	}

	private final static DecimalFormat NUMBER_FORMAT = new DecimalFormat("0.00000");

	public static ArrayList<String> getAllLegalTenderRateHTML() throws Exception {
		HttpRequest request = HttpRequest.get("http://www.usd-cny.com/");
		request.acceptGzipEncoding();
		request.uncompress(true);

		if (!request.ok()) {
			return null;
		}

		ArrayList<String> table = new ArrayList<String>();
		org.jsoup.nodes.Document doc = Jsoup.parse(request.body("gb2312"));
		org.jsoup.select.Elements trs = doc.select("table").select("tr");
		for (int i = 0; i < trs.size(); i++) {
			org.jsoup.select.Elements tds = trs.get(i).select("td");
			int size = tds.size();
			if (size >= 7) {
				String cur = null;
				for (int j = 0; j < size; j++) {
					String txt = tds.get(j).text().trim();
					if (txt.indexOf(' ') != -1) {
						txt = StringUtils.split(txt, " ")[1];
					}
					txt = StringUtils.trim(txt);
					if (!StringUtils.containChinaLanguage(txt)) {
						if (j == 0) {
							cur = txt;
						}
						if (j == 6) {
							table.add("1/" + cur + "<br>Rate<br>" + NUMBER_FORMAT.format(Double.valueOf(txt) / 100.0d)
									+ "/CNY");
							table.add("1/CNY" + "<br>Rate<br>" + NUMBER_FORMAT.format(100.0d / Double.valueOf(txt))
									+ "/" + cur);
						}
					}
				}
			}
		}
		return table;
	}

	private static String getCharacterDataFromElement(Element e) {
		try {
			Node child = e.getFirstChild();
			if (child instanceof org.w3c.dom.CharacterData) {
				org.w3c.dom.CharacterData cd = (org.w3c.dom.CharacterData) child;
				return cd.getData();
			}
		} catch (Exception ex) {
		}
		return "";
	}

	protected float getFloat(String value) {
		if (value != null && !value.equals(""))
			return Float.parseFloat(value);
		else
			return 0;
	}

	protected static String getElementValue(Element parent, String label) {
		return getCharacterDataFromElement((Element) parent.getElementsByTagName(label).item(0));
	}

	/**
	 * 转换两种货币
	 * 
	 * @param src
	 * @param cur
	 * @return
	 * @throws Exception
	 */
	public static String converterMoney(String src, String cur) throws Exception {
		if (src == null || cur == null) {
			return null;
		}
		String query = (cur + "/" + src).toUpperCase();
		double ret = reset(query);
		if (ret != -1) {
			return String.valueOf(ret);
		}
		// 欧盟的免费货币价格转换平台，以前的那个网站api被取消了……
		HttpRequest request = HttpRequest.get(
				String.format("http://api.fixer.io/latest?base=%s&symbols=%s", src.toUpperCase(), cur.toUpperCase()));
		request.acceptGzipEncoding();
		request.uncompress(true);
		if (!request.ok()) {
			return null;
		}
		JSONObject obj = new JSONObject(request.body());
		if (obj.has("rates")) {
			String result = obj.optJSONObject("rates").optString(cur.toUpperCase());
			addStorage(new Store(Double.parseDouble(result), query));
			return result;
		} else {
			// 免费api经常各种悲剧，多加个保险
			ResponseResult response = HttpsUtils
					.getSSL(String.format("http://free.currencyconverterapi.com/api/v3/convert?q=%s_%s&compact=y",
							src.toUpperCase(), cur.toUpperCase()));
			if (!response.ok()) {
				return null;
			}
			String tag = src.toUpperCase() + "_" + cur.toUpperCase();
			obj = new JSONObject(response.getResult());
			if (obj.has(tag)) {
				String result = obj.optJSONObject(tag).optString("val");
				addStorage(new Store(Double.parseDouble(result), query));
				return result;
			}
		}
		return null;

	}

	private static HashMap<String, String> _coin_names = new HashMap<String, String>(10);
	static {
		_coin_names.put("xrp", "ripple");
		_coin_names.put("btc", "bitcoin");
		_coin_names.put("dog", "dogecoin");
		_coin_names.put("doge", "dogecoin");
		_coin_names.put("ppc", "peercoin");
		_coin_names.put("ltc", "litecoin");
		_coin_names.put("btsx", "bitshares-x");
		_coin_names.put("bitsharesx", "bitshares-x");
	}

	public static ArrayMap getCapitalization(int day, String name) throws Exception {
		return getCapitalization(day, name, -1);
	}

	// coinmarketcap data not update……
	public static ArrayMap getCapitalization(int day, String name, int trend_limit) throws Exception {
		if (name == null) {
			return null;
		}
		name = name.trim().toLowerCase();
		if (_coin_names.containsKey(name)) {
			name = _coin_names.get(name);
		}

		DateFormat YYYY_MM_DD_HHMM = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		ArrayMap list = new ArrayMap(100);
		JSONArray arrays = CoinmarketcapAPI.getCoinmarketcapMarketCap(name, day);

		if (arrays != null) {
			if (trend_limit > 0) {
				for (int i = arrays.length() - trend_limit; i < arrays.length(); i++) {
					JSONArray result = arrays.getJSONArray(i);
					list.put(result.getLong(0), result.getDouble(1));
				}
			} else {
				for (int i = 0; i < arrays.length(); i++) {
					JSONArray result = arrays.getJSONArray(i);
					String key = YYYY_MM_DD_HHMM.format(new Date(result.getLong(0)));
					String value = String.valueOf(result.getDouble(1));
					list.put(key, value);
				}
			}
		}

		return list;
	}

}
