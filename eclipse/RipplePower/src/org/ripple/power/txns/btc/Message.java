package org.ripple.power.txns.btc;

import java.nio.ByteBuffer;

/**
 * A message is associated with a peer node and is sent or received using the
 * socket channel assigned to that peer.
 */
public class Message {

	/** The message buffer */
	private ByteBuffer buffer;

	/** The message restart buffer */
	private ByteBuffer restartBuffer;

	/** The associated peer */
	private Peer peer;

	/** The message command */
	private MessageHeader.MessageCommand command;

	/** Inventory message type */
	private int invType;

	/** Deferred restart index */
	private int restartIndex;

	/**
	 * Creates an empty message for use by subclasses
	 */
	protected Message() {
	}

	/**
	 * Creates a new message
	 *
	 * @param buffer
	 *            Message buffer
	 * @param peer
	 *            Associated peer or null for a broadcast message
	 * @param cmd
	 *            Message command
	 */
	public Message(ByteBuffer buffer, Peer peer, MessageHeader.MessageCommand cmd) {
		this.buffer = buffer;
		this.peer = peer;
		this.command = cmd;
	}

	/**
	 * Returns the peer associated with this message or null if this is a
	 * broadcast message
	 *
	 * @return Peer
	 */
	public Peer getPeer() {
		return peer;
	}

	/**
	 * Sets the peer associated with this message
	 *
	 * @param peer
	 *            Associated peer
	 */
	public void setPeer(Peer peer) {
		this.peer = peer;
	}

	/**
	 * Returns the message buffer
	 *
	 * @return Message buffer
	 */
	public ByteBuffer getBuffer() {
		return buffer;
	}

	/**
	 * Sets the message buffer
	 *
	 * @param buffer
	 *            Message buffer
	 */
	public void setBuffer(ByteBuffer buffer) {
		this.buffer = buffer;
	}

	/**
	 * Returns the message restart buffer
	 *
	 * @return Restart buffer
	 */
	public ByteBuffer getRestartBuffer() {
		return restartBuffer;
	}

	/**
	 * Sets the message restart buffer
	 *
	 * @param buffer
	 *            Restart buffer
	 */
	public void setRestartBuffer(ByteBuffer buffer) {
		restartBuffer = buffer;
	}

	/**
	 * Returns the message command
	 *
	 * @return Message command
	 */
	public MessageHeader.MessageCommand getCommand() {
		return command;
	}

	/**
	 * Sets the message command
	 *
	 * @param cmd
	 *            Message command
	 */
	public void setCommand(MessageHeader.MessageCommand cmd) {
		command = cmd;
	}

	/**
	 * Returns the deferred restart index
	 *
	 * @return Restart index
	 */
	public int getRestartIndex() {
		return restartIndex;
	}

	/**
	 * Set the deferred restart index
	 *
	 * @param restartIndex
	 *            Restart index
	 */
	public void setRestartIndex(int restartIndex) {
		this.restartIndex = restartIndex;
	}

	/**
	 * Return the inventory type
	 *
	 * @return Inventory type
	 */
	public int getInventoryType() {
		return invType;
	}

	/**
	 * Set the inventory type
	 *
	 * @param invType
	 *            Inventory type
	 */
	public void setInventoryType(int invType) {
		this.invType = invType;
	}

	/**
	 * Creates a copy of this message
	 *
	 * A new ByteBuffer is created using the same byte array. This allows
	 * multiple output channels to process the message at the same time.
	 *
	 * @param peer
	 *            Target peer
	 * @return Message clone
	 */
	public Message clone(Peer peer) {
		ByteBuffer newBuffer = (buffer != null ? ByteBuffer.wrap(buffer.array()) : null);
		return new Message(newBuffer, peer, command);
	}
}
