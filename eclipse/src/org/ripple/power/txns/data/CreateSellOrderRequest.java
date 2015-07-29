package org.ripple.power.txns.data;

public class CreateSellOrderRequest {
	public String command = "submit";
	public CSOR_TxJson tx_json;
	public String secret;
}
