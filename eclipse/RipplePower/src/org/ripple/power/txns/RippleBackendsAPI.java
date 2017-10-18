package org.ripple.power.txns;

import java.text.DecimalFormat;
import java.util.List;

import org.json.JSONObject;
import org.ripple.power.RippleSeedAddress;
import org.ripple.power.config.LSystem;
import org.ripple.power.txns.data.AccountInfoResponse;
import org.ripple.power.txns.data.AccountLinesResponse;
import org.ripple.power.txns.data.CancelOrderResponse;
import org.ripple.power.txns.data.CandlesResponse;
import org.ripple.power.txns.data.Line;
import org.ripple.power.txns.data.Market;
import org.ripple.power.txns.data.MarketDepthAsksResponse;
import org.ripple.power.txns.data.MarketDepthBidsResponse;
import org.ripple.power.txns.data.NewOrderResponse;
import org.ripple.power.txns.data.Offer;
import org.ripple.power.txns.data.OfferListener;
import org.ripple.power.txns.data.OffersResponse;
import org.ripple.power.txns.data.RippleResult;
import org.ripple.power.txns.data.RippleResultListener;
import org.ripple.power.txns.data.Take;
import org.ripple.power.ui.RPClient;

import com.ripple.client.enums.Command;
import com.ripple.client.requests.Request;
import com.ripple.client.responses.Response;

//select rippled api or ripple rest api
public class RippleBackendsAPI {

	private final static DecimalFormat xrp_num_format = new DecimalFormat("0.00000");

	public static enum Model {
		RippleRestAPI, Rippled
	}

	public Model model = Model.Rippled;

	private boolean _testing = false;

	private RippleSeedAddress _seed;

	public RippleBackendsAPI(RippleSeedAddress seed) {
		this(seed, false);
	}

	public RippleBackendsAPI(RippleSeedAddress seed, boolean test) {
		this._seed = seed;
		this._testing = test;
	}

	public RippleBackendsAPI() {
	}

	public AccountInfoResponse getAccountInfo(final String address, final Updateable update) {
		if (!AccountFind.isRippleAddress(address)) {
			throw new RuntimeException("not ripple address !");
		}
		final AccountInfoResponse accountInfo = new AccountInfoResponse();
		// rippled
		switch (model) {
		case Rippled:
			RPClient client = RPClient.ripple();
			if (client != null) {

				Request req = client.newRequest(Command.account_info);
				req.json("account", address);
				req.once(Request.OnSuccess.class, new Request.OnSuccess() {
					@Override
					public void called(Response response) {

						JSONObject result = response.message;
						if (result != null) {
							accountInfo.from(result);
						}
						if (update != null) {
							update.action(accountInfo);
						}

					}
				});
				req.once(Request.OnError.class, new Request.OnError() {
					@Override
					public void called(Response response) {
						if (update != null) {
							update.action(response.error_message);
						}
					}
				});
				req.request();
			}
			break;
		default:
			break;
		}

		return accountInfo;
	}

	public AccountLinesResponse getAccountLines(final String address, final Updateable update) {
		if (!AccountFind.isRippleAddress(address)) {
			throw new RuntimeException("not ripple address !");
		}
		final AccountLinesResponse accountLines = new AccountLinesResponse();
		// rippled
		switch (model) {
		case Rippled:
			RPClient client = RPClient.ripple();
			if (client != null) {
				Request req = client.newRequest(Command.account_lines);
				req.json("account", address);
				req.json("ledger", "current");
				req.once(Request.OnSuccess.class, new Request.OnSuccess() {
					@Override
					public void called(Response response) {
						JSONObject result = response.message;
						if (result != null) {
							accountLines.from(result);
						}
						if (update != null) {
							update.action(accountLines);
						}

					}
				});
				req.once(Request.OnError.class, new Request.OnError() {
					@Override
					public void called(Response response) {
						if (update != null) {
							update.action(response.error_message);
						}
					}
				});
				req.request();
			}
			break;
		default:
			break;
		}
		return null;
	}

