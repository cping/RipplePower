package org.ripple.power.txns;

import java.util.regex.Pattern;

import com.google.common.base.Strings;

public class Server {

	public final static boolean isDOMAIN_RE(String res) {
		if (Strings.isNullOrEmpty(res)) {
			return false;
		}
		String reg = "^(?=.{1,255}$)[0-9A-Za-z](?:(?:[0-9A-Za-z]|[-_]){0,61}[0-9A-Za-z])?(?:\\.[0-9A-Za-z](?:(?:[0-9A-Za-z]|[-_]){0,61}[0-9A-Za-z])?)*\\.?$";
		return Pattern.matches(reg, res);
	}

	public final static String[] TLS_ERRORS = { "UNABLE_TO_GET_ISSUER_CERT",
			"UNABLE_TO_GET_CRL", "UNABLE_TO_DECRYPT_CERT_SIGNATURE",
			"UNABLE_TO_DECRYPT_CRL_SIGNATURE",
			"UNABLE_TO_DECODE_ISSUER_PUBLIC_KEY", "CERT_SIGNATURE_FAILURE",
			"CRL_SIGNATURE_FAILURE", "CERT_NOT_YET_VALID", "CERT_HAS_EXPIRED",
			"CRL_NOT_YET_VALID", "CRL_HAS_EXPIRED",
			"ERROR_IN_CERT_NOT_BEFORE_FIELD", "ERROR_IN_CERT_NOT_AFTER_FIELD",
			"ERROR_IN_CRL_LAST_UPDATE_FIELD", "ERROR_IN_CRL_NEXT_UPDATE_FIELD",
			"OUT_OF_MEM", "DEPTH_ZERO_SELF_SIGNED_CERT",
			"SELF_SIGNED_CERT_IN_CHAIN", "UNABLE_TO_GET_ISSUER_CERT_LOCALLY",
			"UNABLE_TO_VERIFY_LEAF_SIGNATURE", "CERT_CHAIN_TOO_LONG",
			"CERT_REVOKED", "INVALID_CA", "PATH_LENGTH_EXCEEDED",
			"INVALID_PURPOSE", "CERT_UNTRUSTED", "CERT_REJECTED" };

}
