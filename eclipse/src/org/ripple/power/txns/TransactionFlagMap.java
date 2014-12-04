package org.ripple.power.txns;

import java.util.ArrayList;
import java.util.HashMap;

import com.ripple.core.enums.TransactionFlag;

public class TransactionFlagMap {

	private final static HashMap<Long, String> _flags = new HashMap<Long, String>(
			30);
	static {
		// Constraints flags:
		_flags.put(TransactionFlag.FullyCanonicalSig, "FullyCanonicalSig");
		_flags.put(TransactionFlag.Universal, "Universal");
		_flags.put(TransactionFlag.UniversalMask, "UniversalMask");
		_flags.put(TransactionFlag.RequireDestTag, "RequireDestTag");
		_flags.put(TransactionFlag.OptionalDestTag, "OptionalDestTag");
		_flags.put(TransactionFlag.RequireAuth, "RequireAuth");
		_flags.put(TransactionFlag.OptionalAuth, "OptionalAuth");
		_flags.put(TransactionFlag.DisallowXRP, "DisallowXRP");
		_flags.put(TransactionFlag.AllowXRP, "AllowXRP");
		_flags.put(TransactionFlag.AccountSetMask, "AccountSetMask");
		_flags.put(TransactionFlag.asfRequireDest, "asfRequireDest");
		_flags.put(TransactionFlag.asfRequireAuth, "asfRequireAuth");
		_flags.put(TransactionFlag.asfDisallowXRP, "asfDisallowXRP");
		_flags.put(TransactionFlag.asfDisableMaster, "asfDisableMaster");
		_flags.put(TransactionFlag.asfAccountTxnID, "asfAccountTxnID");
		_flags.put(TransactionFlag.asfNoFreeze, "asfNoFreeze");
		_flags.put(TransactionFlag.asfGlobalFreeze, "asfGlobalFreeze");
		// OfferCreate flags:
		_flags.put(TransactionFlag.Passive, "Passive");
		_flags.put(TransactionFlag.ImmediateOrCancel, "ImmediateOrCancel");
		_flags.put(TransactionFlag.FillOrKill, "FillOrKill");
		_flags.put(TransactionFlag.Sell, "Sell");
		_flags.put(TransactionFlag.OfferCreateMask, "OfferCreateMask");
		// Payment flags:
		_flags.put(TransactionFlag.NoRippleDirect, "NoRippleDirect");
		_flags.put(TransactionFlag.PartialPayment, "PartialPayment");
		_flags.put(TransactionFlag.LimitQuality, "LimitQuality");
		_flags.put(TransactionFlag.PaymentMask, "PaymentMask");
		// TrustSet flags:
		_flags.put(TransactionFlag.SetAuth, "SetAuth");
		_flags.put(TransactionFlag.SetNoRipple, "SetNoRipple");
		_flags.put(TransactionFlag.ClearNoRipple, "ClearNoRipple");
		_flags.put(TransactionFlag.SetFreeze, "SetFreeze");
		_flags.put(TransactionFlag.ClearFreeze, "ClearFreeze");
		_flags.put(TransactionFlag.TrustSetMask, "TrustSetMask");
		// Special flags:
		_flags.put(0x30000L, "Cheater");
		_flags.put(0L, "Empty");
	}

	public final static String getString(long flag) {
		String result = _flags.get(flag);
		return result == null ? "Unkown" : result;
	}

	public final static ArrayList<String> values() {
		ArrayList<String> list = new ArrayList<String>(_flags.size());
		for (String v : _flags.values()) {
			list.add(v);
		}
		return list;
	}
	
}
