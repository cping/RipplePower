package org.ripple.power.txns.btc;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.List;

import org.ripple.power.Helper;

/**
 * <p>
 * A Bloom filter is a probabilistic data structure which can be sent to another
 * client so that it can avoid sending us transactions that aren't relevant to
 * our set of keys. This allows for significantly more efficient use of
 * available network bandwidth and CPU time.
 * </p>
 *
 * <p>
 * Because a Bloom filter is probabilistic, it has a configurable false positive
 * rate. So the filter will sometimes match transactions that weren't inserted
 * into it, but it will never fail to match transactions that were. This is a
 * useful privacy feature - if you have spare bandwidth the false positive rate
 * can be increased so the remote peer gets a noisy picture of what transactions
 * are relevant to your wallet.
 * </p>
 *
 * <p>
 * Bloom Filter
 * </p>
 * 
 * <pre>
 *   Size       Field               Description
 *   ====       =====               ===========
 *   VarInt     Count               Number of bytes in the filter
 *   Variable   Filter              Filter data
 *   4 bytes    nHashFuncs          Number of hash functions
 *   4 bytes    nTweak              Random value to add to the hash seed
 *   1 byte     nFlags              Filter update flags
 * </pre>
 */
public class BloomFilter implements ByteSerializable {

	/** Bloom filter - Filter is not adjusted for matching outputs */
	public static final int UPDATE_NONE = 0;

	/** Bloom filter - Filter is adjusted for all matching outputs */
	public static final int UPDATE_ALL = 1;

	/**
	 * Bloom filter - Filter is adjusted only for pay-to-pubkey or
	 * pay-to-multi-sig
	 */
	public static final int UPDATE_P2PUBKEY_ONLY = 2;

	/** Maximum filter size */
	public static final int MAX_FILTER_SIZE = 36000;

	/** Maximum number of hash functions */
	public static final int MAX_HASH_FUNCS = 50;

	/** Filter data */
	private final byte[] filter;

	/** Number of hash functions */
	private int nHashFuncs;

	/** Random tweak nonce */
	private long nTweak = Double.valueOf(Math.random() * Long.MAX_VALUE).longValue();

	/** Filter update flags */
	private int nFlags = UPDATE_P2PUBKEY_ONLY;

	/** Peer associated with this filter */
	private Peer peer;

	/**
	 * <p>
	 * Constructs a new Bloom Filter which will provide approximately the given
	 * false positive rate when the given number of elements have been inserted.
	 * f the filter would otherwise be larger than the maximum allowed size, it
	 * will be automatically resized to the maximum size.
	 * </p>
	 *
	 * <p>
	 * The anonymity of which coins are yours to any peer which you send a
	 * BloomFilter to is controlled by the false positive rate. For reference,
	 * as of block 187,000, the total number of addresses used in the chain was
	 * roughly 4.5 million. Thus, if you use a false positive rate of 0.001
	 * (0.1%), there will be, on average, 4,500 distinct public keys/addresses
	 * which will be thought to be yours by nodes which have your bloom filter,
	 * but which are not actually yours.
	 * </p>
	 *
	 * @param elements
	 *            Number of elements in the filter
	 */
	public BloomFilter(int elements) {
		//
		// We will use a false-positive rate of 0.0005 (0.05%)
		//
		double falsePositiveRate = 0.0005;
		//
		// Allocate the filter array
		//
		int size = Math.min((int) (-1 / (Math.pow(Math.log(2), 2)) * elements * Math.log(falsePositiveRate)),
				MAX_FILTER_SIZE * 8) / 8;
		filter = new byte[size <= 0 ? 1 : size];
		//
		// Optimal number of hash functions for a given filter size and element
		// count.
		//
		nHashFuncs = Math.min((int) (filter.length * 8 / (double) elements * Math.log(2)), MAX_HASH_FUNCS);
	}

