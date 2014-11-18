package org.ripple.power.txns;

import org.ripple.power.blockchain.RippleMemoEncode;
import org.ripple.power.blockchain.RippleMemoEncodes;
import org.ripple.power.config.LSystem;
import org.ripple.power.ui.RPClient;
import org.ripple.power.utils.Base64Coder;
import org.ripple.power.utils.MathUtils;

import org.address.ripple.RippleObject;
import org.address.ripple.RippleSeedAddress;
import org.address.ripple.RippleSchemas.BinaryFormatField;
import org.address.ripple.RippleSchemas.TransactionTypes;
import org.address.utils.CoinUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.ripple.client.enums.Command;
import com.ripple.client.requests.Request;
import com.ripple.client.responses.Response;
import com.ripple.core.coretypes.STArray;
import com.ripple.core.coretypes.STObject;
import com.ripple.core.fields.Field;
import com.ripple.core.serialized.enums.TransactionType;
import com.ripple.core.types.known.tx.Transaction;

public class Payment {

	public static void send(final RippleSeedAddress seed,
			final String dstAddress, final RippleMemoEncode memo, final String fee,
			final Rollback back) {
		RippleMemoEncodes memos = new RippleMemoEncodes();
		memos.add(memo);
		send(seed, dstAddress, memos, fee, back);
	}

	public static void send(final RippleSeedAddress seed,
			final String dstAddress, final RippleMemoEncodes list, final String fee,
			final Rollback back) {
		final String address = seed.getPublicRippleAddress().toString();
		AccountFind find = new AccountFind();
		find.info(address, new Rollback() {
			@Override
			public void success(JSONObject message) {
				long sequence = TransactionUtils.getSequence(message);
				Transaction txn = new Transaction(TransactionType.Payment);
				txn.putTranslated(Field.Account, seed.getPublicKey());
				txn.putTranslated(Field.Destination, dstAddress);
				txn.putTranslated(Field.Amount, LSystem.MIN_AMOUNT);
				txn.putTranslated(Field.DestinationTag,
						MathUtils.randomLong(1, 999999999));
				STArray arrays = new STArray();
				for (int i = 0; i < list.size(); i++) {
					RippleMemoEncode rpmemo = list.get(i);
					STObject obj = new STObject();
					obj.putTranslated(Field.MemoType, rpmemo.getType());
					obj.putTranslated(Field.MemoData, rpmemo.getData());
					if (rpmemo.getFormat() != null) {
						obj.putTranslated(Field.MemoFormat, rpmemo.getFormat());
					}
					STObject memo = new STObject();
					memo.put(Field.Memo, obj);
					arrays.add(memo);
				}
				txn.putTranslated(Field.Memos, arrays);
				try {
					TransactionUtils.submitBlob(seed, txn, fee, sequence, back);
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

	public static void sendMessage(final RippleSeedAddress seed,
			final String dstAddress, final String memotype,
			final String memodata, final String memoformat, final String fee,
			final Rollback back) {
		try {
			byte[] typeByte = memotype.getBytes(LSystem.encoding);
			byte[] dataByte = memodata.getBytes(LSystem.encoding);
			byte[] formatByte = memoformat.getBytes(LSystem.encoding);
			send(seed, dstAddress, LSystem.MIN_AMOUNT, fee, typeByte, dataByte,
					formatByte, back);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void send(final RippleSeedAddress seed,
			final String dstAddress, final String amount, final String fee,
			final byte[] memotype, final byte[] memodata,
			final byte[] memoformat, final Rollback back) {
		String typeStr = CoinUtils.toHex(Base64Coder.encode(memotype));
		String typeData = CoinUtils.toHex(Base64Coder.encode(memodata));
		String typeFormat = CoinUtils.toHex(Base64Coder.encode(memoformat));
		send(seed, dstAddress, amount, fee, typeStr, typeData, typeFormat, back);
	}

	public static void send(final RippleSeedAddress seed,
			final String dstAddress, final String amount, final String fee,
			final String memotype, final String memodata,
			final String memoformat, final Rollback back) {
		final String address = seed.getPublicRippleAddress().toString();
		AccountFind find = new AccountFind();
		find.info(address, new Rollback() {
			@Override
			public void success(JSONObject message) {
				long sequence = TransactionUtils.getSequence(message);
				Transaction txn = new Transaction(TransactionType.Payment);
				txn.putTranslated(Field.Account, seed.getPublicKey());
				txn.putTranslated(Field.Destination, dstAddress);
				txn.putTranslated(Field.Amount, amount);
				txn.putTranslated(Field.DestinationTag,
						MathUtils.randomLong(1, 999999999));
				STObject obj = new STObject();
				obj.putTranslated(Field.MemoType, memotype);
				obj.putTranslated(Field.MemoData, memodata);
				if (memoformat != null) {
					obj.putTranslated(Field.MemoFormat, memoformat);
				}
				STObject memo = new STObject();
				memo.put(Field.Memo, obj);
				STArray arrays = new STArray();
				arrays.add(memo);
				txn.putTranslated(Field.Memos, arrays);
				try {
					TransactionUtils.submitBlob(seed, txn, fee, sequence, back);
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

	public static void sendTxJson(String srcAddress, String seed,
			String dstAddress, String amount, String fee, final Rollback back) {
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

	public static void sendTxMemosJson(String srcAddress, String seed,
			String dstAddress, String amount, String fee, String memotype,
			String memodata, final Rollback back) {
		RPClient client = RPClient.ripple();
		if (client != null) {
			Request req = client.newRequest(Command.submit);
			JSONObject tx = new JSONObject();
			tx.put("TransactionType", "Payment");
			tx.put("Account", srcAddress);
			tx.put("Amount", CurrencyUtils.getValueToRipple(amount));
			tx.put("Destination", dstAddress);
			tx.put("Fee", CurrencyUtils.getValueToRipple(fee));

			JSONObject obj = new JSONObject();
			obj.put("MemoType", memotype);
			obj.put("MemoData", memodata);

			JSONObject memo = new JSONObject();
			memo.put("Memo", obj);

			JSONArray memos = new JSONArray();
			memos.put(memo);

			req.json("Memos", memos);
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
