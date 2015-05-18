package org.ripple.power.hft;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.json.JSONObject;
import org.ripple.power.RippleSeedAddress;
import org.ripple.power.collection.ArrayMap;
import org.ripple.power.config.LSystem;
import org.ripple.power.timer.LTimer;
import org.ripple.power.timer.LTimerContext;
import org.ripple.power.txns.AccountFind;
import org.ripple.power.txns.AccountInfo;
import org.ripple.power.txns.AccountLine;
import org.ripple.power.txns.OfferPrice;
import org.ripple.power.txns.OtherData;
import org.ripple.power.txns.RippleItem;
import org.ripple.power.txns.OtherData.CoinmarketcapData;
import org.ripple.power.txns.Updateable;
import org.ripple.power.txns.OfferPrice.OfferFruit;

import com.ripple.core.coretypes.Amount;
import com.ripple.core.types.known.sle.entries.Offer;

public class TraderProcess extends TraderBase {

	private int analyze_limit = 5;

	private int orders_percent_filter = 1;

	private ArrayList<Task> _HFT_tasks = new ArrayList<Task>(10);

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
	public Trend getTrend(String curName, int trend_limit) {
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

	public static class Error {
		public int code;
		public String message;

		public Error(String mes) {
			this.message = mes;
		}

		public Error() {
			this("Empty");
		}
	}

	public static class Task {
		int id;
		public TraderProcess process;
		public RippleSeedAddress seed;
		public String target_issuer = "unkown";
		public String target_currency = "unkown";
		public String source_currency = "unkown";
		public String source_issuer = "unkown";
		public float value = -1;
		public float real_max_value = 0;
		public float limit_volume = 0;
		public float minDifference = 0.1f;
		public int orderId = -1;
		public float orderAmount = -1;
		public Model model = Model.Spreads;
		public ArrayList<RippleItem> swaps = new ArrayList<RippleItem>(10);
		public ArrayList<Error> errors = new ArrayList<Error>(10);
		public LTimer task_delay = new LTimer(LSystem.SECOND);
		public boolean stop;
		private long startTimeStamp = -1;

		public Task() {
			startTimeStamp = System.currentTimeMillis();
		}

		public long getStartTimeStamp() {
			return startTimeStamp;
		}

		public void update(LTimerContext context) {
			if (task_delay.action(context)) {

				if (seed == null) {
					return;
				}
				if (id <= 0) {
					return;
				}

				if (target_currency.equalsIgnoreCase(source_currency)) {
					return;
				}
				if (model == null) {
					errors.add(new Error());
					return;
				}
				if (!LSystem.nativeCurrency.equalsIgnoreCase(source_currency)
						&& !LSystem.nativeCurrency
								.equalsIgnoreCase(target_currency)
						&& !target_issuer.equalsIgnoreCase(source_issuer)) {
					return;
				}
				if (source_issuer == null
						|| "unkown".equalsIgnoreCase(source_issuer)) {
					return;
				}
				if (target_issuer == null
						|| "unkown".equalsIgnoreCase(target_issuer)) {
					return;
				}
				if (stop) {
					return;
				}

				// get limit trader price
				final float volumeWall = suggestWallVolume(real_max_value,
						limit_volume);

				// get average price
				String averagePrice = OfferPrice.getMoneyConvert("1",
						source_currency, target_currency);

				if (averagePrice == null
						|| "unkown".equalsIgnoreCase(averagePrice)) {
					averagePrice = "-1";
				}

				final float otherPrice = Float.valueOf(averagePrice);

				// load exchange data
				OfferPrice.load(source_issuer, source_currency,
						target_currency, new OfferPrice() {

							@Override
							public void sell(Offer offer) {

							}

							@Override
							public void error(JSONObject obj) {
								errors.add(new Error(obj.toString()));
							}

							@Override
							public void empty() {
								errors.add(new Error());
							}

							@Override
							public void complete(ArrayList<OfferFruit> buys,
									ArrayList<OfferFruit> sells,
									OfferPrice price) {
								// load data completed
								callCore(volumeWall, otherPrice, Task.this,
										buys, sells, price);
							}

							@Override
							public void buy(Offer offer) {

							}
						}, false);

			}
		}
	}

	private static void callCore(float volumeWall, float otherPrice, Task task,
			ArrayList<OfferFruit> buys, ArrayList<OfferFruit> sells,
			OfferPrice price) {
		// filter the transaction volume
		// 获得交易数量过滤条件,少于此交易数量的数据无视
		float filter = 0;
		if (volumeWall != 0) {
			filter = volumeWall / task.process.orders_percent_filter;
		}
		ArrayList<OfferFruit> list_buy = task.process.convertBuyPrice(buys,
				filter);
		ArrayList<OfferFruit> list_sell = task.process.convertSellPrice(sells,
				filter);
		float highBuy = 0;
		if (list_buy.size() > 0) {
			Offer buy_price = list_buy.get(0).offer;
			BigDecimal payForOne = buy_price.askQuality();
			Amount getsOne = buy_price.getsOne();
			highBuy = getsOne.divide(payForOne).floatValue();
		}
		float highSell = 0;
		if (list_sell.size() > 0) {
			Offer sell_price = list_sell.get(0).offer;
			BigDecimal payForOne = sell_price.askQuality();
			Amount paysOne = sell_price.paysOne();
			highSell = paysOne.multiply(payForOne).floatValue();
		}
		// 买入平均价
		float avg_buy_value = task.process.getBuyPrice(list_buy);
		// 卖出平均价
		float avg_sell_value = task.process.getSellPrice(list_sell);
		//
		float buy_difference = highBuy - avg_buy_value;
		float sell_difference = highSell - avg_buy_value;
		float all_buy_difference = 0;
		if (otherPrice != -1) {
			all_buy_difference = otherPrice - avg_buy_value;
		} else {
			all_buy_difference = buy_difference;
		}
		float all_sell_difference = 0;
		if (otherPrice != -1) {
			all_sell_difference = otherPrice - avg_sell_value;
		} else {
			all_sell_difference = sell_difference;
		}
		float trade_high_spread = highSell - highBuy;
		float trade_high_avgPrice = (highSell + highBuy) / 2;
		float trade_high_percentage = trade_high_spread / trade_high_avgPrice;

		System.out.println("highBuy:" + highBuy);
		System.out.println("highSell:" + highSell);
		System.out.println("trade_high_spread" + trade_high_spread);
		System.out.println("trade_high_avgPrice" + trade_high_avgPrice);
		System.out.println("trade_high_percentage" + trade_high_percentage);
		System.out.println("buy_difference" + buy_difference);
		System.out.println("sell_difference" + sell_difference);
		Trend trend = task.process.getTrend(task.source_currency, 12);
		System.out.println(otherPrice - highBuy);
		System.out.println("all_buy_difference:" + all_buy_difference);

		System.out.println(trend);
		System.out.println("buyd:" + all_buy_difference);
		System.out.println("selld:" + all_sell_difference);
		System.out.println("-------------------");
		switch (task.model) {
		case CrazyBuyer:

			log("" + avg_buy_value);
			log("" + avg_sell_value);
			callBuy(0, 0, task);

			break;
		case CrazySeller:
			callSell(0, 0, task);
			break;
		case Spreads:
			break;
		case Script:
			break;
		default:
			break;
		}
	}

	public void setAnalyzeLimit(int l) {
		this.analyze_limit = l;
	}

	public int getAnalyzeLimit() {
		return this.analyze_limit;
	}

	private ArrayList<OfferFruit> convertBuyPrice(ArrayList<OfferFruit> bids,
			float filter) {
		ArrayList<OfferFruit> tmp = new ArrayList<OfferFruit>(10);
		for (int i = 0; i < bids.size() && tmp.size() < analyze_limit; i++) {
			OfferFruit offer = bids.get(i);
			float v = offer.offer.takerPays().floatValue();
			if (v >= filter || equals(v, filter)) {
				tmp.add(offer);
			}
		}
		return tmp;
	}

	private float getBuyPrice(ArrayList<OfferFruit> bids) {
		float sumVolume = 0.0f;
		int size = bids.size();
		for (OfferFruit bid : bids) {
			float sellValue = Float.valueOf(LSystem.getNumber(
					bid.offer.bidQuality(), false));
			sumVolume += sellValue;
		}
		return Float.valueOf(LSystem.getNumberShort(String.valueOf(sumVolume
				/ size)));
	}

	private ArrayList<OfferFruit> convertSellPrice(ArrayList<OfferFruit> asks,
			float filter) {
		ArrayList<OfferFruit> tmp = new ArrayList<OfferFruit>(10);
		for (int i = 0; i < asks.size() && tmp.size() < analyze_limit; i++) {
			OfferFruit offer = asks.get(i);
			float v = offer.offer.takerGets().floatValue();
			if (v >= filter || equals(v, filter)) {
				tmp.add(offer);
			}
		}
		return tmp;
	}

	private float getSellPrice(ArrayList<OfferFruit> asks) {
		float sumVolume = 0.0f;
		int size = asks.size();
		for (OfferFruit ask : asks) {
			float sellValue = Float.parseFloat(LSystem.getNumber(
					ask.offer.askQuality(), false));
			sumVolume += sellValue;
		}
		return Float.valueOf(LSystem.getNumberShort(String.valueOf(sumVolume
				/ size)));
	}

	private static void log(String mes) {
		System.out.println(mes);
	}

	static void callBuy(float srcValue, float dstValue, Task task) {
		log("testing...");
	}

	static void callSell(float srcValue, float dstValue, Task task) {
		log("testing...");
	}

	public ArrayList<Task> getAllSeller() {
		ArrayList<Task> tasks = new ArrayList<Task>(10);
		for (Task t : _HFT_tasks) {
			if (t.model.equals(Model.CrazySeller)) {
				tasks.add(t);
			}
		}
		return tasks;
	}

	public ArrayList<Task> getAllBuyer() {
		ArrayList<Task> tasks = new ArrayList<Task>(10);
		for (Task t : _HFT_tasks) {
			if (t.model.equals(Model.CrazyBuyer)) {
				tasks.add(t);
			}
		}
		return tasks;
	}

	public ArrayList<Task> getUserSpreads() {
		ArrayList<Task> tasks = new ArrayList<Task>(10);
		for (Task t : _HFT_tasks) {
			if (t.model.equals(Model.Spreads)) {
				tasks.add(t);
			}
		}
		return tasks;
	}

	public ArrayList<Task> getUserScript() {
		ArrayList<Task> tasks = new ArrayList<Task>(10);
		for (Task t : _HFT_tasks) {
			if (t.model.equals(Model.Script)) {
				tasks.add(t);
			}
		}
		return tasks;
	}

	@Override
	public void runTaskTimer(LTimerContext context) {
		int size = _HFT_tasks.size();
		for (int i = 0; i < size; i++) {
			Task task = _HFT_tasks.get(i);
			task.update(context);

		}
	}

	@Override
	public Updateable main() {
		Updateable updateable = new Updateable() {

			@Override
			public void action(Object o) {
				mainLoop();
			}
		};
		return updateable;
	}

	public int getOrdersPercentFilter() {
		return orders_percent_filter;
	}

	public void setOrdersPercentFilter(int o) {
		this.orders_percent_filter = o;
	}

	private final void callTask(Task task) {
		task.id += 1;
		_HFT_tasks.add(task);
	}

	public void execute(final Task task) {
		if (task.seed == null) {
			return;
		}
		task.process = this;
		String address = task.seed.getPublicKey();
		AccountFind find = new AccountFind();
		final AccountInfo info = new AccountInfo();
		if (LSystem.nativeCurrency.equalsIgnoreCase(task.source_currency)) {
			find.processInfo(address, info, new Updateable() {

				@Override
				public void action(Object o) {
					String balance = info.balance;
					float srcXrpValue = Float.parseFloat(LSystem
							.getNumberShort(balance));
					task.real_max_value = srcXrpValue;
					callTask(task);

				}
			});
		} else {
			find.processLines(address, info, new Updateable() {

				@Override
				public void action(Object o) {

					ArrayList<AccountLine> lines = info.lines;
					if (lines.size() > 0) {
						for (AccountLine line : lines) {
							if (task.source_currency.equalsIgnoreCase(line
									.getCurrency())
									&& task.equals(line.getIssuer())) {
								float srcIouValue = Float.parseFloat(line
										.getAmount());
								task.real_max_value = srcIouValue;
							}
						}
					}
					callTask(task);
				}
			});

		}
	}

}
