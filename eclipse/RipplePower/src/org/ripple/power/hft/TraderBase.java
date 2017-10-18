package org.ripple.power.hft;

import java.util.Calendar;
import java.util.List;

import org.ripple.power.RippleSeedAddress;
import org.ripple.power.config.LSystem;
import org.ripple.power.txns.RippleBackendsAPI;
import org.ripple.power.txns.Updateable;
import org.ripple.power.txns.data.Ask;
import org.ripple.power.txns.data.Bid;
import org.ripple.power.txns.data.Candle;
import org.ripple.power.txns.data.Offer;
import org.ripple.power.txns.data.OffersResponse;
import org.ripple.power.txns.data.Take;
import org.ripple.power.utils.DateUtils;

import com.ripple.core.coretypes.RippleDate;

public abstract class TraderBase implements ITrader {

	protected double MIN_WALL_VOLUME = 100.0;
	protected final int ZOMBIE_CHECK = 10;
	protected int _minInterval = 8000;
	protected int _maxInterval = 20000;
	final double MIN_AVG_VOLUME = 400.0;
	final double MAX_AVG_VOLUME = 3000.0;
	final int MIN_TRADES = 2;
	final int MAX_TRADES = 10;
	private boolean _killSignal;
	protected int _intervalMs;
	protected boolean _cleanup;
	protected BotLog _log;
	protected RippleBackendsAPI _rippleApi;
	protected RippleSeedAddress _seed;
	protected BOT_SET _set;

	protected Take _pay;
	protected Take _get;

	protected int query_limit = 15;

	public TraderBase(RippleBackendsAPI api, RippleSeedAddress seed, BOT_SET set, BotLog log) {
		this._rippleApi = api;
		this._seed = seed;
		this._set = set;
		this._log = log;
	}

	@Override
	public void startTrading() {
		do {
			try {
				check();
				LSystem.sleep(_intervalMs);
			} catch (Exception ex) {
			}
		} while (!_killSignal);
	}

	@Override
	public void kill() {
		_killSignal = true;
	}

	protected abstract void check();

	protected void log(String message, Object... args) {
		if (_log != null) {
			_log.mes(message, args);
		}
	}

	public void cleanupZombies(final String address, final long buyOrderId, final long sellOrderId,
			final Updateable update) {
		_rippleApi.getActiveOrders(address, new Updateable() {

			@Override
			public void action(Object o) {

				if (o != null && o instanceof OffersResponse) {
					OffersResponse offers = (OffersResponse) o;
					if (offers == null || offers.result == null) {
						return;
					}
					for (Offer offer : offers.result.offers) {
						if (String.valueOf(offer.getPrice()).contains("12345"))
							log("Cleanup: Order ID=" + offer.seq + " not a zombie, possibly manual");
						else if (-1 != buyOrderId && buyOrderId == offer.seq)
							log("Cleanup: Order ID=" + offer.seq + " not a zombie, our BUY order");
						else if (-1 != sellOrderId && sellOrderId == offer.seq)
							log("Cleanup: Order ID=" + offer.seq + " not a zombie, our SELL order");
						else {
							log("Identified %s zombie order with ID=%s (%s XRP for %s %s). Trying to cancel...",
									offer.getType(), offer.seq, offer.getAmountXrp(), offer.getPrice(),
									offer.getCurrency());
							if (_rippleApi.cancelSynOrder(_seed, offer.seq)) {
								log("... success");
							} else {
								log("... failed. Maybe next time");
							}
						}
					}
				}
				if (update != null) {
					update.action(null);
				}
			}
		});
	}

	public float getMadness(List<Candle> candles) {
		if (candles == null || candles.size() == 0) {
			return 0.0f;
		}

		Candle last5mCandle = candles.get(candles.size() - 1);

		long startTime = last5mCandle.getStartTime().getTime();
		Calendar time = DateUtils.getUTCCalendar();
		time.setTime(RippleDate.now());
		time.set(Calendar.MINUTE, -10);
		if (startTime < time.getTimeInMillis()) {
			return 0.0f;
		}

		if (last5mCandle.isPartial()) {
			if (candles.size() > 1) {
				Candle beforeLast = candles.get(candles.size() - 2);
				last5mCandle = new Candle();

				last5mCandle.startTime = beforeLast.startTime;
				last5mCandle.count = beforeLast.count + last5mCandle.count;
				last5mCandle.baseVolume = beforeLast.baseVolume + last5mCandle.count;

			}
		}
		float intenseCoef;
		if (last5mCandle.count < MIN_TRADES) {
			intenseCoef = 0.0f;
		} else if (last5mCandle.count >= MAX_TRADES) {
			intenseCoef = 1.0f;
		} else {
			intenseCoef = (float) (last5mCandle.count - MIN_TRADES) / (MAX_TRADES - MIN_TRADES);
		}
		float volumeCoef;
		double avgVolume = last5mCandle.baseVolume / last5mCandle.count;
		if (avgVolume < MIN_AVG_VOLUME) {
			volumeCoef = 0.0f;
		} else if (avgVolume >= MAX_AVG_VOLUME) {
			volumeCoef = 1.0f;
		} else {
			volumeCoef = (float) ((avgVolume - MIN_AVG_VOLUME) / (MAX_AVG_VOLUME - MIN_AVG_VOLUME));
		}
		return (intenseCoef + volumeCoef) / 2;
	}

	protected float getSumAVGBuyPrice(List<Bid> bids) {
		double sumVolume = 0.0f;
		int count = 0;
		for (Bid bid : bids) {
			double price = bid.getPrice();
			if (bid.getAmount() > MIN_WALL_VOLUME) {
				sumVolume += price;
				count++;
			}
		}
		return Float.valueOf(LSystem.getNumberShort(String.valueOf(sumVolume / count)));
	}

	protected float getSumAVGSellPrice(List<Ask> asks) {
		double sumVolume = 0.0f;
		int count = 0;
		for (Ask ask : asks) {
			double price = ask.getPrice();
			if (ask.getAmount() > MIN_WALL_VOLUME) {
				sumVolume += price;
				count++;
			}
		}
		return Float.valueOf(LSystem.getNumberShort(String.valueOf(sumVolume / count)));
	}

	protected float getSumAVGBuyAmount(List<Bid> bids) {
		double sumVolume = 0.0f;
		int count = 0;
		for (Bid bid : bids) {
			double amount = bid.getAmount();
			if (amount > MIN_WALL_VOLUME) {
				sumVolume += amount;
				count++;
			}
		}
		return Float.valueOf(LSystem.getNumberShort(String.valueOf(sumVolume / count)));
	}

	protected float getSumAVGSellAmount(List<Ask> asks) {
		double sumVolume = 0.0f;
		int count = 0;
		for (Ask ask : asks) {
			double amount = ask.getAmount();
			if (amount > MIN_WALL_VOLUME) {
				sumVolume += amount;
				count++;
			}
		}
		return Float.valueOf(LSystem.getNumberShort(String.valueOf(sumVolume / count)));
	}

}
