package org.ripple.power.txns.btc;

import java.nio.ByteBuffer;

/**
 * The 'verack' message is sent in response to the 'version' message.  It consists of
 * just the message header.
 */
public class VersionAckMessage {

    /**
     * Build the 'verack' message
     *
     * @param       peer                The destination peer
     * @return                          'verack' message
     */
    public static Message buildVersionAckMessage(Peer peer) {
        ByteBuffer buffer = MessageHeader.buildMessage("verack", new byte[0]);
        return new Message(buffer, peer, MessageHeader.MessageCommand.VERACK);
    }

    /**
     * Process the 'verack' message
     *
     * @param       msg                 Message
     * @param       inBuffer            Input buffer
     * @param       msgListener         Message listener
     */
    public static void processVersionAckMessage(Message msg, SerializedBuffer inBuffer, MessageListener msgListener) {
        msgListener.processVersionAck(msg);
    }
}
