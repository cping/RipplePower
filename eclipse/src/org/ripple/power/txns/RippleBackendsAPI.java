package org.ripple.power.txns;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.ripple.power.txns.data.AccountInfoRequest;
import org.ripple.power.txns.data.AccountInfoResponse;
import org.ripple.power.txns.data.AccountLinesResponse;
import org.ripple.power.txns.data.Line;
import org.ripple.power.txns.data.Offer;
import org.ripple.power.txns.data.OffersResponse;
import org.ripple.power.txns.data.RippleResult;
import org.ripple.power.txns.data.RippleResultListener;
import org.ripple.power.ui.RPClient;

import com.ripple.client.enums.Command;
import com.ripple.client.requests.Request;
import com.ripple.client.responses.Response;

//select rippled api or ripple rest api
public class RippleBackendsAPI {

	public static enum Model {
		RippleRestAPI, Rippled
	}

	public Model model = Model.Rippled;

	public AccountInfoRequest getAccountInfo(final String address,
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

		return null;
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
										result.data = line.balance;
										result.success = true;
										break;
									}
								} else if (currency != null) {
									if (line.currency
											.equalsIgnoreCase(currency)) {
										result.data = line.balance;
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

}
