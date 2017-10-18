package org.ripple.power.txns.btc;

import org.ripple.bouncycastle.asn1.ASN1InputStream;
import org.ripple.bouncycastle.asn1.ASN1Integer;
import org.ripple.bouncycastle.asn1.DERSequenceGenerator;
import org.ripple.bouncycastle.asn1.DLSequence;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;

/**
 * ECDSASignature is an elliptic curve digital signature consisting of the R and
 * S values.
 */
public class ECDSASignature {

	/** R and S components of the digital signature */
	private BigInteger r;
	private BigInteger s;

	/**
	 * Creates a digital signature from the R and S values
	 *
	 * @param r
	 *            R value
	 * @param s
	 *            S value
	 */
	public ECDSASignature(BigInteger r, BigInteger s) {
		this.r = r;
		this.s = s;
	}

	/**
	 * Creates a digital signature from the DER-encoded values
	 *
	 * @param encodedStream
	 *            DER-encoded value
	 * @throws ECException
	 *             Unable to decode the stream
	 */
	public ECDSASignature(byte[] encodedStream) throws ECException {
		try {
			try (ASN1InputStream decoder = new ASN1InputStream(encodedStream)) {
				DLSequence seq = (DLSequence) decoder.readObject();
				r = ((ASN1Integer) seq.getObjectAt(0)).getPositiveValue();
				s = ((ASN1Integer) seq.getObjectAt(1)).getPositiveValue();
			}
		} catch (ClassCastException | IOException exc) {
			throw new ECException("Unable to decode signature", exc);
		}
	}

	/**
	 * Returns the R value
	 *
	 * @return R component
	 */
	public BigInteger getR() {
		return r;
	}

	/**
	 * Returns the S value
	 *
	 * @return S component
	 */
	public BigInteger getS() {
		return s;
	}

	/**
	 * Encodes R and S as a DER-encoded byte stream
	 *
	 * @return DER-encoded byte stream
	 */
	public byte[] encodeToDER() {
		byte[] encodedBytes = null;
		try {
			try (ByteArrayOutputStream outStream = new ByteArrayOutputStream(80)) {
				DERSequenceGenerator seq = new DERSequenceGenerator(outStream);
				seq.addObject(new ASN1Integer(r));
				seq.addObject(new ASN1Integer(s));
				seq.close();
				encodedBytes = outStream.toByteArray();
			}
		} catch (IOException exc) {
			throw new IllegalStateException("Unexpected IOException", exc);
		}
		return encodedBytes;
	}
}
