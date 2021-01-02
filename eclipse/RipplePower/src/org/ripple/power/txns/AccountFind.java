package org.ripple.power.txns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ripple.power.blockchain.RippleMemoDecode;
import org.ripple.power.blockchain.RippleMemoDecodes;
import org.ripple.power.config.LSystem;
import org.ripple.power.txns.TransactionTx.Memo;
import org.ripple.power.txns.data.Meta;
import org.ripple.power.ui.RPClient;
import org.ripple.power.utils.Base58Coder;

import com.google.common.base.Strings;
import com.ripple.client.enums.Command;
import com.ripple.client.requests.Request;
import com.ripple.client.responses.Response;
import com.ripple.core.coretypes.RippleDate;
import com.ripple.core.enums.TransactionFlag;

public class AccountFind {

	// 是否使用dataapi的数据
	public static boolean importDataApi = false;

	public static int LIMIT_TX = 400;

	private static AccountFind instance = null;

	public final static AccountFind get() {
		if (instance == null) {
			instance = new AccountFind();
		}
		return instance;
	}

	public JSONObject _balanceXRP;

	public JSONObject _balanceIOU;

	public JSONObject _offer;

	public JSONObject _subscribe;

	public final static boolean isBitcoinAddress(String address) {
		if (Strings.isNullOrEmpty(address)) {
			return true;
		}
		if (!address.startsWith("1")) {
			return false;
		}
		String reg = String.format("^[13][%s]{26,34}$", Base58Coder.ALPHABET);
		boolean match = Pattern.matches(reg, address);
		return match && Base58Coder.isValidChecksum(address);
	}

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

	public final static boolean isStellarAddress(String address) {
		if (Strings.isNullOrEmpty(address)) {
			return false;
		}
		if (!address.startsWith("g")) {
			return false;
		}
		String reg = "^g[1-9A-HJ-NP-Za-km-z]{25,33}$";
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
		return obj.optString(key, "");
	}

	private final static int getInt(JSONObject obj, String key) {
		return obj.optInt(key, 0);
	}

	private final static long getLong(JSONObject obj, String key) {
		return obj.optLong(key, 0);
	}

	private final static JSONArray getArray(JSONObject obj, String key) {
		if (obj.has(key)) {
			return obj.getJSONArray(key);
		}
		return null;
	}

	public static RippleDate getDateTime(long date) {
		return RippleDate.fromSecondsSinceRippleEpoch(date);
	}

	public static int inCredits(ArrayList<IssuedCurrency> issues, IssuedCurrency currency) {
		for (int i = 0; i < issues.size(); i++) {
			IssuedCurrency cur = issues.get(i);
			if (cur.equals(currency)) {
				return i;
			}
		}
		return -1;
	}

