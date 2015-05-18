package org.ripple.power;

import java.math.BigInteger;
import java.nio.ByteBuffer;

import org.ripple.power.utils.CollectionUtils;
import org.spongycastle.asn1.sec.SECNamedCurves;
import org.spongycastle.asn1.x9.X9ECParameters;
import org.spongycastle.crypto.params.ECDomainParameters;
import org.spongycastle.math.ec.ECPoint;

public class RippleGenerator {
	public static ECDomainParameters SECP256K1_PARAMS;
	protected byte[] seedBytes;

	static {

		X9ECParameters params = SECNamedCurves.getByName("secp256k1");
		SECP256K1_PARAMS = new ECDomainParameters(params.getCurve(),
				params.getG(), params.getN(), params.getH());
	}

	public RippleGenerator(RippleSeedAddress secret) {
		this(secret.getBytes());
	}

	public RippleGenerator(byte[] bytesSeed) {
		if (bytesSeed.length != 16) {
			throw new RuntimeException("The seed size should be 128 bit, was "
					+ bytesSeed.length * 8);
		}
		this.seedBytes = CollectionUtils.copyOf(bytesSeed);
	}

	protected byte[] getPrivateRootKeyBytes() {
		for (int seq = 0;; seq++) {
			byte[] seqBytes = ByteBuffer.allocate(4).putInt(seq).array();
			byte[] seedAndSeqBytes = Helper.concatenate(seedBytes, seqBytes);
			byte[] privateGeneratorBytes = Helper.halfSHA512(seedAndSeqBytes);
			BigInteger privateGeneratorBI = new BigInteger(1,
					privateGeneratorBytes);

			if (privateGeneratorBI.compareTo(SECP256K1_PARAMS.getN()) == -1) {
				return privateGeneratorBytes;
			}
		}
	}

	protected ECPoint getPublicGeneratorPoint() {
		byte[] privateGeneratorBytes = getPrivateRootKeyBytes();
		ECPoint publicGenerator = new RipplePrivateKey(privateGeneratorBytes)
				.getPublicKey().getPublicPoint();
		return publicGenerator;
	}

	public RipplePrivateKey getAccountPrivateKey(int accountNumber) {
		BigInteger privateRootKeyBI = new BigInteger(1,
				getPrivateRootKeyBytes());
		ECPoint publicGeneratorPoint = getPublicGeneratorPoint();
		byte[] publicGeneratorBytes = publicGeneratorPoint.getEncoded();
		byte[] accountNumberBytes = ByteBuffer.allocate(4)
				.putInt(accountNumber).array();
		BigInteger pubGenSeqSubSeqHashBI;
		for (int subSequence = 0;; subSequence++) {
			byte[] subSequenceBytes = ByteBuffer.allocate(4)
					.putInt(subSequence).array();
			byte[] pubGenAccountSubSeqBytes = Helper.concatenate(
					publicGeneratorBytes, accountNumberBytes, subSequenceBytes);
			byte[] publicGeneratorAccountSeqHashBytes = Helper
					.halfSHA512(pubGenAccountSubSeqBytes);

			pubGenSeqSubSeqHashBI = new BigInteger(1,
					publicGeneratorAccountSeqHashBytes);
			if (pubGenSeqSubSeqHashBI.compareTo(SECP256K1_PARAMS.getN()) == -1
					&& !pubGenSeqSubSeqHashBI.equals(BigInteger.ZERO)) {

				break;
			}
		}
		BigInteger privateKeyForAccount = privateRootKeyBI.add(
				pubGenSeqSubSeqHashBI).mod(SECP256K1_PARAMS.getN());
		return new RipplePrivateKey(privateKeyForAccount);
	}

	public RipplePublicKey getAccountPublicKey(int accountNumber) {
		ECPoint publicGeneratorPoint = getPublicGeneratorPoint();
		byte[] publicGeneratorBytes = publicGeneratorPoint.getEncoded();
		byte[] accountNumberBytes = ByteBuffer.allocate(4)
				.putInt(accountNumber).array();
		byte[] publicGeneratorAccountSeqHashBytes;
		for (int subSequence = 0;; subSequence++) {
			byte[] subSequenceBytes = ByteBuffer.allocate(4)
					.putInt(subSequence).array();
			byte[] pubGenAccountSubSeqBytes = Helper.concatenate(
					publicGeneratorBytes, accountNumberBytes, subSequenceBytes);
			publicGeneratorAccountSeqHashBytes = Helper
					.halfSHA512(pubGenAccountSubSeqBytes);
			BigInteger pubGenSeqSubSeqHashBI = new BigInteger(1,
					publicGeneratorAccountSeqHashBytes);
			if (pubGenSeqSubSeqHashBI.compareTo(SECP256K1_PARAMS.getN()) == -1) {
				break;
			}
		}
		ECPoint temporaryPublicPoint = new RipplePrivateKey(
				publicGeneratorAccountSeqHashBytes).getPublicKey()
				.getPublicPoint();
		ECPoint accountPublicKeyPoint = publicGeneratorPoint
				.add(temporaryPublicPoint);
		byte[] publicKeyBytes = accountPublicKeyPoint.getEncoded();
		return new RipplePublicKey(publicKeyBytes);
	}

	public RipplePublicGeneratorAddress getPublicGeneratorFamily()
			throws Exception {
		byte[] publicGeneratorBytes = getPublicGeneratorPoint().getEncoded();
		return new RipplePublicGeneratorAddress(publicGeneratorBytes);
	}
}
