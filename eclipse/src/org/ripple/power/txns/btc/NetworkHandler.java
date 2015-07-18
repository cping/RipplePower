package org.ripple.power.txns.btc;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.StandardSocketOptions;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.ripple.power.Helper;

public class NetworkHandler implements Runnable {

	private static final int MAX_OUTBOUND_CONNECTIONS = 4;

	// Bitcoin Network seed nodes (like ripple UNL)
	private static final String[] dnsSeeds = new String[] {
			"dnsseed.bitcoin.dashjr.org", "bitseed.xf2.org",
			"seed.bitcoin.sipa.be", "dnsseed.bluematt.me",
			"seed.bitcoinstats.com", "seed.bitnodes.io" };

	// Test NetWork
	private static final String[] dnsTestSeeds = new String[] {
			"testnet-seed.alexykot.me", "testnet-seed.bitcoin.petertodd.org",
			"testnet-seed.bluematt.me" };

	private Thread handlerThread;

	private Timer timer;

	private int outboundCount;

	private final Selector networkSelector;

	private final List<Peer> connections = new LinkedList<>();

	private final List<ConnectionListener> listeners = new LinkedList<>();

	private long lastPeerUpdateTime;

	private long lastOutboundConnectTime;

	private long lastConnectionCheckTime;

	private boolean networkShutdown = false;

	private boolean staticConnections = false;

	private int getBlocksHeight = -1;

	public NetworkHandler(PeerAddress[] staticAddresses) throws IOException {

		networkSelector = Selector.open();

		if (staticAddresses != null) {
			staticConnections = true;
			for (PeerAddress address : staticAddresses) {
				address.setStatic(true);
				BTCLoader.peerAddresses.add(address);
				BTCLoader.peerMap.put(address, address);
			}
		}
	}

