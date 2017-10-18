package org.ripple.power.hft.ripple;

import org.ripple.power.RippleSeedAddress;
import org.ripple.power.config.LSystem;
import org.ripple.power.hft.BOT_SET;
import org.ripple.power.hft.BotLog;
import org.ripple.power.hft.Extensions;
import org.ripple.power.hft.TraderBase;
import org.ripple.power.txns.IssuedCurrency;
import org.ripple.power.txns.RippleBackendsAPI;
import org.ripple.power.txns.data.Ask;
import org.ripple.power.txns.data.Bid;
import org.ripple.power.txns.data.CandlesResponse;
import org.ripple.power.txns.data.Market;
import org.ripple.power.txns.data.Offer;
import org.ripple.power.txns.data.Take;
import org.ripple.power.utils.MathUtils;

public class WideSpreadSeller extends TraderBase {

	final double _operativeAmount;
	final double MIN_SPREAD = 0.0002;
	final double MIN_DIFFERENCE = 0.000015;
	final double MIN_PRICE_DELTA = 0.0000012;

	private String _currencyCode;
	private String _gateway_address;

	private boolean _selling = true;
	private long _sellOrderId = -1;
	private double _sellOrderPrice;
	private long _buyOrderId = -1;
	private double _buyOrderAmount;
	private double _buyOrderPrice;
	private double _executedSellPrice = -1.0;
	private double _executedSellAmount;
	private double _xrpBalance;

	public WideSpreadSeller(RippleBackendsAPI api, RippleSeedAddress seed, BOT_SET set, BotLog log) {
		super(api, seed, set, log);
		this._operativeAmount = set.operative_amount;
		if (set.arbitrage) {
			if ((set.baseGateway == null) || (set.baseCurrency == null) || (set.arbCurrency == null)
					|| (set.arbGateway == null)) {
				throw new BOTException("Configuration key 'baseGateway' or 'arbGateway' missing");
			}
			this._gateway_address = set.arbGateway;
			this._currencyCode = set.arbCurrency;
			this._pay = new Take(set.baseCurrency, set.baseGateway);
			this._get = new Take(set.arbCurrency, set.arbGateway);
		} else {
			if (set.gateway_address == null) {
				throw new BOTException("Configuration key 'gateway_address' missing");
			}
			this._gateway_address = set.gateway_address;
			this._currencyCode = set.currency_code;
			this._pay = IssuedCurrency.BASE;
			this._get = new Take(_currencyCode, set.gateway_address);
		}
	}

