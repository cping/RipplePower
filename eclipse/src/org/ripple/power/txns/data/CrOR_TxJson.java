package org.ripple.power.txns.data;

import org.ripple.power.txns.Const;

public class CrOR_TxJson {
	public String TransactionType = "OfferCreate";
	public String Account;
	public String TakerPays;
	public Take TakerGets;
	public int Fee = Const.MAX_FEE;
}
