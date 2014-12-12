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
import org.ripple.power.collection.ArrayMap;
import org.ripple.power.config.LSystem;
import org.ripple.power.utils.HttpRequest;
import org.ripple.power.utils.HttpRequest.HttpRequestException;
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
		public int position = 0;
		public String marketCap;
		public String change7d;
		public String currency;
		public String id;
		public String change1h;
		public long timestamp;
		public String volume24;
		public String price;
		public String name;
		public String change7h;
		public String totalSupply;
		public String change24;

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
		_coinmarketcap_names.put("bts", "btsx");
	}

	public static double reset(String name) {
		for (Store s : _storage) {
			if (s.name.equals(name)
					&& (System.currentTimeMillis() - s.date) <= LSystem.MINUTE) {
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
		if (name == null) {
			return -1;
		}
		try {
			name = name.trim().toLowerCase();
			if (_coinmarketcap_names.containsKey(name)) {
				name = _coinmarketcap_names.get(name);
			}
			double ret = reset(name + "currency");
			if (ret != -1) {
				return ret;
			}
			HttpRequest request = HttpRequest
					.get("http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.xchange%20where%20pair%20in%20('"
							+ name
							+ "')&format=json&diagnostics=false&env=http://datatables.org/alltables.env");

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
		if (name == null) {
			return null;
		}
		try {
			name = name.toLowerCase();
			if (_coinmarketcap_names.containsKey(name)) {
				name = _coinmarketcap_names.get(name);
			}
			double ret = reset(name + "coin");
			if (ret != -1) {
				return String.valueOf(ret);
			}
			HttpRequest request = HttpRequest
					.get("http://coinmarketcap.northpole.ro/api/" + name
							+ ".json");

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
		return null;
	}

	public static JSONObject getCoinmarketcapTo(int limit, String name)
			throws HttpRequestException, JSONException, IOException {
		return getCoinmarketcapTo(limit, name, null);
	}

	public static JSONObject getCoinmarketcapTo(int limit, String name,
			String jsonKey) throws HttpRequestException, JSONException,
			IOException {
		if (name == null) {
			return null;
		}
		name = name.trim().toLowerCase();
		if (_coin_names.containsKey(name)) {
			name = _coin_names.get(name);
		}
		String s = "http://coinmarketcap.com/static/generated_pages/currencies/datapoints/"
				+ name + "-" + limit + "d.json";
		HttpRequest request = HttpRequest.get(s);
		request.accept("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		request.userAgent("Moziaalla/5.0 (Windows NT 6.1; WOW64; rv:34.0) Gecko/20100101 Firefox/34.0");
		request.acceptEncoding("gzip, deflate");
		request.acceptLanguage("en-US,en;q=0.5");
		request.acceptCharset("ISO-8859-1,utf-8;q=0.7,*;q=0.7");
		request.acceptGzipEncoding();
		if (request.ok()) {
			request.uncompress(true);
			if (jsonKey == null) {
				return new JSONObject(request.body());
			} else {
				InputStreamReader reader = new InputStreamReader(
						request.stream());
				StringBuilder sbr = new StringBuilder();
				boolean flag = false;
				boolean brackets = false;
				for (;;) {
					char ch = (char) reader.read();
					if (ch < 0) {
						break;
					}
					if (!flag && sbr.indexOf(jsonKey) != -1) {
						flag = true;
					}
					if (flag) {
						if (!brackets && ch == '[') {
							brackets = true;
						}
						if (brackets && ch == '"') {
							sbr.delete(sbr.length() - 2, sbr.length());
							sbr.append('}');
							return new JSONObject(sbr.toString());
						}
					}
					sbr.append(ch);
				}
				reader.close();
				return new JSONObject(sbr);
			}
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
		HttpRequest request = HttpRequest.get(String.format(
				"http://coinmarketcap.northpole.ro/api/%s/%s.json", cur, name));
		if (request.ok()) {
			JSONObject obj = new JSONObject(request.body());
			CoinmarketcapData data = CoinmarketcapData.from(obj);
			return data;
		}
		return null;
	}

	public static ArrayList<CoinmarketcapData> getCoinmarketcapAllTo(int limit)
			throws Exception {
		if (limit <= 0) {
			limit = 1;
		}
		HttpRequest request = HttpRequest
				.get("http://coinmarketcap.northpole.ro/api/all.json");

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

	private static String find(int nStartLine, int nEndLine, BufferedReader br)
			throws IOException {
		String line;
		String target = "";
		String elements = "";
		int i = 0;
		while ((line = br.readLine()) != null) {
			++i;
			if (i < nStartLine) {
				continue;
			}
			line.trim();
			target = target + line;
			if (i >= nEndLine) {
				break;
			}

		}
		Pattern r = Pattern.compile(REGEX_TABLE, Pattern.CASE_INSENSITIVE);
		Matcher mTable = r.matcher(target);
		if (mTable.find()) {
			String strRows = mTable.group(1).trim();

			Matcher mRow = Pattern.compile(REGEX_ROW, Pattern.CASE_INSENSITIVE)
					.matcher(strRows);
			while (mRow.find()) {
				boolean firstEle = true;
				String strEle = mRow.group(1).trim();

				Matcher mEle = Pattern.compile(REGEX_ELE,
						Pattern.CASE_INSENSITIVE).matcher(strEle);
				if (!elements.equals(""))
					elements = elements + ROW_SEPARATOR;
				while (mEle.find()) {
					String result = mEle.group(1).trim();
					if (firstEle)
						elements = elements + result;
					else
						elements = elements + ELEMENT_SEPARATOR + result;
					firstEle = false;
				}
				if (!elements.equals("")) {
					int len = elements.length();
					elements = elements.substring(0, len - 2);
				}
			}
		}
		return new String(elements);
	}

	private final static DecimalFormat NUMBER_FORMAT = new DecimalFormat(
			"0.00000");

	public static ArrayList<String> getAllLegalTenderRateHTML()
			throws Exception {
		HttpRequest request = HttpRequest.get("http://www.usd-cny.com/");
		request.acceptGzipEncoding();
		request.uncompress(true);

		if (!request.ok()) {
			return null;
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(
				request.stream(), "gb2312"));

		String result = find(78, 313, br);

		String strRows[] = result.split(ROW_SEPARATOR);
		String strCur, strRate;
		NumberFormat numFormat = NumberFormat.getNumberInstance();
		Number numb = null;

		ArrayList<String> hashTable = new ArrayList<String>();

		for (int i = 1; i < strRows.length; i++) {
			String strEle[] = strRows[i].split(ELEMENT_SEPARATOR);
			if (strEle[3].equals("")) {
				break;
			}
			strCur = strEle[0].split(" ")[1];
			strRate = strEle[3];
			numb = numFormat.parse(strRate);
			strRate = numb.toString();
			hashTable.add("1/" + strCur + "<br>Rate<br>"
					+ NUMBER_FORMAT.format(Double.valueOf(strRate) / 100.0d)
					+ "/CNY");
			hashTable.add("1/CNY" + "<br>Rate<br>"
					+ NUMBER_FORMAT.format(100.0d / Double.valueOf(strRate))
					+ "/" + strCur);
		}

		br.close();
		return hashTable;
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
		return getCharacterDataFromElement((Element) parent
				.getElementsByTagName(label).item(0));
	}

	public static String converterMoney(String src, String cur)
			throws Exception {
		if (src == null || cur == null) {
			return null;
		}
		String query = (cur + "/" + src).toUpperCase();
		double ret = reset(query);
		if (ret != -1) {
			return String.valueOf(ret);
		}
		HttpRequest request = HttpRequest.get(String.format(
				"http://themoneyconverter.com/rss-feed/%s/rss.xml", src));
		request.acceptGzipEncoding();
		request.uncompress(true);
		if (!request.ok()) {
			return null;
		}
		DocumentBuilder builder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		Document doc = builder.parse(request.stream());
		NodeList nodes = doc.getElementsByTagName("item");
		for (int i = 0; i < nodes.getLength(); i++) {
			Element element = (Element) nodes.item(i);
			String description = getElementValue(element, "description");
			if (query.equals(getElementValue(element, "title"))) {
				String[] leftright = description.split("=");
				String[] rightWords = leftright[1].split(" ");
				addStorage(new Store(Double.parseDouble(rightWords[1]), query));
				return rightWords[1];
			}
		}

		return null;
	}

	private static HashMap<String, String> _coin_names = new HashMap<String, String>(
			10);
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

	public static ArrayMap getCapitalization(int day, String name)
			throws Exception {
		return getCapitalization(day, name, -1);
	}

	public static ArrayMap getCapitalization(int day, String name,
			int trend_limit) throws Exception {
		if (name == null) {
			return null;
		}
		name = name.trim().toLowerCase();
		if (_coin_names.containsKey(name)) {
			name = _coin_names.get(name);
		}
		final String jsonKey = "market_cap_by_available_supply_data";
		JSONObject o = (getCoinmarketcapTo(day, name, jsonKey));
		if (o == null) {
			CoinmarketcapData data = getCoinmarketcapTo("usd", name);
			if (data != null) {
				_coin_names.put(name, data.name);
				name = data.name;
				o = (getCoinmarketcapTo(1, name));
			}
		}
		if (o == null) {
			return null;
		}
		DateFormat YYYY_MM_DD_HHMM = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		ArrayMap list = new ArrayMap(100);
		if (o.has(jsonKey)) {
			JSONArray arrays = o.getJSONArray(jsonKey);
			if (trend_limit > 0) {
				for (int i = arrays.length() - trend_limit; i < arrays.length(); i++) {
					JSONArray result = arrays.getJSONArray(i);
					list.put(result.getLong(0), result.getLong(1));
				}
			} else {
				for (int i = 0; i < arrays.length(); i++) {
					JSONArray result = arrays.getJSONArray(i);
					String key = YYYY_MM_DD_HHMM.format(new Date(result
							.getLong(0)));
					String value = String.valueOf(result.getLong(1));
					list.put(key, value);
				}
			}
		}
		return list;
	}

}
