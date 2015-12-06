package org.ripple.power.txns.data;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class Meta {

	public int TransactionIndex;

	public ArrayList<AffectedNode> AffectedNodes = new ArrayList<AffectedNode>(
			20);

	public String TransactionResult;

	public void from(JSONObject obj) {
		if (obj != null) {
			this.TransactionIndex = obj.optInt("TransactionIndex");
			this.TransactionResult = obj.optString("TransactionResult");
			JSONArray array = obj.optJSONArray("AffectedNodes");
			if (array != null) {
				for (int i = 0; i < array.length(); i++) {
					JSONObject result = array.getJSONObject(i);
					AffectedNode node = new AffectedNode();
					node.from(result);
					AffectedNodes.add(node);
				}
			}
		}
	}

}
