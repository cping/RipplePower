package org.ripple.power.nodejs;

import java.math.BigDecimal;

import org.ripple.power.collection.LongArray;

public class BigInteger {

	public final static java.math.BigInteger PUBLIC_EXPONENT = new java.math.BigInteger(
			"65537");
	public LongArray limbs;
	private final static String BI_RM = "0123456789abcdefghijklmnopqrstuvwxyz";
	private final static LongArray BI_RC = new LongArray();
	private final static long dbits = 28;
	private final static long DB = dbits;
	private final static long DM = ((1 << dbits) - 1);
	private final static long BI_FP = 52;
	private final static long DV = (1 << dbits);
	private final static BigDecimal FV = BigDecimal.valueOf(Math.pow(2, BI_FP));
	private final static long F1 = BI_FP - dbits;
	private final static long F2 = 2 * dbits - BI_FP;
	private final static long[] lowprimes = { 2, 3, 5, 7, 11, 13, 17, 19, 23,
			29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97,
			101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163,
			167, 173, 179, 181, 191, 193, 197, 199, 211, 223, 227, 229, 233,
			239, 241, 251, 257, 263, 269, 271, 277, 281, 283, 293, 307, 311,
			313, 317, 331, 337, 347, 349, 353, 359, 367, 373, 379, 383, 389,
			397, 401, 409, 419, 421, 431, 433, 439, 443, 449, 457, 461, 463,
			467, 479, 487, 491, 499, 503, 509, 521, 523, 541, 547, 557, 563,
			569, 571, 577, 587, 593, 599, 601, 607, 613, 617, 619, 631, 641,
			643, 647, 653, 659, 661, 673, 677, 683, 691, 701, 709, 719, 727,
			733, 739, 743, 751, 757, 761, 769, 773, 787, 797, 809, 811, 821,
			823, 827, 829, 839, 853, 857, 859, 863, 877, 881, 883, 887, 907,
			911, 919, 929, 937, 941, 947, 953, 967, 971, 977, 983, 991, 997 };
	long lplim = (1 << 26) / lowprimes[lowprimes.length - 1];
	static {
		int rr, vv;
		rr = "0".charAt(0);
		for (vv = 0; vv <= 9; ++vv) {
			BI_RC.set(rr++, vv);
		}
		rr = "a".charAt(0);
		for (vv = 10; vv < 36; ++vv) {
			BI_RC.set(rr++, vv);
		}
		rr = "A".charAt(0);
		for (vv = 10; vv < 36; ++vv) {
			BI_RC.set(rr++, vv);
		}
	}

	long _T, _S;

	public final static BigInteger ZERO = new BigInteger("0");

	public BigInteger(Object o) {

	}

	long am1(int i, long x, long[] w, int j, long c, long n) {
		while (--n >= 0) {
			long v = x * this.limbs.get(i++) + w[j] + c;
			c = BigDecimal
					.valueOf(Math.floor((double) v / (double) 0x4000000l))
					.longValue();
			w[j++] = v & 0x3ffffff;
		}
		return c;
	}

	long am2(int i, long x, long[] w, int j, long c, long n) {
		long xl = x & 0x7fff, xh = x >> 15;
		while (--n >= 0) {
			long l = this.limbs.get(i) & 0x7fff;
			long h = this.limbs.get(i++) >> 15;
			long m = xh * l + h * xl;
			l = xl * l + ((m & 0x7fff) << 15) + w[j] + (c & 0x3fffffff);
			c = (l >>> 30) + (m >>> 15) + xh * h + (c >>> 30);
			w[j++] = l & 0x3fffffff;
		}
		return c;
	}

	long am3(long i, long x, BigInteger w, long j, long c, long n) {
		long xl = x & 0x3fff, xh = x >> 14;
		while (--n >= 0) {
			long l = this.limbs.get((int) i) & 0x3fff;
			long h = this.limbs.get((int) (i++)) >> 14;
			long m = xh * l + h * xl;
			l = xl * l + ((m & 0x3fff) << 14) + w.limbs.items[(int) j] + c;
			c = (l >> 28) + (m >> 14) + xh * h;
			w.limbs.items[(int) (j++)] = l & 0xfffffff;
		}
		return c;
	}

