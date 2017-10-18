package org.ripple.power.txns.btc;

import java.io.EOFException;
import java.nio.ByteBuffer;

/**
 * The 'block' message consists of a single serialized block.
 */
public class BlockMessage {

	/**
	 * Build a 'block' message
	 *
	 * @param peer
	 *            The destination peer or null for a broadcast message
	 * @param block
	 *            Block to be sent to the peer
	 * @return 'block' message
	 */
	public static Message buildBlockMessage(Peer peer, Block block) {
		ByteBuffer buffer = MessageHeader.buildMessage("block", block.getBytes());
		return new Message(buffer, peer, MessageHeader.MessageCommand.BLOCK);
	}

	/**
	 * Build a 'block' message
	 *
	 * @param peer
	 *            The destination peer or null for a broadcast message
	 * @param blockData
	 *            Serialized block
	 * @return 'block' message
	 */
	public static Message buildBlockMessage(Peer peer, byte[] blockData) {
		ByteBuffer buffer = MessageHeader.buildMessage("block", blockData);
		return new Message(buffer, peer, MessageHeader.MessageCommand.BLOCK);
	}

	/**
	 * Process a 'block' message
	 *
	 * @param msg
	 *            Message
	 * @param inBuffer
	 *            Input buffer
	 * @param msgListener
	 *            Message listener
	 * @throws EOFException
	 *             End-of-data while processing stream
	 * @throws VerificationException
	 *             Block verification failed
	 */
	public static void processBlockMessage(Message msg, SerializedBuffer inBuffer, MessageListener msgListener)
			throws EOFException, VerificationException {
		//
		// Get the block
		//
		Block block = new Block(inBuffer, true);
		//
		// Notify the message listener
		//
		msgListener.processBlock(msg, block);
	}
}