	@Override
	protected void check() {

		CandlesResponse candles = _rippleApi.getTradeStatistics(LSystem.DAY * 2, _get);
		Market market = _rippleApi.getSynMarketDepth(null, _pay, _get, query_limit);

		if (market == null) {
			return;
		}
		double spread = MathUtils.round(getLowestAsk(market) - getHighestBid(market), 5);

		float coef = getMadness(candles.results);

		this._intervalMs = Extensions.suggestInterval(coef, _minInterval, _maxInterval);

		log("Madness=%s; spread={1:F5} XRP; Interval=%s ms", coef, spread, _intervalMs);

		if (_selling) {
			// No active SELL order
			if (-1 == _sellOrderId) {
				if (spread >= MIN_SPREAD) {
					double price = suggestSellPrice(market);
					double amount = _operativeAmount;
					_sellOrderId = _rippleApi.placeSynXRPSellOrder(price, amount, _get);

					if (-1 != _sellOrderId) {
						log("Successfully created SELL order with ID=%s; amount=%s XRP; price=%s %s", _sellOrderId,
								amount, price, _currencyCode);
						_sellOrderPrice = price;
					}
				} else
					log("Spread too small for selling");
			} else // We have active SELL order
			{
				Offer sellOrder = _rippleApi.getSynOrderInfo(_sellOrderId);

				if (null == sellOrder) {
					return;
				}

				if (!sellOrder.Closed) {
					// Untouched
					if (Extensions.eq(sellOrder.getAmountXrp(), _operativeAmount)) {
						log("SELL order ID=%s untouched (amount=%s XRP, price=%s %s)", _sellOrderId, _operativeAmount,
								_sellOrderPrice, _currencyCode);

						if (spread < MIN_SPREAD) {
							log("Spread too small, canceling order ID=%s", _sellOrderId);
							if (_rippleApi.cancelSynOrder(_sellOrderId)) {
								_sellOrderId = -1;
								_sellOrderPrice = -1;
							}
						} else {
							double price = suggestSellPrice(market);

							// Evaluate and update if needed
							if (!Extensions.eq(_sellOrderPrice, price)) {
								double amount = _operativeAmount;
								_sellOrderId = _rippleApi.updateSynXRPSellOrder(_sellOrderId, price, amount, _get);
								_sellOrderPrice = price;
								log("Updated SELL order ID=%s; amount=%s XRP; price=%s %s", _sellOrderId,
										_operativeAmount, price, _currencyCode);
							}
						}
					} else // Partially filled
					{
						_executedSellPrice = sellOrder.getPrice();
						_executedSellAmount = _operativeAmount - sellOrder.getAmountXrp();
						log("SELL order ID=%s partially filled at price=%s %s. Filled amount=%s XRP;", _sellOrderId,
								_executedSellPrice, _currencyCode, _executedSellAmount);

						// Cancel the rest of order
						if (_rippleApi.cancelSynOrder(_sellOrderId)) {
							log("Successfully cancelled SELL order ID=%s", _sellOrderId);
							_sellOrderId = -1;
							_selling = false;
						}
					}
				} else {
					// Check if cancelled by Ripple due to "lack of funds"
					double balance = _rippleApi.getSynXrpBalance();
					if (Extensions.eq(balance, _xrpBalance, 0.1)) {
						log("SELL order ID=%s closed but asset validation failed (balance=%s XRP). Asuming was cancelled, trying to recreate",
								_sellOrderId, balance);
						_sellOrderPrice = suggestSellPrice(market);
						double amount = _operativeAmount;
						_sellOrderId = _rippleApi.placeSynXRPSellOrder(_sellOrderPrice, amount, _get);

						if (-1 != _sellOrderId) {
							log("Successfully recreated SELL order with ID=%s; amount=%s XRP; price=%s %s",
									_sellOrderId, amount, _sellOrderPrice, _currencyCode);
						}
					} else {
						_executedSellPrice = _sellOrderPrice;
						_executedSellAmount = _operativeAmount;
						log("SELL order ID=%s (amount=%s XRP) was closed at price=%s %s", _sellOrderId,
								_operativeAmount, _executedSellPrice, _currencyCode);
						_sellOrderId = -1;
						_selling = false;
					}
				}
			}
		} else {
			if (-1 == _buyOrderId) {
				_buyOrderPrice = suggestBuyPrice(market);
				_buyOrderAmount = _executedSellAmount;
				_buyOrderId = _rippleApi.placeSynXRPBuyOrder(_buyOrderPrice, _buyOrderAmount, _get);
				log("Successfully created BUY order with ID=%s; amount=%s XRP; price=%s %s", _buyOrderId,
						_buyOrderAmount, _buyOrderPrice, _currencyCode);
			} else {
				Offer buyOrder = _rippleApi.getSynOrderInfo(_buyOrderId);

				if (null == buyOrder) {
					return;
				}

				if (!buyOrder.Closed) {
					log("BUY order ID=%s open (amount=%s XRP, price=%s %s)", _buyOrderId, buyOrder.getAmountXrp(),
							_buyOrderPrice, _currencyCode);

					double price = suggestBuyPrice(market);

					// Partially filled
					if (!Extensions.eq(buyOrder.getAmountXrp(), _buyOrderAmount)) {
						log("BUY order ID=%s partially filled at price=%s %s. Remaining amount=%s XRP;", _buyOrderId,
								buyOrder.getPrice(), _currencyCode, buyOrder.getAmountXrp());
						_buyOrderId = _rippleApi.updateSynXRPBuyOrder(_buyOrderId, price, buyOrder.getAmountXrp(),
								_get);
						_buyOrderAmount = buyOrder.getAmountXrp();
						_buyOrderPrice = price;
						log("Updated BUY order ID=%s; amount=%s XRP; price=%s %s", _buyOrderId, _buyOrderAmount, price,
								_currencyCode);
					}
					// We simply need to change price.
					else if (!Extensions.eq(_buyOrderPrice, price)) {
						_buyOrderId = _rippleApi.updateSynXRPBuyOrder(_buyOrderId, price, _buyOrderAmount, _get);
						_buyOrderPrice = price;
						log("Updated BUY order ID=%s; amount=%s XRP; price=%s %s", _buyOrderId, _buyOrderAmount, price,
								_currencyCode);
					}
				} else {
					// Check if cancelled by Ripple due to "lack of funds"
					double balance = _rippleApi.getSynXrpBalance();
					if (Extensions.eq(balance, _xrpBalance, 0.1)) {
						log("BUY order ID=%s closed but asset validation failed (balance=%s XRP). Asuming was cancelled, trying to recreate",
								_buyOrderId, balance);
						_buyOrderPrice = suggestBuyPrice(market);
						_buyOrderId = _rippleApi.placeSynXRPBuyOrder(_buyOrderPrice, _buyOrderAmount, _get);

						if (-1 != _buyOrderId)
							log("Successfully created BUY order with ID=%s; amount=%s XRP; price=%s %s", _buyOrderId,
									_buyOrderAmount, _buyOrderPrice, _currencyCode);
					} else {
						log("BUY order ID=%s (amount=%s XRP) was closed at price=%s %s", _buyOrderId, _buyOrderAmount,
								_buyOrderPrice, _currencyCode);
						_buyOrderAmount = 0;
						_buyOrderId = -1;
						_selling = true;
					}
				}
			}
		}

		_xrpBalance = _rippleApi.getSynXrpBalance();
		log("### Balance= %s XRP", _xrpBalance);

	}

