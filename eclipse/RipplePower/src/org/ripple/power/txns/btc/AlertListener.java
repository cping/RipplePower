package org.ripple.power.txns.btc;

public interface AlertListener {

	/**
	 * Notifies when an alert is received
	 *
	 * @param alert
	 *            Alert
	 */
	public void alertReceived(Alert alert);
}
