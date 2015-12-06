package org.ripple.power.txns.btc;

import java.io.IOException;
import java.io.InputStream;
import java.net.BindException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.StandardSocketOptions;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.ripple.power.Helper;
import org.ripple.power.utils.HttpRequest;
import org.ripple.power.utils.IP46Utils;

public class NetworkHandler implements Runnable {

	static class PeerAddressComparator implements Comparator<PeerAddress> {

		@Override
		public int compare(PeerAddress addr1, PeerAddress addr2) {

			long t1 = addr1.getTimeStamp();
			long t2 = addr2.getTimeStamp();
			return (t1 > t2 ? -1 : (t1 < t2 ? 1 : 0));

		}

	}

	/** Maximum number of pending input messages for a single peer */
	private static final int MAX_INPUT_MESSAGES = 10;

	/** Maximum number of pending output messages for a single peer */
	private static final int MAX_OUTPUT_MESSAGES = 500;

	// Bitcoin Network seed nodes (like ripple UNL)
	private static final String[] dnsSeeds = new String[] {
			"seed.bitcoin.sipa.be", // Pieter Wuille
			"dnsseed.bluematt.me", // Matt Corallo
			"dnsseed.bitcoin.dashjr.org", // Luke Dashjr
			"seed.bitcoinstats.com", // Chris Decker
			"seed.bitnodes.io", // Addy Yeow
	};

	// Test NetWork
	private static final String[] dnsTestSeeds = new String[] {
			"testnet-seed.alexykot.me", "testnet-seed.bitcoin.petertodd.org",
			"testnet-seed.bluematt.me" };

	/** Connection listeners */
	private final List<ConnectionListener> connectionListeners = new ArrayList<>();

	/** Network listener thread */
	private Thread listenerThread;

	/** Network timer */
	private Timer timer;

	/** Maximum number of connections */
	private final int maxConnections;

	/** Maximum number of outbound connections */
	private int maxOutbound;

	/** Current number of outbound connections */
	private int outboundCount;

	/** Host name */
	private final String hostName;

	/** Listen channel */
	private ServerSocketChannel listenChannel;

	/** Listen selection key */
	private SelectionKey listenKey;

	/** Network selector */
	private final Selector networkSelector;

	/** Connections list */
	private final List<Peer> connections = new ArrayList<>(128);

	/** Connection map */
	private final Map<InetAddress, Peer> connectionMap = new HashMap<>();

	/** Peer blacklist */
	private final List<BlacklistEntry> peerBlacklist = new ArrayList<>();

	/** Time of Last peer database update */
	private long lastPeerUpdateTime;

	/** Time of last outbound connection attempt */
	private long lastOutboundConnectTime;

	/** Last statistics output time */
	private long lastStatsTime;

	/** Last connection check time */
	private long lastConnectionCheckTime;

	/** Network shutdown */
	private boolean networkShutdown = false;

	/** Static connections */
	private boolean staticConnections = false;

	/** 'getblocks' message sent */
	private boolean getblocksSent = false;

	/**
	 * Creates the network listener
	 *
	 * @param maxConnections
	 *            The maximum number of connections
	 * @param maxOutbound
	 *            The maximum number of outbound connections
	 * @param hostName
	 *            The host name for this port or null
	 * @param listenPort
	 *            The port to listen on
	 * @param staticAddresses
	 *            Static peer address
	 * @param blacklist
	 *            Peer blacklist
	 * @throws IOException
	 *             I/O error
	 */
	public NetworkHandler(int maxConnections, int maxOutbound, String hostName,
			int listenPort, PeerAddress[] staticAddresses,
			List<BlacklistEntry> blacklist) throws IOException {
		this.maxConnections = maxConnections;
		this.maxOutbound = maxOutbound;
		this.hostName = hostName;
		BTCLoader.listenPort = listenPort;
		peerBlacklist.addAll(blacklist);
		//
		// Create the selector for listening for network events
		//
		networkSelector = Selector.open();
		//
		// Build the static peer address list
		//
		if (staticAddresses != null) {
			staticConnections = true;
			this.maxOutbound = Math.min(this.maxOutbound,
					staticAddresses.length);
			for (PeerAddress address : staticAddresses) {
				address.setStatic(true);
				BTCLoader.peerAddresses.add(0, address);
				BTCLoader.peerMap.put(address, address);
			}
		}
	}

