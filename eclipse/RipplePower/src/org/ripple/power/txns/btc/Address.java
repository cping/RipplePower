package org.ripple.power.txns.btc;

import java.util.Arrays;

import org.ripple.power.Helper;

/**
 * An address is a 20-byte Hash160 of a public key. The displayable form is
 * Base-58 encoded with a 1-byte version and a 4-byte checksum.
 */
public class Address {

	/** Address label */
	private String label;

	/** Address hash */
	private byte[] hash;

	/**
	 * Creates a new Address with a zero public key hash
	 */
	public Address() {
		this(new byte[20]);
	}

	/**
	 * Creates a new Address using the 20-byte public key hash
	 *
	 * @param hash
	 *            Public key hash
	 */
	public Address(byte[] hash) {
		this(hash, "");
	}

	/**
	 * Creates a new Address using the 20-byte public key hash and a label
	 *
	 * @param hash
	 *            Public key hash
	 * @param label
	 *            Address label
	 */
	public Address(byte[] hash, String label) {
		this.hash = hash;
		this.label = label;
	}

	/**
	 * Creates a new Address using a Base-58 encoded address
	 *
	 * @param address
	 *            Encoded address
	 * @throws AddressFormatException
	 *             Address string is not a valid address
	 */
	public Address(String address) throws AddressFormatException {
		this(address, "");
	}

	/**
	 * Creates a new Address using a Base-58 encoded address and a label
	 *
	 * @param address
	 *            Encoded address
	 * @param label
	 *            Address label
	 * @throws AddressFormatException
	 *             Address string is not valid
	 */
	public Address(String address, String label) throws AddressFormatException {
		//
		// Set the label
		//
		this.label = label;
		//
		// Decode the address
		//
		byte[] decoded = Base58.decodeChecked(address);
		int version = (int) decoded[0] & 0xff;
		if (version != NetParams.ADDRESS_VERSION)
			throw new AddressFormatException(String.format("Address version %d is not correct", version));
		//
		// The address must be 20 bytes
		//
		if (decoded.length != 20 + 1)
			throw new AddressFormatException("Address length is not 20 bytes");
		//
		// Get the public key hash
		//
		hash = Arrays.copyOfRange(decoded, 1, decoded.length);
	}

	/**
	 * Returns the address label
	 *
	 * @return Address label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets the address label
	 *
	 * @param label
	 *            Address label
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Returns the address hash
	 *
	 * @return 20-byte address hash
	 */
	public byte[] getHash() {
		return hash;
	}

	/**
	 * Returns the address as a Base58-encoded string with a 1-byte version and
	 * a 4-byte checksum
	 *
	 * @return Encoded string
	 */
	@Override
	public String toString() {
		byte[] addressBytes = new byte[1 + hash.length + 4];
		addressBytes[0] = (byte) NetParams.ADDRESS_VERSION;
		System.arraycopy(hash, 0, addressBytes, 1, hash.length);
		byte[] digest = Helper.doubleDigest(addressBytes, 0, hash.length + 1);
		System.arraycopy(digest, 0, addressBytes, hash.length + 1, 4);
		return Base58.encode(addressBytes);
	}

	/**
	 * Returns the hash code for this object
	 *
	 * @return Hash code
	 */
	@Override
	public int hashCode() {
		return Arrays.hashCode(hash);
	}

	/**
	 * Checks if two objects are equal
	 *
	 * @param obj
	 *            The object to check
	 * @return TRUE if the objects are equal
	 */
	@Override
	public boolean equals(Object obj) {
		return (obj != null && (obj instanceof Address) && Arrays.equals(hash, ((Address) obj).hash));
	}
}
