package org.ripple.power.txns.btc;


public interface ConnectionListener {

    public void connectionStarted(Peer peer);

    public void connectionEnded(Peer peer);
}