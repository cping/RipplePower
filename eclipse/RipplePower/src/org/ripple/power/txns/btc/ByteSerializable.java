package org.ripple.power.txns.btc;

/**
 * An object supporting the ByteSerializable interface provides the getBytes()
 * method to serialize the object.
 */
public interface ByteSerializable {

	/**
	 * Return a serialized byte array
	 *
	 * @return Serialized byte array
	 */
	public byte[] getBytes();

	/**
	 * Write the object to a serialized buffer
	 *
	 * @param buffer
	 *            Serialized buffer
	 * @return Serialized buffer
	 */
	public SerializedBuffer getBytes(SerializedBuffer buffer);
}
