package org.ripple.power.txns.data;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class TransactionsResponse {

	public boolean result;
	public int count;
	public String marker;
	public ArrayList<Transaction> transactions = new ArrayList<Transaction>(20);
	public JSONObject json;

	public void from(JSONObject obj) {
		if (obj != null) {
			this.json = obj;
			this.result = obj.optBoolean("result");
			this.count = obj.optInt("count");
			this.marker = obj.optString("marker");
			JSONArray array = obj.optJSONArray("transactions");
			if (array != null) {
				int size = array.length();
				for (int i = 0; i < size; i++) {
					Transaction transaction = new Transaction();
					transaction.from(array.get(i));
					transactions.add(transaction);
				}
			}
		}
	}

}
