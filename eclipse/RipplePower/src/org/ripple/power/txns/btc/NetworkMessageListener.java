package org.ripple.power.txns.btc;

import java.io.EOFException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class NetworkMessageListener extends AbstractMessageListener {

    /** Alert listeners */
    private final List<AlertListener> alertListeners = new ArrayList<>();

    /**
     * Registers an alert listener
     *
     * @param       listener        Alert listener
     */
    public void addListener(AlertListener listener) {
        alertListeners.add(listener);
    }

    /**
     * Handle an inventory request
     *
     * <p>This method is called when a 'getdata' message is received.  The application
     * should send the inventory items to the requesting peer.  A 'notfound' message
     * should be returned to the requesting peer if one or more items cannot be sent.</p>
     *
     * @param       msg             Message
     * @param       invList         Inventory item list
     */
    @Override
    public void sendInventory(Message msg, List<InventoryItem> invList) {
        Peer peer = msg.getPeer();
       BTCLoader.debug(String.format("Processing 'getdata' from %s", peer.getAddress()));
        //
        // If this is a request restart, we need to skip over the items that have already
        // been processed as indicated by the restart index contained in the message.  Otherwise,
        // start with the first inventory item.
        //
        List<InventoryItem> notFound = new ArrayList<>(invList.size());
        int restart = msg.getRestartIndex();
        msg.setRestartIndex(0);
        int blocksSent = 0;
        for (int i=restart; i<invList.size(); i++) {
            //
            // Defer the request if we have sent 25 blocks in the current batch.  We will
            // restart at this point after the current batch has been sent to the peer.
            //
            if (blocksSent == 25) {
                msg.setRestartIndex(i);
                break;
            }
            InventoryItem item = invList.get(i);
            switch (item.getType()) {
                case InventoryItem.INV_TX:
                    //
                    // Send a transaction from the memory pool
                    //
                    StoredTransaction tx;
                    synchronized(BTCLoader.txMap) {
                        tx = BTCLoader.txMap.get(item.getHash());
                    }
                    if (tx != null) {
                        Message txMsg = TransactionMessage.buildTransactionMessage(peer, tx.getBytes());
                        BTCLoader.networkHandler.sendMessage(txMsg);
                        BTCLoader.txSent.incrementAndGet();
                       BTCLoader.debug(String.format("Sent tx %s", tx.getHash()));
                    } else {
                        notFound.add(item);
                    }
                    break;
                case InventoryItem.INV_BLOCK:
                    //
                    // Send a block from the database
                    //
                    try {
                        Block block = BTCLoader.blockStore.getBlock(item.getHash());
                        if (block != null) {
                            blocksSent++;
                            Message blockMsg = BlockMessage.buildBlockMessage(peer, block.getBytes());
                            BTCLoader.networkHandler.sendMessage(blockMsg);
                            BTCLoader.blocksSent.incrementAndGet();
                           BTCLoader.debug(String.format("Sent block %s", block.getHash()));
                        } else {
                            notFound.add(item);
                        }
                    } catch (BlockStoreException exc) {
                        notFound.add(item);
                    } catch (Throwable exc) {
                       BTCLoader.error("Unable to build message", exc);
                        notFound.add(item);
                    }
                    break;
                case InventoryItem.INV_FILTERED_BLOCK:
                    //
                    // Send a filtered block if the peer has loaded a Bloom filter.  The request
                    // will be ignored if there is no Bloom filter.
                    //
                    BloomFilter filter = peer.getBloomFilter();
                    if (filter != null) {
                        //
                        // Get the block from the database and locate any matching transactions.
                        // Send the Merkle block followed by the matching transactions (if any)
                        //
                        try {
                            Block block = BTCLoader.blockStore.getBlock(item.getHash());
                            if (block != null) {
                                List<Sha256Hash> matches = filter.findMatches(block);
                                sendMatchedTransactions(peer, block, matches);
                               BTCLoader.debug(String.format("Sent filtered block %s", block.getHash()));
                            } else {
                                notFound.add(item);
                            }
                        } catch (BlockStoreException exc) {
                            notFound.add(item);
                        } catch (Throwable exc) {
                           BTCLoader.error("Unable to build filtered block message", exc);
                            notFound.add(item);
                        }
                    }
                    break;
                default:
                    notFound.add(item);
            }
        }
        //
        // Create a 'notfound' response if we didn't find all of the requested items
        //
        if (!notFound.isEmpty()) {
            Message invMsg = NotFoundMessage.buildNotFoundMessage(peer, notFound);
            BTCLoader.networkHandler.sendMessage(invMsg);
        }
        //
        // Set up the restart if we didn't process all of the items
        //
        if (peer.getDeferredMessage() == null && msg.getRestartIndex() != 0) {
            ByteBuffer msgBuffer = msg.getBuffer();
            msgBuffer.rewind();
            msg.setRestartBuffer(msgBuffer);
            synchronized(peer) {
                peer.setDeferredMessage(msg);
            }
        }
        //
        // Send an 'inv' message for the current chain head to restart
        // the peer download if the previous 'getblocks' was not able
        // to return all of the blocks leading to the chain head
        //
        if (peer.isIncomplete() && peer.getDeferredMessage()==null) {
            peer.setIncomplete(false);
            List<InventoryItem> blockList = new ArrayList<>(1);
            blockList.add(new InventoryItem(InventoryItem.INV_BLOCK, BTCLoader.blockStore.getChainHead()));
            Message invMessage = InventoryMessage.buildInventoryMessage(peer, blockList);
            BTCLoader.networkHandler.sendMessage(invMessage);
        }
    }

    /**
     * Handle an inventory item available notification
     *
     * <p>This method is called when an 'inv' message is received.  The application
     * should request any needed inventory items from the peer.</p>
     *
     * @param       msg             Message
     * @param       invList         Inventory item list
     */
    @Override
    public void requestInventory(Message msg, List<InventoryItem> invList) {
        Peer peer = msg.getPeer();
        int txRequests = 0;
        //
        // Process the inventory items
        //
        for (InventoryItem item : invList) {
            PeerRequest request = new PeerRequest(item.getHash(), item.getType(), peer);
            switch (item.getType()) {
                case InventoryItem.INV_TX:
                    //
                    // Ignore large transaction broadcasts to avoid running out of storage
                    //
                    if (txRequests >= 50) {
                       BTCLoader.warn(String.format("More than 50 tx entries in 'inv' message from %s - ignoring",
                                               peer.getAddress()));
                        continue;
                    }
                    //
                    // Skip the transaction if we have already seen it
                    //
                    boolean newTx;
                    synchronized(BTCLoader.txMap) {
                        newTx = (BTCLoader.recentTxMap.get(item.getHash()) == null);
                    }
                    if (!newTx)
                        continue;
                    //
                    // Ignore transactions if we are down-level since they will be orphaned
                    // until we catch up to the rest of the network
                    //
                    if (BTCLoader.blockStore.getChainHeight() < BTCLoader.networkChainHeight-5)
                        continue;
                    //
                    // Request the transaction if it is not in the memory pool and has not
                    // been requested.  We add the request at the front of the queue so it
                    // does not get stuck behind pending block requests.
                    //
                    try {
                        if (BTCLoader.blockStore.isNewTransaction(item.getHash())) {
                            synchronized(BTCLoader.pendingRequests) {
                                if (BTCLoader.recentTxMap.get(item.getHash()) == null &&
                                                    !BTCLoader.pendingRequests.contains(request) &&
                                                    !BTCLoader.processedRequests.contains(request)) {
                                    BTCLoader.pendingRequests.add(0, request);
                                    txRequests++;
                                }
                            }
                        }
                    } catch (BlockStoreException exc) {
                        // Unable to check database - wait for another inventory broadcast
                    }
                    break;
                case InventoryItem.INV_BLOCK:
                    //
                    // Request the block if it is not in the database and has not been requested.
                    // Block requests are added to the end of the queue so that we don't hold
                    // up transaction requests while we update the block chain.
                    //
                    try {
                        if (BTCLoader.blockStore.isNewBlock(item.getHash())) {
                            synchronized(BTCLoader.pendingRequests) {
                                if (!BTCLoader.pendingRequests.contains(request) &&
                                                !BTCLoader.processedRequests.contains(request))
                                    BTCLoader.pendingRequests.add(request);
                            }
                        }
                    } catch (BlockStoreException exc) {
                        // Unable to check database - wait for another inventory broadcast
                    }
                    break;
            }
        }
    }

    /**
     * Handle a request not found
     *
     * <p>This method is called when a 'notfound' message is received.  It notifies the
     * application that an inventory request cannot be completed because the item was
     * not found.  The request can be discarded or retried by sending it to a different
     * peer.</p>
     *
     * @param       msg             Message
     * @param       invList         Inventory item list
     */
    @Override
    public void requestNotFound(Message msg, List<InventoryItem> invList) {
        for (InventoryItem item : invList) {
            synchronized(BTCLoader.pendingRequests) {
                //
                // Remove the request from the processedRequests list and put it
                // back on the pendingRequests list.  The network handler will
                // then send the request to a different peer or discard it if
                // all of the available peers have been contacted.
                //
                Iterator<PeerRequest> it = BTCLoader.processedRequests.iterator();
                while (it.hasNext()) {
                    PeerRequest request = it.next();
                    if (request.getType()==item.getType() && request.getHash().equals(item.getHash())) {
                        it.remove();
                        BTCLoader.pendingRequests.add(request);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Handle a request for the transaction memory pool
     *
     * <p>This method is called when a 'mempool' message is received.  The application
     * should return an 'inv' message listing the transactions in the memory pool.</p>
     *
     * @param       msg             Message
     */
    @Override
    public void requestMemoryPool(Message msg) {
        //
        // Get the list of transaction identifiers in the memory pool.  We will send a maximum
        // of 1000 transaction identifiers.
        //
        List<InventoryItem> invList;
        synchronized(BTCLoader.txMap) {
            Set<Sha256Hash> txSet = BTCLoader.txMap.keySet();
            invList = new ArrayList<>(txSet.size());
            Iterator<Sha256Hash> it = txSet.iterator();
            while (it.hasNext() && invList.size() < 1000)
                invList.add(new InventoryItem(InventoryItem.INV_TX, it.next()));
        }
        //
        // Send the 'inv' message
        //
        Message invMsg = InventoryMessage.buildInventoryMessage(msg.getPeer(), invList);
        BTCLoader.networkHandler.sendMessage(invMsg);
    }

    /**
     * Process a peer address list
     *
     * <p>This method is called when an 'addr' message is received.</p>
     *
     * @param       msg             Message
     * @param       addresses       Peer address list
     */
    @Override
    public void processAddresses(Message msg, List<PeerAddress> addresses) {
        long oldestTime = System.currentTimeMillis()/1000 - BTCLoader.MAX_PEER_ADDRESS_AGE;
        //
        // Add new addresses to the peer address list and update the timestamp and services
        // for existing entries.  We will not include peers that provide no services or peers
        // that that are too old.  The peer list is sorted by timestamp from newest to oldest.
        // Existing entries are updated in-place.
        //
        for(PeerAddress addr:addresses){
        	if(addr.getServices()!=0 && addr.getTimeStamp()>oldestTime &&
                    !addr.getAddress().isAnyLocalAddress() &&
                    !addr.getAddress().isLoopbackAddress() &&
                    addr.getPort()>0 && addr.getPort()<65536 &&
                    !addr.equals(BTCLoader.listenAddress)){

                long timeStamp = addr.getTimeStamp();
                synchronized(BTCLoader.peerAddresses) {
                    PeerAddress mapAddress = BTCLoader.peerMap.get(addr);
                    if (mapAddress == null) {
                        int index, lowIndex, highIndex;
                        int lastElem = BTCLoader.peerAddresses.size()-1;
                        if (lastElem < 0) {
                            BTCLoader.peerAddresses.add(addr);
                        } else if (BTCLoader.peerAddresses.get(lastElem).getTimeStamp() >= timeStamp) {
                            BTCLoader.peerAddresses.add(addr);
                        } else {
                            lowIndex = -1;
                            highIndex = lastElem;
                            while (highIndex-lowIndex > 1) {
                                index = (highIndex-lowIndex)/2+lowIndex;
                                if (BTCLoader.peerAddresses.get(index).getTimeStamp() < timeStamp)
                                    highIndex = index;
                                else
                                    lowIndex = index;
                            }
                            BTCLoader.peerAddresses.add(highIndex, addr);
                        }
                        BTCLoader.peerMap.put(addr, addr);
                    } else {
                        mapAddress.setTimeStamp(Math.max(mapAddress.getTimeStamp(), timeStamp));
                        mapAddress.setServices(addr.getServices());
                    }
                }
            
        	}
        }
      
    }

    /**
     * Process an alert
     *
     * <p>This method is called when an 'alert' message is received</p>
     *
     * @param       msg             Message
     * @param       alert           Alert
     */
    @Override
    public void processAlert(Message msg, Alert alert) {
        //
        // Store a new alert in our database
        //
        try {
            if (BTCLoader.blockStore.isNewAlert(alert.getID())) {
                //
                // Store the alert in our database
                //
                BTCLoader.blockStore.storeAlert(alert);
                //
                // Process alert cancels
                //
                int cancelID = alert.getCancelID();
                if (cancelID != 0)
                    BTCLoader.blockStore.cancelAlert(cancelID);
                List<Integer> cancelSet = alert.getCancelSet();
                for (Integer id : cancelSet)
                    BTCLoader.blockStore.cancelAlert(id);
                //
                // Broadcast the alert to our peers
                //
                if (alert.getRelayTime() > System.currentTimeMillis()/1000) {
                    Message alertMsg = AlertMessage.buildAlertMessage(null, alert);
                    BTCLoader.networkHandler.broadcastMessage(alertMsg);
                }
            }
        } catch (BlockStoreException exc) {
            // Can't store the alert - let it go
        }
        //
        // Notify alert listeners
        //
        synchronized(BTCLoader.alerts) {
            BTCLoader.alerts.add(alert);
        }
        for(AlertListener listener:alertListeners){
        	listener.alertReceived(alert);
        }
    }

    /**
     * Process a block
     *
     * <p>This method is called when a 'block' message is received</p>
     *
     * @param       msg             Message
     * @param       block           Block
     */
    @Override
    public void processBlock(Message msg, Block block) {
        //
        // Indicate the block is being processed so the block request won't be rebroadcast
        //
        synchronized(BTCLoader.pendingRequests) {
            for (PeerRequest chkRequest : BTCLoader.processedRequests) {
                if (chkRequest.getType()==InventoryItem.INV_BLOCK && chkRequest.getHash().equals(block.getHash())) {
                    chkRequest.setProcessing(true);
                    break;
                }
            }
        }
        BTCLoader.blocksReceived.incrementAndGet();
        //
        // Add the block to the database handler queue
        //
        try {
            BTCLoader.databaseQueue.put(block);
        } catch (InterruptedException exc) {
            // We should never block since the queue is backed by a linked list
        }
    }

    /**
     * Process a Bloom filter clear request
     *
     * <p>This method is called when a 'filterclear' message is received.  The peer
     * Bloom filter has been cleared before this method is called.</p>
     *
     * @param       msg             Message
     * @param       oldFilter       Previous bloom filter
     */
    @Override
    public void processFilterClear(Message msg, BloomFilter oldFilter) {
        synchronized(BTCLoader.bloomFilters) {
            BTCLoader.bloomFilters.remove(oldFilter);
        }
    }

    /**
     * Process a Bloom filter load request
     *
     * <p>This method is called when a 'filterload' message is received.  The peer bloom
     * filter has been updated before this method is called.</p>
     *
     * @param       msg             Message
     * @param       oldFilter       Previous bloom filter
     * @param       newFilter       New bloom filter
     */
    @Override
    public void processFilterLoad(Message msg, BloomFilter oldFilter, BloomFilter newFilter) {
        synchronized(BTCLoader.bloomFilters) {
            if (oldFilter != null)
                BTCLoader.bloomFilters.remove(oldFilter);
            BTCLoader.bloomFilters.add(newFilter);
        }
    }

    /**
     * Process a get address request
     *
     * <p>This method is called when a 'getaddr' message is received.  The application should
     * call AddressMessage.buildAddressMessage() to build the response message.</p>
     *
     * @param       msg             Message
     */
    @Override
    public void processGetAddress(Message msg) {
        List<PeerAddress> addressList;
        synchronized(BTCLoader.peerAddresses) {
            addressList = new ArrayList<>(BTCLoader.peerAddresses);
        }
        Message addrMsg = AddressMessage.buildAddressMessage(msg.getPeer(), addressList, BTCLoader.listenAddress);
        BTCLoader.networkHandler.sendMessage(addrMsg);
    }

    /**
     * Process a request for the latest blocks
     *
     * <p>This method is called when a 'getblocks' message is received.  The application should
     * use the locator block list to find the latest common block and then send an 'inv'
     * message to the peer for the blocks following the common block.</p>
     *
     * @param       msg             Message
     * @param       version         Negotiated version
     * @param       blockList       Locator block list
     * @param       stopBlock       Stop block (Sha256Hash.ZERO_HASH if all blocks should be sent)
     */
    @Override
    public void processGetBlocks(Message msg, int version, List<Sha256Hash> blockList, Sha256Hash stopBlock) {
        Peer peer = msg.getPeer();
       BTCLoader.debug(String.format("Processing 'getblocks' from %s", peer.getAddress()));
        //
        // We will ignore a 'getblocks' message if we are still processing a prior request
        //
        if (peer.getDeferredMessage() != null)
            return;
        try {
            //
            // Check each locator until we find one that is on the main chain
            //
            boolean foundJunction = false;
            Sha256Hash startBlock = null;
            for (Sha256Hash blockHash : blockList) {
                if (BTCLoader.blockStore.isOnChain(blockHash)) {
                    startBlock = blockHash;
                    foundJunction = true;
                   BTCLoader.debug(String.format("Found junction block %s", startBlock));
                    break;
                }
            }
            //
            // We go back to the genesis block if none of the supplied locators are on the main chain
            //
            if (!foundJunction)
                startBlock = new Sha256Hash(NetParams.GENESIS_BLOCK_HASH);
            //
            // Get the chain list
            //
            List<InventoryItem> chainList = BTCLoader.blockStore.getChainList(startBlock, stopBlock);
            if (chainList.size() >= 500)
                peer.setIncomplete(true);
            //
            // Build the 'inv' response
            //
           BTCLoader.debug(String.format("Returning %d inventory blocks", chainList.size()));
            Message invMsg = InventoryMessage.buildInventoryMessage(peer, chainList);
            BTCLoader.networkHandler.sendMessage(invMsg);
        } catch (BlockStoreException exc) {
            // Can't access the database, so just ignore the 'getblocks' request
        }
    }

    /**
     * Process a request for the latest headers
     *
     * <p>This method is called when a 'getheaders' message is received.  The application should
     * use the locator block list to find the latest common block and then send a 'headers'
     * message to the peer for the blocks following the common block.</p>
     *
     * @param       msg             Message
     * @param       version         Negotiated version
     * @param       blockList       Locator block list
     * @param       stopBlock       Stop block (Sha256Hash.ZERO_HASH if all blocks should be sent)
     */
    @Override
    public void processGetHeaders(Message msg, int version, List<Sha256Hash> blockList, Sha256Hash stopBlock) {
        Peer peer = msg.getPeer();
       BTCLoader.debug(String.format("Processing 'getheaders' from %s", peer.getAddress()));
        //
        // Check each locator until we find one that is on the main chain
        //
        try {
            boolean foundJunction = false;
            Sha256Hash startBlock = null;
            for (Sha256Hash blockHash : blockList) {
                if (BTCLoader.blockStore.isOnChain(blockHash)) {
                    foundJunction = true;
                    startBlock = blockHash;
                   BTCLoader.debug(String.format("Found junction block %s", startBlock));
                    break;
                }
            }
            //
            // We go back to the genesis block if none of the supplied locators are on the main chain
            //
            if (!foundJunction)
                startBlock = new Sha256Hash(NetParams.GENESIS_BLOCK_HASH);
            //
            // Get the chain list
            //
            List<BlockHeader> chainList = BTCLoader.blockStore.getHeaderList(startBlock, stopBlock);
            //
            // Build the 'headers' response
            //
           BTCLoader.debug(String.format("Returning %d headers", chainList.size()));
            Message hdrMsg = HeadersMessage.buildHeadersMessage(peer, chainList);
            BTCLoader.networkHandler.sendMessage(hdrMsg);
        } catch (BlockStoreException exc) {
            // Can't access the database, so just ignore the 'getheaders' request
        }
    }

    /**
     * Process a ping
     *
     * <p>This method is called when a 'ping' message is received.  The application should
     * return a 'pong' message to the sender.  This method will not be called if the sender
     * has not implemented BIP0031.</p>
     *
     * @param       msg             Message
     * @param       nonce           Nonce
     */
    @Override
    public void processPing(Message msg, long nonce) {
        //
        // Send the 'pong' response
        //
        Message pongMsg = PongMessage.buildPongMessage(msg.getPeer(), nonce);
        BTCLoader.networkHandler.sendMessage(pongMsg);
    }

    /**
     * Process a pong
     *
     * <p>This method is called when a 'pong' message is received.</p>
     *
     * @param       msg             Message
     * @param       nonce           Nonce
     */
    @Override
    public void processPong(Message msg, long nonce) {
        msg.getPeer().setPing(false);
       BTCLoader.info(String.format("'pong' response received from %s", msg.getPeer().getAddress()));
    }

    /**
     * Process a message rejection
     *
     * <p>This method is called when a 'reject' message is received.</p>
     *
     * @param       msg             Message
     * @param       cmd             Failing message command
     * @param       reasonCode      Failure reason code
     * @param       description     Description of the failure
     * @param       hash            Item hash or Sha256Hash.ZERO_HASH
     */
    @Override
    public void processReject(Message msg, String cmd, int reasonCode, String description, Sha256Hash hash) {
        //
        // Log the message
        //
        String reason = RejectMessage.reasonCodes.get(reasonCode);
        if (reason == null)
            reason = Integer.toString(reasonCode, 16);
       BTCLoader.error(String.format("Message rejected by %s\n  Command %s, Reason %s - %s\n  %s",
                                msg.getPeer().getAddress(), cmd, reason, description, hash));
    }

    /**
     * Process a transaction
     *
     * <p>This method is called when a 'tx' message is received.</p>
     *
     * @param       msg             Message
     * @param       tx              Transaction
     */
    @Override
    public void processTransaction(Message msg, Transaction tx) {
        Peer peer = msg.getPeer();
        Sha256Hash txHash = tx.getHash();
        int reasonCode = 0;
        //
        // Remove the request from the processedRequests list
        //
        synchronized(BTCLoader.pendingRequests) {
            Iterator<PeerRequest> it = BTCLoader.processedRequests.iterator();
            while (it.hasNext()) {
                PeerRequest request = it.next();
                if (request.getType()==InventoryItem.INV_TX && request.getHash().equals(txHash)) {
                    it.remove();
                    break;
                }
            }
        }
        //
        // Ignore the transaction if we have already seen it.  Otherwise, add it to
        // the recent transaction list
        //
        boolean duplicateTx = false;
        synchronized(BTCLoader.txMap) {
            if (BTCLoader.recentTxMap.get(txHash) != null) {
                duplicateTx = true;
            } else {
                BTCLoader.recentTxMap.put(txHash, txHash);
            }
        }
        if (duplicateTx)
            return;
        try {
            //
            // Don't relay the transaction if the version is not 1 (BIP0034)
            //
            if (tx.getVersion() != 1)
                throw new VerificationException(String.format("Transaction version %d is not valid",
                                                tx.getVersion()),
                                                RejectMessage.REJECT_NONSTANDARD, txHash);
            //
            // Verify the transaction
            //
            tx.verify(true);
            //
            // Coinbase transactions cannot be relayed
            //
            if (tx.isCoinBase())
                throw new VerificationException("Coinbase transaction cannot be relayed",
                                                RejectMessage.REJECT_INVALID, txHash);
            //
            // Validate the transaction
            //
            if (!validateTx(tx))
                return;
            //
            // Broadcast the transaction to our peers
            //
            broadcastTx(tx);
            //
            // Process orphan transactions that were waiting on this transaction
            //
            List<StoredTransaction> orphanTxList;
            synchronized(BTCLoader.txMap) {
                orphanTxList = BTCLoader.orphanTxMap.remove(txHash);
            }
            if (orphanTxList != null) {
                for (StoredTransaction orphanStoredTx : orphanTxList) {
                    Transaction orphanTx = orphanStoredTx.getTransaction();
                    if (validateTx(orphanTx))
                        broadcastTx(orphanTx);
                }
            }
            //
            // Clean up the transaction pools
            //
            synchronized(BTCLoader.txMap) {
                // Clean up the transaction memory pool
                if (BTCLoader.txMap.size() > 5000) {
                    Set<Sha256Hash> txSet = BTCLoader.txMap.keySet();
                    Iterator<Sha256Hash> it = txSet.iterator();
                    do {
                        it.next();
                        it.remove();
                    } while (txSet.size() > 4000);
                }
                // Clean up the recent transaction list
                if (BTCLoader.recentTxMap.size() > 5000) {
                    Set<Sha256Hash> txSet = BTCLoader.recentTxMap.keySet();
                    Iterator<Sha256Hash> it = txSet.iterator();
                    do {
                        it.next();
                        it.remove();
                    } while (txSet.size() > 4000);
                }
                // Clean up the spent outputs list
                if (BTCLoader.spentOutputsMap.size() > 25000) {
                    Set<OutPoint> txSet = BTCLoader.spentOutputsMap.keySet();
                    Iterator<OutPoint> it = txSet.iterator();
                    do {
                        it.next();
                        it.remove();
                    } while (txSet.size() > 20000);
                }
                // Clean up the orphan transactions list
                if (BTCLoader.orphanTxMap.size() > 5000) {
                    Set<Sha256Hash> txSet = BTCLoader.orphanTxMap.keySet();
                    Iterator<Sha256Hash> it = txSet.iterator();
                    do {
                        it.next();
                        it.remove();
                    } while (txSet.size() > 4000);
                }
            }
        } catch (EOFException exc) {
           BTCLoader.error(String.format("End-of-data while processing 'tx' message from %s", peer.getAddress()));
            reasonCode = RejectMessage.REJECT_MALFORMED;
            BTCLoader.txRejected.incrementAndGet();
            if (peer.getVersion() >= 70002) {
                Message rejectMsg = RejectMessage.buildRejectMessage(peer, "tx", reasonCode, exc.getMessage());
                BTCLoader.networkHandler.sendMessage(rejectMsg);
            }
        } catch (VerificationException exc) {
           BTCLoader.error(String.format("Message verification failed for 'tx' message from %s\n  %s\n  %s",
                                    peer.getAddress(), exc.getMessage(), exc.getHash()));
            reasonCode = exc.getReason();
            BTCLoader.txRejected.incrementAndGet();
            if (peer.getVersion() >= 70002) {
                Message rejectMsg = RejectMessage.buildRejectMessage(peer, "tx", reasonCode,
                                                                     exc.getMessage(), exc.getHash());
                BTCLoader.networkHandler.sendMessage(rejectMsg);
            }
        }
        //
        // Increment the banscore for the peer if this is an invalid and malformed transaction
        //
        synchronized(peer) {
            if (reasonCode == RejectMessage.REJECT_MALFORMED || reasonCode == RejectMessage.REJECT_INVALID) {
                int banScore = peer.getBanScore() + 5;
                peer.setBanScore(banScore);
                if (banScore >= BTCLoader.MAX_BAN_SCORE)
                    peer.setDisconnect(true);
            }
        }
    }

    /**
     * Process a version message
     *
     * <p>This method is called when a 'version' message is received.  The application
     * should return a 'verack' message to the sender if the connection is accepted.</p>
     *
     * @param       msg             Message
     * @param       localAddress    Local address as seen by the peer
     */
    @Override
    public void processVersion(Message msg, PeerAddress localAddress) {
        Peer peer = msg.getPeer();
        peer.incVersionCount();
       BTCLoader.info(String.format("Peer %s: Protocol level %d, Services %d, Agent %s, Height %d, "+
                               "Relay blocks %s, Relay tx %s",
                               peer.getAddress(), peer.getVersion(), peer.getServices(),
                               peer.getUserAgent(), peer.getHeight(),
                               peer.shouldRelayBlocks()?"Yes":"No",
                               peer.shouldRelayTx()?"Yes":"No"));
        Message ackMsg = VersionAckMessage.buildVersionAckMessage(peer);
        BTCLoader.networkHandler.sendMessage(ackMsg);
        //
        // Set our local address from the Version message if it hasn't been set yet
        //
        if (BTCLoader.listenAddress == null)
            BTCLoader.listenAddress = localAddress;
    }

    /**
     * Process a version acknowledgment
     *
     * <p>This method is called when a 'verack' message is received.</p>
     *
     * @param       msg             Message
     */
    @Override
    public void processVersionAck(Message msg) {
        msg.getPeer().incVersionCount();
    }

    /**
     * Sends a 'merkleblock' message followed by 'tx' messages for the matched transactions
     *
     * @param       peer            Destination peer
     * @param       block           Block containing the transactions
     * @param       matches         List of matching transactions
     */
    public void sendMatchedTransactions(Peer peer, Block block, List<Sha256Hash> matches) {
        //
        // Build the index list for the matching transactions
        //
        List<Integer> txIndexes;
        List<Transaction> txList = block.getTransactions();
        if (matches.isEmpty()) {
            txIndexes = new ArrayList<>();
        } else {
            txIndexes = new ArrayList<>(matches.size());
            int index = 0;
            for (Transaction tx : txList) {
                if (matches.contains(tx.getHash()))
                    txIndexes.add(index);
                index++;
            }
        }
        //
        // Build and send the 'merkleblock' message
        //
        Message blockMsg = MerkleBlockMessage.buildMerkleBlockMessage(peer, block, txIndexes);
        BTCLoader.networkHandler.sendMessage(blockMsg);
        BTCLoader.filteredBlocksSent.incrementAndGet();
        //
        // Send a 'tx' message for each matching transaction
        //
        for(Integer txIndex:txIndexes){
            Transaction tx = txList.get(txIndex);
            Message txMsg = TransactionMessage.buildTransactionMessage(peer, tx);
            BTCLoader.networkHandler.sendMessage(txMsg);
            BTCLoader.txSent.incrementAndGet();
        }
    }

    /**
     * Retry an orphan transaction
     *
     * @param       tx                      Transaction
     */
    public void retryOrphanTransaction(Transaction tx) {
        try {
            if (validateTx(tx))
                broadcastTx(tx);
        } catch (EOFException | VerificationException exc) {
           // Ignore the transaction since it is no longer valid
        }
    }

    /**
     * Validates the transaction
     *
     * @param       tx                      Transaction
     * @return                              TRUE if the transaction is valid
     * @throws      EOFException            End-of-data processing script
     * @throws      VerificationException   Transaction validation failed
     */
    private boolean validateTx(Transaction tx) throws EOFException, VerificationException {
        Sha256Hash txHash = tx.getHash();
        BigInteger totalInput = BigInteger.ZERO;
        BigInteger totalOutput = BigInteger.ZERO;
        boolean nonFinalTx = false;
        boolean nonFinalTxInput = false;
        //
        // The transaction must be final.  If the transaction lock time is specified as a block height,
        // the block height must not be greater than the current chain height+1.  The reference code
        // does not perform a check if a timestamp is used instead of a block height.
        //
        if (tx.getLockTime()<=500000000L && (int)tx.getLockTime()>BTCLoader.networkChainHeight+1)
            nonFinalTx = true;
        //
        // Validate the transaction outputs
        //
        List<TransactionOutput> outputs = tx.getOutputs();
        for (TransactionOutput output : outputs) {
            // Dust transactions are not relayed - a dust transaction is one where the minimum
            // relay fee is greater than 1/3 of the output value, assuming a single 148-byte input
            // to spend the output
            BigInteger chkValue = output.getValue().multiply(BigInteger.valueOf(1000)).divide(
                                        BigInteger.valueOf(3*(output.getScriptBytes().length+9+148)));
            if (chkValue.compareTo(BTCLoader.MIN_TX_RELAY_FEE) < 0)
                throw new VerificationException("Dust transactions are not relayed",
                                                RejectMessage.REJECT_DUST, tx.getHash());
            // Non-standard payment types are not relayed
            int paymentType = Script.getPaymentType(output.getScriptBytes());
            if (paymentType != ScriptOpCodes.PAY_TO_PUBKEY_HASH &&
                                    paymentType != ScriptOpCodes.PAY_TO_PUBKEY &&
                                    paymentType != ScriptOpCodes.PAY_TO_SCRIPT_HASH &&
                                    paymentType != ScriptOpCodes.PAY_TO_MULTISIG &&
                                    paymentType != ScriptOpCodes.PAY_TO_NOBODY) {
                BTCLoader.dumpData("Failing Script", output.getScriptBytes());
                throw new VerificationException("Non-standard payment types are not relayed",
                                                RejectMessage.REJECT_NONSTANDARD, txHash);
            }
            // Add the output value to the total output value for the transaction
            totalOutput = totalOutput.add(output.getValue());
        }
        //
        // Validate the transaction inputs
        //
        List<OutPoint> spentOutputs = new ArrayList<>();
        List<TransactionInput> inputs = tx.getInputs();
        boolean orphanTx = false;
        boolean duplicateTx = false;
        Sha256Hash orphanHash = null;
        for (TransactionInput input : inputs) {
            // A transaction input is non-final if the sequence number is not -1
            if (input.getSeqNumber() != -1)
                nonFinalTxInput = true;
            // Script size must not exceed 500 bytes
            if (input.getScriptBytes().length > 500)
                throw new VerificationException("Input script size greater than 500 bytes",
                                                RejectMessage.REJECT_NONSTANDARD, txHash);
            // Connected output must not be spent
            OutPoint outPoint = input.getOutPoint();
            StoredOutput output = null;
            Sha256Hash spendHash;
            boolean outputSpent = false;
            synchronized(BTCLoader.txMap) {
                spendHash = BTCLoader.spentOutputsMap.get(outPoint);
            }
            if (spendHash == null) {
                // Connected output is not in the recently spent list, check the memory pool
                StoredTransaction outTx;
                synchronized(BTCLoader.txMap) {
                    outTx = BTCLoader.txMap.get(outPoint.getHash());
                }
                if (outTx != null) {
                    // Transaction is in the memory pool, get the connected output
                    Transaction poolTx = outTx.getTransaction();
                    List<TransactionOutput> txOutputs = poolTx.getOutputs();
                    for (TransactionOutput txOutput : txOutputs) {
                        if (txOutput.getIndex() == outPoint.getIndex()) {
                            totalInput = totalInput.add(txOutput.getValue());
                            output = new StoredOutput(txOutput.getIndex(), txOutput.getValue(),
                                                      txOutput.getScriptBytes(), poolTx.isCoinBase());
                            break;
                        }
                    }
                    if (output == null)
                        throw new VerificationException(String.format(
                                                        "Transaction references non-existent output\n  Tx %s",
                                                        txHash), RejectMessage.REJECT_INVALID, txHash);
                } else {
                    // Transaction is not in the memory pool, check the database
                    try {
                        output = BTCLoader.blockStore.getTxOutput(outPoint);
                        if (output == null) {
                            orphanTx = true;
                            orphanHash = outPoint.getHash();
                        } else if (output.isSpent()) {
                            outputSpent = true;
                        } else {
                            totalInput = totalInput.add(output.getValue());
                        }
                    } catch (BlockStoreException exc) {
                        orphanTx = true;
                        orphanHash = outPoint.getHash();
                    }
                }
            } else if (!spendHash.equals(txHash)) {
                outputSpent = true;
            } else {
                duplicateTx = true;
            }
            // Stop now if we have a problem
            if (duplicateTx || orphanTx)
                break;
            // Error if the output has been spent
            if (outputSpent)
                throw new VerificationException("Input already spent", RejectMessage.REJECT_DUPLICATE, txHash);
            // Check for immature coinbase transaction
            if (output.isCoinBase()) {
                try {
                    int txDepth = BTCLoader.blockStore.getTxDepth(outPoint.getHash());
                    txDepth += BTCLoader.networkChainHeight - BTCLoader.blockStore.getChainHeight();
                    if (txDepth < BTCLoader.COINBASE_MATURITY)
                        throw new VerificationException("Spending immature coinbase output",
                                                        RejectMessage.REJECT_INVALID, txHash);
                } catch (BlockStoreException exc) {
                    // Can't check transaction depth - let it go
                }
            }
            // Check for canonical signatures and public keys
            int paymentType = Script.getPaymentType(output.getScriptBytes());
            List<byte[]> dataList = Script.getData(input.getScriptBytes());
            int canonicalType = 0;
            switch (paymentType) {
                case ScriptOpCodes.PAY_TO_PUBKEY:
                    // First data element is signature
                    if (dataList.isEmpty() || !ECKey.isSignatureCanonical(dataList.get(0)))
                        canonicalType = 1;
                    break;
                case ScriptOpCodes.PAY_TO_PUBKEY_HASH:
                    // First data element is signature, second data element is public key
                    if (dataList.isEmpty() || !ECKey.isSignatureCanonical(dataList.get(0)))
                        canonicalType = 1;
                    else if (dataList.size() < 2 || !ECKey.isPubKeyCanonical(dataList.get(1)))
                        canonicalType = 2;
                    break;
                case ScriptOpCodes.PAY_TO_MULTISIG:
                    // All data elements are public keys
                    for (byte[] sigBytes : dataList) {
                        if (!ECKey.isSignatureCanonical(sigBytes)) {
                            canonicalType = 1;
                            break;
                        }
                    }
            }
            if (canonicalType == 1)
                throw new VerificationException("Non-canonical signature", RejectMessage.REJECT_NONSTANDARD, txHash);
            if (canonicalType == 2)
                throw new VerificationException("Non-canonical public key", RejectMessage.REJECT_NONSTANDARD, txHash);
            // Add the output to the spent outputs list
            spentOutputs.add(outPoint);
        }
        //
        // Don't relay a non-final transaction
        //
        if (nonFinalTx && nonFinalTxInput)
            throw new VerificationException("Non-final transactions are not relayed");
        //
        // Ignore a duplicate transaction (race condition among message handler threads)
        //
        if (duplicateTx)
            return false;
        //
        // Save an orphan transaction for later
        //
        if (orphanTx) {
            StoredTransaction storedTx = new StoredTransaction(tx);
            storedTx.setParent(orphanHash);
            synchronized(BTCLoader.txMap) {
                List<StoredTransaction> orphanList = BTCLoader.orphanTxMap.get(orphanHash);
                if (orphanList == null) {
                    orphanList = new ArrayList<>();
                    orphanList.add(storedTx);
                    BTCLoader.orphanTxMap.put(orphanHash, orphanList);
                } else {
                    orphanList.add(storedTx);
                }
            }
            return false;
        }
        //
        // Check for insufficient transaction fee
        //
        BigInteger totalFee = totalInput.subtract(totalOutput);
        if (totalFee.signum() < 0){
            throw new VerificationException("Transaction output value exceeds transaction input value",
                                            RejectMessage.REJECT_INVALID, txHash);
        }
        int txLength = tx.getBytes().length;
        int feeMultiplier = txLength/1000;
        if (txLength > BTCLoader.MAX_FREE_TX_SIZE) {
            BigInteger minFee = BTCLoader.MIN_TX_RELAY_FEE.multiply(BigInteger.valueOf(feeMultiplier+1));
            if (totalFee.compareTo(minFee) < 0){
                throw new VerificationException("Insufficient transaction fee",
                                                RejectMessage.REJECT_INSUFFICIENT_FEE, txHash);
            }
        }
        //
        // Store the transaction in the memory pool (maximum size we will store is 50KB)
        //
        if (txLength <= 50*1024) {
            StoredTransaction storedTx = new StoredTransaction(tx);
            synchronized(BTCLoader.txMap) {
                if (BTCLoader.txMap.get(txHash) == null) {
                    BTCLoader.txMap.put(txHash, storedTx);
                    BTCLoader.txReceived.incrementAndGet();
               for(OutPoint outPoint:spentOutputs){
            	   BTCLoader.spentOutputsMap.put(outPoint, txHash);
               }
                }
            }
        }
        return true;
    }

    /**
     * Broadcast the transaction
     *
     * @param       tx                  Transaction
     * @throws      EOFException        End-of-data processing script
     */
    private void broadcastTx(Transaction tx) throws EOFException {
        Sha256Hash txHash = tx.getHash();
        //
        // Send an 'inv' message to the broadcast peers (full nodes)
        //
        List<InventoryItem> invList = new ArrayList<>(1);
        invList.add(new InventoryItem(InventoryItem.INV_TX, txHash));
        Message invMsg = InventoryMessage.buildInventoryMessage(null, invList);
        invMsg.setInventoryType(InventoryItem.INV_TX);
        BTCLoader.networkHandler.broadcastMessage(invMsg);
        //
        // Copy the current list of Bloom filters
        //
        List<BloomFilter> filters;
        synchronized(BTCLoader.bloomFilters) {
            filters = new ArrayList<>(BTCLoader.bloomFilters);
        }
        //
        // Check each filter for a match and notify the SPV peer
        //
        for (BloomFilter filter : filters) {
            Peer peer = filter.getPeer();
            //
            // Remove the filter if the peer is no longer connected
            //
            if (!peer.isConnected()) {
                synchronized(BTCLoader.bloomFilters) {
                    BTCLoader.bloomFilters.remove(filter);
                }
                continue;
            }
            //
            // Check the transaction against the filter and send an 'inv' message if it is a match
            //
            if (filter.checkTransaction(tx)) {
                invMsg = InventoryMessage.buildInventoryMessage(peer, invList);
                BTCLoader.networkHandler.sendMessage(invMsg);
            }
        }
    }
}
