package org.ripple.power.txns;

import org.json.JSONObject;
import org.ripple.power.RippleObject;
import org.ripple.power.RippleSeedAddress;
import org.ripple.power.RippleSchemas.BinaryFormatField;
import org.ripple.power.RippleSchemas.TransactionTypes;
import org.ripple.power.config.LSystem;
import org.ripple.power.utils.StringUtils;

import com.ripple.core.enums.TransactionFlag;

public class OfferCreate {

	public static void sell(final RippleSeedAddress seed, final String pair,
			final String issuer, final double price, final double amount,
			final String fee, final Rollback back) {
		set(seed, matchOfferSell(pair, issuer, price, amount), fee, back);
	}

	public static void buy(final RippleSeedAddress seed, final String pair,
			final String issuer, final double price, final double amount,
			final String fee, final Rollback back) {
		set(seed, matchOfferBuy(pair, issuer, price, amount), fee, back);
	}

	public static void set(final RippleSeedAddress seed, final BookOffer offer,
			final String fee, final Rollback back) {
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
					item.putField(BinaryFormatField.TakerPays, offer.buy);
					item.putField(BinaryFormatField.TakerGets, offer.sell);
					item.putField(BinaryFormatField.Fee,
							CurrencyUtils.getValueToRipple(fee));
					item.putField(BinaryFormatField.Sequence, sequence);
					if (offer.sequence > 0) {
						item.putField(BinaryFormatField.OfferSequence,
								offer.sequence);
					}
					if (offer.flags > 0) {
						item.putField(BinaryFormatField.Flags, offer.flags);
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

	public static BookOffer matchOfferSell(String pair, String issuer,
			double price, double amount, long seq) {
		return matchOfferSell(pair, issuer, price, amount, seq,
				TransactionFlag.Sell);
	}

	public static BookOffer matchOfferSell(String pair, String issuer,
			double price, double amount) {
		return matchOfferSell(pair, issuer, price, amount, 0,
				TransactionFlag.Sell);
	}

	/**
	 * matchOfferBuy("XRP,BTC","rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B",2,1)
	 * 
	 * @param pair
	 * @param issuer
	 * @param price
	 * @param amount
	 * @param seq
	 * @param flags
	 * @return
	 */
	public static BookOffer matchOfferSell(String pair, String issuer,
			double price, double amount, long seq, long flags) {
		if (issuer.startsWith("~")) {
			try {
				issuer = NameFind.getAddress(issuer);
			} catch (Exception e) {
				return null;
			}
		}
		String[] split = StringUtils.split(StringUtils.rtrim(pair)
				.toUpperCase(), ",");
		BookOffer offer = new BookOffer(
				matchOffer(split[1],
						LSystem.getNumberShort(String.valueOf(amount * price)),
						issuer),
				matchOffer(split[0],
						LSystem.getNumberShort(String.valueOf(amount)), issuer),
				seq, flags);
		return offer;
	}

	public static BookOffer matchOfferBuy(String pair, String issuer,
			double price, double amount, long seq) {
		return matchOfferBuy(pair, issuer, price, amount, seq, 0);
	}

	public static BookOffer matchOfferBuy(String pair, String issuer,
			double price, double amount) {
		return matchOfferBuy(pair, issuer, price, amount, 0, 0);
	}

	/**
	 * matchOfferBuy("XRP,BTC","rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B",3,1)
	 * 
	 * @param pair
	 * @param issuer
	 * @param price
	 * @param amount
	 * @param seq
	 * @param flags
	 * @return
	 */
	public static BookOffer matchOfferBuy(String pair, String issuer,
			double price, double amount, long seq, long flags) {
		if (issuer.startsWith("~")) {
			try {
				issuer = NameFind.getAddress(issuer);
			} catch (Exception e) {
				return null;
			}
		}
		String[] split = StringUtils.split(StringUtils.rtrim(pair)
				.toUpperCase(), ",");
		BookOffer offer = new BookOffer(matchOffer(split[0],
				LSystem.getNumberShort(String.valueOf(amount)), issuer),
				matchOffer(split[1],
						LSystem.getNumberShort(String.valueOf(amount * price)),
						issuer), seq, flags);
		return offer;
	}

	public static IssuedCurrency matchOffer(String name, String value,
			String issuer) {
		return (LSystem.nativeCurrency.equalsIgnoreCase(name)) ? new IssuedCurrency(
				CurrencyUtils.getValueToRipple(value)) : new IssuedCurrency(
				value, issuer, name);
	}

	public static void set(final RippleSeedAddress seed,
			final IssuedCurrency src, final IssuedCurrency dst,
			final String fee, final Rollback back) {
		OfferCreate.set(seed, src, dst, fee, -1, 1.0001f, back);
	}

	public static void set(final RippleSeedAddress seed,
			final IssuedCurrency src, final IssuedCurrency dst,
			final String fee, float scale, final Rollback back) {
		OfferCreate.set(seed, src, dst, fee, -1, back);
	}

	public static void set(final RippleSeedAddress seed,
			final IssuedCurrency src, final IssuedCurrency dst,
			final String fee, final long offerSequence, final float scale,
			final Rollback back) {
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
					if (scale > 0) {
						dst.scale(scale);
					}
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