	public OffersResponse getActiveOrders(final String address, final Updateable update) {
		if (!AccountFind.isRippleAddress(address)) {
			throw new RuntimeException("not ripple address !");
		}
		final OffersResponse offers = new OffersResponse();
		// rippled
		switch (model) {
		case Rippled:
			RPClient client = RPClient.ripple();
			if (client != null) {
				Request req = client.newRequest(Command.account_offers);
				req.json("account", address);
				req.once(Request.OnSuccess.class, new Request.OnSuccess() {
					@Override
					public void called(Response response) {
						JSONObject result = response.message;

						if (result != null) {
							offers.from(result);
						}
						if (update != null) {
							update.action(offers);
						}

					}
				});
				req.once(Request.OnError.class, new Request.OnError() {
					@Override
					public void called(Response response) {

						if (update != null) {
							update.action(response.error_message);
						}
					}
				});
				req.request();
			}
			break;
		default:
			break;
		}
		return null;
	}

	public double getSynXrpBalance() {
		if (_seed != null) {
			return getSynXrpBalance(_seed.getPublicKey());
		}
		return -1;
	}

	public double getSynXrpBalance(final String address) {
		RippleResult result = getXrpBalance(address, null);
		for (; result.data == null;) {
		}
		if (result.success) {
			if (result.data instanceof Double) {
				return (double) result.data;
			}
		}
		return -1;
	}

	public RippleResult getXrpBalance(final String address, final RippleResultListener listener) {
		final RippleResult result = new RippleResult();
		getAccountInfo(address, new Updateable() {

			@Override
			public void action(Object o) {
				if (o instanceof AccountInfoResponse) {
					AccountInfoResponse account = (AccountInfoResponse) o;
					if (account.result == null || account.result.account_data == null) {
						result.data = -1.0;
						result.success = false;
					} else {
						result.data = account.result.account_data.getBalanceXrp();
						result.success = true;
					}
				} else {
					result.data = -1.0;
					result.success = false;
				}
				if (listener != null) {
					listener.update(result);
				}
			}
		});
		return result;
	}

	public double getSynBalance(Take dst) {
		return getSynBalance(dst.issuer, dst.currency);
	}

	public double getSynBalance(final String issuerAddress, final String currency) {
		if (_seed != null) {
			return getSynBalance(_seed.getPublicKey(), issuerAddress, currency);
		}
		return -1;
	}

	public double getSynBalance(final String address, final String issuerAddress, final String currency) {
		RippleResult result = getBalance(address, issuerAddress, currency, null);
		for (; result.data == null;) {
		}
		if (result.success) {
			if (result.data instanceof Double) {
				return (double) result.data;
			}
		}
		return -1;
	}

	public RippleResult getBalance(final String address, final RippleResultListener listener) {
		return getBalance(address, null, null, listener);
	}

	public RippleResult getBalance(final String address, final String issuerAddress, final String currency,
			final RippleResultListener listener) {
		final RippleResult result = new RippleResult();
		getAccountLines(address, new Updateable() {

			@Override
			public void action(Object o) {
				if (o instanceof AccountLinesResponse) {
					AccountLinesResponse account = (AccountLinesResponse) o;
					if (account.result == null || account.result.lines == null) {
						result.data = -1.0;
						result.success = false;
					} else {
						List<Line> theLines = account.result.lines;
						if (theLines.size() > 0) {
							for (Line line : theLines) {
								if (issuerAddress != null && currency != null) {
									if (line.account.equalsIgnoreCase(issuerAddress)
											&& line.currency.equalsIgnoreCase(currency)) {
										result.data = line.getBalance();
										result.success = true;
										break;
									}
								} else if (currency != null) {
									if (line.currency.equalsIgnoreCase(currency)) {
										result.data = line.getBalance();
										result.success = true;
										break;
									}
								} else {
									result.data = theLines;
									result.success = true;
									break;
								}
							}
						}
						if (!result.success) {
							result.data = -1.0;
							result.success = false;
						}
					}
				} else {
					result.data = -1.0;
					result.success = false;
				}
				if (listener != null) {
					listener.update(result);
				}
			}
		});
		return result;
	}

	public Offer getSynOrderInfo(final long orderId) {
		if (_seed != null) {
			return getSynOrderInfo(_seed.getPublicKey(), orderId);
		}
		return new Offer(true);
	}

	public Offer getSynOrderInfo(final String address, final long orderId) {
		RippleResult result = getOrderInfo(address, orderId, null);
		for (; result.data == null;) {
		}
		if (result.success) {
			if (result.data instanceof Offer) {
				return (Offer) result.data;
			}
		}
		return new Offer(true);
	}

