package org.ripple.power.txns.btc;

import org.ripple.power.utils.StringUtils;

public class BTCPrice implements Comparable<BTCPrice> {

	public BTCStore store;

	public String price;

	public BTCPrice(BTCStore s) {
		this.store = s;
	}

	public BTCPrice(BTCStore s, String p) {
		this.store = s;
		this.price = p;
	}

	public String toString() {
		return String.format("%s %s", this.store, this.price);
	}

	@Override
	public int compareTo(BTCPrice o) {
		if (o == null || price == null || o.price == null) {
			return 0;
		}
		if (!StringUtils.isNumber(price) || !StringUtils.isNumber(o.price)) {
			return 0;
		}
		return (Double.valueOf(price) > Double.valueOf(o.price)) ? 1 : -1;
	}
}
