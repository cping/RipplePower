package org.ripple.power.txns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ripple.power.ui.RPClient;

import com.google.common.base.Strings;
import com.ripple.client.enums.Command;
import com.ripple.client.requests.Request;
import com.ripple.client.responses.Response;
import com.ripple.core.coretypes.RippleDate;

public class AccountFind {

	public JSONObject _balanceXRP;

	public JSONObject _balanceIOU;

	public JSONObject _offer;

	public JSONObject _subscribe;

	public final static boolean isRippleAddress(String address) {
		if (Strings.isNullOrEmpty(address)) {
			return false;
		}
		if (!address.startsWith("r")) {
			return false;
		}
		String reg = "^r[1-9A-HJ-NP-Za-km-z]{25,33}$";
		return Pattern.matches(reg, address);
	}
	
	public final static boolean isRippleSecret(String address) {
		if (Strings.isNullOrEmpty(address)) {
			return false;
		}
		if (!address.startsWith("s")) {
			return false;
		}
		String reg = "^s[1-9A-HJ-NP-Za-km-z]{25,31}$";
		return Pattern.matches(reg, address);
	}
	
	public final static boolean is256hash(String hash) {
		if (Strings.isNullOrEmpty(hash)) {
			return false;
		}
		String reg = "^$|^[A-Fa-f0-9]{64}$";
		return Pattern.matches(reg, hash);
	}

	public final static boolean isRippleResult(String result) {
		if (Strings.isNullOrEmpty(result)) {
			return false;
		}
		String reg = "te[cfjlms][A-Za-z_]+";
		return Pattern.matches(reg, result);
	}

	private final static JSONObject getJsonObject(JSONObject obj, String key) {
		if (obj.has(key)) {
			return obj.getJSONObject(key);
		}
		return null;
	}

	private final static Object getObject(JSONObject obj, String key) {
		if (obj.has(key)) {
			return obj.get(key);
		}
		return null;
	}

	private final static String getStringObject(JSONObject obj, String key) {
		if (obj.has(key)) {
			return obj.getString(key);
		}
		return null;
	}

	private final static int getInt(JSONObject obj, String key) {
		if (obj.has(key)) {
			return obj.getInt(key);
		}
		return 0;
	}

	private final static long getLong(JSONObject obj, String key) {
		if (obj.has(key)) {
			return obj.getLong(key);
		}
		return 0;
	}

	private final static JSONArray getArray(JSONObject obj, String key) {
		if (obj.has(key)) {
			return obj.getJSONArray(key);
		}
		return null;
	}

	public static RippleDate getDateTime(int date) {
		return RippleDate.fromSecondsSinceRippleEpoch(date);
	}

	public static int inCredits(ArrayList<IssuedCurrency> issues,
			IssuedCurrency currency) {
		for (int i = 0; i < issues.size(); i++) {
			IssuedCurrency cur = issues.get(i);
			if (cur.equals(currency)) {
				return i;
			}
		}
		return -1;
	}

	public static ArrayList<IssuedCurrency> getTrusts(final String address,
			final Updateable update) {
		final ArrayList<IssuedCurrency> lines = new ArrayList<IssuedCurrency>(
				10);
		final AccountInfo info = new AccountInfo();
		final AccountFind find = new AccountFind();
		find.processLines(address, info, new Updateable() {

			@Override
			public void action(Object o) {
				for (int i = 0; i < info.lines.size(); i++) {
					lines.add(info.lines.get(i).get());
				}
				for (int i = 0; i < info.zero_lines.size(); i++) {
					lines.add(info.zero_lines.get(i).get());
				}
				if (update != null) {
					update.action(lines);
				}

			}
		});
		return lines;
	}

	public AccountInfo processTx(final String address,
			final AccountInfo accountinfo, final Updateable update) {
		return processTx(address, -1, 200, accountinfo, update);
	}

