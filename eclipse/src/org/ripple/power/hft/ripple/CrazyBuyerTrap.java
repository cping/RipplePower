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
import org.ripple.power.utils.MathUtils;

public class CrazyBuyerTrap extends TraderBase {
	private double _operativeAmount;
	private double _minWallVolume;
	private double _maxWallVolume;
	private double _minDifference;
	private double _minPriceUpdate;
	// down price limit
	private double MIN_ORDER_AMOUNT = 0.5;

	private double NOT_SELL = 0.00001;
	private String _currencyCode;
	private String _gateway_address;
	private double _volumeWall;
	private int _counter;
	private long _sellOrderId = -1;
	private double _sellOrderAmount;
	private double _sellOrderPrice;
	private long _buyOrderId = -1;
	private double _buyOrderAmount;
	private double _buyOrderPrice;
	private double _executedSellPrice = -1.0;
	private double _xrpBalance;

	public CrazyBuyerTrap(RippleBackendsAPI api, RippleSeedAddress seed,
			BOT_SET set, BotLog log) {
		super(api, seed, set, log);
		this._operativeAmount = set.operative_amount;
		this._minWallVolume = set.min_volume;
		this._maxWallVolume = set.max_volume;
		this._minDifference = set.minDifference;
		this._minPriceUpdate = set.minPriceUpdate;
		this._currencyCode = set.currency_code;
		if (set.arbitrage) {
			if ((set.baseGateway == null) || (set.baseCurrency == null)
					|| (set.arbCurrency == null) || (set.arbGateway == null)) {
				throw new BOTException(
						"Configuration key 'baseGateway' or 'arbGateway' missing");
			}
			this._gateway_address = set.arbGateway;
			this._currencyCode = set.arbCurrency;
			this._pay = new IssuedCurrency(set.baseGateway, set.baseCurrency);
			this._get = new IssuedCurrency(set.arbGateway, set.arbCurrency);
		} else {
			if (set.gateway_address == null) {
				throw new BOTException(
						"Configuration key 'gateway_address' missing");
			}
			this._gateway_address = set.gateway_address;
			this._pay = IssuedCurrency.BASE;
			this._get = new IssuedCurrency(set.gateway_address, _currencyCode);
		}
		if (_log != null) {
			log("Zombie cleanup: " + _cleanup);
		}
	}

