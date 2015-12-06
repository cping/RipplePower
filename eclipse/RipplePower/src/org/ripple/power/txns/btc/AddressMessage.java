package org.ripple.power.txns.btc;

import java.io.EOFException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>An 'addr' message is sent to inform a node about peers on the network.</p>
 *
 * <p>Address Message</p>
 * <pre>
 *   Size       Field           Description
 *   ====       =====           ===========
 *   VarInt     Count           The number of addresses
 *   Variable   Addresses       One or more network addresses
 * </pre>
 *
 * <p>Network Address</p>
 * <pre>
 *   Size       Field           Description
 *   ====       =====           ===========
 *   4 bytes    Time            Timestamp in seconds since the epoch
 *   8 bytes    Services        Services provided by the node
 *  16 bytes    Address         IPv6 address (IPv4 addresses are encoded as IPv6 addresses)
 *   2 bytes    Port            Port (network byte order)
 * </pre>
 */
public class AddressMessage {

    /**
     * Build an 'addr' message
     *
     * We will include the first 1000 peers as well as our own external listen address
     *
     * @param       peer                The destination peer or null for a broadcast message
     * @param       addressList         Peer address list
     * @param       localAddress        Local address or null if not accepting inbound connections
     * @return                          'addr' message
     */
    public static Message buildAddressMessage(Peer peer, List<PeerAddress> addressList, PeerAddress localAddress) {
        //
        // Create an address list containing the first 1000 peers plus our own local address.
        // Static addresses are not included in the list nor are peers that provide no services.
        //
        List<PeerAddress> addresses = new ArrayList<>(1000);
        if (localAddress != null) {
            PeerAddress addr = new PeerAddress(localAddress.getAddress(), localAddress.getPort());
            addr.setServices(NetParams.SUPPORTED_SERVICES);
            addresses.add(addr);
        }
        for (PeerAddress address : addressList) {
            if (addresses.size() >= 1000)
                break;
            if (!address.isStatic() && address.getServices() != 0)
                addresses.add(address);
        }
        //
        // Build the message payload
        //
        int bufferLength = addresses.size()*PeerAddress.PEER_ADDRESS_SIZE + 5;
        SerializedBuffer msgBuffer = new SerializedBuffer(bufferLength);
        msgBuffer.putVarInt(addresses.size())
                 .putBytes(addresses);
        //
        // Build the message
        //
        ByteBuffer buffer = MessageHeader.buildMessage("addr", msgBuffer);
        return new Message(buffer, peer, MessageHeader.MessageCommand.ADDR);
    }

    /**
     * Process an 'addr' message
     *
     * Build the address list and then notify the processAddress() message listener.
     *
     * @param       msg                     Message
     * @param       inBuffer                Message buffer
     * @param       msgListener             Message listener
     * @throws      EOFException            End-of-data while processing stream
     * @throws      VerificationException   Message contains more than 1000 entries
     */
    public static void processAddressMessage(Message msg, SerializedBuffer inBuffer, MessageListener msgListener)
                                            throws EOFException, VerificationException {
        //
        // Get the address count
        //
        int addrCount = inBuffer.getVarInt();
        if (addrCount < 0 || addrCount > 1000)
            throw new VerificationException("More than 1000 addresses in 'addr' message");
        //
        // Build the address list
        //
        List<PeerAddress> addresses = new ArrayList<>(addrCount);
        for (int i=0; i<addrCount; i++)
            addresses.add(new PeerAddress(inBuffer));
        //
        // Notify the application message listener
        //
        if (!addresses.isEmpty())
            msgListener.processAddresses(msg, addresses);
    }
}
