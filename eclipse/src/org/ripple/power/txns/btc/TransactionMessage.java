package org.ripple.power.txns.btc;

import java.io.EOFException;
import java.nio.ByteBuffer;

/**
 * <p>The 'tx' message contains a transaction which is not yet in a block.  The transaction
 * will be held in the memory pool for a period of time to allow other peers to request
 * the transaction.</p>
 *
 * <p>Transaction Message</p>
 * <pre>
 *   Size           Field               Description
 *   ====           =====               ===========
 *   4 bytes        Version             Transaction version
 *   VarInt         InputCount          Number of inputs
 *   Variable       InputList           Inputs
 *   VarInt         OutputCount         Number of outputs
 *   Variable       OutputList          Outputs
 *   4 bytes        LockTime            Transaction lock time
 * </pre>
 */
public class TransactionMessage {

    /**
     * Build a 'tx' message
     *
     * @param       peer                    The destination peer or null for a broadcast message
     * @param       tx                      Transaction to be sent
     * @return                              'tx' message
     */
    public static Message buildTransactionMessage(Peer peer, Transaction tx) {
        ByteBuffer buffer = MessageHeader.buildMessage("tx", tx.getBytes());
        return new Message(buffer, peer, MessageHeader.MessageCommand.TX);
    }

    /**
     * Build a 'tx' message
     *
     * @param       peer                    The destination peer or null for a broadcast message
     * @param       txData                  Serialized transaction
     * @return                              'tx' message
     */
    public static Message buildTransactionMessage(Peer peer, byte[] txData) {
        ByteBuffer buffer = MessageHeader.buildMessage("tx", txData);
        return new Message(buffer, peer, MessageHeader.MessageCommand.TX);
    }

    /**
     * Processes a 'tx' message
     *
     * @param       msg                     Message
     * @param       inBuffer                Input buffer
     * @param       msgListener             Message listener
     * @throws      EOFException            Serialized data is too short
     * @throws      VerificationException   Transaction verification failed
     */
    public static void processTransactionMessage(Message msg, SerializedBuffer inBuffer, MessageListener msgListener)
                                            throws EOFException, VerificationException {
        //
        // Get the transaction
        //
        Transaction tx = new Transaction(inBuffer);
        //
        // Notify the message listener that a transaction is ready for processing
        //
        msgListener.processTransaction(msg, tx);
    }
}
