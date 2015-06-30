package org.ripple.power.txns.btc;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.List;

import org.ripple.power.Helper;

/**
 * <p>A Merkle branch is a data structure that contains proofs of block inclusion for one or
 * more transactions in an efficient manner.</p>
 *
 * <p>The encoding works as follows: we traverse the tree in depth-first order, storing a bit
 * for each traversed node, signifying whether the node is the parent of at least one matched
 * leaf txid (or a matched txid itself). In case we are at the leaf level, or this bit is 0,
 * its Merkle node hash is stored, and its children are not explored further.  Otherwise, no
 * hash is stored, but we recurse into both (or the only) child branches. During decoding, the
 * same depth-first traversal is performed, consuming bits and hashes as they were written during
 * encoding.</p>
 *
 * <p>The serialization is fixed and provides a hard guarantee about the encoded size,
 * <tt>SIZE LE 10 + ceil(32.25*N)</tt> where N represents the number of leaf nodes of the partial tree. N itself
 * is bounded by:</p>
 *
 * <p>
 * N LE total_transactions<br>
 * N LE 1 + matched_transactions*tree_height
 * </p>
 *
 * <p>Merkle Branch</p>
 * <pre>
 *   Size       Field           Description
 *   ====       =====           ===========
 *   4 bytes    txCount         Number of transactions in the block
 *   VarInt     hashCount       Number of hashes
 *   Variable   hashes          Hashes in depth-first order
 *   VarInt     flagCount       Number of bytes of flag bits
 *   Variable   flagBits        Flag bits packed 8 per byte, least significant bit first
 * </pre>
 */
public class MerkleBranch implements ByteSerializable {

    /** Bits used while traversing the tree */
    private int bitsUsed;

    /** Hashes used while traversing the tree */
    private int hashesUsed;

    /** Transaction count */
    private final int txCount;

    /** Merkle branch node hashes in big-endian format */
    private List<byte[]> nodeHashes;

    /** Merkle branch node flags */
    private byte[] nodeFlags;

    /**
     * Creates a new Merkle branch from the serialized byte stream
     *
     * @param       inBuffer                Input buffer
     * @throws      EOFException            End-of-data processing input stream
     * @throws      VerificationException   Verification error
     */
    public MerkleBranch(SerializedBuffer inBuffer) throws EOFException, VerificationException {
        //
        // Get the transaction count
        //
        txCount = inBuffer.getInt();
        if (txCount < 1 || txCount > NetParams.MAX_BLOCK_SIZE/60)
            throw new VerificationException(String.format("Transaction count %d is not valid", txCount),
                                            RejectMessage.REJECT_INVALID);
        //
        // Get the node hashes
        //
        int hashCount = inBuffer.getVarInt();
        if (hashCount < 0 || hashCount > txCount)
            throw new VerificationException(String.format("Hash count %d is not valid", hashCount),
                                            RejectMessage.REJECT_INVALID);
        nodeHashes = new ArrayList<>(hashCount);
        for (int i=0; i<hashCount; i++)
            nodeHashes.add(Helper.reverseBytes(inBuffer.getBytes(32)));
        //
        // Get the node flags
        //
        int flagCount = inBuffer.getVarInt();
        if (flagCount < 1)
            throw new VerificationException(String.format("Flag count %d is not valid", flagCount),
                                            RejectMessage.REJECT_INVALID);
        nodeFlags = inBuffer.getBytes(flagCount);
    }

    /**
     * Creates a new Merkle branch for the supplied transactions.  The transaction
     * list must be sorted by transaction index.  The Merkle tree starts with the
     * leaf nodes and ends with the root node.
     *
     * @param       txCount         Total number of transactions in the block
     * @param       txList          Matched transaction index list
     * @param       merkleTree      Merkle tree
     */
    public MerkleBranch(int txCount, List<Integer> txList, List<byte[]>merkleTree) {
        this.txCount = txCount;
        //
        // Calculate the tree height based on the total transaction count.  Each
        // node in the tree has two descendants (the last node in a level may have
        // just one descendant)
        //
        int height = 0;
        int nodeCount = 1;
        int width;
        while ((width=getTreeWidth(height)) > 1) {
            height++;
            nodeCount += width;
        }
        //
        // Allocate the hash list and flag array (we will adjust the flag array size
        // when we are done)
        //
        nodeHashes = new ArrayList<>(nodeCount);
        nodeFlags = new byte[(nodeCount*2+7)/8];
        //
        // Create the paths to reach each matched transaction.  Each tree level has
        // an array of node indexes representing the next node for each transaction.
        // The paths are arranged from the first transaction index to the
        // last index, so the tree is traversed from left to right.
        //
        int[][] nodePath = new int[height][txList.size()];
        for (int i=0; i<txList.size(); i++) {
            int pos = 0;
            int index = txList.get(i);
            for (int pHeight=height-1; pHeight>=0; pHeight--) {
                int p2 = 1<<pHeight;
                pos = pos*2 + index/p2;
                nodePath[pHeight][i] = pos;
                index = index % p2;
            }
        }
        //
        // Build the Merkle branch starting at the Merkle root and continuing
        // down to the leaf transactions.  Remember that the tree is upside down with
        // the leaf nodes first and the Merkle root last.  We flag the root node
        // as a match since you always start at the root.
        //
        // We have a special case if there are no transactions supplied.  In this
        // case, we will create a Merkle branch with the Merkle root as the only
        // nodeHashes element and no matches will be set in nodeFlags.
        //
        // We have another special case if the block contains just the coinbase transaction
        // and this is also the matched transaction.  In this case, we will create
        // a Merkle branch with the coinbase transaction set in nodeFlags.  Note that
        // the merkle root is the same as the coinbase transaction hash in this case.
        //
        if (txList.isEmpty()) {
            bitsUsed++;
            nodeHashes.add(merkleTree.get(merkleTree.size()-1));
        } else if (txCount == 1) {
            Helper.setBitLE(nodeFlags, bitsUsed++);
            nodeHashes.add(merkleTree.get(merkleTree.size()-1));
        } else {
            Helper.setBitLE(nodeFlags, bitsUsed++);
            buildBranch(height-1, 0, nodePath, merkleTree.size()-1, merkleTree);
        }
        //
        // Resize nodeFlags based on the actual number of bits used
        //
        byte[] newFlags = new byte[(bitsUsed+7)/8];
        System.arraycopy(nodeFlags, 0, newFlags, 0, newFlags.length);
        nodeFlags = newFlags;
    }

