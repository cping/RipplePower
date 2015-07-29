package org.ripple.power.txns.data;

import org.json.JSONObject;

public class LastClose {
	public int converge_time;
	public int proposers;

	public void from(JSONObject last_close) {
		if (last_close != null) {
			this.proposers = last_close.optInt("proposers");
			this.converge_time = last_close.optInt("converge_time");
		}
	}
}
