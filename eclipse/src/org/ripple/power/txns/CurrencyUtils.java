package org.ripple.power.txns;

import java.math.BigDecimal;
import java.math.MathContext;

import org.ripple.power.config.LSystem;
import org.ripple.power.utils.MathUtils;

public class CurrencyUtils {
	private static final BigDecimal MILLION = new BigDecimal("1000000");

	public static String toFee(String amount) {
		if (MathUtils.isNan(amount)) {
			long number = Long.valueOf(amount);
			double fee = Double.valueOf(LSystem.FEE);
			double old = fee;
			long limit = 3000;
			for (long l = limit; l < number; l += limit) {
				fee += 0.005d;
			}
			if (fee > 1000d) {
				fee = 1000d;
			}
			if (old < fee) {
				String result = String.valueOf(new BigDecimal(fee).setScale(5,
						java.math.BigDecimal.ROUND_HALF_UP).doubleValue());
				if (result.endsWith(".0")) {
					return result.replace(".0", "");
				} else {
					return result;
				}
			}
		}
		return LSystem.FEE;
	}

	public static String getFee(String fee) {
		Double result = (Double.valueOf(getRippleToValue(fee)) - 1000l) / 10l;
		return String.valueOf(result);
	}

	public static String getRippleToValue(String value) {
		return new BigDecimal(value).divide(MILLION, MathContext.DECIMAL128)
				.toString();
	}

	public static String getValueToRipple(String value) {
		String num = new BigDecimal(value).multiply(MILLION,
				MathContext.DECIMAL128).toString();
		int index = num.indexOf('.');
		if (index != -1) {
			num = num.substring(0, index);
		}
		return num;
	}

}
