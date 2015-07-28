package org.ripple.power.hft.bot.ripple.data;

public class State {
	public String build_version;
	public String complete_ledgers;
	public int io_latency_ms;
	public LastClose last_close;
	public int load_base;
	public int load_factor;
	public int peers;
	public String pubkey_node;
	public String server_state;
	public ValidatedLedger validated_ledger;
	public int validation_quorum;
}
