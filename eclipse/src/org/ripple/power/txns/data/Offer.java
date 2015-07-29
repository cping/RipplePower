package org.ripple.power.txns.data;

import org.ripple.power.hft.bot.TradeType;
import org.ripple.power.txns.Const;

public class Offer {
	public int flags;
	public int seq;
	public Take taker_gets;
	public Take taker_pays;
	public boolean Closed;

	public TradeType getType() {
		return taker_gets.currency == "XRP" ? TradeType.SELL : TradeType.BUY;
	}

	private double _amountXrp;

	public double getAmountXrp() {

		if (_amountXrp == 0) {
			String value = TradeType.BUY == getType() ? taker_pays.value
					: taker_gets.value;
			double valNumber = Double.parseDouble(value);
			_amountXrp = valNumber / Const.DROPS_IN_XRP;
		}

		return _amountXrp;

	}

	private double _amount;

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

		return taker_gets.currency == "XRP" ? taker_pays.currency
				: taker_gets.currency;

	}

	public Offer() {

	}

	public Offer(boolean closed) {
		Closed = closed;
	}
}
