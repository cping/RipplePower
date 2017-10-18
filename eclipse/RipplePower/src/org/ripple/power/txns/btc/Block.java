package org.ripple.power.txns.btc;

import java.io.EOFException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.ripple.power.Helper;

/**
 * <p>
 * The Bitcoin block chain contains all of the transactions that have occurred
 * and is available to everyone. The block chain consists of a series of blocks
 * starting with the genesis block (block 0) and continuing to the chain head
 * (the latest block in the chain).
 * </p>
 *
 * <p>
 * Each block is composed of one or more transactions. The first transaction is
 * called the coinbase transaction and it assigns the block reward to the miner
 * who solved the block hash. The remaining transactions move coins from Input A
 * to Output B. A single transaction can contain multiple inputs and multiple
 * outputs. The sum of the inputs minus the sum of the output represents the
 * mining fee for that transaction.
 * </p>
 *
 * <p>
 * A block has the following format:
 * </p>
 * 
 * <pre>
 *   Size           Field               Description
 *   ====           =====               ===========
 *   80 bytes       BlockHeader         Consists of 6 fields that are hashed to calculate the block hash
 *   VarInt         TxCount             Number of transactions in the block
 *   Variable       Transactions        The transactions in the block
 * </pre>
 *
 * <p>
 * The block header has the following format:
 * </p>
 * 
 * <pre>
 *   Size           Field               Description
 *   ====           =====               ===========
 *   4 bytes        Version             The block version number
 *   32 bytes       PrevBlockHash       The hash of the preceding block in the chain
 *   32 byte        MerkleRoot          The Merkle root for the transactions in the block
 *   4 bytes        Time                The time the block was mined
 *   4 bytes        Difficulty          The target difficulty
 *   4 bytes        Nonce               The nonce used to generate the required hash
 * </pre>
 */
public class Block implements ByteSerializable {

	/** The serialized byte stream */
	private byte[] blockData;

	/** The block version */
	private int blockVersion;

	/** The block hash calculated from the block header */
	private Sha256Hash blockHash;

	/** The hash for the previous block in the chain */
	private Sha256Hash prevBlockHash;

	/** The Merkle root for the transactions in the block */
	private Sha256Hash merkleRoot;

	/** The Merkle tree for the transaction in the block */
	private List<byte[]> merkleTree;

	/** The block timestamp */
	private long timeStamp;

	/** The target difficulty */
	private long targetDifficulty;

	/** The nonce */
	private int nonce;

	/** The transactions contained in the block */
	private List<Transaction> transactions;

	/**
	 * Create an empty block for use by subclasses
	 */
	protected Block() {
	}

	/**
	 * Create a block from a serialized byte array
	 *
	 * @param inBytes
	 *            Byte array containing the serialized data
	 * @param doVerify
	 *            TRUE if the block structure should be verified
	 * @throws EOFException
	 *             End-of-data while processing byte stream
	 * @throws VerificationException
	 *             Block verification failed
	 */
	public Block(byte[] inBytes, boolean doVerify) throws EOFException, VerificationException {
		this(inBytes, 0, inBytes.length, doVerify);
	}

	/**
	 * Create a block from a serialized byte array
	 *
	 * @param inBytes
	 *            Byte array containing the serialized data
	 * @param inOffset
	 *            Starting offset within the array
	 * @param inLength
	 *            Length of the serialized data
	 * @param doVerify
	 *            TRUE if the block structure should be verified
	 * @throws EOFException
	 *             Serialized byte stream is too short
	 * @throws VerificationException
	 *             Block verification failed
	 */
	public Block(byte[] inBytes, int inOffset, int inLength, boolean doVerify)
			throws EOFException, VerificationException {
		this(new SerializedBuffer(inBytes, inOffset, inLength), doVerify);
	}

	/**
	 * Create a block from a serialized buffer
	 *
	 * @param inBuffer
	 *            Serialized buffer
	 * @param doVerify
	 *            TRUE if the block structure should be verified
	 * @throws EOFException
	 *             Serialized byte stream is too short
	 * @throws VerificationException
	 *             Block verification failed
	 */
	public Block(SerializedBuffer inBuffer, boolean doVerify) throws EOFException, VerificationException {
		//
		// We must have at least 80 bytes
		//
		if (inBuffer.available() < BlockHeader.HEADER_SIZE)
			throw new EOFException("Block header truncated");
		//
		// Compute the block hash from the serialized block header
		//
		int startPosition = inBuffer.getPosition();
		blockHash = new Sha256Hash(
				Helper.reverseBytes(Helper.doubleDigest(inBuffer.getBytes(BlockHeader.HEADER_SIZE))));
		inBuffer.setPosition(startPosition);
		//
		// Read the block header
		//
		readHeader(inBuffer);
		//
		// Read the transactions
		//
		readTransactions(inBuffer);
		//
		// Verify the block and its transactions. Note that transaction
		// signatures and connected
		// outputs will be verified when the block is added to the block chain.
		//
		if (doVerify)
			verifyBlock();
		//
		// Save a copy of the serialized byte stream
		//
		inBuffer.setSegmentStart(startPosition);
		blockData = inBuffer.getSegmentBytes();
	}

