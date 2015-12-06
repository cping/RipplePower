package org.ripple.power.txns.btc;

import java.math.BigInteger;

public class StoredBlock {

    /** The work represented by the block chain up to and including this block */
    private BigInteger chainWork;

    /** The block hash */
    private Sha256Hash blockHash;

    /** The previous block hash */
    private Sha256Hash prevBlockHash;

    /** The block height */
    private int blockHeight;

    /** Block is on hold */
    private boolean onHold = false;

    /** Block is on the chain */
    private boolean onChain = false;

    /** The block */
    private Block block;

    /**
     * Creates a StoredBlock without any block data for a block on the chain
     *
     * @param       blockHash       Block hash
     * @param       prevBlockHash   Previous block hash
     * @param       chainWork       Chain work
     * @param       blockHeight     BlockHeight
     */
    public StoredBlock(Sha256Hash blockHash, Sha256Hash prevBlockHash, BigInteger chainWork, int blockHeight) {
        this.chainWork = chainWork;
        this.blockHeight = blockHeight;
        this.blockHash = blockHash;
        this.prevBlockHash = prevBlockHash;
        this.onChain = true;
    }

    /**
     * Creates a StoredBlock containing a new block
     *
     * @param       block           The block to be stored
     * @param       chainWork       Chain work
     * @param       blockHeight     Block height
     */
    public StoredBlock(Block block, BigInteger chainWork, int blockHeight) {
        this(block, chainWork, blockHeight, false, false);
    }

    /**
     * Creates a StoredBlock containing an existing block
     *
     * @param       block           The block
     * @param       chainWork       Chain work
     * @param       blockHeight     Block height
     * @param       onChain         TRUE if the block is on the chain
     * @param       onHold          TRUE if the block is on hold
     */
    public StoredBlock(Block block, BigInteger chainWork, int blockHeight, boolean onChain, boolean onHold) {
        this.block = block;
        this.chainWork = chainWork;
        this.blockHeight = blockHeight;
        this.blockHash = block.getHash();
        this.prevBlockHash = block.getPrevBlockHash();
        this.onChain = onChain;
        this.onHold = onHold;
    }

    /**
     * Returns the stored block
     *
     * @return      Stored block or null if the block has been pruned
     */
    public Block getBlock() {
        return block;
    }

    /**
     * Returns the block hash
     *
     * @return      The block hash
     */
    public Sha256Hash getHash() {
        return blockHash;
    }

    /**
     * Returns the previous block hash
     *
     * @return      The previous block hash
     */
    public Sha256Hash getPrevBlockHash() {
        return prevBlockHash;
    }

    /**
     * Returns the chain work represented by this block
     *
     * @return      The chain work
     */
    public BigInteger getChainWork() {
        return chainWork;
    }

    /**
     * Sets the chain work represented by this block
     * @param       chainWork       The chain work
     */
    public void setChainWork(BigInteger chainWork) {
        this.chainWork = chainWork;
    }

    /**
     * Returns the block height
     *
     * @return      The block height
     */
    public int getHeight() {
        return blockHeight;
    }

    /**
     * Set the block height
     * @param       blockHeight     The block height
     */
    public void setHeight(int blockHeight) {
        this.blockHeight = blockHeight;
    }

    /**
     * Sets the block hold status
     *
     * @param       onHold          TRUE if the block is on hold
     */
    public void setHold(boolean onHold) {
        this.onHold = onHold;
    }

    /**
     * Checks if the block is on hold
     *
     * @return      TRUE if the block is on hold
     */
    public boolean isOnHold() {
        return onHold;
    }

    /**
     * Sets the block chain status
     *
     * @param       onChain         TRUE if the block is on the chain
     */
    public void setChain(boolean onChain) {
        this.onChain = onChain;
    }

    /**
     * Checks if the block is on the chain
     *
     * @return      TRUE if the block is on the chain
     */
    public boolean isOnChain() {
        return onChain;
    }
}
