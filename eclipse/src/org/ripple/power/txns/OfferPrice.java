package org.ripple.power.txns;

import com.ripple.client.Client;
import com.ripple.client.requests.Request;
import com.ripple.client.responses.Response;
import com.ripple.core.coretypes.AccountID;
import com.ripple.core.coretypes.Amount;
import com.ripple.core.coretypes.Issue;
import com.ripple.core.coretypes.STArray;
import com.ripple.core.coretypes.STObject;
import com.ripple.core.types.known.sle.entries.Offer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ripple.power.config.LSystem;
import org.ripple.power.ui.RPClient;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;

public abstract class OfferPrice {

	public static boolean isSellOrder(long flag) {
		return (flag & 0x00020000) > 0;
	}

	private final static double LOG10 = Math.log(10.0d);

	public static double adjustValueFloor(double value, double digit) {
		double n = Math.pow(10d, digit);
		return Math.floor(value * n) / n;
	}

	public static double adjustValueCeil(double value, double digit) {
		double n = Math.pow(10d, digit);
		return Math.ceil(value * n) / n;
	}

	public static double numberOfDigits(double value) {
		return Math.floor(Math.log(value) / LOG10) + 1;
	}

	protected static ArrayList<Store> _storage = new ArrayList<Store>();

	private static class Store {
		public String price;
		public String name;
		public long date = 0;

		public Store(String p, String str) {
			this.price = p;
			this.name = str;
			this.date = System.currentTimeMillis();
		}

	}

	private static String reset(String name) {
		for (Store s : _storage) {
			if (s.name.equals(name)
					&& (System.currentTimeMillis() - s.date) <= LSystem.MINUTE) {
				return s.price;
			} else if (s.name.equals(name)) {
				_storage.remove(s);
				return null;
			}
		}
		return null;
	}

	private static void addStorage(Store s) {
		_storage.add(s);
		if (_storage.size() > 100) {
			_storage.remove(0);
		}
	}

