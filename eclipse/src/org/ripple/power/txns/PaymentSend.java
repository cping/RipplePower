package org.ripple.power.txns;

import org.ripple.power.ui.RPClient;

import org.json.JSONObject;

import com.ripple.client.enums.Command;
import com.ripple.client.requests.Request;
import com.ripple.client.responses.Response;
import com.ripple.core.coretypes.AccountID;
import com.ripple.core.coretypes.Currency;

public class PaymentSend {

	private String _currencyName;

	private String _issuer;

	public PaymentSend() {
		this(null, null);
	}

	public PaymentSend(String cName, String issuer) {
		if (cName != null) {
			_currencyName = _currencyName.toUpperCase();
		} else {
			_currencyName = "XRP";
		}
		if (issuer != null) {
			_issuer = issuer;
		}
	}

	public void makePayment(String srcAddress, String seed, String dstAddress,
			String amount, String fee, final Rollback back) {
		RPClient client = RPClient.ripple();
		if (client != null) {
			Request req = client.newRequest(Command.submit);
			JSONObject tx = new JSONObject();
			tx.put("TransactionType", "Payment");
			tx.put("Account", srcAddress);
			tx.put("Amount", CurrencyUtils.getValueToRipple(amount));
			tx.put("Destination", dstAddress);
			tx.put("Fee", CurrencyUtils.getValueToRipple(fee) + "0");
			req.json("tx_json", tx);
			req.json("secret", seed);
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
