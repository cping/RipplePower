package org.ripple.power.txns;

import java.io.UnsupportedEncodingException;

import org.ripple.power.CoinUtils;
import org.ripple.power.RippleObject;
import org.ripple.power.RippleSeedAddress;
import org.ripple.power.RippleSchemas.BinaryFormatField;
import org.ripple.power.RippleSchemas.TransactionTypes;
import org.ripple.power.blockchain.RippleMemoEncode;
import org.ripple.power.blockchain.RippleMemoEncodes;
import org.ripple.power.config.LSystem;
import org.ripple.power.ui.RPClient;
import org.ripple.power.utils.Base64Coder;
import org.ripple.power.utils.MathUtils;
import org.ripple.power.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ripple.client.enums.Command;
import com.ripple.client.requests.Request;
import com.ripple.client.responses.Response;
import com.ripple.core.coretypes.Amount;
import com.ripple.core.coretypes.STArray;
import com.ripple.core.coretypes.STObject;
import com.ripple.core.coretypes.hash.Hash256;
import com.ripple.core.fields.Field;
import com.ripple.core.serialized.enums.TransactionType;
import com.ripple.core.types.known.tx.Transaction;

/**
 * 		Payment.sendXRP("seed", "address", String.valueOf(0.1), "0.001", new Rollback() {
			
			@Override
			public void success(JSONObject res) {
				System.out.println(res);
				
			}
			
			@Override
			public void error(JSONObject res) {
				System.out.println(res);
				
			}
		});
 */
public class Payment {

	public static long randomTag() {
		return MathUtils.randomLong(10000000, 999999999);
	}
	
	public static void send(final RippleSeedAddress seed, final String amount, final String dstAddress,
			final RippleMemoEncode memo, final String fee, final Rollback back) {
		RippleMemoEncodes memos = new RippleMemoEncodes();
		memos.add(memo);
		send(seed, amount, dstAddress, memos, fee, back);
	}

