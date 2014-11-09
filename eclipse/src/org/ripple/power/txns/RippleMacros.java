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

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ripple.power.command.AMacros;
import org.ripple.power.command.IScriptLog;
import org.ripple.power.command.DMacros;

import com.ripple.client.enums.Command;

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

	// developing
	// send address1 10/usd to address2
	// swap address1 10/usd to address2 10/xrp
	// server_info
	// server_state
	// ping

	public RippleMacros() {
		super("ripple.",
				new String[] { "ping", "server_info", "server_state",
						"account_info", "account_lines", "account_offers",
						"account_tx" }, new String[] { "send" });
	}

	@Override
	public void call(final IScriptLog log, final int scriptLine,
			final DMacros macros, final String message) {
		setConfig(log, macros, scriptLine);
		List<String> list = DMacros.commandSplit(message);
		int size = list.size();

		if (size > 0) {
			JSONObject obj = new JSONObject();
			final String cmd = list.get(0);
			final int type = lookupCommand(cmd);
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
				if (!checkAddress(list, 1, type)) {
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
					find.tx(address, -1, 10, new Rollback() {

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

				break;
			default:
				break;
			}

		}
	}

	private boolean checkAddress(List<String> list, int idx, int type) {
		address = list.get(idx);
		if (!AccountFind.isRippleAddress(address)) {
			if (!address.startsWith("~")) {
				address = "~" + address;
			}
			try {
				address = NameFind.getAddress(address);
			} catch (Exception e) {
				return false;
			}
			if (address == null || !AccountFind.isRippleAddress(address)) {
				log(type, String.format("%s is an invalid address", address));
				return false;
			}
		}
		return true;
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
