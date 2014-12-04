package org.ripple.power.nodejs;

import org.ripple.power.collection.LongArray;

public class HMAC {
	
	private SHA512[] _baseHash;

	private SHA512 _resultHash;
	
	private boolean _updated = false;

	public HMAC(LongArray key) {
		int i = 0;
		int bs = SHA512.blockSize / 32;
		long[][] exKey = new long[2][bs];
		this._baseHash = new SHA512[] { new SHA512(), new SHA512() };
		if (key.length > bs) {
			key = SHA512.hash(key);
		}
		for (i = 0; i < bs; i++) {
			exKey[0][i] = (key.get(i) ^ 0x36363636);
			exKey[1][i] = (key.get(i) ^ 0x5C5C5C5C);
		}
		this._baseHash[0].update(exKey[0]);
		this._baseHash[1].update(exKey[1]);
		this._resultHash = this._baseHash[0];
	}
	
	public LongArray mac(Object data) {
		return encrypt(data);
	}
	
	public LongArray encrypt(Object data) {
		if (!this._updated) {
			this.update(data);
			return this.digest();
		} else {
			throw new RuntimeException(
					"encrypt on already updated hmac called!");
		}
	}

	public void reset() {
		this._updated = false;
		this._resultHash = this._baseHash[0];
	}

	public void update(Object d) {
		this._updated = true;
		this._resultHash.update(d);
	}

	public LongArray digest() {
		LongArray w = this._resultHash._finalize();
		LongArray result = this._baseHash[1].update(w)._finalize();
		this.reset();
		return result;
	}
}