	@Override
	protected void check() {

		RippleBOTLoader.Trend trend = RippleBOTLoader
				.getTrend(_currencyCode, 5);

		if ((trend == null) || (trend == RippleBOTLoader.Trend.DOWN)
				|| (trend == RippleBOTLoader.Trend.UNKOWN)) {

			CandlesResponse candles = _rippleApi.getTradeStatistics(
					LSystem.DAY * 2, _get);
			Market market = _rippleApi.getSynMarketDepth(null, _pay.getTake(),
					_get.getTake(), query_limit);

			if (market == null) {
				return;
			}

			float coef = getMadness(candles.results);

			this._volumeWall = Extensions.suggestWallVolume(coef,
					_minWallVolume, _maxWallVolume);
			this._intervalMs = Extensions.suggestInterval(coef, _minInterval,
					_maxInterval);
			log("Madness=%s; Volume=%s XRP; Interval=%s ms", coef, _volumeWall,
					_intervalMs);

			if (_sellOrderId != -1) {
				Offer sellOrder = _rippleApi.getSynOrderInfo(_sellOrderId);

				if (sellOrder == null) {
					return;
				}
				if (!sellOrder.Closed) {
					if (Extensions.eq(sellOrder.getAmountXrp(),
							_sellOrderAmount)) {
						log("SELL order ID=%s untouched (amount=%s XRP, price=%s %s)",
								_sellOrderId, _sellOrderAmount,
								_sellOrderPrice, sellOrder.getCurrency());
						double price = suggestSellPrice(market);
						double newAmount = _operativeAmount - _buyOrderAmount;

						if (newAmount > _sellOrderAmount
								|| !Extensions.eq(_sellOrderPrice, price)) {
							this._sellOrderId = _rippleApi
									.updateSynXRPSellOrder(_sellOrderId, price,
											newAmount, _get);
							this._sellOrderAmount = newAmount;
							this._sellOrderPrice = price;
							log("Updated SELL order ID=%s; amount=%s XRP; price=%s %s",
									_sellOrderId, _sellOrderAmount, price,
									sellOrder.getCurrency());
						}

					} else {
						this._executedSellPrice = sellOrder.getPrice();
						this._sellOrderAmount = sellOrder.getAmountXrp();
						log("SELL order ID=%s partially filled at price=%s %s. Remaining amount=%s XRP;",
								_sellOrderId, _executedSellPrice,
								sellOrder.getCurrency(),
								sellOrder.getAmountXrp());

						// check update amount, drop the BUY if it's very tiny
						if (sellOrder.getAmountXrp() < MIN_ORDER_AMOUNT) {
							log("The remaining SELL amount is too small, canceling the order ID=%s",
									_sellOrderId);
							_rippleApi.cancelSynOrder(_sellOrderId);
							_executedSellPrice = _sellOrderPrice;
							_sellOrderId = -1;
							_sellOrderAmount = 0.0;
						} else {
							double price = suggestSellPrice(market);
							// The same price is totally unlikely, so we don't
							// check
							// it here
							double amount = sellOrder.getAmountXrp();
							_sellOrderId = _rippleApi.updateSynXRPSellOrder(
									_sellOrderId, price, amount, _get);
							_sellOrderAmount = amount;
							_sellOrderPrice = price;
							log("Updated SELL order ID=%s; amount=%s XRP; price=%s %s",
									_sellOrderId, _sellOrderAmount,
									_sellOrderPrice, sellOrder.getCurrency());
						}
					}
				} else { // offer close
					double balance = _rippleApi.getSynXrpBalance();
					if (Extensions.eq(balance, _xrpBalance, 0.1d)) {
						log("SELL order ID=%s closed but asset validation failed (balance=%s XRP). Asuming was cancelled, trying to recreate",
								_sellOrderId, balance);
						_sellOrderPrice = suggestSellPrice(market);
						_sellOrderId = _rippleApi.placeSynXRPSellOrder(
								_sellOrderPrice, _sellOrderAmount, _get);
						if (_sellOrderId != -1) {
							log("Successfully created SELL order with ID=%s; amount=%s XRP; price=%s %s",
									_sellOrderId, _sellOrderAmount,
									_sellOrderPrice, _currencyCode);
						}
					} else {
						_executedSellPrice = _sellOrderPrice;
						log("SELL order ID=%s (amount=%s XRP) was closed at price=%s %s",
								_sellOrderId, _sellOrderAmount,
								_executedSellPrice, _currencyCode);
						_sellOrderId = -1;
						_sellOrderAmount = 0;
					}

				}

			}
			// create sell order
			else if (_operativeAmount - _buyOrderAmount > NOT_SELL) {
				_sellOrderPrice = suggestSellPrice(market);
				double amount = _operativeAmount - _buyOrderAmount;
				_sellOrderId = _rippleApi.placeSynXRPSellOrder(_sellOrderPrice,
						amount, _get);
				_sellOrderAmount = amount;
				log("Successfully created SELL order with ID=%s; amount=%s XRP; price=%s %s",
						_sellOrderId, _sellOrderAmount, _sellOrderPrice,
						_currencyCode);

			}

			// buy order
			if (_operativeAmount - _sellOrderAmount > NOT_SELL) {

				// BUY order already existed
				if (-1 != _buyOrderId) {
					Offer buyOrder = _rippleApi.getSynOrderInfo(_buyOrderId);

					if (buyOrder == null) {
						return;
					}
					if (!buyOrder.Closed) {
						log("BUY order ID=%s open (amount=%s XRP, price=%s %s)",
								_buyOrderId, buyOrder.getAmountXrp(),
								_buyOrderPrice, buyOrder.getCurrency());

						double price = suggestBuyPrice(market);

						// Partially filled
						if (!Extensions.eq(buyOrder.getAmountXrp(),
								_buyOrderAmount)) {
							log("BUY order ID=%s partially filled at price=%s %s. Remaining amount=%s XRP;",
									_buyOrderId, buyOrder.getPrice(),
									buyOrder.getCurrency(),
									buyOrder.getAmountXrp());

							_buyOrderId = _rippleApi.updateSynXRPBuyOrder(
									_buyOrderId, price,
									buyOrder.getAmountXrp(), _get);
							_buyOrderAmount = buyOrder.getAmountXrp();
							_buyOrderPrice = price;
							log("Updated BUY order ID=%s; amount=%s XRP; price=%s %s",
									_buyOrderId, _buyOrderAmount, price,
									buyOrder.getCurrency());
						} else if (_operativeAmount - _sellOrderAmount > _buyOrderAmount) {
							double newAmount = _operativeAmount
									- _sellOrderAmount;
							log("SELL dumped some XRP. Increasing BUY amount to %s XRP",
									newAmount);
							_buyOrderId = _rippleApi.updateSynXRPBuyOrder(
									_buyOrderId, price, newAmount, _get);
							_buyOrderAmount = newAmount;
							_buyOrderPrice = price;
							log("Updated BUY order ID=%s; amount=%s XRP; price=%s %s",
									_buyOrderId, _buyOrderAmount, price,
									buyOrder.getCurrency());
						} else if (!Extensions.eq(_buyOrderPrice, price)) {
							_buyOrderId = _rippleApi.updateSynXRPBuyOrder(
									_buyOrderId, price, _buyOrderAmount, _get);
							_buyOrderPrice = price;
							log("Updated BUY order ID=%s; amount=%s XRP; price=%s %s",
									_buyOrderId, _buyOrderAmount, price,
									buyOrder.getCurrency());
						}

					} else {
						// Check if cancelled by Ripple due to "lack of funds"
						double balance = _rippleApi.getSynXrpBalance();
						if (Extensions.eq(balance, _xrpBalance, 0.1)) {
							log("BUY order ID=%s closed but asset validation failed (balance=%s XRP). Asuming was cancelled, trying to recreate",
									_buyOrderId, balance);
							_buyOrderPrice = suggestBuyPrice(market);
							_buyOrderId = _rippleApi.placeSynXRPBuyOrder(
									_buyOrderPrice, _buyOrderAmount, _get);

							if (-1 != _buyOrderId) {
								log("Successfully created BUY order with ID=%s; amount=%s XRP; price=%s %s",
										_buyOrderId, _buyOrderAmount,
										_buyOrderPrice, _currencyCode);
							}
						} else {
							log("BUY order ID=%s (amount=%s XRP) was closed at price=%s %s",
									_buyOrderId, _buyOrderAmount,
									_buyOrderPrice, _currencyCode);
							_buyOrderAmount = 0;
							_buyOrderId = -1;
						}

					}
				} else {
					// No BUY order, create one
					_buyOrderPrice = suggestBuyPrice(market);
					_buyOrderAmount = _operativeAmount - _sellOrderAmount;
					_buyOrderId = _rippleApi.placeSynXRPBuyOrder(
							_buyOrderPrice, _buyOrderAmount, _get);
					log("Successfully created BUY order with ID=%s; amount=%s XRP; price=%s %s",
							_buyOrderId, _buyOrderAmount, _buyOrderPrice,
							_currencyCode);

				}

			}

			if (_cleanup && ++_counter == ZOMBIE_CHECK) {
				_counter = 0;
				cleanupZombies(_seed.getPublicKey(), _buyOrderId, _sellOrderId,
						null);
			}
		}

		_xrpBalance = _rippleApi.getSynXrpBalance();
		log("### Balance= %s XRP", _xrpBalance);
	}

