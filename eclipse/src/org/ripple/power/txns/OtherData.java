package org.ripple.power.txns;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ripple.power.config.LSystem;
import org.ripple.power.utils.HttpRequest;
import org.ripple.power.utils.HttpRequest.HttpRequestException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.ripple.core.coretypes.RippleDate;

public class OtherData {

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
		int position = 0;
		String marketCap;
		String change7d;
		String currency;
		String id;
		String change1h;
		long timestamp;
		String volume24;
		String price;
		String name;
		String change7h;
		String totalSupply;
		String change24;

		public static CoinmarketcapData from(JSONObject o) {
			if (o != null) {
				CoinmarketcapData data = new CoinmarketcapData();
				data.position = o.getInt("position");
				data.marketCap = o.getString("marketCap");
				data.change7d = o.getString("change7d");
				data.volume24 = o.getString("volume24");
				data.currency = o.getString("currency");
				data.id = o.getString("id");
				data.change1h = o.getString("change1h");
				data.timestamp = o.getLong("timestamp");
				data.price = o.getString("price");
				if (data.price.toLowerCase().indexOf("e") != -1) {
					data.price = LSystem.getNumber(new BigDecimal(data.price));
				}
				data.name = o.getString("name");
				data.change7h = o.getString("change7h");
				data.totalSupply = o.getString("totalSupply");
				data.change24 = o.getString("change24");
				return data;
			}
			return null;
		}

		public String toHTMLString() {
			return name + "/" + currency.toUpperCase() + "<br>" + " Week:"
					+ change7d + "%<br>" + "7hour:" + change7h + "%<br>"
					+ "1hour:" + change1h + "%<br>" + price + "/"
					+ currency.toUpperCase() + "==1/" + name + "<br>"
					+ "totalSupply:" + totalSupply + "<br>"
					+ RippleDate.now().getTimeString();
		}

