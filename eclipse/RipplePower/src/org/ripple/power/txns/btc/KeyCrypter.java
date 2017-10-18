package org.ripple.power.txns.btc;

import java.io.Serializable;

import org.ripple.bouncycastle.crypto.params.KeyParameter;

public interface KeyCrypter extends Serializable {

	/**
	 * Create a KeyParameter (which typically contains an AES key)
	 * 
	 * @param password
	 * @return KeyParameter The KeyParameter which typically contains the AES
	 *         key to use for encrypting and decrypting
	 * @throws KeyCrypterException
	 */
	public KeyParameter deriveKey(CharSequence password) throws KeyCrypterException;

	/**
	 * Decrypt the provided encrypted bytes, converting them into unencrypted
	 * bytes.
	 *
	 * @throws KeyCrypterException
	 *             if decryption was unsuccessful.
	 */
	public byte[] decrypt(EncryptedPrivateKey encryptedBytesToDecode, KeyParameter aesKey) throws KeyCrypterException;

	/**
	 * Encrypt the supplied bytes, converting them into ciphertext.
	 *
	 * @return encryptedPrivateKey An encryptedPrivateKey containing the
	 *         encrypted bytes and an initialisation vector.
	 * @throws KeyCrypterException
	 *             if encryption was unsuccessful
	 */
	public EncryptedPrivateKey encrypt(byte[] plainBytes, KeyParameter aesKey) throws KeyCrypterException;
}
