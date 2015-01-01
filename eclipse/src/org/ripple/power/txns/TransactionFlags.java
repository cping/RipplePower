package org.ripple.power.txns;

public class TransactionFlags {
	// Universal flags can apply to any transaction type
	public static class Universal {
		public static long FullyCanonicalSig = 0x80000000;
	}

	public static class AccountSet {
		public static long RequireDestTag = 0x00010000,
				OptionalDestTag = 0x00020000, RequireAuth = 0x00040000,
				OptionalAuth = 0x00080000, DisallowXRP = 0x00100000,
				AllowXRP = 0x00200000;
	}

	public static class TrustSet {
		public static long SetAuth = 0x00010000, NoRipple = 0x00020000,
				SetNoRipple = 0x00020000, ClearNoRipple = 0x00040000,
				SetFreeze = 0x00100000, ClearFreeze = 0x00200000;
	}

	public static class OfferCreate {
		public static long Passive = 0x00010000,
				ImmediateOrCancel = 0x00020000, FillOrKill = 0x00040000,
				Sell = 0x00080000;
	}

	public static class Payment {
		public static long NoRippleDirect = 0x00010000,
				PartialPayment = 0x00020000, LimitQuality = 0x00040000;
	}
}
