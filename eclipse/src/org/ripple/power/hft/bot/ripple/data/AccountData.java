package org.ripple.power.hft.bot.ripple.data;

import org.ripple.power.hft.bot.ripple.Const;

public class AccountData {
	public String Account;
	public String Balance;
	public int Flags;
	public String LedgerEntryType;
	public int OwnerCount;
	public String PreviousTxnID;
	public int PreviousTxnLgrSeq;
	public int Sequence;
	public String index;

	public double getBalanceXrp() {

		long drops = Long.parseLong(Balance);
		return (double) drops / Const.DROPS_IN_XRP;

	}
}
