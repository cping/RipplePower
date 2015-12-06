package org.ripple.power.txns.data;

import java.util.List;

import org.ripple.power.utils.StringUtils;

public class Balance {

	public static class BalancesResponse {
		public boolean success;
		public List<Balance> balances;

		public double getAvailable(String cur) {
			if (balances.size() > 0) {
				if (cur.equalsIgnoreCase(balances.get(0).currency)) {
					return balances.get(0).getAvailable();
				}
			}
			return 0.0d;
		}

	}

	public String value;
	public String currency;
	public String counterparty;

	public double getAvailable() {
		if (StringUtils.isEmpty(value)) {
			return 0.0d;
		}
		return Double.parseDouble(value);
	}
}