	public static ArrayList<IssuedCurrency> getTrusts(final String address, final Updateable update) {
		final ArrayList<IssuedCurrency> lines = new ArrayList<IssuedCurrency>(10);
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

	public RippleMemoDecodes message(final String address, final Updateable update) {
		return message(address, null, -1, -1, LIMIT_TX, update);
	}

	public RippleMemoDecodes message(final String address, final String password, final Updateable update) {
		return message(address, password, -1, -1, LIMIT_TX, update);
	}

	/**
	 * send message to Rippled
	 * 
	 * @param address
	 * @param password
	 *            (Only ENCODE mode become effective)
	 * @param txPreLgrSeq
	 * @param max
	 * @param update
	 * @return
	 */
	public RippleMemoDecodes message(final String address, final String password, final long min, final long max,
			final long limit, final Updateable update) {
		final RippleMemoDecodes decodes = new RippleMemoDecodes();

		tx(address, min, max, limit, new Rollback() {

			@Override
			public void success(JSONObject res) {

				if (res.has("result")) {
					JSONObject result = res.getJSONObject("result");
					if (result != null) {
						if (result.has("transactions")) {
							JSONArray arrays = result.getJSONArray("transactions");
							for (int i = 0; i < arrays.length(); i++) {
								JSONObject transaction = arrays.getJSONObject(i);
								JSONObject tx = transaction.getJSONObject("tx");
								String account = tx.getString("Account");
								long date = tx.getLong("date");
								if (tx.has("Memos")) {
									JSONArray list = tx.getJSONArray("Memos");
									for (int m = 0; m < list.length(); m++) {
										RippleMemoDecode decode = new RippleMemoDecode(account, list.getJSONObject(m),
												date, password);
										decodes.add(decode);
									}
								}

							}
						}
					}
				}
				if (update != null) {
					update.action(decodes);
				}
			}

			@Override
			public void error(JSONObject res) {
				if (update != null) {
					update.action(decodes);
				}
			}
		});

		return decodes;
	}

	public TransactionTx processTxHash(final String hash, final TransactionTx transactionTx, final Updateable update) {
		txHash(hash, new Rollback() {

			@Override
			public void success(JSONObject res) {
				if (res.has("result")) {
					JSONObject result = res.getJSONObject("result");

					JSONObject meta = getJsonObject(result, "meta");
					String type = getStringObject(result, "TransactionType");

					if (meta != null) {
						transactionTx.metaString = meta.toString();
						transactionTx.meta = new Meta();
						transactionTx.meta.from(meta);
					}

					transactionTx.account = getStringObject(result, "Account");
					transactionTx.destination = getStringObject(result, "Destination");

					long date = getLong(result, "date");

					transactionTx.date_number = date;
					transactionTx.date = getDateTime(date).getTimeString();

					if (result.has("Memos")) {
						JSONArray list = result.getJSONArray("Memos");
						for (int m = 0; m < list.length(); m++) {
							transactionTx.memos.add(new Memo(list.getJSONObject(m), date));
						}
					}

					String fee = CurrencyUtils.getRippleToValue(String.valueOf(getLong(result, "Fee")));

					transactionTx.fee = fee;
					transactionTx.hash = getStringObject(result, "hash");
					transactionTx.sequence = getLong(result, "Sequence");
					transactionTx.offersSequence = getLong(result, "OfferSequence");
					transactionTx.inLedger = getLong(result, "inLedger");
					transactionTx.ledgerIndex = getLong(result, "ledger_index");
					transactionTx.flags = getLong(result, "Flags");
					transactionTx.clazz = type;
					if (transactionTx.flags != 0) {
						transactionTx.isPartialPayment = (TransactionFlag.PartialPayment == transactionTx.flags);
						transactionTx.flagsName = TransactionFlagMap.getString(transactionTx.flags);
					} else {
						transactionTx.flagsName = "Empty";
					}
					if (result.has("SendMax")) {
						transactionTx.sendMax = CurrencyUtils.getIssuedCurrency(result.get("SendMax"));
					}
					transactionTx.signingPubKey = getStringObject(result, "SigningPubKey");
					transactionTx.txnSignature = getStringObject(result, "TxnSignature");

					switch (type) {
					case "Payment":
						transactionTx.destinationTag = getLong(result, "DestinationTag");
						transactionTx.invoiceID = getStringObject(result, "InvoiceID");
						IssuedCurrency currency = null;
						String counterparty = null;
						if (meta != null && meta.has("DeliveredAmount")) {
							currency = CurrencyUtils.getIssuedCurrency(getObject(meta, "DeliveredAmount"));
						} else {
							currency = CurrencyUtils.getIssuedCurrency(getObject(result, "Amount"));
						}
						transactionTx.currency = currency;
						transactionTx.counterparty = counterparty;
						break;
					case "TrustSet":
						Object limitAmount = getObject(result, "LimitAmount");
						if (limitAmount != null) {
							transactionTx.currency = CurrencyUtils.getIssuedCurrency(limitAmount);
							transactionTx.trusted = transactionTx.currency.issuer.toString();
						}
						break;
					case "OfferCreate":
						transactionTx.get = CurrencyUtils.getIssuedCurrency(getObject(result, "TakerGets"));
						transactionTx.pay = CurrencyUtils.getIssuedCurrency(getObject(result, "TakerPays"));
						break;
					case "OfferCancel":
						JSONArray affectedNodes = getArray(meta, "AffectedNodes");
						for (int n = 0; n < affectedNodes.length(); n++) {
							JSONObject obj = affectedNodes.getJSONObject(n);
							if (obj.has("DeletedNode")) {
								JSONObject deleted = obj.getJSONObject("DeletedNode");
								String ledgerEntryType = getStringObject(deleted, "LedgerEntryType");
								if ("Offer".equals(ledgerEntryType)) {
									JSONObject ff = getJsonObject(deleted, "FinalFields");
									String ffactount = getStringObject(ff, "Account");
									if (ffactount.equals(transactionTx.account)) {
										transactionTx.get = CurrencyUtils.getIssuedCurrency(getObject(ff, "TakerGets"));
										transactionTx.pay = CurrencyUtils.getIssuedCurrency(getObject(ff, "TakerPays"));
									}
								}
							}

						}
						break;
					}

				}

				if (update != null) {
					update.action(res);
				}
			}

			@Override
			public void error(JSONObject res) {
				if (update != null) {
					update.action(res);
				}
			}
		});
		return transactionTx;
	}

	public AccountInfo processTx(final String address, final Updateable update) {
		return processTx(address, -1, -1, LIMIT_TX, new AccountInfo(), update);
	}

	public AccountInfo processTx(final String address, final AccountInfo accountinfo, final Updateable update) {
		return processTx(address, -1, -1, LIMIT_TX, accountinfo, update);
	}

	public AccountInfo processTxAffectedNodes(final String address, final AccountInfo accountinfo,
			final Updateable update) {
		return processTx(address, -1, -1, LIMIT_TX, accountinfo, update, true);
	}

	public AccountInfo processTxAffectedNodes(final String address, final long min, final long max, final long limit,
			final AccountInfo accountinfo, final Updateable update) {
		return processTx(address, min, max, limit, accountinfo, update, true);
	}

	public AccountInfo processTx(final String address, final long min, final long max, final long limit,
			final AccountInfo accountinfo, final Updateable update) {
		return processTx(address, min, max, limit, accountinfo, update, false);
	}

	public AccountInfo processTx(final String address, final long min, final long max, final long limit,
			final AccountInfo accountinfo, final Updateable update, final boolean loadAffectedNodes) {

		final ArrayList<IssuedCurrency> issues = new ArrayList<IssuedCurrency>(10);

		if (importDataApi) {
			JSONObject json = RippleDataApi.transactionsFind(accountinfo, issues, address);

			if (json != null && accountinfo.transactions.size() > 0) {
				update.action(json);
				return accountinfo;
			}
		}

		Updateable updateable = new Updateable() {

			@Override
			public void action(Object res) {

				tx(address, -1, -1, limit, new Rollback() {

					@Override
					public void success(JSONObject res) {

						try {
							JSONObject result = getJsonObject(res, "result");
							if (result != null) {
								if (result.has("marker")) {
									JSONObject marker = result.getJSONObject("marker");
									long ledger = getLong(marker, "ledger");
									if (accountinfo.marker != ledger) {
										if (accountinfo.marker == ledger) {
											accountinfo.marker = ledger - 1;
										} else {
											accountinfo.marker = ledger;
										}
										AccountInfo newInfo = new AccountInfo();
										newInfo.marker = accountinfo.marker;
										processTx(address, accountinfo.marker, ledger, limit, newInfo, null);
										accountinfo.accountlinks.add(newInfo);
									}
								}

								if (result.has("transactions")) {
									JSONArray arrays = getArray(result, "transactions");

									for (int i = 0; i < arrays.length(); i++) {

										TransactionTx transactionTx = new TransactionTx();

										JSONObject transaction = arrays.getJSONObject(i);
										JSONObject tx = getJsonObject(transaction, "tx");
										JSONObject meta = getJsonObject(transaction, "meta");
										String type = getStringObject(tx, "TransactionType");

										transactionTx.account = getStringObject(tx, "Account");
										transactionTx.destination = getStringObject(tx, "Destination");

										long date = getLong(tx, "date");

										transactionTx.date_number = date;
										transactionTx.date = getDateTime(date).getTimeString();

										if (tx.has("Memos")) {
											JSONArray list = tx.getJSONArray("Memos");
											for (int m = 0; m < list.length(); m++) {
												transactionTx.memos.add(new Memo(list.getJSONObject(m), date));
											}
										}

										String fee = CurrencyUtils.getRippleToValue(String.valueOf(getLong(tx, "Fee")));

										transactionTx.fee = fee;
										transactionTx.hash = getStringObject(tx, "hash");
										transactionTx.sequence = getLong(tx, "Sequence");
										transactionTx.offersSequence = getLong(tx, "OfferSequence");
										transactionTx.inLedger = getLong(tx, "inLedger");
										transactionTx.ledgerIndex = getLong(tx, "ledger_index");
										transactionTx.flags = getLong(tx, "Flags");
										transactionTx.clazz = type;
										if (transactionTx.flags != 0) {
											transactionTx.isPartialPayment = (TransactionFlag.PartialPayment == transactionTx.flags);
											transactionTx.flagsName = TransactionFlagMap.getString(transactionTx.flags);
										} else {
											transactionTx.flagsName = "Empty";
										}
										if (tx.has("SendMax")) {
											transactionTx.sendMax = CurrencyUtils.getIssuedCurrency(tx.get("SendMax"));
										}
										transactionTx.signingPubKey = getStringObject(tx, "SigningPubKey");
										transactionTx.txnSignature = getStringObject(tx, "TxnSignature");

										if (meta != null) {

											transactionTx.metaString = meta.toString();
											transactionTx.meta = new Meta();
											transactionTx.meta.from(meta);

											if (loadAffectedNodes && meta.has("AffectedNodes")) {
												JSONArray affectedNodes = meta.getJSONArray("AffectedNodes");
												int size = affectedNodes.length();

												for (int j = 0; j < size; j++) {
													JSONObject affectedNode = affectedNodes.getJSONObject(j);
													JSONArray names = affectedNode.names();
													for (int n = 0; n < names.length(); n++) {
														String key = names.getString(n);

														TransactionTx.AffectedNode node = new TransactionTx.AffectedNode();
														transactionTx.affectedNodeList.add(node);

														node.name = key;
														JSONObject ledger_node = affectedNode.getJSONObject(key);
														node.ledgerEntryType = getStringObject(ledger_node,
																"LedgerEntryType");
														if (node.ledgerEntryType != null) {

															node.previousTxnID = getStringObject(ledger_node,
																	"PreviousTxnID");
															node.txid = node.previousTxnID != null ? node.previousTxnID
																	: transactionTx.hash;
															node.ledgerIndex = getStringObject(ledger_node,
																	"LedgerIndex");
															node.previousTxnLgrSeq = getLong(ledger_node,
																	"PreviousTxnLgrSeq");

															JSONObject fields = null;
															if (ledger_node.has("FinalFields")) {
																fields = getJsonObject(ledger_node, "FinalFields");
															} else if (ledger_node.has("NewFields")) {
																fields = getJsonObject(ledger_node, "NewFields");
															}
															if (fields != null) {

																node.account = getStringObject(fields, "Account");
																node.regularKey = getStringObject(fields, "RegularKey");

																node.takerGetsIssuer = getStringObject(fields,
																		"TakerGetsIssuer");
																node.takerPaysIssuer = getStringObject(fields,
																		"TakerPaysIssuer");
																node.exchangeRate = getStringObject(fields,
																		"ExchangeRate");
																node.takerPaysCurrency = getStringObject(fields,
																		"TakerPaysCurrency");
																node.takerGetsCurrency = getStringObject(fields,
																		"TakerGetsCurrency");

																node.balance = CurrencyUtils.getIssuedCurrency(
																		getObject(fields, "Balance"));
																node.highLimit = CurrencyUtils.getIssuedCurrency(
																		getObject(fields, "HighLimit"));
																node.lowLimit = CurrencyUtils.getIssuedCurrency(
																		getObject(fields, "LowLimit"));

																node.owner = getStringObject(fields, "Owner");
																node.rootIndex = getStringObject(fields, "RootIndex");
																node.indexPrevious = getStringObject(fields,
																		"IndexPrevious");
																node.indexNext = getStringObject(fields, "IndexNext");
																node.sequence = getLong(fields, "Sequence");
																node.ownerCount = getLong(fields, "OwnerCount");
																node.transferRate = getLong(fields, "TransferRate");

																node.takerGets = CurrencyUtils.getIssuedCurrency(
																		getObject(fields, "TakerGets"));
																node.takerPays = CurrencyUtils.getIssuedCurrency(
																		getObject(fields, "TakerPays"));
																if (fields.has("Flags")) {
																	node.flags = getLong(fields, "Flags");
																} else {
																	node.flags = transactionTx.flags;
																}
																node.sell = OfferPrice.isSellOrder(node.flags);
																node.sellOrBuy = node.sell ? "sell" : "buy";
															}

														}
													}
												}

											}

										}

										switch (type) {
										case "Payment":

											transactionTx.destinationTag = getLong(tx, "DestinationTag");
											transactionTx.invoiceID = getStringObject(tx, "InvoiceID");

											IssuedCurrency currency = null;
											String counterparty = null;
											if (meta != null && meta.has("DeliveredAmount")) {
												currency = CurrencyUtils
														.getIssuedCurrency(getObject(meta, "DeliveredAmount"));
											} else {
												currency = CurrencyUtils.getIssuedCurrency(getObject(tx, "Amount"));
											}
											transactionTx.currency = currency;
											String flagType;
											if (address.equals(transactionTx.account)) {
												if (address.equals(transactionTx.destination)) {
													flagType = "Exchange";
												} else {
													flagType = "Send";
													counterparty = transactionTx.destination;
													int index = inCredits(issues, currency);
													if (index >= 0) {

													} else {
														issues.add(currency);
													}
												}
											} else if (address.equals(transactionTx.destination)) {
												flagType = "Receive";
												counterparty = transactionTx.account;
											} else {
												flagType = "Convert";
											}
											transactionTx.mode = flagType;
											transactionTx.counterparty = counterparty;
											break;
										case "TrustSet":
											Object limitAmount = getObject(tx, "LimitAmount");
											if (limitAmount != null) {
												transactionTx.currency = CurrencyUtils.getIssuedCurrency(limitAmount);
												transactionTx.trusted = transactionTx.currency.issuer.toString();
											}
											break;
										case "OfferCreate":
											transactionTx.get = CurrencyUtils
													.getIssuedCurrency(getObject(tx, "TakerGets"));
											transactionTx.pay = CurrencyUtils
													.getIssuedCurrency(getObject(tx, "TakerPays"));
											break;
										case "OfferCancel":
											JSONArray affectedNodes = getArray(meta, "AffectedNodes");
											for (int n = 0; n < affectedNodes.length(); n++) {
												JSONObject obj = affectedNodes.getJSONObject(n);
												if (obj.has("DeletedNode")) {
													JSONObject deleted = obj.getJSONObject("DeletedNode");
													String ledgerEntryType = getStringObject(deleted,
															"LedgerEntryType");
													if ("Offer".equals(ledgerEntryType)) {
														JSONObject ff = getJsonObject(deleted, "FinalFields");
														String ffactount = getStringObject(ff, "Account");
														if (ffactount.equals(transactionTx.account)) {
															transactionTx.get = CurrencyUtils
																	.getIssuedCurrency(getObject(ff, "TakerGets"));
															transactionTx.pay = CurrencyUtils
																	.getIssuedCurrency(getObject(ff, "TakerPays"));
														}
													}
												}

											}
											break;
										case "AccountSet":
											// Ignore
											break;
										}
										accountinfo.transactions.add(transactionTx);

									}
								}

							} else {
								// 如果获取不到数据，尝试使用页面api
								JSONObject json = RippleDataApi.transactionsFind(accountinfo, issues, address);
								accountinfo.count++;
								if (json != null && accountinfo.transactions.size() > 0) {
									JSONObject newObject = new JSONObject(json);
									newObject.put("info_data", accountinfo);
									update.action(newObject);
								}
								return;
							}

							accountinfo.count++;
							if (update != null) {
								JSONObject newObject = new JSONObject(res);
								newObject.put("info_data", accountinfo);
								update.action(newObject);
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						}

					}

					@Override
					public void error(JSONObject res) {
						accountinfo.error = true;
						if (update != null) {
							JSONObject newObject = new JSONObject(res);
							newObject.put("info_data", accountinfo);
							update.action(newObject);
						}
					}
				});
			}
		};
		return processInfo(address, accountinfo, updateable);
	}

	public AccountInfo processLines(final String address, final AccountInfo accountinfo, final Updateable update) {
		lines(address, new Rollback() {

			@Override
			public void success(JSONObject res) {
				JSONObject result = getJsonObject(res, "result");
				if (result != null) {
			
					JSONArray arrays = getArray(result, "lines");
					if (arrays != null) {

						int cntTrust = 0;
						HashMap<String, Double> debt = new HashMap<String, Double>(10);
						HashMap<String, Long> debtCount = new HashMap<String, Long>(10);
						HashMap<String, Integer> trustCount = new HashMap<String, Integer>(10);

						for (int i = 0; i < arrays.length(); i++) {
							JSONObject node = arrays.getJSONObject(i);
							String limit = getStringObject(node, "limit");
							long quality_out = getLong(node, "quality_out");
							long quality_in = getLong(node, "quality_in");
							String account = getStringObject(node, "account");
							String currency = getStringObject(node, "currency");
							String balance = getStringObject(node, "balance");
							String limit_peer = getStringObject(node, "limit_peer");
							boolean no_ripple = node.optBoolean("no_ripple");
							boolean peer_authorized = node.optBoolean("peer_authorized");

							Double number = Double.valueOf(balance);

							Double limit_peer_number = Double.valueOf(limit_peer);
					
							// get IOU
							if (number > 0) {
								AccountLine line = new AccountLine();
								line.peer_authorized = peer_authorized;
								line.no_ripple = no_ripple;
								line.issuer = account;
								line.currency = currency;
								line.balance = LSystem.getNumberShort(balance);
								line.limit = limit;
								line.quality_in = quality_in;
								line.quality_out = quality_out;
								line.limit_peer = limit_peer;
								accountinfo.lines.add(line);
								// send IOU
							} else if (number < 0) {
								if (currency != null) {
									cntTrust++;
									double n = debt.get(currency) == null ? 0 : debt.get(currency);
									if (debt.containsKey(currency)) {
										debt.put(currency, Double.parseDouble(LSystem.getNumberShort(n + number)));
										debtCount.put(currency, debtCount.get(currency) + 1l);
									} else {
										debt.put(currency, Double.parseDouble(LSystem.getNumberShort(n + number)));
										debtCount.put(currency, 1l);
									}
								}
								// set Trust
							} else if (number == 0) {
								double n = debt.get(currency) == null ? 0 : debt.get(currency);
								if (debt.containsKey(currency)) {
									debt.put(currency, Double.parseDouble(LSystem.getNumberShort(n + number)));
									debtCount.put(currency, debtCount.get(currency) + 1l);
								} else {
									debt.put(currency, Double.parseDouble(LSystem.getNumberShort(n + number)));
									debtCount.put(currency, 1l);
								}
								AccountLine line = new AccountLine();
								line.peer_authorized = peer_authorized;
								line.no_ripple = no_ripple;
								line.issuer = account;
								line.currency = currency;
								line.balance = balance;
								line.limit = limit;
								line.quality_in = quality_in;
								line.quality_out = quality_out;
								accountinfo.zero_lines.add(line);
							} else if (limit_peer_number > 0) {
								if (trustCount.containsKey(currency)) {
									trustCount.put(currency, trustCount.get(currency) + 1);
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

	public AccountInfo processInfo(final String address, final AccountInfo accountinfo, final Updateable update) {

		info(address, new Rollback() {

			@Override
			public void success(JSONObject res) {
				JSONObject result = getJsonObject(res, "result");

				if (result != null) {
					JSONObject account_data = getJsonObject(result, "account_data");
					if (account_data != null) {
						String balance = getStringObject(account_data, "Balance");
						if (balance != null) {
							accountinfo.balance = CurrencyUtils.getRippleToValue(balance);
						}
						accountinfo.faceURL = getStringObject(account_data, "urlgravatar");
						accountinfo.sequence = getInt(account_data, "Sequence");
						accountinfo.domain = getStringObject(account_data, "Domain");

						accountinfo.fee = String.valueOf(getLong(account_data, "TransferRate"));
						if (accountinfo.fee != null) {
							accountinfo.fee = CurrencyUtils.getFee(accountinfo.fee);
						}

						accountinfo.txPreLgrSeq = getInt(account_data, "PreviousTxnLgrSeq");

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

	public AccountInfo processOfffer(final String address, final AccountInfo accountinfo, final Updateable update) {
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

							long flags = 0;

							if (o.has("flags")) {
								flags = getLong(o, "flags");
							}

							Object taker_gets = getObject(o, "taker_gets");
							Object taker_pays = getObject(o, "taker_pays");

							BookOffer offer = new BookOffer(CurrencyUtils.getIssuedCurrency(taker_gets),
									CurrencyUtils.getIssuedCurrency(taker_pays), seq, flags);

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

	public AccountInfo load(String address, AccountInfo accountinfo, Updateable update) {

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
			req.json("ledger_index", "current");
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
			req.json("ledger", "current");
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

	public void currencies(String srcAddress, final Rollback back) {
		RPClient client = RPClient.ripple();
		if (client != null) {
			Request req = client.newRequest(Command.account_currencies);
			req.json("account", srcAddress);
			req.json("strict", true);
			req.json("ledger_index", "validated");
			req.json("account_index", 0);
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

	public void objects(String srcAddress, final int limit, final Rollback back) {
		RPClient client = RPClient.ripple();
		if (client != null) {
			Request req = client.newRequest(Command.account_objects);
			req.json("account", srcAddress);
			req.json("type", "state");
			req.json("ledger_index", "validated");
			req.json("limit", limit);
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

	public void noripple_check(String srcAddress, final int limit, final Rollback back) {
		RPClient client = RPClient.ripple();
		if (client != null) {
			Request req = client.newRequest(Command.noripple_check);
			req.json("account", srcAddress);
			req.json("role", "gateway");
			req.json("ledger_index", "current");
			req.json("limit", limit);
			req.json("transactions", true);
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

	public void info(String srcAddress, final Rollback back) {
		RPClient client = RPClient.ripple();
		if (client != null) {
			Request req = client.newRequest(Command.account_info);
			req.json("account", srcAddress);
			req.json("strict", true);
			req.json("ledger_index", "validated");
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

	public void tx(String srcAddress, long ledger_min, long ledger_max, long limit, final Rollback back) {

		JSONObject req = new JSONObject();
		req.put("account", srcAddress);
		req.put("ledger_index_min", ledger_min);
		req.put("ledger_index_max", ledger_max);

		req.put("binary", false);

		req.put("limit", limit);
		req.put("count", false);
		req.put("forward", false);

		TemporaryWebSocket.post(Command.account_tx, req, back);
	}

	public void txHash(String hash, final Rollback back) {
		RPClient client = RPClient.ripple();
		if (client != null) {
			Request req = client.newRequest(Command.tx);
			req.json("transaction", hash);
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
