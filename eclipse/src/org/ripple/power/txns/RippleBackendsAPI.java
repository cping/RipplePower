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

	private final static DecimalFormat xrp_num_format = new DecimalFormat(
			"0.00000");

	public static enum Model {
		RippleRestAPI, Rippled
	}

	public Model model = Model.Rippled;

	public AccountInfoResponse getAccountInfo(final String address,
			final Updateable update) {
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

	public AccountLinesResponse getAccountLines(final String address,
			final Updateable update) {
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

	public OffersResponse getActiveOrders(final String address,
			final Updateable update) {
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

	public RippleResult getXrpBalance(final String address,
			final RippleResultListener listener) {
		final RippleResult result = new RippleResult();
		getAccountInfo(address, new Updateable() {

			@Override
			public void action(Object o) {
				if (o instanceof AccountInfoResponse) {
					AccountInfoResponse account = (AccountInfoResponse) o;
					if (account.result == null
							|| account.result.account_data == null) {
						result.data = -1.0;
						result.success = false;
					} else {
						result.data = account.result.account_data
								.getBalanceXrp();
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

	public double getSynBalance(final String address,
			final String issuerAddress, final String currency) {
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

	public RippleResult getBalance(final String address,
			final RippleResultListener listener) {
		return getBalance(address, null, null, listener);
	}

	public RippleResult getBalance(final String address,
			final String issuerAddress, final String currency,
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
									if (line.account
											.equalsIgnoreCase(issuerAddress)
											&& line.currency
													.equalsIgnoreCase(currency)) {
										result.data = line.getBalance();
										result.success = true;
										break;
									}
								} else if (currency != null) {
									if (line.currency
											.equalsIgnoreCase(currency)) {
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

	public Offer getSynOrderInfo(final String address, final int orderId) {
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

	public RippleResult getOrderInfo(final String address, final int orderId,
			final RippleResultListener listener) {
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

	public MarketDepthBidsResponse getBids(String takerAddress,
			final Take pays, final Take gets, final int limit,
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

	public MarketDepthAsksResponse getAsks(String takerAddress,
			final Take pays, final Take gets, final int limit,
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

	public Market getMarketDepth(String takerAddress, final Take gets,
			final Take pays, final int limit) {
		final Market market = new Market();
		// rippled
		switch (model) {
		case Rippled:
			getBids(takerAddress, gets, pays, limit, new Updateable() {

				@Override
				public void action(Object o) {
					if (o instanceof MarketDepthBidsResponse) {
						market.Bids = ((MarketDepthBidsResponse) o).result.offers;
					}

				}
			});
			getAsks(takerAddress, gets, pays, limit, new Updateable() {

				@Override
				public void action(Object o) {
					if (o instanceof MarketDepthAsksResponse) {
						market.Asks = ((MarketDepthAsksResponse) o).result.offers;
					}
				}
			});

		default:
			break;
		}
		return market;
	}

	public RippleResult PlaceOrder(final RippleSeedAddress seed,
			IssuedCurrency pays, IssuedCurrency gets, long flags,
			RippleResultListener listener) {
		final RippleResult result = new RippleResult();
		OfferCreate.set(seed, pays, gets, LSystem.getFee(), -1, 1.0001f, flags,
				new Rollback() {

					@Override
					public void success(JSONObject res) {
						NewOrderResponse newOrder = new NewOrderResponse();
						newOrder.from(res);
						result.data = newOrder;
						result.success = true;
					}

					@Override
					public void error(JSONObject res) {
						result.data = -1;
						result.success = false;
					}
				});
		return result;
	}

	public RippleResult PlaceXRPBuyOrder(final RippleSeedAddress seed,
			double price, double amount, String curreny, String issuer,
			RippleResultListener listener) {
		long amountXrpDrops = (long) Math.round(amount * Const.DROPS_IN_XRP);
		double amountFiat = price * amount;
		IssuedCurrency pays = new IssuedCurrency(String.valueOf(amountXrpDrops));
		IssuedCurrency gets = new IssuedCurrency(String.valueOf(xrp_num_format
				.format(amountFiat)), issuer, curreny);
		return PlaceOrder(seed, pays, gets, 0, listener);
	}

	public RippleResult PlaceXRPSellOrder(final RippleSeedAddress seed,
			double amountFiat, double amountXrp, String curreny, String issuer,
			RippleResultListener listener) {
		long amountXrpDrops = (long) Math.round(amountXrp * Const.DROPS_IN_XRP);
		IssuedCurrency pays = new IssuedCurrency(String.valueOf(xrp_num_format
				.format(amountXrpDrops)), issuer, curreny);
		IssuedCurrency gets = new IssuedCurrency(String.valueOf(amountXrpDrops));
		// flags == 2147483648(sell)
		return PlaceOrder(seed, pays, gets, 2147483648l, listener);
	}

	public RippleResult cancelOrder(final RippleSeedAddress seed, long orderId,
			RippleResultListener listener) {
		final RippleResult result = new RippleResult();
		OfferCancel.set(seed, orderId, LSystem.getFee(), new Rollback() {

			@Override
			public void success(JSONObject res) {
				CancelOrderResponse cancelOrder = new CancelOrderResponse();
				cancelOrder.from(res);
				result.data = cancelOrder;
				result.success = true;
			}

			@Override
			public void error(JSONObject res) {
				result.data = -1;
				result.success = false;
			}
		});
		return result;
	}

	public CandlesResponse getTradeStatistics(long time, String curreny,
			String issuer) {
		return RippleChartsAPI.getTradeStatistics(curreny, issuer, time);
	}
}
