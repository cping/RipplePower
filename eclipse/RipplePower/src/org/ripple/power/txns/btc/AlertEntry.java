package org.ripple.power.txns.btc;

import java.io.EOFException;

public class AlertEntry {

    /** Cancel status */
    private boolean isCanceled;

    /** Alert payload */
    private final byte[] payload;

    /** Alert signature */
    private final byte[] signature;

    /**
     * Creates a new AlertEntry
     *
     * @param       payload         Alert payload
     * @param       signature       Alert signature
     * @param       isCanceled      TRUE if the alert has been canceled
     */
    public AlertEntry(byte[] payload, byte[] signature, boolean isCanceled) {
        this.isCanceled = isCanceled;
        this.payload = payload;
        this.signature = signature;
    }

    /**
     * Creates a new TransactionEntry
     *
     * @param       entryData       Serialized entry data
     * @throws      EOFException    End-of-data processing serialized data
     */
    public AlertEntry(byte[] entryData) throws EOFException {
        SerializedBuffer inBuffer = new SerializedBuffer(entryData);
        isCanceled = inBuffer.getBoolean();
        payload = inBuffer.getBytes();
        signature = inBuffer.getBytes();
    }

    /**
     * Returns the serialized data stream
     *
     * @return      Serialized data stream
     */
    public byte[] getBytes() {
        SerializedBuffer outBuffer = new SerializedBuffer();
        outBuffer.putBoolean(isCanceled)
                 .putVarInt(payload.length)
                 .putBytes(payload)
                 .putVarInt(signature.length)
                 .putBytes(signature);
        return outBuffer.toByteArray();
    }

    /**
     * Returns the payload
     *
     * @return      Alert payload
     */
    public byte[] getPayload() {
        return payload;
    }

    /**
     * Returns the signature
     *
     * @return      Alert signature
     */
    public byte[] getSignature() {
        return signature;
    }

    /**
     * Checks if the alert has been canceled
     *
     * @return      TRUE if the alert has been canceled
     */
    public boolean isCanceled() {
        return isCanceled;
    }

    /**
     * Set the alert cancel status
     *
     * @param       isCanceled      TRUE if the alert has been canceled
     */
    public void setCancel(boolean isCanceled) {
        this.isCanceled = isCanceled;
    }
}
