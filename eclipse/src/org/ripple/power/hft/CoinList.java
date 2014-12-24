package org.ripple.power.hft;

import java.util.ArrayList;

import org.ripple.power.txns.OfferPrice.OfferFruit;

import com.ripple.core.types.known.sle.entries.Offer;

public class CoinList {

	private String IssuedName;

	private double IssuedCount;

	private ArrayList<Coin> _list = new ArrayList<Coin>(100);

	private long baseTime;

	public long getStartTimestamp() {
		return baseTime;
	}

	public long getTimestamp(int idx) {
		try {
			return _list.get(idx).getTimestamp();
		} catch (Exception ex) {
			return -1;
		}
	}

	public Coin getCoinTime(long time) {
		for (Coin c : _list) {
			if (c.getTimestamp() == time) {
				return c;
			}
		}
		return null;
	}

	public int size() {
		return _list.size();
	}

	public double getIssuedCount() {
		return IssuedCount;
	}

	public ArrayList<Coin> getList() {
		return _list;
	}

	public String getIssuedName() {
		return IssuedName;
	}

}