	public AccountInfo processTx(final String address, final long txPreLgrSeq,
			final int max, final AccountInfo accountinfo,
			final Updateable update) {
		final ArrayList<IssuedCurrency> issues = new ArrayList<>(10);
		Updateable updateable = new Updateable() {

			@Override
			public void action(Object res) {

				tx(address, txPreLgrSeq == -1 ? accountinfo.txPreLgrSeq : -1,
						max, new Rollback() {

							@Override
							public void success(JSONObject res) {

								JSONObject result = getJsonObject(res, "result");
								if (result != null) {
									if (result.has("marker")) {
										JSONObject marker = result
												.getJSONObject("marker");
										accountinfo.marker = accountinfo.marker == getLong(
												marker, "ledger") ? accountinfo.marker - 1
												: getLong(marker, "ledger");
										AccountInfo newInfo = new AccountInfo();
										processTx(address, accountinfo.marker,
												max, newInfo, null);
										accountinfo.accountlinks.add(newInfo);
									}
									if (result.has("transactions")) {
										JSONArray arrays = getArray(result,
												"transactions");

										for (int i = 0; i < arrays.length(); i++) {

											TransactionTx transactionTx = new TransactionTx();

											JSONObject transaction = arrays
													.getJSONObject(i);
											JSONObject tx = getJsonObject(
													transaction, "tx");
											JSONObject meta = getJsonObject(
													transaction, "meta");
											String type = getStringObject(tx,
													"TransactionType");

											if (meta != null) {
												transactionTx.meda = meta
														.toString();
											}

											transactionTx.account = getStringObject(
													tx, "Account");

											int date = getInt(tx, "date");

											transactionTx.date = getDateTime(
													date).getTimeString();

											String fee = CurrencyUtils.getRippleToValue(String
													.valueOf(getLong(tx, "Fee")));

											transactionTx.fee = fee;
											transactionTx.hash = getStringObject(
													tx, "hash");
											transactionTx.sequence = getLong(
													tx, "Sequence");
											transactionTx.offersSequence = getLong(
													tx, "OfferSequence");
											transactionTx.inLedger = getLong(
													tx, "inLedger");
											transactionTx.ledgerIndex = getLong(
													tx, "ledger_index");
											transactionTx.flags = getLong(tx,
													"Flags");
											transactionTx.clazz = type;

											switch (type) {
											case "Payment":

												transactionTx.destinationTag = getLong(
														tx, "DestinationTag");
												transactionTx.invoiceID = getStringObject(
														tx, "InvoiceID");

												IssuedCurrency currency = null;
												String counterparty = null;
												if (meta != null
														&& meta.has("DeliveredAmount")) {
													currency = CurrencyUtils.getAmount(getObject(
															meta,
															"DeliveredAmount"));
												} else {
													currency = CurrencyUtils.getAmount(getObject(
															tx, "Amount"));
												}
												transactionTx.currency = currency;
												String flagType;
												if (address
														.equals(getStringObject(
																tx, "Account"))) {
													if (address
															.equals(getStringObject(
																	tx,
																	"Destination"))) {
														flagType = "Exchange";
													} else {
														flagType = "Send";
														counterparty = getStringObject(
																tx,
																"Destination");
														int index = inCredits(
																issues,
																currency);
														if (index >= 0) {

														} else {
															issues.add(currency);
														}
													}
												} else if (address
														.equals(getStringObject(
																tx,
																"Destination"))) {
													flagType = "Receive";
													counterparty = getStringObject(
															tx, "Account");
												} else {
													flagType = "Convert";
												}
												transactionTx.mode = flagType;
												transactionTx.counterparty = counterparty;
												break;
											case "TrustSet":
												Object limitAmount = getObject(
														tx, "LimitAmount");
												if (limitAmount != null) {
													transactionTx.currency = CurrencyUtils.getAmount(limitAmount);
													transactionTx.trusted = transactionTx.currency.issuer
															.toString();
												}
												break;
											case "OfferCreate":
												transactionTx.get = CurrencyUtils.getAmount(getObject(
														tx, "TakerGets"));
												transactionTx.pay = CurrencyUtils.getAmount(getObject(
														tx, "TakerPays"));
												break;
											case "OfferCancel":
												JSONArray affectedNodes = getArray(
														meta, "AffectedNodes");
												for (int n = 0; n < affectedNodes
														.length(); n++) {
													JSONObject obj = affectedNodes
															.getJSONObject(n);
													if (obj.has("DeletedNode")) {
														JSONObject deleted = obj
																.getJSONObject("DeletedNode");
														String ledgerEntryType = getStringObject(
																deleted,
																"LedgerEntryType");
														if ("Offer"
																.equals(ledgerEntryType)) {
															JSONObject ff = getJsonObject(
																	deleted,
																	"FinalFields");
															String ffactount = getStringObject(
																	ff,
																	"Account");
															if (ffactount
																	.equals(transactionTx.account)) {
																transactionTx.get = CurrencyUtils.getAmount(getObject(
																		ff,
																		"TakerGets"));
																transactionTx.pay = CurrencyUtils.getAmount(getObject(
																		ff,
																		"TakerPays"));
															}
														}
													}

												}
												break;
											}
											accountinfo.transactions
													.add(transactionTx);

										}
									}

								}

								accountinfo.count++;
								if (update != null) {
									update.action(res);
								}

							}

							@Override
							public void error(JSONObject res) {
								accountinfo.error = true;
								if (update != null) {
									update.action(res);
								}
							}
						});
			}
		};
		return processInfo(address, accountinfo, updateable);
	}

