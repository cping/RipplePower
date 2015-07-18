package org.ripple.power.txns.btc;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DatabaseHandler implements Runnable {

	private Thread handlerThread;

	private boolean handlerShutdown = false;

	private final Map<Sha256Hash, Sha256Hash> txMap = new HashMap<>(50);

	List<WalletListener> listeners = new LinkedList<>();

	private int rescanHeight = 0;

	public DatabaseHandler() {
	}

	public void shutdown() {
		handlerShutdown = true;
		handlerThread.interrupt();
	}

	public void addListener(WalletListener listener) {
		listeners.add(listener);
	}

	public void rescanChain(long rescanTime) throws WalletException {
	
		rescanHeight = BTCLoader.wallet.getRescanHeight(rescanTime);

		if (rescanHeight > 0) {
			BTCLoader.info(String.format(
					"Block chain rescan started at height %d", rescanHeight));
			Sha256Hash blockHash = BTCLoader.wallet.getBlockHash(rescanHeight);
			PeerRequest request = new PeerRequest(blockHash,
					InventoryItem.INV_FILTERED_BLOCK);
			synchronized (BTCLoader.lock) {
				BTCLoader.pendingRequests.add(request);
			}
			BTCLoader.networkHandler.wakeup();
		}
	}

	@Override
	public void run() {
		BTCLoader.info("Database handler started");
		handlerThread = Thread.currentThread();
		try {
			while (!handlerShutdown) {
				Object obj = BTCLoader.databaseQueue.take();
				if (obj instanceof BlockHeader) {
					processBlock(new StoredHeader((BlockHeader) obj));
					if (BTCLoader.databaseQueue.isEmpty()) {
						if (BTCLoader.wallet.getChainHeight() >= BTCLoader.networkChainHeight)
							BTCLoader.loadingChain = false;
						else
							BTCLoader.networkHandler.getBlocks();
					}
				} else if (obj instanceof Transaction) {
					processTransaction((Transaction) obj);
				}
			}
		} catch (InterruptedException exc) {
			if (!handlerShutdown)
				BTCLoader.warn("Database handler interrupted", exc);
		} catch (Exception exc) {
			BTCLoader.error("Exception while processing request", exc);
		}
		BTCLoader.info("Database handler stopped");
	}


	private void processBlock(StoredHeader blockHeader) {
		Sha256Hash blockHash = blockHeader.getHash();
		try {
		
			synchronized (BTCLoader.lock) {
				List<Sha256Hash> matches = blockHeader.getMatches();
				if (matches != null) {
					for (Sha256Hash txHash : matches) {
						if (BTCLoader.wallet.isNewTransaction(txHash))
							txMap.put(txHash, blockHash);
					}
				}
			}
		
			if (BTCLoader.wallet.isNewBlock(blockHash)) {
		
				BTCLoader.wallet.storeHeader(blockHeader);
				updateChain(blockHeader);
				if (blockHeader.isOnChain()) {
					Sha256Hash parentHash = blockHash;
					while (parentHash != null)
						parentHash = processChildBlock(parentHash);
				}
			} else {
	
				BTCLoader.wallet.updateMatches(blockHeader);
				if (rescanHeight != 0) {
			
					rescanHeight++;
					if (rescanHeight > BTCLoader.wallet.getChainHeight()) {
						rescanHeight = 0;
						BTCLoader.info("Block rescan completed");
						for (WalletListener listener : listeners) {
							listener.rescanCompleted();
						}
					} else {
						if (rescanHeight % 1000 == 0)
							BTCLoader.debug(String.format(
									"Block rescan at block %d", rescanHeight));
						Sha256Hash nextHash = BTCLoader.wallet
								.getBlockHash(rescanHeight);
						PeerRequest request = new PeerRequest(nextHash,
								InventoryItem.INV_FILTERED_BLOCK);
						synchronized (BTCLoader.lock) {
							BTCLoader.pendingRequests.add(request);
						}
						BTCLoader.networkHandler.wakeup();
					}
				} else {
		
					StoredHeader chkHeader = BTCLoader.wallet
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
		} catch (WalletException exc) {
			BTCLoader.error(
					String.format("Unable to process block\n  %s",
							blockHash.toString()), exc);
		}
	}

	private void updateChain(StoredHeader blockHeader)
			throws VerificationException, WalletException {

		List<StoredHeader> chainList = BTCLoader.wallet.getJunction(blockHeader
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
				BTCLoader.wallet.getChainWork()) > 0) {
			BTCLoader.wallet.setChainHead(chainList);
			for (int i = 1; i < chainList.size(); i++) {
				chainHeader = chainList.get(i);
				chainHeader.setChain(true);
				for (WalletListener listener : listeners)
					listener.addChainBlock(chainHeader);
			}
			BTCLoader.networkChainHeight = Math.max(
					BTCLoader.networkChainHeight, blockHeader.getBlockHeight());
		} else {
			BTCLoader
					.debug(String
							.format("Block not added to chain: New chain work %d, Current chain work %d\n  Block %s",
									blockHeader.getChainWork(),
									BTCLoader.wallet.getChainWork(),
									blockHeader.getHash()));
		}
	}

	private Sha256Hash processChildBlock(Sha256Hash parentHash)
			throws VerificationException, WalletException {
		Sha256Hash nextParent = null;
		StoredHeader childHeader = BTCLoader.wallet.getChildHeader(parentHash);
		if (childHeader != null && !childHeader.isOnChain()) {
			updateChain(childHeader);
			if (childHeader.isOnChain())
				nextParent = childHeader.getHash();
		}
		return nextParent;
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
				StoredHeader blockHeader = BTCLoader.wallet
						.getHeader(blockHash);
				txTime = blockHeader.getBlockTime();
				if (!blockHeader.isOnChain())
					blockHash = null;
			} else {
				txTime = System.currentTimeMillis() / 1000;
			}

			if (BTCLoader.wallet.isNewTransaction(txHash)) {

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
						BTCLoader.wallet.storeReceiveTx(rcvTx);
						txUpdated = true;
					}
				}

				boolean isRelevant = false;
				List<ReceiveTransaction> rcvList = BTCLoader.wallet
						.getReceiveTxList();
				List<TransactionInput> txInputs = tx.getInputs();
				BigInteger totalInput = BigInteger.ZERO;
				for (TransactionInput txInput : txInputs) {
					OutPoint txOutPoint = txInput.getOutPoint();
					for (ReceiveTransaction rcv : rcvList) {
						if (rcv.getTxHash().equals(txOutPoint.getHash())
								&& rcv.getTxIndex() == txOutPoint.getIndex()) {
							totalInput = totalInput.add(rcv.getValue());
							BTCLoader.wallet.setTxSpent(rcv.getTxHash(),
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
						if (address != null){
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
						BTCLoader.wallet.storeSendTx(sendTx);
					}
				}
		
				if (txUpdated) {
					for (WalletListener listener : listeners) {
						listener.txUpdated();
					}
				}
			}
		} catch (WalletException exc) {
			BTCLoader.error(String.format(
					"Unable to process transaction\n  %s", txHash), exc);
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
