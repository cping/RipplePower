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

public class CrazySellerTrap extends TraderBase {

	// XRP amount to trade
	private double _operativeAmount;
	private double _minWallVolume;
	private double _maxWallVolume;
	// Volumen of XRP necessary to accept our offer
	private double _volumeWall;
	// Minimum difference between BUY price and subsequent SELL price (so we
	// have at least some profit). Value from config.
	private double _minDifference;
	// Tolerance of BUY price. Usefull if possible price change is minor, to
	// avoid frequent order updates. Value from config.
	private double _minPriceUpdate; // fiat/XRP
	private final double MIN_ORDER_AMOUNT = 0.5;
	private String _currencyCode;
	private String _gateway_address;
	private int _counter;

	private double MIN_WALL_VOLUME = 100.0;

	// Active BUY order ID
	private long _buyOrderId = -1;
	// Active BUY order amount
	private double _buyOrderAmount;
	// Active BUY order price
	private double _buyOrderPrice;

	// Active SELL order ID
	private long _sellOrderId = -1;
	// Active SELL order amount
	private double _sellOrderAmount;
	// Active SELL order price
	private double _sellOrderPrice;
	// The price at which we bought from crazy buyer
	private double _executedBuyPrice = -1.0;

	private double _xrpBalance;

	public CrazySellerTrap(RippleBackendsAPI api, RippleSeedAddress seed,
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

		if ((trend == null) || (trend == RippleBOTLoader.Trend.UP)
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

			if (-1 != _buyOrderId) {
				Offer buyOrder = _rippleApi.getSynOrderInfo(_buyOrderId);

				if (buyOrder == null) {
					return;
				}

				if (!buyOrder.Closed) {
					if (Extensions.eq(buyOrder.getAmountXrp(), _buyOrderAmount)) {
						log("BUY order ID=%s untouched (amount=%s XRP, price=%s %s)",
								_buyOrderId, _buyOrderAmount, _buyOrderPrice,
								buyOrder.getCurrency());

						double price = suggestBuyPrice(market);
						double newAmount = _operativeAmount - _sellOrderAmount;

						if (newAmount > _buyOrderAmount
								|| !Extensions.eq(_buyOrderPrice, price)) {
							_buyOrderAmount = newAmount;
							_buyOrderId = _rippleApi.updateSynXRPBuyOrder(
									_buyOrderId, price, newAmount, _get);
							_buyOrderPrice = price;
							log("Updated BUY order ID=%s; amount=%s XRP; price=%s %s",
									_buyOrderId, _buyOrderAmount, price,
									buyOrder.getCurrency());
						}
					} else
					{
						_executedBuyPrice = buyOrder.getPrice();
						_buyOrderAmount = buyOrder.getAmountXrp();
						log("BUY order ID=%s partially filled at price=%s %s. Remaining amount=%s XRP;",
								_buyOrderId, _executedBuyPrice,
								buyOrder.getCurrency(), buyOrder.getAmountXrp());
		
						if (buyOrder.getAmountXrp() < MIN_ORDER_AMOUNT) {
							log("The remaining BUY amount is too small, canceling the order ID=%s",
									_buyOrderId);
							_rippleApi.cancelSynOrder(_buyOrderId); 
							_executedBuyPrice = _buyOrderPrice;
							_buyOrderId = -1;
							_buyOrderAmount = 0.0;
						} else {
							double price = suggestBuyPrice(market);
							_buyOrderId = _rippleApi.updateSynXRPBuyOrder(
									_buyOrderId, price,
									buyOrder.getAmountXrp(), _get);
							_buyOrderPrice = price;
							log("Updated BUY order ID=%s; amount=%s XRP; price=%s %s",
									_buyOrderId, _buyOrderAmount,
									_buyOrderPrice, buyOrder.getCurrency());
						}
					}
				} else {
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
						_executedBuyPrice = _buyOrderPrice;
						log("BUY order ID=%s (amount=%s XRP) was closed at price=%s %s",
								_buyOrderId, _buyOrderAmount,
								_executedBuyPrice, _currencyCode);
						_buyOrderId = -1;
						_buyOrderAmount = 0;
					}
				}
			} else if (_operativeAmount - _sellOrderAmount > 0.00001) 
			{
				_buyOrderPrice = suggestBuyPrice(market);
				_buyOrderAmount = _operativeAmount - _sellOrderAmount;
				_buyOrderId = _rippleApi.placeSynXRPBuyOrder(_buyOrderPrice,
						_buyOrderAmount, _get);

				if (-1 != _buyOrderId) {
					log("Successfully created BUY order with ID=%s; amount=%s XRP; price=%s %s",
							_buyOrderId, _buyOrderAmount, _buyOrderPrice,
							_currencyCode);
				}
			}

			if (_operativeAmount - _buyOrderAmount > 0.00001) {
				// SELL order already existed
				if (-1 != _sellOrderId) {
					Offer sellOrder = _rippleApi.getSynOrderInfo(_sellOrderId);

					if (null == sellOrder)
						return;

					// The order is still open
					if (!sellOrder.Closed) {
						log("SELL order ID=%s open (amount=%s XRP, price=%s %s)",
								_sellOrderId, sellOrder.getAmountXrp(),
								_sellOrderPrice, sellOrder.getCurrency());

						double price = suggestSellPrice(market);

						// Partially filled
						if (!Extensions.eq(sellOrder.getAmountXrp(),
								_sellOrderAmount)) {
							log("SELL order ID=%s partially filled at price=%s %s. Remaining amount=%s XRP;",
									_sellOrderId, sellOrder.getPrice(),
									sellOrder.getCurrency(),
									sellOrder.getAmountXrp());

							// Check remaining amount, drop the SELL if it's
							// very tiny
							if (sellOrder.getAmountXrp() < MIN_ORDER_AMOUNT) {
								log("The remaining SELL amount is too small, canceling the order ID=%s",
										_sellOrderId);
								_rippleApi.cancelSynOrder(_sellOrderId);
								_sellOrderId = -1;
								_sellOrderAmount = 0.0;
							} else {
								double amount = sellOrder.getAmountXrp();
								_sellOrderId = _rippleApi
										.updateSynXRPSellOrder(_sellOrderId,
												price, amount, _get);
								_sellOrderAmount = amount;
								_sellOrderPrice = price;
								log("Updated SELL order ID=%s; amount=%s XRP; price=%s %s",
										_sellOrderId, _sellOrderAmount, price,
										sellOrder.getCurrency());
							}
						}
						// If there were some money released by filling a BUY
						// order, increase this SELL order
						else if (_operativeAmount - _buyOrderAmount > _sellOrderAmount) {
							double newAmount = _operativeAmount
									- _buyOrderAmount;
							_sellOrderId = _rippleApi.updateSynXRPSellOrder(
									_sellOrderId, price, newAmount, _get);
							_sellOrderAmount = newAmount;
							_sellOrderPrice = price;
							log("Updated SELL order ID=%s; amount=%s XRP; price=%s %s",
									_sellOrderId, _sellOrderAmount, price,
									sellOrder.getCurrency());
						}
						// Or if we simply need to change price.
						else if (!Extensions.eq(_sellOrderPrice, price)) {
							_sellOrderId = _rippleApi
									.updateSynXRPSellOrder(_sellOrderId, price,
											_sellOrderAmount, _get);
							_sellOrderPrice = price;
							log("Updated SELL order ID=%s; amount=%s XRP; price=%s %s",
									_sellOrderId, _sellOrderAmount, price,
									sellOrder.getCurrency());
						}
					} else // Closed or cancelled
					{
						// Check if cancelled by the network
						double balance = _rippleApi.getSynXrpBalance();
						if (Extensions.eq(balance, _xrpBalance, 0.1)) {
							log("SELL order ID=%s closed but asset validation failed (balance=%s XRP). Asuming was cancelled, trying to recreate",
									_sellOrderId, balance);
							_sellOrderPrice = suggestSellPrice(market);
							_sellOrderId = _rippleApi.placeSynXRPSellOrder(
									_sellOrderPrice, _sellOrderAmount, _get);

							if (-1 != _sellOrderId) {
								log("Successfully created SELL order with ID=%s; amount=%s XRP; price=%s %s",
										_sellOrderId, _sellOrderAmount,
										_sellOrderPrice, _currencyCode);
							}
						} else {
							log("SELL order ID=%s (amount=%s XRP) was closed at price=%s %s",
									_sellOrderId, _sellOrderAmount,
									_sellOrderPrice, _currencyCode);
							_sellOrderAmount = 0;
							_sellOrderId = -1;
						}
					}
				} else // No SELL order, create one
				{
					_sellOrderPrice = suggestSellPrice(market);
					double amount = _operativeAmount - _buyOrderAmount;
					_sellOrderId = _rippleApi.placeSynXRPSellOrder(
							_sellOrderPrice, amount, _get);
					_sellOrderAmount = amount;

					if (-1 != _sellOrderId) {
						log("Successfully created SELL order with ID=%s; amount=%s XRP; price=%s %s",
								_sellOrderId, _sellOrderAmount,
								_sellOrderPrice, _currencyCode);
					}
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

	private double suggestBuyPrice(Market market) {
		final int decPlaces = 14;
		double increment = Math.pow(10.0, -1.0 * decPlaces); 
		double sum = 0;
		double lowestAsk = market.Asks.get(0).getPrice();

		for (Bid bid : market.Bids) {
			if (sum + _operativeAmount > _volumeWall
					&& bid.getPrice() + 2.0 * _minDifference < lowestAsk) {
				double buyPrice = MathUtils.round(bid.getPrice() + increment,
						decPlaces);

				if (-1 != _buyOrderId
						&& buyPrice < market.Bids.get(0).getPrice()
						&& Math.abs(buyPrice - _buyOrderPrice) < _minPriceUpdate) {
					log("DEBUG: BUY price %s too similar, using previous",
							buyPrice);
					return _buyOrderPrice;
				}

				return buyPrice;
			}
			sum += bid.getAmount();

			if (Extensions.eq(bid.getPrice(), _buyOrderPrice)) {
				sum -= _buyOrderAmount;
			}
		}

		double price = market.Bids.get(market.Bids.size() - 1).getPrice()
				+ increment;
		if (-1 != _buyOrderId
				&& Math.abs(price - _buyOrderPrice) < _minPriceUpdate) {
			return _buyOrderPrice;
		}
		return MathUtils.round(price, 7);
	}

	private double suggestSellPrice(Market market) {
		final int decPlaces = 14;
		double increment = Math.pow(10.0, -1.0 * decPlaces); 

		double sumVolume = 0.0;
		for (Ask ask : market.Asks) {

			if (Extensions.eq(ask.getPrice(), _sellOrderPrice)
					&& Extensions.eq(ask.getAmount(), _sellOrderAmount)) {
				continue;
			}

			sumVolume += ask.Amount;
			if (sumVolume < MIN_WALL_VOLUME) {
				continue;
			}

			if (ask.getPrice() > _executedBuyPrice + _minDifference) {
				return Extensions.eq(ask.getPrice(), _sellOrderPrice) ? _sellOrderPrice
						: MathUtils
								.round(ask.getPrice() - increment, decPlaces);
			}
		}

		return _executedBuyPrice + _minDifference;
	}

	public String getCurrencyCode() {
		return _currencyCode;
	}

	public String getGatewayAddress() {
		return _gateway_address;
	}
}
