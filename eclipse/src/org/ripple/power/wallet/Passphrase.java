package org.ripple.power.wallet;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.ripple.power.CoinUtils;
import org.ripple.power.Helper;
import org.ripple.power.config.LSystem;
import org.ripple.power.utils.ByteUtils;

public class Passphrase {

	private static final String KEYGEN = "PBKDF2WithHmacSHA1";
	private static final String AES = "AES/CBC/NoPadding";
	private static final int AES_BLOCK_SIZE = 16;
	private static final int AES_KEY_LENGTH = 128;

	public static byte[] decrypt(Passphrase passphrase, byte[] encryptedData)
			throws Exception {
		if (encryptedData.length == 0) {
			return new byte[0];
		}
		byte[] byteHolder1;
		byte[] byteHolder2 = null;
		try {
			List<Cipher> ciphers = cipherList(passphrase.getPassphraseHash(),
					Cipher.DECRYPT_MODE);
			byteHolder1 = ciphers.get(2).doFinal(encryptedData);
			byteHolder2 = ciphers.get(1).doFinal(byteHolder1);
			byteHolder1 = ciphers.get(0).doFinal(byteHolder2);
			byteHolder2 = unPad4AES(byteHolder1);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return byteHolder2;
	}

	public static byte[] encrypt(Passphrase passphrase, byte[] unencryptedData)
			throws Exception {
		if (unencryptedData.length == 0) {
			return new byte[0];
		}
		byte[] byteHolder1;
		byte[] byteHolder2 = null;
		try {
			List<Cipher> ciphers = cipherList(passphrase.getPassphraseHash(),
					Cipher.ENCRYPT_MODE);
			byteHolder1 = pad4AES(unencryptedData);
			byteHolder2 = ciphers.get(0).doFinal(byteHolder1);
			byteHolder1 = ciphers.get(1).doFinal(byteHolder2);
			byteHolder2 = ciphers.get(2).doFinal(byteHolder1);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return byteHolder2;
	}

	private static byte[] unPad4AES(byte[] paddedUnencryptedData)
			throws BadPaddingException {
		if (paddedUnencryptedData.length == 0) {
			throw new BadPaddingException();
		}
		if ((paddedUnencryptedData.length % AES_BLOCK_SIZE) != 0) {
			throw new BadPaddingException();
		}
		byte lastByte = paddedUnencryptedData[paddedUnencryptedData.length - 1];
		if ((lastByte < 0) || (lastByte >= AES_BLOCK_SIZE)) {
			throw new BadPaddingException();
		}
		for (int i = 1; i <= (lastByte + AES_BLOCK_SIZE); i++) {
			byte aByte = paddedUnencryptedData[paddedUnencryptedData.length - i];
			if (aByte != lastByte) {
				throw new BadPaddingException();
			}
		}
		int unencryptedSize = paddedUnencryptedData.length - lastByte
				- AES_BLOCK_SIZE;
		byte[] unencryptedData = new byte[unencryptedSize];
		System.arraycopy(paddedUnencryptedData, 0, unencryptedData, 0,
				unencryptedSize);
		return unencryptedData;
	}

	private static byte[] pad4AES(byte[] unencryptedData) {
		int bytesToPad = AES_BLOCK_SIZE
				- (unencryptedData.length % AES_BLOCK_SIZE);
		byte bytesToPadValue = Integer.valueOf(bytesToPad).byteValue();
		int totalBytes = unencryptedData.length + bytesToPad + AES_BLOCK_SIZE;
		byte[] paddedUnencryptedData = new byte[totalBytes];
		System.arraycopy(unencryptedData, 0, paddedUnencryptedData, 0,
				unencryptedData.length);
		Arrays.fill(paddedUnencryptedData, unencryptedData.length, totalBytes,
				bytesToPadValue);
		return paddedUnencryptedData;
	}

	private static IvParameterSpec ivParameterSpec16(byte[] salt, int level)
			throws Exception {
		byte[] iv = { 1, 1, 30, 1, 99, 2, 90, 1, 0, 2, 13, 32, 20, 3, 1, 70 };
		iv[level] = salt[0];
		iv[level + 1] = salt[10 * level];
		iv[level + 3] = salt[16 * level];
		iv[level + 5] = (byte) (0xff & (salt[1 + level] ^ salt[7 * level]));
		iv[level + 7] = salt[17 * level];
		iv[level + 10] = salt[20 * level];
		iv[level + 11] = (byte) (0xff & (salt[13 * level] ^ salt[7 - level]));
		return new IvParameterSpec(iv);
	}

	private static List<Cipher> cipherList(byte[] passphraseBytes, int mode)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, InvalidAlgorithmParameterException, Exception {
		Cipher aes1 = Cipher.getInstance(AES);
		Cipher aes2 = Cipher.getInstance(AES);
		Cipher aes3 = Cipher.getInstance(AES);
		aes1.init(
				mode,
				keyGenAES(passphraseBytes, "cping@passsaltyN3SS&Wha9dste",
						18913), ivParameterSpec16(passphraseBytes, 1));
		aes2.init(
				mode,
				keyGenAES(passphraseBytes, "cping!wordsaltyN74G@337q0877",
						23944), ivParameterSpec16(passphraseBytes, 2));
		aes3.init(
				mode,
				keyGenAES(passphraseBytes, "cping#privatesaltyN9A9!14Ra1",
						19781), ivParameterSpec16(passphraseBytes, 3));
		return Arrays.asList(aes1, aes2, aes3);
	}

	private static SecretKey keyGenAES(byte[] passphraseBytes,
			String saltString, int iterations) throws Exception {
		SecretKey key = null;
		try {
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEYGEN);
			byte[] salt = saltString.getBytes();
			salt[1] = passphraseBytes[2];
			salt[2] = passphraseBytes[10];
			salt[3] = (byte) (0xff & (passphraseBytes[60] ^ passphraseBytes[50]));
			PBEKeySpec pbeKeySpec = new PBEKeySpec(ByteUtils.toHexString(
					passphraseBytes).toCharArray(), salt, iterations,
					AES_KEY_LENGTH);
			key = keyFactory.generateSecret(pbeKeySpec);
			key = new SecretKeySpec(key.getEncoded(), "AES");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return key;
	}

	private byte[] passphraseHash;

	private static final String STATIC_SALT = LSystem.getAppPassword();
	private static final int PASSWORD_ITERATIONS = 20011;
	public static final int MINIMUM_PASSPHRASE_LENGTH = 8;
	public static final int DEFAULT_CACHE_TIME_IN_SECONDS = 10 * 60;

	public Passphrase() {
		this.passphraseHash = null;
	}

	public Passphrase(String passphrase) throws Exception {
		this();
		this.setPassphrase(passphrase);
	}

	public void validatePassphrase(String passphrase) throws Exception {
		if (passphrase == null
				|| passphrase.length() < MINIMUM_PASSPHRASE_LENGTH) {
			throw new Exception();
		}
	}

	public final void setPassphrase(String passphrase) throws Exception {
		validatePassphrase(passphrase);
		try {
			this.passphraseHash = Helper.hash512(passphrase, STATIC_SALT,
					PASSWORD_ITERATIONS);
		} catch (Exception ex) {
			clear();
			throw ex;
		}
	}

	public byte[] getPassphraseHash() throws Exception {
		if (this.passphraseHash == null) {
			throw new Exception("passphraseHash == null");
		}
		return this.passphraseHash;
	}

	public void clear() {
		if (this.passphraseHash != null) {
			Arrays.fill(this.passphraseHash, (byte) 0b00000000);
		}
		this.passphraseHash = null;
	}

	public boolean isClear() {
		return this.passphraseHash == null;
	}

	public static String encodeToHex(String context, String password)
			throws Exception {
		Passphrase passphrase = new Passphrase(password);
		byte[] buffer = Passphrase.encrypt(passphrase,
				context.getBytes(LSystem.encoding));
		return CoinUtils.toHex(buffer);
	}

	public static String decodeToHex(String hash, String password)
			throws Exception {
		Passphrase passphrase = new Passphrase(password);
		byte[] buffer = Passphrase.decrypt(passphrase, CoinUtils.fromHex(hash));
		return new String(buffer, LSystem.encoding);
	}

	public static void main(String[] args) {
		try {
			String context = "中华民族万岁";
			String password = "dsdfuj5439gtfgfh";
			System.out.println(decodeToHex(encodeToHex(context, password),
					password));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
