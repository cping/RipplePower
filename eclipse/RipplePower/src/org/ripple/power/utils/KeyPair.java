package org.ripple.power.utils;

import org.ripple.power.CoinUtils;

public class KeyPair {
	public final byte[] publicKey;
	public final String address;
	public final CoinUtils.PrivateKeyInfo privateKey;

	public KeyPair(CoinUtils.PrivateKeyInfo privateKeyInfo) {
		if (privateKeyInfo.privateKeyDecoded == null) {
			publicKey = null;
			address = null;
		} else {
			publicKey = CoinUtils.generatePublicKey(
					privateKeyInfo.privateKeyDecoded,
					privateKeyInfo.isPublicKeyCompressed);
			address = CoinUtils.publicKeyToAddress(publicKey);
		}
		privateKey = privateKeyInfo;
	}

	public KeyPair(String address, byte[] publicKey,
			CoinUtils.PrivateKeyInfo privateKey) {
		this.publicKey = publicKey;
		this.address = address;
		this.privateKey = privateKey;
	}
}
