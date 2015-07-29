package org.ripple.power.txns.data;

public class CreateBuyOrderRequest {
	public String command = "submit";
	public CrOR_TxJson tx_json;
	public String secret;
}
