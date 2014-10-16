package org.ripple.power.txns;

import org.json.JSONObject;

public interface Rollback {
	
	 void success(JSONObject res);

	 void error(JSONObject res);

}
