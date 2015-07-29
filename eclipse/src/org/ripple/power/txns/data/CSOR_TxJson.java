package org.ripple.power.txns.data;

import org.ripple.power.txns.Const;

public class CSOR_TxJson {

	public String TransactionType = "OfferCreate";
	public String Account;
	public Take TakerPays;
	public String TakerGets;

	public long Flags = 2147483648l;
	public int Fee = Const.MAX_FEE;
}