	public RippleResult getOrderInfo(final String address, final long orderId, final RippleResultListener listener) {
		final RippleResult result = new RippleResult();
		getActiveOrders(address, new Updateable() {

			@Override
			public void action(Object o) {
				if (o instanceof OffersResponse) {
					OffersResponse account = (OffersResponse) o;
					if (account.result == null || account.result.offers == null) {
						result.data = -1.0;
						result.success = false;
					} else {
						if (orderId < 0) {
							result.data = account.result.offers;
							result.success = true;
						} else {
							List<Offer> theOffers = account.result.offers;
							if (theOffers.size() == 0) {
								result.data = new Offer(true);
								result.success = true;
								return;
							} else {
								for (Offer offer : theOffers) {
									if (offer.seq == orderId) {
										result.data = offer;
										result.success = true;
										return;
									}
								}

							}
							result.data = -1.0;
							result.success = false;
						}
					}
				} else {
					result.data = -1.0;
					result.success = false;
				}
				if (listener != null) {
					listener.update(result);
				}
			}
		});
		return result;
	}

	public MarketDepthBidsResponse getBids(String takerAddress, final Take pays, final Take gets, final int limit,
			final Updateable update) {
		final MarketDepthBidsResponse bids = new MarketDepthBidsResponse();
		RPClient client = RPClient.ripple();
		if (client != null) {
			Request req = client.newRequest(Command.book_offers);
			if (takerAddress != null) {
				req.json("taker", takerAddress);
			}
			req.json("taker_pays", pays.getJSON());
			req.json("taker_gets", gets.getJSON());
			if (limit > 0) {
				req.json("limit", limit);
			} else {
				req.json("limit", 15);
			}
			req.once(Request.OnSuccess.class, new Request.OnSuccess() {
				@Override
				public void called(Response response) {
					JSONObject result = response.message;
					if (result != null) {
						bids.from(result);
					}
					if (update != null) {
						update.action(bids);
					}
				}
			});
			req.once(Request.OnError.class, new Request.OnError() {
				@Override
				public void called(Response response) {
					if (update != null) {
						update.action(response.error_message);
					}
				}
			});
			req.request();
		}
		return bids;
	}

	public MarketDepthAsksResponse getAsks(String takerAddress, final Take pays, final Take gets, final int limit,
			final Updateable update) {
		final MarketDepthAsksResponse asks = new MarketDepthAsksResponse();
		RPClient client = RPClient.ripple();
		if (client != null) {
			Request req = client.newRequest(Command.book_offers);
			if (takerAddress != null) {
				req.json("taker", takerAddress);
			}
			req.json("taker_pays", gets.getJSON());
			req.json("taker_gets", pays.getJSON());
			if (limit > 0) {
				req.json("limit", limit);
			} else {
				req.json("limit", 15);
			}
			req.once(Request.OnSuccess.class, new Request.OnSuccess() {
				@Override
				public void called(Response response) {
					JSONObject result = response.message;
					if (result != null) {
						asks.from(result);
					}
					if (update != null) {
						update.action(asks);
					}
				}
			});
			req.once(Request.OnError.class, new Request.OnError() {
				@Override
				public void called(Response response) {
					if (update != null) {
						update.action(response.error_message);
					}
				}
			});
			req.request();
		}
		return asks;
	}

	public Market getSynXRPMarketDepth(String takerAddress, final Take gets, final int limit) {
		return getSynMarketDepth(takerAddress, RippleDefault.BASE, gets, limit);
	}

	public Market getSynMarketDepth(String takerAddress, final Take pays, final Take gets, final int limit) {
		Market market = getMarketDepth(takerAddress, pays, gets, limit, null);
		for (; market.count < 2;) {
		}
		return market;
	}

	public Market getMarketDepth(String takerAddress, final Take pays, final Take gets, final int limit) {
		return getMarketDepth(takerAddress, pays, gets, limit, null);
	}

