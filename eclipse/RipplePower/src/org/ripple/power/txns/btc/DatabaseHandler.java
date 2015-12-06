package org.ripple.power.txns.btc;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class DatabaseHandler implements Runnable {

	private final Map<Sha256Hash, Sha256Hash> txMap = new HashMap<>(50);

	List<BlockStoreListener> listeners = new LinkedList<BlockStoreListener>();

	/** Database timer */
	private Timer timer;

	/** Timer task to delete spent outputs */
	private TimerTask timerTask;

	/** Database shutdown requested */
	private boolean databaseShutdown = false;

	/** 'getblocks' chain height */
	private int getblocksHeight = 0;

	/** 'getblocks' time */
	private long getblocksTime = 0;

	/**
	 * Creates the database listener
	 */
	public DatabaseHandler() {
	}

	/**
	 * Shutdown the database handler
	 */
	public void shutdown() {
		try {
			databaseShutdown = true;
			BTCLoader.databaseQueue.put(new ShutdownDatabase());
		} catch (InterruptedException exc) {
			BTCLoader.warn("Database handler shutdown interrupted", exc);
		}
	}

	private int rescanHeight = 0;

	public void addListener(BlockStoreListener listener) {
		listeners.add(listener);
	}

	public void rescanChain(long rescanTime) throws BlockStoreException {

		rescanHeight = BTCLoader.blockStore.getRescanHeight(rescanTime);

		if (rescanHeight > 0) {
			BTCLoader.info(String.format(
					"Block chain rescan started at height %d", rescanHeight));
			Sha256Hash blockHash = BTCLoader.blockStore
					.getBlockHash(rescanHeight);
			PeerRequest request = new PeerRequest(blockHash,
					InventoryItem.INV_FILTERED_BLOCK);
			synchronized (BTCLoader.lock) {
				BTCLoader.pendingRequests.add(request);
			}
			BTCLoader.networkHandler.wakeup();
		}
	}

	/**
	 * Starts the database listener running
	 */
	@Override
	public void run() {
		BTCLoader.info("Database handler started");
		//
		// Create a timer to delete spent transaction outputs
		//
		timer = new Timer();
		timerTask = new DeleteOutputsTask();
		timer.schedule(timerTask, 15 * 60 * 1000);
		try {
			//
			// Handle any pending blocks before accepting new blocks
			//
			processPendingBlocks();
			//
			// Process blocks until the shutdown() method is called
			//
			for (;;) {
				//
				// Get the next block from the database queue, blocking if no
				// block is available
				//
				Object obj = BTCLoader.databaseQueue.take();
				if (databaseShutdown) {
					break;
				}
				if (obj instanceof Block) {
					//
					// Process the block
					//
					processBlock((Block) obj);
					//
					// Get the next group of blocks if we are synchronizing with
					// the network
					//
					int chainHeight = BTCLoader.blockStore.getChainHeight();
					if (chainHeight < BTCLoader.networkChainHeight - 100
							&& (getblocksHeight < chainHeight - 300 || getblocksTime < System
									.currentTimeMillis() - 60000)
							&& BTCLoader.networkHandler != null) {
						getblocksHeight = chainHeight;
						getblocksTime = System.currentTimeMillis();
						BTCLoader.networkHandler.getBlocks();
					} else if (obj instanceof BlockHeader) {
						processBlock(new StoredHeader((BlockHeader) obj));
						if (BTCLoader.databaseQueue.isEmpty()) {
							if (BTCLoader.blockStore.getChainHeight() >= BTCLoader.networkChainHeight) {
								BTCLoader.loadingChain = false;
							} else {
								BTCLoader.networkHandler.getBlocks();
							}
						}
					} else if (obj instanceof Transaction) {
						processTransaction((Transaction) obj);
					}
				}
			}
		} catch (InterruptedException exc) {
			BTCLoader.warn("Database handler interrupted", exc);
		} catch (Throwable exc) {
			BTCLoader.error("Runtime exception while processing blocks", exc);
		}
		//
		// Stopping
		//
		timerTask.cancel();
		timer.cancel();
		BTCLoader.info("Database handler stopped");
	}

	private void processBlock(StoredHeader blockHeader) {
		Sha256Hash blockHash = blockHeader.getHash();
		try {

			synchronized (BTCLoader.lock) {
				List<Sha256Hash> matches = blockHeader.getMatches();
				if (matches != null) {
					for (Sha256Hash txHash : matches) {
						if (BTCLoader.blockStore.isNewTransaction(txHash)) {
							txMap.put(txHash, blockHash);
						}
					}
				}
			}

			if (BTCLoader.blockStore.isNewBlock(blockHash)) {

				BTCLoader.blockStore.storeHeader(blockHeader);
				updateChain(blockHeader);
				if (blockHeader.isOnChain()) {
					Sha256Hash parentHash = blockHash;
					while (parentHash != null)
						parentHash = processChildBlock(parentHash);
				}
			} else {

				BTCLoader.blockStore.updateMatches(blockHeader);
				if (rescanHeight != 0) {

					rescanHeight++;
					if (rescanHeight > BTCLoader.blockStore.getChainHeight()) {
						rescanHeight = 0;
						BTCLoader.info("Block rescan completed");
						for (BlockStoreListener listener : listeners) {
							listener.rescanCompleted();
						}
					} else {
						if (rescanHeight % 1000 == 0)
							BTCLoader.debug(String.format(
									"Block rescan at block %d", rescanHeight));
						Sha256Hash nextHash = BTCLoader.blockStore
								.getBlockHash(rescanHeight);
						PeerRequest request = new PeerRequest(nextHash,
								InventoryItem.INV_FILTERED_BLOCK);
						synchronized (BTCLoader.lock) {
							BTCLoader.pendingRequests.add(request);
						}
						BTCLoader.networkHandler.wakeup();
					}
				} else {

					StoredHeader chkHeader = BTCLoader.blockStore
							.getHeader(blockHash);
					if (!chkHeader.isOnChain()) {
						updateChain(blockHeader);
						if (blockHeader.isOnChain()) {
							Sha256Hash parentHash = blockHash;
							while (parentHash != null)
								parentHash = processChildBlock(parentHash);
						}
					}
				}
			}
		} catch (BlockNotFoundException exc) {
			PeerRequest request = new PeerRequest(exc.getHash(),
					InventoryItem.INV_FILTERED_BLOCK);
			boolean wakeup = false;
			synchronized (BTCLoader.lock) {
				if (!BTCLoader.pendingRequests.contains(request)
						&& !BTCLoader.processedRequests.contains(request)) {
					BTCLoader.pendingRequests.add(request);
					wakeup = true;
				}
			}
			if (wakeup)
				BTCLoader.networkHandler.wakeup();
		} catch (VerificationException exc) {
			BTCLoader.error(
					String.format("Checkpoint verification failed\n  %s",
							exc.getHash()), exc);
		} catch (BlockStoreException exc) {
			BTCLoader.error(
					String.format("Unable to process block\n  %s",
							blockHash.toString()), exc);
		}
	}


	private void updateChain(StoredHeader blockHeader)
			throws VerificationException, BlockStoreException {

		List<StoredHeader> chainList = BTCLoader.blockStore.getJunctionHeader(blockHeader
				.getPrevHash());
		chainList.add(blockHeader);

		StoredHeader chainHeader = chainList.get(0);
		BigInteger chainWork = chainHeader.getChainWork();
		int blockHeight = chainHeader.getBlockHeight();
		for (int i = 1; i < chainList.size(); i++) {
			chainHeader = chainList.get(i);
			chainWork = chainWork.add(chainHeader.getBlockWork());
			chainHeader.setChainWork(chainWork);
			chainHeader.setBlockHeight(++blockHeight);
		}

		if (blockHeader.getChainWork().compareTo(
				BTCLoader.blockStore.getChainWork()) > 0) {
			BTCLoader.blockStore.setChainStoredHead(chainList);
			for (int i = 1; i < chainList.size(); i++) {
				chainHeader = chainList.get(i);
				chainHeader.setChain(true);
				for (BlockStoreListener listener : listeners){
					listener.addChainBlock(chainHeader);
				}
			}
			BTCLoader.networkChainHeight = Math.max(
					BTCLoader.networkChainHeight, blockHeader.getBlockHeight());
		} else {
			BTCLoader
					.debug(String
							.format("Block not added to chain: New chain work %d, Current chain work %d\n  Block %s",
									blockHeader.getChainWork(),
									BTCLoader.blockStore.getChainWork(),
									blockHeader.getHash()));
		}
	}

	private Sha256Hash processChildBlock(Sha256Hash parentHash)
			throws VerificationException, BlockStoreException {
		Sha256Hash nextParent = null;
		StoredHeader childHeader = BTCLoader.blockStore.getChildHeader(parentHash);
		if (childHeader != null && !childHeader.isOnChain()) {
			updateChain(childHeader);
			if (childHeader.isOnChain()){
				nextParent = childHeader.getHash();
			}
		}
		return nextParent;
	}

	/**
	 * Process a block
	 *
	 * @param block
	 *            Block to process
	 */
	private void processBlock(Block block) {
		try {
			//
			// Process the new block
			//
			List<StoredBlock> chainList = null;
			StoredBlock storedBlock = BTCLoader.blockStore.getStoredBlock(block
					.getHash());
			if (storedBlock == null) {
				//
				// Add a new block to our database
				//
				chainList = BTCLoader.blockChain.storeBlock(block);
			} else if (!storedBlock.isOnChain()) {
				//
				// Attempt to connect an existing block to the current block
				// chain
				//
				chainList = BTCLoader.blockChain.updateBlockChain(storedBlock);
			}
			//
			// Notify our peers that we have added new blocks to the chain and
			// then
			// see if we have a child block which can now be processed. To avoid
			// flooding peers with blocks they have already seen, we won't send
			// an
			// 'inv' message if we are more than 3 blocks behind the best
			// network chain.
			//
			if (chainList != null) {
				for (StoredBlock chainStoredBlock : chainList) {
					Block chainBlock = chainStoredBlock.getBlock();
					if (chainBlock != null) {
						updateTxPool(chainBlock);
						int chainHeight = chainStoredBlock.getHeight();
						BTCLoader.networkChainHeight = Math.max(chainHeight,
								BTCLoader.networkChainHeight);
						if (chainHeight >= BTCLoader.networkChainHeight - 3)
							notifyPeers(chainStoredBlock);
					}
				}
				StoredBlock parentBlock = chainList.get(chainList.size() - 1);
				while (parentBlock != null && !databaseShutdown)
					parentBlock = processChildBlock(parentBlock);
			}
			//
			// Remove the request from the processedRequests list
			//
			synchronized (BTCLoader.pendingRequests) {
				Iterator<PeerRequest> it = BTCLoader.processedRequests
						.iterator();
				while (it.hasNext()) {
					PeerRequest request = it.next();
					if (request.getType() == InventoryItem.INV_BLOCK
							&& request.getHash().equals(block.getHash())) {
						it.remove();
						break;
					}
				}
			}
		} catch (BlockStoreException exc) {
			BTCLoader.error(String.format(
					"Unable to store block in database\n  Block %s",
					block.getHashAsString()), exc);
		}
	}

	/**
	 * Connect pending blocks to the current block chain
	 *
	 * @throws BlockStoreException
	 *             Database error occurred
	 */
	private void processPendingBlocks() throws BlockStoreException {
		StoredBlock parentBlock = BTCLoader.blockStore
				.getStoredBlock(BTCLoader.blockStore.getChainHead());
		while (parentBlock != null && !databaseShutdown)
			parentBlock = processChildBlock(parentBlock);
	}

	/**
	 * Process a child block and see if it can now be added to the chain
	 *
	 * @param storedBlock
	 *            The updated block
	 * @return Next parent block or null
	 * @throws BlockStoreException
	 *             Database error occurred
	 */
	private StoredBlock processChildBlock(StoredBlock storedBlock)
			throws BlockStoreException {
		StoredBlock parentBlock = null;
		StoredBlock childStoredBlock = BTCLoader.blockStore
				.getChildStoredBlock(storedBlock.getHash());
		if (childStoredBlock != null && !childStoredBlock.isOnChain()) {
			//
			// Update the chain with the child block
			//
			BTCLoader.blockChain.updateBlockChain(childStoredBlock);
			if (childStoredBlock.isOnChain()) {
				updateTxPool(childStoredBlock.getBlock());
				//
				// Notify our peers about this block. To avoid
				// flooding peers with blocks they have already seen, we won't
				// send an
				// 'inv' message if we are more than 3 blocks behind the best
				// network chain.
				//
				int chainHeight = childStoredBlock.getHeight();
				BTCLoader.networkChainHeight = Math.max(chainHeight,
						BTCLoader.networkChainHeight);
				if (chainHeight >= BTCLoader.networkChainHeight - 3)
					notifyPeers(childStoredBlock);
				//
				// Continue working our way up the chain
				//
				parentBlock = childStoredBlock;
			}
		}
		return parentBlock;
	}

	/**
	 * Remove the transactions in the current block from the memory pool, update
	 * the spent outputs map, and retry orphan transactions
	 *
	 * @param block
	 *            The current block
	 * @throws BlockStoreException
	 *             Database error occurred
	 */
	private void updateTxPool(Block block) throws BlockStoreException {
		List<Transaction> txList = block.getTransactions();
		List<StoredTransaction> retryList = new ArrayList<>();
		synchronized (BTCLoader.txMap) {
			for(Transaction tx:txList){

				Sha256Hash txHash = tx.getHash();
				//
				// Remove the transaction from the transaction maps
				//
					BTCLoader.txMap.remove(txHash);
					BTCLoader.recentTxMap.remove(txHash);
					//
					// Remove spent outputs from the map since they are now
					// updated in the database
					//
					List<TransactionInput> txInputs = tx.getInputs();
					for(TransactionInput txInput:txInputs){
						BTCLoader.spentOutputsMap
						.remove(txInput.getOutPoint());
					}
					//
					// Get orphan transactions dependent on this transaction
					//
					List<StoredTransaction> orphanList = BTCLoader.orphanTxMap
							.remove(txHash);
					if (orphanList != null)
						retryList.addAll(orphanList);
				
			}
	
		}
		//
		// Retry orphan transactions that are not in the database
		//
		for (StoredTransaction orphan : retryList) {
			if (BTCLoader.blockStore.isNewTransaction(orphan.getHash()))
				BTCLoader.networkMessageListener.retryOrphanTransaction(orphan
						.getTransaction());
		}
	}

	/**
	 * Notify peers when a block has been added to the chain
	 *
	 * @param storedBlock
	 *            The stored block added to the chain
	 */
	private void notifyPeers(StoredBlock storedBlock) {
		List<InventoryItem> invList = new ArrayList<>(1);
		invList.add(new InventoryItem(InventoryItem.INV_BLOCK, storedBlock
				.getHash()));
		Message invMsg = InventoryMessage.buildInventoryMessage(null, invList);
		invMsg.setInventoryType(InventoryItem.INV_BLOCK);
		BTCLoader.networkHandler.broadcastMessage(invMsg);
	}

	public void processTransaction(Transaction tx) {
		Sha256Hash txHash = tx.getHash();
		Sha256Hash blockHash;
		long txTime;
		boolean txUpdated = false;
		try {

			synchronized (BTCLoader.lock) {
				blockHash = txMap.get(txHash);
				if (blockHash != null)
					txMap.remove(txHash);
			}
			if (blockHash != null) {
				StoredHeader blockHeader = BTCLoader.blockStore
						.getHeader(blockHash);
				txTime = blockHeader.getBlockTime();
				if (!blockHeader.isOnChain())
					blockHash = null;
			} else {
				txTime = System.currentTimeMillis() / 1000;
			}

			if (BTCLoader.blockStore.isNewTransaction(txHash)) {

				List<TransactionOutput> txOutputs = tx.getOutputs();
				BigInteger totalValue = BigInteger.ZERO;
				BigInteger totalChange = BigInteger.ZERO;
				for (int txIndex = 0; txIndex < txOutputs.size(); txIndex++) {
					TransactionOutput txOutput = txOutputs.get(txIndex);
					totalValue = totalValue.add(txOutput.getValue());
					ECKey key = (ECKey) checkAddress(txOutput, true);
					if (key != null) {
						if (key.isChange())
							totalChange = totalChange.add(txOutput.getValue());
						ReceiveTransaction rcvTx = new ReceiveTransaction(
								tx.getNormalizedID(), txHash, txIndex, txTime,
								blockHash, key.toAddress(),
								txOutput.getValue(), txOutput.getScriptBytes(),
								key.isChange(), tx.isCoinBase());
						BTCLoader.blockStore.storeReceiveTx(rcvTx);
						txUpdated = true;
					}
				}

				boolean isRelevant = false;
				List<ReceiveTransaction> rcvList = BTCLoader.blockStore
						.getReceiveTxList();
				List<TransactionInput> txInputs = tx.getInputs();
				BigInteger totalInput = BigInteger.ZERO;
				for (TransactionInput txInput : txInputs) {
					OutPoint txOutPoint = txInput.getOutPoint();
					for (ReceiveTransaction rcv : rcvList) {
						if (rcv.getTxHash().equals(txOutPoint.getHash())
								&& rcv.getTxIndex() == txOutPoint.getIndex()) {
							totalInput = totalInput.add(rcv.getValue());
							BTCLoader.blockStore.setTxSpent(rcv.getTxHash(),
									rcv.getTxIndex(), true);
							isRelevant = true;
							txUpdated = true;
							break;
						}
					}
				}

				if (isRelevant) {
					Address address = null;
					for (TransactionOutput txOutput : txOutputs) {
						address = (Address) checkAddress(txOutput, false);
						if (address != null) {
							break;
						}
					}
					if (address != null) {
						BigInteger fee = totalInput.subtract(totalValue);
						BigInteger sentValue = totalValue.subtract(totalChange);
						SendTransaction sendTx = new SendTransaction(
								tx.getNormalizedID(), txHash, txTime - 15,
								blockHash, address, sentValue, fee,
								tx.getBytes());
						BTCLoader.blockStore.storeSendTx(sendTx);
					}
				}

				if (txUpdated) {
					for (BlockStoreListener listener : listeners) {
						listener.txUpdated();
					}
				}
			}
		} catch (BlockStoreException exc) {
			BTCLoader.error(String.format(
					"Unable to process transaction\n  %s", txHash), exc);
		}
	}

	/**
	 * Timer task to delete spent transaction outputs
	 */
	private class DeleteOutputsTask extends TimerTask {

		/** Task is active */
		private volatile boolean isSleeping = false;

		/** Execution thread */
		private volatile Thread thread;

		/**
		 * Create the timer task
		 */
		public DeleteOutputsTask() {
			super();
		}

		/**
		 * Delete spent outputs every hour. The task will run until all spent
		 * outputs are deleted before scheduling the next execution. 1000
		 * outputs will be deleted in each batch with a 30-second interval
		 * between each database request.
		 */
		@Override
		public void run() {
			//
			// Indicate task is active
			//
			thread = Thread.currentThread();
			try {
				//
				// Delete spent transaction outputs at 30 second intervals
				//
				int count;
				do {
					isSleeping = true;
					Thread.sleep(30000);
					isSleeping = false;
					if (databaseShutdown) {
						break;
					}
					count = BTCLoader.blockStore.deleteSpentTxOutputs();
				} while (count > 0 && !databaseShutdown);
				//
				// Schedule the next execution in one hour
				//
				timerTask = new DeleteOutputsTask();
				timer.schedule(timerTask, 60 * 60 * 1000);
			} catch (BlockStoreException exc) {
				BTCLoader.error("Unable to delete spent transaction outputs",
						exc);
			} catch (InterruptedException exc) {
				BTCLoader.info("Database prune task terminated");
			} catch (Throwable exc) {
				BTCLoader
						.error("Unexpected exception while deleting spent transaction outputs",
								exc);
			}
			//
			// Indicate task is no longer active
			//
			thread = null;
		}

		/**
		 * Cancel task execution
		 *
		 * @return TRUE if a future execution was cancelled
		 */
		@Override
		public boolean cancel() {
			//
			// Cancel the current execution
			//
			try {
				while (thread != null) {
					if (isSleeping) {
						thread.interrupt();
					}
					Thread.sleep(1000);
				}
			} catch (InterruptedException exc) {
				BTCLoader
						.error("Unable to wait for database prune task to complete");
			}
			//
			// Cancel future execution
			//
			return super.cancel();
		}
	}

	private Object checkAddress(TransactionOutput txOutput, boolean ourAddress) {
		Object result = null;

		byte[] scriptBytes = txOutput.getScriptBytes();
		if (scriptBytes.length == 25
				&& scriptBytes[0] == (byte) ScriptOpCodes.OP_DUP
				&& scriptBytes[1] == (byte) ScriptOpCodes.OP_HASH160
				&& scriptBytes[2] == 20
				&& scriptBytes[23] == (byte) ScriptOpCodes.OP_EQUALVERIFY
				&& scriptBytes[24] == (byte) ScriptOpCodes.OP_CHECKSIG) {

			byte[] scriptAddress = Arrays.copyOfRange(scriptBytes, 3, 23);
			synchronized (BTCLoader.lock) {
				for (ECKey chkKey : BTCLoader.keys) {
					if (Arrays.equals(chkKey.getPubKeyHash(), scriptAddress)) {
						result = chkKey;
						break;
					}
				}
			}

			if (!ourAddress) {
				if (result == null) {
					result = new Address(scriptAddress);
				} else if (((ECKey) result).isChange()) {
					result = null;
				} else {
					result = ((ECKey) result).toAddress();
				}
			}
		}
		return result;
	}
}
