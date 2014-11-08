package org.ripple.power.txns;

import java.util.List;

import org.json.JSONObject;
import org.ripple.power.command.AMacros;
import org.ripple.power.command.IScriptLog;
import org.ripple.power.command.DMacros;

import com.ripple.client.enums.Command;

public class RippleMacros extends AMacros {

	private final int PING = 0;

	private final int SERVER_INFO = 1;

	private final int SERVER_STATE = 2;

	// developing
	// send address1 10/usd to address2
	// swap address1 10/usd to address2 10/xrp
	// server_info
	// server_state
	// ping

	public RippleMacros() {
		super("ripple.",
				new String[] { "ping", "server_info", "server_state" },
				new String[] { "send" });
	}

	@Override
	public void call(final IScriptLog log, final int scriptLine,
			final DMacros macros, final String message) {
		setScriptLog(log);
		setMacros(macros);
		List<String> list = DMacros.commandSplit(message);
		int size = list.size();

		if (size > 0) {

			switch (size) {
			case 1:
				JSONObject obj = new JSONObject();
				final String cmd = list.get(0);
				final int type = lookupCommand(cmd);

				switch (type) {
				case PING:
					setSyncing(type, true);
					RippleCommand.get(Command.ping, obj, new Rollback() {

						@Override
						public void success(JSONObject res) {
							setBaseInfo(type, res);
							log(type, res);
							setSyncing(type, false);
						}

						@Override
						public void error(JSONObject res) {
							setSyncing(type, false);
						}
					});
					break;
				case SERVER_INFO:
					setSyncing(type, true);
					RippleCommand.get(Command.server_info, obj, new Rollback() {

						@Override
						public void success(JSONObject res) {
							setBaseInfo(type, res);
							log(type, res);
							setSyncing(type, false);
						}

						@Override
						public void error(JSONObject res) {
							setSyncing(type, false);
						}
					});
					break;
				case SERVER_STATE:
					setSyncing(type, true);
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
									setSyncing(type, false);
								}
							});
					break;
				default:
					break;
				}

				break;

			default:
				break;
			}

		}
	}

	private void setBaseInfo(int type, JSONObject obj) {
		setJsonVar(type, obj, "id");
		setJsonVar(type, obj, "status");
		setJsonVar(type, obj, "type");
		setJsonVar(type, obj, "result");
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
