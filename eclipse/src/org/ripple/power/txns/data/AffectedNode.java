package org.ripple.power.txns.data;

import org.json.JSONObject;

public class AffectedNode {

	public Node CreatedNode;

	public Node ModifiedNode;

	public Node DeletedNode;

	public void from(JSONObject obj) {
		if (obj != null) {
			JSONObject created = obj.optJSONObject("CreatedNode");
			if (created != null) {
				CreatedNode = new Node();
				CreatedNode.from(created);
			}
			JSONObject modified = obj.optJSONObject("ModifiedNode");
			if (modified != null) {
				ModifiedNode = new Node();
				ModifiedNode.from(modified);
			}
			JSONObject deleted = obj.optJSONObject("DeletedNode");
			if (deleted != null) {
				DeletedNode = new Node();
				DeletedNode.from(deleted);
			}
		}
	}

	public String toString() {
		StringBuilder sbr = new StringBuilder();
		sbr.append("CreatedNode:");
		if (CreatedNode != null) {
			sbr.append(CreatedNode);
		}
		sbr.append("ModifiedNode:");
		if (ModifiedNode != null) {
			sbr.append(ModifiedNode);
		}
		sbr.append("DeletedNode:");
		if (DeletedNode != null) {
			sbr.append(DeletedNode);
		}
		return sbr.toString();
	}

}
