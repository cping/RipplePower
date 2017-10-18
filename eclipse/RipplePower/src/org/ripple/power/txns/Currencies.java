package org.ripple.power.txns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.ripple.power.ui.UIRes;

import com.google.common.base.Strings;

public class Currencies {

	private static ArrayList<Item> _items = new ArrayList<Item>(100);

	private static Comparator<Item> itmeComparator = new Comparator<Currencies.Item>() {

		@Override
		public int compare(Item o1, Item o2) {
			return o2.order - o1.order;
		}
	};

	public static class Item {
		// {"value": "XRP", "name": "Ripple", "standard_precision": 4, "order":
		// 5}
		public String value;
		public String name;
		public int standard_precision;
		public int order;
		public boolean display;

		public String toString() {
			return value;
		}
	}

	public static ArrayList<String> values() {
		ArrayList<Item> items = load();
		int size = items.size();
		ArrayList<String> list = new ArrayList<String>(size);
		for (Item item : items) {
			list.add(item.value);
		}
		return list;
	}

	public static ArrayList<String> keys() {
		ArrayList<Item> items = load();
		int size = items.size();
		ArrayList<String> list = new ArrayList<String>(size);
		for (Item item : items) {
			list.add(item.name);
		}
		return list;
	}

	public static String name(String name) {
		if (Strings.isNullOrEmpty(name)) {
			return null;
		}
		ArrayList<Item> items = load();
		for (Item item : items) {
			if (name.equalsIgnoreCase(item.value)) {
				return item.name;
			}
		}
		return null;
	}

	public static ArrayList<Item> get() {
		return new ArrayList<Item>(load());
	}

	private static ArrayList<Item> load() {
		if (_items.size() == 0) {
			JSONTokener tokener = null;
			try {
				tokener = new JSONTokener(UIRes.getStream("config/currencies.json"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			JSONArray arrays = new JSONArray(tokener);
			for (int i = 0; i < arrays.length(); i++) {
				JSONObject obj = arrays.getJSONObject(i);
				Item item = new Item();
				item.name = obj.getString("name");
				item.value = obj.getString("value");
				item.order = obj.getInt("order");
				item.standard_precision = obj.getInt("standard_precision");
				if (obj.has("display")) {
					item.display = obj.getBoolean("display");
				}
				_items.add(item);
			}
			Collections.sort(_items, itmeComparator);
		}
		return _items;
	}
}
