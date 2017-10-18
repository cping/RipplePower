package org.ripple.power.txns.btc;

import java.io.EOFException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.ripple.power.Helper;

/**
 * <p>
 * The 'getheaders' request returns a list of block headers. This is similar to
 * the 'getblocks' request and is used by SPV clients who don't need the entire
 * block. The response is a 'headers' message containing up to 2000 block
 * headers.
 * </p>
 *
 * <p>
 * GetHeaders Message
 * </p>
 * 
 * <pre>
 *   Size       Field               Description
 *   ====       =====               ===========
 *   4 bytes    Version             Negotiated protocol version
 *   VarInt     Count               Number of locator hash entries
 *   Variable   Entries             Locator hash entries
 *  32 bytes    Stop                Hash of the last desired block or zero to get as many as possible
 * </pre>
 */
public class GetHeadersMessage {

	/**
	 * Build a 'getheaderss' message
	 *
	 * @param peer
	 *            Destination peer
	 * @param blockList
	 *            Block hash list
	 * @param stopBlock
	 *            Stop block (Sha256Hash.ZERO_HASH to return all blocks)
	 * @return 'getheaders' message
	 */
	public static Message buildGetHeadersMessage(Peer peer, List<Sha256Hash> blockList, Sha256Hash stopBlock) {

		//
		// Build the message payload
		//
		// The protocol version will be set to the lesser of our version and the
		// peer version
		//
		SerializedBuffer msgBuffer = new SerializedBuffer(blockList.size() * 32 + 40);
		msgBuffer.putInt(Math.min(peer.getVersion(), NetParams.PROTOCOL_VERSION)).putVarInt(blockList.size());
		for (Sha256Hash hash : blockList) {
			msgBuffer.putBytes(Helper.reverseBytes(hash.getBytes()));
		}
		msgBuffer.putBytes(Helper.reverseBytes(stopBlock.getBytes()));
		//
		// Build the message
		//
		ByteBuffer buffer = MessageHeader.buildMessage("getheaders", msgBuffer);
		return new Message(buffer, peer, MessageHeader.MessageCommand.GETHEADERS);
	}

	/**
	 * Process the 'getheaders' message
	 *
	 * @param msg
	 *            Message
	 * @param inBuffer
	 *            Input buffer
	 * @param msgListener
	 *            Message listener
	 * @throws EOFException
	 *             Message stream is too short
	 * @throws VerificationException
	 *             Message verification failed
	 */
	public static void processGetHeadersMessage(Message msg, SerializedBuffer inBuffer, MessageListener msgListener)
			throws EOFException, VerificationException {
		//
		// Get the protocol version
		//
		int version = inBuffer.getInt();
		if (version < NetParams.MIN_PROTOCOL_VERSION)
			throw new VerificationException(String.format("Protocol version %d is not supported", version));
		//
		// Get the locator entries
		//
		int count = inBuffer.getVarInt();
		if (count < 0 || count > 500)
			throw new VerificationException(String.format("'getheaders' message contains more than 500 locators"));
		List<Sha256Hash> blockList = new ArrayList<>(count);
		for (int i = 0; i < count; i++)
			blockList.add(new Sha256Hash(Helper.reverseBytes(inBuffer.getBytes(32))));
		Sha256Hash stopBlock = new Sha256Hash(Helper.reverseBytes(inBuffer.getBytes(32)));
		//
		// Notify the message listener
		//
		msgListener.processGetHeaders(msg, version, blockList, stopBlock);
	}
}
