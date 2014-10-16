package org.ripple.power.txns;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class TrustLines extends ArrayList<TrustLine> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void copyFrom(JSONObject jsonCommandResult) {
		JSONArray jsonLinesOfTrust = jsonCommandResult.getJSONArray("lines");
		for (int i = 0; i < jsonLinesOfTrust.length(); i++) {
			TrustLine lineOfCredit = new TrustLine();
			lineOfCredit.copyFrom((JSONObject) jsonLinesOfTrust.get(i));
			add(lineOfCredit);
		}
	}
}
