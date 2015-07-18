package org.ripple.power.txns.btc;

import java.io.EOFException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.ripple.power.config.LSystem;

/**
 * <p>The 'version' message is exchanged when two nodes connect.  It identifies
 * the services provided by the nodes and the latest block each has seen.  A node
 * responds with a 'verack' message if it accepts the connection, otherwise the
 * node will close the connection.</p>
 *
 * <p>Version Message:</p>
 * <pre>
 *   Size       Field               Description
 *   ====       =====               ===========
 *   4 bytes    Version             Protocol version
 *   8 bytes    Services            Supported services (bit field)
 *   8 bytes    Timestamp           Time in seconds since the epoch
 *  26 bytes    RemoteAddress       Remote node address
 *  26 bytes    LocalAddress        Local node address
 *   8 bytes    Nonce               Random value to identify sending node
 *  VarString   UserAgent           Identification string
 *   4 bytes    BlockHeight         Last block received by sending node
 *   1 byte     TxRelay             TRUE if remote peer should relay transactions
 * </pre>
 *
 * <p>Network Address:</p>
 * <pre>
 *   Size       Field               Description
 *   ====       =====               ===========
 *   8 bytes    Services            Supported services (same as 'version' message)
 *  16 bytes    NetworkAddress      IPv6 address (IPv4 address encoded as IPv6 address)
 *   2 bytes    Port                Port (network byte order)
 * </pre>
 */
public class VersionMessage {

    /** Node identifier for this peer execution */
    public static final long NODE_ID = Double.doubleToRawLongBits(Double.valueOf(Math.random()));

    /**
     * Builds a 'version' message
     *
     * @param       peer                The remote peer
     * @param       localAddress        Local listen address or null if not accepting inbound connections
     * @param       chainHeight         Current chain height
     * @return                          Message to send to remote peer
     */
    public static Message buildVersionMessage(Peer peer, PeerAddress localAddress, int chainHeight) {
        //
        // Set the protocol version, supported services and current time
        //
        SerializedBuffer msgBuffer = new SerializedBuffer();
        msgBuffer.putInt(NetParams.PROTOCOL_VERSION)
                 .putLong(NetParams.SUPPORTED_SERVICES)
                 .putLong(System.currentTimeMillis()/1000);
        //
        // Set the destination address
        //
        PeerAddress peerAddress = peer.getAddress();
        byte[] dstAddress = peerAddress.getAddress().getAddress();
        msgBuffer.skip(8);
        if (dstAddress.length == 16) {
            msgBuffer.putBytes(dstAddress);
        } else {
            msgBuffer.putBytes(PeerAddress.IPV6_PREFIX);
            msgBuffer.putBytes(dstAddress);
        }
        msgBuffer.putUnsignedByte(peerAddress.getPort()>>>8)
                 .putUnsignedByte(peerAddress.getPort());
        //
        // Set the source address
        //
        msgBuffer.putLong(NetParams.SUPPORTED_SERVICES);
        if (localAddress != null) {
            byte[] srcAddress = localAddress.getAddress().getAddress();
            if (srcAddress.length == 16) {
                msgBuffer.putBytes(srcAddress);
            } else {
                msgBuffer.putBytes(PeerAddress.IPV6_PREFIX)
                         .putBytes(srcAddress);
            }
            msgBuffer.putUnsignedByte(localAddress.getPort()>>>8)
                     .putUnsignedByte(localAddress.getPort());
        } else {
            msgBuffer.skip(16+2);
        }
        //
        // Set the random node identifier
        //
        msgBuffer.putLong(NODE_ID);
        //
        // Set the agent name
        //
        String agentName = String.format("/%s/%s/",LSystem.applicationName, LSystem.applicationVersion);
        msgBuffer.putString(agentName);
        //
        // Set the chain height and transaction relay flag
        //
        msgBuffer.putInt(chainHeight);
        msgBuffer.putBoolean((NetParams.SUPPORTED_SERVICES&NetParams.NODE_NETWORK)!=0);
        //
        // Build the message
        //
        ByteBuffer buffer = MessageHeader.buildMessage("version", msgBuffer);
        return new Message(buffer, peer, MessageHeader.MessageCommand.VERSION);
    }

    /**
     * Processes a 'version' message
     *
     * @param       msg                     Message
     * @param       inBuffer                Input buffer
     * @param       msgListener             Message listener
     * @throws      EOFException            End-of-data processing message data
     * @throws      VerificationException   Message verification failed
     */
    public static void processVersionMessage(Message msg, SerializedBuffer inBuffer, MessageListener msgListener)
                                            throws EOFException, VerificationException {

        Peer peer = msg.getPeer();
        //
        // Validate the protocol level
        //
        int version = inBuffer.getInt();
        if (version < NetParams.MIN_PROTOCOL_VERSION)
            throw new VerificationException(String.format("Protocol version %d is not supported", version),
                                            RejectMessage.REJECT_OBSOLETE);
        peer.setVersion(version);
        //
        // Get the peer services
        //
        peer.setServices(inBuffer.getLong());
        peer.getAddress().setServices(peer.getServices());
        //
        // Get our address as seen by the peer
        //
        inBuffer.skip(8+8);
        byte[] addrBytes = inBuffer.getBytes(16);
        InetAddress addr;
        try {
            boolean ipv4 = true;
            for (int j=0; j<12; j++) {
                if (addrBytes[j] != PeerAddress.IPV6_PREFIX[j]) {
                    ipv4 = false;
                    break;
                }
            }
            if (ipv4)
                addr = InetAddress.getByAddress(Arrays.copyOfRange(addrBytes, 12, 16));
            else
                addr = InetAddress.getByAddress(addrBytes);
        } catch (UnknownHostException exc) {
            throw new VerificationException("Destination address is not valid: "+exc.getMessage());
        }
        PeerAddress localAddress = new PeerAddress(addr, (inBuffer.getUnsignedByte()<<8)|inBuffer.getUnsignedByte());
        //
        // Get the user agent
        //
        inBuffer.skip(26+8);
        peer.setUserAgent(inBuffer.getString());
        //
        // Get the chain height and transaction relay flag (the transaction relay flag is
        // not included in earlier protocol versions and defaults to TRUE).  Always relay
        // block notifications.
        //
        peer.setHeight(inBuffer.getInt());
        peer.setTxRelay(inBuffer.available()==0 || inBuffer.getByte()!=0);
        peer.setBlockRelay(true);
        //
        // Notify the message listener
        //
        msgListener.processVersion(msg, localAddress);
    }
}
