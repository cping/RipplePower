package org.ripple.power.txns;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ripple.power.ui.RPClient;

import com.ripple.client.enums.Command;
import com.ripple.client.requests.Request;
import com.ripple.client.responses.Response;

public class AccountFind {

	public JSONObject _balanceXRP;

	public JSONObject _balanceIOU;

	public JSONObject _offer;

	public JSONObject _subscribe;

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
					update.action();
				}
			}

			@Override
			public void error(JSONObject res) {

				accountinfo.error = true;

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
					update.action();
				}
			}

			@Override
			public void error(JSONObject res) {

				accountinfo.error = true;

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

							Object taker_gets = getObject(o, "taker_gets");
							Object taker_pays = getObject(o, "taker_pays");

							BookOffer offer = new BookOffer(
									jsonToDenominatedAmount(taker_gets),
									jsonToDenominatedAmount(taker_pays));

							accountinfo.bookOffers.add(offer);

						}
					}
				}
				accountinfo.count++;
				if (update != null) {
					update.action();
				}
			}

			@Override
			public void error(JSONObject res) {

				accountinfo.error = true;

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

	private IssuedCurrency jsonToDenominatedAmount(Object jsonDenominatedAmount) {
		if (jsonDenominatedAmount instanceof JSONObject) {
			IssuedCurrency amount = new IssuedCurrency();
			amount.copyFrom((JSONObject) jsonDenominatedAmount);
			return amount;
		} else {
			return new IssuedCurrency((String) jsonDenominatedAmount);
		}
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
