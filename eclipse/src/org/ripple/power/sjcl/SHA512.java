package org.ripple.power.sjcl;

import java.math.BigDecimal;

import org.ripple.power.collection.LongArray;

public class SHA512 {

	public static LongArray hash(LongArray data) {
		SHA512 sha = new SHA512();
		if (sha._key == null || sha._key.length == 0) {
			sha._precompute();
		}
		return sha.update(data)._finalize();
	}

	long[] _keyr = new long[] { 0x28ae22, 0xef65cd, 0x4d3b2f, 0x89dbbc,
			0x48b538, 0x05d019, 0x194f9b, 0x6d8118, 0x030242, 0x706fbe,
			0xe4b28c, 0xffb4e2, 0x7b896f, 0x1696b1, 0xc71235, 0x692694,
			0xf14ad2, 0x4f25e3, 0x8cd5b5, 0xac9c65, 0x2b0275, 0xa6e483,
			0x41fbd4, 0x1153b5, 0x66dfab, 0xb43210, 0xfb213f, 0xef0ee4,
			0xa88fc2, 0x0aa725, 0x03826f, 0x0e6e70, 0xd22ffc, 0x26c926,
			0xc42aed, 0x95b3df, 0xaf63de, 0x77b2a8, 0xedaee6, 0x82353b,
			0xf10364, 0x423001, 0xf89791, 0x54be30, 0xef5218, 0x65a910,
			0x71202a, 0xbbd1b8, 0xd2d0c8, 0x41ab53, 0x8eeb99, 0x9b48a8,
			0xc95a63, 0x418acb, 0x63e373, 0xb2b8a3, 0xefb2fc, 0x172f60,
			0xf0ab72, 0x6439ec, 0x631e28, 0x82bde9, 0xc67915, 0x72532b,
			0x26619c, 0xc0c207, 0xe0eb1e, 0x6ed178, 0x176fba, 0xc898a6,
			0xf90dae, 0x1c471b, 0x047d84, 0xc72493, 0xc9bebc, 0x100d4c,
			0x3e42b6, 0x657e2a, 0xd6faec, 0x475817 };

	long[] _initr = new long[] { 0xbcc908, 0xcaa73b, 0x94f82b, 0x1d36f1,
			0xe682d1, 0x3e6c1f, 0x41bd6b, 0x7e2179 };

	int blockSize = 1024;

	LongArray _init = null;

	LongArray _key = null;

	LongArray _h = null;

	LongArray _buffer = null;

	int _length = 0;

	public SHA512 reset() {
		this._h = this._init.slice(0);
		this._buffer = new LongArray();
		this._length = 0;
		return this;
	}

	public SHA512 update(Object d) {
		LongArray data = null;
		if (d instanceof String) {
			data = BigNumber.utf8_toBits((String) d);
		} else {
			data = (LongArray) d;
		}

		int i;
		if (_buffer == null) {
			_buffer = new LongArray();
		}
		LongArray b = this._buffer = BitArray.concat(this._buffer, data);
		
		int ol = this._length, nl = this._length = ol
				+ (int) BitArray.bitLength(data);

		for (i = 1024 + ol & -1024; i <= nl; i += 1024) {
			this._block(b.splice(0, 32));
		}

		return this;
	}

	public LongArray _finalize() {
		int i;
		LongArray b = this._buffer, h = this._h;

	
		long[] a = new long[] { BitArray.partial(1, 1) };
		
		b = BitArray.concat(b, new LongArray(a));

		for (i = b.length + 4; (i & 31) > 0; i++) {
			b.push(0);
		}

		b.push(0);
		b.push(0);
		b.push((long) Math.floor(this._length / 0x100000000l));
		b.push(this._length | 0);
		while (b.length > 0) {
			this._block(b.splice(0, 32));
		}

		this.reset();
		return h;
	}

	public long frac(double x) {
		double math_floor = Math.floor(x);
		BigDecimal intx = new BigDecimal(x - math_floor);
		intx = intx.multiply(BigDecimal.valueOf(0x100000000l));
		return intx.longValue() | 0;
	}

