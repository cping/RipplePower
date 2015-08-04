package org.ripple.power.hft.bot.ripple;

import org.ripple.power.RippleSeedAddress;
import org.ripple.power.config.Loop;
import org.ripple.power.hft.bot.BotLog;
import org.ripple.power.timer.LTimerContext;
import org.ripple.power.txns.RippleBackendsAPI;
import org.ripple.power.txns.Updateable;
import org.ripple.power.txns.data.Offer;
import org.ripple.power.txns.data.OffersResponse;

public class RippleBOTLoader extends Loop {

	private RippleBackendsAPI rippleApi = new RippleBackendsAPI();

	private BotLog _log;

	final RippleSeedAddress _seed;

	public RippleBOTLoader(RippleSeedAddress seed, BotLog log) {
		this._seed = seed;
		this._log = log;
	}

	@Override
	public void runTaskTimer(LTimerContext context) {

	}

	@Override
	public Updateable main() {
		return new Updateable() {

			@Override
			public void action(Object o) {
				mainLoop();
			}
		};
	}

	public void cleanupZombies(final String address, final int buyOrderId,
			final int sellOrderId) {
		rippleApi.getActiveOrders(address, new Updateable() {

			@Override
			public void action(Object o) {

				if (o != null && o instanceof OffersResponse) {
					OffersResponse offers = (OffersResponse) o;
					if (offers == null || offers.result == null) {
						return;
					}
					for (Offer offer : offers.result.offers) {
						if (String.valueOf(offer.getPrice()).contains("12345"))
							_log.mes("Cleanup: Order ID=" + offer.seq
									+ " not a zombie, possibly manual");
						else if (-1 != buyOrderId && buyOrderId == offer.seq)
							_log.mes("Cleanup: Order ID=" + offer.seq
									+ " not a zombie, our BUY order");
						else if (-1 != sellOrderId && sellOrderId == offer.seq)
							_log.mes("Cleanup: Order ID=" + offer.seq
									+ " not a zombie, our SELL order");
						else {
							_log.mes(
									"Identified %s zombie order with ID=%s (%s XRP for %s %s). Trying to cancel...",
									offer.getType(), offer.seq,
									offer.getAmountXrp(), offer.getPrice(),
									offer.getCurrency());
							if (rippleApi.cancelSynOrder(_seed, offer.seq)) {
								_log.mes("... success");
							} else {
								_log.mes("... failed. Maybe next time");
							}
						}
					}
				}

			}
		});

	}

}
