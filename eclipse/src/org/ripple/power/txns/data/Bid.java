package org.ripple.power.txns.data;

import org.ripple.power.hft.bot.IMarketOrder;
import org.ripple.power.txns.Const;

public class Bid implements IMarketOrder{
    public String Account ;
    public String BookDirectory ;
    public String BookNode ;
    public int Flags ;
    public String LedgerEntryType ;
    public String OwnerNode ;
    public String PreviousTxnID ;
    public int PreviousTxnLgrSeq ;
    public int Sequence ;
    public Take TakerGets ;
    public String TakerPays ;
    public String index ;
    public String quality ;
    public int Expiration ;
    public Take taker_gets_funded ;
    public String taker_pays_funded ;
    public double Amount;
    
	@Override
	public double getPrice() {
		return Double.parseDouble(TakerPays) / Const.DROPS_IN_XRP;
	}

	@Override
	public double getAmount() {
        double dollars = Double.parseDouble(TakerGets.value);
        return dollars / Amount;
	}

}
