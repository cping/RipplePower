package org.ripple.power.qr;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

import org.ripple.power.wallet.WalletItem;

public class RippleURI {

	public static final String BITCOIN_SCHEME = "ripplepower";
	private static final String ENCODED_SPACE_CHARACTER = "%20";
	private static final String AMPERSAND_SEPARATOR = "&";
	private static final String QUESTION_MARK_SEPARATOR = "?";
	public static final String FIELD_MESSAGE = "message";
	public static final String FIELD_LABEL = "label";
	public static final String FIELD_AMOUNT = "amount";
	public static final String FIELD_ADDRESS = "address";

	public static String convertToBitcoinURI(WalletItem address, BigDecimal amount, String label, String message) {
		return convertToBitcoinURI(address.getPublicKey(), amount, label, message);
	}

	public static String convertToBitcoinURI(String address, BigDecimal amount, String label, String message) {
		if (amount != null && amount.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException("Amount must be positive");
		}

		StringBuilder builder = new StringBuilder();
		builder.append(BITCOIN_SCHEME).append(":").append(address);

		boolean questionMarkHasBeenOutput = false;

		if (amount != null) {
			builder.append(QUESTION_MARK_SEPARATOR).append(FIELD_AMOUNT).append("=");
			builder.append(amount);
			questionMarkHasBeenOutput = true;
		}

		if (label != null && !"".equals(label)) {
			if (questionMarkHasBeenOutput) {
				builder.append(AMPERSAND_SEPARATOR);
			} else {
				builder.append(QUESTION_MARK_SEPARATOR);
				questionMarkHasBeenOutput = true;
			}
			builder.append(FIELD_LABEL).append("=").append(encodeURLString(label));
		}

		if (message != null && !"".equals(message)) {
			if (questionMarkHasBeenOutput) {
				builder.append(AMPERSAND_SEPARATOR);
			} else {
				builder.append(QUESTION_MARK_SEPARATOR);
			}
			builder.append(FIELD_MESSAGE).append("=").append(encodeURLString(message));
		}

		return builder.toString();
	}

	static String encodeURLString(String stringToEncode) {
		try {
			return java.net.URLEncoder.encode(stringToEncode, "UTF-8").replace("+", ENCODED_SPACE_CHARACTER);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

}
