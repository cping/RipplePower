package org.ripple.power.hft.bot.ripple.data;

import org.ripple.power.hft.bot.IMarketOrder;
import org.ripple.power.hft.bot.ripple.Const;

public class Ask implements IMarketOrder {
	public String Account;
	public String BookDirectory;
	public String BookNode;
	public int Flags;
	public String LedgerEntryType;
	public String OwnerNode;
	public String PreviousTxnID;
	public int PreviousTxnLgrSeq;
	public int Sequence;
	public String TakerGets;
	public Take TakerPays;
	public String index;
	public String quality;
	public int Expiration;
	public String taker_gets_funded;
	public Take taker_pays_funded;
	public double Amount;

	@Override
	public double getPrice() {
		double dollars = Double.parseDouble(TakerPays.value);
		return dollars / Amount;
	}

	@Override
	public double getAmount() {
		return Double.parseDouble(TakerGets) / Const.DROPS_IN_XRP;
	}

}
