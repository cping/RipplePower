package org.ripple.power.wallet;

public class WalletItem {

	private String pDate;

	private String pPublicKey;

	private String pPrivateKey;

	private String pAmount;

	private String pStatus;

	public WalletItem(String date, String pubKey, String priKey, String amount,
			String status) {
		this.pDate = date;
		this.pPublicKey = pubKey;
		this.pPrivateKey = priKey;
		this.pAmount = amount;
		this.pStatus = status;
	}

	public String toString() {
		return String.format(
				"date:%s,public:%s,private:%s,amount:%s,status:%s", pDate,
				pPublicKey, pPrivateKey, pAmount, pStatus);
	}

	public String getDate() {
		return pDate;
	}

	public String getPublicKey() {
		return pPublicKey;
	}

	public String getPrivateKey() {
		return pPrivateKey;
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

	public void setPublicKey(String pub) {
		this.pPublicKey = pub;
	}

	public void setPrivateKey(String pri) {
		this.pPrivateKey = pri;
	}

	public void setAmount(String a) {
		this.pAmount = a;
	}

	public void setStatus(String pStatus) {
		this.pStatus = pStatus;
	}

}
