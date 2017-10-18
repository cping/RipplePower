package org.ripple.power.txns.btc;

import java.io.EOFException;

/**
 * MessageProcessor will process a message received from a peer. It takes a
 * Message, validates the message header and then calls the appropriate message
 * processing method based on the message command.
 *
 * Before calling this routine, the message ByteBuffer must contain the message
 * to be processed with the buffer position set to the start of the message and
 * the buffer limit set to the end of the message. Upon return, the appropriate
 * message listener will have been called to handle application-specific tasks
 * for the message.
 */
public class MessageProcessor {

	/**
	 * Process a message
	 *
	 * @param msg
	 *            Message to be processed
	 * @param msgListener
	 *            Application message listener
	 * @throws EOFException
	 *             End-of-data while processing the message
	 * @throws VerificationException
	 *             Message verification failed
	 */
	public static void processMessage(Message msg, MessageListener msgListener)
			throws EOFException, VerificationException {
		//
		// Process the message header and get the command name
		//
		SerializedBuffer inBuffer = new SerializedBuffer(msg.getBuffer());
		MessageHeader.MessageCommand cmdOp = MessageHeader.processMessage(inBuffer);
		msg.setCommand(cmdOp);
		//
		// Process the message
		//
		switch (cmdOp) {
		case ADDR:
			AddressMessage.processAddressMessage(msg, inBuffer, msgListener);
			break;
		case ALERT:
			AlertMessage.processAlertMessage(msg, inBuffer, msgListener);
			break;
		case BLOCK:
			BlockMessage.processBlockMessage(msg, inBuffer, msgListener);
			break;
		case FILTERADD:
			FilterAddMessage.processFilterAddMessage(msg, inBuffer, msgListener);
			break;
		case FILTERCLEAR:
			FilterClearMessage.processFilterClearMessage(msg, inBuffer, msgListener);
			break;
		case FILTERLOAD:
			FilterLoadMessage.processFilterLoadMessage(msg, inBuffer, msgListener);
			break;
		case GETADDR:
			GetAddressMessage.processGetAddressMessage(msg, inBuffer, msgListener);
			break;
		case GETBLOCKS:
			GetBlocksMessage.processGetBlocksMessage(msg, inBuffer, msgListener);
			break;
		case GETDATA:
			GetDataMessage.processGetDataMessage(msg, inBuffer, msgListener);
			break;
		case GETHEADERS:
			GetHeadersMessage.processGetHeadersMessage(msg, inBuffer, msgListener);
			break;
		case HEADERS:
			HeadersMessage.processHeadersMessage(msg, inBuffer, msgListener);
			break;
		case INV:
			InventoryMessage.processInventoryMessage(msg, inBuffer, msgListener);
			break;
		case MEMPOOL:
			MempoolMessage.processMempoolMessage(msg, inBuffer, msgListener);
			break;
		case MERKLEBLOCK:
			MerkleBlockMessage.processMerkleBlockMessage(msg, inBuffer, msgListener);
			break;
		case NOTFOUND:
			NotFoundMessage.processNotFoundMessage(msg, inBuffer, msgListener);
			break;
		case PING:
			PingMessage.processPingMessage(msg, inBuffer, msgListener);
			break;
		case PONG:
			PongMessage.processPongMessage(msg, inBuffer, msgListener);
			break;
		case REJECT:
			RejectMessage.processRejectMessage(msg, inBuffer, msgListener);
			break;
		case TX:
			TransactionMessage.processTransactionMessage(msg, inBuffer, msgListener);
			break;
		case VERACK:
			VersionAckMessage.processVersionAckMessage(msg, inBuffer, msgListener);
			break;
		case VERSION:
			VersionMessage.processVersionMessage(msg, inBuffer, msgListener);
			break;
		}
	}
}