	public AccountInfo processLines(final String address,
			final AccountInfo accountinfo, final Updateable update) {
		lines(address, new Rollback() {

			@Override
			public void success(JSONObject res) {
				JSONObject result = getJsonObject(res, "result");
				if (result != null) {
					JSONArray arrays = getArray(result, "lines");
					if (arrays != null) {

						int cntTrust = 0;
						HashMap<String, Double> debt = new HashMap<String, Double>(
								10);
						HashMap<String, Long> debtCount = new HashMap<String, Long>(
								10);
						HashMap<String, Integer> trustCount = new HashMap<String, Integer>(
								10);

						for (int i = 0; i < arrays.length(); i++) {
							JSONObject node = arrays.getJSONObject(i);
							String limit = getStringObject(node, "limit");
							long quality_out = getLong(node, "quality_out");
							long quality_in = getLong(node, "quality_in");
							String account = getStringObject(node, "account");
							String currency = getStringObject(node, "currency");
							String amount = getStringObject(node, "balance");
							String limit_peer = getStringObject(node,
									"limit_peer");

							Double number = Double.valueOf(amount);

							Double limit_peer_number = Double
									.valueOf(limit_peer);

							// 获得的IOU
							if (number > 0) {
								AccountLine line = new AccountLine();
								line.issuer = account;
								line.currency = currency;
								line.amount = amount;
								line.limit = limit;
								line.quality_in = quality_in;
								line.quality_out = quality_out;
								line.limit_peer = limit_peer;
								accountinfo.lines.add(line);
								// 发出的IOU
							} else if (number < 0) {
								if (currency != null) {
									cntTrust++;
									double n = debt.get(currency) == null ? 0
											: debt.get(currency);
									if (debt.containsKey(currency)) {
										debt.put(currency, n + number);
										debtCount.put(currency,
												debtCount.get(currency) + 1l);
									} else {
										debt.put(currency, n + number);
										debtCount.put(currency, 1l);
									}
								}
								// 单纯信任
							} else if (number == 0) {
								AccountLine line = new AccountLine();
								line.issuer = account;
								line.currency = currency;
								line.amount = amount;
								line.limit = limit;
								line.quality_in = quality_in;
								line.quality_out = quality_out;
								accountinfo.zero_lines.add(line);
							} else if (limit_peer_number > 0) {
								if (trustCount.containsKey(currency)) {
									trustCount.put(currency,
											trustCount.get(currency) + 1);
								} else {
									trustCount.put(currency, 1);
								}
							}
						}

						// for end
						for (String cur : debt.keySet()) {
							if (!trustCount.containsKey(cur)) {
								trustCount.put(cur, 0);
							}
						}

						accountinfo.cntTrust = cntTrust;
						accountinfo.debt = debt;
						accountinfo.debtCount = debtCount;
						accountinfo.trustCount = trustCount;
					}
				}
				accountinfo.count++;
				if (update != null) {
					update.action(res);
				}
			}

			@Override
			public void error(JSONObject res) {

				accountinfo.error = true;
				if (update != null) {
					update.action(res);
				}
			}
		});

		return accountinfo;
	}

	public AccountInfo processInfo(final String address,
			final AccountInfo accountinfo, final Updateable update) {

		info(address, new Rollback() {

			@Override
			public void success(JSONObject res) {
				JSONObject result = getJsonObject(res, "result");
				if (result != null) {
					JSONObject account_data = getJsonObject(result,
							"account_data");
					if (account_data != null) {
						String balance = getStringObject(account_data,
								"Balance");
						if (balance != null) {
							accountinfo.balance = CurrencyUtils
									.getRippleToValue(balance);
						}
						accountinfo.faceURL = getStringObject(account_data,
								"urlgravatar");
						accountinfo.sequence = getInt(account_data, "Sequence");
						accountinfo.domain = getStringObject(account_data,
								"Domain");

						accountinfo.fee = String.valueOf(getLong(account_data,
								"TransferRate"));
						if (accountinfo.fee != null) {
							accountinfo.fee = CurrencyUtils
									.getFee(accountinfo.fee);
						}

						accountinfo.txPreLgrSeq = getInt(account_data,
								"PreviousTxnLgrSeq");

					}
				}
				accountinfo.count++;
				if (update != null) {
					update.action(res);
				}
			}

			@Override
			public void error(JSONObject res) {

				accountinfo.error = true;
				if (update != null) {
					update.action(res);
				}
			}
		});

		return accountinfo;
	}

