package org.ripple.power.password;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class PasswordStrengthMeter {

	private static String alpha_10_0 = "千";
	private static String alpha_10_4 = "万";
	private static String alpha_10_8 = "亿";
	private static String alpha_10_12 = "兆";
	private static String alpha_10_16 = "京";
	private static String alpha_10_20 = "垓";
	private static String alpha_10_24 = "秭";
	private static String alpha_10_28 = "穰";
	private static String alpha_10_32 = "沟";
	private static String alpha_10_36 = "涧";
	private static String alpha_10_40 = "正";
	private static String alpha_10_44 = "载 ";
	private static String alpha_10_48 = "极";
	private static String alpha_10_52 = "恒河沙";
	private static String alpha_10_56 = "阿僧只";
	private static String alpha_10_60 = "那由他";
	private static String alpha_10_64 = "不可思议";
	private static String alpha_10_68 = "无量大数";

	private static String[] alpha_arrays = new String[] { alpha_10_0,
			alpha_10_4, alpha_10_8, alpha_10_12, alpha_10_16, alpha_10_20,
			alpha_10_24, alpha_10_28, alpha_10_32, alpha_10_36, alpha_10_40,
			alpha_10_44, alpha_10_48, alpha_10_52, alpha_10_56, alpha_10_60,
			alpha_10_64, alpha_10_68 };
	public static final int PASSWORD_LENGTH_LIMIT = 256;
	private static final int BIG_DECIMAL_SCALE = 4096;

	private boolean verifyPartialSumResult = false;

	private static Map<Class<? extends PasswordStrengthMeter>, Object> impls;

	protected PasswordStrengthMeter() {
	}

	public static PasswordStrengthMeter getInstance() {
		if (null == impls) {
			impls = new HashMap<Class<? extends PasswordStrengthMeter>, Object>();
		}
		Object impl = impls.get(PasswordStrengthMeter.class);
		if (null == impl) {
			impl = new PasswordStrengthMeter();
			impls.put(PasswordStrengthMeter.class, impl);
		}

		return (PasswordStrengthMeter) impl;
	}

	public static PasswordStrengthMeter getInstance(
			Class<? extends PasswordStrengthMeter> clazz) {
		if (null == impls) {
			impls = new HashMap<Class<? extends PasswordStrengthMeter>, Object>();
		}
		Object impl = impls.get(clazz);
		if (null == impl) {
			try {
				impl = clazz.newInstance();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			impls.put(clazz, impl);
		}

		return (PasswordStrengthMeter) impl;
	}

	public boolean satisfiesStrengthClass(String password,
			PasswordStrengthClass strengthClass) {
		BigInteger iterationCount = null;
		try {
			iterationCount = iterationCount(password, false);
		} catch (RuntimeException lengthException) {
			return true;
		}

		return iterationCount.compareTo(strengthClass.getIterations()) >= 0;
	}

	public BigInteger iterationCount(String passwordPlaintext) {
		try {
			return iterationCount(passwordPlaintext, false);
		} catch (RuntimeException e) {
			return new BigInteger("-1");
		}
	}

	public BigInteger iterationCount(String passwordPlaintext,
			boolean bypassLengthLimitCheck) throws RuntimeException {
		if (null == passwordPlaintext || passwordPlaintext.length() < 1) {
			return new BigInteger("0");
		}

		int passwordLength = Character.codePointCount(passwordPlaintext, 0,
				passwordPlaintext.length());
		if (!bypassLengthLimitCheck && passwordLength > PASSWORD_LENGTH_LIMIT) {
			throw new RuntimeException();
		}

		PasswordCharacterRange range = new PasswordCharacterRange(
				passwordPlaintext);
		BigInteger rangeSize = new BigInteger(Long.toString(range.size()));

		BigInteger result;

		BigInteger partialSumInner = rangeSize.pow(passwordLength - 1)
				.subtract(new BigInteger("1"));

		BigDecimal partialSumMultiplier = new BigDecimal(range.size());
		partialSumMultiplier = partialSumMultiplier.divide(
				partialSumMultiplier.subtract(new BigDecimal("1")),
				BIG_DECIMAL_SCALE, RoundingMode.HALF_UP);
		BigDecimal partialSumResult = partialSumMultiplier
				.multiply(new BigDecimal(partialSumInner));
		result = partialSumResult.setScale(0, RoundingMode.HALF_UP)
				.toBigIntegerExact();

		if (verifyPartialSumResult) {
			BigInteger slowResult = new BigInteger("0");
			for (int i = 1; i < passwordLength; i++) {
				BigInteger iteration = rangeSize.pow(i);
				slowResult = slowResult.add(iteration);
			}

			boolean resultsMatch = result.compareTo(slowResult) == 0;
			if (!resultsMatch) {
				throw new RuntimeException(
						"Values didn't match on password with length "
								+ passwordLength);
			}
		}

		for (int i = 1, supplementalCharCount = 0; i <= passwordPlaintext
				.length(); i++) {
			int power = passwordLength - (i - supplementalCharCount);
			int codePoint = passwordPlaintext.codePointAt(i - 1);
			long placeValue = range.position(codePoint);

			if (Character.isSupplementaryCodePoint(codePoint)) {
				i++;
				supplementalCharCount++;
			}

			if (power == 0 && placeValue == 0) {
				continue;
			}

			BigInteger multiplier = rangeSize.pow(power);
			BigInteger iteration = new BigInteger(Long.toString(placeValue))
					.multiply(multiplier);
			result = result.add(iteration);
		}

		return result.add(new BigInteger("1"));
	}

	public static String createString(BigInteger bi) {
		if (null == bi) {
			throw new IllegalArgumentException("number must be specified.");
		}
		if (-1 == bi.signum()) {
			throw new IllegalArgumentException(
					"number must be zero or positive number.given:"
							+ bi.toString());
		}
		BigInteger unit = BigInteger.valueOf(10000);
		BigInteger buf = bi;
		int numDigits = 0;
		Queue<BigInteger> queue = new LinkedList<BigInteger>();
		while (-1 != buf.compareTo(unit) && numDigits <= 64) {
			BigInteger mod = buf.mod(unit);
			queue.add(mod);
			buf = (buf.subtract(mod)).divide(unit);
			numDigits += 4;
		}
		queue.add(buf);
		StringBuilder sb = new StringBuilder();
		int queueSize = queue.size();
		for (int i = 0; i < queueSize; i++) {
			BigInteger mod = queue.poll();
			if (mod.longValue() != 0) {
				sb.insert(0, mod + alpha_arrays[i]);
			}
		}
		if (0 != sb.length()) {
			return sb.toString();
		} else {
			return "0";
		}
	}

}
