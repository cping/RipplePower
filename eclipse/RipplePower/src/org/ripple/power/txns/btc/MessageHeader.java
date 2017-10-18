package org.ripple.power.txns.btc;

import java.io.EOFException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.HashMap;

import org.ripple.power.Helper;

/**
 * <p>
 * Each message on the network consists of the message header followed by an
 * optional payload.
 * </p>
 *
 * <p>
 * Message Header:
 * </p>
 * 
 * <pre>
 *   Size       Field               Description
 *   ====       =====               ===========
 *   4 bytes    Magic               Magic number
 *  12 bytes    Command             Null-terminated ASCII command
 *   4 bytes    Length              Payload length
 *   4 bytes    Checksum            First 4 bytes of the SHA-256 double digest of the payload
 * </pre>
 */
public class MessageHeader {

	/** Message header length */
	public static final int HEADER_LENGTH = 24;

	/** Checksum for zero-length payload */
	public static final byte[] ZERO_LENGTH_CHECKSUM = new byte[] { (byte) 0x5d, (byte) 0xf6, (byte) 0xe0, (byte) 0xe2 };

	/** Message commands */
	public enum MessageCommand {
		ADDR, // 'addr' message
		ALERT, // 'alert' message
		BLOCK, // 'block' message
		FILTERADD, // 'filteradd' message
		FILTERCLEAR, // 'filterclear' message
		FILTERLOAD, // 'filterload' message
		GETADDR, // 'getaddr' message
		GETBLOCKS, // 'getblocks' message
		GETDATA, // 'getdata' message
		GETHEADERS, // 'getheaders' message
		HEADERS, // 'headers' message
		INV, // 'inv' message
		MEMPOOL, // 'mempool' message
		MERKLEBLOCK, // 'merkleblock' message
		NOTFOUND, // 'notfound' message
		PING, // 'ping' message
		PONG, // 'pong' message
		REJECT, // 'reject' message
		TX, // 'tx' message
		VERACK, // 'verack' message
		VERSION // 'version' message
	}

	/** Message command map */
	public static final Map<String, MessageCommand> cmdMap = new HashMap<>();
	static {
		cmdMap.put("addr", MessageCommand.ADDR);
		cmdMap.put("alert", MessageCommand.ALERT);
		cmdMap.put("block", MessageCommand.BLOCK);
		cmdMap.put("filteradd", MessageCommand.FILTERADD);
		cmdMap.put("filterclear", MessageCommand.FILTERCLEAR);
		cmdMap.put("filterload", MessageCommand.FILTERLOAD);
		cmdMap.put("getaddr", MessageCommand.GETADDR);
		cmdMap.put("getblocks", MessageCommand.GETBLOCKS);
		cmdMap.put("getdata", MessageCommand.GETDATA);
		cmdMap.put("getheaders", MessageCommand.GETHEADERS);
		cmdMap.put("headers", MessageCommand.HEADERS);
		cmdMap.put("inv", MessageCommand.INV);
		cmdMap.put("mempool", MessageCommand.MEMPOOL);
		cmdMap.put("merkleblock", MessageCommand.MERKLEBLOCK);
		cmdMap.put("notfound", MessageCommand.NOTFOUND);
		cmdMap.put("ping", MessageCommand.PING);
		cmdMap.put("pong", MessageCommand.PONG);
		cmdMap.put("reject", MessageCommand.REJECT);
		cmdMap.put("tx", MessageCommand.TX);
		cmdMap.put("verack", MessageCommand.VERACK);
		cmdMap.put("version", MessageCommand.VERSION);
	}

	/**
	 * Build the message header and then construct a buffer containing the
	 * message header and the message data
	 *
	 * @param cmd
	 *            Message command
	 * @param msgData
	 *            Message data
	 * @return Message buffer
	 */
	public static ByteBuffer buildMessage(String cmd, SerializedBuffer msgData) {
		return buildMessage(cmd, msgData.toByteArray());
	}

	/**
	 * Build the message header and then construct a buffer containing the
	 * message header and the message data
	 *
	 * @param cmd
	 *            Message command
	 * @param msgBytes
	 *            Message data
	 * @return Message buffer
	 */
	public static ByteBuffer buildMessage(String cmd, byte[] msgBytes) {
		byte[] bytes = new byte[HEADER_LENGTH + msgBytes.length];
		//
		// Set the magic number
		//
		Helper.uint32ToByteArrayLE(NetParams.MAGIC_NUMBER, bytes, 0);
		//
		// Set the command name (single-byte ASCII characters)
		//
		for (int i = 0; i < cmd.length(); i++)
			bytes[4 + i] = (byte) cmd.codePointAt(i);
		//
		// Set the payload length
		//
		Helper.uint32ToByteArrayLE(msgBytes.length, bytes, 16);
		//
		// Compute the payload checksum
		//
		// The message header contains a fixed checksum value when there is no
		// payload
		//
		if (msgBytes.length == 0) {
			System.arraycopy(ZERO_LENGTH_CHECKSUM, 0, bytes, 20, 4);
		} else {
			byte[] digest = Helper.doubleDigest(msgBytes);
			System.arraycopy(digest, 0, bytes, 20, 4);
			System.arraycopy(msgBytes, 0, bytes, 24, msgBytes.length);
		}
		return ByteBuffer.wrap(bytes);
	}

	/**
	 * Processes the message header and returns the message command. A
	 * VerificationException is thrown if the message header is incomplete, has
	 * an incorrect magic value, or the checksum is not correct.
	 *
	 * @param msgBuffer
	 *            Message buffer
	 * @return Message command
	 * @throws EOFException
	 *             End-of-data processing stream
	 * @throws VerificationException
	 *             Message verification failed
	 */
	public static MessageCommand processMessage(SerializedBuffer msgBuffer) throws EOFException, VerificationException {
		//
		// Get the message bytes
		//
		byte[] msgBytes;
		if (msgBuffer.getBufferStart() == 0)
			msgBytes = msgBuffer.array();
		else
			msgBytes = msgBuffer.getBytes(msgBuffer.available());
		if (msgBytes.length < HEADER_LENGTH)
			throw new EOFException("Message header is too short");
		msgBuffer.setPosition(HEADER_LENGTH);
		//
		// Verify the magic number
		//
		long magic = Helper.readUint32LE(msgBytes, 0);
		if (magic != NetParams.MAGIC_NUMBER)
			throw new VerificationException(String.format("Message header magic number %d is invalid", magic));
		//
		// Verify the payload checksum
		//
		if (msgBytes.length > HEADER_LENGTH) {
			byte[] digest = Helper.doubleDigest(msgBytes, HEADER_LENGTH, msgBytes.length - HEADER_LENGTH);
			if (digest[0] != msgBytes[20] || digest[1] != msgBytes[21] || digest[2] != msgBytes[22]
					|| digest[3] != msgBytes[23])
				throw new VerificationException("Message checksum incorrect");
		}
		//
		// Build the command name
		//
		StringBuilder cmdString = new StringBuilder(16);
		for (int i = 4; i < 16; i++) {
			if (msgBytes[i] == 0)
				break;
			cmdString.appendCodePoint(((int) msgBytes[i]) & 0xff);
		}
		String cmd = cmdString.toString();
		//
		// Get the message command
		//
		MessageCommand cmdOp = cmdMap.get(cmd);
		if (cmdOp == null)
			throw new VerificationException(String.format("Message '%s' is not supported", cmd));
		return cmdOp;
	}
}
