package org.ripple.power.txns;

import java.math.BigDecimal;
import java.math.MathContext;

public class CurrencyUtils {
	private static final BigDecimal MILLION = new BigDecimal("1000000");

	public static String getRippleToValue(String value) {
		return new BigDecimal(value).divide(MILLION, MathContext.DECIMAL128)
				.toString();
	}

	public static String getValueToRipple(String value) {
		String num = new BigDecimal(value).multiply(MILLION,
				MathContext.DECIMAL128).toString();
		int index = num.indexOf('.');
		if (index != -1) {
			num = num.substring(0, index - 1);
		}
		return num;
	}

}
