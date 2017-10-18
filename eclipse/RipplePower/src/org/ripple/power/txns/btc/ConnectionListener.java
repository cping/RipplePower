package org.ripple.power.txns.btc;

public interface ConnectionListener {

	/**
	 * Notifies when a connection is started
	 *
	 * @param peer
	 *            Remote peer
	 * @param count
	 *            Connection count
	 */
	public void connectionStarted(Peer peer, int count);

	/**
	 * Notifies when a connection is terminated
	 *
	 * @param peer
	 *            Remote peer
	 * @param count
	 *            Connection count
	 */
	public void connectionEnded(Peer peer, int count);
}
