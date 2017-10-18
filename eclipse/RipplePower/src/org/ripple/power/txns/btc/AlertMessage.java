package org.ripple.power.txns.btc;

import java.io.EOFException;
import java.nio.ByteBuffer;

/**
 * <p>
 * The 'alert' message is sent out by the development team to notify all peers
 * in the network about a problem. The alert is displayed in the user interface
 * and written to the log. It is also sent each time a node connects to another
 * node until the relay time is exceeded or the alert is canceled.
 * </p>
 *
 * <p>
 * The 'alert' message contains two variable-length byte arrays. The first array
 * is the payload and the second array is the signature. The alert is packaged
 * this way so that a peer at any level can relay the alert even if it doesn't
 * understand the alert format.
 * </p>
 *
 * <p>
 * Alert Message
 * </p>
 * 
 * <pre>
 *   Size       Field           Description
 *   ====       =====           ===========
 *   VarInt     PayloadLength   Length of the payload
 *   Variable   Payload         Alert payload
 *   VarInt     SigLength       Length of the signature
 *   Variable   Signature       Alert signature
 * </pre>
 */
public class AlertMessage {

	/** Public key used to verify an alert */
	private static final byte[] alertPubKey = { (byte) 0x04, (byte) 0xfc, (byte) 0x97, (byte) 0x02, (byte) 0x84,
			(byte) 0x78, (byte) 0x40, (byte) 0xaa, (byte) 0xf1, (byte) 0x95, (byte) 0xde, (byte) 0x84, (byte) 0x42,
			(byte) 0xeb, (byte) 0xec, (byte) 0xed, (byte) 0xf5, (byte) 0xb0, (byte) 0x95, (byte) 0xcd, (byte) 0xbb,
			(byte) 0x9b, (byte) 0xc7, (byte) 0x16, (byte) 0xbd, (byte) 0xa9, (byte) 0x11, (byte) 0x09, (byte) 0x71,
			(byte) 0xb2, (byte) 0x8a, (byte) 0x49, (byte) 0xe0, (byte) 0xea, (byte) 0xd8, (byte) 0x56, (byte) 0x4f,
			(byte) 0xf0, (byte) 0xdb, (byte) 0x22, (byte) 0x20, (byte) 0x9e, (byte) 0x03, (byte) 0x74, (byte) 0x78,
			(byte) 0x2c, (byte) 0x09, (byte) 0x3b, (byte) 0xb8, (byte) 0x99, (byte) 0x69, (byte) 0x2d, (byte) 0x52,
			(byte) 0x4e, (byte) 0x9d, (byte) 0x6a, (byte) 0x69, (byte) 0x56, (byte) 0xe7, (byte) 0xc5, (byte) 0xec,
			(byte) 0xbc, (byte) 0xd6, (byte) 0x82, (byte) 0x84 };

	/**
	 * Create an 'alert' message
	 *
	 * @param peer
	 *            Destination peer or null for a broadcast message
	 * @param alert
	 *            Alert
	 * @return 'alert' message
	 */
	public static Message buildAlertMessage(Peer peer, Alert alert) {
		//
		// Build the message data
		//
		SerializedBuffer msgData = new SerializedBuffer();
		msgData.putVarInt(alert.getPayload().length).putBytes(alert.getPayload()).putVarInt(alert.getSignature().length)
				.putBytes(alert.getSignature());
		//
		// Build the message
		//
		ByteBuffer buffer = MessageHeader.buildMessage("alert", msgData);
		Message msg = new Message(buffer, peer, MessageHeader.MessageCommand.ALERT);
		return msg;
	}

	/**
	 * Process an 'alert' message
	 *
	 * @param msg
	 *            Message
	 * @param inBuffer
	 *            Message buffer
	 * @param msgListener
	 *            Message listener
	 * @throws EOFException
	 *             End-of-data while processing stream
	 * @throws VerificationException
	 *             Message verification failed
	 */
	public static void processAlertMessage(Message msg, SerializedBuffer inBuffer, MessageListener msgListener)
			throws EOFException, VerificationException {
		//
		// Process the message data
		//
		byte[] payload = inBuffer.getBytes();
		byte[] signature = inBuffer.getBytes();
		//
		// Verify the signature
		//
		ECKey ecKey = new ECKey(alertPubKey);
		try {
			if (!ecKey.verifySignature(payload, signature))
				throw new VerificationException("Alert signature is not valid");
		} catch (ECException exc) {
			throw new VerificationException("Alert signature verification failed", exc);
		}
		//
		// Notify the application message listener
		//
		msgListener.processAlert(msg, new Alert(payload, signature));
	}
}
