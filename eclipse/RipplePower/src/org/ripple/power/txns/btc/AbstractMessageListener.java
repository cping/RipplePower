package org.ripple.power.txns.btc;

import java.util.List;

/**
 * A MessageListener is called during message processing to handle
 * application-specific tasks. This abstract class provides default handlers for
 * messages that are not used by the application.
 */
public abstract class AbstractMessageListener implements MessageListener {

	/**
	 * Handle an inventory request
	 *
	 * <p>
	 * This method is called when a 'getdata' message is received. The
	 * application should send the inventory items to the requesting peer. A
	 * 'notfound' message should be returned to the requesting peer if one or
	 * more items cannot be sent.
	 * </p>
	 *
	 * @param msg
	 *            Message
	 * @param invList
	 *            Inventory item list
	 */
	@Override
	public void sendInventory(Message msg, List<InventoryItem> invList) {
	}

	/**
	 * Handle an inventory item available notification
	 *
	 * <p>
	 * This method is called when an 'inv' message is received. The application
	 * should request any needed inventory items from the peer.
	 * </p>
	 *
	 * @param msg
	 *            Message
	 * @param invList
	 *            Inventory item list
	 */
	@Override
	public void requestInventory(Message msg, List<InventoryItem> invList) {
	}

	/**
	 * Handle a request not found
	 *
	 * <p>
	 * This method is called when a 'notfound' message is received. It notifies
	 * the application that an inventory request cannot be completed because the
	 * item was not found. The request can be discarded or retried by sending it
	 * to a different peer.
	 * </p>
	 *
	 * @param msg
	 *            Message
	 * @param invList
	 *            Inventory item list
	 */
	@Override
	public void requestNotFound(Message msg, List<InventoryItem> invList) {
	}

	/**
	 * Handle a request for the transaction memory pool
	 *
	 * <p>
	 * This method is called when a 'mempool' message is received. The
	 * application should return an 'inv' message listing the transactions in
	 * the memory pool.
	 * </p>
	 *
	 * @param msg
	 *            Message
	 */
	@Override
	public void requestMemoryPool(Message msg) {
	}

	/**
	 * Process a peer address list
	 *
	 * <p>
	 * This method is called when an 'addr' message is received.
	 * </p>
	 *
	 * @param msg
	 *            Message
	 * @param addresses
	 *            Peer address list
	 */
	@Override
	public void processAddresses(Message msg, List<PeerAddress> addresses) {
	}

	/**
	 * Process an alert
	 *
	 * <p>
	 * This method is called when an 'alert' message is received
	 * </p>
	 *
	 * @param msg
	 *            Message
	 * @param alert
	 *            Alert
	 */
	@Override
	public void processAlert(Message msg, Alert alert) {
	}

	/**
	 * Process a block
	 *
	 * <p>
	 * This method is called when a 'block' message is received
	 * </p>
	 *
	 * @param msg
	 *            Message
	 * @param block
	 *            Block
	 */
	@Override
	public void processBlock(Message msg, Block block) {
	}

	/**
	 * Process a block header
	 *
	 * <p>
	 * This method is called when a 'headers' message is received
	 * </p>
	 *
	 * @param msg
	 *            Message
	 * @param hdrList
	 *            Block header list
	 */
	@Override
	public void processBlockHeaders(Message msg, List<BlockHeader> hdrList) {
	}

	/**
	 * Process a Bloom filter clear request
	 *
	 * <p>
	 * This method is called when a 'filterclear' message is received. The peer
	 * Bloom filter has been cleared before this method is called.
	 * </p>
	 *
	 * @param msg
	 *            Message
	 * @param oldFilter
	 *            Previous bloom filter
	 */
	@Override
	public void processFilterClear(Message msg, BloomFilter oldFilter) {
	}

	/**
	 * Process a Bloom filter load request
	 *
	 * <p>
	 * This method is called when a 'filterload' message is received. The peer
	 * bloom filter has been updated before this method is called.
	 * </p>
	 *
	 * @param msg
	 *            Message
	 * @param oldFilter
	 *            Previous bloom filter
	 * @param newFilter
	 *            New bloom filter
	 */
	@Override
	public void processFilterLoad(Message msg, BloomFilter oldFilter, BloomFilter newFilter) {
	}

