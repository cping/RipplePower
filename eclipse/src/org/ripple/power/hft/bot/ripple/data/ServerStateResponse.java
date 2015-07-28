package org.ripple.power.hft.bot.ripple.data;

import org.ripple.power.hft.bot.ripple.Const;

public class ServerStateResponse {
	public Result result;
	public String status;
	public String type;

	public double getLastFee() {
		State s = result.state;
		double fee = (double) s.validated_ledger.base_fee * s.load_factor
				/ s.load_base;
		return fee / Const.DROPS_IN_XRP;
	}
}
