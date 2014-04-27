package org.ripple.power.wallet;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.ripple.power.config.LSystem;

public class WalletCryptos {

	public static class DecryptException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public DecryptException(Throwable trowable) {
			super(trowable);
		}

		public DecryptException() {
			super();
		}
	}

	public static class EncryptException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public EncryptException(Throwable trowable) {
			super(trowable);
		}

		public EncryptException() {
			super();
		}
	}

	public static byte[] encrypt(String passphrase, byte[] plaintext)
			throws EncryptException {
		try {
			SecretKeySpec key = getKey(passphrase);
			return encrypt(key, plaintext);
		} catch (NoSuchAlgorithmException t) {
			throw new EncryptException(t);
		} catch (InvalidKeySpecException t) {
			throw new EncryptException(t);
		} catch (InvalidKeyException t) {
			throw new EncryptException(t);
		} catch (NoSuchPaddingException t) {
			throw new EncryptException(t);
		} catch (IllegalBlockSizeException t) {
			throw new EncryptException(t);
		} catch (BadPaddingException t) {
			throw new EncryptException(t);
		} catch (UnsupportedEncodingException t) {
			throw new EncryptException(t);
		}
	}

	public static byte[] decrypt(String passphrase, byte[] ciphertext)
			throws DecryptException {
		try {
			SecretKeySpec key = getKey(passphrase);
			return decrypt(key, ciphertext);
		} catch (NoSuchAlgorithmException t) {
			throw new DecryptException(t);
		} catch (InvalidKeyException t) {
			throw new DecryptException(t);
		} catch (IllegalBlockSizeException t) {
			throw new DecryptException(t);
		} catch (BadPaddingException t) {
			throw new DecryptException(t);
		} catch (NoSuchPaddingException t) {
			throw new DecryptException(t);
		} catch (InvalidKeySpecException t) {
			throw new DecryptException(t);
		} catch (UnsupportedEncodingException t) {
			throw new DecryptException(t);
		}
	}

	private static SecretKeySpec getKey(String passphrase)
			throws NoSuchAlgorithmException, InvalidKeySpecException,
			UnsupportedEncodingException {
		byte[] salt = makeSHA1Hash(
				passphrase + "my name cping is very cool !RE%$%$67opop00943% "
						+ passphrase + " ").getBytes(LSystem.encoding);
		int iterations = 10000;
		SecretKeyFactory factory = SecretKeyFactory
				.getInstance("PBKDF2WithHmacSHA1");
		SecretKey tmp = factory.generateSecret(new PBEKeySpec(passphrase
				.toCharArray(), salt, iterations, 128));
		return new SecretKeySpec(tmp.getEncoded(), "AES");
	}

	private static byte[] encrypt(SecretKeySpec key, byte[] plaintext)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, UnsupportedEncodingException {
		Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
		aes.init(Cipher.ENCRYPT_MODE, key);
		return aes.doFinal(plaintext);
	}

	private static byte[] decrypt(SecretKeySpec key, byte[] ciphertext)
			throws IllegalBlockSizeException, BadPaddingException,
			InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException {
		Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
		aes.init(Cipher.DECRYPT_MODE, key);
		return aes.doFinal(ciphertext);
	}

	public static String makeSHA1Hash(String input)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance("SHA1");
		md.reset();
		byte[] buffer = input.getBytes(LSystem.encoding);
		md.update(buffer);
		byte[] digest = md.digest();
		String hexStr = "";
		for (int i = 0; i < digest.length; i++) {
			hexStr += Integer.toString((digest[i] & 0xff) + 0x100, 16)
					.substring(1);
		}
		return hexStr;
	}

}