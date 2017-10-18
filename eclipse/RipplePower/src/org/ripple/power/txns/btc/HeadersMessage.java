package org.ripple.power.txns.btc;

import java.io.EOFException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * The 'headers' message is returned in response to a 'getheaders' message. Note
 * that the returned header includes the block header (80 bytes) plus the
 * transaction count (the count is set to zero)
 * </p>
 *
 * <p>
 * Headers Message
 * </p>
 * 
 * <pre>
 *   Size       Field               Description
 *   ====       =====               ===========
 *   VarInt     Count               Number of headers
 *   Variable   Entries             Header entries
 * </pre>
 */
public class HeadersMessage {

	/**
	 * Build the 'headers' message
	 *
	 * @param peer
	 *            Destination peer
	 * @param hdrList
	 *            List of block headers
	 * @return 'headers' message
	 */
	public static Message buildHeadersMessage(Peer peer, List<BlockHeader> hdrList) {
		SerializedBuffer msgBuffer = new SerializedBuffer(hdrList.size() * (BlockHeader.HEADER_SIZE + 1) + 4);
		//
		// Build the message data
		//
		msgBuffer.putVarInt(hdrList.size());
		for (BlockHeader header : hdrList) {
			header.getBytes(msgBuffer).putVarInt(0);
		}
		//
		// Build the message
		//
		ByteBuffer buffer = MessageHeader.buildMessage("headers", msgBuffer);
		return new Message(buffer, peer, MessageHeader.MessageCommand.HEADERS);
	}

	/**
	 * Process the 'headers' message
	 *
	 * @param msg
	 *            Message
	 * @param inBuffer
	 *            Input buffer
	 * @param msgListener
	 *            Message listener
	 * @throws EOFException
	 *             End-of-data encountered while processing input stream
	 * @throws VerificationException
	 *             Verification error
	 */
	public static void processHeadersMessage(Message msg, SerializedBuffer inBuffer, MessageListener msgListener)
			throws EOFException, VerificationException {
		//
		// Build the block header list
		//
		int count = inBuffer.getVarInt();
		if (count < 0 || count > 2000)
			throw new VerificationException("More than 2000 headers", RejectMessage.REJECT_INVALID);
		List<BlockHeader> hdrList = new ArrayList<>(count);
		for (int i = 0; i < count; i++) {
			hdrList.add(new BlockHeader(inBuffer, true));
			inBuffer.getVarInt();
		}
		//
		// Notify the message listener
		//
		msgListener.processBlockHeaders(msg, hdrList);
	}
}
