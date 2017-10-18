package org.ripple.power.txns.btc;

import java.io.EOFException;
import java.nio.ByteBuffer;

/**
 * <p>
 * Pong Message
 * </p>
 * 
 * <pre>
 *   Size       Field               Description
 *   ====       =====               ===========
 *   8 bytes    Nonce               Random value from ping message
 * </pre>
 * 
 * *
 */
public class PongMessage {

	/**
	 * Send a 'pong' message to a peer
	 *
	 * @param peer
	 *            Destination peer
	 * @param nonce
	 *            Nonce from the 'ping' message
	 * @return 'pong' message
	 */
	public static Message buildPongMessage(Peer peer, long nonce) {
		//
		// Build the message data
		//
		SerializedBuffer msgBuffer = new SerializedBuffer(8).putLong(nonce);
		//
		// Build the message
		//
		ByteBuffer buffer = MessageHeader.buildMessage("pong", msgBuffer);
		return new Message(buffer, peer, MessageHeader.MessageCommand.PONG);
	}

	/**
	 * Process a 'pong'
	 *
	 * @param msg
	 *            Message
	 * @param inBuffer
	 *            Input buffer
	 * @param msgListener
	 *            Message listener
	 * @throws EOFException
	 *             End-of-data while processing input stream
	 */
	public static void processPongMessage(Message msg, SerializedBuffer inBuffer, MessageListener msgListener)
			throws EOFException {
		//
		// Get the nonce from the 'pong' message
		//
		long nonce = inBuffer.getLong();
		//
		// Notify the message listener
		//
		msgListener.processPong(msg, nonce);
	}
}
