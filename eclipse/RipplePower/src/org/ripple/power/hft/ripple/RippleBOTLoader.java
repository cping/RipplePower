package org.ripple.power.hft.ripple;

import java.util.ArrayList;

import org.ripple.power.collection.ArrayMap;
import org.ripple.power.config.LSystem;
import org.ripple.power.config.Loop;
import org.ripple.power.hft.TraderBase;
import org.ripple.power.timer.LTimerContext;
import org.ripple.power.txns.OtherData;
import org.ripple.power.txns.Updateable;
import org.ripple.power.txns.OtherData.CoinmarketcapData;

public class RippleBOTLoader extends Loop {

	public static enum Model {
		CrazyBuyer, CrazySeller, Spreads, Script
	}

	public static enum Trend {
		UP, DOWN, UNKOWN;
	}

	protected static ArrayList<Store> _storage = new ArrayList<Store>();

	private static class Store {
		public Trend trend = Trend.UNKOWN;
		public String name;
		public long date = 0;

		public Store(String name, Trend trend) {
			this.trend = trend;
			this.name = name;
			this.date = System.currentTimeMillis();
		}

	}

	private static Trend reset(String name) {
		for (Store s : _storage) {
			if (s.name.equals(name)
					&& (System.currentTimeMillis() - s.date) <= LSystem.MINUTE * 10) {
				return s.trend;
			} else if (s.name.equals(name)) {
				_storage.remove(s);
				return null;
			}
		}
		return null;
	}

	private static void addStorage(Store s) {
		_storage.add(s);
		if (_storage.size() > 100) {
			_storage.remove(0);
		}
	}

	/**
	 * get Price trends
	 * 
	 * @param curName
	 * @param trend_limit
	 *            (1==10minute)
	 * @return
	 */
	public static Trend getTrend(String curName, int trend_limit) {
		curName = curName.trim().toLowerCase();
		Trend result = reset(curName);
		if (result == null || result == Trend.UNKOWN) {
			try {
				if (trend_limit <= 1) {
					trend_limit = 2;
				}
				ArrayMap arrays = OtherData.getCapitalization(1, curName,
						trend_limit);
				if (arrays.size() == 0) {
					result = Trend.UNKOWN;
				} else {
					int up_coherent_flag = 0;
					int down_coherent_flag = 0;
					int size = arrays.size();
					int limit = size - trend_limit;
					if (limit < 0) {
						limit = 0;
					}
					Trend last = Trend.UNKOWN;
					for (int i = size - trend_limit; i < size; i++) {
						if (i + 1 < size) {
							long one = (long) arrays.get(i);
							long two = (long) arrays.get(i + 1);

							if (two >= one) {
								if (last == Trend.DOWN) {
									up_coherent_flag--;
								} else {
									up_coherent_flag++;
								}
								last = Trend.UP;
							} else if (two < one) {
								if (last == Trend.UP) {
									down_coherent_flag--;
								} else {
									down_coherent_flag++;
								}
								last = Trend.DOWN;
							}
						}
					}
					if (down_coherent_flag - 1 == up_coherent_flag) {
						return Trend.UP;
					} else if (down_coherent_flag > up_coherent_flag) {
						return Trend.DOWN;
					} else if (up_coherent_flag > down_coherent_flag) {
						return Trend.UP;
					} else {
						return Trend.UNKOWN;
					}
				}
			} catch (Exception e) {
				try {
					CoinmarketcapData data = OtherData.getCoinmarketcapTo(
							"usd", curName);
					if (data.change7h.indexOf("-") == -1
							&& data.change1h.indexOf("-") == -1) {
						result = Trend.UP;
					} else {
						result = Trend.DOWN;
					}
				} catch (Exception ex) {
					addStorage(new Store(curName, Trend.UNKOWN));
				}
			}
		}
		return result == null ? Trend.UNKOWN : result;
	}
	
	private TraderBase _base;

	public RippleBOTLoader(TraderBase base) {
		this._base = base;
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

	public TraderBase getBase() {
		return _base;
	}

	public void setBase(TraderBase base) {
		this._base = base;
	}

}