	/**
	 * Write the serialized block data to the output buffer
	 *
	 * @param outBuffer
	 *            Output buffer
	 * @return Output buffer
	 */
	@Override
	public SerializedBuffer getBytes(SerializedBuffer outBuffer) {
		outBuffer.putBytes(blockData);
		return outBuffer;
	}

	/**
	 * Return the serialized block data
	 *
	 * @return Byte array containing the serialized block
	 */
	@Override
	public byte[] getBytes() {
		return blockData;
	}

	/**
	 * Write the serialized block header to the output buffer
	 *
	 * @param outBuffer
	 *            Output buffer
	 * @return Output buffer
	 */
	public SerializedBuffer getHeaderBytes(SerializedBuffer outBuffer) {
		outBuffer.putBytes(blockData, 0, BlockHeader.HEADER_SIZE);
		return outBuffer;
	}

	/**
	 * Return the serialized block header
	 *
	 * @return Byte array containing just the block header
	 */
	public byte[] getHeaderBytes() {
		return Arrays.copyOfRange(blockData, 0, BlockHeader.HEADER_SIZE);
	}

	/**
	 * <p>
	 * Returns the block version. Only Version 1 and Version 2 blocks are
	 * supported.
	 * </p>
	 * <ul>
	 * <li>Blocks created before BIP 34 are Version 1 and do not contain the
	 * chain height in the coinbase transaction input script</li>
	 * <li>Blocks created after BIP 34 are Version 2 and contain the chain
	 * height in the coinbase transaction input script</li>
	 * </ul>
	 *
	 * @return Block version
	 */
	public int getVersion() {
		return blockVersion;
	}

	/**
	 * Returns the time the block was mined
	 *
	 * @return The block timestamp in seconds since the Unix epoch (Jan 1, 1970)
	 */
	public long getTimeStamp() {
		return timeStamp;
	}

	/**
	 * Returns the block hash calculated over the block header
	 *
	 * @return Block hash
	 */
	public Sha256Hash getHash() {
		return blockHash;
	}

	/**
	 * Returns the block hash as a formatted hex string
	 *
	 * @return Hex string
	 */
	public String getHashAsString() {
		return blockHash.toString();
	}

	/**
	 * Returns the hash of the previous block in the chain
	 *
	 * @return Previous block hash
	 */
	public Sha256Hash getPrevBlockHash() {
		return prevBlockHash;
	}

	/**
	 * Returns the Merkle root
	 *
	 * @return Merkle root
	 */
	public Sha256Hash getMerkleRoot() {
		return merkleRoot;
	}

	/**
	 * Returns the Merkle tree
	 *
	 * @return Merkle tree
	 */
	public List<byte[]> getMerkleTree() {
		if (merkleTree == null)
			merkleTree = buildMerkleTree();
		return merkleTree;
	}

	/**
	 * Returns the target difficulty in compact form
	 *
	 * @return Target difficulty
	 */
	public long getTargetDifficulty() {
		return targetDifficulty;
	}

	/**
	 * Returns the target difficulty as a 256-bit value that can be compared to
	 * a SHA-256 hash. Inside a block. the target is represented using the
	 * compact form.
	 *
	 * @return The difficulty target
	 */
	public BigInteger getTargetDifficultyAsInteger() {
		return Helper.decodeCompactBits(targetDifficulty);
	}

	/**
	 * Returns the work represented by this block
	 *
	 * Work is defined as the number of tries needed to solve a block in the
	 * average case. As the target gets lower, the amount of work goes up.
	 *
	 * @return The work represented by this block
	 */
	public BigInteger getWork() {
		BigInteger target = getTargetDifficultyAsInteger();
		return BlockHeader.LARGEST_HASH.divide(target.add(BigInteger.ONE));
	}

	/**
	 * Returns the block nonce
	 *
	 * @return Block nonce
	 */
	public int getNonce() {
		return nonce;
	}