    /**
     * Write the serialized Merkle branch to the output buffer
     *
     * @param       outBuffer       Output buffer
     * @return                      Output buffer
     */
    @Override
    public SerializedBuffer getBytes(SerializedBuffer outBuffer) {
        outBuffer.putInt(txCount)
                 .putVarInt(nodeHashes.size());
        for (byte[] hash : nodeHashes)
            outBuffer.putBytes(Helper.reverseBytes(hash));
        outBuffer.putVarInt(nodeFlags.length)
                 .putBytes(nodeFlags);
        return outBuffer;
    }

    /**
     * Get the serialized byte array
     *
     * @return                      Serialized byte array
     */
    @Override
    public byte[] getBytes() {
        return getBytes(new SerializedBuffer(nodeHashes.size()*32+nodeFlags.length+12)).toByteArray();
    }

    /**
     * Traverse the Merkle branch down to the leaf transaction.  Return the leaf transaction
     * hashes and the calculated Merkle root hash.  The matchedHash list will be cleared
     * before starting.
     *
     * @param       matchedHashes           Return the hashes of the matched leaf transactions
     * @return                              Merkle root
     * @throws      VerificationException   Malformed Merkle branch
     */
    public Sha256Hash calculateMerkleRoot(List<Sha256Hash> matchedHashes) throws VerificationException {
        matchedHashes.clear();
        bitsUsed = 0;
        hashesUsed = 0;
        //
        // Start at the root and travel down to the leaf node
        //
        int height = 0;
        while (getTreeWidth(height) > 1)
            height++;
        byte[] merkleRoot = parseBranch(height, 0, matchedHashes);
        //
        // Verify that all bits and hashes were consumed
        //
        if ((bitsUsed+7)/8 != nodeFlags.length)
            throw new VerificationException("Merkle branch did not use all of its bits");
        if (hashesUsed != nodeHashes.size())
            throw new VerificationException(String.format("Merkle branch used %d of %d hashes",
                                                          hashesUsed, nodeHashes.size()),
                                                          RejectMessage.REJECT_INVALID);
        return new Sha256Hash(merkleRoot);
    }