		public String toString() {
			return name + "/" + currency.toUpperCase() + " " + " Week:"
					+ change7d + "% " + " 7hour:" + change7h + "% " + " 1hour:"
					+ change1h + "% " + price + "/" + currency.toUpperCase()
					+ "==1/" + name + " " + "totalSupply:" + totalSupply + " "
					+ RippleDate.now().getTimeString();
		}

	}

	protected static ArrayList<Store> _storage = new ArrayList<Store>();

	protected static class Store {
		protected double price;
		protected String name;
		protected Date date;

		public Store(double p, String str) {
			this.price = p;
			this.name = str;
			this.date = new Date();
		}

		public boolean equals(Store other) {
			return price == other.price && name.equals(other.name);
		}
	}

	private final static ArrayList<String> _coinmarketcap_limits = new ArrayList<String>(
			10);
	static {
		_coinmarketcap_limits.add("usd");
		_coinmarketcap_limits.add("eur");
		_coinmarketcap_limits.add("cny");
		_coinmarketcap_limits.add("cad");
		_coinmarketcap_limits.add("rub");
		_coinmarketcap_limits.add("btc");
	}

	private final static HashMap<String, String> _coinmarketcap_names = new HashMap<String, String>(
			10);
	static {
		_coinmarketcap_names.put("ripple", "xrp");
		_coinmarketcap_names.put("bitcoin", "btc");
		_coinmarketcap_names.put("litecoin", "ltc");
		_coinmarketcap_names.put("dog", "doge");
		_coinmarketcap_names.put("dogecoin", "doge");
		_coinmarketcap_names.put("nxtcoin", "nxt");
	}

	public static double reset(String name) {
		for (Store s : _storage) {
			if (s.name.equals(name)
					&& (s.date.getTime() - (new Date()).getTime()) >= -60 * 1000) {
				_storage.remove(s);
				_storage.add(s);
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
		try {
			double ret = reset(name + "currency");
			if (ret != -1) {
				return ret;
			}
			HttpRequest request = HttpRequest
					.get("http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.xchange%20where%20pair%20in%20('"
							+ name
							+ "')&format=json&diagnostics=false&env=http://datatables.org/alltables.env");
			request.acceptGzipEncoding();
			if (request.ok()) {
				String result = request.body();
				JSONObject obj = new JSONObject(result);
				obj = obj.getJSONObject("query");
				obj = obj.getJSONObject("results");
				obj = obj.getJSONObject("rate");
				if (obj.has("Rate")) {
					addStorage(new Store(obj.getDouble("Rate"), name
							+ "currency"));
					return obj.getDouble("Rate");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static String getCoinmarketcapCoinToUSD(String name) {
		try {
			if (_coinmarketcap_names.containsKey(name.toLowerCase())) {
				name = _coinmarketcap_names.get(name.toLowerCase());
			}
			double ret = reset(name + "coin");
			if (ret != -1) {
				return String.valueOf(ret);
			}
			HttpRequest request = HttpRequest
					.get("http://coinmarketcap.northpole.ro/api/" + name
							+ ".json");
			request.acceptGzipEncoding();
			if (request.ok()) {
				JSONObject a = new JSONObject(request.body());
				if (a.has("price")) {
					double result = a.getDouble("price");
					addStorage(new Store(result, name + "coin"));
					return LSystem.getNumber(new BigDecimal(result));
				}
			}

		} catch (Exception e) {
		}
		return String.valueOf(-1);
	}

	public static JSONArray getCoinmarketcap365d(String name)
			throws HttpRequestException, JSONException, IOException {
		HttpRequest request = HttpRequest
				.get("http://coinmarketcap.com/static/generated_pages/currencies/datapoints/"
						+ name + "-365d.json");
		request.acceptGzipEncoding();
		if (request.ok()) {
			return new JSONArray(request.body());
		}
		return null;
	}

	public static JSONArray getCoinmarketcap1d(String name)
			throws HttpRequestException, JSONException, IOException {
		HttpRequest request = HttpRequest
				.get("http://coinmarketcap.com/static/generated_pages/currencies/datapoints/"
						+ name + "-1d.json");
		request.acceptGzipEncoding();
		if (request.ok()) {
			return new JSONArray(request.body());
		}
		return null;
	}

	public static CoinmarketcapData getCoinmarketcapTo(String cur, String name)
			throws HttpRequestException, JSONException, IOException {
		if (!_coinmarketcap_limits.contains(cur.toLowerCase())) {
			cur = "usd";
		}
		if (_coinmarketcap_names.containsKey(name.toLowerCase())) {
			name = _coinmarketcap_names.get(name.toLowerCase());
		}
		HttpRequest request = HttpRequest.get(String.format(
				"http://coinmarketcap.northpole.ro/api/%s/%s.json", cur, name));
		request.acceptGzipEncoding();
		if (request.ok()) {
			JSONObject obj = new JSONObject(request.body());
			CoinmarketcapData data = CoinmarketcapData.from(obj);
			return data;
		}
		return null;
	}

	public static ArrayList<CoinmarketcapData> getCoinmarketcapAllTo(int limit)
			throws Exception {
		HttpRequest request = HttpRequest
				.get("http://coinmarketcap.northpole.ro/api/all.json");
		request.acceptGzipEncoding();
		if (request.ok()) {
			InputStreamReader reader = new InputStreamReader(request.stream());
			StringBuilder sbr = new StringBuilder();
			boolean flag = false;
			for (;;) {
				int ch = reader.read();
				if (ch < 0) {
					break;
				}
				if (flag) {
					if (sbr.toString().endsWith(
							"\"position\":\"" + (limit + 1) + "\"")) {
						break;
					}
				} else {
					if (sbr.toString().endsWith("\"markets\":[")) {
						flag = true;
						sbr.delete(0, sbr.length());
					}
				}
				sbr.append((char) ch);
			}
			reader.close();
			String result = sbr.toString();
			JSONArray arrays = new JSONArray(String.format("[%s]",
					result.substring(0, result.lastIndexOf(","))));
			final int size = arrays.length();
			ArrayList<CoinmarketcapData> list = new ArrayList<CoinmarketcapData>(
					size);
			for (int i = 0; i < size; i++) {
				JSONObject o = arrays.getJSONObject(i);
				CoinmarketcapData data = CoinmarketcapData.from(o);
				list.add(data);
			}
			return list;
		}
		return null;
	}

	private static String FromNodeValue(Element el, String name) {
		return el.getElementsByTagName(name).item(0).getFirstChild()
				.getNodeValue();
	}

	public static LegalTenderCurrency[] getBoiAllLegalTenderRates()
			throws Exception {
		DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		Document doc;
		HttpRequest request = HttpRequest
				.get("http://www.boi.org.il/currency.xml");
		if (request.ok()) {
			doc = dBuilder.parse(request.stream());
			NodeList currencies = doc.getElementsByTagName("CURRENCY");
			LegalTenderCurrency result[] = new LegalTenderCurrency[currencies
					.getLength()];
			for (int i = 0; i < currencies.getLength(); i++) {
				Element el = (Element) currencies.item(i);
				result[i] = new LegalTenderCurrency();
				result[i].name = FromNodeValue(el, "NAME");
				result[i].country = FromNodeValue(el, "COUNTRY");
				result[i].currencyCode = FromNodeValue(el, "CURRENCYCODE");
				result[i].unit = Integer.parseInt(FromNodeValue(el, ("UNIT")));
				result[i].rate = Double
						.parseDouble(FromNodeValue(el, ("RATE")));
				result[i].change = Double.parseDouble(FromNodeValue(el,
						("CHANGE")));
			}
			return result;
		}
		return null;
	}

}
