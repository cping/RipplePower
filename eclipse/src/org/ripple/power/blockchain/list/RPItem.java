package org.ripple.power.blockchain.list;

public class RPItem implements RPElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	byte[] rpData;

	public RPItem(byte[] rpData) {
		this.rpData = rpData;
	}

	public byte[] getRPData() {
		if (rpData.length == 0) {
			return null;
		}
		return rpData;
	}
}
