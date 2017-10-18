package org.ripple.power.txns;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ripple.power.config.LSystem;

public class CurrencyGateway {

	public class Item {

		public String currencyName;

		public ArrayList<GatewayItem> gateways = new ArrayList<GatewayItem>(10);

		@Override
		public String toString() {
			StringBuilder sbr = new StringBuilder();
			for (GatewayItem item : gateways) {
				sbr.append(item);
				sbr.append(LSystem.LS);
			}
			return sbr.toString();
		}

	}

	public ArrayList<CurrencyGateway.Item> currencys = new ArrayList<CurrencyGateway.Item>(10);

	public JSONObject json;

	public void copyFrom(JSONObject result) {
		this.json = result;
		if (result != null) {
			@SuppressWarnings("unchecked")
			Iterator<Object> keys = result.keys();
			for (; keys.hasNext();) {
				Object obj = keys.next();
				String name = obj.toString();
				CurrencyGateway.Item item = new CurrencyGateway.Item();
				// gbi
				if ("0158415500000000C1F76FF6ECB0BAC600000000".equals(name)) {
					item.currencyName = "GBI";
				} else {
					item.currencyName = name;
				}
				JSONArray arrays = result.optJSONArray(name);
				if (arrays != null && arrays.length() > 0) {
					for (int i = 0; i < arrays.length(); i++) {
						JSONObject o = arrays.getJSONObject(i);
						GatewayItem gatewayItem = new GatewayItem();
						gatewayItem.copyFrom(o);
						item.gateways.add(gatewayItem);
					}
				}
				currencys.add(item);
			}
		}
	}

	public CurrencyGateway.Item find(String name) {
		for (CurrencyGateway.Item item : currencys) {
			if (item.currencyName.equalsIgnoreCase(name)) {
				return item;
			}
		}
		return null;
	}

	public int size() {
		return currencys.size();
	}

	@Override
	public String toString() {
		if (json == null) {
			return null;
		}
		return json.toString();
	}
}