	/**
	 * Returns the transactions in this block
	 *
	 * @return Transaction list
	 */
	public List<Transaction> getTransactions() {
		return transactions;
	}

	/**
	 * Calculates the Merkle root from the block transactions
	 *
	 * @return Merkle root
	 */
	private Sha256Hash calculateMerkleRoot() {
		if (merkleTree == null)
			merkleTree = buildMerkleTree();
		return new Sha256Hash(merkleTree.get(merkleTree.size() - 1));
	}

	/**
	 * Builds the Merkle tree from the block transactions
	 *
	 * @return List of byte arrays representing the nodes in the Merkle tree
	 */
	private List<byte[]> buildMerkleTree() {
		//
		// The Merkle root is based on a tree of hashes calculated from the
		// transactions:
		//
		// root
		// / \
		// A B
		// / \ / \
		// t1 t2 t3 t4
		//
		// The tree is represented as a list: t1,t2,t3,t4,A,B,root where each
		// entry is a hash
		//
		// The hashing algorithm is double SHA-256. The leaves are a hash of the
		// serialized contents of the transaction.
		// The interior nodes are hashes of the concatenation of the two child
		// hashes.
		//
		// This structure allows the creation of proof that a transaction was
		// included into a block without having to
		// provide the full block contents. Instead, you can provide only a
		// Merkle branch. For example to prove tx2 was
		// in a block you can just provide tx2, the hash(tx1) and B. Now the
		// other party has everything they need to
		// derive the root, which can be checked against the block header. These
		// proofs are useful when we
		// want to download partial block contents.
		//
		// Note that if the number of transactions is not even, the last tx is
		// repeated to make it so.
		// A tree with 5 transactions would look like this:
		//
		// root
		// / \
		// 4 5
		// / \ / \
		// 1 2 3 3
		// / \ / \ / \
		// t1 t2 t3 t4 t5 t5
		//
		ArrayList<byte[]> tree = new ArrayList<>();
		for (Transaction tx : transactions) {
			tree.add(tx.getHash().getBytes());
		}
		//
		// The tree is generated starting at the leaves and moving down to the
		// root
		//
		int levelOffset = 0;
		//
		// Step through each level, stopping when we reach the root (levelSize
		// == 1).
		//
		for (int levelSize = transactions.size(); levelSize > 1; levelSize = (levelSize + 1) / 2) {
			//
			// Process each pair of nodes on the current level
			//
			for (int left = 0; left < levelSize; left += 2) {
				//
				// The right hand node can be the same as the left hand in the
				// case where we have
				// an odd number of nodes for the level
				//
				int right = Math.min(left + 1, levelSize - 1);
				byte[] leftBytes = Helper.reverseBytes(tree.get(levelOffset + left));
				byte[] rightBytes = Helper.reverseBytes(tree.get(levelOffset + right));
				byte[] nodeHash = Helper.doubleDigestTwoBuffers(leftBytes, 0, 32, rightBytes, 0, 32);
				tree.add(Helper.reverseBytes(nodeHash));
			}
			//
			// Move to the next level.
			//
			levelOffset += levelSize;
		}
		return tree;
	}

	/**
	 * Reads the block header from the input stream
	 *
	 * @param inBuffer
	 *            Input buffer
	 * @throws EOFException
	 *             Serialized input stream is too short
	 * @throws VerificationException
	 *             Block structure is incorrect
	 */
	private void readHeader(SerializedBuffer inBuffer) throws EOFException, VerificationException {
		blockVersion = inBuffer.getInt();
		if (blockVersion < 1 || blockVersion > 3)
			throw new VerificationException(String.format("Block version %d is not supported", blockVersion));
		prevBlockHash = new Sha256Hash(Helper.reverseBytes(inBuffer.getBytes(32)));
		merkleRoot = new Sha256Hash(Helper.reverseBytes(inBuffer.getBytes(32)));
		timeStamp = inBuffer.getUnsignedInt();
		targetDifficulty = inBuffer.getUnsignedInt();
		nonce = inBuffer.getInt();
	}

	/**
	 * Reads the transactions from the serialized stream
	 *
	 * @param inBuffer
	 *            Serialized buffer
	 * @throws EOFException
	 *             Serialized input stream is too short
	 * @throws VerificationException
	 *             Transaction verification failed
	 */
	private void readTransactions(SerializedBuffer inBuffer) throws EOFException, VerificationException {
		int count = inBuffer.getVarInt();
		if (count < 1 || count > NetParams.MAX_BLOCK_SIZE / 60)
			throw new VerificationException(String.format("Transaction count %d is not valid", count));
		transactions = new ArrayList<>(count);
		for (int i = 0; i < count; i++)
			transactions.add(new Transaction(inBuffer));
	}

