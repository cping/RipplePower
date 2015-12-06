package org.ripple.power.txns.btc;

import java.util.HashMap;
import java.util.Map;

public class PeerRequest {

    /** Peer that sent the 'inv' message */
    private Peer origin;

    /** The block or transaction hash */
    private Sha256Hash hash;

    /** The inventory type */
    private int type;

    /** Map of peers that have been contacted for this request */
    private final Map<Peer, Peer> peerMap = new HashMap<>(25);

    /** Timestamp */
    private long timeStamp;

    /** Request is being processed */
    private boolean processing;

    /**
     * Creates a new peer request
     *
     * @param       hash            The transaction or block hash
     * @param       type            The inventory type (INV_BLOCK or INV_TX)
     */
    public PeerRequest(Sha256Hash hash, int type) {
        this(hash, type, null);
    }

    /**
     * Creates a new peer request
     *
     * @param       hash            The transaction or block hash
     * @param       type            The inventory type (INV_BLOCK or INV_TX)
     * @param       origin          Peer that sent the 'inv' message
     */
    public PeerRequest(Sha256Hash hash, int type, Peer origin) {
        this.hash = hash;
        this.type = type;
        this.origin = origin;
    }

    /**
     * Returns the origin for this request
     *
     * @return      Peer that sent the 'inv' message or null if not an 'inv' request
     */
    public Peer getOrigin() {
        return origin;
    }

    /**
     * Returns the block or transaction hash
     *
     * @return      Block or transaction hash
     */
    public Sha256Hash getHash() {
        return hash;
    }

    /**
     * Returns the inventory type
     *
     * @return      Inventory type (INV_BLOCK or INV_TX)
     */
    public int getType() {
        return type;
    }

    /**
     * Returns the request timestamp
     *
     * @return      Request timestamp
     */
    public long getTimeStamp() {
        return timeStamp;
    }

    /**
     * Sets the request timestamp
     *
     * @param       timeStamp       Request timestamp
     */
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * Checks if a peer has already been contacted for this request
     *
     * @param       peer            Peer
     * @return      TRUE if the peer has been contacted
     */
    public boolean wasContacted(Peer peer) {
        return (peerMap.get(peer)!=null);
    }

    /**
     * Indicates that a peer has been contacted
     *
     * @param       peer            The peer that has been contacted
     */
    public void addPeer(Peer peer) {
        if (peerMap.get(peer) == null)
            peerMap.put(peer, peer);
    }

    /**
     * Checks if the request is being processed
     *
     * @return      TRUE if the request is being processed
     */
    public boolean isProcessing() {
        return processing;
    }

    /**
     * Sets request as being processed
     *
     * @param       isProcessing        TRUE if the request is being processed
     */
    public void setProcessing(boolean isProcessing) {
        processing = isProcessing;
    }

    /**
     * Returns the hash code for this object
     *
     * @return      Hash code
     */
    @Override
    public int hashCode() {
        return hash.hashCode()^type;
    }

    /**
     * Checks if two objects are equal
     *
     * @param       obj             The object to compare
     * @return      TRUE if the objects are equal
     */
    @Override
    public boolean equals(Object obj) {
        return (obj!=null && (obj instanceof PeerRequest) && hash.equals(((PeerRequest)obj).hash) &&
                                type==((PeerRequest)obj).type);
    }
}
