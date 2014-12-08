package org.ripple.power.nodejs;

import org.ripple.power.collection.LongArray;

public class AES {

	long[][][] _tables = {
			{ new long[256], new long[256], new long[256], new long[256],
					new long[256] },
			{ new long[256], new long[256], new long[256], new long[256],
					new long[256] } };

	long[][] _key;

	public AES(long[] key) {

		_precompute();
		int i = 0, j = 0;

		long tmp;
		long[] encKey, decKey;
		long[] sbox = this._tables[0][4];
		long[][] decTable = this._tables[1];
		int keyLen = key.length, rcon = 1;
		if (keyLen != 4 && keyLen != 6 && keyLen != 8) {
			throw new RuntimeException("invalid aes key size");
		}
		encKey = LongArray.slice(key, 0);
		decKey = new long[encKey.length];
		this._key = new long[][] { encKey, decKey };

		for (i = keyLen; i < 4 * keyLen + 28; i++) {
			tmp = encKey[i - 1];
			if (i % keyLen == 0 || (keyLen == 8 && i % keyLen == 4)) {
				tmp = sbox[(int) JS.MOVE_RightUShift(tmp, 24)] << 24
						^ sbox[(int) (tmp >> 16 & 255)] << 16
						^ sbox[(int) (tmp >> 8 & 255)] << 8
						^ sbox[(int) (tmp & 255)];

				if (i % keyLen == 0) {
					tmp = tmp << 8 ^ tmp >>> 24 ^ rcon << 24;
					rcon = rcon << 1 ^ (rcon >> 7) * 283;
				}
			}
			encKey[i] = encKey[i - keyLen] ^ tmp;
		}
		for (j = 0; i > 0; j++, i--) {
			tmp = encKey[(j & 3) > 0 ? i : i - 4];
			if (i <= 4 || j < 4) {
				decKey[j] = tmp;
			} else {
				decKey[j] = decTable[0][(int) sbox[(int) JS.MOVE_RightUShift(
						tmp, 24)]]
						^ decTable[1][(int) sbox[(int) (tmp >> 16 & 255)]]
						^ decTable[2][(int) sbox[(int) (tmp >> 8 & 255)]]
						^ decTable[3][(int) sbox[(int) (tmp & 255)]];
			}
		}
	}

	public void _precompute() {

		long[][] encTable = this._tables[0];
		long[][] decTable = this._tables[1];

		long[] sbox = encTable[4];
		long[] sboxInv = decTable[4];
		int i, x, xInv;
		long[] d = new long[256];
		long[] th = new long[256];
		int x2, x4, x8, s, tEnc, tDec;
		for (i = 0; i < 256; i++) {
			th[(int) ((d[i] = i << 1 ^ (i >> 7) * 283) ^ i)] = i;
		}

		for (x = xInv = 0; !(sbox[x] > 0); x ^= JS.OR(x2, 1).intValue(), xInv = JS
				.OR(th[xInv], 1).intValue()) {

			s = xInv ^ xInv << 1 ^ xInv << 2 ^ xInv << 3 ^ xInv << 4;
			s = s >> 8 ^ s & 255 ^ 99;
			sbox[x] = s;
			sboxInv[s] = x;

			x8 = (int) d[x4 = (int) d[x2 = (int) d[x]]];
			tDec = x8 * 0x1010101 ^ x4 * 0x10001 ^ x2 * 0x101 ^ x * 0x1010100;
			tEnc = (int) (d[s] * 0x101 ^ s * 0x1010100);

			for (i = 0; i < 4; i++) {
				encTable[i][x] = tEnc = tEnc << 24 ^ tEnc >>> 8;
				decTable[i][s] = tDec = tDec << 24 ^ tDec >>> 8;
			}
		}

		for (i = 0; i < 5; i++) {
			encTable[i] = LongArray.slice(encTable[i], 0);
			decTable[i] = LongArray.slice(decTable[i], 0);
		}
	}

	public long[] encrypt(long[] data) {
		return this._crypt(data, 0);
	}

	public long[] decrypt(long[] data) {
		return this._crypt(data, 1);
	}

	public long[] _crypt(long[] input, int dir) {
		if (input.length != 4) {
			throw new RuntimeException("invalid aes block size");
		}

		long[] key = this._key[dir];

		long a = input[0] ^ key[0], b = input[dir > 0 ? 3 : 1] ^ key[1], c = input[2]
				^ key[2], d = input[dir > 0 ? 1 : 3] ^ key[3], a2, b2, c2,

		nInnerRounds = key.length / 4 - 2, i;
		int kIndex = 4;
		long[] out = { 0, 0, 0, 0 };
		long[][] table = this._tables[dir];

		long[] t0 = table[0], t1 = table[1], t2 = table[2], t3 = table[3], sbox = table[4];

		for (i = 0; i < nInnerRounds; i++) {
			a2 = t0[(int) JS.MOVE_RightUShift(a, 24)]
					^ t1[(int) (b >> 16 & 255)] ^ t2[(int) (c >> 8 & 255)]
					^ t3[(int) (d & 255)] ^ key[kIndex];
			b2 = t0[(int) JS.MOVE_RightUShift(b, 24)]
					^ t1[(int) (c >> 16 & 255)] ^ t2[(int) (d >> 8 & 255)]
					^ t3[(int) (a & 255)] ^ key[kIndex + 1];
			c2 = t0[(int) JS.MOVE_RightUShift(c, 24)]
					^ t1[(int) (d >> 16 & 255)] ^ t2[(int) (a >> 8 & 255)]
					^ t3[(int) (b & 255)] ^ key[kIndex + 2];
			d = t0[(int) JS.MOVE_RightUShift(d, 24)]
					^ t1[(int) (a >> 16 & 255)] ^ t2[(int) (b >> 8 & 255)]
					^ t3[(int) (c & 255)] ^ key[kIndex + 3];
			kIndex += 4;
			a = a2;
			b = b2;
			c = c2;
		}

		for (i = 0; i < 4; i++) {
			out[(int) (dir > 0 ? 3 & -i : i)] = sbox[(int) JS.MOVE_RightUShift(
					a, 24)] << 24
					^ sbox[(int) (b >> 16 & 255)] << 16
					^ sbox[(int) (c >> 8 & 255)] << 8
					^ sbox[(int) (d & 255)]
					^ key[kIndex++];
			a2 = a;
			a = b;
			b = c;
			c = d;
			d = a2;
		}

		return out;
	}

}
