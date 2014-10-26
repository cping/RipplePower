package org.ripple.power.txns;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ripple.power.utils.HttpRequest;
import org.ripple.power.utils.HttpRequest.HttpRequestException;

import com.ripple.core.coretypes.RippleDate;

public class OtherData {

	public static class CoinmarketcapData {
		int position = 0;
		String marketCap;
		String change7d;
		String currency;
		String id;
		String change1h;
		long timestamp;
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
				data.currency = o.getString("currency");
				data.id = o.getString("id");
				data.change1h = o.getString("change1h");
				data.timestamp = o.getLong("timestamp");
				data.price = o.getString("price");
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
					+ change7d + "%<br>" + "7hour:" + change7h + "%<br>" + "1hour:"
					+ change1h + "%<br>" + price + "/" + currency.toUpperCase()
					+ "==1/" + name + "<br>" + RippleDate.now().getTimeString();
		}

		public String toString() {
			return name + "/" + currency.toUpperCase() + " " + " Week:"
					+ change7d + "% " + " 7hour:" + change7h + "% " + " 1hour:"
					+ change1h + "% " + price + "/" + currency.toUpperCase()
					+ "==1/" + name + " " + RippleDate.now().getTimeString();
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

	public static JSONArray getCoinmarketcap365d(String name)
			throws HttpRequestException, JSONException, IOException {
		HttpRequest request = HttpRequest
				.get("http://coinmarketcap.com/static/generated_pages/currencies/datapoints/"
						+ name + "-365d.json");
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
		if (request.ok()) {
			JSONObject obj = new JSONObject(request.body());
			CoinmarketcapData data = CoinmarketcapData.from(obj);
			return data;
		}
		return null;
	}

	public static JSONObject getCoinmarketcapAllTo()
			throws HttpRequestException, JSONException, IOException {
		HttpRequest request = HttpRequest
				.get("http://coinmarketcap.northpole.ro/api/all.json");
		if (request.ok()) {
			JSONObject obj = new JSONObject(request.body());
			return obj;
		}
		return null;
	}

}
