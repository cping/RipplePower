package org.ripple.power.txns.data;

import org.json.JSONObject;
import org.ripple.power.config.LSystem;
import org.ripple.power.hft.bot.TradeType;
import org.ripple.power.txns.Const;

public class Offer {
	public int flags;
	public int seq;
	public Take taker_gets = new Take();
	public Take taker_pays = new Take();
	public boolean Closed;

	private double _amount;
	private double _amountXrp;

	public Offer() {
	}

	public Offer(boolean closed) {
		Closed = closed;
	}

	public TradeType getType() {
		return taker_gets.currency.equalsIgnoreCase(LSystem.nativeCurrency) ? TradeType.SELL
				: TradeType.BUY;
	}

	public double getAmountXrp() {
		if (_amountXrp == 0) {
			String value = TradeType.BUY == getType() ? taker_pays.value
					: taker_gets.value;
			double valNumber = Double.parseDouble(value);
			_amountXrp = valNumber / Const.DROPS_IN_XRP;
		}
		return _amountXrp;
	}

	public double getAmount() {
		if (_amount == 0) {
			String value = TradeType.BUY == getType() ? taker_gets.value
					: taker_pays.value;
			_amount = Double.parseDouble(value);
		}
		return _amount;
	}

	public double getPrice() {
		return getAmount() / getAmountXrp();
	}

	public String getCurrency() {
		return taker_gets.currency.equalsIgnoreCase(LSystem.nativeCurrency) ? taker_pays.currency
				: taker_gets.currency;
	}

	public void from(JSONObject obj) {
		if (obj != null) {
			this.flags = obj.optInt("flags");
			this.taker_gets.from(obj.opt("taker_gets"));
			this.taker_pays.from(obj.opt("taker_pays"));
			this.seq = obj.optInt("seq");
		}
	}
}