	/**
	 * Creates a Bloom filter from the serialized data
	 *
	 * @param inBuffer
	 *            Input buffer
	 * @throws EOFException
	 *             End-of-data processing input stream
	 * @throws VerificationException
	 *             Verification error
	 */
	public BloomFilter(SerializedBuffer inBuffer) throws EOFException, VerificationException {
		filter = inBuffer.getBytes();
		if (filter.length > MAX_FILTER_SIZE)
			throw new VerificationException("Bloom filter is too large");
		nHashFuncs = inBuffer.getInt();
		if (nHashFuncs > MAX_HASH_FUNCS)
			throw new VerificationException("Too many Bloom filter hash functions");
		nTweak = inBuffer.getUnsignedInt();
		nFlags = inBuffer.getByte();
	}

	/**
	 * Serialize the filter
	 *
	 * @param outBuffer
	 *            Output buffer
	 * @return Output buffer
	 */
	@Override
	public SerializedBuffer getBytes(SerializedBuffer outBuffer) {
		outBuffer.putVarInt(filter.length).putBytes(filter).putInt(nHashFuncs).putUnsignedInt(nTweak)
				.putByte((byte) nFlags);
		return outBuffer;
	}

	/**
	 * Serialize the filter and return a byte array
	 *
	 * @return Serialized filter
	 */
	@Override
	public byte[] getBytes() {
		return getBytes(new SerializedBuffer()).toByteArray();
	}

	/**
	 * Returns the filter flags
	 *
	 * @return Filter flags
	 */
	public int getFlags() {
		return nFlags;
	}

	/**
	 * Returns the peer associated with this filter
	 *
	 * @return Peer
	 */
	public Peer getPeer() {
		return peer;
	}

	/**
	 * Sets the peer associated with this filter
	 *
	 * @param peer
	 *            Peer
	 */
	public void setPeer(Peer peer) {
		this.peer = peer;
	}

	/**
	 * Checks if the filter contains the specified object
	 *
	 * @param object
	 *            Object to test
	 * @return TRUE if the filter contains the object
	 */
	public boolean contains(byte[] object) {
		for (int i = 0; i < nHashFuncs; i++) {
			if (!Helper.checkBitLE(filter, hash(i, object, 0, object.length)))
				return false;
		}
		return true;
	}

	/**
	 * Checks if the filter contains the specified object
	 *
	 * @param object
	 *            Object to test
	 * @param offset
	 *            Starting offset
	 * @param length
	 *            Length to check
	 * @return TRUE if the filter contains the object
	 */
	public boolean contains(byte[] object, int offset, int length) {
		for (int i = 0; i < nHashFuncs; i++) {
			if (!Helper.checkBitLE(filter, hash(i, object, offset, length)))
				return false;
		}
		return true;
	}

	/**
	 * Inserts an object into the filter
	 *
	 * @param object
	 *            Object to insert
	 */
	public void insert(byte[] object) {
		for (int i = 0; i < nHashFuncs; i++)
			Helper.setBitLE(filter, hash(i, object, 0, object.length));
	}

	/**
	 * Check a transaction against the Bloom filter for a match
	 *
	 * @param tx
	 *            Transaction to check
	 * @return TRUE if the transaction matches the filter
	 */
	public boolean checkTransaction(Transaction tx) {
		boolean foundMatch = false;
		Sha256Hash txHash = tx.getHash();
		byte[] outpointData = new byte[36];
		//
		// Check the transaction hash
		//
		if (contains(txHash.getBytes()))
			return true;
		//
		// Check transaction outputs
		//
		// Test each script data element. If a match is found, add
		// the serialized output point to the filter (if requested)
		// so the peer will be notified if the output is later spent.
		// We need to check all of the outputs since more than one transaction
		// in the block may be of interest and we would need to
		// update the filter for each one.
		//
		int index = 0;
		List<TransactionOutput> outputs = tx.getOutputs();
		for (TransactionOutput output : outputs) {
			//
			// Test the filter against each data element in the output script
			//
			byte[] scriptBytes = output.getScriptBytes();
			boolean isMatch = Script.checkFilter(this, scriptBytes);
			if (isMatch) {
				foundMatch = true;
				int type = Script.getPaymentType(scriptBytes);
				//
				// Update the filter with the outpoint if requested
				//
				if (nFlags == BloomFilter.UPDATE_ALL || (nFlags == BloomFilter.UPDATE_P2PUBKEY_ONLY
						&& (type == ScriptOpCodes.PAY_TO_PUBKEY || type == ScriptOpCodes.PAY_TO_MULTISIG))) {
					System.arraycopy(Helper.reverseBytes(txHash.getBytes()), 0, outpointData, 0, 32);
					Helper.uint32ToByteArrayLE(index, outpointData, 32);
					insert(outpointData);
				}
			}
			index++;
		}
		if (foundMatch)
			return true;
		//
		// Check transaction inputs
		//
		// Test each outpoint against the filter as well as each script data
		// element.
		//
		List<TransactionInput> inputs = tx.getInputs();
		for (TransactionInput input : inputs) {
			//
			// Test the filter against each data element in the input script
			// (don't test the coinbase transaction)
			//
			if (!tx.isCoinBase()) {
				byte[] scriptBytes = input.getScriptBytes();
				if (scriptBytes.length > 0) {
					foundMatch = Script.checkFilter(this, scriptBytes);
					if (foundMatch)
						break;
				}
				//
				// Check the filter against the outpoint
				//
				if (contains(input.getOutPoint().getBytes())) {
					foundMatch = true;
					break;
				}
			}
		}
		return foundMatch;
	}

