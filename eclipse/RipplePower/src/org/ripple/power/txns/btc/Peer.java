package org.ripple.power.txns.btc;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * The Bitcoin network consists of peer nodes which establish communication links
 * between themselves.  The nodes exchange blocks and transactions which are used
 * to create new blocks which are then added to the block chain.
 *
 * The Peer object contains the information needed to handle communications with a
 * remote peer.
 */
public class Peer {

    /** Peer address */
    private final PeerAddress address;

    /** Socket channel */
    private final SocketChannel channel;

    /** Selection key */
    private final SelectionKey key;

    /** Transaction Bloom filter */
    private BloomFilter bloomFilter;

    /** Inbound message count */
    private int inputCount;

    /** Current input buffer */
    private ByteBuffer inputBuffer;

    /** Output message list */
    private final List<Message> outputList = new ArrayList<>();

    /** Current output buffer */
    private ByteBuffer outputBuffer;

    /** Deferred message */
    private Message deferredMessage;

    /** Disconnect peer */
    private boolean disconnectPeer;

    /** Connected status */
    private boolean connected;

    /** Peer protocol version */
    private int version;

    /** Peer services */
    private long services;

    /** User agent */
    private String userAgent;

    /** Peer chain height */
    private int chainHeight;

    /** Version handshake count */
    private int versionCount;

    /** Current ban score */
    private int banScore;

    /** Relay blocks */
    private boolean relayBlocks;

    /** Relay transactions */
    private boolean relayTransactions;

    /** Incomplete GetBlocks response */
    private boolean incompleteResponse;

    /** Ping sent */
    private boolean pingSent;

    /**
     * Creates a new peer
     *
     * @param       address         The network address for this peer
     * @param       channel         The socket channel for this peer
     * @param       key             The selection key for this peer
     */
    public Peer(PeerAddress address, SocketChannel channel, SelectionKey key) {
        this.address = address;
        this.channel = channel;
        this.key = key;
    }

    /**
     * Returns the peer address
     *
     * @return      Peer address
     */
    public PeerAddress getAddress() {
        return address;
    }

    /**
     * Returns the socket channel
     *
     * @return      Socket channel
     */
    public SocketChannel getChannel() {
        return channel;
    }

    /**
     * Returns the selection key
     *
     * @return      Selection key
     */
    public SelectionKey getKey() {
        return key;
    }

    /**
     * Returns the input message count
     *
     * @return      Message count
     */
    public int getInputCount() {
        return inputCount;
    }

    /**
     * Sets the input message count
     *
     * @param       messageCount        The new message count
     */
    public void setInputCount(int messageCount) {
        this.inputCount = messageCount;
    }

    /**
     * Returns the current input buffer
     *
     * @return      Input buffer
     */
    public ByteBuffer getInputBuffer() {
        return inputBuffer;
    }

    /**
     * Sets the current input buffer
     *
     * @param       buffer              The new input buffer
     */
    public void setInputBuffer(ByteBuffer buffer) {
        this.inputBuffer = buffer;
    }

    /**
     * Returns the output message list
     *
     * @return      Output message list
     */
    public List<Message> getOutputList() {
        return outputList;
    }

    /**
     * Returns the current output buffer
     *
     * @return      Output buffer or null if there is no buffer
     */
    public ByteBuffer getOutputBuffer() {
        return outputBuffer;
    }

    /**
     * Sets the current output buffer
     *
     * @param       buffer              The new output buffer
     */
    public void setOutputBuffer(ByteBuffer buffer) {
        this.outputBuffer = buffer;
    }

    /**
     * Returns the deferred message
     *
     * @return      Deferred message or null if there is no message
     */
    public Message getDeferredMessage() {
        return deferredMessage;
    }

    /**
     * Sets the deferred message
     *
     * @param       deferredMessage     The deferred message
     */
    public void setDeferredMessage(Message deferredMessage) {
        this.deferredMessage = deferredMessage;
    }

    /**
     * Set the Bloom filter for this peer
     *
     * @param       filter              Bloom filter
     */

