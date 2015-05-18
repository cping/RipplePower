package org.ripple.power.txns;

import java.io.UnsupportedEncodingException;

import org.json.JSONObject;
import org.ripple.power.RippleSeedAddress;
import org.ripple.power.config.LSystem;

import com.ripple.core.coretypes.Blob;
import com.ripple.core.coretypes.hash.Hash128;
import com.ripple.core.fields.Field;
import com.ripple.core.serialized.enums.TransactionType;
import com.ripple.core.types.known.tx.Transaction;

public class AccountSet {

	public static void set(final RippleSeedAddress seed,
			final String messageKey, final String domain,
			final String emailHash, final long clearFlag, final long setFlag,
			final long transferRate, final String fee, final Rollback back) {
		final String address = seed.getPublicRippleAddress().toString();
		AccountFind find = new AccountFind();
		find.info(address, new Rollback() {
			@Override
			public void success(JSONObject message) {
				long sequence = TransactionUtils.getSequence(message);
				Transaction txn = new Transaction(TransactionType.AccountSet);
				txn.putTranslated(Field.Account, seed.getPublicKey());
				if (messageKey != null) {
					try {
						txn.put(Field.MessageKey, Blob
								.fromBytes(domain.getBytes(LSystem.encoding)));
					} catch (UnsupportedEncodingException e) {
						txn.put(Field.MessageKey,
								Blob.fromBytes(domain.getBytes()));
					}
				}
				if (domain != null) {
					try {
						txn.put(Field.Domain, Blob.fromBytes(domain
								.getBytes(LSystem.encoding)));
					} catch (UnsupportedEncodingException e) {
						txn.put(Field.Domain,
								Blob.fromBytes(domain.getBytes()));
					}
				}
				if (emailHash != null) {
					try {
						txn.put(Field.EmailHash,
								new Hash128(emailHash
										.getBytes(LSystem.encoding)));
					} catch (UnsupportedEncodingException e) {
						txn.put(Field.EmailHash,
								new Hash128(emailHash.getBytes()));
					}
				}
				if (transferRate > -1) {
					txn.putTranslated(Field.TransferRate, transferRate);
				}
				if (clearFlag > -1) {
					txn.putTranslated(Field.ClearFlag, clearFlag);
				}
				if (setFlag > -1) {
					txn.putTranslated(Field.SetFlag, setFlag);
				}
				try {
					TransactionUtils.submitBlob(seed, txn, fee, sequence, back);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void error(JSONObject message) {
				if (back != null) {
					back.error(message);
				}

			}
		});

	}
}