	@Override
	public void run() {
		BTCLoader.info(String.format(
				"Network handler started: Max connections %d",
				MAX_OUTBOUND_CONNECTIONS));
		lastPeerUpdateTime = System.currentTimeMillis() / 1000;
		lastOutboundConnectTime = lastPeerUpdateTime;
		lastConnectionCheckTime = lastPeerUpdateTime;
		handlerThread = Thread.currentThread();

		if (!staticConnections) {
			dnsDiscovery();
		}

		while (!networkShutdown && outboundCount < MAX_OUTBOUND_CONNECTIONS / 2
				&& connections.size() < BTCLoader.peerAddresses.size()) {
			if (!connectOutbound()) {
				break;
			}
		}

		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				wakeup();
			}
		}, 2 * 60 * 1000, 2 * 60 * 1000);

		try {
			while (!networkShutdown)
				processEvents();
		} catch (Exception exc) {
			BTCLoader.error("Exception while processing network events", exc);
		}

		timer.cancel();
		BTCLoader.info("Network handler stopped");
	}

	private void processEvents() {
		int count;
		try {

			count = networkSelector.select();
			if (count > 0 && !networkShutdown) {
				Set<SelectionKey> selectedKeys = networkSelector.selectedKeys();
				Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
				while (keyIterator.hasNext() && !networkShutdown) {
					SelectionKey key = keyIterator.next();
					SelectableChannel channel = key.channel();
					if (channel.isOpen()) {
						if (key.isConnectable()) {
							processConnect(key);
						} else if (key.isReadable()) {
							processRead(key);
						} else if (key.isWritable()) {
							processWrite(key);
						}
					}
					keyIterator.remove();
				}
			}
			if (!networkShutdown) {
				if (!BTCLoader.completedMessages.isEmpty()) {
					processCompletedMessages();
				}
				if (!BTCLoader.pendingRequests.isEmpty()
						|| !BTCLoader.processedRequests.isEmpty()) {
					processRequests();
				}
				long currentTime = System.currentTimeMillis() / 1000;
				if (currentTime > lastPeerUpdateTime + (30 * 60)) {
					synchronized (BTCLoader.lock) {
						Iterator<PeerAddress> iterator = BTCLoader.peerAddresses
								.iterator();
						while (iterator.hasNext()) {
							PeerAddress address = iterator.next();
							if (address.isStatic()) {
								continue;
							}
							long timestamp = address.getTimeStamp();
							if (timestamp < lastPeerUpdateTime) {
								BTCLoader.peerMap.remove(address);
								iterator.remove();
							}
						}
					}
					lastPeerUpdateTime = currentTime;
				}
				if (currentTime > lastConnectionCheckTime + 5 * 60) {
					lastConnectionCheckTime = currentTime;
					List<Peer> inactiveList = new LinkedList<>();
					for (Peer chkPeer : connections) {
						PeerAddress chkAddress = chkPeer.getAddress();
						if (chkAddress.getTimeStamp() < currentTime - 10 * 60) {
							inactiveList.add(chkPeer);
						} else if (chkAddress.getTimeStamp() < currentTime - 5 * 60) {
							if (chkPeer.getVersionCount() < 2) {
								inactiveList.add(chkPeer);
							} else if (!chkPeer.wasPingSent()) {
								chkPeer.setPing(true);
								Message chkMsg = PingMessage
										.buildPingMessage(chkPeer);
								synchronized (BTCLoader.lock) {
									chkPeer.getOutputList().add(chkMsg);
									SelectionKey chkKey = chkPeer.getKey();
									chkKey.interestOps(chkKey.interestOps()
											| SelectionKey.OP_WRITE);
									BTCLoader.info(String.format(
											"'ping' message sent to %s",
											chkAddress.toString()));
								}
							}
						}
					}
					for (Peer chkPeer : inactiveList) {
						BTCLoader.info(String.format(
								"Closing connection due to inactivity: %s",
								chkPeer.getAddress().toString()));
						closeConnection(chkPeer);
						synchronized (BTCLoader.lock) {
							PeerAddress chkAddress = chkPeer.getAddress();
							BTCLoader.peerMap.remove(chkAddress);
							BTCLoader.peerAddresses.remove(chkAddress);
						}

					}

				}
				if (currentTime > lastOutboundConnectTime + 60) {
					lastOutboundConnectTime = currentTime;
					if (outboundCount < MAX_OUTBOUND_CONNECTIONS
							&& connections.size() < BTCLoader.peerAddresses
									.size()) {
						connectOutbound();
					}
				}
			}
		} catch (ClosedChannelException exc) {
			BTCLoader.error("Network channel closed unexpectedly", exc);
		} catch (ClosedSelectorException exc) {
			BTCLoader.error("Network selector closed unexpectedly", exc);
			networkShutdown = true;
		} catch (IOException exc) {
			BTCLoader.error("I/O error while processing selection event", exc);
		}
	}

	public void addListener(ConnectionListener listener) {
		synchronized (BTCLoader.lock) {
			listeners.add(listener);
		}
		for (Peer peer : connections) {
			if (peer.getVersionCount() > 2) {
				listener.connectionStarted(peer);
			}
		}
	}

	public void wakeup() {
		if (Thread.currentThread() != handlerThread) {
			networkSelector.wakeup();
		}
	}

	public void shutdown() {
		networkShutdown = true;
		wakeup();
	}

	public void sendMessage(Message msg) {
		Peer peer = msg.getPeer();
		SelectionKey key = peer.getKey();
		PeerAddress address = peer.getAddress();
		synchronized (BTCLoader.lock) {
			if (address.isConnected()) {
				peer.getOutputList().add(msg);
				key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
			}
		}
		wakeup();
	}

	public void broadcastMessage(Message msg) {
		synchronized (BTCLoader.lock) {
			for (Peer relayPeer : connections) {
				if (relayPeer.getVersionCount() > 2) {
					relayPeer.getOutputList().add(msg.clone(relayPeer));
					SelectionKey relayKey = relayPeer.getKey();
					relayKey.interestOps(relayKey.interestOps()
							| SelectionKey.OP_WRITE);
				}
			}

		}
		wakeup();
	}

	public void getBlocks() {
		if (!BTCLoader.loadingChain
				&& BTCLoader.wallet.getChainHeight() < getBlocksHeight + 50) {
			return;
		}
		Peer peer;
		boolean peerFound = false;
		synchronized (BTCLoader.lock) {
			int index = (int) ((double) connections.size() * Math.random());
			peer = connections.get(index);
			if (peer.getVersionCount() > 2
					&& peer.getHeight() > BTCLoader.wallet.getChainHeight()) {
				peerFound = true;
			} else {
				for (int i = index + 1; i < connections.size(); i++) {
					peer = connections.get(i);
					if (peer.getVersionCount() > 2
							&& peer.getHeight() > BTCLoader.wallet
									.getChainHeight()) {
						peerFound = true;
						break;
					}
				}
			}
			if (!peerFound) {
				for (int i = 0; i < index; i++) {
					peer = connections.get(i);
					if (peer.getVersionCount() > 2
							&& peer.getHeight() > BTCLoader.wallet
									.getChainHeight()) {
						peerFound = true;
						break;
					}
				}
			}
		}
		if (peerFound) {
			Message blocksMsg = buildGetBlocksMessage(peer);
			synchronized (BTCLoader.lock) {
				peer.getOutputList().add(blocksMsg);
				SelectionKey key = peer.getKey();
				key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
			}
			BTCLoader
					.info(String.format(
							"'%s' message sent to %s",
							blocksMsg.getCommand() == MessageHeader.MessageCommand.GETBLOCKS ? "getblocks"
									: "getheaders", peer.getAddress()));
			getBlocksHeight = BTCLoader.wallet.getChainHeight();
		}
		wakeup();
	}

	private boolean connectOutbound() {
		PeerAddress address;
		boolean addressFound = true;
		synchronized (BTCLoader.lock) {
			int index = (int) ((double) BTCLoader.peerAddresses.size() * Math
					.random());
			address = BTCLoader.peerAddresses.get(index);
			if (address.isConnected()
					|| (staticConnections && !address.isStatic())) {
				addressFound = false;
				for (int i = index + 1; i < BTCLoader.peerAddresses.size(); i++) {
					address = BTCLoader.peerAddresses.get(i);
					if (!address.isConnected()
							&& (!staticConnections || address.isStatic())) {
						addressFound = true;
						break;
					}
				}
			}
			if (!addressFound) {
				for (int i = 0; i < index; i++) {
					address = BTCLoader.peerAddresses.get(i);
					if (!address.isConnected()
							&& (!staticConnections || address.isStatic())) {
						addressFound = true;
						break;
					}
				}
			}
		}
		if (!addressFound) {
			return false;
		}
		try {
			SocketChannel channel = SocketChannel.open();
			channel.configureBlocking(false);
			channel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
			channel.bind(null);
			SelectionKey key = channel.register(networkSelector,
					SelectionKey.OP_CONNECT);
			Peer peer = new Peer(address, channel, key);
			key.attach(peer);
			peer.setConnected(true);
			address.setConnected(true);
			channel.connect(address.toSocketAddress());
			outboundCount++;
			synchronized (BTCLoader.lock) {
				connections.add(peer);
			}
		} catch (IOException exc) {
			BTCLoader.error(
					String.format("Unable to open connection to %s",
							address.toString()), exc);
			networkShutdown = true;
		}
		return true;
	}

	private void processConnect(SelectionKey key) {
		Peer peer = (Peer) key.attachment();
		PeerAddress address = peer.getAddress();
		SocketChannel channel = peer.getChannel();
		try {
			channel.finishConnect();
			BTCLoader.info(String.format("Connection established to %s",
					address.toString()));
			Message msg = VersionMessage.buildVersionMessage(peer, null,
					BTCLoader.wallet.getChainHeight());
			synchronized (BTCLoader.lock) {
				peer.getOutputList().add(msg);
				key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
			}
			BTCLoader.info(String.format("Sent 'version' message to %s",
					address.toString()));
		} catch (ConnectException exc) {
			BTCLoader.info(exc.getMessage());
			closeConnection(peer);
			if (!address.isStatic()) {
				synchronized (BTCLoader.lock) {
					if (BTCLoader.peerMap.get(address) != null) {
						BTCLoader.peerAddresses.remove(address);
						BTCLoader.peerMap.remove(address);
					}
				}
			}
		} catch (IOException exc) {
			BTCLoader.error(String.format("Connection failed to %s",
					address.toString()), exc);
			closeConnection(peer);
		}
	}

	private void processRead(SelectionKey key) {
		Peer peer = (Peer) key.attachment();
		PeerAddress address = peer.getAddress();
		SocketChannel channel = peer.getChannel();
		ByteBuffer buffer = peer.getInputBuffer();
		address.setTimeStamp(System.currentTimeMillis() / 1000);
		try {
			int count;
			for (;;) {
				if (buffer == null) {
					buffer = ByteBuffer
							.wrap(new byte[MessageHeader.HEADER_LENGTH]);
					peer.setInputBuffer(buffer);
				}
				if (buffer.position() < buffer.limit()) {
					count = channel.read(buffer);
					if (count <= 0) {
						if (count < 0) {
							closeConnection(peer);
						}
						break;
					}
				}
				if (buffer.position() == buffer.limit()
						&& buffer.limit() == MessageHeader.HEADER_LENGTH) {
					byte[] hdrBytes = buffer.array();
					long magic = Helper.readUint32LE(hdrBytes, 0);
					long length = Helper.readUint32LE(hdrBytes, 16);
					if (magic != NetParams.MAGIC_NUMBER) {
						BTCLoader.error(String.format(
								"Message magic number %X is incorrect", magic));
						BTCLoader.dumpData("Failing Message Header", hdrBytes);
						closeConnection(peer);
						break;
					}
					if (length > NetParams.MAX_MESSAGE_SIZE) {
						BTCLoader.error(String.format(
								"Message length %,d is too large", length));
						closeConnection(peer);
						break;
					}
					if (length > 0) {
						BTCLoader.debug(String.format("Received '%s' message",
								new String(hdrBytes, 4, 12).replace((char) 0,
										' ')));
						byte[] msgBytes = new byte[MessageHeader.HEADER_LENGTH
								+ (int) length];
						System.arraycopy(hdrBytes, 0, msgBytes, 0,
								MessageHeader.HEADER_LENGTH);
						buffer = ByteBuffer.wrap(msgBytes);
						buffer.position(MessageHeader.HEADER_LENGTH);
						peer.setInputBuffer(buffer);
					}
				}
				if (buffer.position() == buffer.limit()) {
					peer.setInputBuffer(null);
					buffer.position(0);
					Message msg = new Message(buffer, peer, null);
					BTCLoader.messageQueue.put(msg);
					break;
				}
			}
		} catch (IOException exc) {
			closeConnection(peer);
		} catch (InterruptedException exc) {
			BTCLoader.warn("Interrupted while processing read request");
			networkShutdown = true;
		}
	}

	private void processWrite(SelectionKey key) {
		Peer peer = (Peer) key.attachment();
		SocketChannel channel = peer.getChannel();
		ByteBuffer buffer = peer.getOutputBuffer();
		try {
			for (;;) {
				if (buffer == null) {
					synchronized (BTCLoader.lock) {
						List<Message> outputList = peer.getOutputList();
						if (outputList.isEmpty()) {
							key.interestOps(key.interestOps()
									& (~SelectionKey.OP_WRITE));
						} else {
							Message msg = outputList.remove(0);
							buffer = msg.getBuffer();
							peer.setOutputBuffer(buffer);
						}
					}
				}
				if (buffer == null) {
					break;
				}
				channel.write(buffer);
				if (buffer.position() < buffer.limit())
					break;
				buffer = null;
				peer.setOutputBuffer(null);
			}
		} catch (IOException exc) {
			closeConnection(peer);
		}
	}

	private void closeConnection(Peer peer) {
		PeerAddress address = peer.getAddress();
		SocketChannel channel = peer.getChannel();
		try {
			peer.setInputBuffer(null);
			peer.setOutputBuffer(null);
			peer.getOutputList().clear();
			outboundCount--;
			address.setConnected(false);
			peer.setConnected(false);
			synchronized (BTCLoader.lock) {
				connections.remove(peer);
			}
			if (channel.isOpen()) {
				channel.close();
			}
			if (peer.getVersionCount() > 2) {
				for (ConnectionListener listener : listeners) {
					listener.connectionEnded(peer);
				}
			}
			BTCLoader.info(String.format("Connection closed with peer %s",
					address.toString()));
		} catch (IOException exc) {
			BTCLoader.error(String.format(
					"Error while closing socket channel with %s",
					address.toString()), exc);
		}
	}

	private void processCompletedMessages() {
		while (!BTCLoader.completedMessages.isEmpty()) {
			Message msg;
			synchronized (BTCLoader.lock) {
				msg = BTCLoader.completedMessages.remove(0);
			}
			Peer peer = msg.getPeer();
			PeerAddress address = peer.getAddress();
			SelectionKey key = peer.getKey();
			if (!address.isConnected()) {
				continue;
			}
			if (peer.shouldDisconnect()) {
				closeConnection(peer);
				continue;
			}
			if (msg.getBuffer() != null) {
				synchronized (BTCLoader.lock) {
					peer.getOutputList().add(msg);
					key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
				}
			}
			if (peer.getVersionCount() == 2) {
				peer.incVersionCount();
				BTCLoader.info(String.format(
						"Connection handshake completed with %s",
						address.toString()));
				BTCLoader.networkChainHeight = Math.max(
						BTCLoader.networkChainHeight, peer.getHeight());
				if (!staticConnections) {
					Message addrMsg = GetAddressMessage
							.buildGetAddressMessage(peer);
					synchronized (BTCLoader.lock) {
						peer.getOutputList().add(addrMsg);
						key.interestOps(key.interestOps()
								| SelectionKey.OP_WRITE);
					}
					BTCLoader
							.info(String.format("'getaddr' message sent to %s",
									address.toString()));
				}
				Message filterMsg = FilterLoadMessage.buildFilterLoadMessage(
						peer, BTCLoader.bloomFilter);
				synchronized (BTCLoader.lock) {
					peer.getOutputList().add(filterMsg);
					key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
				}
				BTCLoader.info(String.format("'filterload' message sent to %s",
						address.toString()));
				if (getBlocksHeight < 0
						&& BTCLoader.wallet.getChainHeight() < peer.getHeight()) {
					if (BTCLoader.wallet.getChainHeight() == 0)
						BTCLoader.loadingChain = true;
					Message blocksMsg = buildGetBlocksMessage(peer);
					BTCLoader
							.info(String
									.format("'%s' message sent to %s",
											blocksMsg.getCommand() == MessageHeader.MessageCommand.GETBLOCKS ? "getblocks"
													: "getheaders", address));
					synchronized (BTCLoader.lock) {
						peer.getOutputList().add(blocksMsg);
						key.interestOps(key.interestOps()
								| SelectionKey.OP_WRITE);
					}
					getBlocksHeight = BTCLoader.wallet.getChainHeight();
				}
				for (ConnectionListener listener : listeners) {
					listener.connectionStarted(peer);
				}

			}
		}
	}

	private Message buildGetBlocksMessage(Peer peer) {
		List<Sha256Hash> invList = new ArrayList<>(100);
		try {
			int chainHeight = BTCLoader.wallet.getChainHeight();
			int blockHeight = Math.max(0, chainHeight - 500);
			List<Sha256Hash> chainList = BTCLoader.wallet.getChainList(
					blockHeight, Sha256Hash.ZERO_HASH);
			int step = 1;
			int loop = 0;
			int pos = chainList.size() - 1;
			while (pos >= 0) {
				invList.add(chainList.get(pos));
				if (loop == 10) {
					step = step * 2;
					pos = pos - step;
				} else {
					loop++;
					pos--;
				}
			}
			if (invList.isEmpty())
				invList.add(BTCLoader.wallet.getChainHead());
		} catch (WalletException exc) {
			invList.add(BTCLoader.wallet.getChainHead());
		}
		return BTCLoader.loadingChain ? GetHeadersMessage
				.buildGetHeadersMessage(peer, invList, Sha256Hash.ZERO_HASH)
				: GetBlocksMessage.buildGetBlocksMessage(peer, invList,
						Sha256Hash.ZERO_HASH);
	}

	private void processRequests() {
		long currentTime = System.currentTimeMillis() / 1000;
		PeerRequest request;
		Peer peer;
		synchronized (BTCLoader.lock) {
			while (!BTCLoader.processedRequests.isEmpty()) {
				request = BTCLoader.processedRequests.get(0);
				if (request.getTimeStamp() >= currentTime - 30
						|| request.isProcessing()) {
					break;
				}
				BTCLoader.processedRequests.remove(0);
				BTCLoader.pendingRequests.add(request);
			}
		}
		while (!BTCLoader.pendingRequests.isEmpty()) {
			synchronized (BTCLoader.lock) {
				request = BTCLoader.pendingRequests.remove(0);
				BTCLoader.processedRequests.add(request);
			}
			peer = request.getOrigin();
			if (peer != null
					&& (request.wasContacted(peer) || !peer.isConnected())) {
				peer = null;
			}
			if (peer == null) {
				int index = (int) (((double) connections.size()) * Math
						.random());
				for (int i = index; i < connections.size(); i++) {
					Peer chkPeer = connections.get(i);
					if ((chkPeer.getServices() & NetParams.NODE_NETWORK) != 0
							&& !request.wasContacted(chkPeer)
							&& chkPeer.isConnected()) {
						peer = chkPeer;
						break;
					}
				}
				if (peer == null) {
					for (int i = 0; i < index; i++) {
						Peer chkPeer = connections.get(i);
						if ((chkPeer.getServices() & NetParams.NODE_NETWORK) != 0
								&& !request.wasContacted(chkPeer)
								&& chkPeer.isConnected()) {
							peer = chkPeer;
							break;
						}
					}
				}
			}
			if (peer == null) {
				Peer originPeer = request.getOrigin();
				synchronized (BTCLoader.lock) {
					BTCLoader.processedRequests.remove(request);
					if (originPeer != null) {
						int banScore = originPeer.getBanScore() + 2;
						originPeer.setBanScore(banScore);
						if (banScore >= BTCLoader.MAX_BAN_SCORE)
							originPeer.setDisconnect(true);
					}
				}
				String originAddress = (originPeer != null ? originPeer
						.getAddress().toString() : "local");
				BTCLoader.info(String.format(
						"Purging unavailable %s request initiated by %s\n  %s",
						(request.getType() == InventoryItem.INV_BLOCK ? "block"
								: "transaction"), originAddress, request
								.getHash().toString()));
				continue;
			}
			request.addPeer(peer);
			request.setTimeStamp(currentTime);
			List<InventoryItem> invList = new ArrayList<>(1);
			invList.add(new InventoryItem(request.getType(), request.getHash()));
			Message msg = GetDataMessage.buildGetDataMessage(peer, invList);
			synchronized (BTCLoader.lock) {
				peer.getOutputList().add(msg);
				SelectionKey key = peer.getKey();
				key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
			}
		}
	}

	private void dnsDiscovery() {
		String[] nodes = null;
		if (BTCLoader.testNetwork) {
			nodes = dnsTestSeeds;
		} else {
			nodes = dnsSeeds;
		}
		for (String host : nodes) {
			PeerAddress peerAddress;
			try {
				InetAddress[] addresses = InetAddress.getAllByName(host);
				for (InetAddress address : addresses) {
					peerAddress = new PeerAddress(address,
							BTCLoader.DEFAULT_PORT);
					peerAddress.setServices(NetParams.NODE_NETWORK);
					if (BTCLoader.peerMap.get(peerAddress) == null) {
						BTCLoader.peerAddresses.add(peerAddress);
						BTCLoader.peerMap.put(peerAddress, peerAddress);
					}
				}
			} catch (UnknownHostException exc) {
				BTCLoader.info(String.format("DNS host %s not found", host));
			}
		}
	}
}
