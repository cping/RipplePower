package org.ripple.power.ioc.reflect;

import java.util.Arrays;

import org.ripple.power.utils.ReflectorUtils;

public class TypeArray {

	protected final Class<?>[] types;

	private int hashcode;

	private boolean aliased;

	public TypeArray(final Class<?>[] types) {
		this.types = (Class[]) (types != null ? types.clone() : new Class[0]);
		this.hashcode = ReflectorUtils.arrayHashCode(this.types);
	}

	public int hashCode() {
		return hashcode;
	}

	public boolean equals(Object obj) {
		if (obj != null && TypeArray.class.equals(obj.getClass())) {
			TypeArray other = (TypeArray) obj;
			return (Arrays.equals(this.types, other.types) && this.aliased == other.aliased);
		}
		return false;
	}

	public Class<?>[] getParameterTypes() {
		return (Class[]) types.clone();
	}

	public boolean isAliased() {
		return aliased;
	}

	public void setAliased(boolean aliased) {
		if (this.aliased != aliased) {
			if (aliased) {
				hashcode += 19;
			} else {
				hashcode -= 19;
			}
			this.aliased = aliased;
		}
	}
}
