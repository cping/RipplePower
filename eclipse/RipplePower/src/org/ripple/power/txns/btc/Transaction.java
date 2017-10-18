package org.ripple.power.txns.btc;

import java.io.EOFException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.ripple.power.Helper;

/**
 * <p>
 * A block is composed of one or more transactions. The first transaction is
 * called the coinbase transaction and it assigns the block reward to the miner
 * who solved the block hash. The remaining transactions move coins from Input A
 * to Output B. A single transaction can contain multiple inputs and multiple
 * outputs. The sum of the inputs minus the sum of the output represents the
 * mining fee for that transaction.
 * </p>
 *
 * <p>
 * Each transaction input is connected to the output of a proceeding
 * transaction. The input contains the first half of a script (ScriptSig) and
 * the output contains the second half (ScriptPubKey). The script is interpreted
 * to determines if the transaction input is allowed to spend the transaction
 * output.
 * </p>
 *
 * <p>
 * A transaction has the following format:
 * </p>
 * 
 * <pre>
 *   Size           Field               Description
 *   ====           =====               ===========
 *   4 bytes        Version             Currently 1
 *   VarInt         InputCount          Number of inputs
 *   Variable       InputList           Inputs
 *   VarInt         OutputCount         Number of outputs
 *   Variable       OutputList          Outputs
 *   4 bytes        LockTime            Transaction lock time
 * </pre>
 *
 * <p>
 * All numbers are encoded in little-endian format (least-significant byte to
 * most-significant byte)
 * </p>
 */
public class Transaction implements ByteSerializable {

	/** Serialized transaction data */
	private final byte[] txData;

	/** Transaction version */
	private final int txVersion;

	/** Transaction hash */
	private final Sha256Hash txHash;

	/** Normalized transaction ID */
	private final Sha256Hash normID;

	/** Transaction lock time */
	private final long txLockTime;

	/* This a coinbase transaction */
	private final boolean coinBase;

	/** List of transaction inputs */
	private final List<TransactionInput> txInputs;

	/** List of transaction outputs */
	private final List<TransactionOutput> txOutputs;

	/**
	 * Creates a new transaction using the provided inputs
	 *
	 * @param inputs
	 *            List of signed inputs
	 * @param outputs
	 *            List of outputs
	 * @throws ECException
	 *             Unable to sign transaction
	 * @throws ScriptException
	 *             Script processing error
	 * @throws VerificationException
	 *             Transaction verification failure
	 */
	public Transaction(List<SignedInput> inputs, List<TransactionOutput> outputs)
			throws ECException, ScriptException, VerificationException {
		SerializedBuffer outBuffer = new SerializedBuffer(1024);
		txVersion = 1;
		txOutputs = outputs;
		txLockTime = 0;
		coinBase = false;
		//
		// Create the transaction inputs
		//
		txInputs = new ArrayList<>(inputs.size());
		for (int i = 0; i < inputs.size(); i++)
			txInputs.add(new TransactionInput(this, i, inputs.get(i).getOutPoint()));
		//
		// Now sign each input and create the input scripts
		//
		for (int i = 0; i < inputs.size(); i++) {
			SignedInput input = inputs.get(i);
			ECKey key = input.getKey();
			byte[] contents;
			//
			// Serialize the transaction for signing using the SIGHASH_ALL hash
			// type
			//
			outBuffer.rewind();
			serializeForSignature(i, ScriptOpCodes.SIGHASH_ALL, input.getScriptBytes(), outBuffer);
			outBuffer.putInt(ScriptOpCodes.SIGHASH_ALL);
			contents = outBuffer.toByteArray();
			//
			// Create the DER-encoded signature
			//
			ECDSASignature sig = key.createSignature(contents);
			byte[] encodedSig = sig.encodeToDER();
			//
			// Create the input script using the SIGHASH_ALL hash type
			// <sig> <pubKey>
			//
			byte[] pubKey = key.getPubKey();
			byte[] scriptBytes = new byte[1 + encodedSig.length + 1 + 1 + pubKey.length];
			scriptBytes[0] = (byte) (encodedSig.length + 1);
			System.arraycopy(encodedSig, 0, scriptBytes, 1, encodedSig.length);
			int offset = encodedSig.length + 1;
			scriptBytes[offset++] = (byte) ScriptOpCodes.SIGHASH_ALL;
			scriptBytes[offset++] = (byte) pubKey.length;
			System.arraycopy(pubKey, 0, scriptBytes, offset, pubKey.length);
			txInputs.get(i).setScriptBytes(scriptBytes);
		}
		//
		// Serialize the entire transaction
		//
		outBuffer.rewind();
		getBytes(outBuffer);
		txData = outBuffer.toByteArray();
		//
		// Calculate the transaction hash using the serialized data
		//
		txHash = new Sha256Hash(Helper.reverseBytes(Helper.doubleDigest(txData)));
		//
		// Calculate the normalized transaction ID
		//
		List<byte[]> bufferList = new ArrayList<>(txInputs.size() + txOutputs.size());
		for (TransactionInput tx : txInputs) {
			bufferList.add(tx.getOutPoint().getBytes());
		}
		for (TransactionOutput tx : txOutputs) {
			bufferList.add(tx.getBytes());
		}

		normID = new Sha256Hash(Helper.reverseBytes(Helper.doubleDigest(bufferList)));
	}