	private double getLowestAsk(Market market) {
		double lowestAsk = market.Asks.get(0).getPrice();
		if (-1 != _sellOrderId) {
			Ask ask = market.Asks.get(0);
			if (Extensions.eq(ask.getAmount(), _operativeAmount) && Extensions.eq(ask.getPrice(), _sellOrderPrice)) {
				lowestAsk = market.Asks.get(1).getPrice();
			}
		}

		return lowestAsk;
	}

	private double getHighestBid(Market market) {
		double bidVolume = 0.0;

		for (Bid bid : market.Bids) {
			bidVolume += bid.getAmount();
			if (bidVolume > MIN_WALL_VOLUME) {
				return bid.getPrice();
			}
		}

		return market.Bids.get(market.Bids.size() - 1).getPrice();
	}

	private double suggestSellPrice(Market market) {
		double lowestAsk = getLowestAsk(market);
		double highestBid = market.Bids.get(0).getPrice();
		double spread = lowestAsk - highestBid;

		double sellPrice = MathUtils.round(lowestAsk - (spread / 3.0), 7);

		if (-1 != _sellOrderId && Math.abs(sellPrice - _sellOrderPrice) < MIN_PRICE_DELTA) {
			log("DEBUG: SELL price %s too similar, using previous", sellPrice);
			return _sellOrderPrice;
		}

		return sellPrice;
	}

	private double suggestBuyPrice(Market market) {

		double maxPrice = _executedSellPrice - MIN_DIFFERENCE;
		double highestBid = market.Bids.get(0).getPrice();

		double sumVolume = 0.0;
		for (Bid bid : market.Bids) {
			if (Extensions.eq(bid.getPrice(), _buyOrderPrice) && Extensions.eq(bid.getAmount(), _buyOrderAmount)) {
				continue;
			}
			sumVolume += bid.getAmount();
			if (sumVolume < MIN_WALL_VOLUME) {
				continue;
			}
			highestBid = bid.getPrice();
			break;
		}

		if (highestBid > maxPrice) {
			return maxPrice;
		}
		double buyPrice = maxPrice - ((maxPrice - highestBid) / 2.0);
		return MathUtils.round(buyPrice, 7);
	}

	public String getCurrencyCode() {
		return _currencyCode;
	}

	public String getGatewayAddress() {
		return _gateway_address;
	}
}
