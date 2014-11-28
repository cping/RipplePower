package org.ripple.power.sjcl;

import java.util.ArrayList;

import org.ripple.power.collection.Array;
import org.ripple.power.collection.IntArray;

public class BigNumber {

	class Montgomery {

		BigNumber m;

		int mt, mt2, mp, mpl, mph, um;

		public Montgomery(BigNumber m) {
			this.m = m;
			this.mt = m.limbs.length;
			this.mt2 = this.mt * 2;
			this.mp = m.invDigit();
			this.mpl = this.mp & 0x7fff;
			this.mph = this.mp >> 15;
			this.um = (1 << (m.radix - 15)) - 1;
		}

		public BigNumber reduce(BigNumber x) {
			int radixMod = x.radixMask + 1;
			while (x.limbs.length <= this.mt2) {
				x.limbs.set(x.limbs.length, 0);
			}
			for (int i = 0; i < this.mt; ++i) {
				int j = x.limbs.get(i) & 0x7fff;
				int u0 = (j * this.mpl + (((j * this.mph + (x.limbs.get(i) >> 15)
						* this.mpl) & this.um) << 15))
						& x.radixMask;
				j = i + this.mt;
				x.limbs.set(j,
						x.limbs.get(j) + this.m.am(0, u0, x, i, 0, this.mt));
				while (x.limbs.get(j) >= radixMod) {
					x.limbs.set(j, x.limbs.get(j) - radixMod);
					int idx = ++j;
					x.limbs.set(idx, x.limbs.get(idx) + 1);
				}
			}
			x.trim();
			x = x.shiftRight(this.mt * this.m.radix);
			if (x.greaterEquals(this.m) > 0) {
				x = x.sub(this.m);
			}
			return x.trim().normalize().reduce();
		}

		public BigNumber square(BigNumber x) {
			return this.reduce(x.square());
		}

		public BigNumber multiply(BigNumber x, BigNumber y) {
			return this.reduce(x.mul(y));
		}

		public BigNumber convert(BigNumber x) {
			return x.abs().shiftLeft(this.mt * this.m.radix).mod(this.m);
		}

		public BigNumber revert(BigNumber x) {
			return this.reduce(x.copy());
		};

	}

	public BigNumber ZERO = new BigNumber(0);
	public int radix = 24;
	public int maxMul = 8;
	IntArray limbs;
	public int placeVal = (int) Math.pow(2, radix);
	public int ipv = 1 / placeVal;
	public int radixMask = (1 << radix) - 1;
	public int exponent;

	public BigNumber(Object it) {
		initWith(it);
	}

	public BigNumber() {
		initWith(null);
	}

	public IntArray get() {
		return limbs;
	}

	public BigNumber copy() {
		return new BigNumber(this);
	}

	public String toString() {
		this.normalize();
		String out = "";
		int i;
		String s;
		IntArray l = this.limbs;
		for (i = 0; i < this.limbs.length; i++) {
			s = Long.toHexString(l.get(i));
			while (i < this.limbs.length - 1 && s.length() < 6) {
				s = "0" + s;
			}
			out = s + out;
		}
		return "0x" + out;
	}