	/**
	 * Processes network events
	 */
	@Override
	public void run() {
		BTCLoader
				.info(String
						.format("Network listener started: Port %d, Max connections %d, Max outbound %d",
								BTCLoader.listenPort, maxConnections,
								maxOutbound));
		lastPeerUpdateTime = System.currentTimeMillis() / 1000;
		lastOutboundConnectTime = lastPeerUpdateTime;
		lastStatsTime = lastPeerUpdateTime;
		lastConnectionCheckTime = lastPeerUpdateTime;
		listenerThread = Thread.currentThread();
		BTCLoader.networkChainHeight = BTCLoader.blockStore.getChainHeight();
		try {
			//
			// Get our external IP address from checkip.dyndns.org
			//
			// The returned string is '<html><body>Current IP Address:
			// n.n.n.n</body></html>'
			//
			getExternalIP();
			//
			// Get the peer nodes DNS discovery if we are not using static
			// connections.
			// The address list will be sorted in descending timestamp order so
			// that the
			// most recent peers appear first in the list.
			//
			if (!staticConnections) {
				dnsDiscovery();
				Collections.sort(BTCLoader.peerAddresses,
						new PeerAddressComparator());
			}
			//
			// Get the current alerts
			//
			BTCLoader.alerts.addAll(BTCLoader.blockStore.getAlerts());
			//
			// Create the listen channel
			//
			listenChannel = ServerSocketChannel.open();
			listenChannel.configureBlocking(false);
			listenChannel.bind(new InetSocketAddress(BTCLoader.listenPort), 10);
			listenKey = listenChannel.register(networkSelector,
					SelectionKey.OP_ACCEPT);
			//
			// Create the initial outbound connections to get us started
			//
			while (!networkShutdown && outboundCount < Math.min(maxOutbound, 4)
					&& connections.size() < maxConnections
					&& connections.size() < BTCLoader.peerAddresses.size())
				if (!connectOutbound())
					break;
		} catch (BlockStoreException | IOException exc) {
			BTCLoader.error("Unable to initialize network listener", exc);
			networkShutdown = true;
		}
		//
		// Create a timer to wake us up every 2 minutes
		//
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				wakeup();
			}
		}, 2 * 60 * 1000, 2 * 60 * 1000);
		//
		// Process network events until shutdown() is called
		//
		try {
			while (!networkShutdown) {
				processEvents();
			}
		} catch (Throwable exc) {
			BTCLoader.error(
					"Runtime exception while processing network events", exc);
		}
		//
		// Stopping
		//
		timer.cancel();
		BTCLoader.info("Network listener stopped");
	}

	/**
	 * Process network events
	 */
	private void processEvents() {
		int count;
		try {
			//
			// Process selectable events
			//
			// Note that you need to remove the key from the selected key
			// set. Otherwise, the selector will return immediately since
			// it thinks there are still unprocessed events. Also, accessing
			// a key after the channel is closed will cause an exception to be
			// thrown, so it is best to test for just one event at a time.
			//
			count = networkSelector.select();
			if (count > 0 && !networkShutdown) {
				Set<SelectionKey> selectedKeys = networkSelector.selectedKeys();
				Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
				while (keyIterator.hasNext() && !networkShutdown) {
					SelectionKey key = keyIterator.next();
					SelectableChannel channel = key.channel();
					if (channel.isOpen()) {
						if (key.isAcceptable()) {
							processAccept(key);
						} else if (key.isConnectable()) {
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
				//
				// Process completed messages
				//
				if (!BTCLoader.completedMessages.isEmpty()) {
					processCompletedMessages();
				}
				//
				// Process peer requests
				//
				if (!BTCLoader.pendingRequests.isEmpty()
						|| !BTCLoader.processedRequests.isEmpty())
					processRequests();
				//
				// Remove peer addresses that are too old and broadcast any new
				// addresses.
				// A maximum of 999 addresses will be broadcast in a single
				// 'addr' message
				// since we always broadcast our own address.
				//
				long currentTime = System.currentTimeMillis() / 1000;
				if (currentTime > lastPeerUpdateTime
						+ BTCLoader.MAX_PEER_ADDRESS_AGE) {
					List<PeerAddress> newAddresses = new ArrayList<>(
							BTCLoader.peerAddresses.size());
					synchronized (BTCLoader.peerAddresses) {
						Iterator<PeerAddress> iterator = BTCLoader.peerAddresses
								.iterator();
						while (iterator.hasNext()) {
							PeerAddress address = iterator.next();
							if (address.isStatic())
								continue;
							if (address.getTimeStamp() < lastPeerUpdateTime) {
								BTCLoader.peerMap.remove(address);
								iterator.remove();
							} else if (!address.wasBroadcast()
									&& newAddresses.size() < 999) {
								address.setBroadcast(true);
								newAddresses.add(address);
							}
						}
					}
					if (!newAddresses.isEmpty()) {
						Message addrMsg = AddressMessage.buildAddressMessage(
								null, newAddresses, BTCLoader.listenAddress);
						broadcastMessage(addrMsg);
						BTCLoader.info(String.format(
								"%d addresses broadcast to peers",
								newAddresses.size()));
					}
					lastPeerUpdateTime = currentTime;
				}
				//
				// Check for inactive peer connections every 2 minutes
				//
				// Close the connection if the peer hasn't completed the version
				// handshake within 2 minutes.
				// Otherwise, send a 'ping' message. Close the connection if the
				// peer is still inactive
				// after 4 minutes.
				//
				if (currentTime > lastConnectionCheckTime + 2 * 60) {
					lastConnectionCheckTime = currentTime;
					List<Peer> inactiveList = new ArrayList<Peer>();
					for (Peer chkPeer : connections) {

						PeerAddress chkAddress = chkPeer.getAddress();
						if (chkAddress.getTimeStamp() < currentTime - 5 * 60) {
							inactiveList.add(chkPeer);
						} else if (chkAddress.getTimeStamp() < currentTime - 2 * 60) {
							if (chkPeer.getVersionCount() < 2) {
								inactiveList.add(chkPeer);
							} else if (!chkPeer.wasPingSent()) {
								chkPeer.setPing(true);
								Message chkMsg = PingMessage
										.buildPingMessage(chkPeer);
								synchronized (chkPeer) {
									chkPeer.getOutputList().add(chkMsg);
									SelectionKey chkKey = chkPeer.getKey();
									chkKey.interestOps(chkKey.interestOps()
											| SelectionKey.OP_WRITE);
								}
								BTCLoader.info(String
										.format("'ping' message sent to %s",
												chkAddress));
							}
						}

					}

					for (Peer chkPeer : inactiveList) {

						BTCLoader.info(String.format(
								"Closing connection due to inactivity: %s",
								chkPeer.getAddress()));
						closeConnection(chkPeer);

					}

				}
				//
				// Create a new outbound connection if we have less than the
				// maximum number and we haven't tried for 30 seconds
				//
				if (currentTime > lastOutboundConnectTime + 30) {
					lastOutboundConnectTime = currentTime;
					while (outboundCount < maxOutbound
							&& connections.size() < maxConnections
							&& connections.size() < BTCLoader.peerAddresses
									.size()) {
						if (!connectOutbound()
								|| outboundCount >= Math.min(maxOutbound, 4))
							break;
					}
				}
				//
				// Print statistics every 5 minutes
				//
				if (currentTime > lastStatsTime + (5 * 60)) {
					lastStatsTime = currentTime;
					BTCLoader
							.info(String
									.format("\n"
											+ "=======================================================\n"
											+ "** Chain height: Network %,d, Local %,d\n"
											+ "** Connections: %,d outbound, %,d inbound\n"
											+ "** Addresses: %,d peers, %,d banned\n"
											+ "** Blocks: %,d received, %,d sent, %,d filtered sent\n"
											+ "** Transactions: %,d received, %,d sent, %,d pool, %,d rejected, %,d orphaned\n"
											+ "=======================================================",
											BTCLoader.networkChainHeight,
											BTCLoader.blockStore
													.getChainHeight(),
											outboundCount, connections.size()
													- outboundCount,
											BTCLoader.peerAddresses.size(),
											peerBlacklist.size(),
											BTCLoader.blocksReceived.get(),
											BTCLoader.blocksSent.get(),
											BTCLoader.filteredBlocksSent.get(),
											BTCLoader.txReceived.get(),
											BTCLoader.txSent.get(),
											BTCLoader.txMap.size(),
											BTCLoader.txRejected.get(),
											BTCLoader.orphanTxMap.size()));
					System.gc();
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

	/**
	 * Register a connection listener
	 *
	 * @param listener
	 *            Connection listener
	 */
	public void addListener(ConnectionListener listener) {
		connectionListeners.add(listener);
	}

	/**
	 * Returns the current connections
	 *
	 * @return Peer connections
	 */
	public List<Peer> getConnections() {
		//
		// Get the current connection list
		//
		List<Peer> connectionList;
		synchronized (connections) {
			connectionList = new ArrayList<>(connections);
		}
		//
		// Remove pending connections from the list
		//
		Iterator<Peer> it = connectionList.iterator();
		while (it.hasNext()) {
			Peer peer = it.next();
			if (peer.getVersionCount() < 3)
				it.remove();
		}
		return connectionList;
	}

	/**
	 * Wakes up the network listener
	 */
	public void wakeup() {
		if (Thread.currentThread() != listenerThread)
			networkSelector.wakeup();
	}

	/**
	 * Shutdowns the network listener
	 */
	public void shutdown() {
		networkShutdown = true;
		wakeup();
	}

	/**
	 * Sends a message to a connected peer
	 *
	 * @param msg
	 *            The message to be sent
	 */
	public void sendMessage(Message msg) {
		Peer peer = msg.getPeer();
		if (peer != null) {
			SelectionKey key = peer.getKey();
			PeerAddress address = peer.getAddress();
			synchronized (peer) {
				if (address.isConnected()) {
					peer.getOutputList().add(msg);
					key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
				}
			}
			wakeup();
		}
	}

	/**
	 * Broadcasts a message to all connected peers
	 *
	 * Block notifications will be sent to peers that are providing network
	 * services. Transaction notifications will be sent to peers that have
	 * requested transaction relays. All other notifications will be sent to all
	 * connected peers.
	 *
	 * @param msg
	 *            Message to broadcast
	 */
	public void broadcastMessage(Message msg) {
		//
		// Send the message to each connected peer
		//
		synchronized (connections) {
			for (Peer relayPeer : connections) {

				if (relayPeer.getVersionCount() > 2) {
					boolean sendMsg = true;
					MessageHeader.MessageCommand cmd = msg.getCommand();
					if (cmd == MessageHeader.MessageCommand.INV) {
						if (msg.getInventoryType() == InventoryItem.INV_BLOCK)
							sendMsg = relayPeer.shouldRelayBlocks();
						else if (msg.getInventoryType() == InventoryItem.INV_TX)
							sendMsg = relayPeer.shouldRelayTx();
					}
					if (sendMsg) {
						synchronized (relayPeer) {
							relayPeer.getOutputList().add(msg.clone(relayPeer));
							SelectionKey relayKey = relayPeer.getKey();
							relayKey.interestOps(relayKey.interestOps()
									| SelectionKey.OP_WRITE);
						}
					}
				}

			}

		}
		//
		// Wakeup the network listener to send the broadcast messages
		//
		wakeup();
	}

	/**
	 * Processes an OP_ACCEPT selection event
	 *
	 * We will accept the connection if we haven't reached the maximum number of
	 * connections. The new socket channel will be placed in non-blocking mode
	 * and the selection key enabled for read events. We will not add the peer
	 * address to the peer address list since we only want nodes that have
	 * advertised their availability on the list.
	 */
	private void processAccept(SelectionKey acceptKey) {
		try {
			SocketChannel channel = listenChannel.accept();
			if (channel != null) {
				InetSocketAddress remoteAddress = (InetSocketAddress) channel
						.getRemoteAddress();
				PeerAddress address = new PeerAddress(remoteAddress);
				if (connections.size() >= maxConnections) {
					channel.close();
					BTCLoader
							.info(String
									.format("Max connections reached: Connection rejected from %s",
											address));
				} else if (isBlacklisted(address.getAddress())) {
					channel.close();
					BTCLoader.info(String.format(
							"Connection rejected from banned address %s",
							address));
				} else if (connectionMap.get(address.getAddress()) != null) {
					channel.close();
					BTCLoader.info(String.format(
							"Duplicate connection rejected from %s", address));
				} else {
					address.setTimeConnected(System.currentTimeMillis() / 1000);
					channel.configureBlocking(false);
					channel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
					SelectionKey key = channel.register(networkSelector,
							SelectionKey.OP_READ | SelectionKey.OP_WRITE);
					Peer peer = new Peer(address, channel, key);
					key.attach(peer);
					peer.setConnected(true);
					address.setConnected(true);
					BTCLoader.info(String.format("Connection accepted from %s",
							address));
					Message msg = VersionMessage.buildVersionMessage(peer,
							BTCLoader.listenAddress,
							BTCLoader.blockStore.getChainHeight());
					synchronized (connections) {
						connections.add(peer);
						connectionMap.put(address.getAddress(), peer);
						peer.getOutputList().add(msg);
					}
					BTCLoader.info(String.format(
							"Sent 'version' message to %s", address));
				}
			}
		} catch (IOException exc) {
			BTCLoader.error("Unable to accept connection", exc);
			networkShutdown = true;
		}
	}

	/**
	 * Creates a new outbound connection
	 *
	 * This routine selects the most recent peer from the peer address list. The
	 * channel is placed in non-blocking mode and the connection is initiated.
	 * An OP_CONNECT selection event will be generated when the connection has
	 * been established or has failed.
	 *
	 * @return TRUE if a connection was established
	 */
	private boolean connectOutbound() {
		//
		// Get the most recent peer that does not have a connection
		//
		PeerAddress address = null;
		synchronized (BTCLoader.peerAddresses) {
			for (PeerAddress chkAddress : BTCLoader.peerAddresses) {
				if (!chkAddress.isConnected()
						&& connectionMap.get(chkAddress.getAddress()) == null
						&& !isBlacklisted(chkAddress.getAddress())
						&& (!staticConnections || chkAddress.isStatic())) {
					address = chkAddress;
					break;
				}
			}
		}
		if (address == null)
			return false;
		//
		// Create a socket channel for the connection and open the connection
		//
		Peer peer = null;
		try {
			SocketChannel channel = SocketChannel.open();
			channel.configureBlocking(false);
			channel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
			channel.bind(null);
			SelectionKey key = channel.register(networkSelector,
					SelectionKey.OP_CONNECT);
			peer = new Peer(address, channel, key);
			key.attach(peer);
			peer.setConnected(true);
			address.setConnected(true);
			address.setOutbound(true);
			channel.connect(address.toSocketAddress());
			outboundCount++;
			synchronized (connections) {
				connections.add(peer);
				connectionMap.put(address.getAddress(), peer);
			}
		} catch (BindException exc) {
			BTCLoader.error(
					String.format("Unable to open connection to %s", address),
					exc);
			if (peer != null)
				closeConnection(peer);
		} catch (IOException exc) {
			BTCLoader.error(
					String.format("Unable to open connection to %s", address),
					exc);
			networkShutdown = true;
		}
		return true;
	}

	/**
	 * Processes an OP_CONNECT selection event
	 *
	 * We will finish the connection and send a Version message to the remote
	 * peer
	 *
	 * @param key
	 *            The channel selection key
	 */
	private void processConnect(SelectionKey key) {
		Peer peer = (Peer) key.attachment();
		PeerAddress address = peer.getAddress();
		SocketChannel channel = peer.getChannel();
		try {
			channel.finishConnect();
			BTCLoader.info(String.format("Connection established to %s",
					address));
			address.setTimeConnected(System.currentTimeMillis() / 1000);
			Message msg = VersionMessage.buildVersionMessage(peer,
					BTCLoader.listenAddress,
					BTCLoader.blockStore.getChainHeight());
			synchronized (peer) {
				peer.getOutputList().add(msg);
				key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
			}
			BTCLoader.info(String.format("Sent 'version' message to %s",
					address));
		} catch (SocketException exc) {
			BTCLoader.info(String.format("%s: Peer %s",
					exc.getLocalizedMessage(), address));
			closeConnection(peer);
			if (!address.isStatic()) {
				synchronized (BTCLoader.peerAddresses) {
					if (BTCLoader.peerMap.get(address) != null) {
						BTCLoader.peerAddresses.remove(address);
						BTCLoader.peerMap.remove(address);
					}
				}
			}
		} catch (IOException exc) {
			BTCLoader.error(String.format("Connection failed to %s", address),
					exc);
			closeConnection(peer);
		}
	}

	/**
	 * Processes an OP_READ selection event
	 *
	 * @param key
	 *            The channel selection key
	 */
	private void processRead(SelectionKey key) {
		Peer peer = (Peer) key.attachment();
		PeerAddress address = peer.getAddress();
		SocketChannel channel = peer.getChannel();
		ByteBuffer buffer = peer.getInputBuffer();
		address.setTimeStamp(System.currentTimeMillis() / 1000);
		try {
			int count;
			//
			// Read data until we have a complete message or no more data is
			// available
			//
			while (true) {
				//
				// Allocate a header buffer if no read is in progress
				//
				if (buffer == null) {
					buffer = ByteBuffer
							.wrap(new byte[MessageHeader.HEADER_LENGTH]);
					peer.setInputBuffer(buffer);
				}
				//
				// Fill the input buffer
				//
				if (buffer.position() < buffer.limit()) {
					count = channel.read(buffer);
					if (count <= 0) {
						if (count < 0)
							closeConnection(peer);
						break;
					}
				}
				//
				// Process the message header
				//
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
						byte[] msgBytes = new byte[MessageHeader.HEADER_LENGTH
								+ (int) length];
						System.arraycopy(hdrBytes, 0, msgBytes, 0,
								MessageHeader.HEADER_LENGTH);
						buffer = ByteBuffer.wrap(msgBytes);
						buffer.position(MessageHeader.HEADER_LENGTH);
						peer.setInputBuffer(buffer);
					}
				}
				//
				// Queue the message for a message handler
				//
				// We will disable read operations for this peer if it has too
				// many
				// pending messages. Read operations will be re-enabled once
				// all of the messages have been processed. We do this to keep
				// one node from flooding us with requests.
				//
				if (buffer.position() == buffer.limit()) {
					peer.setInputBuffer(null);
					buffer.position(0);
					Message msg = new Message(buffer, peer, null);
					BTCLoader.messageQueue.put(msg);
					synchronized (peer) {
						count = peer.getInputCount() + 1;
						peer.setInputCount(count);
						if (count >= MAX_INPUT_MESSAGES
								|| peer.getOutputList().size() >= MAX_OUTPUT_MESSAGES)
							key.interestOps(key.interestOps()
									& (~SelectionKey.OP_READ));
					}
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

	/**
	 * Processes an OP_WRITE selection event
	 *
	 * @param key
	 *            The channel selection key
	 */
	private void processWrite(SelectionKey key) {
		Peer peer = (Peer) key.attachment();
		SocketChannel channel = peer.getChannel();
		ByteBuffer buffer = peer.getOutputBuffer();
		try {
			//
			// Write data until all pending messages have been sent or the
			// socket buffer is full
			//
			while (true) {
				//
				// Get the next message if no write is in progress. Disable
				// write events
				// if there are no more messages to write.
				//
				if (buffer == null) {
					synchronized (peer) {
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
				//
				// Stop if all messages have been sent
				//
				if (buffer == null)
					break;
				//
				// Write the current buffer to the channel
				//
				channel.write(buffer);
				if (buffer.position() < buffer.limit())
					break;
				buffer = null;
				peer.setOutputBuffer(null);
			}
			//
			// Restart a deferred request if we have sent all of the pending
			// data
			//
			if (peer.getOutputBuffer() == null) {
				synchronized (peer) {
					if (peer.getInputCount() == 0)
						key.interestOps(key.interestOps()
								| SelectionKey.OP_READ);
					Message deferredMsg = peer.getDeferredMessage();
					if (deferredMsg != null) {
						peer.setDeferredMessage(null);
						deferredMsg.setPeer(peer);
						deferredMsg.setBuffer(deferredMsg.getRestartBuffer());
						deferredMsg.setRestartBuffer(null);
						BTCLoader.messageQueue.put(deferredMsg);
						int count = peer.getInputCount() + 1;
						peer.setInputCount(count);
						if (count >= MAX_INPUT_MESSAGES)
							key.interestOps(key.interestOps()
									& (~SelectionKey.OP_READ));
					}
				}
			}
		} catch (IOException exc) {
			closeConnection(peer);
		} catch (InterruptedException msg) {
			BTCLoader.warn("Interrupted while queueing deferred request");
			networkShutdown = true;
		}
	}

	/**
	 * Closes a peer connection and discards any pending messages
	 *
	 * @param peer
	 *            The peer being closed
	 */
	private void closeConnection(Peer peer) {
		PeerAddress address = peer.getAddress();
		SocketChannel channel = peer.getChannel();
		try {
			//
			// Disconnect the peer
			//
			peer.setInputBuffer(null);
			peer.setOutputBuffer(null);
			peer.setDeferredMessage(null);
			peer.getOutputList().clear();
			if (address.isOutbound())
				outboundCount--;
			address.setConnected(false);
			address.setOutbound(false);
			peer.setConnected(false);
			synchronized (connections) {
				connections.remove(peer);
				connectionMap.remove(address.getAddress());
			}
			if (!address.isStatic()) {
				synchronized (BTCLoader.peerAddresses) {
					BTCLoader.peerAddresses.remove(address);
					BTCLoader.peerMap.remove(address);
				}
			}
			//
			// Ban the peer if necessary
			//
			synchronized (peer) {
				if (peer.getBanScore() >= BTCLoader.MAX_BAN_SCORE
						&& !isBlacklisted(address.getAddress())) {
					peerBlacklist.add(new BlacklistEntry(address.getAddress(),
							-1));
					BTCLoader.info(String.format("Peer address %s banned",
							address.getAddress().getHostAddress()));
				}
			}
			//
			// Notify listeners that a connection has ended
			//
			if (peer.getVersionCount() > 2) {

				for (ConnectionListener listener : connectionListeners) {
					listener.connectionEnded(peer, connections.size());
				}
			}
			//
			// Close the channel
			//
			if (channel.isOpen())
				channel.close();
			BTCLoader.info(String.format("Connection closed with peer %s",
					address));
		} catch (IOException exc) {
			BTCLoader
					.error(String.format(
							"Error while closing socket channel with %s",
							address), exc);
		}
	}

	/**
	 * Processes completed messages
	 */
	private void processCompletedMessages() {
		Message msg;
		while ((msg = BTCLoader.completedMessages.poll()) != null) {
			Peer peer = msg.getPeer();
			if (peer == null)
				continue;
			PeerAddress address = peer.getAddress();
			SelectionKey key = peer.getKey();
			//
			// Nothing to do if the connection has been closed
			//
			if (!address.isConnected())
				continue;
			//
			// Close the connection if requested
			//
			if (peer.shouldDisconnect()) {
				closeConnection(peer);
				continue;
			}
			//
			// Send the response (if any)
			//
			if (msg.getBuffer() != null) {
				synchronized (peer) {
					peer.getOutputList().add(msg);
					key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
				}
			}
			//
			// Decrement the pending input count for the peer and re-enable read
			// when the count reaches zero. Read is disabled when the peer has
			// sent too many requests at one time.
			//
			synchronized (peer) {
				int count = peer.getInputCount() - 1;
				peer.setInputCount(count);
				if (count == 0 && peer.getOutputList().isEmpty())
					key.interestOps(key.interestOps() | SelectionKey.OP_READ);
			}
			//
			// Sent initial setup messages if we have successfully exchanged
			// 'version' messages
			//
			if (peer.getVersionCount() == 2) {
				peer.incVersionCount();
				BTCLoader.info(String.format(
						"Connection handshake completed with %s", address));
				//
				// Disconnect if this is an outbound connection and the peer
				// doesn't provide network services
				//
				if ((peer.getServices() & NetParams.NODE_NETWORK) == 0
						&& address.isOutbound()) {
					BTCLoader.info(String.format(
							"Network services not provided by %s", address));
					closeConnection(peer);
					continue;
				}
				//
				// Send a 'getaddr' message to exchange peer address lists.
				// Do not do this if we are using static connections since we
				// don't need
				// to know peer addresses.
				//
				if (!staticConnections) {
					if ((peer.getServices() & NetParams.NODE_NETWORK) != 0) {
						Message addrMsg = GetAddressMessage
								.buildGetAddressMessage(peer);
						synchronized (peer) {
							peer.getOutputList().add(addrMsg);
							key.interestOps(key.interestOps()
									| SelectionKey.OP_WRITE);
						}
					}
				}
				//
				// Send current alert messages
				//
				long currentTime = System.currentTimeMillis() / 1000;
				synchronized (BTCLoader.alerts) {
					for (Alert alert : BTCLoader.alerts) {
						if (!alert.isCanceled()
								&& alert.getExpireTime() > currentTime) {
							Message alertMsg = AlertMessage.buildAlertMessage(
									peer, alert);
							synchronized (peer) {
								peer.getOutputList().add(alertMsg);
								key.interestOps(key.interestOps()
										| SelectionKey.OP_WRITE);
							}
							BTCLoader.info(String.format("Sent alert %d to %s",
									alert.getID(), address));
						}
					}
				}
				//
				// Send a 'getblocks' message if we are down-level and we
				// haven't sent
				// one yet
				//
				if (!getblocksSent
						&& (peer.getServices() & NetParams.NODE_NETWORK) != 0
						&& peer.getHeight() > BTCLoader.blockStore
								.getChainHeight()) {
					BTCLoader.networkChainHeight = Math.max(
							BTCLoader.networkChainHeight, peer.getHeight());
					List<Sha256Hash> blockList = getBlockList();
					Message getMsg = GetBlocksMessage.buildGetBlocksMessage(
							peer, blockList, Sha256Hash.ZERO_HASH);
					synchronized (peer) {
						peer.getOutputList().add(getMsg);
						key.interestOps(key.interestOps()
								| SelectionKey.OP_WRITE);
					}
					getblocksSent = true;
					BTCLoader.info(String.format(
							"Sent 'getblocks' message to %s", address));
				}
				//
				// Notify listeners of the new connection
				//
				for (ConnectionListener listener : connectionListeners) {
					listener.connectionStarted(peer, connections.size());
				}
			}
		}
	}

	/**
	 * Process peer requests
	 */
	private void processRequests() {
		long currentTime = System.currentTimeMillis() / 1000;
		PeerRequest request;
		Peer peer;
		//
		// Check for request timeouts (we will wait 10 seconds for a response)
		//
		synchronized (BTCLoader.pendingRequests) {
			while (!BTCLoader.processedRequests.isEmpty()) {
				request = BTCLoader.processedRequests.get(0);
				if (request.getTimeStamp() >= currentTime - 10
						|| request.isProcessing())
					break;
				//
				// Move the request back to the pending queue
				//
				BTCLoader.processedRequests.remove(0);
				if (request.getType() == InventoryItem.INV_BLOCK)
					BTCLoader.pendingRequests.add(request);
				else
					BTCLoader.pendingRequests.add(0, request);
			}
		}
		//
		// Send pending requests. We will suspend request processing if we come
		// to
		// a block request and the database handler has 10 blocks waiting for
		// processing.
		// All pending transaction requests will have been processed at this
		// point since
		// transaction requests are placed at the front of the queue while block
		// requests
		// are placed at the end of the queue.
		//
		while (!BTCLoader.pendingRequests.isEmpty()) {
			synchronized (BTCLoader.pendingRequests) {
				request = BTCLoader.pendingRequests.get(0);
				if (request.getType() == InventoryItem.INV_BLOCK
						&& (BTCLoader.databaseQueue.size() >= 10 || BTCLoader.processedRequests
								.size() > 50)) {
					request = null;
				} else {
					BTCLoader.pendingRequests.remove(0);
					BTCLoader.processedRequests.add(request);
				}
			}
			if (request == null)
				break;
			//
			// Send the request to the origin peer unless we already tried or
			// the peer is
			// no longer connected
			//
			peer = request.getOrigin();
			if (peer != null
					&& (request.wasContacted(peer) || !peer.isConnected()))
				peer = null;
			//
			// Select a peer to process the request. The peer must provide
			// network
			// services and must not have been contacted for this request.
			//
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
			//
			// Discard the request if all of the available peers have been
			// contacted. We will
			// increment the banscore for the origin peer since he is
			// broadcasting inventory
			// that he doesn't have (except transactions which are removed from
			// the memory pool
			// when they are included in a block)
			//
			if (peer == null) {
				Peer originPeer = request.getOrigin();
				synchronized (BTCLoader.pendingRequests) {
					BTCLoader.processedRequests.remove(request);
				}
				if (originPeer != null
						&& request.getType() != InventoryItem.INV_TX) {
					synchronized (originPeer) {
						int banScore = originPeer.getBanScore() + 5;
						originPeer.setBanScore(banScore);
						if (banScore >= BTCLoader.MAX_BAN_SCORE)
							originPeer.setDisconnect(true);
					}
				}
				String originAddress = (originPeer != null ? originPeer
						.getAddress().toString() : "local");
				BTCLoader
						.warn(String
								.format("Purging unavailable %s request initiated by %s\n  %s",
										(request.getType() == InventoryItem.INV_TX ? "transaction"
												: "block"), originAddress,
										request.getHash()));
				continue;
			}
			//
			// Send the request to the peer
			//
			request.addPeer(peer);
			request.setTimeStamp(currentTime);
			List<InventoryItem> invList = new ArrayList<>(1);
			invList.add(new InventoryItem(request.getType(), request.getHash()));
			Message msg = GetDataMessage.buildGetDataMessage(peer, invList);
			synchronized (peer) {
				peer.getOutputList().add(msg);
				SelectionKey key = peer.getKey();
				key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
			}
		}
	}

	/**
	 * Get more blocks
	 *
	 * This method is called by the database handler when it is loading the
	 * initial block chain and it needs more blocks. We rely on inventory
	 * messages to obtain new blocks once the block chain has been loaded.
	 */
	public void getBlocks() {
		//
		// Select a random peer
		//
		Peer peer = null;
		int chainHeight = BTCLoader.blockStore.getChainHeight();
		synchronized (connections) {
			int index = (int) (((double) connections.size()) * Math.random());
			for (int i = index; i < connections.size(); i++) {
				Peer chkPeer = connections.get(i);
				if (chkPeer.isConnected()
						&& chkPeer.getVersionCount() > 2
						&& chkPeer.getHeight() > chainHeight
						&& (chkPeer.getServices() & NetParams.NODE_NETWORK) != 0) {
					peer = chkPeer;
					break;
				}
			}
			if (peer == null) {
				for (int i = 0; i < index; i++) {
					Peer chkPeer = connections.get(i);
					if (chkPeer.isConnected()
							&& chkPeer.getVersionCount() > 2
							&& chkPeer.getHeight() > chainHeight
							&& (chkPeer.getServices() & NetParams.NODE_NETWORK) != 0) {
						peer = chkPeer;
						break;
					}
				}
			}
		}
		if (peer == null)
			return;
		//
		// Send the 'getblocks' message
		//
		List<Sha256Hash> blockList = getBlockList();
		Message msg = GetBlocksMessage.buildGetBlocksMessage(peer, blockList,
				Sha256Hash.ZERO_HASH);
		sendMessage(msg);
		BTCLoader.info(String.format("Sent 'getblocks' message to %s",
				peer.getAddress()));
	}

	/**
	 * Get block list for 'getblocks' message
	 */
	private List<Sha256Hash> getBlockList() {
		List<Sha256Hash> invList = new ArrayList<>(500);
		try {
			//
			// Get the chain list
			//
			int chainHeight = BTCLoader.blockStore.getChainHeight();
			int blockHeight = Math.max(0, chainHeight - 500);
			List<InventoryItem> chainList = BTCLoader.blockStore.getChainList(
					blockHeight, Sha256Hash.ZERO_HASH);
			//
			// Build the locator list starting with the chain head and working
			// backwards towards
			// the genesis block
			//
			int step = 1;
			int loop = 0;
			int pos = chainList.size() - 1;
			while (pos >= 0) {
				invList.add(chainList.get(pos).getHash());
				if (loop == 10) {
					step = step * 2;
					pos = pos - step;
				} else {
					loop++;
					pos--;
				}
			}
			if (invList.isEmpty())
				invList.add(BTCLoader.blockStore.getChainHead());
		} catch (BlockStoreException exc) {
			//
			// We can't query the database, so just locate the chain head and
			// hope we
			// are on the main chain
			//
			invList.add(BTCLoader.blockStore.getChainHead());
		}
		return invList;
	}

	private void getExternalIP() {
		int inChar;
		try {
			if (hostName != null) {
				BTCLoader.listenAddress = new PeerAddress(
						InetAddress.getByName(hostName), BTCLoader.listenPort);
				BTCLoader.info(String.format("External IP address is %s",
						BTCLoader.listenAddress));
			} else {
				try {
					HttpRequest request = HttpRequest
							.get("http://checkip.dyndns.org:80/");

					if (request.ok()) {
						BTCLoader
								.info("Getting external IP address from checkip.dyndns.org");
						try (InputStream inStream = request.stream()) {
							StringBuilder outString = new StringBuilder(128);
							while ((inChar = inStream.read()) >= 0)
								outString.appendCodePoint(inChar);
							String ipString = outString.toString();
							int start = ipString.indexOf(':');
							if (start < 0) {
								BTCLoader
										.error(String
												.format("Unrecognized response from checkip.dyndns.org\n  Response: %s",
														ipString));
							} else {
								int stop = ipString.indexOf('<', start);
								String ipAddress = ipString.substring(
										start + 1, stop).trim();
								BTCLoader.listenAddress = new PeerAddress(
										InetAddress.getByName(ipAddress),
										BTCLoader.listenPort);
								BTCLoader.info(String.format(
										"External IP address is %s",
										BTCLoader.listenAddress));
							}
						}
					}
				} catch (Throwable ex) {
					try {
						String ipAddress = IP46Utils.getLocalIP();
						BTCLoader.listenAddress = new PeerAddress(
								InetAddress.getByName(ipAddress),
								BTCLoader.listenPort);
						BTCLoader.info(String.format(
								"External IP address is %s",
								BTCLoader.listenAddress));
					} catch (Exception exc) {

					}
				}
			}
		} catch (Exception exc) {
			BTCLoader.error("Unable to get external IP address", exc);
		}
	}

	/**
	 * Performs DNS lookups to get the initial peer list
	 */
	private void dnsDiscovery() {
		String[] dns = BTCLoader.testNetwork ? dnsTestSeeds : dnsSeeds;
		long currentTime = System.currentTimeMillis() / 1000;
		for (String host : dns) {
			PeerAddress peerAddress;
			try {
				InetAddress[] addresses = InetAddress.getAllByName(host);
				for (InetAddress address : addresses) {
					if (BTCLoader.listenAddress != null
							&& address.equals(BTCLoader.listenAddress
									.getAddress()))
						continue;
					long timeSeen = currentTime
							- (long) ((double) (7 * 24 * 3600) * Math.random());
					peerAddress = new PeerAddress(address,
							BTCLoader.DEFAULT_PORT, timeSeen);
					peerAddress.setBroadcast(true);
					if (BTCLoader.peerMap.get(peerAddress) == null) {
						BTCLoader.peerAddresses.add(peerAddress);
						BTCLoader.peerMap.put(peerAddress, peerAddress);
					}
				}
			} catch (UnknownHostException exc) {
				BTCLoader.warn(String.format("DNS host %s not found", host));
			}
		}
	}

	/**
	 * Check if an address is blacklisted
	 *
	 * @param address
	 *            Address to check
	 * @return TRUE if the address is blacklisted
	 */
	private boolean isBlacklisted(InetAddress addr) {
		boolean blacklisted = false;
		for (BlacklistEntry entry : peerBlacklist) {
			if (entry.isBlacklisted(addr)) {
				blacklisted = true;
				break;
			}
		}
		return blacklisted;
	}

	/**
	 * Blacklist entry
	 */
	public static class BlacklistEntry {

		/** Base address */
		private final byte[] baseAddr;

		/** Subnet mask */
		private final byte[] subnetMask;

		/**
		 * Create the blacklist entry
		 *
		 * @param addr
		 *            IP address
		 * @param maskBits
		 *            Number of bits in the address mask or -1 to use entire
		 *            address
		 */
		public BlacklistEntry(InetAddress addr, int maskBits) {
			baseAddr = addr.getAddress();
			subnetMask = new byte[baseAddr.length];
			int bitCount = baseAddr.length * 8;
			if (maskBits >= 0)
				bitCount = Math.min(bitCount, maskBits);
			for (int i = 0; i < subnetMask.length; i++) {
				if (bitCount >= 8) {
					subnetMask[i] = (byte) 0xff;
					bitCount -= 8;
				} else if (bitCount > 0) {
					subnetMask[i] = (byte) (0xff << (8 - bitCount));
					bitCount = 0;
				}
				baseAddr[i] &= subnetMask[i];
			}
		}

		/**
		 * Check if an address matches this blacklist entry
		 *
		 * @param addr
		 *            IP address
		 * @return TRUE if the address matches
		 */
		public boolean isBlacklisted(InetAddress addr) {
			byte[] addrBytes = addr.getAddress();
			if (addrBytes.length != baseAddr.length)
				return false;
			boolean matches = true;
			for (int i = 0; i < baseAddr.length; i++) {
				if ((addrBytes[i] & subnetMask[i]) != baseAddr[i]) {
					matches = false;
					break;
				}
			}
			return matches;
		}
	}

	public SelectionKey getListenKey() {
		return listenKey;
	}

	public void setListenKey(SelectionKey listenKey) {
		this.listenKey = listenKey;
	}
}
