package org.ripple.power.txns.btc;

import java.io.EOFException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.ripple.power.Helper;

/**
 * <p>
 * The 'getblocks' message is sent by a peer when it does not have the latest
 * block chain and needs a list of the blocks required to get to the latest
 * block.
 * </p>
 *
 * <p>
 * GetBlocks Message
 * </p>
 * 
 * <pre>
 *   Size       Field               Description
 *   ====       =====               ===========
 *   4 bytes    Version             Negotiated protocol version
 *   VarInt     Count               Number of locator hash entries
 *   Variable   Entries             Locator hash entries
 *   32 bytes   Stop                Hash of the last desired block or zero to get as many as possible
 * </pre>
 */
public class GetBlocksMessage {

	/**
	 * Build a 'getblocks' message
	 *
	 * @param peer
	 *            Destination peer
	 * @param blockList
	 *            Block hash list
	 * @param stopBlock
	 *            Stop block hash (Sha256Hash.ZERO_HASH to get all blocks)
	 * @return 'getblocks' message
	 */
	public static Message buildGetBlocksMessage(Peer peer, List<Sha256Hash> blockList, Sha256Hash stopBlock) {
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
		ByteBuffer buffer = MessageHeader.buildMessage("getblocks", msgBuffer);
		return new Message(buffer, peer, MessageHeader.MessageCommand.GETBLOCKS);
	}

	/**
	 * Process the 'getblocks' message and return an 'inv' message
	 *
	 * @param msg
	 *            Message
	 * @param inBuffer
	 *            Input buffer
	 * @param msgListener
	 *            Message listener
	 * @throws EOFException
	 *             End-of-data processing stream
	 * @throws VerificationException
	 *             Message verification failed
	 */
	public static void processGetBlocksMessage(Message msg, SerializedBuffer inBuffer, MessageListener msgListener)
			throws EOFException, VerificationException {
		//
		// Process the message
		//
		int version = inBuffer.getInt();
		if (version < NetParams.MIN_PROTOCOL_VERSION)
			throw new VerificationException(String.format("Protocol version %d is not supported", version));
		int count = inBuffer.getVarInt();
		if (count < 0 || count > 500)
			throw new VerificationException("More than 500 locator entries in 'getblocks' message");
		List<Sha256Hash> blockList = new ArrayList<>(count);
		for (int i = 0; i < count; i++)
			blockList.add(new Sha256Hash(Helper.reverseBytes(inBuffer.getBytes(32))));
		Sha256Hash stopBlock = new Sha256Hash(Helper.reverseBytes(inBuffer.getBytes(32)));
		//
		// Notify the message listener
		//
		msgListener.processGetBlocks(msg, version, blockList, stopBlock);
	}
}
