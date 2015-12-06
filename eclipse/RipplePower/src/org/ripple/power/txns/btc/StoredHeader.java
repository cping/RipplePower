package org.ripple.power.txns.btc;

import java.math.BigInteger;
import java.util.List;


public class StoredHeader extends BlockHeader {

    private boolean onChain;

    private int blockHeight;

    private BigInteger chainWork;

    public StoredHeader(BlockHeader header) {
        super(header.getVersion(), header.getHash(), header.getPrevHash(), header.getBlockTime(),
                                header.getTargetDifficulty(), header.getMerkleRoot(), header.getNonce(),
                                header.getMatches());
        onChain = false;
        blockHeight = -1;
        chainWork = BigInteger.ONE;
    }

    public StoredHeader(int version, Sha256Hash blockHash, Sha256Hash prevHash, long blockTime, long targetDifficulty,
                        Sha256Hash merkleRoot, boolean onChain, int blockHeight, BigInteger chainWork,
                        List<Sha256Hash> matches) {
        super(version, blockHash, prevHash, blockTime, targetDifficulty, merkleRoot, 0, matches);
        this.onChain = onChain;
        this.blockHeight = blockHeight;
        this.chainWork = chainWork;
    }

    public boolean isOnChain() {
        return onChain;
    }

    public void setChain(boolean onChain) {
        this.onChain = onChain;
    }

    public int getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(int blockHeight) {
        this.blockHeight = blockHeight;
    }

    public BigInteger getChainWork() {
        return chainWork;
    }

    public void setChainWork(BigInteger chainWork) {
        this.chainWork = chainWork;
    }
}
