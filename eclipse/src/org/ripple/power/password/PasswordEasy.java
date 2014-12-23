package org.ripple.power.password;

import org.ripple.power.config.Alphabet;
import org.ripple.power.config.LSystem;
import org.ripple.power.utils.MathUtils;

public class PasswordEasy {

	private final static Integer LENGTH_PASS = 9;

	private final static char[] DEFAULT_PASS_MAP = Alphabet.ENGLISH
			.getAlphabet();

	public final static char[] getDefaultMap() {
		return DEFAULT_PASS_MAP;
	}

	private char[] MATRIX = DEFAULT_PASS_MAP;

	public void setPassMatrix(char[] matrix) {
		MATRIX = matrix;
	}

	public String pass(int length_pass) {
		if (length_pass <= 0) {
			return "";
		}
		char[] buffer = new char[length_pass];
		int c = 0;
		for (; c != length_pass;) {
			buffer[c] = MATRIX[LSystem.random.nextInt(MATRIX.length)];
			c++;
		}
		return new String(buffer);
	}

	public String pass() {
		return pass(LENGTH_PASS);
	}

	public String pass(int min, int max) {
		return pass(MathUtils.random(min, max));
	}

}