	public void bnpFromString(String s, Number b) {
		long k = 0;
		if (b.intValue() == 16)
			k = 4;
		else if (b.intValue() == 8)
			k = 3;
		else if (b.intValue() == 256)
			k = 8;
		else if (b.intValue() == 2)
			k = 1;
		else if (b.intValue() == 32)
			k = 5;
		else if (b.intValue() == 4)
			k = 2;
		// else { this.fromRadix(s,b); return; }
		this._T = 0;
		this._S = 0;
		int i = s.length();
		boolean mi = false;
		long sh = 0;
		while (--i >= 0) {
			long x = (k == 8) ? s.charAt(i) & 0xff : intAt(s, i);
			if (x < 0) {
				if (s.charAt(i) == '-') {
					mi = true;
				}
				continue;
			}
			mi = false;
			if (sh == 0) {
				this.limbs.set((int) (this._T++), x);
			} else if (sh + k > DB) {
				this.limbs.items[(int) this._T - 1] |= (x & ((1 << (DB - sh)) - 1)) << sh;
				this.limbs.items[(int) this._T++] = (x >> (DB - sh));
			} else {
				this.limbs.items[(int) this._T - 1] |= x << sh;
			}
			sh += k;
			if (sh >= DB) {
				sh -= DB;
			}
		}
		if (k == 8 && (s.charAt(0) & 0x80) != 0) {
			this._S = -1;
			if (sh > 0)
				this.limbs.items[(int) this._T - 1] |= ((1 << (DB - sh)) - 1) << sh;
		}
		this.bnpClamp();
		// if(mi) BigInteger.ZERO.subTo(this,this);
	}

	public char int2char(int n) {
		return BI_RM.charAt(n);
	}

	public int intAt(String s, int i) {
		char res = s.charAt(i);
		if (BI_RC.contains(res)) {
			return (int) BI_RC.get(res);
		}
		return -1;
	}

	public void clamp() {
		long c = this._S & DM;
		while (this._T > 0 && this.limbs.get((int) this._T - 1) == c) {
			--this._T;
		}
	}

	public BigInteger negate() {
		return bnNegate();
	}

	public BigInteger bnNegate() {
		BigInteger r = nbi();
		BigInteger.ZERO.subTo(this, r);
		return r;
	}

	public BigInteger abs() {
		return bnAbs();
	}

	public BigInteger bnAbs() {
		return (this._S < 0) ? this.negate() : this;
	}

	public void copyTo(BigInteger r) {
		bnpCopyTo(r);
	}

	public void bnpCopyTo(BigInteger r) {
		for (int i = (int) (this._T - 1); i >= 0; --i) {
			r.limbs.set(i, this.limbs.get(i));
		}
		r._T = this._T;
		r._S = this._S;
	}

	public BigInteger nbv(long i) {
		BigInteger r = nbi();
		r.fromInt(i);
		return r;
	}

	public BigInteger nbi() {
		return new BigInteger(null);
	}

	public void fromInt(long x) {
		bnpFromInt(x);
	}

	public void bnpFromInt(long x) {
		this._T = 1;
		this._S = (x < 0) ? -1 : 0;
		if (x > 0) {
			this.limbs.set(0, x);
		} else if (x < -1) {
			this.limbs.set(0, x + DV);
		} else {
			this._T = 0;
		}
	}

	public long bnCompareTo(BigInteger a) {
		long r = this._S - a._S;
		if (r != 0) {
			return r;
		}
		int i = (int) this._T;
		r = i - a._T;
		if (r != 0) {
			return (this._S < 0) ? -r : r;
		}
		while (--i >= 0)
			if ((r = this.limbs.get(i) - a.limbs.get(i)) != 0) {
				return r;
			}
		return 0;
	}

