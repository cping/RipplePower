package org.ripple.power.txns.btc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WalletMessageListener extends AbstractMessageListener {

	@Override
	public void sendInventory(Message msg, List<InventoryItem> invList) {
		Peer peer = msg.getPeer();
		List<InventoryItem> notFoundList = new ArrayList<>(invList.size());
		for (InventoryItem item : invList) {
			switch (item.getType()) {
			case InventoryItem.INV_TX:
				try {
					SendTransaction sendTx = BTCLoader.wallet.getSendTx(item
							.getHash());
					if (sendTx != null) {
						Message txMsg = TransactionMessage
								.buildTransactionMessage(peer,
										sendTx.getTxData());
						BTCLoader.networkHandler.sendMessage(txMsg);
						BTCLoader.info(String.format(
								"Transaction sent to peer %s\n  Tx %s", peer
										.getAddress().toString(), item
										.getHash().toString()));
					} else {
						BTCLoader.debug(String.format(
								"Requested transaction not found\n  Tx %s",
								item.getHash().toString()));
						notFoundList.add(item);
					}
				} catch (WalletException exc) {
					BTCLoader.error("Unable to retrieve wallet transaction",
							exc);
					notFoundList.add(item);
				}
				break;
			default:
				notFoundList.add(item);
			}
		}
		if (!notFoundList.isEmpty()) {
			Message invMsg = NotFoundMessage.buildNotFoundMessage(peer,
					notFoundList);
			BTCLoader.networkHandler.sendMessage(invMsg);
		}
	}

	@Override
	public void requestInventory(Message msg, List<InventoryItem> invList) {
		Peer peer = msg.getPeer();
		for (InventoryItem item : invList) {
			try {
				switch (item.getType()) {
				case InventoryItem.INV_TX:
					if (BTCLoader.wallet.isNewTransaction(item.getHash())) {
						PeerRequest request = new PeerRequest(item.getHash(),
								InventoryItem.INV_TX, peer);
						synchronized (BTCLoader.lock) {
							if (!BTCLoader.pendingRequests.contains(request)
									&& !BTCLoader.processedRequests
											.contains(request))
								BTCLoader.pendingRequests.add(request);
						}
					}
					break;
				case InventoryItem.INV_BLOCK:
					if (BTCLoader.wallet.isNewBlock(item.getHash())
							|| BTCLoader.networkChainHeight > BTCLoader.wallet
									.getChainHeight()) {
						PeerRequest request = new PeerRequest(item.getHash(),
								InventoryItem.INV_FILTERED_BLOCK, peer);
						synchronized (BTCLoader.lock) {
							if (!BTCLoader.pendingRequests.contains(request)
									&& !BTCLoader.processedRequests
											.contains(request))
								BTCLoader.pendingRequests.add(request);
						}
					}
					break;
				}
			} catch (WalletException exc) {
				BTCLoader.error("Unable to check wallet status", exc);
			}

		}

	}

	@Override
	public void requestNotFound(Message msg, List<InventoryItem> invList) {
		for (InventoryItem item : invList) {
			synchronized (BTCLoader.lock) {
				Iterator<PeerRequest> it = BTCLoader.processedRequests
						.iterator();
				while (it.hasNext()) {
					PeerRequest request = it.next();
					if (request.getType() == item.getType()
							&& request.getHash().equals(item.getHash())) {
						it.remove();
						BTCLoader.pendingRequests.add(request);
						break;
					}
				}
			}
		}
	}

	@Override
	public void processAddresses(Message msg, List<PeerAddress> addresses) {
		synchronized (BTCLoader.lock) {
			for (PeerAddress addr : addresses) {
				PeerAddress chkAddr = BTCLoader.peerMap.get(addr);
				if (chkAddr != null) {
					chkAddr.setTimeStamp(addr.getTimeStamp());
				} else {
					BTCLoader.peerAddresses.add(0, addr);
					BTCLoader.peerMap.put(addr, addr);
				}

			}
		}

	}

	@Override
	public void processAlert(Message msg, Alert alert) {
		if (alert.getExpireTime() > System.currentTimeMillis() / 1000){
			BTCLoader.warn(String.format("**** Alert %d ****\n  %s",
					alert.getID(), alert.getMessage()));
		}
	}

	@Override
    public void processBlockHeaders(Message msg, List<BlockHeader> hdrList) {
        for(BlockHeader header : hdrList){
            try {
                BTCLoader.databaseQueue.put(header);
            } catch (InterruptedException exc) {
                BTCLoader.error("Thread interrupted while adding to database handler queue", exc);
            }
        
        }
    }

	@Override
	public void processGetAddress(Message msg) {
		List<PeerAddress> addresses;
		synchronized (BTCLoader.lock) {
			addresses = new ArrayList<>(BTCLoader.peerAddresses);
		}
		Message addrMsg = AddressMessage.buildAddressMessage(msg.getPeer(),
				addresses, null);
		BTCLoader.networkHandler.sendMessage(addrMsg);
	}

	@Override
	public void processMerkleBlock(Message msg, BlockHeader blkHeader) {
		try {
			requestCompleted(InventoryItem.INV_FILTERED_BLOCK,
					blkHeader.getHash());
			BTCLoader.databaseQueue.put(blkHeader);
		} catch (InterruptedException exc) {
			BTCLoader
					.error("Thread interrupted while adding to database handler queue",
							exc);
		}
	}

	@Override
	public void processPing(Message msg, long nonce) {
		Message pongMsg = PongMessage.buildPongMessage(msg.getPeer(), nonce);
		BTCLoader.networkHandler.sendMessage(pongMsg);
	}

	@Override
	public void processPong(Message msg, long nonce) {
		Peer peer = msg.getPeer();
		peer.setPing(false);
		BTCLoader.info(String.format("'pong' response received from %s", peer
				.getAddress().toString()));
	}

	@Override
	public void processReject(Message msg, String cmd, int reasonCode,
			String description, Sha256Hash hash) {
		String reason = RejectMessage.reasonCodes.get(reasonCode);
		if (reason == null)
			reason = "N/A";
		BTCLoader.error(String.format(
				"Message rejected by %s\n  Command %s, Reason %s - %s\n  %s",
				msg.getPeer().getAddress().toString(), cmd, reason,
				description, hash.toString()));
	}

	@Override
	public void processTransaction(Message msg, Transaction tx) {
		try {
			requestCompleted(InventoryItem.INV_TX, tx.getHash());
			BTCLoader.databaseQueue.put(tx);
		} catch (InterruptedException exc) {
			BTCLoader
					.error("Thread interrupted while adding to database handler queue",
							exc);
		}
	}

	@Override
	public void processVersion(Message msg, PeerAddress localAddress) {
		Peer peer = msg.getPeer();
		if ((peer.getServices() & NetParams.NODE_NETWORK) == 0) {
			peer.setDisconnect(true);
			BTCLoader.info(String.format("Connection rejected from %s", peer
					.getAddress().toString()));
		} else {
			peer.incVersionCount();
			Message ackMsg = VersionAckMessage.buildVersionAckMessage(peer);
			BTCLoader.networkHandler.sendMessage(ackMsg);
			BTCLoader
					.info(String
							.format("Peer %s: Protocol level %d, Services %d, Agent %s, Height %d",
									peer.getAddress().toString(),
									peer.getVersion(), peer.getServices(),
									peer.getUserAgent(), peer.getHeight()));
		}
	}

	@Override
	public void processVersionAck(Message msg) {
		msg.getPeer().incVersionCount();
	}

	private void requestCompleted(int type, Sha256Hash hash) {
		synchronized (BTCLoader.lock) {
			Iterator<PeerRequest> it = BTCLoader.processedRequests.iterator();
			while (it.hasNext()) {
				PeerRequest request = it.next();
				if (request.getType() == type && request.getHash().equals(hash)) {
					it.remove();
					break;
				}
			}
		}
	}
}
