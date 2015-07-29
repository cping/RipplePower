package org.ripple.power.txns;

import org.json.JSONObject;
import org.ripple.power.txns.data.AccountInfoRequest;
import org.ripple.power.txns.data.AccountInfoResponse;
import org.ripple.power.txns.data.AccountLinesResponse;
import org.ripple.power.ui.RPClient;

import com.ripple.client.enums.Command;
import com.ripple.client.requests.Request;
import com.ripple.client.responses.Response;

//select rippled api or ripple rest api
public class RippleBackendsAPI {

	public static enum Model {
		RippleRestAPI, Rippled
	}

	public Model model = Model.Rippled;

	public AccountInfoRequest getAccountInfo(final String address,
			final Updateable update) {
		if (!AccountFind.isRippleAddress(address)) {
			throw new RuntimeException("not ripple address !");
		}
		final AccountInfoResponse accountInfo = new AccountInfoResponse();
		// rippled
		switch (model) {
		case Rippled:
			RPClient client = RPClient.ripple();
			if (client != null) {

				Request req = client.newRequest(Command.account_info);
				req.json("account", address);
				req.once(Request.OnSuccess.class, new Request.OnSuccess() {
					@Override
					public void called(Response response) {

						JSONObject result = response.message;
						if (result != null) {
							accountInfo.from(result);
						}
						if (update != null) {
							update.action(result);
						}

					}
				});
				req.once(Request.OnError.class, new Request.OnError() {
					@Override
					public void called(Response response) {
						if (update != null) {
							update.action(response.error_message);
						}
					}
				});
				req.request();
			}
			break;
		default:
			break;
		}

		return null;
	}

	public AccountLinesResponse getAccountLines(final String address,
			final Updateable update) {
		if (!AccountFind.isRippleAddress(address)) {
			throw new RuntimeException("not ripple address !");
		}
		final AccountLinesResponse accountLines = new AccountLinesResponse();
		// rippled
		switch (model) {
		case Rippled:
			RPClient client = RPClient.ripple();
			if (client != null) {
				Request req = client.newRequest(Command.account_lines);
				req.json("account", address);
				req.json("ledger","current");
				req.once(Request.OnSuccess.class, new Request.OnSuccess() {
					@Override
					public void called(Response response) {
						JSONObject result = response.message;
						if (result != null) {
							accountLines.from(result);
						}
						if (update != null) {
							update.action(result);
						}

					}
				});
				req.once(Request.OnError.class, new Request.OnError() {
					@Override
					public void called(Response response) {
						if (update != null) {
							update.action(response.error_message);
						}
					}
				});
				req.request();
			}
			break;
		default:
			break;
		}
		return null;
	}
	
}
