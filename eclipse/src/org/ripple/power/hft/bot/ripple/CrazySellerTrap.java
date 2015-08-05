package org.ripple.power.hft.bot.ripple;

import org.ripple.power.RippleSeedAddress;
import org.ripple.power.hft.bot.BOT_SET;
import org.ripple.power.hft.bot.BotLog;
import org.ripple.power.hft.bot.TraderBase;
import org.ripple.power.txns.RippleBackendsAPI;

public class CrazySellerTrap extends TraderBase{

	public CrazySellerTrap(RippleBackendsAPI api, RippleSeedAddress seed,
			BOT_SET set, BotLog log) {
		super(api, seed, set, log);
	}


	@Override
	protected void check() {

	}

}
