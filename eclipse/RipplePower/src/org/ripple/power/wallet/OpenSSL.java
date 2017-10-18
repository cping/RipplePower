package org.ripple.power.wallet;

import java.security.SecureRandom;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;
import org.ripple.bouncycastle.crypto.BufferedBlockCipher;
import org.ripple.bouncycastle.crypto.CipherParameters;
import org.ripple.bouncycastle.crypto.PBEParametersGenerator;
import org.ripple.bouncycastle.crypto.engines.AESFastEngine;
import org.ripple.bouncycastle.crypto.generators.OpenSSLPBEParametersGenerator;
import org.ripple.bouncycastle.crypto.modes.CBCBlockCipher;
import org.ripple.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.ripple.bouncycastle.crypto.params.ParametersWithIV;
import org.ripple.power.CoinUtils;
import org.ripple.power.config.LSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenSSL {
	private Logger log = LoggerFactory.getLogger(OpenSSL.class);

	private static final int NUMBER_OF_ITERATIONS = 1024;

	private static final int KEY_LENGTH = 256;

	private static final int IV_LENGTH = 128;

	private static final int SALT_LENGTH = 8;

	public static final String OPENSSL_SALTED_TEXT = "Salted__";

	public byte[] openSSLSaltedBytes;

	private String openSSLMagicText = null;

	public static final int NUMBER_OF_CHARACTERS_TO_MATCH_IN_OPENSSL_MAGIC_TEXT = 10;

	private static SecureRandom secureRandom = new SecureRandom();

	public OpenSSL() {
		try {
			openSSLSaltedBytes = OPENSSL_SALTED_TEXT.getBytes(LSystem.encoding);

			openSSLMagicText = Base64.encodeBase64String(OpenSSL.OPENSSL_SALTED_TEXT.getBytes(LSystem.encoding))
					.substring(0, OpenSSL.NUMBER_OF_CHARACTERS_TO_MATCH_IN_OPENSSL_MAGIC_TEXT);

		} catch (UnsupportedEncodingException e) {
			log.error("Could not construct EncrypterDecrypter", e.getMessage());
		}
	}

	private CipherParameters getAESPasswordKey(CharSequence password, byte[] salt) throws Exception {
		try {
			PBEParametersGenerator generator = new OpenSSLPBEParametersGenerator();
			generator.init(PBEParametersGenerator.PKCS5PasswordToBytes(convertToCharArray(password)), salt,
					NUMBER_OF_ITERATIONS);

			ParametersWithIV key = (ParametersWithIV) generator.generateDerivedParameters(KEY_LENGTH, IV_LENGTH);

			return key;
		} catch (Exception e) {
			throw new Exception("Could not generate key from password of length " + password.length() + " and salt '"
					+ CoinUtils.toHex(salt), e);
		}
	}

	public String encrypt(String plainText, CharSequence password) throws Exception {
		try {
			byte[] plainTextAsBytes;
			if (plainText == null) {
				plainTextAsBytes = new byte[0];
			} else {
				plainTextAsBytes = plainText.getBytes(LSystem.encoding);
			}

			byte[] encryptedBytes = encrypt(plainTextAsBytes, password);

			byte[] encryptedBytesPlusSaltedText = concat(openSSLSaltedBytes, encryptedBytes);

			return Base64.encodeBase64String(encryptedBytesPlusSaltedText);
		} catch (Exception e) {
			throw new Exception("Could not encrypt string '" + plainText + "'", e);
		}
	}

	public byte[] encrypt(byte[] plainTextAsBytes, CharSequence password) throws Exception {
		try {
			byte[] salt = new byte[SALT_LENGTH];
			secureRandom.nextBytes(salt);

			ParametersWithIV key = (ParametersWithIV) getAESPasswordKey(password, salt);

			BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESFastEngine()));
			cipher.init(true, key);
			byte[] encryptedBytes = new byte[cipher.getOutputSize(plainTextAsBytes.length)];
			int length = cipher.processBytes(plainTextAsBytes, 0, plainTextAsBytes.length, encryptedBytes, 0);

			cipher.doFinal(encryptedBytes, length);

			return concat(salt, encryptedBytes);
		} catch (Exception e) {
			throw new Exception("Could not encrypt bytes '" + CoinUtils.toHex(plainTextAsBytes) + "'", e);
		}
	}

	public String decrypt(String textToDecode, CharSequence password) throws Exception {
		try {
			final byte[] decodeTextAsBytes = Base64.decodeBase64(textToDecode.getBytes(LSystem.encoding));

			int saltPrefixTextLength = openSSLSaltedBytes.length;

			byte[] cipherBytes = new byte[decodeTextAsBytes.length - saltPrefixTextLength];
			System.arraycopy(decodeTextAsBytes, saltPrefixTextLength, cipherBytes, 0,
					decodeTextAsBytes.length - saltPrefixTextLength);

			byte[] decryptedBytes = decrypt(cipherBytes, password);

			return new String(decryptedBytes, LSystem.encoding).trim();
		} catch (Exception e) {
			throw new Exception("Could not decrypt input string", e);
		}
	}

	public byte[] decrypt(byte[] bytesToDecode, CharSequence password) throws Exception {
		try {

			byte[] salt = new byte[SALT_LENGTH];

			System.arraycopy(bytesToDecode, 0, salt, 0, SALT_LENGTH);

			byte[] cipherBytes = new byte[bytesToDecode.length - SALT_LENGTH];
			System.arraycopy(bytesToDecode, SALT_LENGTH, cipherBytes, 0, bytesToDecode.length - SALT_LENGTH);

			ParametersWithIV key = (ParametersWithIV) getAESPasswordKey(password, salt);

			BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESFastEngine()));
			cipher.init(false, key);

			byte[] decryptedBytes = new byte[cipher.getOutputSize(cipherBytes.length)];
			int length = cipher.processBytes(cipherBytes, 0, cipherBytes.length, decryptedBytes, 0);

			cipher.doFinal(decryptedBytes, length);

			return decryptedBytes;
		} catch (Exception e) {
			throw new Exception("Could not decrypt input string", e);
		}
	}

	private byte[] concat(byte[] arrayA, byte[] arrayB) {
		byte[] result = new byte[arrayA.length + arrayB.length];
		System.arraycopy(arrayA, 0, result, 0, arrayA.length);
		System.arraycopy(arrayB, 0, result, arrayA.length, arrayB.length);

		return result;
	}

	private char[] convertToCharArray(CharSequence charSequence) {
		if (charSequence == null) {
			return null;
		}

		char[] charArray = new char[charSequence.length()];
		for (int i = 0; i < charSequence.length(); i++) {
			charArray[i] = charSequence.charAt(i);
		}
		return charArray;
	}

	public byte[] getOpenSSLSaltedBytes() {
		return openSSLSaltedBytes;
	}

	public String getOpenSSLMagicText() {
		return openSSLMagicText;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(openSSLSaltedBytes);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof OpenSSL)) {
			return false;
		}
		return true;
	}
}
