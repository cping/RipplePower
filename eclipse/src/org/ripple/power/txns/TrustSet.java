package org.ripple.power.txns;
import org.address.ripple.RippleObject;
import org.address.ripple.RippleSeedAddress;
import org.address.ripple.RippleSchemas.BinaryFormatField;
import org.address.ripple.RippleSchemas.TransactionTypes;

import org.json.JSONObject;
import org.ripple.power.ui.RPClient;

import com.ripple.client.enums.Command;
import com.ripple.client.requests.Request;
import com.ripple.client.responses.Response;

public class TrustSet {

	public static void setTxJson(final String seed,
			final IssuedCurrency currency, final String fee, final Rollback back) {
		TrustSet.setTxJson(new RippleSeedAddress(seed), currency, fee, back);
	}

	public static void setTxJson(final RippleSeedAddress seed,
			final IssuedCurrency currency, final String fee, final Rollback back) {
		RPClient client = RPClient.ripple();
		if (client != null) {
			Request req = client.newRequest(Command.submit);
			JSONObject obj = new JSONObject();
			obj.put("TransactionType", "TrustSet");
			obj.put("Account", seed.getPublicKey());
			JSONObject limitAmount = new JSONObject();
			limitAmount.put("currency", currency.currency);
			limitAmount.put("value",
					String.valueOf(currency.amount.longValue()));
			limitAmount.put("issuer", currency.issuer.toString());
			obj.put("LimitAmount", limitAmount);
			obj.put("Fee", CurrencyUtils.getValueToRipple(fee) + "0");
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
							(int) TransactionTypes.TRUST_SET.byteValue);
					item.putField(BinaryFormatField.Account,
							seed.getPublicRippleAddress());
					item.putField(BinaryFormatField.LimitAmount, currency);
					item.putField(BinaryFormatField.Fee,
							CurrencyUtils.getValueToRipple(fee) + "0");
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
}