	/**
	 * Find matching transactions in the supplied block
	 *
	 * @param block
	 *            Block containing the transactions
	 * @return List of matching transactions (List size will be 0 if no matches
	 *         found)
	 */
	public List<Sha256Hash> findMatches(Block block) {
		List<Transaction> txList = block.getTransactions();
		List<Sha256Hash> matches = new ArrayList<>(txList.size());
		for (Transaction tx : txList) {
			if (checkTransaction(tx)) {
				matches.add(tx.getHash());
			}
		}
		return matches;
	}

	/**
	 * Rotate a 32-bit value left by the specified number of bits
	 *
	 * @param x
	 *            The bit value
	 * @param count
	 *            The number of bits to rotate
	 * @return The rotated value
	 */
	private int ROTL32(int x, int count) {
		return (x << count) | (x >>> (32 - count));
	}

	/**
	 * Performs a MurmurHash3
	 *
	 * @param hashNum
	 *            The hash number
	 * @param object
	 *            The byte array to hash
	 * @param offset
	 *            The starting offset
	 * @param length
	 *            Length to hash
	 * @return The hash of the object using the specified hash number
	 */
	private int hash(int hashNum, byte[] object, int offset, int length) {
		int h1 = (int) (hashNum * 0xFBA4C795L + nTweak);
		final int c1 = 0xcc9e2d51;
		final int c2 = 0x1b873593;
		int numBlocks = (length / 4) * 4;
		//
		// Body
		//
		for (int i = 0; i < numBlocks; i += 4) {
			int k1 = ((int) object[offset + i] & 0xFF) | (((int) object[offset + i + 1] & 0xFF) << 8)
					| (((int) object[offset + i + 2] & 0xFF) << 16) | (((int) object[offset + i + 3] & 0xFF) << 24);
			k1 *= c1;
			k1 = ROTL32(k1, 15);
			k1 *= c2;
			h1 ^= k1;
			h1 = ROTL32(h1, 13);
			h1 = h1 * 5 + 0xe6546b64;
		}
		int k1 = 0;
		switch (length & 3) {
		case 3:
			k1 ^= (object[offset + numBlocks + 2] & 0xff) << 16;
			// Fall through.
		case 2:
			k1 ^= (object[offset + numBlocks + 1] & 0xff) << 8;
			// Fall through.
		case 1:
			k1 ^= (object[offset + numBlocks] & 0xff);
			k1 *= c1;
			k1 = ROTL32(k1, 15);
			k1 *= c2;
			h1 ^= k1;
			// Fall through.
		default:
			// Do nothing.
			break;
		}
		//
		// Finalization
		//
		h1 ^= length;
		h1 ^= h1 >>> 16;
		h1 *= 0x85ebca6b;
		h1 ^= h1 >>> 13;
		h1 *= 0xc2b2ae35;
		h1 ^= h1 >>> 16;
		return (int) ((h1 & 0xFFFFFFFFL) % (filter.length * 8));
	}
}
