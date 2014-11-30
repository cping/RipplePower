package org.ripple.power.sjcl;

public class JSValue<T> implements Value<T> {

	public static final JSValue<String> undefined = new JSValue<String>(
			JSType.UNDEF, JSType.UNDEF);

	private T value = null;

	String typeFlag = JSType.UNDEF;

	public JSValue(final String flag, final JSValue<T> toclone) {
		this(flag, toclone.value);
	}

	public JSValue(final String flag, final T value) {
		this.typeFlag = flag;
		this.value = value;
	}

	public JSBool equals(JSValue<?> rhs) {
		if (this.typeFlag == rhs.typeFlag) {
			if (this.typeFlag == JSType.UNDEF) {
				return JSBool.TRUE;
			}
			if (this.typeFlag == JSType.NUMBER) {
				if (this == JSNumber.NAN) {
					return JSBool.FALSE;
				}
				if (rhs == JSNumber.NAN) {
					return JSBool.FALSE;
				}
				if (this.value == rhs.value) {
					return JSBool.TRUE;
				}
				return JSBool.FALSE;
			}
			if (this.typeFlag == JSType.STRING) {
				return this.value.equals(rhs.value) ? JSBool.TRUE
						: JSBool.FALSE;
			}
			return this == rhs ? JSBool.TRUE : JSBool.FALSE;
		}
		return JSBool.FALSE;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!JSValue.isJS(obj)) {
			return false;
		}
		final JSValue<?> other = (JSValue<?>) obj;
		if (this.value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!this.value.equals(other.value)) {
			return false;
		}
		return true;
	}

	public T getValue() {
		return this.value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (this.value == null ? 0 : this.value.hashCode());
		return result;
	}

	public boolean isNull() {
		return null == this.value;
	}

	public boolean isUndefined() {
		return JSValue.undefined == this.value;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		if (isUndefined()) {
			builder.append(JSValue.undefined);
		} else {
			builder.append(String.valueOf(this.value));
		}
		return builder.toString();
	}

	public static final boolean isJS(final Object obj) {
		return obj instanceof JSValue<?>;
	}

	public static final boolean isNull(final Object obj) {
		if (!JSValue.isJS(obj)) {
			return false;
		}
		final JSValue<?> j = (JSValue<?>) obj;
		return null == j.value;
	}

	public static final boolean isUndefined(final Object o) {
		if (!JSValue.isJS(o)) {
			return false;
		}
		final JSValue<?> j = (JSValue<?>) o;
		return JSValue.undefined == j.value;
	}

	@Override
	public T get() {
		return value;
	}

	public String getFlag() {
		return typeFlag;
	}

}
