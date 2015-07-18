package org.ripple.power.txns.btc;

import java.util.HashMap;
import java.util.Map;

public class PeerRequest {

    private Peer origin;

    private Sha256Hash hash;

    private int type;

    private final Map<Peer, Peer> peerMap = new HashMap<>(25);

    private long timeStamp;

    private boolean processing;

    public PeerRequest(Sha256Hash hash, int type) {
        this(hash, type, null);
    }

    public PeerRequest(Sha256Hash hash, int type, Peer origin) {
        this.hash = hash;
        this.type = type;
        this.origin = origin;
    }

    public Peer getOrigin() {
        return origin;
    }

    public Sha256Hash getHash() {
        return hash;
    }

    public int getType() {
        return type;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean wasContacted(Peer peer) {
        return (peerMap.get(peer)!=null);
    }

    public void addPeer(Peer peer) {
        if (peerMap.get(peer) == null)
            peerMap.put(peer, peer);
    }

    public boolean isProcessing() {
        return processing;
    }

    public void setProcessing(boolean isProcessing) {
        processing = isProcessing;
    }

    @Override
    public int hashCode() {
        return hash.hashCode()^type;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj!=null && (obj instanceof PeerRequest) && hash.equals(((PeerRequest)obj).hash) &&
                                        type==((PeerRequest)obj).type);
    }
}
