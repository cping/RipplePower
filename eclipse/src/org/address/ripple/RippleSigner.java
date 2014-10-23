package org.address.ripple;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;

import org.address.ripple.RippleSchemas.BinaryFormatField;
import org.spongycastle.asn1.ASN1InputStream;
import org.spongycastle.asn1.DERInteger;
import org.spongycastle.asn1.DERSequenceGenerator;
import org.spongycastle.asn1.DLSequence;
import org.spongycastle.crypto.params.ECPrivateKeyParameters;
import org.spongycastle.crypto.params.ECPublicKeyParameters;
import org.spongycastle.crypto.signers.ECDSASigner;
import org.spongycastle.math.ec.ECPoint;

public class RippleSigner {
	RipplePrivateKey privateKey;

	public RippleSigner(RipplePrivateKey privateKey) {
		this.privateKey = privateKey;
	}

	public RippleObject sign(RippleObject serObjToSign) throws Exception {
		if (serObjToSign.getField(BinaryFormatField.TxnSignature) != null) {
			throw new Exception("Object already signed");
		}
		RippleObject signedRBO = new RippleObject(serObjToSign);
		signedRBO.putField(BinaryFormatField.SigningPubKey, privateKey
				.getPublicKey().getPublicPoint().getEncoded());

		byte[] hashOfRBOBytes = signedRBO.generateHashFromBinaryObject();
		ECDSASignature signature = signHash(hashOfRBOBytes);
		signedRBO.putField(BinaryFormatField.TxnSignature,
				signature.encodeToDER());
		return signedRBO;
	}

	private ECDSASignature signHash(byte[] hashOfBytes) throws Exception {
		if (hashOfBytes.length != 32) {
			throw new RuntimeException("can sign only a hash of 32 bytes");
		}
		ECDSASigner signer = new ECDSASigner();
		ECPrivateKeyParameters privKey = privateKey.getECPrivateKey();
		signer.init(true, privKey);
		BigInteger[] RandS = signer.generateSignature(hashOfBytes);
		return new ECDSASignature(RandS[0], RandS[1], privateKey.getPublicKey()
				.getPublicPoint());
	}

	public boolean isSignatureVerified(RippleObject serObj) {
		try {
			byte[] signatureBytes = (byte[]) serObj
					.getField(BinaryFormatField.TxnSignature);
			if (signatureBytes == null) {
				throw new RuntimeException("The specified  has no signature");
			}
			byte[] signingPubKeyBytes = (byte[]) serObj
					.getField(BinaryFormatField.SigningPubKey);
			if (signingPubKeyBytes == null) {
				throw new RuntimeException(
						"The specified  has no public key associated to the signature");
			}

			RippleObject unsignedRBO = serObj.getUnsignedCopy();
			byte[] hashToVerify = unsignedRBO.generateHashFromBinaryObject();

			ECDSASigner signer = new ECDSASigner();
			ECDSASignature signature = new ECDSASignature(signatureBytes,
					signingPubKeyBytes);
			signer.init(false, new ECPublicKeyParameters(
					signature.publicSigningKey,
					RippleGenerator.SECP256K1_PARAMS));
			return signer.verifySignature(hashToVerify, signature.r,
					signature.s);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static class ECDSASignature {
		public BigInteger r, s;
		private ECPoint publicSigningKey;

		public ECDSASignature(BigInteger r, BigInteger s,
				ECPoint publicSigningKey) {
			this.r = r;
			this.s = s;
			this.publicSigningKey = publicSigningKey;
		}

		public ECDSASignature(byte[] signatureDEREncodedBytes,
				byte[] signingPubKey) throws IOException {
			publicSigningKey = RippleGenerator.SECP256K1_PARAMS.getCurve()
					.decodePoint(signingPubKey);

			ASN1InputStream decoder = new ASN1InputStream(
					signatureDEREncodedBytes);
			DLSequence seq = (DLSequence) decoder.readObject();
			DERInteger r = (DERInteger) seq.getObjectAt(0);
			DERInteger s = (DERInteger) seq.getObjectAt(1);

			this.r = r.getPositiveValue();
			this.s = s.getPositiveValue();
			decoder.close();
		}

		@SuppressWarnings("deprecation")
		public byte[] encodeToDER() {
			try {
				ByteArrayOutputStream bos = new ByteArrayOutputStream(72);
				DERSequenceGenerator seq = new DERSequenceGenerator(bos);
				seq.addObject(new DERInteger(r));
				seq.addObject(new DERInteger(s));
				seq.close();
				return bos.toByteArray();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

	}
}
