package org.ripple.power.hft;

public class Symbol {

	private String symbol;
	
	private Symbol(String symbol) {
		this.symbol = symbol;
	}

	public static Symbol newInstance(String symbol) {
		if (symbol == null) {
			throw new java.lang.IllegalArgumentException(
					"symbol cannot be null");
		}

		return new Symbol(symbol);
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + symbol.hashCode();

		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof Symbol)) {
			return false;
		}
		return this.symbol.equals(((Symbol) o).symbol);
	}

	@Override
	public String toString() {
		return symbol;
	}


}
