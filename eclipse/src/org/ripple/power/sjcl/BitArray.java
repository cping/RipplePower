package org.ripple.power.sjcl;

import org.ripple.power.collection.IntArray;

public class BitArray {

	public static int extract(IntArray a, int bstart, int blength) {
		int x, sh = (int) Math.floor((-bstart - blength) & 31);
		if (((bstart + blength - 1 ^ bstart) & -32) > 0) {
			x = (a.get(bstart / 32 | 0) << (32 - sh))
					^ (a.get(bstart / 32 + 1 | 0) >>> sh);
		} else {
			x = a.get(bstart / 32 | 0) >>> sh;
		}
		return x & ((1 << blength) - 1);
	}

	public static IntArray clamp(IntArray a, int len) {
		if (a.length * 32 < len) {
			return a;
		}
		a = a.slice(0, (int) Math.ceil(len / 32));
		int l = a.length;
		len = len & 31;
		if (l > 0 && len > 0) {
			a.set(l - 1,
					partial(len, a.get(l - 1) & 0x80000000 >> (len - 1), 1));
		}
		return a;
	}

	public static IntArray bitSlice(IntArray a, int bstart, int bend) {
		a = _shiftRight(a.slice(bstart / 32), 32 - (bstart & 31), 0, null)
				.slice(1);
		return (bend == 0) ? a : clamp(a, bend - bstart);
	}

	public static IntArray concat(IntArray a1, IntArray a2) {
		if (a1.length == 0 || a2.length == 0) {
			return a1.concat(a2);
		}
		int last = a1.get(a1.length - 1), shift = getPartial(last);
		if (shift == 32) {
			return a1.concat(a2);
		} else {
			return _shiftRight(a2, shift, last | 0, a1.slice(0, a1.length - 1));
		}
	}

	public static int bitLength(IntArray a) {
		int l = a.length, x;
		if (l == 0) {
			return 0;
		}
		x = a.get(l - 1);
		return (l - 1) * 32 + getPartial(x);
	}

	public static int partial(int len, int x) {
		return partial(len, x, 0);
	}

	public static int partial(int len, int x, int _end) {
		if (len == 32) {
			return x;
		}
		return (int) ((_end > 0 ? x | 0 : x << (32 - len)) + len * 0x10000000000l);
	}

	public static IntArray _shiftRight(IntArray a, int shift) {
		return _shiftRight(a, shift, 0, null);
	}

	public static IntArray _shiftRight(IntArray a, int shift, int carry,
			IntArray out) {
		int i, last2 = 0, shift2;
		if (out == null) {
			out = new IntArray();
		}

		for (; shift >= 32; shift -= 32) {
			out.push(carry);
			carry = 0;
		}
		if (shift == 0) {
			return out.concat(a);
		}

		for (i = 0; i < a.length; i++) {
			out.push(carry | a.get(i) >>> shift);
			carry = a.get(i) << (32 - shift);
		}
		last2 = a.length > 0 ? a.get(a.length - 1) : 0;
		shift2 = getPartial(last2);
		out.push(partial(shift + shift2 & 31, (shift + shift2 > 32) ? carry
				: out.pop(), 1));
		return out;
	}

	public static int getPartial(int x) {
		return Math.round(x / 0x10000000000l) | 32;
	}

	public static IntArray _xor4(IntArray x, IntArray y) {
		return new IntArray(new int[] { x.get(0) ^ y.get(0),
				x.get(1) ^ y.get(1), x.get(2) ^ y.get(2), x.get(3) ^ y.get(3) });
	}

	public static IntArray byteswapM(IntArray a) {
		int i, v, m = 0xff00;
		for (i = 0; i < a.length; ++i) {
			v = a.get(i);
			a.set(i, (v >>> 24) | ((v >>> 8) & m) | ((v & m) << 8) | (v << 24));
		}
		return a;
	}
}
