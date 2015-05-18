package org.ripple.power.nodejs;

public class Curve {

	final static Curve c192 = new Curve(new Field(),
			"0xffffffffffffffffffffffff99def836146bc9b1b4d22831", -3,
			"0x64210519e59c80e70fa7e9ab72243049feb8deecc146b9b1",
			"0x188da80eb03090f67cbf20eb43a18800f4ff0afd82ff1012",
			"0x188da80eb03090f67cbf20eb43a18800f4ff0afd82ff1012");

	final static Curve c224 = new Curve(new Field(),
			"0xffffffffffffffffffffffffffff16a2e0b8f03e13dd29455c5c2a3d", -3,
			"0xb4050a850c04b3abf54132565044b0b7d7bfd8ba270b39432355ffb4",
			"0xb70e0cbd6bb4bf7f321390b94a03c1d356c21122343280d6115c1d21",
			"0xbd376388b5f723fb4c22dfe6cd4375a05a07476444d5819985007e34");

	final static Curve c256 = new Curve(
			new Field(),
			"0xffffffff00000000ffffffffffffffffbce6faada7179e84f3b9cac2fc632551",
			-3,
			"0x5ac635d8aa3a93e7b3ebbd55769886bc651d06b0cc53b0f63bce3c3e27d2604b",
			"0x6b17d1f2e12c4247f8bce6e563a440f277037d812deb33a0f4a13945d898c296",
			"0x4fe342e2fe1a7f9b8ee7eb4a7c0f9e162bce33576b315ececbb6406837bf51f5");

	final static Curve c384 = new Curve(
			new Field(),
			"0xffffffffffffffffffffffffffffffffffffffffffffffffc7634d81f4372ddf581a0db248b0a77aecec196accc52973",
			-3,
			"0xb3312fa7e23ee7e4988e056be3f82d19181d9c6efe8141120314088f5013875ac656398d8a2ed19d2a85c8edd3ec2aef",
			"0xaa87ca22be8b05378eb1c71ef320ad746e1d3b628ba79b9859f741e082542a385502f25dbf55296c3a545e3872760ab7",
			"0x3617de4a96262c6f5d9e98bf9292dc29f8f41dbd289a147ce9da3113b5f0b8c00a60b1ce1d7e819d7a431d7c90ea0e5f");

	final static Curve k192 = new Curve(new Field(),
			"0xfffffffffffffffffffffffe26f2fc170f69466a74defd8d", 0, 3,
			"0xdb4ff10ec057e9ae26b07d0280b7f4341da5d1b1eae06c7d",
			"0x9b2f2f6d9c5628a7844163d015be86344082aa88d95e2f9d");

	final static Curve k224 = new Curve(new Field(),
			"0x010000000000000000000000000001dce8d2ec6184caf0a971769fb1f7", 0,
			5, "0xa1455b334df099df30fc28a169a467e9e47075a90f7e650eb6b7a45c",
			"0x7e089fed7fba344282cafbd6f7e319f7c0b0bd59e2ca4bdb556d61a5");

	final static Curve k256 = new Curve(
			new Field(),
			"0xfffffffffffffffffffffffffffffffebaaedce6af48a03bbfd25e8cd0364141",
			0,
			7,
			"0x79be667ef9dcbbac55a06295ce870b07029bfcdb2dce28d959f2815b16f81798",
			"0x483ada7726a3c4655da4fbfc0e1108a8fd17b448a68554199c47d08ffb10d4b8");

	public static class Field {

	}

	public Curve(Field f, Object r, Object a, Object b, Object x, Object y) {
		/*
		 * this.r = new sjcl.bn(r); this.a = new Field(a); this.b = new
		 * Field(b); this.G = new sjcl.ecc.point(this, new Field(x), new
		 * Field(y));
		 */
	}
}
