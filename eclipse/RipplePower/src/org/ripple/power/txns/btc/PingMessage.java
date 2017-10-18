package org.ripple.power.txns.btc;

import java.io.EOFException;
import java.nio.ByteBuffer;

/**
 * <p>
 * A 'ping' message is sent to test network connectivity to a node. Upon
 * receiving a ping, the node responds with a pong.
 * </p>
 *
 * <p>
 * Ping Message
 * </p>
 * 
 * <pre>
 *   Size       Field               Description
 *   ====       =====               ===========
 *   8 bytes    Nonce               Random value
 * </pre>
 */
public class PingMessage {

	/**
	 * Send a 'ping' message to a peer
	 *
	 * @param peer
	 *            Destination peer
	 * @return 'ping' message
	 */
	public static Message buildPingMessage(Peer peer) {
		//
		// We will use the current time as the nonce
		//
		SerializedBuffer msgBuffer = new SerializedBuffer(8).putLong(System.currentTimeMillis());
		//
		// Build the message
		//
		ByteBuffer buffer = MessageHeader.buildMessage("ping", msgBuffer);
		return new Message(buffer, peer, MessageHeader.MessageCommand.PING);
	}

	/**
	 * Process a 'ping' message
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
	public static void processPingMessage(Message msg, SerializedBuffer inBuffer, MessageListener msgListener)
			throws EOFException {
		//
		// BIP0031 adds the 'pong' message and requires an 8-byte nonce in the
		// 'ping'
		// message. If we receive a 'ping' without a payload, we do not return a
		// 'pong' since the client has not implemented BIP0031.
		//
		if (inBuffer.available() >= 8) {
			//
			// Get the nonce from the 'ping' message
			//
			long nonce = inBuffer.getLong();
			//
			// Notify the message listener
			//
			msgListener.processPing(msg, nonce);
		}
	}
}
