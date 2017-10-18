package org.ripple.power.txns.data;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ripple.power.config.LSystem;

public class AccountLineResult {
	public String account;
	public long ledger_current_index;
	public List<Line> lines = new ArrayList<Line>(LSystem.DEFAULT_MAX_CACHE_SIZE);
	public boolean validated;

	public void from(JSONObject obj) {
		if (obj != null) {
			lines.clear();
			this.account = obj.optString("account");
			this.ledger_current_index = obj.optLong("ledger_current_index");
			this.validated = obj.optBoolean("validated");
			Object lines_array = obj.opt("lines");
			if (lines_array != null && lines_array instanceof JSONArray) {
				JSONArray list = (JSONArray) lines_array;
				int size = list.length();
				if (size > 0) {
					for (int i = 0; i < size; i++) {
						Line line = new Line();
						line.from(list.optJSONObject(i));
						lines.add(line);
					}
				}
			}
		}
	}
}
