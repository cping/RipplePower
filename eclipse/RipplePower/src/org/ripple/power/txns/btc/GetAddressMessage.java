package org.ripple.power.txns.btc;

import java.nio.ByteBuffer;

/**
 * The 'getaddr' message is sent to a peer to request a list of known peers. The
 * response is an 'addr' message (the application should call
 * AddressMessage.buildAddressMessage() when it receives a 'getaddr' message)
 */
public class GetAddressMessage {

	/**
	 * Build the 'getaddr' message
	 *
	 * @param peer
	 *            The remote peer
	 * @return 'getaddr' message
	 */
	public static Message buildGetAddressMessage(Peer peer) {
		//
		// The 'getaddr' message consists of just the message header
		//
		ByteBuffer buffer = MessageHeader.buildMessage("getaddr", new byte[0]);
		return new Message(buffer, peer, MessageHeader.MessageCommand.GETADDR);
	}

	/**
	 * Process the 'getaddr' message
	 *
	 * @param msg
	 *            Message
	 * @param inBuffer
	 *            Input buffer
	 * @param msgListener
	 *            Message listener
	 */
	public static void processGetAddressMessage(Message msg, SerializedBuffer inBuffer, MessageListener msgListener) {
		//
		// Notify the message listener
		//
		msgListener.processGetAddress(msg);
	}
}
