package org.ripple.power.txns.btc;

import java.io.EOFException;
import java.math.BigInteger;
import java.util.Arrays;

/**
  * <p>A transaction output has the following format:</p>
 * <pre>
 *   Size           Field               Description
 *   ====           =====               ===========
 *   8 bytes        TxOutValue          Value expressed in Satoshis (0.00000001 BTC)
 *   VarInt         TxOutScriptLength   Script length
 *   Variable       TxOutScript         Script
 * </pre>
 *
 * <p>All numbers are encoded in little-endian format (least-significant byte to most-significant byte)</p>
 */
public class TransactionOutput implements ByteSerializable {

    /** Unspendable 'Proof-of-burn' script (1CounterpartyXXXX...) */
    private final byte[] unspendableScript = new byte[] {
        (byte)0x76, (byte)0xa9, (byte)0x14,
        (byte)0x81, (byte)0x88, (byte)0x95, (byte)0xf3, (byte)0xdc, (byte)0x2c, (byte)0x17, (byte)0x86,
        (byte)0x29, (byte)0xd3, (byte)0xd2, (byte)0xd8, (byte)0xfa, (byte)0x3e, (byte)0xc4, (byte)0xa3,
        (byte)0xf8, (byte)0x17, (byte)0x98, (byte)0x21,
        (byte)0x88, (byte)0xac
    };

    /** Output value in Satoshis (0.00000001 BTC) */
    private final BigInteger value;

    /** Transaction output index */
    private final int txIndex;

    /** Output script */
    private final byte[] scriptBytes;

    /**
     * Creates a transaction output for the specified amount using a
     * PAY_TO_PUBKEY_HASH script
     *
     * @param       txIndex                 Transaction output index
     * @param       value                   Transaction output value
     * @param       address                 Send address
     */
    public TransactionOutput(int txIndex, BigInteger value, Address address) {
        this.txIndex = txIndex;
        this.value = value;
        //
        // Create the output script for PAY_TO_PUBKEY_HASH
        //   OP_DUP OP_HASH160 <pubkey-hash> OP_EQUALVERIFY OP_CHECKSIG
        //
        scriptBytes = new byte[1+1+1+20+1+1];
        scriptBytes[0] = (byte)ScriptOpCodes.OP_DUP;
        scriptBytes[1] = (byte)ScriptOpCodes.OP_HASH160;
        scriptBytes[2] = (byte)20;
        System.arraycopy(address.getHash(), 0, scriptBytes, 3, 20);
        scriptBytes[23] = (byte)ScriptOpCodes.OP_EQUALVERIFY;
        scriptBytes[24] = (byte)ScriptOpCodes.OP_CHECKSIG;
    }

    /**
     * Creates a transaction output for the specified amount using the supplied script
     *
     * @param       txIndex                 Transaction output index
     * @param       value                   Transaction output value
     * @param       scriptBytes             Transaction output script
     */
    public TransactionOutput(int txIndex, BigInteger value, byte[] scriptBytes) {
        this.txIndex = txIndex;
        this.value = value;
        this.scriptBytes = scriptBytes;
    }

    /**
     * Creates a transaction output from the encoded byte stream
     *
     * @param       txIndex                 Index within the transaction output list
     * @param       inBuffer                Input stream
     * @throws      EOFException            Input stream is too short
     * @throws      VerificationException   Verification failed
     */
    public TransactionOutput(int txIndex, SerializedBuffer inBuffer) throws EOFException, VerificationException {
        this.txIndex = txIndex;
        //
        // Get the amount
        //
        value = BigInteger.valueOf(inBuffer.getLong());
        //
        // Get the script
        //
        scriptBytes = inBuffer.getBytes();
    }

    /**
     * Return the serialized transaction output
     *
     * @param       outBuffer       Output buffer
     * @return                      Output buffer
     */
    @Override
    public SerializedBuffer getBytes(SerializedBuffer outBuffer) {
        outBuffer.putLong(value.longValue())
                 .putVarInt(scriptBytes.length)
                 .putBytes(scriptBytes);
        return outBuffer;
    }

    /**
     * Returns the serialized transaction output
     *
     * @return                      Serialized transaction output
     */
    @Override
    public byte[] getBytes() {
        SerializedBuffer buffer = new SerializedBuffer();
        return getBytes(buffer).toByteArray();
    }

    /**
     * Returns the output amount
     *
     * @return      Output amount
     */
    public BigInteger getValue() {
        return value;
    }

    /**
     * Returns the transaction index for this output
     *
     * @return      Transaction index
     */
    public int getIndex() {
        return txIndex;
    }

    /**
     * Returns the script bytes
     *
     * @return      Script bytes or null
     */
    public byte[] getScriptBytes() {
        return scriptBytes;
    }

    /**
     * Checks if the output is spendable.  This is done by checking for OP_RETURN
     * as the first script operation.  Any script starting this way can never be
     * spent.  Note that an empty script is always spendable.
     *
     * Proof-of-burn transactions are sent to '1CounterpartyXXXXXXXXXXXXXXXUWLpVr'.
     * This address has no private key and thus can never be spent.  So we will
     * mark it as unspendable.
     *
     * @return                      TRUE if the output is spendable
     */
    public boolean isSpendable() {
        boolean spendable = true;
        if (scriptBytes.length > 0) {
            if (scriptBytes[0] == ScriptOpCodes.OP_RETURN)
                spendable = false;
            else if (Arrays.equals(scriptBytes, unspendableScript))
                spendable = false;
        }
        return spendable;
    }

    /**
     * Serializes this output for use in a transaction signature
     *
     * @param       index           Index of input being signed
     * @param       hashType        The signature hash type
     * @param       outBuffer       Output buffer
     */
    public void serializeForSignature(int index, int hashType, SerializedBuffer outBuffer) {
        if (hashType == ScriptOpCodes.SIGHASH_SINGLE && index != txIndex) {
            //
            // For SIGHASH_SINGLE, we have a zero-length script and a value of -1
            //
            outBuffer.putLong(-1L)
                     .putVarInt(0);
        } else {
            //
            // Encode normally
            //
            outBuffer.putLong(value.longValue())
                     .putVarInt(scriptBytes.length)
                     .putBytes(scriptBytes);
        }
    }
}
