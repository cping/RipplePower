package org.ripple.power;

import java.math.BigInteger;

import org.ripple.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.ripple.bouncycastle.math.ec.ECPoint;
import org.ripple.power.config.LSystem;

public class RipplePrivateKey extends RippleIdentifier {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	boolean isDeterministic = false;

	RipplePublicKey publicKey;

	public RipplePrivateKey(byte[] privateKeyBytes) {
		super(privateKeyBytes, 34);
		if (privateKeyBytes.length != 32) {
			throw new RuntimeException("The private key must be of length 32 bytes");
		}
	}

	public static byte[] bigIntegerToBytes(BigInteger biToConvert, int nbBytesToReturn) {
		byte[] twosComplement = biToConvert.toByteArray();
		byte[] bytesToReturn = new byte[nbBytesToReturn];

		if ((biToConvert.bitLength() + 7) / 8 != twosComplement.length) {
			byte[] twosComplementWithoutSign = new byte[twosComplement.length - 1];
			System.arraycopy(twosComplement, 1, twosComplementWithoutSign, 0, twosComplementWithoutSign.length);
			twosComplement = twosComplementWithoutSign;
		}

		int nbBytesOfPaddingRequired = nbBytesToReturn - twosComplement.length;
		if (nbBytesOfPaddingRequired < 0) {
			throw new RuntimeException("nbBytesToReturn " + nbBytesToReturn + " is too small");
		}
		System.arraycopy(twosComplement, 0, bytesToReturn, nbBytesOfPaddingRequired, twosComplement.length);

		return bytesToReturn;
	}

	public RipplePrivateKey(BigInteger privateKeyForAccount) {
		super(bigIntegerToBytes(privateKeyForAccount, 32), 34);
	}

	public RipplePublicKey getPublicKey() {
		if (publicKey != null) {
			return publicKey;
		}
		BigInteger privateBI = new BigInteger(1, this.payloadBytes);
		ECPoint uncompressed = RippleGenerator.SECP256K1_PARAMS.getG().multiply(privateBI);

		ECPoint publicPoint = CoinUtils.Fp(RippleGenerator.SECP256K1_PARAMS.getCurve(), uncompressed);
		publicKey = new RipplePublicKey(publicPoint.getEncoded(LSystem.ENCODE));
		return publicKey;
	}

	public ECPrivateKeyParameters getECPrivateKey() {
		BigInteger privateBI = new BigInteger(1, this.payloadBytes);
		ECPrivateKeyParameters privKey = new ECPrivateKeyParameters(privateBI, RippleGenerator.SECP256K1_PARAMS);
		return privKey;
	}
}