	/**
	 * Creates a new transaction from the serialized data in the byte stream
	 *
	 * @param inBuffer
	 *            Serialized buffer
	 * @throws EOFException
	 *             Byte stream is too short
	 * @throws VerificationException
	 *             Verification error
	 */
	public Transaction(SerializedBuffer inBuffer) throws EOFException, VerificationException {
		//
		// Mark our current position within the input stream
		//
		int segmentStart = inBuffer.getSegmentStart();
		inBuffer.setSegmentStart();
		//
		// Get the transaction version
		//
		txVersion = inBuffer.getInt();
		//
		// Get the transaction inputs
		//
		int inCount = inBuffer.getVarInt();
		if (inCount < 0)
			throw new VerificationException("Transaction input count is negative");
		txInputs = new ArrayList<>(Math.max(inCount, 1));
		for (int i = 0; i < inCount; i++)
			txInputs.add(new TransactionInput(this, i, inBuffer));
		//
		// A coinbase transaction has a single unconnected input with a
		// transaction hash of zero
		// and an output index of -1
		//
		if (txInputs.size() == 1) {
			OutPoint outPoint = txInputs.get(0).getOutPoint();
			coinBase = (outPoint.getHash().equals(Sha256Hash.ZERO_HASH) && outPoint.getIndex() == -1);
		} else {
			coinBase = false;
		}
		//
		// Get the transaction outputs
		//
		int outCount = inBuffer.getVarInt();
		if (outCount < 0)
			throw new EOFException("Transaction output count is negative");
		txOutputs = new ArrayList<>(Math.max(outCount, 1));
		for (int i = 0; i < outCount; i++)
			txOutputs.add(new TransactionOutput(i, inBuffer));
		//
		// Get the transaction lock time
		//
		txLockTime = inBuffer.getUnsignedInt();
		//
		// Save a copy of the serialized transaction
		//
		txData = inBuffer.getSegmentBytes();
		//
		// Calculate the transaction hash using the serialized data
		//
		txHash = new Sha256Hash(Helper.reverseBytes(Helper.doubleDigest(txData)));
		//
		// Calculate the normalized transaction ID
		//
		List<byte[]> bufferList = new ArrayList<>(txInputs.size() + txOutputs.size());

		for (TransactionInput tx : txInputs) {
			bufferList.add(tx.getOutPoint().getBytes());
		}
		for (TransactionOutput tx : txOutputs) {
			bufferList.add(tx.getBytes());
		}

		normID = new Sha256Hash(Helper.reverseBytes(Helper.doubleDigest(bufferList)));
		//
		// Restore the previous segment (if any)
		//
		inBuffer.setSegmentStart(segmentStart);
	}

	/**
	 * Serialize the transaction
	 *
	 * @param outBuffer
	 *            Output buffer
	 * @return Output buffer
	 */
	@Override
	public final SerializedBuffer getBytes(SerializedBuffer outBuffer) {
		if (txData != null) {
			outBuffer.putBytes(txData);
		} else {
			outBuffer.putInt(txVersion).putVarInt(txInputs.size()).putBytes(txInputs).putVarInt(txOutputs.size())
					.putBytes(txOutputs).putUnsignedInt(txLockTime);
		}
		return outBuffer;
	}

	/**
	 * Returns the original serialized transaction data
	 *
	 * @return Serialized transaction data
	 */
	@Override
	public byte[] getBytes() {
		return txData;
	}

	/**
	 * Returns the transaction version
	 *
	 * @return Transaction version
	 */
	public long getVersion() {
		return txVersion;
	}

	/**
	 * Returns the transaction lock time
	 *
	 * @return Transaction lock time or zero
	 */
	public long getLockTime() {
		return txLockTime;
	}

	/**
	 * Returns the transaction hash
	 *
	 * @return Transaction hash
	 */
	public Sha256Hash getHash() {
		return txHash;
	}