	public BigNumber normalize() {
		int carry = 0, i, pv = this.placeVal, ipv = this.ipv, l, m, ll = limbs.length, mask = this.radixMask;
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

	public boolean equals(BigNumber that) {
		int difference = 0, i;
		this.fullReduce();
		that.fullReduce();
		for (i = 0; i < this.limbs.length || i < that.limbs.length; i++) {
			difference |= this.getLimb(i) ^ that.getLimb(i);
		}
		return (difference == 0);
	}

	public boolean equals(Object that) {
		if (that == null) {
			return false;
		}
		BigNumber newthat = new BigNumber(that);
		return equals(newthat);
	}

	public int greaterEquals(BigNumber that) {
		int less = 0, greater = 0, i, a, b;
		i = Math.max(this.limbs.length, that.limbs.length) - 1;
		for (; i >= 0; i--) {
			a = this.getLimb(i);
			b = that.getLimb(i);
			greater |= (b - a) & ~less;
			less |= (a - b) & ~greater;
		}
		return (greater | ~less) >>> 31;
	}

	public BigNumber[] divRem(BigNumber that) {
		BigNumber thisa = this.abs(), thata = that.abs(), quot = new BigNumber(
				0);
		int ci = 0;
		if (!(thisa.greaterEquals(thata) > 0)) {
			return new BigNumber[] { new BigNumber(0), this.copy() };
		} else if (thisa.equals(thata)) {
			return new BigNumber[] { new BigNumber(1), new BigNumber(0) };
		}

		for (; thisa.greaterEquals(thata) > 0; ci++) {
			thata.doubleM();
		}
		for (; ci > 0; ci--) {
			quot.doubleM();
			thata.halveM();
			if (thisa.greaterEquals(thata) > 0) {
				quot.addM(new BigNumber(1));
				thisa.subM(that).normalize();
			}
		}
		return new BigNumber[] { quot, thisa };
	}

	public BigNumber divRound(BigNumber that) {
		BigNumber[] dr = this.divRem(that);
		BigNumber quot = dr[0];
		BigNumber rem = dr[1];
		if (rem.doubleM().greaterEquals(that) > 0) {
			quot.addM(new BigNumber(1));
		}
		return quot;
	}

	public BigNumber div(BigNumber that) {
		BigNumber[] dr = this.divRem(that);
		return dr[0];
	}

	public BigNumber addM(BigNumber that) {
		int i = 0;
		IntArray l = this.limbs, ll = that.limbs;
		for (i = l.length; i < ll.length; i++) {
			l.set(i, 0);
		}
		for (i = 0; i < ll.length; i++) {
			l.set(i, l.get(i) + ll.get(i));
		}
		return this;
	}

	public BigNumber doubleM() {
		int i, carry = 0, tmp, r = this.radix, m = this.radixMask;
		IntArray l = this.limbs;
		for (i = 0; i < l.length; i++) {
			tmp = l.get(i);
			tmp = tmp + tmp + carry;
			l.set(i, tmp & m);
			carry = tmp >> r;
		}
		if (carry > 0) {
			l.add(carry);
		}
		return this;
	}

	public BigNumber halveM() {
		int i, carry = 0, tmp, r = this.radix;
		IntArray l = this.limbs;
		for (i = l.length - 1; i >= 0; i--) {
			tmp = l.get(i);
			l.set(i, (tmp + carry) >> 1);
			carry = (tmp & 1) << r;
		}
		if (!(l.get(l.length - 1) > 0)) {
			l.pop();
		}
		return this;
	}

	public BigNumber subM(BigNumber that) {
		int i;
		IntArray l = this.limbs, ll = that.limbs;
		for (i = l.length; i < ll.length; i++) {
			l.set(i, 0);
		}
		for (i = 0; i < ll.length; i++) {
			l.set(i, l.get(i) - ll.get(i));
		}
		return this;
	}

	public BigNumber trim() {
		IntArray l = this.limbs;
		int p;
		do {
			p = l.pop();
		} while (l.length > 0 && p == 0);
		l.push(p);
		return this;
	}

	public BigNumber mod(BigNumber that) {
		boolean neg = !(this.greaterEquals(that) > 0);
		that = new BigNumber(that).normalize();
		BigNumber out = new BigNumber(this).normalize();
		int ci = 0;
		if (neg) {
			out = (new BigNumber(0)).subM(out).normalize();
		}
		for (; out.greaterEquals(that) > 0; ci++) {
			that.doubleM();
		}
		if (neg) {
			out = that.sub(out).normalize();
		}
		for (; ci > 0; ci--) {
			that.halveM();
			if (out.greaterEquals(that) > 0) {
				out.subM(that).normalize();
			}
		}
		return out.trim();
	}

	public BigNumber add(BigNumber that) {
		return this.copy().addM(that);
	}

	public BigNumber sub(BigNumber that) {
		return this.copy().subM(that);
	}

	public BigNumber inverseMod(BigNumber p) {
		BigNumber a = new BigNumber(1), b = new BigNumber(0), x = new BigNumber(
				this), y = new BigNumber(p), tmp;
		int i, nz = 1;
		if (!((p.limbs.get(0) & 1) > 0)) {
			throw new RuntimeException("inverseMod: p must be odd");
		}
		do {
			if ((x.limbs.get(0) & 1) > 0) {
				if (!(x.greaterEquals(y) > 0)) {
					tmp = x;
					x = y;
					y = tmp;
					tmp = a;
					a = b;
					b = tmp;
				}
				x.subM(y);
				x.normalize();
				if (!(a.greaterEquals(b) > 0)) {
					a.addM(p);
				}
				a.subM(b);
			}

			x.halveM();
			if ((a.limbs.get(0) & 1) > 0) {
				a.addM(p);
			}
			a.normalize();
			a.halveM();

			for (i = nz = 0; i < x.limbs.length; i++) {
				nz |= x.limbs.get(i);
			}
		} while (nz > 0);

		if (!y.equals(1)) {
			throw new RuntimeException(
					"inverseMod: p and x must be relatively prime");
		}

		return b;
	}

	public BigNumber mul(BigNumber that) {
		int i, j;
		IntArray a = this.limbs, b = that.limbs;
		int al = a.length, bl = b.length;
		BigNumber out = new BigNumber(this);
		IntArray c = out.limbs;
		int ai, ii = this.maxMul;
		for (i = 0; i < this.limbs.length + that.limbs.length + 1; i++) {
			c.set(i, 0);
		}
		for (i = 0; i < al; i++) {
			ai = a.get(i);
			for (j = 0; j < bl; j++) {
				c.set(i + j, c.get(i + j) + (ai * b.get(j)));
			}
			if (!(--ii > 0)) {
				ii = this.maxMul;
				out.cnormalize();
			}
		}
		return out.cnormalize().reduce();
	}

	public BigNumber square() {
		return this.mul(this);
	}

	public BigNumber power(BigNumber l) {
		IntArray nl = l.normalize().limbs;
		int i, j;
		BigNumber out = new BigNumber(1), pow = this;
		for (i = 0; i < nl.length; i++) {
			for (j = 0; j < this.radix; j++) {
				if ((nl.get(i) & (1 << j)) > 0) {
					out = out.mul(pow);
				}
				pow = pow.square();
			}
		}
		return out;
	}

	public BigNumber reduce() {
		return this;
	}

	public BigNumber mulmod(BigNumber that, BigNumber N) {
		return this.mod(N).mul(that.mod(N)).mod(N);
	}

	public int bitLength() {
		this.fullReduce();
		int out = this.radix * (this.limbs.length - 1), b = this.limbs
				.get(this.limbs.length - 1);
		for (; b > 0; b >>>= 1) {
			out++;
		}
		return out + 7 & -8;
	}

	public IntArray toBits(int len) {
		this.fullReduce();
		len = len | this.exponent | this.bitLength();
		int i = (int) Math.floor((len - 1) / 24);
		int e = (len + 7 & -8) % this.radix | this.radix;
		IntArray out = new IntArray(new int[] { BitArray.partial(e,
				this.getLimb(i), 0) });
		for (i--; i >= 0; i--) {
			int[] arrays = new int[] { BitArray.partial(
					Math.min(this.radix, len), this.getLimb(i)) };
			out = BitArray.concat(out, new IntArray(arrays));
			len -= this.radix;
		}
		return out;
	}

	public int sign() {
		return this.greaterEquals(ZERO) > 0 ? 1 : -1;
	}

	public BigNumber shiftRight(int that) {
		that = +that;
		if (that < 0) {
			return this.shiftLeft(that);
		}
		BigNumber a = new BigNumber(this);
		while (that >= this.radix) {
			a.limbs.shift();
			that -= this.radix;
		}
		while ((that--) > 0) {
			a.halveM();
		}
		return a;
	}

	public BigNumber shiftLeft(int that) {
		that = +that;
		if (that < 0) {
			return shiftRight(that);
		}

		BigNumber a = new BigNumber(this);

		while (that >= this.radix) {
			a.limbs.unshift(0);
			that -= this.radix;
		}

		while ((that--) > 0) {
			a.doubleM();
		}

		return a;
	}

	public int toNumber() {
		return this.limbs.get(0) | 0;
	}

	public int testBit(int bitIndex) {
		int limbIndex = (int) Math.floor(bitIndex / this.radix);
		int bitIndexInLimb = bitIndex % this.radix;
		if (limbIndex >= this.limbs.length) {
			return 0;
		}
		return (this.limbs.get(limbIndex) >>> bitIndexInLimb) & 1;
	}

	public BigNumber setBitM(int bitIndex) {
		int limbIndex = (int) Math.floor(bitIndex / this.radix);
		int bitIndexInLimb = bitIndex % this.radix;
		while (limbIndex >= this.limbs.length) {
			this.limbs.push(0);
		}
		this.limbs.set(limbIndex, this.limbs.get(limbIndex)
				| 1 << bitIndexInLimb);
		this.cnormalize();
		return this;
	}

	public int modInt(int n) {
		return this.toNumber() % n;
	}

	public int jacobi(BigNumber that) {
		BigNumber a = this;
		that = new BigNumber(that);
		if (that.sign() == -1) {
			return -1;
		}
		if (a.equals(0)) {
			return 0;
		}
		if (a.equals(1)) {
			return 1;
		}

		int s = 0;

		int e = 0;
		while (!(a.testBit(e) > 0)) {
			e++;
		}
		BigNumber a1 = a.shiftRight(e);

		if ((e & 1) == 0) {
			s = 1;
		} else {
			int residue = that.modInt(8);
			if (residue == 1 || residue == 7) {
				s = 1;
			} else if (residue == 3 || residue == 5) {
				s = -1;
			}
		}
		if (that.modInt(4) == 3 && a1.modInt(4) == 3) {
			s = -s;
		}

		if (a1.equals(1)) {
			return s;
		} else {
			return s * that.mod(a1).jacobi(a1);
		}
	}

	public BigNumber powermod(BigNumber x, BigNumber N) {
		BigNumber result = new BigNumber(1), a = new BigNumber(this), k = new BigNumber(
				x);
		for (;;) {
			if ((k.limbs.get(0) & 1) > 0) {
				result = result.mulmod(a, N);
			}
			k.halveM();
			if (k.equals(0)) {
				break;
			}
			a = a.mulmod(a, N);
		}
		return result.normalize().reduce();
	}

	public BigNumber fromBits(IntArray bits) {
		BigNumber out = new BigNumber();
		IntArray words = new IntArray();
		int l = (int) Math.min(this.bitLength() | 0x100000000l,
				BitArray.bitLength(bits)), e = l % radix | radix;
		words.set(0, BitArray.extract(bits, 0, e));
		for (; e < l; e += radix) {
			words.unshift(BitArray.extract(bits, e, radix));
		}
		out.limbs = words;
		return out;
	}

	public BigNumber cnormalize() {
		int carry = 0, i, ipv = this.ipv, l, m;
		IntArray limbs = this.limbs;
		int ll = limbs.length;
		int mask = this.radixMask;
		for (i = 0; i < ll - 1; i++) {
			l = limbs.get(i) + carry;
			limbs.set(i, l & mask);
			m = limbs.get(i);
			carry = (l - m) * ipv;
		}
		limbs.set(i, limbs.get(i + carry));
		return this;
	}

	public int getLimb(int i) {
		return (i >= this.limbs.length) ? 0 : this.limbs.get(i);
	}

	public BigNumber fullReduce() {
		return this.normalize();
	}

	public int invDigit() {
		int radixMod = 1 + this.radixMask;

		if (this.limbs.length < 1) {
			return 0;
		}
		int x = this.limbs.get(0);
		if ((x & 1) == 0) {
			return 0;
		}
		int y = x & 3;
		y = (y * (2 - (x & 0xf) * y)) & 0xf;
		y = (y * (2 - (x & 0xff) * y)) & 0xff;
		y = (y * (2 - (((x & 0xffff) * y) & 0xffff))) & 0xffff;
		y = (y * (2 - x * y % radixMod)) % radixMod;
		return (y > 0) ? radixMod - y : -y;
	}

	public BigNumber neg() {
		return ZERO.sub(this);
	}

	public BigNumber abs() {
		if (this.sign() == -1) {
			return this.neg();
		} else
			return this;
	}

	public int nbits(int x) {
		int r = 1, t;
		if ((t = x >>> 16) != 0) {
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

	public int am(int i, int x, BigNumber w, int j, int c, int n) {
		int xl = x & 0xfff, xh = x >> 12;
		while (--n >= 0) {
			int l = this.limbs.get(i) & 0xfff;
			int h = this.limbs.get(i++) >> 12;
			int m = xh * l + h * xl;
			l = xl * l + ((m & 0xfff) << 12) + w.limbs.get(j) + c;
			c = (l >> 24) + (m >> 12) + xh * h;
			w.limbs.set(j++, (int) (l & 0xffffffl));
		}
		return c;
	}

	public void initWith(Object it) {
		if (it == null) {
			this.limbs = new IntArray();
			this.normalize();
			return;
		}
		int i = 0, k;
		if (it instanceof String) {
			String itStr = (String) it;
			it = itStr.replace("0x", "");
			this.limbs = new IntArray();
			k = this.radix / 4;
			for (i = 0; i < itStr.length(); i += k) {
				int v = Integer.parseInt(
						itStr.substring(Math.max(itStr.length() - i - k, 0),
								itStr.length() - i), 16);
				this.limbs.add(v);
			}
		} else if (it instanceof Array) {
			IntArray itArray = (IntArray) it;
			this.limbs = itArray.slice(0);
		} else if (it instanceof Number) {
			this.limbs = new IntArray(new int[] { ((Number) it).intValue() });
			this.normalize();
		}
	}

	public BigNumber powermodMontgomery(BigNumber e, BigNumber m) {
		int i = e.bitLength(), k;
		BigNumber r = new BigNumber(1);

		if (i <= 0) {
			return r;
		} else if (i < 18) {
			k = 1;
		} else if (i < 48) {
			k = 3;
		} else if (i < 144) {
			k = 4;
		} else if (i < 768) {
			k = 5;
		} else {
			k = 6;
		}

		if (i < 8 || !(m.testBit(0) > 0)) {
			return this.powermod(e, m);
		}

		Montgomery z = new Montgomery(m);

		e.trim().normalize();

		ArrayList<BigNumber> g = new ArrayList<BigNumber>();
		int n = 3, k1 = k - 1, km = (1 << k) - 1;
		g.set(1, z.convert(this));
		if (k > 1) {
			BigNumber g2 = z.square(g.get(1));
			while (n <= km) {
				g.set(n, z.multiply(g2, g.get(n - 2)));
				n += 2;
			}
		}

		int j = e.limbs.length - 1, w;
		boolean is1 = true;
		BigNumber r2 = new BigNumber(), t;
		i = nbits(e.limbs.get(j)) - 1;
		while (j >= 0) {
			if (i >= k1)
				w = (e.limbs.get(j) >> (i - k1)) & km;
			else {
				w = (e.limbs.get(j) & ((1 << (i + 1)) - 1)) << (k1 - i);
				if (j > 0)
					w |= e.limbs.get(j - 1) >> (this.radix + i - k1);
			}

			n = k;
			while ((w & 1) == 0) {
				w >>= 1;
				--n;
			}
			if ((i -= n) < 0) {
				i += this.radix;
				--j;
			}
			if (is1) {
				r = g.get(w).copy();
				is1 = false;
			} else {
				while (n > 1) {
					r2 = z.square(r);
					r = z.square(r2);
					n -= 2;
				}
				if (n > 0)
					r2 = z.square(r);
				else {
					t = r;
					r = r2;
					r2 = t;
				}
				r = z.multiply(r2, g.get(w));
			}

			while (j >= 0 && (e.limbs.get(j) & (1 << i)) == 0) {
				r2 = z.square(r);
				t = r;
				r = r2;
				r2 = t;
				if (--i < 0) {
					i = this.radix - 1;
					--j;
				}
			}
		}
		return z.revert(r);
	}
}
