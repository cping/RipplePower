package org.ripple.power.wallet;

import org.ripple.power.RippleSeedAddress;

public class WalletItem {

	private boolean pTip = true;

	private String pDate;

	private RippleSeedAddress pSeed;

	private String pAmount;

	private String pStatus;

	private boolean online;

	public WalletItem(String date, String seed, String amount, String status) {
		this(date, new RippleSeedAddress(seed), amount, status);
	}

	public WalletItem(String date, RippleSeedAddress seed, String amount,
			String status) {
		this.pDate = date;
		this.pSeed = seed;
		this.pAmount = amount;
		this.pStatus = status;
	}

	public RippleSeedAddress getSeed() {
		return pSeed;
	}

	public String toString() {
		return String.format(
				"date:%s,public:%s,private:%s,amount:%s,status:%s", pDate,
				pSeed.getPublicKey(), pSeed.getPrivateKey(), pAmount, pStatus);
	}

	public String getDate() {
		return pDate;
	}

	public String getPublicKey() {
		return pSeed.getPublicKey();
	}

	public String getPrivateKey() {
		return pSeed.getPrivateKey();
	}

	public String getAmount() {
		return pAmount;
	}

	public String getStatus() {
		return pStatus;
	}

	public void setDate(String date) {
		this.pDate = date;
	}

	public void setAmount(String a) {
		this.pAmount = a;
	}

	public void setStatus(String pStatus) {
		this.pStatus = pStatus;
	}

	public boolean isTip() {
		return pTip;
	}

	public void setTip(boolean t) {
		this.pTip = t;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

}
