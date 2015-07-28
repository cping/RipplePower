package org.ripple.power.hft.bot.ripple.data;

import org.ripple.power.hft.bot.ripple.Const;

public class CrOR_TxJson {
	public String TransactionType = "OfferCreate";
	public String Account;
	public String TakerPays;
	public Take TakerGets;
	public int Fee = Const.MAX_FEE;
}
