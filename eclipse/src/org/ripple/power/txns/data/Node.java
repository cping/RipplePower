package org.ripple.power.txns.data;

import org.json.JSONObject;
import org.ripple.power.utils.StringUtils;

public class Node {

	public String LedgerEntryType;

	public String LedgerIndex;

	public Field PreviousFields;

	public Field NewFields;

	public Field FinalFields;

	public void from(JSONObject obj) {
		if (obj != null) {
			this.LedgerEntryType = obj.optString("LedgerEntryType");
			this.LedgerIndex = obj.optString("LedgerIndex");
			JSONObject previous = obj.optJSONObject("PreviousFields");
			if (previous != null) {
				PreviousFields = new Field();
				PreviousFields.from(previous);
			}
			JSONObject news = obj.optJSONObject("NewFields");
			if (news != null) {
				NewFields = new Field();
				NewFields.from(news);
			}
			JSONObject finals = obj.optJSONObject("FinalFields");
			if (finals != null) {
				FinalFields = new Field();
				FinalFields.from(finals);
			}
		}
	}

	public String toString() {
		return StringUtils.join(",", LedgerEntryType, LedgerIndex);
	}

}
