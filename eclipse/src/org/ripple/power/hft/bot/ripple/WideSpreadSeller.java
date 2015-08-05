package org.ripple.power.hft.bot.ripple;

import org.ripple.power.RippleSeedAddress;
import org.ripple.power.hft.bot.BOT_SET;
import org.ripple.power.hft.bot.BotLog;
import org.ripple.power.hft.bot.TraderBase;
import org.ripple.power.txns.RippleBackendsAPI;

public class WideSpreadSeller extends TraderBase{
	
     final double _operativeAmount;
     final double MIN_SPREAD = 0.0002;
     final double MIN_DIFFERENCE = 0.000015;
     final double MIN_PRICE_DELTA = 0.0000012;  
     boolean _selling = true;

     int _sellOrderId = -1;

     double _sellOrderPrice;

     int _buyOrderId = -1;
  
     double _buyOrderAmount;

     double _buyOrderPrice;
   
     double _executedSellPrice = -1.0;
     double _executedSellAmount;

     double _xrpBalance;
	
	 public WideSpreadSeller(RippleBackendsAPI api, RippleSeedAddress seed,
			BOT_SET set, BotLog log) {
		super(api, seed, set, log);
		this._operativeAmount = set.operative_amount;
		
	}

	@Override
	protected void check() {
	
	}

}
