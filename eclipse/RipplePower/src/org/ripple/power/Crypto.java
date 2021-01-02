package org.ripple.power;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

public final class Crypto {

	private static final ThreadLocal<SecureRandom> secureRandom = new ThreadLocal<SecureRandom>() {
		@Override
		protected SecureRandom initialValue() {
			return new SecureRandom();
		}
	};

	private Crypto() {
	} // never

	public static MessageDigest getMessageDigest(String algorithm) {
		try {
			return MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static MessageDigest sha256() {
		return getMessageDigest("SHA-256");
	}

	public static byte[] getPrivateKey(String secretPhrase) {
		byte[] s = Crypto.sha256().digest(Convert.toBytes(secretPhrase));
		Curve25519.clamp(s);
		return s;
	}

	public static void curve(byte[] Z, byte[] k, byte[] P) {
		Curve25519.curve(Z, k, P);
	}

	public static byte[] sign(byte[] message, String secretPhrase) {

		byte[] P = new byte[32];
		byte[] s = new byte[32];
		MessageDigest digest = Crypto.sha256();
		Curve25519.keygen(P, s, digest.digest(Convert.toBytes(secretPhrase)));

		byte[] m = digest.digest(message);

		digest.update(m);
		byte[] x = digest.digest(s);

		byte[] Y = new byte[32];
		Curve25519.keygen(Y, null, x);

		digest.update(m);
		byte[] h = digest.digest(Y);

		byte[] v = new byte[32];
		Curve25519.sign(v, h, x, s);

		byte[] signature = new byte[64];
		System.arraycopy(v, 0, signature, 0, 32);
		System.arraycopy(h, 0, signature, 32, 32);

		/*
		 * if (!Curve25519.isCanonicalSignature(signature)) { throw new
		 * RuntimeException("Signature not canonical"); }
		 */
		return signature;

	}

	public static boolean verify(byte[] signature, byte[] message, byte[] publicKey, boolean enforceCanonical) {

		if (enforceCanonical && !Curve25519.isCanonicalSignature(signature)) {
			return false;
		}

		if (enforceCanonical && !Curve25519.isCanonicalPublicKey(publicKey)) {
			return false;
		}

		byte[] Y = new byte[32];
		byte[] v = new byte[32];
		System.arraycopy(signature, 0, v, 0, 32);
		byte[] h = new byte[32];
		System.arraycopy(signature, 32, h, 0, 32);
		Curve25519.verify(Y, v, h, publicKey);

		MessageDigest digest = Crypto.sha256();
		byte[] m = digest.digest(message);
		digest.update(m);
		byte[] h2 = digest.digest(Y);

		return Arrays.equals(h, h2);
	}

	private static void xorProcess(byte[] data, int position, int length, byte[] myPrivateKey, byte[] theirPublicKey,
			byte[] nonce) {

		byte[] seed = new byte[32];
		Curve25519.curve(seed, myPrivateKey, theirPublicKey);
		for (int i = 0; i < 32; i++) {
			seed[i] ^= nonce[i];
		}

		MessageDigest sha256 = sha256();
		seed = sha256.digest(seed);

		for (int i = 0; i < length / 32; i++) {
			byte[] key = sha256.digest(seed);
			for (int j = 0; j < 32; j++) {
				data[position++] ^= key[j];
				seed[j] = (byte) (~seed[j]);
			}
			seed = sha256.digest(seed);
		}
		byte[] key = sha256.digest(seed);
		for (int i = 0; i < length % 32; i++) {
			data[position++] ^= key[i];
		}

	}

	public static byte[] xorEncrypt(byte[] data, int position, int length, byte[] myPrivateKey, byte[] theirPublicKey) {
		byte[] nonce = new byte[32];
		secureRandom.get().nextBytes(nonce); // cfb: May block as entropy is
												// being gathered, for example,
												// if they need to read from
												// /dev/random on various
												// unix-like operating systems
		xorProcess(data, position, length, myPrivateKey, theirPublicKey, nonce);
		return nonce;
	}

	public static void xorDecrypt(byte[] data, int position, int length, byte[] myPrivateKey, byte[] theirPublicKey,
			byte[] nonce) {
		xorProcess(data, position, length, myPrivateKey, theirPublicKey, nonce);
	}

	public static byte[] getSharedSecret(byte[] myPrivateKey, byte[] theirPublicKey) {
		try {
			byte[] sharedSecret = new byte[32];
			Curve25519.curve(sharedSecret, myPrivateKey, theirPublicKey);
			return sharedSecret;
		} catch (RuntimeException e) {
			throw e;
		}
	}

	public static String rsEncode(long id) {
		return ReedSolomon.encode(id);
	}

	public static long rsDecode(String rsString) {
		rsString = rsString.toUpperCase();
		try {
			long id = ReedSolomon.decode(rsString);
			if (!rsString.equals(ReedSolomon.encode(id))) {
				throw new RuntimeException(
						"ERROR: Reed-Solomon decoding of " + rsString + " not reversible, decoded to " + id);
			}
			return id;
		} catch (ReedSolomon.DecodeException e) {
			throw new RuntimeException(e.toString(), e);
		}
	}

}