	/**
	 * Returns the transaction hash as a printable string
	 *
	 * @return Transaction hash
	 */
	public String getHashAsString() {
		return txHash.toString();
	}

	/**
	 * Returns the normalized transaction ID
	 *
	 * @return Normalized transaction ID
	 */
	public Sha256Hash getNormalizedID() {
		return normID;
	}

	/**
	 * Returns the list of transaction inputs
	 *
	 * @return List of transaction inputs
	 */
	public List<TransactionInput> getInputs() {
		return txInputs;
	}

	/**
	 * Returns the list of transaction outputs
	 *
	 * @return List of transaction outputs
	 */
	public List<TransactionOutput> getOutputs() {
		return txOutputs;
	}

	/**
	 * Checks if this is the coinbase transaction
	 *
	 * @return TRUE if this is the coinbase transaction
	 */
	public boolean isCoinBase() {
		return coinBase;
	}

	/**
	 * Returns the hash code for this transaction. This is based on the
	 * transaction hash but is not the same value.
	 *
	 * @return Hash code
	 */
	@Override
	public int hashCode() {
		return getHash().hashCode();
	}

	/**
	 * Compare this transaction to another transaction to determine if they are
	 * equal.
	 *
	 * @param obj
	 *            The transaction to compare
	 * @return TRUE if they are equal
	 */
	@Override
	public boolean equals(Object obj) {
		boolean areEqual = false;
		if (obj != null && (obj instanceof Transaction))
			areEqual = getHash().equals(((Transaction) obj).getHash());

		return areEqual;
	}

	/**
	 * Returns a string representation of this transaction
	 *
	 * @return Formatted string
	 */
	@Override
	public String toString() {
		return String.format("Transaction: %s\n  %d inputs, %d outputs, %s", getHashAsString(), txInputs.size(),
				txOutputs.size(), (coinBase ? "Coinbase" : "Not coinbase"));
	}

	/**
	 * <p>
	 * Verify the transaction structure as follows
	 * </p>
	 * <ul>
	 * <li>A transaction must have at least one input and one output</li>
	 * <li>A transaction output may not specify a negative number of coins</li>
	 * <li>The sum of all of the output amounts must not exceed 21,000,000
	 * BTC</li>
	 * <li>A non-coinbase transaction may not contain any unconnected
	 * inputs</li>
	 * <li>A connected output may not be used by more than one input</li>
	 * <li>The input script must contain only push-data operations</li>
	 * </ul>
	 *
	 * @param canonical
	 *            TRUE to enforce canonical transactions
	 * @throws VerificationException
	 *             Script verification failed
	 */
	public void verify(boolean canonical) throws VerificationException {
		try {
			// Must have at least one input and one output
			if (txInputs.isEmpty() || txOutputs.isEmpty())
				throw new VerificationException("Transaction does not have at least 1 input and 1 output",
						RejectMessage.REJECT_INVALID, txHash);
			// No output value may be negative
			// Sum of all output values must not exceed MAX_MONEY
			BigInteger outTotal = BigInteger.ZERO;
			for (TransactionOutput txOut : txOutputs) {
				BigInteger outValue = txOut.getValue();
				if (outValue.signum() < 0)
					throw new VerificationException("Transaction output value is negative",
							RejectMessage.REJECT_INVALID, txHash);
				outTotal = outTotal.add(outValue);
				if (outTotal.compareTo(NetParams.MAX_MONEY) > 0)
					throw new VerificationException("Total transaction output amount exceeds maximum",
							RejectMessage.REJECT_INVALID, txHash);
				// byte[] scriptBytes = txOut.getScriptBytes();
			}
			if (!coinBase) {
				// All inputs must have connected outputs
				// No outpoint may be used more than once
				// Input scripts must consist of only push-data operations
				List<OutPoint> outPoints = new ArrayList<>(txInputs.size());
				for (TransactionInput txIn : txInputs) {
					OutPoint outPoint = txIn.getOutPoint();
					if (outPoint.getHash().equals(Sha256Hash.ZERO_HASH) || outPoint.getIndex() < 0)
						throw new VerificationException("Non-coinbase transaction contains unconnected inputs",
								RejectMessage.REJECT_INVALID, txHash);
					if (outPoints.contains(outPoint))
						throw new VerificationException("Connected output used in multiple inputs",
								RejectMessage.REJECT_INVALID, txHash);
					outPoints.add(outPoint);
					if (canonical) {
						if (!Script.checkInputScript(txIn.getScriptBytes()))
							throw new VerificationException(
									"Input script must contain only canonical push-data operations",
									RejectMessage.REJECT_NONSTANDARD, txHash);
					}
				}
			}
		} catch (EOFException exc) {
			throw new VerificationException("End-of-data while processing script", RejectMessage.REJECT_MALFORMED,
					txHash);
		}
	}

