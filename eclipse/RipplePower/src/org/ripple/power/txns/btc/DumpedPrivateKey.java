package org.ripple.power.txns.btc;

import java.math.BigInteger;
import java.util.Arrays;

import org.ripple.power.Helper;

/**
 * The DumpedPrivateKey represents a private key in printable format as created
 * by the Bitcoin reference client. It can be imported into another wallet to
 * recreate the private key, public key and address.
 */
public class DumpedPrivateKey {

	/** Private key bytes (32 bytes) */
	private byte[] privKeyBytes;

	/** Compressed public key */
	private boolean isCompressed;

	/**
	 * Creates a DumpedPrivateKey from an existing private key
	 *
	 * @param privKey
	 *            Private key
	 * @param compressed
	 *            TRUE if the public key should be compressed
	 */
	public DumpedPrivateKey(BigInteger privKey, boolean compressed) {
		privKeyBytes = Helper.bigIntegerToBytes(privKey, 32);
		isCompressed = compressed;
	}

	/**
	 * Creates a DumpedPrivateKey from an encoded string
	 *
	 * @param string
	 *            Encoded private key
	 * @throws AddressFormatException
	 *             Encoded private key is not valid
	 */
	public DumpedPrivateKey(String string) throws AddressFormatException {
		//
		// Decode the private key
		//
		byte[] decodedKey = Base58.decodeChecked(string);
		int version = (int) decodedKey[0] & 0xff;
		if (version != NetParams.DUMPED_PRIVATE_KEY_VERSION)
			throw new AddressFormatException(String.format("Version %d is not correct", version));
		//
		// The private key length is 33 for a compressed public key, otherwise
		// it is 32
		//
		if (decodedKey.length == 33 + 1 && decodedKey[33] == (byte) 1) {
			isCompressed = true;
			privKeyBytes = Arrays.copyOfRange(decodedKey, 1, decodedKey.length - 1);
		} else if (decodedKey.length == 32 + 1) {
			isCompressed = false;
			privKeyBytes = Arrays.copyOfRange(decodedKey, 1, decodedKey.length);
		} else {
			throw new AddressFormatException("Private key length is incorrect");
		}
	}

	/**
	 * Returns an ECKey for this private key
	 *
	 * @return ECKey
	 */
	public ECKey getKey() {
		return new ECKey(new BigInteger(1, privKeyBytes), isCompressed);
	}

	/**
	 * Returns the Base58-encoded string with a 1-byte version and a 4-byte
	 * checksum.
	 *
	 * @return Base58-encoded string
	 */
	@Override
	public String toString() {
		//
		// The encoded private key has an extra byte appended if the public key
		// is compressed
		//
		byte[] keyBytes;
		if (isCompressed) {
			keyBytes = new byte[1 + 32 + 1 + 4];
			keyBytes[1 + 32] = (byte) 1;
		} else {
			keyBytes = new byte[1 + 32 + 4];
		}
		keyBytes[0] = (byte) NetParams.DUMPED_PRIVATE_KEY_VERSION;
		System.arraycopy(privKeyBytes, 0, keyBytes, 1, 32);
		byte[] digest = Helper.doubleDigest(keyBytes, 0, keyBytes.length - 4);
		System.arraycopy(digest, 0, keyBytes, keyBytes.length - 4, 4);
		return Base58.encode(keyBytes);
	}
}
