package org.ripple.power.txns.btc;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BlockChain {

    /** Verify blocks */
    private final boolean verifyBlocks;

    /** Chain listeners */
    private final List<ChainListener> listeners = new ArrayList<ChainListener>();

    /**
     * Creates a new block chain
     *
     * @param       verifyBlocks    TRUE if new blocks should be verified
     */
    public BlockChain(boolean verifyBlocks) {
        this.verifyBlocks = verifyBlocks;
    }

    /**
     * Registers a chain listener
     *
     * @param       chainListener   The chain listener
     */
    public void addListener(ChainListener chainListener) {
        listeners.add(chainListener);
    }

    /**
     * Adds a block to the block store and updates the block chain
     *
     * @param       block                   The block to add
     * @return                              List of blocks that have been added to the chain.
     *                                      The first element in the list is the junction block
     *                                      and will not contain any block data.  The list will
     *                                      be null if no blocks have been added to the chain.
     * @throws      BlockStoreException     Unable to store the block in the database
     */
    public List<StoredBlock> storeBlock(Block block) throws BlockStoreException {
        //
        // Store the block in the database with hold status until we have verified the block
        //
        StoredBlock storedBlock = new StoredBlock(block, BigInteger.ZERO, 0);
        storedBlock.setHold(true);
        BTCLoader.blockStore.storeBlock(storedBlock);
        for(ChainListener listener:listeners){
        	listener.blockStored(storedBlock);
        }
        //
        // Update the block chain and return the chain list to our caller
        //
        return updateBlockChain(storedBlock);
    }

    /**
     * Updates the block chain to reflect a new or updated block.
     * The block must be in the block store and must be on hold if this
     * is a new block that hasn't been verified yet.
     *
     * @param       storedBlock         The new or updated stored block
     * @return                          List of blocks that have been added to the chain.
     *                                  The first element in the list is the junction block
     *                                  and will not contain any block data.  The list will
     *                                  be null if no blocks have been added to the chain.
     * @throws      BlockStoreException Unable to update the block chain in the database
     */
    public List<StoredBlock> updateBlockChain(StoredBlock storedBlock) throws BlockStoreException {
        List<StoredBlock> chainList = null;
        Map<Sha256Hash, Transaction> txMap = null;
        Map<Sha256Hash, List<StoredOutput>> outputMap = null;
        boolean onHold = false;
        Block block = storedBlock.getBlock();
        //
        // Locate the chain containing this block and map the transactions in the chain.
        // We will need this information when validating transactions since these transactions
        // are not in the database yet.
        //
        // A BlockNotFoundException is thrown if a block in the chain is not in the database.
        // This can happen if we receive blocks out-of-order.  In this case, we need to place
        // the new block on hold until we receive another block in the chain.  We will add
        // the missing block to the list of blocks to be fetched from a peer.
        //
        // A ChainTooLongException is thrown if the block chain exceeds 144 blocks.  This is
        // done to avoid running out of storage as the unresolved chain increases in size.
        // The exception contains the hash for the restart block.  We will recursively call
        // ourself to work our way down to the junction block.
        //
        boolean buildChain = true;
        while (buildChain && !onHold) {
            try {
                chainList = BTCLoader.blockStore.getJunction(block.getPrevBlockHash());
                txMap = new HashMap<>(chainList.size());
                outputMap = new HashMap<>(chainList.size()*250);
                for (StoredBlock chainStoredBlock : chainList) {
                    Block chainBlock = chainStoredBlock.getBlock();
                    if (chainBlock != null) {
                        List<Transaction> txList = chainBlock.getTransactions();
                        for (Transaction tx : txList)
                            txMap.put(tx.getHash(), tx);
                    }
                }
                List<Transaction> txList = block.getTransactions();
                for (Transaction tx : txList)
                    txMap.put(tx.getHash(), tx);
                buildChain = false;
            } catch (ChainTooLongException exc) {
                Sha256Hash chainHash = exc.getHash();
                StoredBlock chainStoredBlock = BTCLoader.blockStore.getStoredBlock(chainHash);
                chainList = updateBlockChain(chainStoredBlock);
                if (chainList == null) {
                    buildChain = false;
                    onHold = true;
                }
            } catch (BlockNotFoundException exc) {
                onHold = true;
                if (BTCLoader.networkHandler != null) {
                    PeerRequest request = new PeerRequest(exc.getHash(), InventoryItem.INV_BLOCK);
                    synchronized(BTCLoader.pendingRequests) {
                        if (!BTCLoader.pendingRequests.contains(request) &&
                                                !BTCLoader.processedRequests.contains(request))
                            BTCLoader.pendingRequests.add(request);
                    }
                    BTCLoader.networkHandler.wakeup();
                }
            }
        }
        if (onHold)
            return null;
        //
        // The block version must be 2 (or greater) if the chain height is 250,000 or greater
        //
        long version = block.getVersion();
        if (BTCLoader.blockStore.getChainHeight() >= 250000 && version < 2) {
            BTCLoader.error(String.format("Block version %d is no longer acceptable", version));
            return null;
        }
        //
        // Check for any held blocks in the chain.  If we find one, attempt to verify it.
        // If the verification fails, we will need to wait until another block is received
        // before we can try to verify the chain again.
        //
        BigInteger chainWork = chainList.get(0).getChainWork();
        int chainHeight = chainList.get(0).getHeight();
        for (StoredBlock chainStoredBlock : chainList) {
            Block chainBlock = chainStoredBlock.getBlock();
            if (chainBlock != null) {
                chainWork = chainWork.add(chainBlock.getWork());
                chainStoredBlock.setChainWork(chainWork);
                chainStoredBlock.setHeight(++chainHeight);
                if (chainStoredBlock.isOnHold()) {
                    if (verifyBlocks) {
                        if (!verifyBlock(chainStoredBlock, chainList.get(0).getHeight(), txMap, outputMap)) {
                            BTCLoader.info(String.format("Failed to verify held block\n  Block %s",
                                                   chainBlock.getHashAsString()));
                            onHold = true;
                            break;
                        }
                    }
                    chainStoredBlock.setHold(false);
                    BTCLoader.blockStore.releaseBlock(chainStoredBlock.getHash());
                    BTCLoader.info(String.format(String.format("Held block released\n  Block %s",
                                                         chainBlock.getHashAsString())));
                    for(ChainListener listener:listeners){
                    	listener.blockUpdated(chainStoredBlock);
                    }

                }
            }
        }
        //
        // Update the new block
        //
        if (!onHold) {
            chainWork = chainWork.add(block.getWork());
            storedBlock.setChainWork(chainWork);
            storedBlock.setHeight(++chainHeight);
        }
        //
        // Verify the transactions for the new block
        //
        if (!onHold && verifyBlocks) {
            if (!verifyBlock(storedBlock, chainList.get(0).getHeight(), txMap, outputMap)) {
                BTCLoader.info(String.format("Block verification failed\n  Block %s", storedBlock.getHash()));
                onHold = true;
            }
        }
        //
        // Stop now if the block is not ready for processing
        //
        if (onHold)
            return null;
        //
        // Add this block to the end of the chain
        //
        chainList.add(storedBlock);
        //
        // Release the block and update the chain work and block height values in the database
        //
        storedBlock.setHold(false);
        BTCLoader.blockStore.releaseBlock(storedBlock.getHash());
    
        for(ChainListener listener:listeners){
        	listener.blockUpdated(storedBlock);
        }

        //
        // Make this block the new chain head if it is a better chain than the current chain.
        // This means the cumulative chain work is greater.
        //
        if (storedBlock.getChainWork().compareTo(BTCLoader.blockStore.getChainWork()) > 0) {
            try {
                BTCLoader.blockStore.setChainHead(chainList);
                for (StoredBlock updatedStoredBlock : chainList) {
                    Block updatedBlock = updatedStoredBlock.getBlock();
                    if (updatedBlock == null)
                        continue;
                    //
                    // Notify listeners that we updated the block
                    //
                    updatedStoredBlock.setChain(true);
                    for(ChainListener listener:listeners){
                    	listener.blockUpdated(updatedStoredBlock);
                    }
                }
                for(ChainListener listener:listeners){
                	listener.chainUpdated();
                }
                //
                // Delete spent transaction outputs if we are caught up with the network
                //
                if (BTCLoader.blockStore.getChainHeight() >= BTCLoader.networkChainHeight)
                    BTCLoader.blockStore.deleteSpentTxOutputs();
            } catch (VerificationException exc) {
                chainList = null;
                BTCLoader.info(String.format("Block being held due to verification failure\n  Block %s", exc.getHash()));
            }
        }
        return chainList;
    }

    /**
     * Verify a block
     *
     * @param       block                   Block to be verified
     * @param       junctionHeight          Height of the junction block
     * @param       txMap                   Transaction map
     * @param       outputMap               Transaction output map
     * @return                              TRUE if the block is verified, FALSE otherwise
     * @throws      BlockStoreException     Unable to read from database
     */
    private boolean verifyBlock(StoredBlock storedBlock, int junctionHeight,
                                            Map<Sha256Hash, Transaction> txMap,
                                            Map<Sha256Hash, List<StoredOutput>> outputMap)
                                            throws BlockStoreException {
        Block block = storedBlock.getBlock();
        boolean txValid = true;
        BigInteger totalFees = BigInteger.ZERO;
        //
        // Check each transaction in the block
        //
        List<Transaction> txList = block.getTransactions();
        for (Transaction tx : txList) {
            //
            // The input script for the coinbase transaction must contain the chain height
            // as the first data element if the block version is 2 (BIP0034)
            //
            if (tx.isCoinBase()) {
                if (block.getVersion() >= 2 && junctionHeight >= 250000) {
                    TransactionInput input = tx.getInputs().get(0);
                    byte[] scriptBytes = input.getScriptBytes();
                    if (scriptBytes.length < 1) {
                        BTCLoader.error(String.format("Coinbase input script is not valid\n  Tx %s", tx.getHash()));
                        txValid = false;
                        break;
                    }
                    int length = (int)scriptBytes[0]&0xff;
                    if (length+1 > scriptBytes.length) {
                        BTCLoader.error(String.format("Coinbase script is too short\n  Tx %s", tx.getHash()));
                        txValid = false;
                        break;
                    }
                    int chainHeight = (int)scriptBytes[1]&0xff;
                    for (int i=1; i<length; i++)
                        chainHeight = chainHeight | (((int)scriptBytes[i+1]&0xff)<<(i*8));
                    if (chainHeight != storedBlock.getHeight()) {
                        BTCLoader.error(String.format("Coinbase height %d does not match block height %d\n  Tx %s",
                                                chainHeight, storedBlock.getHeight(), tx.getHash()));
                        BTCLoader.dumpData("Coinbase Script", scriptBytes);
                        txValid = false;
                        break;
                    }
                }
                continue;
            }
            //
            // Check each input in the transaction
            //
            BigInteger txAmount = BigInteger.ZERO;
            List<TransactionInput> inputs = tx.getInputs();
            for (TransactionInput input : inputs) {
                OutPoint op = input.getOutPoint();
                Sha256Hash opHash = op.getHash();
                int opIndex = op.getIndex();
                //
                // Locate the connected transaction output
                //
                List<StoredOutput> outputs = outputMap.get(opHash);
                if (outputs == null) {
                    Transaction outTx = txMap.get(opHash);
                    if (outTx == null) {
                        outputs = BTCLoader.blockStore.getTxOutputs(opHash);
                        if (outputs == null) {
                            BTCLoader.error(String.format("Transaction input specifies unavailable transaction\n"+
                                                    "  Transaction %s\n  Transaction input %d\n  Connected output %s",
                                                    tx.getHash(), input.getIndex(), opHash));
                            txValid = false;
                        } else {
                            outputMap.put(opHash, outputs);
                        }
                    } else {
                        List<TransactionOutput> txOutputList = outTx.getOutputs();
                        outputs = new ArrayList<>(txOutputList.size());
                        for (TransactionOutput txOutput : txOutputList)
                            outputs.add(new StoredOutput(txOutput.getIndex(), txOutput.getValue(),
                                                         txOutput.getScriptBytes(), outTx.isCoinBase()));
                        outputMap.put(opHash, outputs);
                    }
                }
                //
                // Add the input amount to the running total for the transaction.
                // Verify the input signature against the connected output.  We allow a double-spend
                // if the spending block is above the junction block since that spending block will
                // be removed if the chain ends up being reorganized.
                //
                if (txValid) {
                    StoredOutput output = null;
                    boolean foundOutput = false;
                    for (StoredOutput output1 : outputs) {
                        output = output1;
                        if (output.getIndex() == opIndex) {
                            foundOutput = true;
                            break;
                        }
                    }
                    if (!foundOutput) {
                        // Connected output not found
                        BTCLoader.error(String.format("Transaction input specifies non-existent output\n"+
                                                "  Transaction %s\n  Transaction input %d\n"+
                                                "  Connected output %s\n  Connected output index %d",
                                                tx.getHash(), input.getIndex(), opHash, opIndex));
                        BTCLoader.dumpData("Failing Transaction", tx.getBytes());
                        txValid = false;
                    } else {
                        if (output.isSpent() && output.getHeight()!=0 && output.getHeight()<=junctionHeight) {
                            // Connected output has been spent
                            BTCLoader.error(String.format("Transaction input specifies spent output\n"+
                                                    "  Transaction %s\n  Transaction intput %d\n"+
                                                    "  Connected output %s\n  Connected output index %d",
                                                    tx.getHash(), input.getIndex(), opHash, opIndex));
                            txValid = false;
                        } else {
                            if (output.isCoinBase()) {
                                // Check for immature coinbase transaction output
                                int txDepth = BTCLoader.blockStore.getTxDepth(opHash);
                                txDepth += storedBlock.getHeight() - BTCLoader.blockStore.getChainHeight();
                                if (txDepth < BTCLoader.COINBASE_MATURITY) {
                                    BTCLoader.error(String.format("Transaction input specifies immature coinbase output\n"+
                                                    "  Transaction %s\n  Transaction input %d\n"+
                                                    "  Connected output %s\n  Connected output index %d",
                                                    tx.getHash(), input.getIndex(), opHash, opIndex));
                                    txValid = false;
                                }
                            }
                            if (txValid) {
                                // Update amounts
                                txAmount = txAmount.add(output.getValue());
                                output.setSpent(true);
                                output.setHeight(storedBlock.getHeight());
                            }
                        }
                    }
                    //
                    // Verify the transaction signature
                    //
                    if (txValid) {
                        try {
                            txValid = BitcoinConsensus.verifyScript(input, output);
                            if (!txValid) {
                                BTCLoader.error(String.format("Transaction failed signature verification\n"+
                                                        "  Transaction %s\n  Transaction input %d\n"+
                                                        "  Outpoint %s\n  Outpoint index %d",
                                                        tx.getHash(), input.getIndex(),
                                                        op.getHash(), op.getIndex()));
                            }
                        } catch (ScriptException exc) {
                            BTCLoader.warn(String.format("Unable to verify transaction input\n  Tx %s",
                                                   tx.getHash()), exc);
                            txValid = false;
                        }
                        if (!txValid) {
                            BTCLoader.dumpData("Input Script", input.getScriptBytes());
                            BTCLoader.dumpData("output Script", output.getScriptBytes());
                        }
                    }
                }
                //
                // Stop processing transaction inputs if this input failed to verify
                //
                if (!txValid){
                    break;
                }
            }
            //
            // Get the amount for each output and subtract it from the transaction total
            //
            if (txValid) {
                List<TransactionOutput> outputs = tx.getOutputs();
                for (TransactionOutput output : outputs)
                    txAmount = txAmount.subtract(output.getValue());
                if (txAmount.compareTo(BigInteger.ZERO) < 0) {
                    BTCLoader.error(String.format("Transaction inputs less than transaction outputs\n  Tx %s",
                                            tx.getHash()));
                    txValid = false;
                } else {
                    totalFees = totalFees.add(txAmount);
                }
            }
            //
            // Stop processing the block transactions if we already have a failed transaction
            //
            if (!txValid)
                break;
        }
        //
        // The coinbase amount must not exceed the block reward plus the transaction fees for the block.
        // The block reward starts at 50 BTC and is cut in half every 210,000 blocks.
        //
        if (txValid) {
            long divisor = 1<<((storedBlock.getHeight())/210000);
            BigInteger blockReward = BigInteger.valueOf(5000000000L).divide(BigInteger.valueOf(divisor));
            Transaction tx = block.getTransactions().get(0);
            List<TransactionOutput> outputs = tx.getOutputs();
            BigInteger txAmount = blockReward.add(totalFees);
            for (TransactionOutput output : outputs)
                txAmount = txAmount.subtract(output.getValue());
            if (txAmount.compareTo(BigInteger.ZERO) < 0) {
                BTCLoader.error(String.format("Coinbase transaction outputs exceed block reward plus fees\n  Block %s",
                                        block.getHashAsString()));
                txValid = false;
            }
        }
        return txValid;
    }
}