	public Market getMarketDepth(String takerAddress, final Take pays, final Take gets, final int limit,
			final OfferListener listener) {
		final Market market = new Market();
		// rippled
		switch (model) {
		case Rippled:
			getBids(takerAddress, pays, gets, limit, new Updateable() {

				@Override
				public void action(Object o) {
					if (o instanceof MarketDepthBidsResponse) {
						market.Bids = ((MarketDepthBidsResponse) o).result.offers;
						if (listener != null) {
							listener.bids(market.Bids);
						}
					}
					market.count++;
				}
			});
			getAsks(takerAddress, pays, gets, limit, new Updateable() {

				@Override
				public void action(Object o) {
					if (o instanceof MarketDepthAsksResponse) {
						market.Asks = ((MarketDepthAsksResponse) o).result.offers;
						if (listener != null) {
							listener.asks(market.Asks);
						}
					}
					market.count++;
				}
			});
		default:
			break;
		}
		return market;
	}

	public RippleResult placeOrder(final RippleSeedAddress seed, IssuedCurrency pays, IssuedCurrency gets, long flags,
			final RippleResultListener listener) {
		final RippleResult result = new RippleResult();
		if (_testing) {
			NewOrderResponse newOrder = new NewOrderResponse();
			result.data = newOrder;
			result.success = true;
			return result;
		}
		OfferCreate.set(seed, pays, gets, LSystem.getFee(), -1, 1.0001f, flags, new Rollback() {

			@Override
			public void success(JSONObject res) {
				NewOrderResponse newOrder = new NewOrderResponse();
				newOrder.from(res);
				result.data = newOrder;
				result.success = true;
				if (listener != null) {
					listener.update(result);
				}
			}

			@Override
			public void error(JSONObject res) {
				result.data = -1;
				result.success = false;
				if (listener != null) {
					listener.update(result);
				}
			}
		});
		return result;
	}

	public long placeSynXRPBuyOrder(double price, double amount, Take dst) {
		return placeSynXRPBuyOrder(price, amount, dst.currency, dst.issuer);
	}

	public long placeSynXRPBuyOrder(double price, double amount, String curreny, String issuer) {
		if (_seed != null) {
			return placeSynXRPBuyOrder(_seed, price, amount, curreny, issuer);
		}
		return -1;
	}

	public long placeSynXRPBuyOrder(final RippleSeedAddress seed, double price, double amount, String curreny,
			String issuer) {
		RippleResult result = placeXRPBuyOrder(seed, price, amount, curreny, issuer, null);
		for (; result.data == null;) {
		}
		if (result.success) {
			if (result.data instanceof NewOrderResponse) {
				return ((NewOrderResponse) result.data).result.tx_json.Sequence;
			}
		}
		return -1;
	}

	public long placeSynXRPSellOrder(double price, double amount, Take dst) {
		return placeSynXRPSellOrder(price, amount, dst.currency, dst.issuer);
	}

	public long placeSynXRPSellOrder(double price, double amount, String curreny, String issuer) {
		if (_seed != null) {
			return placeSynXRPSellOrder(_seed, price, amount, curreny, issuer);
		}
		return -1;
	}

	public long placeSynXRPSellOrder(final RippleSeedAddress seed, double price, double amountXrp, String curreny,
			String issuer) {
		RippleResult result = placeXRPSellOrder(seed, price, amountXrp, curreny, issuer, null);
		for (; result.data == null;) {
		}
		if (result.success) {
			if (result.data instanceof NewOrderResponse) {
				return ((NewOrderResponse) result.data).result.tx_json.Sequence;
			}
		}
		return -1;
	}

	public RippleResult placeXRPBuyOrder(final RippleSeedAddress seed, double price, double amount, String curreny,
			String issuer, RippleResultListener listener) {
		long amountXrpDrops = (long) Math.round(amount * Const.DROPS_IN_XRP);
		double amountFiat = price * amount;
		IssuedCurrency pays = new IssuedCurrency(String.valueOf(amountXrpDrops));
		IssuedCurrency gets = new IssuedCurrency(String.valueOf(xrp_num_format.format(amountFiat)), issuer, curreny);
		return placeOrder(seed, pays, gets, 0, listener);
	}

	public RippleResult placeXRPSellOrder(final RippleSeedAddress seed, double price, double amountXrp, String curreny,
			String issuer, RippleResultListener listener) {
		double amountFiat = price * amountXrp;
		long amountXrpDrops = (long) Math.round(amountXrp * Const.DROPS_IN_XRP);
		IssuedCurrency pays = new IssuedCurrency(String.valueOf(xrp_num_format.format(amountFiat)), issuer, curreny);
		IssuedCurrency gets = new IssuedCurrency(String.valueOf(amountXrpDrops));
		// flags == 2147483648(sell)
		return placeOrder(seed, pays, gets, 2147483648l, listener);
	}