    /**
     * Recursively build the Merkle branch setting the node flags to indicate the branches
     * we have taken.  For each node, follow the left branch as long as it leads to the next
     * leaf transaction.  Then switch to the right branch to continue.  At each node junction,
     * set the appropriate node flag to indicate which branch we took.  For each branch that
     * we don't take, add the hash for that path to the node hash list.
     *
     * @param       height          Current height
     * @param       levelPos        Current position within level
     * @param       nodePath        Transaction paths
     * @param       treePos         Current position within the tree
     * @param       merkleTree      Merkle tree
     */
    private void buildBranch(int height, int levelPos, int[][]nodePath, int treePos, List<byte[]>merkleTree) {
        //
        // Determine which branch to take to reach the current transaction
        //
        // hashesUsed tracks our position within nodePath and bitsUsed tracks our
        // position within nodeFlags
        //
        int levelWidth = getTreeWidth(height);
        int nextLevelPos = levelPos*2;
        int leftTreePos = treePos-levelWidth+nextLevelPos;
        int nextNode = nodePath[height][hashesUsed];
        if (height > 0) {
            //
            // We are not at a leaf node yet, so follow the left or right branch
            // to the next level of the tree.
            //
            if (nextNode == nextLevelPos) {
                //
                // We need to follow the left branch
                //
                Helper.setBitLE(nodeFlags, bitsUsed++);
                buildBranch(height-1, nextLevelPos, nodePath, treePos-levelWidth, merkleTree);
                //
                // We have found the current transaction, so update nextNode and see if
                // we should now follow the right branch.  If not, add the hash for the
                // right path to nodeHashes.
                //
                if (nextLevelPos+1 < getTreeWidth(height)) {
                    if (hashesUsed < nodePath[height].length) {
                        nextNode = nodePath[height][hashesUsed];
                        if (nextNode == nextLevelPos+1) {
                            Helper.setBitLE(nodeFlags, bitsUsed++);
                            buildBranch(height-1, nextLevelPos+1, nodePath, treePos-levelWidth, merkleTree);
                        } else {
                            nodeHashes.add(merkleTree.get(leftTreePos+1));
                            bitsUsed++;
                        }
                    } else {
                        nodeHashes.add(merkleTree.get(leftTreePos+1));
                        bitsUsed++;
                    }
                }
            } else if (nextNode == nextLevelPos+1) {
                //
                // We need to follow the right branch, so add the hash for the left node
                // and then recurse
                //
                nodeHashes.add(merkleTree.get(leftTreePos));
                Helper.setBitLE(nodeFlags, bitsUsed+1);
                bitsUsed += 2;
                buildBranch(height-1, nextLevelPos+1, nodePath, treePos-levelWidth, merkleTree);
            } else {
                throw new IllegalStateException(String.format("Next node %d at height %d is not valid",
                                                              nextNode, height));
            }
        } else {
            //
            // We are at the leaf nodes, so our choice is simple: left node or right node
            //
            if (nextNode == nextLevelPos) {
                //
                // The desired transaction is the left leaf
                //
                Helper.setBitLE(nodeFlags, bitsUsed++);
                nodeHashes.add(merkleTree.get(leftTreePos));
                hashesUsed++;
                //
                // We always add the hash for the right leaf node to nodeHashes.
                // Update nextNode and see if we also have another match.
                //
                if (nextLevelPos+1 < getTreeWidth(height)) {
                    nodeHashes.add(merkleTree.get(leftTreePos+1));
                    if (hashesUsed < nodePath[height].length) {
                        nextNode = nodePath[height][hashesUsed];
                        if (nextNode == nextLevelPos+1) {
                            Helper.setBitLE(nodeFlags, bitsUsed);
                            hashesUsed++;
                        }
                    }
                    bitsUsed++;
                }
            } else if (nextNode == nextLevelPos+1) {
                //
                // The desired transaction is the right leaf.  Add the hashes
                // for both leaves to nodeHashes and indicate the right leaf
                // is a match.
                //
                Helper.setBitLE(nodeFlags, bitsUsed+1);
                bitsUsed += 2;
                nodeHashes.add(merkleTree.get(leftTreePos));
                nodeHashes.add(merkleTree.get(leftTreePos+1));
                hashesUsed++;
            } else {
                throw new IllegalStateException(String.format("Next node %d at height %d is not valid",
                                                              nextNode, height));
            }
        }
    }

    /**
     * Recursively traverse the tree nodes as dictated by the node flags, getting missing
     * hashes from the node hash list for branches not on our path.  Matching leaf hashes are
     * added to matchesHashes.
     *
     * @param       height          Current height
     * @param       pos             Current position within the level
     * @param       matchedHashes   Matched hashes from the leaf node
     * @return      Node hash
     * @throws      VerificationException
     */
    private byte[] parseBranch(int height, int pos, List<Sha256Hash> matchedHashes)
                                    throws VerificationException {
        if (bitsUsed >= nodeFlags.length*8)
            throw new VerificationException("Merkle branch overflowed the bits array",
                                            RejectMessage.REJECT_INVALID);
        boolean parentOfMatch = Helper.checkBitLE(nodeFlags, bitsUsed++);
        if (height == 0 || !parentOfMatch) {
            //
            // If at height 0 or nothing interesting below, use the stored hash and do not descend
            // to the next level.  If we have a match at height 0, it is a matching transaction.
            //
            if (hashesUsed >= nodeHashes.size())
                throw new VerificationException("Merkle branch overflowed the hash array",
                                                RejectMessage.REJECT_INVALID);
            if (height == 0 && parentOfMatch)
                matchedHashes.add(new Sha256Hash(nodeHashes.get(hashesUsed)));
            return nodeHashes.get(hashesUsed++);
        }
        //
        // Continue down to the next level
        //
        byte[] right;
        byte[] left = parseBranch(height-1, pos*2, matchedHashes);
        if (pos*2+1 < getTreeWidth(height-1))
            right = parseBranch(height-1, pos*2+1, matchedHashes);
        else
            right = left;
        //
        // Calculate the node hash from the left and right branches.  We need to
        // reverse the bytes for the digest calculation and then reverse the
        // result to match the way Sha256Hash stores the hash bytes.
        //
        return Helper.reverseBytes(Helper.doubleDigestTwoBuffers(Helper.reverseBytes(left), 0, 32,
                                                               Helper.reverseBytes(right), 0, 32));
    }

    /**
     * Calculate the Merkle tree width for a given height based upon the total transaction count
     *
     * @param       height          Tree height
     * @return      Tree width
     */
    private int getTreeWidth(int height) {
        return (txCount+(1<<height)-1)>>height;
    }
}
