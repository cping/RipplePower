package org.ripple.power.txns.btc;

import java.io.EOFException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>The 'inv' message is sent by a remote peer to advertise blocks and transactions
 * that are available.  This message can be unsolicited or in response to a 'getblocks'
 * request.</p>
 *
 * <p>We will add items that we don't have to the 'pendingRequests' queue.  This will cause
 * the network handler to send 'getdata' requests to get the missing items.</p>
 *
 * <p>Inventory Message:</p>
 * <pre>
 *   Size       Field               Description
 *   ====       =====               ===========
 *   VarInt     Count               Number of inventory items
 *   Variable   InvItems            One or more inventory items
 * </pre>
 */
public class InventoryMessage {

    /**
     * Build an 'inv' message
     *
     * @param       peer            Destination peer
     * @param       itemList        Inventory item list
     * @return                      'inv' message
     */
    public static Message buildInventoryMessage(Peer peer, List<InventoryItem> itemList) {
        //
        // Build the message data
        //
        SerializedBuffer msgBuffer = new SerializedBuffer(itemList.size()*36+4);
        msgBuffer.putVarInt(itemList.size())
                 .putBytes(itemList);
        //
        // Build the message
        //
        ByteBuffer buffer = MessageHeader.buildMessage("inv", msgBuffer);
        return new Message(buffer, peer, MessageHeader.MessageCommand.INV);
    }

    /**
     * Process an 'inv' message.
     *
     * @param       msg                     Message
     * @param       inBuffer                Input buffer
     * @param       msgListener             Message listener
     * @throws      EOFException            End-of-data while processing input stream
     * @throws      VerificationException   Verification failed
     */
    public static void processInventoryMessage(Message msg, SerializedBuffer inBuffer, MessageListener msgListener)
                                            throws EOFException, VerificationException {
        //
        // Get the number of inventory vectors (maximum of 1000 entries)
        //
        int count = inBuffer.getVarInt();
        if (count < 0 || count > 1000)
            throw new VerificationException("More than 1000 entries in 'inv' message",
                                            RejectMessage.REJECT_INVALID);
        //
        // Build the item list
        //
        List<InventoryItem> itemList = new ArrayList<>(count);
        for (int i=0; i<count; i++)
            itemList.add(new InventoryItem(inBuffer));
        //
        // Notify the message listener
        //
        msgListener.requestInventory(msg, itemList);
    }
}
