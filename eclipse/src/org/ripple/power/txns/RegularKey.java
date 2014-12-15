package org.ripple.power.txns;

import org.json.JSONObject;
import org.ripple.power.RippleObject;
import org.ripple.power.RippleSeedAddress;
import org.ripple.power.RippleSchemas.BinaryFormatField;
import org.ripple.power.RippleSchemas.TransactionTypes;
import org.ripple.power.ui.RPClient;

import com.ripple.client.enums.Command;
import com.ripple.client.requests.Request;
import com.ripple.client.responses.Response;

public class RegularKey {

	public static void set(final String seed, final IssuedCurrency currency,
			final String fee, final Rollback back) {
		TrustSet.set(new RippleSeedAddress(seed), currency, fee, back);
	}

	public static void set(final RippleSeedAddress seed,
			final IssuedCurrency currency, final String fee, final Rollback back) {
		final String address = seed.getPublicRippleAddress().toString();
		AccountFind find = new AccountFind();
		find.info(address, new Rollback() {
			@Override
			public void success(JSONObject message) {
				try {
					long sequence = TransactionUtils.getSequence(message);
					RippleObject item = new RippleObject();
					item.putField(BinaryFormatField.TransactionType,
							(int) TransactionTypes.REGULAR_KEY_SET.byteValue);
					item.putField(BinaryFormatField.Account,
							seed.getPublicRippleAddress());
					item.putField(BinaryFormatField.LimitAmount, currency);
					item.putField(BinaryFormatField.Fee,
							CurrencyUtils.getValueToRipple(fee));
					item.putField(BinaryFormatField.Sequence, sequence);
					TransactionUtils.submitBlob(seed, item, back);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void error(JSONObject message) {
				if (back != null) {
					back.error(message);
				}

			}
		});

	}
	public static void setTxJson(final String seed, final String message,
			final String fee, final Rollback back) {
		RegularKey.setTxJson(new RippleSeedAddress(seed), message, fee, back);
	}

	public static void setTxJson(final RippleSeedAddress seed,
			final String message, final String fee, final Rollback back) {
		RPClient client = RPClient.ripple();
		if (client != null) {
			Request req = client.newRequest(Command.submit);
			JSONObject obj = new JSONObject();
			obj.put("TransactionType", "SetRegularKey");
			obj.put("Account", seed.getPublicKey());
			obj.put("RegularKey", message);
			obj.put("Fee", CurrencyUtils.getValueToRipple(fee));
			req.json("tx_json", obj);
			req.json("secret", seed.getPrivateKey());
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
