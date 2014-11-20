package org.ripple.power.txns;


import org.json.JSONObject;
import org.ripple.power.RippleObject;
import org.ripple.power.RippleSeedAddress;
import org.ripple.power.RippleSchemas.BinaryFormatField;
import org.ripple.power.RippleSchemas.TransactionTypes;
import org.ripple.power.ui.RPClient;
import org.ripple.power.utils.StringUtils;

import com.ripple.client.enums.Command;
import com.ripple.client.requests.Request;
import com.ripple.client.responses.Response;

public class TrustSet {

	public static IssuedCurrency fromString(String res) {
		String[] split = StringUtils.split(res, "/");
		int idx = split.length;
		IssuedCurrency currency;
		if (idx == 0) {
			currency = new IssuedCurrency();
		} else if (idx == 1) {
			currency = new IssuedCurrency(split[0]);
		} else if (idx == 3) {
			String address = split[2].split(" ")[0];
			if (address.startsWith("r") && AccountFind.isRippleAddress(address)) {
				currency = new IssuedCurrency(split[0], address, split[1]);
			} else {
				currency = new IssuedCurrency(split[0],
						Gateway.getAddress(address).accounts.get(0).address,
						split[1]);
			}
		} else {
			throw new RuntimeException(res);
		}
		return currency;
	}

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
}