	public long nbits(long x) {
		long r = 1, t;
		if ((t = JS.MOVE_RightUShift(x, 16)) != 0) {
			x = t;
			r += 16;
		}
		if ((t = x >> 8) != 0) {
			x = t;
			r += 8;
		}
		if ((t = x >> 4) != 0) {
			x = t;
			r += 4;
		}
		if ((t = x >> 2) != 0) {
			x = t;
			r += 2;
		}
		if ((t = x >> 1) != 0) {
			x = t;
			r += 1;
		}
		return r;
	}

	public long bnBitLength() {
		if (this._T <= 0) {
			return 0;
		}
		return DB
				* (this._T - 1)
				+ (int) (nbits(this.limbs.get((int) this._T - 1)
						^ (this._S & DM)));
	}

	public void bnpDLShiftTo(int n, BigInteger r) {
		int i;
		for (i = (int) this._T - 1; i >= 0; --i) {
			r.limbs.set(i + n, this.limbs.get(i));
		}
		for (i = n - 1; i >= 0; --i) {
			r.limbs.set(i, 0);
		}
		r._T = this._T + n;
		r._S = this._S;
	}

	public void bnpDRShiftTo(int n, BigInteger r) {
		for (int i = n; i < this._T; ++i) {
			r.limbs.set(i - n, this.limbs.get(i));
		}
		r._T = Math.max(this._T - n, 0);
		r._S = this._S;
	}

	public void bnpLShiftTo(int n, BigInteger r) {
		long bs = n % DB;
		long cbs = DB - bs;
		long bm = (1 << cbs) - 1;
		long ds = new BigDecimal(Math.floor((double) n / (double) DB))
				.longValue();
		long c = (this._S << bs) & DM;
		int i;
		for (i = (int) this._T - 1; i >= 0; --i) {
			long res = (int) ((this.limbs.get(i) >> cbs) | c);
			r.limbs.set((int) (i + ds + 1), res);
			c = (this.limbs.get(i) & bm) << bs;
		}
		for (i = (int) ds - 1; i >= 0; --i) {
			r.limbs.set(i, 0);
		}
		r.limbs.set((int) ds, c);
		r._T = this._T + ds + 1;
		r._S = this._S;
		r.bnpClamp();
	}

	public void bnpClamp() {
		long c = this._S & DM;
		while (this._S > 0 && this.limbs.get((int) this._T - 1) == c) {
			--this._T;
		}
	}

	public void bnpRShiftTo(int n, BigInteger r) {
		r._S = this._S;
		long ds = new BigDecimal(Math.floor((double) n / (double) DB))
				.longValue();
		if (ds >= this._T) {
			r._T = 0;
			return;
		}
		long bs = n % DB;
		long cbs = DB - bs;
		long bm = (1 << bs) - 1;
		r.limbs.set(0, this.limbs.get((int) ds) >> bs);
		for (int i = (int) ds + 1; i < this._T; ++i) {
			r.limbs.items[(int) (i - ds - 1)] |= (int) ((this.limbs.items[i] & bm) << cbs);
			r.limbs.items[(int) (i - ds)] = this.limbs.items[i] >> bs;
		}
		if (bs > 0) {
			r.limbs.items[(int) (this._T - ds - 1)] |= (int) ((this._S & bm) << cbs);
		}
		r._T = this._T - ds;
		r.bnpClamp();
	}

	public void subTo(BigInteger a, BigInteger r) {
		bnpSubTo(a, r);
	}

	public void bnpSubTo(BigInteger a, BigInteger r) {
		int i = 0, c = 0;
		int m = (int) Math.min(a._T, this._T);
		while (i < m) {
			c += this.limbs.items[i] - a.limbs.items[i];
			r.limbs.items[i++] = c & DM;
			c >>= DB;
		}
		if (a._T < this._T) {
			c -= a._S;
			while (i < this._T) {
				c += this.limbs.items[i];
				r.limbs.items[i++] = c & DM;
				c >>= DB;
			}
			c += this._S;
		} else {
			c += this._S;
			while (i < a._T) {
				c -= a.limbs.items[i];
				r.limbs.items[i++] = c & DM;
				c >>= DB;
			}
			c -= a._S;
		}
		r._S = (c < 0) ? -1 : 0;
		if (c < -1) {
			r.limbs.items[i++] = DV + c;
		} else if (c > 0) {
			r.limbs.items[i++] = c;
		}
		r._T = i;
		r.bnpClamp();
	}