	public long updateSynXRPSellOrder(long orderId, double price, double amount, Take dst) {
		if (_seed != null) {
			return updateSynXRPSellOrder(_seed, orderId, price, amount, dst);
		}
		return -1;
	}

	public long updateSynXRPSellOrder(final RippleSeedAddress seed, long orderId, double price, double amount,
			Take dst) {
		return updateSynXRPSellOrder(orderId, price, amount, dst.currency, dst.issuer);
	}

	public long updateSynXRPSellOrder(long orderId, double price, double amount, String curreny, String issuer) {
		if (_seed != null) {
			return updateSynXRPSellOrder(_seed, orderId, price, amount, curreny, issuer);
		}
		return -1;
	}

	public long updateSynXRPSellOrder(final RippleSeedAddress seed, long orderId, double price, double amount,
			String curreny, String issuer) {
		if (cancelSynOrder(seed, orderId)) {
			long id = placeSynXRPSellOrder(seed, price, amount, curreny, issuer);
			if (id == -1) {
				return orderId;
			}
			return id;
		}
		return orderId;
	}

	public long updateSynXRPBuyOrder(long orderId, double price, double amount, Take dst) {
		if (_seed != null) {
			return updateSynXRPBuyOrder(_seed, orderId, price, amount, dst);
		}
		return -1;
	}

	public long updateSynXRPBuyOrder(final RippleSeedAddress seed, long orderId, double price, double amount,
			Take dst) {
		return updateSynXRPBuyOrder(orderId, price, amount, dst.currency, dst.issuer.toString());
	}

	public long updateSynXRPBuyOrder(long orderId, double price, double amount, String curreny, String issuer) {
		if (_seed != null) {
			return updateSynXRPSellOrder(_seed, orderId, price, amount, curreny, issuer);
		}
		return -1;
	}

	public long updateSynXRPBuyOrder(final RippleSeedAddress seed, long orderId, double price, double amount,
			String curreny, String issuer) {
		if (cancelSynOrder(orderId)) {
			long id = placeSynXRPBuyOrder(seed, price, amount, curreny, issuer);
			if (-1 == id) {
				return orderId;
			}
			return id;
		}

		return orderId;
	}

	public boolean cancelSynOrder(long orderId) {
		if (_seed != null) {
			return cancelSynOrder(_seed, orderId);
		}
		return false;
	}

	public boolean cancelSynOrder(final RippleSeedAddress seed, long orderId) {
		RippleResult result = cancelOrder(seed, orderId, null);
		for (; result.data == null;) {
		}
		if (result.success) {
			if (result.data instanceof CancelOrderResponse) {
				return ((CancelOrderResponse) result.data).result.getResultOK();
			}
		}
		return false;
	}

	public RippleResult cancelOrder(final RippleSeedAddress seed, long orderId, final RippleResultListener listener) {
		final RippleResult result = new RippleResult();
		if (_testing) {
			CancelOrderResponse cancelOrder = new CancelOrderResponse();
			result.data = cancelOrder;
			result.success = true;
			return result;
		}
		OfferCancel.set(seed, orderId, LSystem.getFee(), new Rollback() {

			@Override
			public void success(JSONObject res) {
				CancelOrderResponse cancelOrder = new CancelOrderResponse();
				cancelOrder.from(res);
				result.data = cancelOrder;
				result.success = true;
				if (listener != null) {
					listener.update(result);
				}
			}

			@Override
			public void error(JSONObject res) {
				result.data = -1;
				result.success = false;
				if (listener != null) {
					listener.update(result);
				}
			}
		});
		return result;
	}

	public CandlesResponse getTradeStatistics(long time, String curreny, String issuer) {
		return RippleChartsAPI.getTradeStatistics(curreny, issuer, time);
	}

	public CandlesResponse getTradeStatistics(long time, Take basecur, Take counter) {
		return RippleChartsAPI.getTradeStatistics(basecur, counter, time);
	}

	public CandlesResponse getTradeStatistics(long time, Take counter) {
		return RippleChartsAPI.getTradeStatistics(RippleDefault.BASE, counter, time);
	}

	public boolean isTesting() {
		return _testing;
	}

	public RippleSeedAddress getAccountSeed() {
		return _seed;
	}

}
