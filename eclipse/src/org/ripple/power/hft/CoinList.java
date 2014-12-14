package org.ripple.power.hft;

import java.util.ArrayList;

public class CoinList {

	private String IssuedName;

	private double IssuedCount;

	private ArrayList<Coin> list = new ArrayList<Coin>(100);

	private long baseTime;

	public long getStartTimestamp() {
		return baseTime;
	}

	public long getTimestamp(int idx) {
		try {
			return list.get(idx).getTimestamp();
		} catch (Exception ex) {
			return -1;
		}
	}

	public Coin getCoinTime(long time) {
		for (Coin c : list) {
			if (c.getTimestamp() == time) {
				return c;
			}
		}
		return null;
	}

	public int size() {
		return list.size();
	}

	public double getIssuedCount() {
		return IssuedCount;
	}

	public ArrayList<Coin> getList() {
		return list;
	}

	public String getIssuedName() {
		return IssuedName;
	}

}
