package org.ripple.power.txns.btc;

public interface BTCMonitor {

	public void update(BTCPrice price);

	public void end();
}
