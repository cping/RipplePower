package org.ripple.power.txns.btc;

import java.nio.ByteBuffer;

/**
 * The 'filterclear' message clear the bloom filter for the client.
 *
 * The message consists of just the message header.
 */
public class FilterClearMessage {

    /**
     * Build a 'filterclear' message
     *
     * @param       peer            Destination peer
     * @return                      'filterclear' message
     */
    public static Message buildFilterClearMessage(Peer peer) {
        ByteBuffer buffer = MessageHeader.buildMessage("filterclear", new byte[0]);
        return new Message(buffer, peer, MessageHeader.MessageCommand.FILTERCLEAR);
    }

    /**
     * Process a 'filterclear' message
     *
     * The existing Bloom filter will be cleared
     *
     * @param       msg             Message
     * @param       inBuffer        Input buffer
     * @param       msgListener     Message listener
     */
    public static void processFilterClearMessage(Message msg, SerializedBuffer inBuffer, MessageListener msgListener) {
        Peer peer = msg.getPeer();
        //
        // Clear the current Bloom filter
        //
        BloomFilter oldFilter;
        synchronized(peer) {
            oldFilter = peer.getBloomFilter();
            peer.setBloomFilter(null);
        }
        //
        // Notify the message listener
        //
        msgListener.processFilterClear(msg, oldFilter);
    }
}
