package org.ripple.power.nodejs;

public class JSNumber extends JSValue<Number> {

	public static final JSNumber POS_INFINITY = new JSNumber(
			Double.POSITIVE_INFINITY);

	public static final JSNumber NEG_INFINITY = new JSNumber(
			Double.NEGATIVE_INFINITY);

	public static final JSNumber NAN = new JSNumber(Double.NaN);

	public static final JSNumber ZERO = new JSNumber(0.0);

	public static final JSNumber ONE = new JSNumber(1.0);

	public JSNumber(final Number value) {
		super(JSType.NUMBER, value);
	}

	public BigNumber bn() {
		return BigNumber.bn(this.get());
	}
}