	public synchronized static String getMoneyConvert(String srcValue,
			String src, String dst) {
		if (srcValue == null || src == null | dst == null) {
			return "unkown";
		}
		if (src.equalsIgnoreCase(dst)) {
			return srcValue;
		}
		if ("ker".equalsIgnoreCase(src) || "won".equalsIgnoreCase(src)) {
			src = "krw";
		}
		if ("ker".equalsIgnoreCase(dst) || "won".equalsIgnoreCase(dst)) {
			dst = "krw";
		}
		String name = (srcValue + src + dst).trim().toLowerCase();
		String ret = reset(name);

		if (ret != null) {
			return ret;
		}

		String oneValue = null;
		String twoValue = null;
		try {
			oneValue = OtherData.getCoinmarketcapCoinToUSD(src);
			twoValue = OtherData.getCoinmarketcapCoinToUSD(dst);
			if (oneValue != null && twoValue != null) {
				BigDecimal a1 = new BigDecimal(oneValue);
				BigDecimal b1 = new BigDecimal(twoValue);
				String result = LSystem.getNumber(a1.divide(b1,
						MathContext.DECIMAL128).multiply(
						new BigDecimal(srcValue)));
				addStorage(new Store(result, name));
				return result;
			}
		} catch (Exception ex) {
			// null
			oneValue = null;
			twoValue = null;
		}

		try {
			String tmp = OtherData.converterMoney(src, dst);
			if (tmp != null) {
				BigDecimal srcValueb = new BigDecimal(srcValue);
				BigDecimal valueb = new BigDecimal(tmp);
				String result = LSystem.getNumber(srcValueb.multiply(valueb));
				addStorage(new Store(result, name));
				return result;
			} else {
				if (oneValue == null) {
					oneValue = OtherData.converterMoney(src, "usd");
				}
				if (oneValue == null) {
					oneValue = OtherData.getCoinmarketcapCoinToUSD(src);
				}
				if (oneValue != null) {
					if (twoValue == null) {
						twoValue = OtherData.converterMoney(dst, "usd");
					}
					if (twoValue != null) {
						BigDecimal srcValueb = new BigDecimal(oneValue);
						BigDecimal valueb = new BigDecimal(twoValue);
						String result = LSystem.getNumber(srcValueb.divide(
								valueb, MathContext.DECIMAL128).multiply(
								new BigDecimal(srcValue)));
						addStorage(new Store(result, name));
						return result;
					} else {
						double yahooValue = OtherData
								.getLegaltenderCurrencyToUSD(src);
						if (yahooValue != -1) {
							if (twoValue == null) {
								twoValue = OtherData.converterMoney(dst, "usd");
							}
							if (twoValue != null) {
								BigDecimal srcValueb = new BigDecimal(
										yahooValue);
								BigDecimal valueb = new BigDecimal(twoValue);
								String result = LSystem.getNumber(srcValueb
										.divide(valueb, MathContext.DECIMAL128)
										.multiply(new BigDecimal(srcValue)));
								addStorage(new Store(result, name));
								return result;
							} else {
								addStorage(new Store("unkown", name));
								return "unkown";
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			return "unkown";
		}
		return "unkown";
	}

	public static class OfferFruit {

		public String message;

		public Offer offer;

		public String toString() {
			return message;
		}
	}

	public boolean subscribe = false;

	public String highBuy;

	public String highSell;

	public String spread;

	public abstract void buy(Offer offer);

	public abstract void sell(Offer offer);

	public abstract void empty();

	public abstract void error(JSONObject obj);

	public abstract void complete(ArrayList<OfferFruit> buys,
			ArrayList<OfferFruit> sells, OfferPrice price);

	public static void load(String address, String buyCurName,
			String sellCurName, OfferPrice price) {
		load(address, buyCurName, sellCurName, price, true);
	}

	public static void load(String address, String buyCurName,
			String sellCurName, OfferPrice price, boolean html) {
		AccountID account = AccountID.fromAddress(address);
		RPClient ripple = RPClient.ripple();
		if (ripple != null) {
			Issue buy = account.issue(buyCurName);
			Issue sell = account.issue(sellCurName);
			load(ripple.getClinet(), buy, sell, price, html);
		}
	}

	public static void load(IssuedCurrency buy, IssuedCurrency sell,
			OfferPrice price) {
		load(buy, sell, price, true);
	}

	public static void load(IssuedCurrency buy, IssuedCurrency sell,
			OfferPrice price, boolean html) {
		RPClient ripple = RPClient.ripple();
		if (ripple != null) {
			load(ripple.getClinet(), buy.getIssue(), sell.getIssue(), price,
					html);
		}
	}

	public static void load(Issue buy, Issue sell, OfferPrice price) {
		load(buy, sell, price, true);
	}

	public static void load(Issue buy, Issue sell, OfferPrice price,
			boolean html) {
		RPClient ripple = RPClient.ripple();
		if (ripple != null) {
			load(ripple.getClinet(), buy, sell, price, html);
		}
	}

	public static class OrderBooks {
		public static interface BookEvents {
			public void onUpdate(OrderBooks book);
		}

		private Client client;
		private int limit = 30;
		private final BookEvents callback;
		public Issue first, second;
		public STArray asks, bids;
		public Amount ask, bid, spread;

		public OrderBooks(Client client, Issue first, Issue second,
				BookEvents callback) {
			this(client, first, second, 30, callback);
		}

		public OrderBooks(Client client, Issue first, Issue second, int limit,
				BookEvents callback) {
			this.limit = limit;
			this.client = client;
			this.first = first;
			this.second = second;
			this.callback = callback;
		}

		private void calculateStats() {
			Offer firstAsk = (Offer) asks.get(0);
			Offer firstBid = (Offer) bids.get(0);
			BigDecimal askQuality = firstAsk.askQuality();
			BigDecimal bidQuality = firstBid.bidQuality();
			Amount secondOne = firstAsk.paysOne();
			ask = secondOne.multiply(askQuality);
			bid = secondOne.multiply(bidQuality);
			spread = ask.subtract(bid).abs();
		}

		private void requestUpdate(final OfferPrice price) {
			for (int i = 0; i < 2; i++) {
				final boolean getAsks = i == 0, getBids = !getAsks;
				Issue getIssue = getAsks ? first : second, payIssue = getAsks ? second
						: first;
				Request request = null;
				if (price.subscribe) {
					request = client.subscribeBookOffers(getIssue, payIssue);
				} else {
					request = client.requestBookOffers(getIssue, payIssue);
				}
				request.json("limit", limit);
				request.once(Request.OnResponse.class,
						new Request.OnResponse() {
							@Override
							public void called(Response response) {
								if (response.succeeded) {

									JSONArray offersJSON = response.result
											.optJSONArray("offers");
									STArray offers = STArray.translate
											.fromJSONArray(offersJSON);

									if (getBids) {
										bids = offers;
									} else {
										asks = offers;
									}
									if (retrievedBothBooks()) {
										if (!isEmpty()) {
											calculateStats();
										}
										callback.onUpdate(OrderBooks.this);
									}
								} else {
									price.error(response.message);
								}
							}
						});
				request.once(Request.OnError.class, new Request.OnError() {

					@Override
					public void called(Response response) {
						price.error(response.message);

					}
				});
				request.request();
			}
		}

		public boolean retrievedBothBooks() {
			return asks != null && bids != null;
		}

		public boolean isEmpty() {
			return !retrievedBothBooks() || asks.isEmpty() || bids.isEmpty();
		}
	}

	private static void load(Client client, final Issue first,
			final Issue second, final OfferPrice price, final boolean html) {
		if (price == null) {
			return;
		}
		new OrderBooks(client, first, second, new OrderBooks.BookEvents() {
			@Override
			public void onUpdate(OrderBooks book) {
				ArrayList<OfferFruit> buys = new ArrayList<OfferFruit>(100);
				ArrayList<OfferFruit> sells = new ArrayList<OfferFruit>(100);
				if (!book.isEmpty()) {
					price.highBuy = book.bid.toText();
					price.highSell = book.ask.toText();
					price.spread = book.spread.toText();
					// sell
					for (STObject offer : book.asks) {
						Offer o = (Offer) offer;
						price.sell(o);
						BigDecimal payForOne = o.askQuality();
						Amount paysOne = o.paysOne();
						Amount getsOne = o.getsOne();
						OfferFruit fruit = new OfferFruit();
						fruit.offer = o;
						if (html) {
							fruit.message = o.takerGets().toText()
									+ "<br><font size=5 color=red>Sell</font><br>"
									+ (o.takerPays().toText())
									+ "<br><font size=5 color=green>Exchange rate</font><br>"
									+ getsOne.toText() + "=="
									+ paysOne.multiply(payForOne).toText()
									+ "<br>"
									+ getsOne.divide(payForOne).toText() + "=="
									+ paysOne.toText();
						}
						sells.add(fruit);
					}
					// buy
					for (STObject offer : book.bids) {
						Offer o = (Offer) offer;
						price.buy(o);
						BigDecimal payForOne = o.askQuality();
						Amount paysOne = o.paysOne();
						Amount getsOne = o.getsOne();
						OfferFruit fruit = new OfferFruit();
						fruit.offer = o;
						if (html) {
							fruit.message = o.takerGets().toText()
									+ "<br><font size=5 color=green>Buy</font><br>"
									+ (o.takerPays().toText())
									+ "<br><font size=5 color=red>Exchange rate</font><br>"
									+ paysOne.multiply(payForOne).toText()
									+ "==" + getsOne.toText() + "<br>"
									+ paysOne.toText() + "=="
									+ getsOne.divide(payForOne).toText();
						}
						buys.add(fruit);
					}
				} else {
					// empty
					price.empty();
				}
				price.complete(buys, sells, price);
			}
		}).requestUpdate(price);
	}

}
