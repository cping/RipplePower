package org.ripple.power.txns.btc;

import java.io.EOFException;

public class MessageHandler implements Runnable {

	private Thread handlerThread;

	private boolean handlerShutdown = false;

	public MessageHandler() {
	}

	public void shutdown() {
		handlerShutdown = true;
		handlerThread.interrupt();
	}

	@Override
	public void run() {
		BTCLoader.info("Message handler started");
		handlerThread = Thread.currentThread();
		try {
			while (!handlerShutdown) {
				Message msg = BTCLoader.messageQueue.take();
				processMessage(msg);
			}
		} catch (InterruptedException exc) {
			if (!handlerShutdown)
				BTCLoader.warn("Message handler interrupted", exc);
		} catch (Exception exc) {
			BTCLoader.error("Exception while processing messages", exc);
		}
		BTCLoader.info("Message handler stopped");
	}

	private void processMessage(Message msg) throws InterruptedException {
		Peer peer = msg.getPeer();
		PeerAddress address = peer.getAddress();
		int reasonCode = 0;
		try {
			MessageProcessor.processMessage(msg, BTCLoader.messageListener);
			msg.setBuffer(null);
		} catch (EOFException exc) {
			MessageHeader.MessageCommand cmdOp = msg.getCommand();
			String cmdName = (cmdOp != null ? cmdOp.toString().toLowerCase()
					: "N/A");
			BTCLoader.error(String.format(
					"End-of-data while processing '%s' message from %s",
					cmdName, address.toString()), exc);
			reasonCode = RejectMessage.REJECT_MALFORMED;
			if (cmdOp == MessageHeader.MessageCommand.VERSION)
				peer.setDisconnect(true);
			if (peer.getVersion() >= 70002) {
				Message rejectMsg = RejectMessage.buildRejectMessage(peer,
						cmdName, reasonCode, exc.getMessage());
				msg.setBuffer(rejectMsg.getBuffer());
				msg.setCommand(rejectMsg.getCommand());
			} else {
				msg.setBuffer(null);
			}
		} catch (VerificationException exc) {
			MessageHeader.MessageCommand cmdOp = msg.getCommand();
			String cmdName = (cmdOp != null ? cmdOp.toString().toLowerCase()
					: "N/A");
			BTCLoader
					.error(String
							.format("Message verification failed for '%s' message from %s\n  %s\n  %s",
									cmdName, address.toString(),
									exc.getMessage(), exc.getHash().toString()));
			if (cmdOp == MessageHeader.MessageCommand.VERSION)
				peer.setDisconnect(true);
			reasonCode = exc.getReason();
			if (peer.getVersion() >= 70002) {
				Message rejectMsg = RejectMessage.buildRejectMessage(peer,
						cmdName, reasonCode, exc.getMessage(), exc.getHash());
				msg.setBuffer(rejectMsg.getBuffer());
				msg.setCommand(rejectMsg.getCommand());
			} else {
				msg.setBuffer(null);
			}
		}
		synchronized (BTCLoader.lock) {
			BTCLoader.completedMessages.add(msg);
			if (reasonCode == RejectMessage.REJECT_MALFORMED
					|| reasonCode == RejectMessage.REJECT_INVALID) {
				int banScore = peer.getBanScore() + 5;
				peer.setBanScore(banScore);
				if (banScore >= BTCLoader.MAX_BAN_SCORE)
					peer.setDisconnect(true);
			}
		}
		BTCLoader.networkHandler.wakeup();
	}
}
