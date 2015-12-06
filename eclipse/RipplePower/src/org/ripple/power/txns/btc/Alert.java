package org.ripple.power.txns.btc;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>An alert is sent out by the development team to notify all peers in the network
 * about a problem.  The alert is displayed in the user interface and written to the
 * log.  It is also sent each time a node connects to another node until the relay time
 * is exceeded or the alert is canceled.</p>
 *
 * <p>Alert Payload</p>
 * <pre>
 *   Size       Field           Description
 *   ====       =====           ===========
 *   4 bytes    Version         Alert version
 *   8 bytes    RelayUntil      Relay the alert until this time (seconds)
 *   8 bytes    Expires         Alert expires at this time (seconds)
 *   4 bytes    AlertID         Unique identifier for this alert
 *   4 bytes    CancelID        Cancel the alert with this identifier
 *    IntSet    CancelSet       Set of alert identifiers to cancel
 *   4 bytes    MinVersion      Minimum applicable protocol version
 *   4 bytes    MaxVersion      Maximum applicable protocol version
 *    StrSet    SubVersionSet   Applicable subversions
 *   4 bytes    Priority        Relative priority
 *   String     Comment         Comment about the alert
 *   String     Status          Alert message to display and log
 *   String     Reserved        Reserved for future use
 * </pre>
 */
public class Alert {

    /** Alert payload */
    private final byte[] payload;

    /** Alert signature */
    private final byte[] signature;

    /** Alert version */
    private final int version;

    /** Relay until time */
    private final long relayTime;

    /** Expiration time */
    private final long expireTime;

    /** Alert ID */
    private final int alertID;

    /** Cancel ID */
    private final int cancelID;

    /** Cancel set */
    private final List<Integer> cancelSet;

    /** Min applicable version */
    private final int minVersion;

    /** Max applicable version */
    private final int maxVersion;

    /** Subversion */
    private final List<String> subVersions;

    /** Priority */
    private final int priority;

    /** Comment */
    private final String comment;

    /** Message */
    private final String message;

    /** Cancel status */
    private boolean isCanceled;

    /**
     * Creates an alert from the serialized message data
     *
     * @param       payload             Alert payload
     * @param       signature           Alert signature
     * @throws      EOFException        End-of-data while processing payload
     */
    public Alert(byte[] payload, byte[] signature) throws EOFException {
        this.payload = payload;
        this.signature = signature;
        //
        // Get version, relayTime, expireTime, alertID and cancelID
        //
        SerializedBuffer inBuffer = new SerializedBuffer(payload);
        version = inBuffer.getInt();
        relayTime = inBuffer.getLong();
        expireTime = inBuffer.getLong();
        alertID = inBuffer.getInt();
        cancelID = inBuffer.getInt();
        //
        // Get the cancel set
        //
        int setCount = inBuffer.getVarInt();
        cancelSet = new ArrayList<>(Math.max(setCount, 1));
        for (int i=0; i<setCount; i++)
            cancelSet.add(inBuffer.getInt());
        //
        // Get minVersion and maxVersion
        //
        minVersion = inBuffer.getInt();
        maxVersion = inBuffer.getInt();
        //
        // Get the subversions
        //
        int subCount = inBuffer.getVarInt();
        subVersions = new ArrayList<>(Math.max(subCount, 1));
        for (int i=0; i<subCount; i++)
            subVersions.add(inBuffer.getString());
        //
        // Get the priority
        //
        priority = inBuffer.getInt();
        //
        // Get the comment
        //
        comment = inBuffer.getString();
        //
        // Get the alert message
        //
        message = inBuffer.getString();
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
     * Returns the alert ID
     *
     * @return      Alert ID
     */
    public int getID() {
        return alertID;
    }

    /**
     * Returns the cancel ID
     *
     * @return      Cancel ID
     */
    public int getCancelID() {
        return cancelID;
    }

    /**
     * Returns the cancel set
     *
     * @return      Cancel set
     */
    public List<Integer> getCancelSet() {
        return cancelSet;
    }

    /**
     * Returns the alert message
     *
     * @return      Alert message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the relay until time
     *
     * @return      Relay until time
     */
    public long getRelayTime() {
        return relayTime;
    }

    /**
     * Returns the expiration time
     *
     * @return      Expiration time
     */
    public long getExpireTime() {
        return expireTime;
    }

    /**
     * Returns the alert priority
     *
     * @return      Alert priority
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Returns the message version
     *
     * @return      Alert message version
     */
    public int getVersion() {
        return version;
    }

    /**
     * Returns the minimum protocol version
     *
     * @return      Minimum protocol version
     */
    public int getMinVersion() {
        return minVersion;
    }

    /**
     * Returns the maximum protocol version
     *
     * @return      Maximum protocol version
     */
    public int getMaxVersion() {
        return maxVersion;
    }

    /**
     * Returns the user agent (subVersion) list
     *
     * @return      User agent list
     */
    public List<String> getSubVersions() {
        return subVersions;
    }

    /**
     * Returns the developer comment
     *
     * @return      Developer comment
     */
    public String getComment() {
        return comment;
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
     * Sets the alert cancel status
     *
     * @param       isCanceled          TRUE if the alert has been canceled
     */
    public void setCancel(boolean isCanceled) {
        this.isCanceled = isCanceled;
    }
}