	public long frac2(double x) {
		double math_floor = Math.floor(x);
		BigDecimal intx = new BigDecimal(x - math_floor);
		intx = intx.multiply(BigDecimal.valueOf(0x10000000000l));
		return intx.longValue() & 0xff;
	}

	public void _precompute() {

		int i = 0, prime = 2, factor;
		outer: for (; i < 80; prime++) {
			for (factor = 2; factor * factor <= prime; factor++) {
				if (prime % factor == 0) {
					continue outer;
				}
			}
			if (i < 8) {
				if (_init == null) {
					_init = new LongArray();
				}
				this._init.set(i * 2, frac(Math.pow(prime, 1f / 2f)));
				this._init.set(i * 2 + 1,
						(frac2((long) Math.pow(prime, 1 / 2)) << 24)
								| this._initr[i]);
			}
			if (_key == null) {
				_key = new LongArray();
			}
			this._key.set(i * 2, frac((long) Math.pow(prime, 1 / 3)));
			this._key.set(i * 2 + 1,
					(frac2((long) Math.pow(prime, 1 / 3)) << 24)
							| this._keyr[i]);
			i++;
		}
	}

	public void _block(LongArray words) {

		int i;
		long wrh, wrl;
		LongArray w = words.slice(0);

		if (_h == null) {
			_h = new LongArray();
		}
		if (_key == null) {
			_key = null;
		}
		LongArray h = this._h;
		LongArray k = this._key;
		long h0h = h.items[0], h0l = h.items[1], h1h = h.items[2], h1l = h.items[3], h2h = h.items[4], h2l = h.items[5], h3h = h.items[6], h3l = h.items[7], h4h = h.items[8], h4l = h.items[9], h5h = h.items[10], h5l = h.items[11], h6h = h.items[12], h6l = h.items[13], h7h = h.items[14], h7l = h.items[15];

		long ah = h0h, al = h0l, bh = h1h, bl = h1l, ch = h2h, cl = h2l, dh = h3h, dl = h3l, eh = h4h, el = h4l, fh = h5h, fl = h5l, gh = h6h, gl = h6l, hh = h7h, hl = h7l;

		for (i = 0; i < 80; i++) {
			if (i < 16) {
				wrh = w.items[i * 2];
				wrl = w.items[i * 2 + 1];
			} else {
				long gamma0xh = w.items[(i - 15) * 2];
				long gamma0xl = w.items[(i - 15) * 2 + 1];
				long gamma0h = ((gamma0xl << 31) | (gamma0xh >>> 1))
						^ ((gamma0xl << 24) | (gamma0xh >>> 8))
						^ (gamma0xh >>> 7);
				long gamma0l = ((gamma0xh << 31) | (gamma0xl >>> 1))
						^ ((gamma0xh << 24) | (gamma0xl >>> 8))
						^ ((gamma0xh << 25) | (gamma0xl >>> 7));

				long gamma1xh = w.items[(i - 2) * 2];
				long gamma1xl = w.items[(i - 2) * 2 + 1];
				long gamma1h = ((gamma1xl << 13) | (gamma1xh >>> 19))
						^ ((gamma1xh << 3) | (gamma1xl >>> 29))
						^ (gamma1xh >>> 6);
				long gamma1l = ((gamma1xh << 13) | (gamma1xl >>> 19))
						^ ((gamma1xl << 3) | (gamma1xh >>> 29))
						^ ((gamma1xh << 26) | (gamma1xl >>> 6));

				long wr7h = w.items[(i - 7) * 2];
				long wr7l = w.items[(i - 7) * 2 + 1];

				long wr16h = w.items[(i - 16) * 2];
				long wr16l = w.items[(i - 16) * 2 + 1];

				wrl = gamma0l + wr7l;
				wrh = gamma0h + wr7h + ((wrl >>> 0) < (gamma0l >>> 0) ? 1 : 0);
				wrl += gamma1l;
				wrh += gamma1h + ((wrl >>> 0) < (gamma1l >>> 0) ? 1 : 0);
				wrl += wr16l;
				wrh += wr16h + ((wrl >>> 0) < (wr16l >>> 0) ? 1 : 0);
			}

			w.set(i * 2, wrh |= 0);
			w.set(i * 2 + 1, wrl |= 0);

			long chh = (eh & fh) ^ (~eh & gh);
			long chl = (el & fl) ^ (~el & gl);

			long majh = (ah & bh) ^ (ah & ch) ^ (bh & ch);
			long majl = (al & bl) ^ (al & cl) ^ (bl & cl);

			long sigma0h = ((al << 4) | (ah >>> 28))
					^ ((ah << 30) | (al >>> 2)) ^ ((ah << 25) | (al >>> 7));
			long sigma0l = ((ah << 4) | (al >>> 28))
					^ ((al << 30) | (ah >>> 2)) ^ ((al << 25) | (ah >>> 7));

			long sigma1h = ((el << 18) | (eh >>> 14))
					^ ((el << 14) | (eh >>> 18)) ^ ((eh << 23) | (el >>> 9));
			long sigma1l = ((eh << 18) | (el >>> 14))
					^ ((eh << 14) | (el >>> 18)) ^ ((el << 23) | (eh >>> 9));

			long krh = k.get(i * 2);
			long krl = k.get(i * 2 + 1);

			long t1l = hl + sigma1l;
			long t1h = hh + sigma1h + ((t1l >>> 0) < (hl >>> 0) ? 1 : 0);
			t1l += chl;
			t1h += chh + ((t1l >>> 0) < (chl >>> 0) ? 1 : 0);
			t1l += krl;
			t1h += krh + ((t1l >>> 0) < (krl >>> 0) ? 1 : 0);
			t1l = t1l + wrl | 0;
			t1h += wrh + ((t1l >>> 0) < (wrl >>> 0) ? 1 : 0);

			long t2l = sigma0l + majl;
			long t2h = sigma0h + majh + ((t2l >>> 0) < (sigma0l >>> 0) ? 1 : 0);

			hh = gh;
			hl = gl;
			gh = fh;
			gl = fl;
			fh = eh;
			fl = el;
			el = (dl + t1l) | 0;
			eh = (dh + t1h + ((el >>> 0) < (dl >>> 0) ? 1 : 0)) | 0;
			dh = ch;
			dl = cl;
			ch = bh;
			cl = bl;
			bh = ah;
			bl = al;
			al = (t1l + t2l) | 0;
			ah = (t1h + t2h + ((al >>> 0) < (t1l >>> 0) ? 1 : 0)) | 0;
		}

		h0l = h.items[1] = (h0l + al) | 0;
		h.items[0] = (h0h + ah + ((h0l >>> 0) < (al >>> 0) ? 1 : 0)) | 0;
		h1l = h.items[3] = (h1l + bl) | 0;
		h.items[2] = (h1h + bh + ((h1l >>> 0) < (bl >>> 0) ? 1 : 0)) | 0;
		h2l = h.items[5] = (h2l + cl) | 0;
		h.items[4] = (h2h + ch + ((h2l >>> 0) < (cl >>> 0) ? 1 : 0)) | 0;
		h3l = h.items[7] = (h3l + dl) | 0;
		h.items[6] = (h3h + dh + ((h3l >>> 0) < (dl >>> 0) ? 1 : 0)) | 0;
		h4l = h.items[9] = (h4l + el) | 0;
		h.items[8] = (h4h + eh + ((h4l >>> 0) < (el >>> 0) ? 1 : 0)) | 0;
		h5l = h.items[11] = (h5l + fl) | 0;
		h.items[10] = (h5h + fh + ((h5l >>> 0) < (fl >>> 0) ? 1 : 0)) | 0;
		h6l = h.items[13] = (h6l + gl) | 0;
		h.items[12] = (h6h + gh + ((h6l >>> 0) < (gl >>> 0) ? 1 : 0)) | 0;
		h7l = h.items[15] = (h7l + hl) | 0;
		h.items[14] = (h7h + hh + ((h7l >>> 0) < (hl >>> 0) ? 1 : 0)) | 0;
	}
}
