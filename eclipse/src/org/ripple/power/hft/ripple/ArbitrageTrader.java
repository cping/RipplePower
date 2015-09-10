package org.ripple.power.hft.ripple;

import org.ripple.power.RippleSeedAddress;
import org.ripple.power.hft.BOT_SET;
import org.ripple.power.hft.BotLog;
import org.ripple.power.hft.TraderBase;
import org.ripple.power.txns.RippleBackendsAPI;
import org.ripple.power.txns.data.Market;
import org.ripple.power.txns.data.Offer;
import org.ripple.power.txns.data.Take;

public class ArbitrageTrader extends TraderBase {
	private final String _baseCurrency;
	private final String _arbCurrency;
	private final String _baseGateway;
	private final String _arbGateway;
	private final double _parity;
	private double _arbFactor = 1.007;
	private final double MIN_TRADE_VOLUME = 1.0;
	private final double MIN_ORDER_AMOUNT = 0.5;
	
	private int _counter;

	private double _lastValidXrpBalance = -1.0;

	public ArbitrageTrader(RippleBackendsAPI api, RippleSeedAddress seed,
			BOT_SET set, BotLog log) {
		super(api, seed, set, log);
		_baseCurrency = set.baseCurrency;
		_baseGateway = set.baseGateway;

		_arbCurrency = set.arbCurrency;
		_arbGateway = set.arbGateway;

		this._pay = new Take(set.baseCurrency,set.baseGateway);
		this._get = new Take(set.arbCurrency,set.arbGateway);

		this._parity = Double.parseDouble(set.parity);
		this._arbFactor = Double.parseDouble(set.arbFactor);
	}

