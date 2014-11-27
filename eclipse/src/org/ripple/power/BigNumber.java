package org.ripple.power;

import org.ripple.power.collection.Array;

public class BigNumber {
	int radix = 24;
	int maxMul = 8;
	Array<Integer> limbs;
	int placeVal = (int) Math.pow(2, radix);
	int ipv = 1 / placeVal;
	int radixMask = (1 << radix) - 1;

	public BigNumber(Object it) {
		initWith(it);
	}

	public Array<Integer> get() {
		return limbs;
	}

	public String toString() {
		this.normalize();
		String out = "";
		int i;
		String s;
		Array<Integer> l = this.limbs;
		for (i = 0; i < this.limbs.size(); i++) {
			s = Long.toHexString(l.get(i));
			while (i < this.limbs.size() - 1 && s.length() < 6) {
				s = "0" + s;
			}
			out = s + out;
		}
		return "0x" + out;
	}

	public BigNumber normalize() {
		int carry = 0, i, pv = this.placeVal, ipv = this.ipv, l, m, ll = limbs
				.size(), mask = this.radixMask;
		for (i = 0; i < ll || (carry != 0 && carry != -1); i++) {
			l = (limbs.get(i) | 0) + carry;
			m = limbs.get(i);
			limbs.set(i, l & mask);
			carry = (l - m) * ipv;
		}
		if (carry == -1) {
			limbs.set(i - 1, limbs.get(i - 1) - pv);
		}
		return this;
	}

	public void initWith(Object it) {
		int i = 0, k;
		if (it instanceof String) {
			String itStr = (String) it;
			it = itStr.replace("0x", "");
			this.limbs = new Array<Integer>();
			k = this.radix / 4;
			for (i = 0; i < itStr.length(); i += k) {
				int v = Integer.parseInt(
						itStr.substring(Math.max(itStr.length() - i - k, 0),
								itStr.length() - i), 16);
				this.limbs.add(v);
			}
		}else if(it instanceof Array){
			@SuppressWarnings("unchecked")
			Array<Integer> itArray = (Array<Integer>) it;
			this.limbs = itArray;
		}

	}
}