    public void setBloomFilter(BloomFilter filter) {
        this.bloomFilter = filter;
    }

    /**
     * Returns the Bloom filter for this peer
     *
     * @return      Bloom filter or null if there is no filter
     */
    public BloomFilter getBloomFilter() {
        return bloomFilter;
    }

    /**
     * Set connected state
     *
     * @param       connected           TRUE if the peer is connected
     */
    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    /** Checks if this peer is connected
     *
     * @return      TRUE if the peer is connected
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Checks if we should disconnect the peer
     *
     * @return      TRUE if peer should be disconnected
     */
    public boolean shouldDisconnect() {
        return disconnectPeer;
    }

    /**
     * Sets the peer disconnect status
     *
     * @param       disconnect          TRUE to disconnect the peer
     */
    public void setDisconnect(boolean disconnect) {
        this.disconnectPeer = disconnect;
    }

    /**
     * Sets the peer version
     *
     * @param       version             Peer protocol version
     */
    public void setVersion(int version) {
        this.version = version;
    }

    /**
     * Gets the peer version
     *
     * @return      Peer version
     */
    public int getVersion() {
        return version;
    }

    /**
     * Sets the ban score for this peer
     *
     * @param       banScore            New ban score
     */
    public void setBanScore(int banScore) {
        this.banScore = banScore;
    }

    /**
     * Returns the current ban score for this peer
     *
     * @return      Current ban score
     */
    public int getBanScore() {
        return banScore;
    }

    /**
     * Sets the peer services
     *
     * @param       services            Peer services
     */
    public void setServices(long services) {
        this.services = services;
    }

    /**
     * Returns the peer services
     *
     * @return      Peer services
     */
    public long getServices() {
        return services;
    }

    /**
     * Sets the user agent
     *
     * @param       userAgent           User agent
     */
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    /**
     * Returns the user agent
     *
     * @return      User agent
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * Sets the chain height
     *
     * @param       chainHeight         Chain height
     */
    public void setHeight(int chainHeight) {
        this.chainHeight = chainHeight;
    }

    /**
     * Returns the chain height
     *
     * @return      Chain height
     */
    public int getHeight() {
        return chainHeight;
    }

    /**
     * Returns the version handshake count
     *
     * @return      Version handshake count
     */
    public int getVersionCount() {
        return versionCount;
    }

    /**
     * Increments the version handshake count
     */
    public void incVersionCount() {
        versionCount++;
    }

    /**
     * Sets transaction relay
     *
     * @param       relay               TRUE if transactions should be relayed
     */
    public void setTxRelay(boolean relay) {
        this.relayTransactions = relay;
    }

    /**
     * Checks if transactions should be relayed
     *
     * @return      TRUE if transactions should be relayed
     */
    public boolean shouldRelayTx() {
        return relayTransactions;
    }

    /**
     * Sets block relay
     *
     * @param       relay               TRUE if blocks should be relayed
     */
    public void setBlockRelay(boolean relay) {
        this.relayBlocks = relay;
    }

    /**
     * Checks if blocks should be relayed
     *
     * @return      TRUE if blocks should be relayed
     */
    public boolean shouldRelayBlocks() {
        return relayBlocks;
    }

    /**
     * Sets the incomplete response flag
     *
     * @param       incomplete           TRUE if response was incomplete
     */
    public void setIncomplete(boolean incomplete) {
        this.incompleteResponse = incomplete;
    }

    /**
     * Checks if the previous response was incomplete
     *
     * @return      TRUE if the response was incomplete
     */
    public boolean isIncomplete() {
        return incompleteResponse;
    }

    /**
     * Checks if a 'ping' message has been sent to this peer
     *
     * @return      TRUE if a ping has been sent
     */
    public boolean wasPingSent() {
        return pingSent;
    }

    /**
     * Sets the ping status for this peer
     *
     * @param       pingSent        TRUE if a ping has been sent
     */
    public void setPing(boolean pingSent) {
        this.pingSent = pingSent;
    }
}
