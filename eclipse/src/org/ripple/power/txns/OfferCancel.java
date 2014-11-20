package org.ripple.power.txns;

import org.json.JSONObject;
import org.ripple.power.RippleObject;
import org.ripple.power.RippleSeedAddress;
import org.ripple.power.RippleSchemas.BinaryFormatField;
import org.ripple.power.RippleSchemas.TransactionTypes;

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