	public static void send(final RippleSeedAddress seed, final String amount, final String dstAddress,
			final RippleMemoEncodes list, final String fee, final Rollback back) {
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
				txn.putTranslated(Field.DestinationTag, randomTag());
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

	public static void send(final RippleSeedAddress seed, final Amount amount, final Amount sendMax,
			final Amount deliveredAmount, final String dstAddress, final long flags, final long dt,
			final String invoiceID, final RippleMemoEncode memo, final String fee, final Rollback back) {
		RippleMemoEncodes memos = null;
		if (memo != null) {
			memos = new RippleMemoEncodes();
			memos.add(memo);
		}
		send(seed, amount, sendMax, deliveredAmount, dstAddress, flags, dt, invoiceID, memos, fee, back);
	}

	public static void send(final RippleSeedAddress seed, final Amount amount, final Amount sendMax,
			final Amount deliveredAmount, final String dstAddress, final long flags, final long dt,
			final String invoiceID, final RippleMemoEncodes list, final String fee, final Rollback back) {
		final String address = seed.getPublicRippleAddress().toString();
		AccountFind find = new AccountFind();

		find.info(address, new Rollback() {
			@Override
			public void success(JSONObject message) {
				long sequence = TransactionUtils.getSequence(message);
				Transaction txn = new Transaction(TransactionType.Payment);
				txn.putTranslated(Field.Account, seed.getPublicKey());
				txn.putTranslated(Field.Flags, flags);
				txn.putTranslated(Field.Destination, dstAddress);
				txn.putTranslated(Field.Amount, amount);
				if (sendMax != null) {
					txn.putTranslated(Field.SendMax, sendMax);
				}
				txn.putTranslated(Field.DestinationTag, dt);
				if (StringUtils.isEmpty(invoiceID)) {
					byte[] id = null;
					if (!AccountFind.is256hash(invoiceID)) {
						try {
							id = CoinUtils.toHex(invoiceID.getBytes(LSystem.encoding)).getBytes(LSystem.encoding);
						} catch (UnsupportedEncodingException e) {
							id = CoinUtils.toHex(invoiceID.getBytes()).getBytes();
						}
					} else {
						id = CoinUtils.fromHex(invoiceID);
					}
					txn.putTranslated(Field.InvoiceID, new Hash256(id));
				}
				if (list != null) {
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
				}
				if (deliveredAmount != null) {
					STObject delivered = new STObject();
					delivered.putTranslated(Field.DeliveredAmount, deliveredAmount);
					delivered.putTranslated(Field.TransactionIndex, 0);
					txn.put(Field.TransactionMetaData, delivered);
				}
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

	public static void sendMessage(final RippleSeedAddress seed, final String dstAddress, final String memotype,
			final String memodata, final String memoformat, final String fee, final Rollback back) {
		try {
			byte[] typeByte = memotype.getBytes(LSystem.encoding);
			byte[] dataByte = memodata.getBytes(LSystem.encoding);
			byte[] formatByte = memoformat.getBytes(LSystem.encoding);
			send(seed, dstAddress, LSystem.getMinSend(), fee, typeByte, dataByte, formatByte, back);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void send(final RippleSeedAddress seed, final String dstAddress, final String amount,
			final String fee, final byte[] memotype, final byte[] memodata, final byte[] memoformat,
			final Rollback back) {
		String typeStr = CoinUtils.toHex(Base64Coder.encode(memotype));
		String typeData = CoinUtils.toHex(Base64Coder.encode(memodata));
		String typeFormat = CoinUtils.toHex(Base64Coder.encode(memoformat));
		send(seed, dstAddress, amount, fee, typeStr, typeData, typeFormat, back);
	}

	public static void send(final RippleSeedAddress seed, final String dstAddress, final String amount,
			final String fee, final String memotype, final String memodata, final String memoformat,
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
				txn.putTranslated(Field.Amount, amount);
				txn.putTranslated(Field.DestinationTag, randomTag());
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

	public static void sendXRP(final String seed, final String dstAddress, final String amount, final String fee,
			final Rollback back) {
		Payment.sendXRP(new RippleSeedAddress(seed), dstAddress, amount, fee, back);
	}

	public static void sendXRP(final RippleSeedAddress seed, final String dstAddress, final String amount,
			final String fee, final Rollback back) {
		sendXRP(seed, dstAddress, randomTag(), amount, fee, back);
	}

	public static void sendXRP(final RippleSeedAddress seed, final String dstAddress, final long destinationTag,
			final String amount, final String fee, final Rollback back) {
		final String address = seed.getPublicKey();
		AccountFind find = new AccountFind();
		find.info(address, new Rollback() {
			@Override
			public void success(JSONObject message) {
				try {
					long sequence = TransactionUtils.getSequence(message);
					RippleObject item = new RippleObject();
					item.putField(BinaryFormatField.TransactionType, (int) TransactionTypes.PAYMENT.byteValue);
					item.putField(BinaryFormatField.Account, seed.getPublicRippleAddress());
					item.putField(BinaryFormatField.Amount, CurrencyUtils.getValueToRipple(amount));
					item.putField(BinaryFormatField.Sequence, sequence);
					item.putField(BinaryFormatField.Destination, dstAddress);
					item.putField(BinaryFormatField.DestinationTag, destinationTag);
					item.putField(BinaryFormatField.Fee, CurrencyUtils.getValueToRipple(fee));
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

	public static void sendXRP(final RippleSeedAddress seed, final String dstAddress, final long destinationTag,
			final String memoTag, final String amount, final String fee, final Rollback back) {
		final String address = seed.getPublicKey();
		AccountFind find = new AccountFind();
		find.info(address, new Rollback() {
			@Override
			public void success(JSONObject message) {
				try {
					long sequence = TransactionUtils.getSequence(message);

					RippleObject item = new RippleObject();
					item.putField(BinaryFormatField.TransactionType, (int) TransactionTypes.PAYMENT.byteValue);
					item.putField(BinaryFormatField.Account, seed.getPublicRippleAddress());
					item.putField(BinaryFormatField.Amount, CurrencyUtils.getValueToRipple(amount));
					item.putField(BinaryFormatField.Sequence, sequence);
					item.putField(BinaryFormatField.Destination, dstAddress);
					item.putField(BinaryFormatField.DestinationTag, destinationTag);
					if (memoTag.length() > 0) {
						RippleObject memos = new RippleObject();
						RippleObject obj = new RippleObject();
						obj.putField(BinaryFormatField.MemoType, "message");
						obj.putField(BinaryFormatField.MemoFormat, "text");
						obj.putField(BinaryFormatField.MemoData, memoTag);
						memos.putField(BinaryFormatField.Memo, obj);
						
						item.putField(BinaryFormatField.Memos, memos);
					}
					item.putField(BinaryFormatField.Fee, CurrencyUtils.getValueToRipple(fee));
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

	public static void sendXRPAndInvoiceIDAndTag(final String seed, final String dstAddress, final String amount,
			final byte[] hash, final long tag, final String fee, final Rollback back) {
		Payment.sendXRPAndInvoiceIDAndTag(new RippleSeedAddress(seed), dstAddress, amount, hash, tag, fee, back);
	}

	public static void sendXRPAndInvoiceIDAndTag(final RippleSeedAddress seed, final String dstAddress,
			final String amount, final byte[] hash, final long tag, final String fee, final Rollback back) {
		final String address = seed.getPublicKey();
		AccountFind find = new AccountFind();
		find.info(address, new Rollback() {
			@Override
			public void success(JSONObject message) {
				try {
					long sequence = TransactionUtils.getSequence(message);
					RippleObject item = new RippleObject();
					item.putField(BinaryFormatField.TransactionType, (int) TransactionTypes.PAYMENT.byteValue);
					item.putField(BinaryFormatField.Account, seed.getPublicRippleAddress());
					item.putField(BinaryFormatField.Amount, CurrencyUtils.getValueToRipple(amount));
					item.putField(BinaryFormatField.InvoiceID, hash);
					item.putField(BinaryFormatField.Sequence, sequence);
					item.putField(BinaryFormatField.Destination, dstAddress);
					item.putField(BinaryFormatField.DestinationTag, tag);
					item.putField(BinaryFormatField.Fee, CurrencyUtils.getValueToRipple(fee));
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

	public static void send(final String seed, final String dstAddress, final IssuedCurrency amount, final String fee,
			final Rollback back) {
		Payment.send(new RippleSeedAddress(seed), dstAddress, amount, fee, back);
	}

	public static void send(final RippleSeedAddress seed, final String dstAddress, final IssuedCurrency amount,
			final String fee, final Rollback back) {
		send(seed, dstAddress, amount, fee, randomTag(), back);
	}

	public static void send(final RippleSeedAddress seed, final String dstAddress, final IssuedCurrency amount,
			final String fee, final long tagNumber, final Rollback back) {

		final String address = seed.getPublicRippleAddress().toString();
		AccountFind find = new AccountFind();
		find.info(address, new Rollback() {
			@Override
			public void success(JSONObject message) {
				try {
					long sequence = TransactionUtils.getSequence(message);
					RippleObject item = new RippleObject();
					item.putField(BinaryFormatField.TransactionType, (int) TransactionTypes.PAYMENT.byteValue);
					item.putField(BinaryFormatField.Account, seed.getPublicRippleAddress());
					item.putField(BinaryFormatField.Destination, dstAddress);
					item.putField(BinaryFormatField.Amount, amount);
					item.putField(BinaryFormatField.Sequence, sequence);
					item.putField(BinaryFormatField.DestinationTag, tagNumber);
					item.putField(BinaryFormatField.Fee, CurrencyUtils.getValueToRipple(fee));
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
			_currencyName = LSystem.nativeCurrency.toUpperCase();
		}
		if (issuer != null) {
			_issuer = issuer;
		}
	}

	public static void sendTxJson(String srcAddress, String seed, String dstAddress, String amount, String fee,
			final Rollback back) {
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

	public static void sendTxMemosJson(String srcAddress, String seed, String dstAddress, String amount, String fee,
			String memotype, String memodata, final Rollback back) {
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
