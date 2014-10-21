package org.ripple.power.txns;

import org.address.ripple.RippleObject;
import org.address.ripple.RippleSeedAddress;
import org.address.ripple.RippleSchemas.BinaryFormatField;
import org.address.ripple.RippleSchemas.TransactionTypes;
import org.json.JSONObject;

public class OfferCancel {

	public static void set(final RippleSeedAddress seed,
			final long offerSequence, final String fee, final Rollback back) {
		final String address = seed.getPublicRippleAddress().toString();
		AccountFind find = new AccountFind();
		find.info(address, new Rollback() {
			@Override
			public void success(JSONObject message) {
				try {
					long sequence = TransactionUtils.getSequence(message);
					RippleObject item = new RippleObject();
					item.putField(BinaryFormatField.TransactionType,
							(int) TransactionTypes.OFFER_CANCEL.byteValue);
					item.putField(BinaryFormatField.Account,
							seed.getPublicRippleAddress());
					item.putField(BinaryFormatField.Fee,
							CurrencyUtils.getValueToRipple(fee));
					item.putField(BinaryFormatField.Sequence, sequence);
					item.putField(BinaryFormatField.OfferSequence,
							offerSequence);
					TransactionUtils.submitBlob(seed, item, back);
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
