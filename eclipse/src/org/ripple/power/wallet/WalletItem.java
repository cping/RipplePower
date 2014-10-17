package org.ripple.power.wallet;

import org.address.ripple.RippleSeedAddress;

public class WalletItem {

	private String pDate;

	private RippleSeedAddress pSeed;

	private String pAmount;

	private String pStatus;

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

}
