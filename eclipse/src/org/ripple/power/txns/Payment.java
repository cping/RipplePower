package org.ripple.power.txns;

import org.ripple.power.ui.RPClient;
import org.ripple.power.utils.MathUtils;

import org.address.ripple.RippleObject;
import org.address.ripple.RippleSeedAddress;
import org.address.ripple.RippleSchemas.BinaryFormatField;
import org.address.ripple.RippleSchemas.TransactionTypes;
import org.json.JSONObject;

import com.ripple.client.enums.Command;
import com.ripple.client.requests.Request;
import com.ripple.client.responses.Response;

public class Payment {

	public static void sendXRP(final String seed, final String dstAddress,
			final String amount, final String fee, final Rollback back) {
		Payment.sendXRP(new RippleSeedAddress(seed), dstAddress, amount, fee,
				back);
	}

	public static void sendXRP(final RippleSeedAddress seed,
			final String dstAddress, final String amount, final String fee,
			final Rollback back) {
		final String address = seed.getPublicKey();
		AccountFind find = new AccountFind();
		find.info(address, new Rollback() {
			@Override
			public void success(JSONObject message) {
				try {
					long sequence = TransactionUtils.getSequence(message);
					RippleObject item = new RippleObject();
					item.putField(BinaryFormatField.TransactionType,
							(int) TransactionTypes.PAYMENT.byteValue);
					item.putField(BinaryFormatField.Account,
							seed.getPublicRippleAddress());
					item.putField(BinaryFormatField.Amount,
							CurrencyUtils.getValueToRipple(amount));
					item.putField(BinaryFormatField.Sequence, sequence);
					item.putField(BinaryFormatField.Destination, dstAddress);
					item.putField(BinaryFormatField.DestinationTag,
							MathUtils.randomLong(1, 999999999));
					item.putField(BinaryFormatField.Fee,
							CurrencyUtils.getValueToRipple(fee));
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

	public static void sendXRPAndInvoiceIDAndTag(final String seed,
			final String dstAddress, final String amount, final byte[] hash,
			final long tag, final String fee, final Rollback back) {
		Payment.sendXRPAndInvoiceIDAndTag(new RippleSeedAddress(seed),
				dstAddress, amount, hash, tag, fee, back);
	}

	public static void sendXRPAndInvoiceIDAndTag(final RippleSeedAddress seed,
			final String dstAddress, final String amount, final byte[] hash,
			final long tag, final String fee, final Rollback back) {
		final String address = seed.getPublicKey();
		AccountFind find = new AccountFind();
		find.info(address, new Rollback() {
			@Override
			public void success(JSONObject message) {
				try {
					long sequence = TransactionUtils.getSequence(message);
					RippleObject item = new RippleObject();
					item.putField(BinaryFormatField.TransactionType,
							(int) TransactionTypes.PAYMENT.byteValue);
					item.putField(BinaryFormatField.Account,
							seed.getPublicRippleAddress());
					item.putField(BinaryFormatField.Amount,
							CurrencyUtils.getValueToRipple(amount));
					item.putField(BinaryFormatField.InvoiceID, hash);
					item.putField(BinaryFormatField.Sequence, sequence);
					item.putField(BinaryFormatField.Destination, dstAddress);
					item.putField(BinaryFormatField.DestinationTag, tag);
					item.putField(BinaryFormatField.Fee,
							CurrencyUtils.getValueToRipple(fee));
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

	public static void send(final String seed, final String dstAddress,
			final IssuedCurrency amount, final String fee, final Rollback back) {
		Payment.send(new RippleSeedAddress(seed), dstAddress, amount, fee, back);
	}

	public static void send(final RippleSeedAddress seed,
			final String dstAddress, final IssuedCurrency amount,
			final String fee, final Rollback back) {

		final String address = seed.getPublicRippleAddress().toString();
		AccountFind find = new AccountFind();
		find.info(address, new Rollback() {
			@Override
			public void success(JSONObject message) {
				try {
					long sequence = TransactionUtils.getSequence(message);
					RippleObject item = new RippleObject();
					item.putField(BinaryFormatField.TransactionType,
							(int) TransactionTypes.PAYMENT.byteValue);
					item.putField(BinaryFormatField.Account,
							seed.getPublicRippleAddress());
					item.putField(BinaryFormatField.Destination, dstAddress);
					item.putField(BinaryFormatField.Amount, amount);
					item.putField(BinaryFormatField.Sequence, sequence);
					item.putField(BinaryFormatField.DestinationTag,
							MathUtils.randomLong(1, 999999999));
					item.putField(BinaryFormatField.Fee,
							CurrencyUtils.getValueToRipple(fee));
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

	private String _currencyName;

	private String _issuer;

	public Payment() {
		this(null, null);
	}

	public Payment(String cName, String issuer) {
		if (cName != null) {
			_currencyName = _currencyName.toUpperCase();
		} else {
			_currencyName = "XRP";
		}
		if (issuer != null) {
			_issuer = issuer;
		}
	}

	public void sendTxJson(String srcAddress, String seed, String dstAddress,
			String amount, String fee, final Rollback back) {
		RPClient client = RPClient.ripple();
		if (client != null) {
			Request req = client.newRequest(Command.submit);
			JSONObject tx = new JSONObject();
			tx.put("TransactionType", "Payment");
			tx.put("Account", srcAddress);
			tx.put("Amount", CurrencyUtils.getValueToRipple(amount));
			tx.put("Destination", dstAddress);
			tx.put("Fee", CurrencyUtils.getValueToRipple(fee));
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

	public String getCurrencyName() {
		return _currencyName;
	}

	public String getIssuer() {
		return _issuer;
	}

}
