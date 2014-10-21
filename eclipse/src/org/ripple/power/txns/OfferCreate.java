package org.ripple.power.txns;

import org.address.ripple.RippleObject;
import org.address.ripple.RippleSeedAddress;
import org.address.ripple.RippleSchemas.BinaryFormatField;
import org.address.ripple.RippleSchemas.TransactionTypes;
import org.json.JSONObject;

public class OfferCreate {

	public static void set(final RippleSeedAddress seed,
			final IssuedCurrency src, final IssuedCurrency dst,
			final String fee, final Rollback back) {
		OfferCreate.set(seed, src, dst, fee, -1, back);
	}

	public static void set(final RippleSeedAddress seed,
			final IssuedCurrency src, final IssuedCurrency dst,
			final String fee, final long offerSequence, final Rollback back) {
		final String address = seed.getPublicRippleAddress().toString();
		AccountFind find = new AccountFind();
		find.info(address, new Rollback() {
			@Override
			public void success(JSONObject message) {
				try {
					long sequence = TransactionUtils.getSequence(message);
					RippleObject item = new RippleObject();
					item.putField(BinaryFormatField.TransactionType,
							(int) TransactionTypes.OFFER_CREATE.byteValue);
					item.putField(BinaryFormatField.Account,
							seed.getPublicRippleAddress());
					item.putField(BinaryFormatField.TakerPays, src);
					item.putField(BinaryFormatField.TakerGets, dst);
					item.putField(BinaryFormatField.Fee,
							CurrencyUtils.getValueToRipple(fee));
					item.putField(BinaryFormatField.Sequence, sequence);
					if (offerSequence > -1) {
						item.putField(BinaryFormatField.OfferSequence,
								offerSequence);
					}
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