	/**
	 * <p>
	 * Checks the block to ensure it follows the rules laid out in the network
	 * parameters.
	 * </p>
	 * <p>
	 * The following checks are performed:
	 * </p>
	 * <ul>
	 * <li>Check the proof of work by comparing the block hash to the target
	 * difficulty</li>
	 * <li>Check the timestamp against the current time</li>
	 * <li>Verify that there is a single coinbase transaction and it is the
	 * first transaction in the block</li>
	 * <li>Verify the merkle root</li>
	 * <li>Verify the transaction structure</li>
	 * <li>Verify the transaction lock time</li>
	 * </ul>
	 *
	 * @throws VerificationException
	 *             Block verification failed
	 */
	private void verifyBlock() throws VerificationException {
		//
		// Ensure this block does in fact represent real work done. If the
		// difficulty is high enough,
		// we can be fairly certain the work was done by the network.
		//
		// The block hash must be less than or equal to the target difficulty
		// (the difficulty increases
		// by requiring an increasing number of leading zeroes in the block
		// hash)
		//
		BigInteger target = getTargetDifficultyAsInteger();
		if (target.signum() <= 0 || target.compareTo(NetParams.PROOF_OF_WORK_LIMIT) > 0)
			throw new VerificationException("Target difficulty is not valid", RejectMessage.REJECT_INVALID, blockHash);
		BigInteger hash = getHash().toBigInteger();
		if (hash.compareTo(target) > 0)
			throw new VerificationException("Block hash is higher than target difficulty", RejectMessage.REJECT_INVALID,
					blockHash);
		//
		// Verify the block timestamp
		//
		long currentTime = System.currentTimeMillis() / 1000;
		if (timeStamp > currentTime + NetParams.ALLOWED_TIME_DRIFT)
			throw new VerificationException("Block timestamp is too far in the future", RejectMessage.REJECT_INVALID,
					blockHash);
		//
		// Check that there is just one coinbase transaction and it is the first
		// transaction in the block
		//
		boolean foundCoinBase = false;
		for (Transaction tx : transactions) {
			if (tx.isCoinBase()) {
				if (foundCoinBase)
					throw new VerificationException("Block contains multiple coinbase transactions",
							RejectMessage.REJECT_MALFORMED, blockHash);
				foundCoinBase = true;
			} else if (!foundCoinBase) {
				throw new VerificationException("First transaction in block is not the coinbase transaction",
						RejectMessage.REJECT_MALFORMED, blockHash);
			}
		}
		//
		// Verify the Merkle root
		//
		Sha256Hash checkRoot = calculateMerkleRoot();
		if (!checkRoot.equals(merkleRoot))
			throw new VerificationException("Merkle root is not correct", RejectMessage.REJECT_INVALID, blockHash);
		//
		// Verify the transactions in the block
		//
		for (Transaction tx : transactions) {
			//
			// Verify the transaction structure
			//
			tx.verify(false);
			//
			// A transaction is locked if the lock time is greater than the
			// block time (we allow
			// a 10-minute leeway)
			//
			if (tx.getLockTime() > timeStamp + (10 * 60)) {
				//
				// A transaction is unlocked if all of the input sequences are
				// -1 even though
				// the lock time has not been reached
				//
				List<TransactionInput> txInputs = tx.getInputs();
				for (TransactionInput txInput : txInputs) {
					if (txInput.getSeqNumber() != -1)
						throw new VerificationException("Transaction lock time greater than block time",
								RejectMessage.REJECT_INVALID, tx.getHash());
				}
			}
		}
	}

	/**
	 * Determines if this block is equal to another block
	 *
	 * @param obj
	 *            The block to compare
	 * @return TRUE if the blocks are equal
	 */
	@Override
	public boolean equals(Object obj) {
		return (obj != null && (obj instanceof Block) && blockHash.equals(((Block) obj).blockHash));
	}

	/**
	 * Returns the hash code for this object. The returned value is based on the
	 * block hash but is not the same value.
	 *
	 * @return Hash code
	 */
	@Override
	public int hashCode() {
		return blockHash.hashCode();
	}

	/**
	 * Returns a string representation for this block
	 *
	 * @return Formatted string
	 */
	@Override
	public String toString() {
		return String.format("Block hash: %s\n  Previous block hash %s\n  Merkle root: %s\n  Target difficulty %d",
				getHashAsString(), getPrevBlockHash().toString(), getMerkleRoot().toString(), targetDifficulty);
	}
}
