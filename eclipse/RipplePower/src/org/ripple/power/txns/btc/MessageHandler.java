package org.ripple.power.txns.btc;

import java.io.EOFException;

public class MessageHandler implements Runnable {

	/** Message handler shutdown requested */
	private boolean messageShutdown = false;

	/**
	 * Creates a message handler
	 */
	public MessageHandler() {
	}

	/**
	 * Processes messages and returns responses
	 */
	@Override
	public void run() {
		BTCLoader.info("Message handler started");
		//
		// Process messages until we are shutdown
		//
		try {
			while (true) {
				Message msg = BTCLoader.messageQueue.take();
				if (messageShutdown)
					break;
				processMessage(msg);
			}
		} catch (InterruptedException exc) {
			BTCLoader.warn("Message handler interrupted", exc);
		} catch (Throwable exc) {
			BTCLoader.error("Runtime exception while processing messages", exc);
		}
		//
		// Stopping
		//
		BTCLoader.info("Message handler stopped");
	}

	/**
	 * Shutdown the message handler
	 */
	public void shutdown() {
		try {
			messageShutdown = true;
			BTCLoader.messageQueue.put(new ShutdownMessage());
		} catch (InterruptedException exc) {
			BTCLoader.warn("Message handler shutdown interrupted", exc);
		}
	}

	/**
	 * Process a message and return a response
	 *
	 * @param msg
	 *            Message
	 */
	private void processMessage(Message msg) throws InterruptedException {
		Peer peer = msg.getPeer();
		if (peer == null) {
			BTCLoader.dumpData("Message With No Peer", msg.getBuffer().array());
			return;
		}
		PeerAddress address = peer.getAddress();
		int reasonCode = 0;
		try {
			MessageProcessor.processMessage(msg, BTCLoader.networkMessageListener);
			msg.setBuffer(null);
		} catch (EOFException exc) {
			MessageHeader.MessageCommand cmdOp = msg.getCommand();
			String cmdName = (cmdOp != null ? cmdOp.toString().toLowerCase() : "N/A");
			BTCLoader.error(String.format("End-of-data while processing '%s' message from %s", cmdName, address), exc);
			reasonCode = RejectMessage.REJECT_MALFORMED;
			if (cmdOp == MessageHeader.MessageCommand.TX)
				BTCLoader.txRejected.incrementAndGet();
			else if (cmdOp == MessageHeader.MessageCommand.VERSION)
				peer.setDisconnect(true);
			if (peer.getVersion() >= 70002) {
				Message rejectMsg = RejectMessage.buildRejectMessage(peer, cmdName, reasonCode, exc.getMessage());
				msg.setBuffer(rejectMsg.getBuffer());
				msg.setCommand(rejectMsg.getCommand());
			} else {
				msg.setBuffer(null);
			}
		} catch (VerificationException exc) {
			MessageHeader.MessageCommand cmdOp = msg.getCommand();
			String cmdName = (cmdOp != null ? cmdOp.toString().toLowerCase() : "N/A");
			BTCLoader.error(String.format("Message verification failed for '%s' message from %s\n  %s\n  %s", cmdName,
					address, exc.getMessage(), exc.getHash()));
			reasonCode = exc.getReason();
			if (cmdOp == MessageHeader.MessageCommand.TX)
				BTCLoader.txRejected.incrementAndGet();
			else if (cmdOp == MessageHeader.MessageCommand.VERSION)
				peer.setDisconnect(true);
			if (peer.getVersion() >= 70002) {
				Message rejectMsg = RejectMessage.buildRejectMessage(peer, cmdName, reasonCode, exc.getMessage(),
						exc.getHash());
				msg.setBuffer(rejectMsg.getBuffer());
				msg.setCommand(rejectMsg.getCommand());
			} else {
				msg.setBuffer(null);
			}
		}
		//
		// Add the message to the completed message list and wakeup the network
		// listener. We will
		// bump the banscore for the peer if the message was rejected because it
		// was malformed
		// or invalid. We will ban the peer if it is using an obsolete protocol.
		//
		BTCLoader.completedMessages.add(msg);
		int banScore;
		switch (reasonCode) {
		case RejectMessage.REJECT_MALFORMED:
		case RejectMessage.REJECT_INVALID:
			banScore = 5;
			break;
		case RejectMessage.REJECT_OBSOLETE:
			banScore = BTCLoader.MAX_BAN_SCORE;
			break;
		default:
			banScore = 0;
		}
		if (banScore > 0) {
			synchronized (peer) {
				banScore += peer.getBanScore();
				peer.setBanScore(banScore);
				if (banScore >= BTCLoader.MAX_BAN_SCORE)
					peer.setDisconnect(true);
			}
		}
		BTCLoader.networkHandler.wakeup();
	}
}
