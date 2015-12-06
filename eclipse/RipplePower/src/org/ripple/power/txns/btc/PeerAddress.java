package org.ripple.power.txns.btc;

import java.io.EOFException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * A PeerAddress holds an IP address and port number representing the network location of
 * a peer in the Bitcoin Peer-to-Peer network.
 */
public class PeerAddress implements ByteSerializable {

    /** Length of an encoded peer address */
    public static final int PEER_ADDRESS_SIZE = 30;

    /** IPv6-encoded IPv4 address prefix */
    public static final byte[] IPV6_PREFIX = new byte[] {
            (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
            (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xff, (byte)0xff
    };

    /** The IP address */
    private InetAddress address;

    /** The IP port */
    private int port;

    /** Time seen */
    private long timeSeen;

    /** Peer services */
    private long services;

    /** Peer connected */
    private boolean connected;

    /** Time peer connected */
    private long timeConnected;

    /** Outbound connection */
    private boolean outboundConnection;

    /** Static address */
    private boolean staticAddress;

    /** Address has been broadcast */
    private boolean wasBroadcast;

    /**
     * Constructs a peer address from the given IP address and port
     *
     * @param       address         IP address
     * @param       port            IP port
     */
    public PeerAddress(InetAddress address, int port) {
        this.address = address;
        this.port = port;
        timeSeen = System.currentTimeMillis()/1000;
    }

    /**
     * Constructs a peer address from the given IP address and port
     *
     * @param       address         IP address
     * @param       port            IP port
     * @param       timeSeen        Latest time peer was seen
     */
    public PeerAddress(InetAddress address, int port, long timeSeen) {
        this.address = address;
        this.port = port;
        this.timeSeen = timeSeen;
    }

    /**
     * Constructs a peer address from a network socket
     *
     * @param       socket          Network socket
     */
    public PeerAddress(InetSocketAddress socket) {
        this(socket.getAddress(), socket.getPort());
    }

    /**
     * Constructs a peer address from a string in the format "[address]:port" where
     * address can be "nnn.nnn.nnn.nnn" for IPv4 or "xxxx:xxxx:xxxx;xxxx:xxxx:xxxx:xxxx:xxxx"
     * for IPv6.
     *
     * @param       peerString              Address string
     * @throws      UnknownHostException    Incorrect address format
     */
    public PeerAddress(String peerString) throws UnknownHostException {
        //
        // Separate the address and the port
        //
        int addrSep = peerString.lastIndexOf(']');
        int portSep = peerString.lastIndexOf(':');
        if (peerString.charAt(0) != '[' || addrSep < 0 ||
                                portSep < addrSep || portSep == peerString.length()-1)
                throw new UnknownHostException("Incorrect [address]:port format");
        String addrString = peerString.substring(1, addrSep);
        String portString = peerString.substring(portSep+1);
        //
        // Create the address and port values
        //
        address = InetAddress.getByName(addrString);
        port = Integer.parseInt(portString);
        timeSeen = System.currentTimeMillis()/1000;
    }

    /**
     * Constructs a peer address from the serialized data
     *
     * @param       inBuffer        Serialized buffer
     * @throws      EOFException    End-of-data while processing serialized data
     */
    public PeerAddress(SerializedBuffer inBuffer) throws EOFException {
        //
        // Get the address values
        //
        timeSeen = inBuffer.getInt();
        services = inBuffer.getLong();
        byte[] addrBytes = inBuffer.getBytes(16);
        port = (inBuffer.getUnsignedByte()<<8) | inBuffer.getUnsignedByte();
        //
        // Generate the IPv4 or IPv6 address
        //
        try {
            boolean ipv4 = true;
            for (int j=0; j<12; j++) {
                if (addrBytes[j] != IPV6_PREFIX[j]) {
                    ipv4 = false;
                    break;
                }
            }
            if (ipv4)
                address = InetAddress.getByAddress(Arrays.copyOfRange(addrBytes, 12, 16));
            else
                address = InetAddress.getByAddress(addrBytes);
        } catch (UnknownHostException exc) {
            throw new RuntimeException("Unexpected exception thrown by InetAddress.getByAddress: "+exc.getMessage());
        }
    }

    /**
     * Writes the serialized address to a buffer
     *
     * @param       buffer          Serialized buffer
     * @return                      Serialized buffer
     */
    @Override
    public SerializedBuffer getBytes(SerializedBuffer buffer) {
        buffer.putInt((int)timeSeen)
              .putLong(services);
        byte[] addrBytes = address.getAddress();
        if (addrBytes.length == 16)
            buffer.putBytes(addrBytes);
        else
            buffer.putBytes(IPV6_PREFIX)
                  .putBytes(addrBytes);
        buffer.putUnsignedByte(port>>>8)
              .putUnsignedByte(port);
        return buffer;
    }

    /**
     * Returns the serialized address
     *
     * @return                      Serialized address
     */
    @Override
    public byte[] getBytes() {
        SerializedBuffer buffer = new SerializedBuffer(PEER_ADDRESS_SIZE);
        return getBytes(buffer).toByteArray();
    }

    /**
     * Returns the IP address
     *
     * @return                      IP address
     */
    public InetAddress getAddress() {
        return address;
    }

    /**
     * Sets the IP address
     *
     * @param       address         IP address
     */
    public void setAddress(InetAddress address) {
        this.address = address;
    }

    /**
     * Returns the IP port
     *
     * @return                      IP port
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets the IP port
     *
     * @param       port            IP port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Returns the timestamp for this peer
     *
     * @return                          Timestamp in seconds since the epoch
     */
    public long getTimeStamp() {
        return timeSeen;
    }

    /**
     * Sets the timestamp for this peer
     *
     * @param       timeSeen            Time peer was seen in seconds since the epoch
     */
    public void setTimeStamp(long timeSeen) {
        this.timeSeen = timeSeen;
    }

    /**
     * Check if address has been broadcast
     *
     * @return                          True if address has been broadcast
     */
    public boolean wasBroadcast() {
        return wasBroadcast;
    }

    /**
     * Set address broadcast status
     *
     * @param       wasBroadcast        True if the address has been broadcast
     */
    public void setBroadcast(boolean wasBroadcast) {
        this.wasBroadcast = wasBroadcast;
    }

    /**
     * Sets the peer services
     *
     * @param       services            Peer services
     */
    public void setServices(long services) {
        this.services = services;
    }

    /**
     * Returns the peer services
     *
     * @return      Peer services
     */
    public long getServices() {
        return services;
    }

    /**
     * Checks if this peer is connected
     *
     * @return      TRUE if the peer is connected
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Sets the peer connection status
     *
     * @param       isConnected     TRUE if the peer is connected
     */
    public void setConnected(boolean isConnected) {
        connected = isConnected;
    }

    /**
     * Returns the time the peer connected to us
     *
     * @return                      Time connected
     */
    public long getTimeConnected() {
        return timeConnected;
    }

    /**
     * Sets the time the peer connected to us
     *
     * @param       timeConnected   Time the peer connected (seconds since the epoch)
     */
    public void setTimeConnected(long timeConnected) {
        this.timeConnected = timeConnected;
    }

    /**
     * Checks if this is an outbound connection
     *
     * @return      TRUE if this is an outbound connection
     */
    public boolean isOutbound() {
        return outboundConnection;
    }

    /**
     * Set the peer connection type
     *
     * @param       isOutbound          TRUE if this is an outbound connection
     */
    public void setOutbound(boolean isOutbound) {
        outboundConnection = isOutbound;
    }

    /**
     * Check if this is a static address
     *
     * @return      TRUE if this is a static address
     */
    public boolean isStatic() {
        return staticAddress;
    }

    /**
     * Set the address type
     *
     * @param       isStatic            TRUE if this is a static address
     */
    public void setStatic(boolean isStatic) {
        staticAddress = isStatic;
    }

    /**
     * Return a socket address for our IP address and port
     *
     * @return                      Socket address
     */
    public InetSocketAddress toSocketAddress() {
        return new InetSocketAddress(address, port);
    }

    /**
     * Returns a string representation of the IP address and port
     *
     * @return                      String representation
     */
    @Override
    public String toString() {
        return String.format("[%s]:%d", address.getHostAddress(), port);
    }

    /**
     * Checks if the supplied address is equal to this address
     *
     * @param       obj             Address to check
     * @return                      TRUE if the addresses are equal
     */
    @Override
    public boolean equals(Object obj) {
        return (obj!=null && (obj instanceof PeerAddress) &&
                address.equals(((PeerAddress)obj).address) && port == ((PeerAddress)obj).port);
    }

    /**
     * Returns the hash code for this object
     *
     * @return                      The hash code
     */
    @Override
    public int hashCode() {
        return (address.hashCode()^port);
    }
}
