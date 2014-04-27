package org.address.ripple;

import java.math.BigInteger;

import org.spongycastle.crypto.params.ECPrivateKeyParameters;
import org.spongycastle.math.ec.ECPoint;

public class RipplePrivateKey extends RippleIdentifier {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	boolean isDeterministic=false; 
	
	RipplePublicKey publicKey;
	
	public RipplePrivateKey(byte[] privateKeyBytes) {
		super(privateKeyBytes, 34);
		if(privateKeyBytes.length!=32){
			throw new RuntimeException("The private key must be of length 32 bytes");
		}
	}

	public static byte[] bigIntegerToBytes(BigInteger biToConvert, int nbBytesToReturn){
		byte[] twosComplement = biToConvert.toByteArray();
		byte[] bytesToReturn=new byte[nbBytesToReturn];

		if((biToConvert.bitLength()+7)/8!=twosComplement.length){
			byte[] twosComplementWithoutSign = new byte[twosComplement.length-1];
			System.arraycopy(twosComplement, 1, twosComplementWithoutSign, 0, twosComplementWithoutSign.length);
			twosComplement=twosComplementWithoutSign;
		}

		int nbBytesOfPaddingRequired=nbBytesToReturn-twosComplement.length;
		if(nbBytesOfPaddingRequired<0){
			throw new RuntimeException("nbBytesToReturn "+nbBytesToReturn+" is too small");
		}
		System.arraycopy(twosComplement, 0, bytesToReturn, nbBytesOfPaddingRequired, twosComplement.length);

		return bytesToReturn;
	}
	
	public RipplePrivateKey(BigInteger privateKeyForAccount) {
		super(bigIntegerToBytes(privateKeyForAccount, 32), 34);
	}

	public RipplePublicKey getPublicKey(){
		if(publicKey!=null){
			return publicKey;
		}
		BigInteger privateBI=new BigInteger(1, this.payloadBytes);
		ECPoint uncompressed= RippleDeterministicKeyGenerator.SECP256K1_PARAMS.getG().multiply(privateBI);
		ECPoint publicPoint = new ECPoint.Fp(RippleDeterministicKeyGenerator.SECP256K1_PARAMS.getCurve(), uncompressed.getX(), uncompressed.getY(), true);
		publicKey = new RipplePublicKey(publicPoint.getEncoded());
		return publicKey;
	}

	public ECPrivateKeyParameters getECPrivateKey(){
		BigInteger privateBI=new BigInteger(1, this.payloadBytes);
		ECPrivateKeyParameters privKey = new ECPrivateKeyParameters(privateBI, RippleDeterministicKeyGenerator.SECP256K1_PARAMS);
		return privKey;
	}
}