	public void bnpSquareTo(BigInteger r) {
		BigInteger x = this.abs();
		long i = r._T = 2 * x._T;
		while (--i >= 0) {
			r.limbs.items[(int) i] = 0;
		}
		for (i = 0; i < x._T - 1; ++i) {
			long c = x.am3(i, x.limbs.items[(int) i], r, 2 * i, 0, 1);
			if ((r.limbs.items[(int) (i + x._T)] += x.am3(i + 1,
					2 * x.limbs.items[(int) i], r, 2 * i + 1, c, x._T - i - 1)) >= DV) {
				r.limbs.items[(int) (i + x._T)] -= DV;
				r.limbs.items[(int) (i + x._T + 1)] = 1;
			}
		}
		if (r._T > 0) {
			r.limbs.items[(int) r._T - 1] += x.am3(i, x.limbs.items[(int) i],
					r, 2 * i, 0, 1);
		}
		r._S = 0;
		r.clamp();
	}

	public long lbit(long x) {
		if (x == 0) {
			return -1;
		}
		long r = 0;
		if ((x & 0xffff) == 0) {
			x >>= 16;
			r += 16;
		}
		if ((x & 0xff) == 0) {
			x >>= 8;
			r += 8;
		}
		if ((x & 0xf) == 0) {
			x >>= 4;
			r += 4;
		}
		if ((x & 3) == 0) {
			x >>= 2;
			r += 2;
		}
		if ((x & 1) == 0) {
			++r;
		}
		return r;
	}

	public long cbit(long x) {
		long r = 0;
		while (x != 0) {
			x &= x - 1;
			++r;
		}
		return r;
	}

	public long bnGetLowestSetBit() {
		for (int i = 0; i < this._T; ++i)
			if (this.limbs.get(i) != 0) {
				return i * DB + lbit(this.limbs.get(i));
			}
		if (this._S < 0) {
			return this._T * DB;
		}
		return -1;
	}

	public long bnBitCount() {
		long r = 0, x = this._S & DM;
		for (int i = 0; i < this._T; ++i) {
			r += cbit(this.limbs.get(i) ^ x);
		}
		return r;
	}

	public static java.math.BigInteger encode(java.math.BigInteger exponent,
			java.math.BigInteger moduli, java.math.BigInteger message) {
		return message.modPow(exponent, moduli);
	}

	public static String encode(String moduli, String message) {
		return toString((encode(PUBLIC_EXPONENT, fromString(moduli),
				fromString(message))));
	}

	public static String encode(java.math.BigInteger moduli, String message) {
		return toString((encode(PUBLIC_EXPONENT, moduli, fromString(message))));
	}

	public static String encode(String exponent, String moduli, String message) {
		return toString((encode(fromString(exponent), fromString(moduli),
				fromString(message))));
	}

	public static java.math.BigInteger fromString(String value) {
		byte[] bytes = toByteArray(value);
		java.math.BigInteger number = new java.math.BigInteger(bytes);
		return number;
	}

	public static java.math.BigInteger fromHexString(String value) {
		return new java.math.BigInteger(value, 16);
	}

	public static String toString(java.math.BigInteger value) {
		byte[] bytes = value.toByteArray();
		String str = fromByteArray(bytes);
		return str;
	}

	private static byte[] toByteArray(String value) {
		char[] chars = value.toCharArray();
		byte[] bytes = new byte[chars.length];
		for (int i = 0; i < chars.length; i++) {
			bytes[i] = (byte) chars[i];
		}
		return bytes;
	}

	private static String fromByteArray(byte[] value) {
		byte[] bytes = value;
		char[] chars = new char[bytes.length];
		for (int i = 0; i < bytes.length; i++) {
			chars[i] = (char) bytes[i];
		}
		return String.valueOf(chars);
	}

}