	private double suggestSellPrice(Market market) {
		final int decPlaces = 14;
		double increment = Math.pow(10.0, -1.0 * decPlaces);

		double sum = 0;
		double highestBid = market.Bids.get(0).getPrice();

		for (Ask ask : market.Asks) {
			if (sum + _operativeAmount > _volumeWall
					&& ask.getPrice() - _minDifference > highestBid) {
				double sellPrice = MathUtils.round(ask.getPrice() - increment,
						decPlaces);

				if (-1 != _sellOrderId
						&& sellPrice > market.Asks.get(0).getPrice()
						&& Math.abs(sellPrice - _sellOrderPrice) < _minPriceUpdate) {
					log("DEBUG: SELL price %s too similar, using previous",
							sellPrice);
					return _sellOrderPrice;
				}

				return sellPrice;
			}
			sum += ask.Amount;
			if (Extensions.eq(ask.getPrice(), _sellOrderPrice)) {
				sum -= _sellOrderAmount;
			}
		}

		double price = market.Asks.get(market.Asks.size() - 1).getPrice()
				- increment;
		if (_sellOrderId != -1
				&& Math.abs(price - _sellOrderPrice) < _minPriceUpdate) {
			return _sellOrderPrice;
		}
		return MathUtils.round(price, decPlaces);
	}

	private double suggestBuyPrice(Market market) {
		final int decPlaces = 14;
		double increment = Math.pow(10.0, -1.0 * decPlaces);

		double sumVolume = 0.0;
		for (Bid bid : market.Bids) {

			if (Extensions.eq(bid.getPrice(), _buyOrderPrice)
					&& Extensions.eq(bid.getAmount(), _buyOrderAmount)) {
				continue;
			}

			sumVolume += bid.getAmount();
			if (sumVolume < MIN_WALL_VOLUME) {
				continue;
			}

			if (bid.getPrice() < _executedSellPrice - _minDifference) {
				return Extensions.eq(bid.getPrice(), _buyOrderPrice, increment) ? _buyOrderPrice
						: MathUtils
								.round(bid.getPrice() + increment, decPlaces);
			}
		}
		return _executedSellPrice - _minDifference;
	}

	public String getCurrencyCode() {
		return _currencyCode;
	}

	public String getGatewayAddress() {
		return _gateway_address;
	}

}
