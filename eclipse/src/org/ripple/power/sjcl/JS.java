package org.ripple.power.sjcl;

public class JS {

	public static Number OR(Number a, Number b) {
		if (a.longValue() > 0) {
			return a;
		}
		if (b.longValue() > 0) {
			return b;
		}
		return a;
	}

}
