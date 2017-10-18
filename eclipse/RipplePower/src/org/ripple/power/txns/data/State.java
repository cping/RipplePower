package org.ripple.power.txns.data;

import org.json.JSONObject;

public class State {
	public String build_version;
	public String complete_ledgers;
	public int io_latency_ms;
	public LastClose last_close = new LastClose();
	public int load_base;
	public int load_factor;
	public int peers;
	public String hostid;
	public String pubkey_node;
	public String server_state;
	public ValidatedLedger validated_ledger = new ValidatedLedger();
	public int validation_quorum;

	public void from(JSONObject result) {
		if (result != null) {
			this.build_version = result.optString("build_version");
			this.pubkey_node = result.optString("pubkey_node");
			this.load_factor = result.optInt("load_factor");
			this.complete_ledgers = result.optString("complete_ledgers");
			this.peers = result.optInt("peers");
			this.hostid = result.optString("hostid");
			JSONObject last_close_obj = result.optJSONObject("last_close");
			if (last_close_obj != null) {
				this.last_close.from(last_close_obj);
			}
			this.io_latency_ms = result.optInt("io_latency_ms");
			JSONObject validated_ledger_obj = result.optJSONObject("validated_ledger");
			if (validated_ledger_obj != null) {
				this.validated_ledger.from(validated_ledger_obj);
			}
			this.validation_quorum = result.optInt("validation_quorum");
			this.server_state = result.optString("server_state");
		}
	}
}