	public AccountInfo processOfffer(final String address,
			final AccountInfo accountinfo, final Updateable update) {
		offer(address, new Rollback() {

			@Override
			public void success(JSONObject res) {
				JSONObject result = getJsonObject(res, "result");
				if (result != null) {
					JSONArray offers = getArray(result, "offers");

					if (offers.length() > 0) {
						for (int i = 0; i < offers.length(); i++) {
							JSONObject o = offers.getJSONObject(i);

							long seq = getLong(o, "seq");

							Object taker_gets = getObject(o, "taker_gets");
							Object taker_pays = getObject(o, "taker_pays");

							BookOffer offer = new BookOffer(
									CurrencyUtils.getAmount(taker_gets),
									CurrencyUtils.getAmount(taker_pays), seq);

							accountinfo.bookOffers.add(offer);

						}
					}
				}
				accountinfo.count++;
				if (update != null) {
					update.action(res);
				}
			}

			@Override
			public void error(JSONObject res) {

				accountinfo.error = true;
				if (update != null) {
					update.action(res);
				}
			}
		});

		return accountinfo;

	}

	public AccountInfo load(String address, Updateable update) {

		final AccountInfo accountinfo = new AccountInfo();

		processInfo(address, accountinfo, update);
		processLines(address, accountinfo, update);
		processOfffer(address, accountinfo, update);

		return accountinfo;
	}

	public AccountInfo load(String address, AccountInfo accountinfo,
			Updateable update) {

		processInfo(address, accountinfo, update);
		processLines(address, accountinfo, update);
		processOfffer(address, accountinfo, update);

		return accountinfo;
	}


	public void subscribe(String[] srcAddress, final Rollback back) {
		RPClient client = RPClient.ripple();
		if (client != null) {
			Request req = client.newRequest(Command.subscribe);
			JSONArray array = new JSONArray();
			array.put(srcAddress);
			req.json("Accounts", array);
			JSONArray item = new JSONArray();
			item.put("server");
			item.put("ledger");
			item.put("transactions");
			req.json("streams", item);
			req.once(Request.OnSuccess.class, new Request.OnSuccess() {
				@Override
				public void called(Response response) {
					_subscribe = response.message;
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

	public void offer(String srcAddress, final Rollback back) {
		RPClient client = RPClient.ripple();
		if (client != null) {
			Request req = client.newRequest(Command.account_offers);
			req.json("account", srcAddress);
			req.once(Request.OnSuccess.class, new Request.OnSuccess() {
				@Override
				public void called(Response response) {
					_offer = response.message;
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

	public void lines(String srcAddress, final Rollback back) {
		RPClient client = RPClient.ripple();
		if (client != null) {
			Request req = client.newRequest(Command.account_lines);
			req.json("account", srcAddress);
			req.once(Request.OnSuccess.class, new Request.OnSuccess() {
				@Override
				public void called(Response response) {
					_balanceIOU = response.message;
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

	public void info(String srcAddress, final Rollback back) {
		RPClient client = RPClient.ripple();
		if (client != null) {
			Request req = client.newRequest(Command.account_info);
			req.json("account", srcAddress);
			req.once(Request.OnSuccess.class, new Request.OnSuccess() {
				@Override
				public void called(Response response) {
					_balanceXRP = response.message;
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

	public void tx(String srcAddress, int ledger, int limit, final Rollback back) {
		RPClient client = RPClient.ripple();
		if (client != null) {
			Request req = client.newRequest(Command.account_tx);
			req.json("account", srcAddress);
			req.json("ledger_index_max", ledger);
			req.json("limit", limit);
			req.once(Request.OnSuccess.class, new Request.OnSuccess() {
				@Override
				public void called(Response response) {
					_balanceXRP = response.message;
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

	public JSONObject getBalanceXRP() {
		return _balanceXRP;
	}

	public JSONObject getBalanceIOU() {
		return _balanceIOU;
	}

	public JSONObject getOffer() {
		return _offer;
	}

	public JSONObject getSubscribe() {
		return _subscribe;
	}

}