	@Override
	protected void check() {

		Market baseMarket = _rippleApi.getSynXRPMarketDepth(null,
				_pay, query_limit);

		if (null == baseMarket || null == baseMarket.Asks
				|| null == baseMarket.Bids) {
			return;
		}

		Market arbMarket = _rippleApi.getSynXRPMarketDepth(null,
				_get, query_limit);

		if (null == arbMarket || null == arbMarket.Asks
				|| null == arbMarket.Bids) {
			return;
		}

		double baseBalance = _rippleApi.getSynBalance(_pay);
		double arbBalance = _rippleApi.getSynBalance(_get);

		double xrpBalance = _rippleApi.getSynXrpBalance();
		log("Balances: %s %s; %s %s; %s XRP", baseBalance, _baseCurrency,
				arbBalance, _arbCurrency, xrpBalance);

		double lowestBaseAskPrice = baseMarket.Asks.get(0).getPrice();
		double highestArbBidPrice = arbMarket.Bids.get(0).getPrice();
		double baseRatio = highestArbBidPrice / lowestBaseAskPrice;

		double lowestArbAskPrice = arbMarket.Asks.get(0).getPrice();
		double highestBaseBidPrice = baseMarket.Bids.get(0).getPrice();
		double arbRatio = lowestArbAskPrice / highestBaseBidPrice;

		log("BASIC ratio=%s; ARB ratio=%s", baseRatio, arbRatio);

		if (Double.isNaN(baseRatio) || Double.isNaN(arbRatio)) {
			return; 
		}

		if (baseBalance >= 0.1) {
			if (baseRatio > _parity * _arbFactor) {
				if (baseRatio > _parity * 1.1 || baseRatio < _parity * 0.9) {
					log("BASIC ratio has suspicious value %s. Let's leave it be",
							baseRatio);
					return;
				}

				log("Chance to buy cheap %s (BASIC ratio %s > %s)",
						_arbCurrency, baseRatio, _parity * _arbFactor);
				double baseVolume = baseMarket.Asks.get(0).getAmount();
				double arbVolume = arbMarket.Bids.get(0).getAmount();
				if (baseVolume < MIN_TRADE_VOLUME
						|| arbVolume < MIN_TRADE_VOLUME) {
					log("Insufficient volume: %s XRP for %s; %s XRP for %s",
							baseVolume, _baseCurrency, arbVolume, _arbCurrency);
				} else {
					// Try to buy XRP for BASIC
					double amount = Math.min(baseVolume, arbVolume);
					long orderId = _rippleApi.placeSynXRPBuyOrder(
							lowestBaseAskPrice + 0.00001, amount, _pay);
					log("Tried to buy %s XRP for %s %s each. OrderID=%s",
							amount, lowestBaseAskPrice, _baseCurrency, orderId);
					Offer orderInfo = _rippleApi.getSynOrderInfo(orderId);

					if (null != orderInfo && orderInfo.Closed) {
						double newXrpBalance = _rippleApi.getSynXrpBalance();
						amount = newXrpBalance - xrpBalance;
						log("Buy XRP orderID=%s filled OK, bought %s XRP",
								orderId, amount);
						amount -= MIN_ORDER_AMOUNT; 
						long arbBuyOrderId = _rippleApi.placeSynXRPSellOrder(
								highestArbBidPrice * 0.9, amount, _get); 
						log("Tried to sell %s XRP for %s %s each. OrderID=%s",
								amount, highestArbBidPrice, _arbCurrency,
								arbBuyOrderId);
						Offer arbBuyOrderInfo = _rippleApi
								.getSynOrderInfo(arbBuyOrderId);
						if (null != arbBuyOrderInfo && arbBuyOrderInfo.Closed) {
							log("Buy %s orderID=%s filled OK", _arbCurrency,
									arbBuyOrderId);
							log("%s -> %s ARBITRAGE SUCCEEDED!",
									_baseCurrency, _arbCurrency);
						} else {
							log("OrderID=%s (sell %s XRP for %s %s each) remains dangling. Forgetting it...",
									arbBuyOrderId,
									arbBuyOrderInfo.getAmountXrp(),
									arbBuyOrderInfo.getPrice(), _arbCurrency);
						}
					} else {
						log("OrderID=%s (buy %s XRP for %s %s each) remains dangling. Trying to cancel...",
								orderId, orderInfo.getAmountXrp(),
								orderInfo.getPrice(), _baseCurrency);
						if (_rippleApi.cancelSynOrder(orderId)) {
							log("...success");
						} else {
							log("...failed");
						}
					}
				}
			}
		}

		if (arbBalance >= 0.1) {
			if (arbRatio < _parity) {
				if (arbRatio > _parity * 1.1 || arbRatio < _parity * 0.9) {
					log("ARB ratio has suspicious value %s. Let's leave it be",
							arbRatio);
					return;
				}

				log("Chance to sell %s for %s (ARB ratio %s < {3:0.00000})",
						_arbCurrency, _baseCurrency, arbRatio, _parity);
				double arbVolume = arbMarket.Asks.get(0).getAmount();
				double baseVolume = baseMarket.Bids.get(0).getAmount();
				if (arbVolume < MIN_TRADE_VOLUME
						|| baseVolume < MIN_TRADE_VOLUME) {
					log("Insufficient volume: %s XRP for %s; %s XRP for %s",
							arbVolume, _arbCurrency, baseVolume, _baseCurrency);
				} else {
					// Try to buy XRP for ARB
					double amount = Math.min(baseVolume, arbVolume);
					long orderId = _rippleApi.placeSynXRPBuyOrder(
							lowestArbAskPrice + 0.00001, amount, _get);
					log("Tried to buy %s XRP for %s %s each. OrderID=%s",
							amount, lowestArbAskPrice, _arbCurrency, orderId);
					Offer orderInfo = _rippleApi.getSynOrderInfo(orderId);

					if (null != orderInfo && orderInfo.Closed) {
						double newXrpBalance = _rippleApi.getSynXrpBalance();
						amount = newXrpBalance - xrpBalance;
						log("Buy XRP orderID=%s filled OK, bought %s XRP",
								orderId, amount);
						// Try to sell XRP for BASIC
						long baseBuyOrderId = _rippleApi.placeSynXRPSellOrder(
								highestBaseBidPrice * 0.9, amount, _pay); 
						log("Tried to sell %s XRP for %s %s each. OrderID=%s",
								amount, highestBaseBidPrice, _baseCurrency,
								baseBuyOrderId);
						Offer baseBuyOrderInfo = _rippleApi
								.getSynOrderInfo(baseBuyOrderId);
						if (null != baseBuyOrderInfo && baseBuyOrderInfo.Closed) {
							log("Buy %s orderID=%s filled OK", _baseCurrency,
									baseBuyOrderId);
							log("%s -> %s ARBITRAGE SUCCEEDED!",
									_arbCurrency, _baseCurrency);
						} else {
							log("OrderID=%s (sell %s XRP for %s %s each) remains dangling. Forgetting it...",
									baseBuyOrderId,
									baseBuyOrderInfo.getAmountXrp(),
									baseBuyOrderInfo.getPrice(), _baseCurrency);
						}
					} else {
						log("OrderID=%s (buy %s XRP for %s %s each) remains dangling. Trying to cancel...",
								orderId, orderInfo.getAmountXrp(),
								orderInfo.getPrice(), _arbCurrency);
						if (_rippleApi.cancelSynOrder(orderId)) {
							log("...success");
						} else {
							log("...failed");
						}
					}
				}
			}
		}

		if (++_counter == ZOMBIE_CHECK) {
			_counter = 0;
			cleanupZombies(_baseGateway, -1, -1, null);
			cleanupZombies(_arbGateway, -1, -1, null);
		}

		if (_lastValidXrpBalance > 0.0
				&& xrpBalance - 2.0 > _lastValidXrpBalance) {
			long orderId = -1;
			double amount = xrpBalance - _lastValidXrpBalance;
			log("Balance {0:0.000} XRP is too high. Must convert %s to fiat.",
					xrpBalance, amount);
			if (baseRatio > _parity * _arbFactor) {
				log("Converting to %s", _arbCurrency);
				orderId = _rippleApi.placeSynXRPSellOrder(
						highestArbBidPrice * 0.9, amount, _get);
			} else if (arbRatio < _parity) {
				log("Converting to %s", _baseCurrency);
				orderId = _rippleApi.placeSynXRPSellOrder(
						highestBaseBidPrice * 0.9, amount, _pay);
			} else {
				double baseDiffFromSell = (_parity * _arbFactor) - baseRatio;
				double arbDiffFromBuyback = arbRatio - _parity;

				if (baseDiffFromSell < arbDiffFromBuyback) {
					log("Better converting to %s", _arbCurrency);
					orderId = _rippleApi.placeSynXRPSellOrder(
							highestArbBidPrice * 0.9, amount, _get);
				} else {
					log("Bettger converting to %s", _baseCurrency);
					orderId = _rippleApi.placeSynXRPSellOrder(
							highestBaseBidPrice * 0.9, amount, _pay);
				}
			}
			log("OrderId : %s", orderId);
		} else if (xrpBalance > -1.0) {
			_lastValidXrpBalance = xrpBalance;
		}

	}

}
