package org.ripple.power.txns.btc;

import java.io.EOFException;
import java.nio.ByteBuffer;

/**
 * <p>
 * The 'filterload' message supplies a Bloom filter to select transactions of
 * interest to the requester. The requester will be notified when transactions
 * are received that match the supplied filter. The requester can then respond
 * with a 'getdata' message to request Merkle blocks for those transactions.
 * </p>
 *
 * <p>
 * FilterLoad Message
 * </p>
 * 
 * <pre>
 *   Size       Field           Description
 *   ====       =====           ===========
 *   VarInt     byteCount       Number of bytes in the filter (BloomFilter.MAX_FILTER_SIZE)
 *   Variable   filter          Bloom filter
 *   4 bytes    nHashFuncs      Number of hash functions (BloomFilter.MAX_HASH_FUNCS)
 *   4 bytes    nTweak          Random value to add to seed value
 *   1 byte     nFlags          Matching flags
 * </pre>
 */
public class FilterLoadMessage {

	/**
	 * Builds a FilterLoad message
	 *
	 * @param peer
	 *            Destination peer
	 * @param filter
	 *            Bloom filter
	 * @return 'filterload' message
	 */
	public static Message buildFilterLoadMessage(Peer peer, BloomFilter filter) {
		//
		// Build the message
		//
		ByteBuffer buffer = MessageHeader.buildMessage("filterload", filter.getBytes());
		return new Message(buffer, peer, MessageHeader.MessageCommand.FILTERLOAD);
	}

	/**
	 * Creates the Bloom filter
	 *
	 * @param msg
	 *            Message
	 * @param inBuffer
	 *            Input buffer
	 * @param msgListener
	 *            Message listener
	 * @throws EOFException
	 *             End-of-data processing input stream
	 * @throws VerificationException
	 *             Verification error
	 */
	public static void processFilterLoadMessage(Message msg, SerializedBuffer inBuffer, MessageListener msgListener)
			throws EOFException, VerificationException {
		//
		// Load the new bloom filter
		//
		Peer peer = msg.getPeer();
		BloomFilter newFilter = new BloomFilter(inBuffer);
		BloomFilter oldFilter;
		synchronized (peer) {
			oldFilter = peer.getBloomFilter();
			newFilter.setPeer(peer);
			peer.setBloomFilter(newFilter);
		}
		//
		// Notify the message listener
		//
		msgListener.processFilterLoad(msg, oldFilter, newFilter);
	}
}
