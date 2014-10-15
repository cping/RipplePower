package org.ripple.power.txns;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.ripple.power.ui.UIRes;

public class Gateway {

	public static class Item {

		public String address;
		public ArrayList<String> currencies = new ArrayList<String>(10);
	}

	public String name;

	public String domain;

	public ArrayList<String> hotwallets = new ArrayList<String>(10);

	public ArrayList<Item> accounts = new ArrayList<Item>(10);

	public int level = 0;

	private final static ArrayList<Gateway> gateways = new ArrayList<Gateway>(
			100);

	public static String[] gatewayList() {
		ArrayList<Gateway> temps = get();
		String[] list = new String[temps.size()];
		for (int i = 0; i < temps.size(); i++) {
			list[i] = temps.get(i).name;
		}
		return list;
	}

	public static Gateway getGateway(String address) {
		ArrayList<Gateway> temps = get();
		for (Gateway g : temps) {
			for (Item item : g.accounts) {
				if (item.address.equals(address)) {
					return g;
				}
			}
			for (String hotwallet : g.hotwallets) {
				if (hotwallet.equals(address)) {
					return g;
				}
			}
		}
		return null;
	}

	public synchronized static ArrayList<Gateway> get() {
		if (gateways.size() == 0) {
			JSONTokener jsonTokener = new JSONTokener(
					UIRes.getStream("config/gateways.json"));
			JSONArray array = new JSONArray(jsonTokener);

			for (int i = 0; i < array.length(); i++) {
				Gateway gateway = new Gateway();
				JSONObject o = array.getJSONObject(i);
				String name = o.getString("name");
				gateway.name = name;
				JSONArray hotwallets = o.getJSONArray("hotwallets");
				for (int n = 0; n < hotwallets.length(); n++) {
					gateway.hotwallets.add(hotwallets.getString(n));
				}
				JSONArray accounts = o.getJSONArray("accounts");
				for (int n = 0; n < accounts.length(); n++) {
					JSONObject obj = accounts.getJSONObject(n);
					String address = obj.getString("address");
					Item item = new Item();
					item.address = address;
					JSONArray currencies = obj.getJSONArray("currencies");
					for (int m = 0; m < currencies.length(); m++) {
						Object value = currencies.get(m);
						if (value instanceof String) {
							item.currencies.add((String) value);
						} else if (value instanceof JSONObject) {
							item.currencies.add(((JSONObject) value)
									.getString("label"));
						}
					}
					gateway.accounts.add(item);
				}
				gateways.add(gateway);
			}
		}
		return gateways;
	}

}
