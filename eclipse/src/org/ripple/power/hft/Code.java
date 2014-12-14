package org.ripple.power.hft;

public class Code {

	private Code(String code) {
		this.code = code;
	}

	public static Code newInstance(String code) {
		if (code == null) {
			throw new java.lang.IllegalArgumentException("code cannot be null");
		}
		return new Code(code);
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + code.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof Code)) {
			return false;
		}
		return this.code.equals(((Code) o).code);
	}

	@Override
	public String toString() {
		return code;
	}

	private String code;

}
