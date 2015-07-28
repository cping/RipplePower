package org.ripple.power.hft.bot.ripple.data;

public class CreateSellOrderRequest {
	public String command = "submit";
	public CSOR_TxJson tx_json;
	public String secret;
}
