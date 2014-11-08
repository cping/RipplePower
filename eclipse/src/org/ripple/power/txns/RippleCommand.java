package org.ripple.power.txns;

import org.json.JSONObject;
import org.ripple.power.ui.RPClient;

import com.ripple.client.enums.Command;
import com.ripple.client.requests.Request;
import com.ripple.client.responses.Response;

public class RippleCommand {

	public static void get(final Command cmd, JSONObject obj,
			final Rollback back) {
		RPClient client = RPClient.ripple();
		if (client != null) {
			Request req = client.newRequest(cmd);
			req.json(obj);
			req.once(Request.OnSuccess.class, new Request.OnSuccess() {
				@Override
				public void called(Response response) {
					if (back != null) {
						back.success(response.message);
					}
				}
			});
			req.once(Request.OnError.class, new Request.OnError() {
				@Override
				public void called(Response response) {
					if (back != null) {
						back.error(response.message);
					}
				}
			});
			req.request();
		}
	}

}