	/**
	 * Serializes the transaction for use in a signature
	 *
	 * @param index
	 *            Current transaction index
	 * @param sigHashType
	 *            Signature hash type
	 * @param subScriptBytes
	 *            Replacement script for the current input
	 * @param outBuffer
	 *            Output buffer
	 * @throws ScriptException
	 *             Transaction index out-of-range
	 */
	public final void serializeForSignature(int index, int sigHashType, byte[] subScriptBytes,
			SerializedBuffer outBuffer) throws ScriptException {
		int hashType;
		boolean anyoneCanPay;
		//
		// The transaction input must be within range
		//
		if (index < 0 || index >= txInputs.size())
			throw new ScriptException("Transaction input index is not valid");
		//
		// Check for a valid hash type
		//
		// Note that SIGHASH_ANYONE_CAN_PAY is or'ed with one of the other hash
		// types. So we need
		// to remove it when checking for a valid signature.
		//
		// SIGHASH_ALL: This is the default. It indicates that everything about
		// the transaction is signed
		// except for the input scripts. Signing the input scripts as well would
		// obviously make
		// it impossible to construct a transaction.
		// SIGHASH_NONE: The outputs are not signed and can be anything. This
		// mode allows others to update
		// the transaction by changing their inputs sequence numbers. This means
		// that all
		// input sequence numbers are set to 0 except for the current input.
		// SIGHASH_SINGLE: Outputs up to and including the current input index
		// number are included. Outputs
		// before the current index have a -1 value and an empty script. All
		// input sequence
		// numbers are set to 0 except for the current input.
		//
		// The SIGHASH_ANYONE_CAN_PAY modifier can be combined with the above
		// three modes. When set, only that
		// input is signed and the other inputs can be anything.
		//
		// In all cases, the script for the current input is replaced with the
		// script from the connected
		// output. All other input scripts are set to an empty script.
		//
		// The reference client accepts an invalid hash types and treats it as
		// SIGHASH_ALL. So we need to
		// do the same.
		//
		anyoneCanPay = ((sigHashType & ScriptOpCodes.SIGHASH_ANYONE_CAN_PAY) != 0);
		hashType = sigHashType & (255 - ScriptOpCodes.SIGHASH_ANYONE_CAN_PAY);
		if (hashType != ScriptOpCodes.SIGHASH_ALL && hashType != ScriptOpCodes.SIGHASH_NONE
				&& hashType != ScriptOpCodes.SIGHASH_SINGLE)
			hashType = ScriptOpCodes.SIGHASH_ALL;
		//
		// Serialize the version
		//
		outBuffer.putInt(txVersion);
		//
		// Serialize the inputs
		//
		// For SIGHASH_ANYONE_CAN_PAY, only the current input is included in the
		// signature.
		// Otherwise, all inputs are included.
		//
		List<TransactionInput> sigInputs;
		if (anyoneCanPay) {
			sigInputs = new ArrayList<>(1);
			sigInputs.add(txInputs.get(index));
		} else {
			sigInputs = txInputs;
		}
		outBuffer.putVarInt(sigInputs.size());
		byte[] emptyScriptBytes = new byte[0];
		for (TransactionInput txInput : sigInputs)
			txInput.serializeForSignature(index, hashType,
					(txInput.getIndex() == index ? subScriptBytes : emptyScriptBytes), outBuffer);
		//
		// Serialize the outputs
		//
		if (hashType == ScriptOpCodes.SIGHASH_NONE) {
			//
			// There are no outputs for SIGHASH_NONE
			//
			outBuffer.putVarInt(0);
		} else if (hashType == ScriptOpCodes.SIGHASH_SINGLE) {
			//
			// The output list is resized to the input index+1
			//
			if (txOutputs.size() <= index)
				throw new ScriptException("Input index out-of-range for SIGHASH_SINGLE");
			outBuffer.putVarInt(index + 1);
			for (TransactionOutput txOutput : txOutputs) {
				if (txOutput.getIndex() > index)
					break;
				txOutput.serializeForSignature(index, hashType, outBuffer);
			}
		} else {
			//
			// All outputs are serialized for SIGHASH_ALL
			//
			outBuffer.putVarInt(txOutputs.size());
			for (TransactionOutput txOutput : txOutputs)
				txOutput.serializeForSignature(index, hashType, outBuffer);
		}
		//
		// Serialize the lock time
		//
		outBuffer.putUnsignedInt(txLockTime);
	}
}