	/**
	 * Process a get address request
	 *
	 * <p>
	 * This method is called when a 'getaddr' message is received. The
	 * application should call AddressMessage.buildAddressMessage() to build the
	 * response message.
	 * </p>
	 *
	 * @param msg
	 *            Message
	 */
	@Override
	public void processGetAddress(Message msg) {
	}

	/**
	 * Process a request for the latest blocks
	 *
	 * <p>
	 * This method is called when a 'getblocks' message is received. The
	 * application should use the locator block list to find the latest common
	 * block and then send an 'inv' message to the peer for the blocks following
	 * the common block.
	 * </p>
	 *
	 * @param msg
	 *            Message
	 * @param version
	 *            Negotiated version
	 * @param blockList
	 *            Locator block list
	 * @param stopBlock
	 *            Stop block (Sha256Hash.ZERO_HASH if all blocks should be sent)
	 */
	@Override
	public void processGetBlocks(Message msg, int version, List<Sha256Hash> blockList, Sha256Hash stopBlock) {
	}

	/**
	 * Process a request for the latest headers
	 *
	 * <p>
	 * This method is called when a 'getheaders' message is received. The
	 * application should use the locator block list to find the latest common
	 * block and then send a 'headers' message to the peer for the blocks
	 * following the common block.
	 * </p>
	 *
	 * @param msg
	 *            Message
	 * @param version
	 *            Negotiated version
	 * @param blockList
	 *            Locator block list
	 * @param stopBlock
	 *            Stop block (Sha256Hash.ZERO_HASH if all blocks should be sent)
	 */
	@Override
	public void processGetHeaders(Message msg, int version, List<Sha256Hash> blockList, Sha256Hash stopBlock) {
	}

	/**
	 * Process a Merkle block
	 *
	 * <p>
	 * This method is called when a 'merkleblock' message is received.
	 * </p>
	 *
	 * @param msg
	 *            Message
	 * @param blkHeader
	 *            Merkle block header
	 */
	@Override
	public void processMerkleBlock(Message msg, BlockHeader blkHeader) {
	}

	/**
	 * Process a ping
	 *
	 * <p>
	 * This method is called when a 'ping' message is received. The application
	 * should return a 'pong' message to the sender. This method will not be
	 * called if the sender has not implemented BIP0031.
	 * </p>
	 *
	 * @param msg
	 *            Message
	 * @param nonce
	 *            Nonce
	 */
	@Override
	public void processPing(Message msg, long nonce) {
	}

	/**
	 * Process a pong
	 *
	 * <p>
	 * This method is called when a 'pong' message is received.
	 * </p>
	 *
	 * @param msg
	 *            Message
	 * @param nonce
	 *            Nonce
	 */
	@Override
	public void processPong(Message msg, long nonce) {
	}

	/**
	 * Process a message rejection
	 *
	 * <p>
	 * This method is called when a 'reject' message is received.
	 * </p>
	 *
	 * @param msg
	 *            Message
	 * @param cmd
	 *            Failing message command
	 * @param reasonCode
	 *            Failure reason code
	 * @param description
	 *            Description of the failure
	 * @param hash
	 *            Item hash or Sha256Hash.ZERO_HASH
	 */
	@Override
	public void processReject(Message msg, String cmd, int reasonCode, String description, Sha256Hash hash) {
	}

	/**
	 * Process a transaction
	 *
	 * <p>
	 * This method is called when a 'tx' message is received.
	 * </p>
	 *
	 * @param msg
	 *            Message
	 * @param tx
	 *            Transaction
	 */
	@Override
	public void processTransaction(Message msg, Transaction tx) {
	}

	/**
	 * Process a version message
	 *
	 * <p>
	 * This method is called when a 'version' message is received. The
	 * application should return a 'verack' message to the sender if the
	 * connection is accepted.
	 * </p>
	 *
	 * @param msg
	 *            Message
	 * @param localAddress
	 *            Local address as seen by the peer
	 */
	@Override
	public void processVersion(Message msg, PeerAddress localAddress) {
	}

	/**
	 * Process a version acknowledgment
	 *
	 * <p>
	 * This method is called when a 'verack' message is received.
	 * </p>
	 *
	 * @param msg
	 *            Message
	 */
	@Override
	public void processVersionAck(Message msg) {
	}
}
