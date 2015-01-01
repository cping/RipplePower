package org.ripple.power.txns;

public class LedgerEntryFlags {

	public static class Account_root {
		public static long PasswordSpent = 0x00010000, // True, if password set
														// fee is
				// spent.
				RequireDestTag = 0x00020000, // True, to require a
												// DestinationTag for payments.
				RequireAuth = 0x00040000, // True, to require a authorization to
											// hold IOUs.
				DisallowXRP = 0x00080000, // True, to disallow sending XRP.
				DisableMaster = 0x00100000; // True, force regular key.
	}

	public static class Offer {
		public static long Passive = 0x00010000, Sell = 0x00020000;// True,
																	// offer
																	// was
																	// placed as
																	// a sell.
	}

	public static class State {
		public static long LowReserve = 0x00010000, // True, if entry counts
													// toward reserve.
				HighReserve = 0x00020000,
				LowAuth = 0x00040000,
				HighAuth = 0x00080000,
				LowNoRipple = 0x00100000,
				HighNoRipple = 0x00200000;
	}

}
