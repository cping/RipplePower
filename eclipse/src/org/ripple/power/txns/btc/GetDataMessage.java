package org.ripple.power.txns.btc;

import java.io.EOFException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>The 'getdata' message is used to request one or more blocks and transactions.
 * Blocks are returned as 'block' messages and transactions are returned as 'tx'
 * messages.  Any entries that are not found are returned as a 'notfound' response.</p>
 *
 * <p>GetData Message:</p>
 * <pre>
 *   Size       Field               Definition
 *   ====       =====               ==========
 *   VarInt     Count               Number of inventory items
 *   Variable   InvItems            One or more inventory items
 * </pre>
 */
public class GetDataMessage {

    /**
     * Create a 'getdata' message
     *
     * @param       peer            Destination peer
     * @param       invList         Inventory item list
     * @return                      'getdata' message
     */
    public static Message buildGetDataMessage(Peer peer, List<InventoryItem> invList) {
        SerializedBuffer msgBuffer = new SerializedBuffer(invList.size()*36+4);
        msgBuffer.putVarInt(invList.size());
        for(InventoryItem item:invList){
        	item.getBytes(msgBuffer);
        }
        //
        // Build the message
        //
        ByteBuffer buffer = MessageHeader.buildMessage("getdata", msgBuffer);
        return new Message(buffer, peer, MessageHeader.MessageCommand.GETDATA);
    }

    /**
     * Process a 'getdata' message
     *
     * @param       msg                     Message
     * @param       inBuffer                Input buffer
     * @param       msgListener             Message listener
     * @throws      EOFException            End-of-data while processing message data
     * @throws      VerificationException   Data verification failed
     */
    public static void processGetDataMessage(Message msg, SerializedBuffer inBuffer, MessageListener msgListener)
                                            throws EOFException, VerificationException {
        //
        // Build the request list
        //
        int count = inBuffer.getVarInt();
        if (count < 0 || count > 1000)
            throw new VerificationException("More than 1000 inventory items in 'getdata' message");
        List<InventoryItem> itemList = new ArrayList<>(count);
        for (int i=0; i<count; i++)
            itemList.add(new InventoryItem(inBuffer));
        //
        // Notify the message listener
        //
        msgListener.sendInventory(msg, itemList);
    }
}
