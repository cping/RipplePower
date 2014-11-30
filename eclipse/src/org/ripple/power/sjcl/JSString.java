package org.ripple.power.sjcl;

public class JSString extends JSValue<String> {

	public static final JSString EMPTY = new JSString("");

	public JSString(final String value) {
		super(JSType.STRING, value);
	}

	@Override
	public String toString() {
		if (isUndefined()) {
			return super.toString();
		} else {
			final StringBuilder builder = new StringBuilder();
			builder.append("\"").append(getValue()).append("\"");
			return builder.toString();
		}
	}

}