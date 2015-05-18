/**
 * Copyright 2008
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
package org.ripple.power.txns;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ripple.power.RippleSeedAddress;
import org.ripple.power.command.AMacros;
import org.ripple.power.command.IScriptLog;
import org.ripple.power.command.DMacros;
import org.ripple.power.config.LSystem;
import org.ripple.power.utils.StringUtils;
import org.ripple.power.wallet.WalletCache;

import com.ripple.client.enums.Command;
import com.ripple.core.types.known.sle.entries.Offer;

public class RippleMacros extends AMacros {

	final AccountFind find = new AccountFind();

	private String address = null;

	private final int PING = 0;

	private final int SERVER_INFO = 1;

	private final int SERVER_STATE = 2;

	private final int ACCOUNT_INFO = 3;

	private final int ACCOUNT_LINES = 4;

	private final int ACCOUNT_OFFERS = 5;

	private final int ACCOUNT_TX = 6;

	private final int TRANSACTION_ENTRY = 7;

	private final int TRANSACTION_TX = 8;

	private final int TRANSACTION_HISTORY = 9;

	private final int SEND = 10;

	private final int OFFER_CREATE = 11;

	private final int OFFER_CANCEL = 12;

	private final int OFFER_PRICE = 13;

	private final int CONVERT_PRICE = 14;

	public RippleMacros() {
		super("ripple.",
				new String[] { "ping", "server_info", "server_state",
						"account_info", "account_lines", "account_offers",
						"account_tx", "transaction_entry", "tx", "tx_history",
						"send", "offer_create", "offer_cancel", "offer_price",
						"convert_price" });
	}

	@Override
	public void call(final IScriptLog log, final int scriptLine,
			final DMacros macros, final String message) {
		setConfig(log, macros, scriptLine);
		List<String> list = DMacros.commandSplit(message, false);
		int size = list.size();

		if (size > 0) {
			JSONObject obj = new JSONObject();
			final String cmd = list.get(0);
			final int type = lookupCommand(cmd);

			// 视为同种交易操作
			if (type == SEND || type == OFFER_CREATE) {
				if (size > 3) {
					String curOne = list.get(1);
					String curTwo = list.get(2);
					int start = curOne.indexOf("/");
					String secret = null;
					if (start != -1) {
						secret = curOne.substring(0, start);
						secret = getSecret(secret);
					}
					if (secret == null) {
						return;
					}
					if (size == 3) {
						send(type, curOne, curTwo, secret, LSystem.getFee());
					} else if (size == 4) {
						String fee = list.get(3);
						if (StringUtils.isNumber(fee)) {
							send(type, curOne, curTwo, secret, fee);
						} else {
							error(new Exception(
									"Transaction fees must be in digital format"));
						}
					}
				}
				return;
			} else if (type == OFFER_CANCEL) {
				if (size > 3) {
					String secret = list.get(1);
					secret = getSecret(secret);
					if (secret == null) {
						return;
					}
					long offerSequence = 0;
					try {
						offerSequence = Long.parseLong(list.get(2));
					} catch (Exception ex) {
						error(ex);
						return;
					}
					String fee = LSystem.getFee();
					if (size == 4) {
						fee = list.get(3);
					}
					if (!StringUtils.isNumber(fee)) {
						error(new Exception(
								"Transaction fees must be in digital format"));
					}
					setSyncing(type, true);
					OfferCancel.set(new RippleSeedAddress(secret),
							offerSequence, fee, new Rollback() {

								@Override
								public void success(JSONObject res) {
									setVar(type, res);
									log(type, res);
									setSyncing(type, false);
								}

								@Override
								public void error(JSONObject res) {
									log(type, res);
									setSyncing(type, false);
								}
							});
				}
				return;
			} else if (type == OFFER_PRICE) {
				if (size == 4) {
					String address = list.get(1);
					final String seller = list.get(2);
					final String buyer = list.get(3);
					address = getAddress(address);

					if (address == null) {
						return;
					}
					setSyncing(type, true);
					OfferPrice.load(address, seller, buyer, new OfferPrice() {

						@Override
						public void sell(Offer offer) {

						}

						@Override
						public void error(JSONObject obj) {
							setSyncing(type, false);
						}

						@Override
						public void empty() {
							setSyncing(type, false);
						}

						@Override
						public void complete(ArrayList<OfferFruit> buys,
								ArrayList<OfferFruit> sells, OfferPrice price) {
							log(type, String.format(
									"1/%s high_buy:%s high_sell:%s spread:%s",
									seller, price.highBuy, price.highSell,
									price.spread));
							setVar(type, "highbuy", price.highBuy);
							setVar(type, "highsell", price.highSell);
							setVar(type, "spread", price.spread);
							setVar(type, "highbuy_value", Double
									.parseDouble(price.highBuy.split("/")[0]));
							setVar(type, "highsell_value", Double
									.parseDouble(price.highSell.split("/")[0]));
							setVar(type, "spread_value", Double
									.parseDouble(price.spread.split("/")[0]));
							setVar(type, "buys", buys);
							setVar(type, "sells", sells);
							setSyncing(type, false);
						}

						@Override
						public void buy(Offer offer) {

						}
					}, false);

				}
				return;
			} else if (type == CONVERT_PRICE) {
				if (size == 4) {
					setSyncing(type, true);
					String amount = list.get(1);
					if (StringUtils.isNumber(amount)) {
						String cur1 = list.get(2);
						String cur2 = list.get(3);
						String result = OfferPrice.getMoneyConvert(amount,
								cur1, cur2);
						if (StringUtils.isNumber(result)) {
							setVar(type, Double.parseDouble(result));
						} else {
							setVar(type, result);
						}
						log(type,
								String.format("%s/%s == %s/%s", amount,
										cur1.toUpperCase(), result,
										cur2.toUpperCase()));
					} else {
						error(new Exception("Invalid Conversion Amount"));
					}
					setSyncing(type, false);
				}
				return;
			}
			switch (size) {
			case 1:
				setSyncing(type, true);
				switch (type) {
				case PING:
					RippleCommand.get(Command.ping, obj, new Rollback() {

						@Override
						public void success(JSONObject res) {
							setBaseInfo(type, res);
							log(type, res);
							setSyncing(type, false);
						}

						@Override
						public void error(JSONObject res) {
							log(type, res);
							setSyncing(type, false);
						}
					});
					break;
				case SERVER_INFO:
					RippleCommand.get(Command.server_info, obj, new Rollback() {

						@Override
						public void success(JSONObject res) {
							setBaseInfo(type, res);
							log(type, res);
							setSyncing(type, false);
						}

						@Override
						public void error(JSONObject res) {
							log(type, res);
							setSyncing(type, false);
						}
					});
					break;
				case SERVER_STATE:
					RippleCommand.get(Command.server_state, obj,
							new Rollback() {

								@Override
								public void success(JSONObject res) {
									setBaseInfo(type, res);
									log(type, res);
									setSyncing(type, false);
								}

								@Override
								public void error(JSONObject res) {
									log(type, res);
									setSyncing(type, false);
								}
							});
					break;
				}

				break;
			case 2:
				if (type == TRANSACTION_TX) {
					setSyncing(type, true);
					String parameter = list.get(1);
					if (!AccountFind.is256hash(parameter)) {
						error(new Exception(String.format("%s Not 256 Hash",
								parameter)));
						break;
					}
					obj.put("transaction", parameter);
					RippleCommand.get(Command.tx, obj, new Rollback() {

						@Override
						public void success(JSONObject res) {
							setVar(type, res);
							log(type, res);
							setSyncing(type, false);

						}

						@Override
						public void error(JSONObject res) {
							log(type, res);
							setSyncing(type, false);

						}
					});
					break;
				} else if (type == TRANSACTION_HISTORY) {
					setSyncing(type, true);
					String parameter = list.get(1);
					obj.put("start", Long.parseLong(parameter));
					RippleCommand.get(Command.tx_history, obj, new Rollback() {

						@Override
						public void success(JSONObject res) {
							setVar(type, res);
							log(type, res);
							setSyncing(type, false);

						}

						@Override
						public void error(JSONObject res) {
							log(type, res);
							setSyncing(type, false);

						}
					});
					break;
				}
				if (!checkAddress(list, 1)) {
					break;
				}
				setSyncing(type, true);
				switch (type) {
				case ACCOUNT_INFO:
					find.info(address, new Rollback() {

						@Override
						public void success(JSONObject res) {
							setAccountInfo(type, res);
							log(type, res);
							setSyncing(type, false);
						}

						@Override
						public void error(JSONObject res) {
							log(type, res);
							setSyncing(type, false);
						}
					});
					break;
				case ACCOUNT_LINES:
					find.lines(address, new Rollback() {

						@Override
						public void success(JSONObject res) {
							setAccountLine(type, res);
							log(type, res);
							setSyncing(type, false);
						}

						@Override
						public void error(JSONObject res) {
							log(type, res);
							setSyncing(type, false);
						}
					});
					break;
				case ACCOUNT_OFFERS:
					find.offer(address, new Rollback() {

						@Override
						public void success(JSONObject res) {
							setAccountOffer(type, res);
							log(type, res);
							setSyncing(type, false);
						}

						@Override
						public void error(JSONObject res) {
							log(type, res);
							setSyncing(type, false);
						}
					});
					break;
				case ACCOUNT_TX:
					find.tx(address, -1,-1, 20, new Rollback() {

						@Override
						public void success(JSONObject res) {
							setVar(type, res);
							log(type, res);
							setSyncing(type, false);
						}

						@Override
						public void error(JSONObject res) {
							log(type, res);
							setSyncing(type, false);
						}
					});
					break;
				}
				break;
			case 3:
				String parameter1 = list.get(1);
				String parameter2 = list.get(2);
				if (!AccountFind.is256hash(parameter1)) {
					error(new Exception(String.format("%s Not 256 Hash",
							parameter1)));
					break;
				}
				setSyncing(type, true);
				switch (type) {
				case TRANSACTION_ENTRY:
					obj.put("tx_hash", parameter1);
					obj.put("ledger_index", Long.parseLong(parameter2));
					RippleCommand.get(Command.transaction_entry, obj,
							new Rollback() {

								@Override
								public void success(JSONObject res) {
									setVar(type, res);
									log(type, res);
									setSyncing(type, false);

								}

								@Override
								public void error(JSONObject res) {
									log(type, res);
									setSyncing(type, false);

								}
							});
					break;
				case 2:
					obj.put("tx_hash", parameter1);
					obj.put("ledger_index", Long.parseLong(parameter2));
					RippleCommand.get(Command.tx_history, obj, new Rollback() {

						@Override
						public void success(JSONObject res) {
							setVar(type, res);
							log(type, res);
							setSyncing(type, false);

						}

						@Override
						public void error(JSONObject res) {
							log(type, res);
							setSyncing(type, false);

						}
					});
					break;
				}
				break;
			default:
				break;
			}

		}
	}

	protected void send(final int type, final String curOne,
			final String curTwo, final String secret, final String fee) {
		Object sendSrc = getCurrency(curOne);
		Object sendDst = getCurrency(curTwo);

		if (sendSrc != null && sendDst != null && !sendSrc.equals(sendDst)) {

			if (sendSrc instanceof IssuedCurrency && sendDst instanceof String) {
				setSyncing(type, true);
				Payment.send(secret, (String) sendDst,
						((IssuedCurrency) sendSrc), fee, new Rollback() {

							@Override
							public void success(JSONObject res) {
								setVar(type, res);
								log(type, res);
								setSyncing(type, false);
							}

							@Override
							public void error(JSONObject res) {
								log(type, res);
								setSyncing(type, false);
							}
						});

				// 视为挂单
			} else if (sendSrc instanceof IssuedCurrency
					&& sendDst instanceof IssuedCurrency) {
				setSyncing(type, true);
				OfferCreate.set(new RippleSeedAddress(secret),
						((IssuedCurrency) sendSrc), ((IssuedCurrency) sendDst),
						fee, new Rollback() {

							@Override
							public void success(JSONObject res) {
								setVar(type, res);
								log(type, res);
								setSyncing(type, false);
							}

							@Override
							public void error(JSONObject res) {
								log(type, res);
								setSyncing(type, false);
							}
						});
			}
		} else {
			error(new Exception("Invalid Send command"));
		}
	}

	protected Object getCurrency(String str) {
		IssuedCurrency currency = null;
		int start = str.indexOf("/");
		if (start != -1) {
			String cur = str.substring(start + 1, str.length());
			if (cur.indexOf(LSystem.nativeCurrency) != -1) {
				currency = new IssuedCurrency(cur, true);
			} else {
				String[] split = StringUtils.split(cur, "/");
				if (split.length == 3) {
					String v = split[2];
					String addr = getAddress(v);
					if (addr == null) {
						if (Gateway.getAddress(v) != null) {
							ArrayList<Gateway.Item> items = Gateway
									.getAddress(v).accounts;
							if (items.size() > 0) {
								addr = items.get(0).address;
							}
						}
						if (addr == null) {
							return null;
						}
						StringBuffer sbr = new StringBuffer();
						for (int i = 0; i < split.length - 1; i++) {
							sbr.append(split[i]);
							sbr.append('/');
						}
						sbr.append(addr);
						cur = sbr.toString();
					}
					currency = new IssuedCurrency(cur);
				} else {
					error(new Exception(String.format("%s is invalid format",
							cur)));
				}
			}
		} else {
			if (AccountFind.isRippleAddress(str)) {
				return str;
			} else {
				str = getAddress(str);
				if (str == null) {
					error(new Exception(String.format("%s is invalid format",
							str)));
				}
			}
		}
		return currency;
	}

	protected String getSecret(final String name) {
		String result = null;
		if (AccountFind.isRippleSecret(name)) {
			return name;
		} else {
			result = getAddress(name);
			if (result == null) {
				return null;
			}
		}
		result = WalletCache.get().findSecret(result);
		if (result == null) {
			error(new Exception(String.format("%s is an invalid secret", name)));
		}
		return result;
	}

	protected String getAddress(String name) {
		if (!AccountFind.isRippleAddress(name)) {
			if (!name.startsWith("~")) {
				name = "~" + name;
			}
			try {
				name = NameFind.getAddress(name);
			} catch (Exception e) {
			}
			if (name == null || !AccountFind.isRippleAddress(name)) {
				error(new Exception(String.format("%s is an invalid address",
						name)));
			}
			return name;
		} else {
			return name;
		}
	}

	private boolean checkAddress(List<String> list, int idx) {
		return (address = getAddress(list.get(idx))) != null;
	}

	private void setAccountOffer(int type, JSONObject obj) {
		try {
			if (obj.has("result")) {
				JSONObject result = obj.getJSONObject("result");
				String name = "offers";
				if (result.has(name)) {
					JSONArray lines = result.getJSONArray(name);
					setVar(type, "offers.size", lines.length());
					for (int i = 0; i < lines.length(); i++) {
						JSONObject line = lines.getJSONObject(i);
						setJsonArrayVar(type, line, name, i, "taker_pays", true);
						setJsonArrayVar(type, line, name, i, "flags");
						setJsonArrayVar(type, line, name, i, "taker_gets", true);
						setJsonArrayVar(type, line, name, i, "seq");
					}
				}
			}
		} catch (Exception ex) {
			error(ex);
		}
	}

	private void setAccountLine(int type, JSONObject obj) {
		try {
			if (obj.has("result")) {
				JSONObject result = obj.getJSONObject("result");
				setJsonVar(type, result, "account");
				String name = "lines";
				if (result.has(name)) {
					JSONArray lines = result.getJSONArray(name);
					setVar(type, "lines.size", lines.length());
					for (int i = 0; i < lines.length(); i++) {
						JSONObject line = lines.getJSONObject(i);
						setJsonArrayVar(type, line, name, i, "limit");
						setJsonArrayVar(type, line, name, i, "balance");
						setJsonArrayVar(type, line, name, i, "quality_out");
						setJsonArrayVar(type, line, name, i, "account");
						setJsonArrayVar(type, line, name, i, "quality_in");
						setJsonArrayVar(type, line, name, i, "limit_peer");
						setJsonArrayVar(type, line, name, i, "currency");
					}
				}
			}
		} catch (Exception ex) {
			error(ex);
		}
	}

	private void setAccountInfo(int type, JSONObject obj) {
		try {
			if (obj.has("result")) {
				JSONObject result = obj.getJSONObject("result");
				setJsonVar(type, result, "ledger_current_index");
				if (result.has("account_data")) {
					JSONObject account_data = result
							.getJSONObject("account_data");
					setJsonVar(type, account_data, "LedgerEntryType");
					setJsonVar(type, account_data, "AccountRoot");
					setJsonVar(type, account_data, "index");
					setJsonVar(type, account_data, "Domain");
					setJsonVar(type, account_data, "PreviousTxnID");
					setJsonVar(type, account_data, "PreviousTxnLgrSeq");
					setJsonVar(type, account_data, "OwnerCount");
					setJsonVar(type, account_data, "Flags");
					setJsonVar(type, account_data, "Sequence");
					setJsonVar(type, account_data, "Balance");
				}
			}
		} catch (Exception ex) {
			error(ex);
		}
	}

	private void setBaseInfo(int type, JSONObject obj) {
		setJsonVar(type, obj, "id");
		setJsonVar(type, obj, "status");
		setJsonVar(type, obj, "type");
		if (obj.has("result")) {
			JSONObject result = obj.getJSONObject("result");
			if (result.has("info")) {
				JSONObject info = result.getJSONObject("info");
				setJsonVar(type, info, "hostid");
				setJsonVar(type, info, "server_state");
				setJsonVar(type, info, "load_factor");
				setJsonVar(type, info, "build_version");
				setJsonVar(type, info, "validation_quorum");
				setJsonVar(type, info, "io_latency_ms");
				setJsonVar(type, info, "load_factor");
				if (info.has("validated_ledger")) {
					JSONObject validated_ledger = info
							.getJSONObject("validated_ledger");
					setJsonVar(type, validated_ledger, "base_fee_xrp");
					setJsonVar(type, validated_ledger, "age");
					setJsonVar(type, validated_ledger, "reserve_base_xrp");
					setJsonVar(type, validated_ledger, "reserve_inc_xrp");
					setJsonVar(type, validated_ledger, "seq");
				}
				setJsonVar(type, info, "validated_ledger");
				setJsonVar(type, info, "pubkey_node");
			}
			if (result.has("state")) {
				JSONObject state = result.getJSONObject("state");
				setJsonVar(type, state, "build_version");
				setJsonVar(type, state, "io_latency_ms");
				setJsonVar(type, state, "validation_quorum");
				setJsonVar(type, state, "load_base");
				setJsonVar(type, state, "peers");
				setJsonVar(type, state, "pubkey_node");
				setJsonVar(type, state, "server_state");
				if (state.has("validated_ledger")) {
					JSONObject validated_ledger = state
							.getJSONObject("validated_ledger");
					setJsonVar(type, validated_ledger, "base_fee");
					setJsonVar(type, validated_ledger, "close_time");
					setJsonVar(type, validated_ledger, "reserve_base");
					setJsonVar(type, validated_ledger, "reserve_inc");
					setJsonVar(type, validated_ledger, "seq");
				}
				setJsonVar(type, state, "validation_quorum");
			}
		}
	}

}
