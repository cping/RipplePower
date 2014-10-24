package org.ripple.power.txns;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.ripple.power.config.LSystem;
import org.ripple.power.config.Session;
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

	private final static ArrayList<Gateway> user_gateways = new ArrayList<Gateway>(
			100);

	public static JSONArray setUserGateway(ArrayList<Gateway> gs) {
		if (gs == null || gs.size() == 0) {
			return null;
		}
		if (gs != user_gateways) {
			user_gateways.clear();
		}
		JSONArray result = new JSONArray();
		for (Gateway g : gs) {
			JSONObject obj = new JSONObject();
			obj.put("name", g.name);
			if (g.accounts.size() > 0) {
				obj.put("address", g.accounts.get(0).address);
				JSONArray arrays = new JSONArray();
				for (String c : g.accounts.get(0).currencies) {
					arrays.put(c);
				}
				obj.put("currencies", arrays);
			}
			result.put(obj);
		}
		Session session = LSystem.session("user_gateway");
		session.set("data", result.toString());
		session.save();
		return result;
	}

	public static int delUserGateway(String name) {
		int idx = -1;
		if (name == null) {
			return idx;
		}
		ArrayList<Gateway> gs = getUserGateway();
		int count = 0;
		for (Gateway g : gs) {
			if (g.name.equalsIgnoreCase(name)) {
				idx = count;
				break;
			}
			count++;
		}
		if (idx != -1) {
			gs.remove(idx);
			setUserGateway(gs);
		}
		return idx;
	}

	public static Gateway delIndexUserGateway(int index) {
		if (index < 0) {
			return null;
		}
		ArrayList<Gateway> gs = getUserGateway();
		if (index >= gs.size()) {
			return null;
		}
		Gateway g = gs.remove(index);
		setUserGateway(gs);
		return g;
	}

	public static ArrayList<Gateway> getUserGateway() {
		if (user_gateways.size() == 0) {
			Session session = LSystem.session("user_gateway");
			String result = session.get("data");
			if (result != null) {
				user_gateways.addAll(getUserGateway(result));
				return user_gateways;
			}
			return null;
		} else {
			return user_gateways;
		}
	}

	public static ArrayList<Gateway> getUserGateway(String result) {
		if (result == null || result.length() == 0) {
			return null;
		}
		JSONArray arrays = new JSONArray(result);
		ArrayList<Gateway> list = new ArrayList<>();
		for (int i = 0; i < arrays.length(); i++) {
			JSONObject obj = arrays.getJSONObject(i);
			Gateway g = new Gateway();
			g.name = obj.getString("name");
			if (obj.has("address")) {
				Item item = new Item();
				item.address = obj.getString("address");
				if (obj.has("currencies")) {
					JSONArray currencies = obj.getJSONArray("currencies");
					for (int j = 0; j < currencies.length(); j++) {
						item.currencies.add(currencies.getString(j));
					}
				}
				g.accounts.add(item);
			}
			list.add(g);
		}
		return list;
	}

	public static ArrayList<String> gatewayList() {
		ArrayList<Gateway> temps = get();
		ArrayList<String> list = new ArrayList<>();
		for (int i = 0; i < temps.size(); i++) {
			list.add(temps.get(i).name);
		}
		ArrayList<Gateway> userlist = getUserGateway();
		if (userlist != null) {
			for (int i = 0; i < userlist.size(); i++) {
				list.add(userlist.get(i).name);
			}
		}
		return list;
	}

	//
	public static Gateway getOneUserAddress(String name) {
		if (name == null) {
			return null;
		}
		ArrayList<Gateway> temps = getUserGateway();
		for (Gateway g : temps) {
			if (g.name.equalsIgnoreCase(name)) {
				return g;
			}
		}
		return null;
	}

	public static Gateway getOneUserGateway(String address) {
		if (address == null) {
			return null;
		}
		ArrayList<Gateway> temps = getUserGateway();
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

	//
	public static Gateway getAddress(String name) {
		if (name == null) {
			return null;
		}
		ArrayList<Gateway> temps = get();
		for (Gateway g : temps) {
			if (g.name.equalsIgnoreCase(name)) {
				return g;
			}
		}
		temps = getUserGateway();
		for (Gateway g : temps) {
			if (g.name.equalsIgnoreCase(name)) {
				return g;
			}
		}
		return null;
	}

	public static Gateway getGateway(String address) {
		if (address == null) {
			return null;
		}
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
		temps = getUserGateway();
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
