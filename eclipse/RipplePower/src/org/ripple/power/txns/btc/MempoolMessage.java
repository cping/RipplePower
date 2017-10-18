package org.ripple.power.txns.btc;

import java.nio.ByteBuffer;

/**
 * The 'mempool' message requests a list of transactions in the peer memory
 * pool. The response is an 'inv' message listing the transactions in the pool.
 *
 * The message consists of just the message header.
 */
public class MempoolMessage {

	/**
	 * Build a 'mempool' message
	 *
	 * @param peer
	 *            Destination peer
	 * @return 'mempool' message
	 */
	public static Message buildMempoolMessage(Peer peer) {
		ByteBuffer buffer = MessageHeader.buildMessage("mempool", new byte[0]);
		return new Message(buffer, peer, MessageHeader.MessageCommand.MEMPOOL);
	}

	/**
	 * Process a 'mempool' message
	 *
	 * @param msg
	 *            Message
	 * @param inBuffer
	 *            Input buffer
	 * @param msgListener
	 *            Message listener
	 */
	public static void processMempoolMessage(Message msg, SerializedBuffer inBuffer, MessageListener msgListener) {
		msgListener.requestMemoryPool(msg);
	}
}
