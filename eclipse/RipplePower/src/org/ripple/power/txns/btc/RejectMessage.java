package org.ripple.power.txns.btc;

import java.io.EOFException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.ripple.power.Helper;

/**
 * <p>
 * A 'reject' message is sent when the receiver rejects a message. The message
 * contains a reason code and text description for the rejection. There is no
 * response to the message - it is merely a diagnostic aid. However, the sender
 * may disconnect if too many rejections occur.
 * </p>
 *
 * <p>
 * Reject Message
 * </p>
 * 
 * <pre>
 *   Size       Field           Description
 *   ====       =====           ===========
 *   VarString  Command         The failing command
 *   1 byte     Reason          The reason code
 *   VarString  Description     Descriptive text
 *   32 bytes   Hash            Block hash ('block') or transaction hash ('tx'), omitted otherwise
 * </pre>
 */
public class RejectMessage {

	/** Malformed message */
	public static final int REJECT_MALFORMED = 0x01;

	/** Invalid message */
	public static final int REJECT_INVALID = 0x10;

	/** Obsolete message */
	public static final int REJECT_OBSOLETE = 0x11;

	/** Duplicate transaction */
	public static final int REJECT_DUPLICATE = 0x12;

	/** Non-standard transaction */
	public static final int REJECT_NONSTANDARD = 0x40;

	/** Dust transaction */
	public static final int REJECT_DUST = 0x41;

	/** Insufficient fee provided */
	public static final int REJECT_INSUFFICIENT_FEE = 0x42;

	/** Block checkpoint mismatch */
	public static final int REJECT_CHECKPOINT = 0x43;

	/** Reject message reason code mappings */
	public static final Map<Integer, String> reasonCodes = new HashMap<>();
	static {
		reasonCodes.put(REJECT_MALFORMED, "Malformed");
		reasonCodes.put(REJECT_INVALID, "Invalid");
		reasonCodes.put(REJECT_OBSOLETE, "Obsolete");
		reasonCodes.put(REJECT_DUPLICATE, "Duplicate");
		reasonCodes.put(REJECT_NONSTANDARD, "Nonstandard");
		reasonCodes.put(REJECT_DUST, "Dust");
		reasonCodes.put(REJECT_INSUFFICIENT_FEE, "Insufficient fee");
		reasonCodes.put(REJECT_CHECKPOINT, "Checkpoint");
	}

	/**
	 * Builds a 'reject' message to be sent to the destination peer
	 *
	 * @param peer
	 *            Destination peer
	 * @param cmd
	 *            Failing command
	 * @param reason
	 *            Reason code
	 * @param description
	 *            Descriptive text
	 * @return 'reject' message
	 */
	public static Message buildRejectMessage(Peer peer, String cmd, int reason, String description) {
		return buildRejectMessage(peer, cmd, reason, description, Sha256Hash.ZERO_HASH);
	}

	/**
	 * Builds a 'reject' message to be sent to the destination peer
	 *
	 * @param peer
	 *            Destination peer
	 * @param cmd
	 *            Failing command
	 * @param reason
	 *            Reason code
	 * @param desc
	 *            Descriptive text
	 * @param hash
	 *            Block or transaction hash
	 * @return 'reject' message
	 */
	public static Message buildRejectMessage(Peer peer, String cmd, int reason, String desc, Sha256Hash hash) {
		//
		// Build the message data
		//
		SerializedBuffer msgBuffer = new SerializedBuffer();
		msgBuffer.putString(cmd).putUnsignedByte(reason).putString(desc);
		if (!hash.equals(Sha256Hash.ZERO_HASH))
			msgBuffer.putBytes(Helper.reverseBytes(hash.getBytes()));
		//
		// Build the message
		//
		ByteBuffer buffer = MessageHeader.buildMessage("reject", msgBuffer);
		return new Message(buffer, peer, MessageHeader.MessageCommand.REJECT);
	}

	/**
	 * Processes a 'reject' message
	 *
	 * @param msg
	 *            Message
	 * @param inBuffer
	 *            Input buffer
	 * @param msgListener
	 *            Message listener
	 * @throws EOFException
	 *             Serialized byte stream is too short
	 */
	public static void processRejectMessage(Message msg, SerializedBuffer inBuffer, MessageListener msgListener)
			throws EOFException {
		//
		// Get the command name
		//
		String cmd = inBuffer.getString();
		//
		// Get the reason code
		//
		int reasonCode = inBuffer.getUnsignedByte();
		//
		// Get the description
		//
		String desc = inBuffer.getString();
		//
		// Get the hash
		//
		Sha256Hash hash = Sha256Hash.ZERO_HASH;
		if (inBuffer.available() >= 32)
			hash = new Sha256Hash(Helper.reverseBytes(inBuffer.getBytes(32)));
		//
		// Notify the message listener
		//
		msgListener.processReject(msg, cmd, reasonCode, desc, hash);
	}
}
