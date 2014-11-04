package org.ripple.power.hft;

import java.util.ArrayList;
import java.util.List;

import org.address.collection.ArrayMap;
import org.address.ripple.RippleSeedAddress;
import org.json.JSONObject;
import org.ripple.power.config.LSystem;
import org.ripple.power.timer.LTimer;
import org.ripple.power.timer.LTimerContext;
import org.ripple.power.timer.SystemTimer;
import org.ripple.power.txns.AccountFind;
import org.ripple.power.txns.AccountInfo;
import org.ripple.power.txns.AccountLine;
import org.ripple.power.txns.OfferPrice;
import org.ripple.power.txns.OtherData;
import org.ripple.power.txns.OtherData.CoinmarketcapData;
import org.ripple.power.txns.Updateable;
import org.ripple.power.txns.OfferPrice.OfferFruit;
import org.ripple.power.utils.MathUtils;

import com.ripple.core.types.known.sle.entries.Offer;

public class TraderProcess extends TraderBase {

	private int analyze_limit = 5;

	private int orders_percent_filter;

	private ArrayList<Task> _HFT_tasks = new ArrayList<Task>(10);

	private long _lastTimeMicros, _currTimeMicros, _goalTimeMicros,
			_elapsedTimeMicros, _remainderMicros, _elapsedTime;

	private long _maxFrames = 60;

	private Thread _mainLoop = null;

	private final Object _synch = new Object();

	private final LTimerContext _timerContext = new LTimerContext();

	private SystemTimer _timer;

	private boolean _isRunning, _isPause, _isDestroy, _isResume;

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
							if (two > one) {
								if (last == Trend.DOWN) {
									up_coherent_flag--;
								} else {
									up_coherent_flag++;
								}
								last = Trend.UP;
							} else {
								if (last == Trend.UP) {
									down_coherent_flag--;
								} else {
									down_coherent_flag++;
								}
								last = Trend.DOWN;
							}
						}
					}
					if (down_coherent_flag > up_coherent_flag) {
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
		public double value = -1;
		public double real_max_value = -1;
		public double limit_volume = -1;
		public double minDifference = 0.1;
		public int orderId = -1;
		public double orderAmount = -1;
		public Model model = Model.Spreads;
		public ArrayList<Swap> swaps = new ArrayList<Swap>(10);
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
				final double volumeWall = suggestWallVolume(real_max_value,
						limit_volume);

				// get average price
				String averagePrice = OfferPrice.getMoneyConvert("1",
						source_currency, target_currency);

				if (averagePrice == null
						|| "unkown".equalsIgnoreCase(averagePrice)) {
					averagePrice = "-1";
				}

				final double otherPrice = Double.parseDouble(averagePrice);

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

								// accurate to five decimal places
								String highBuy = null;
								if (price.highBuy != null) {
									if (price.highBuy.indexOf("/") != -1) {
										highBuy = price.highBuy.split("/")[0];
									}
									highBuy = LSystem.getNumberShort(highBuy);
								}
								String hightSell = null;
								if (price.hightSell != null) {
									if (price.hightSell.indexOf("/") != -1) {
										hightSell = price.hightSell.split("/")[0];
									}
									hightSell = LSystem
											.getNumberShort(hightSell);
								}

								log("other:" + otherPrice);
								log("buy:" + highBuy);
								log("sell:" + hightSell);
								// load data completed
								callCore(volumeWall, otherPrice, Task.this,
										buys, sells, price,
										Double.parseDouble(highBuy),
										Double.parseDouble(hightSell));
							}

							@Override
							public void buy(Offer offer) {

							}
						}, false);

			}
		}
	}

	private static void callCore(double volumeWall, double otherPrice,
			Task task, ArrayList<OfferFruit> buys, ArrayList<OfferFruit> sells,
			OfferPrice price, double highBuy, double hightSell) {
		//filter the transaction volume
		double filter = volumeWall / task.process.orders_percent_filter;
		double avg_buy_value = task.process.averageBuyPrice(buys,filter);
		double avg_sell_value = task.process.averageSellPrice(sells,filter);
		double buy_difference = highBuy - avg_buy_value;
		double sell_difference = hightSell - avg_buy_value;
		double all_buy_difference = 0;
		if (otherPrice != -1) {
			all_buy_difference = otherPrice - avg_buy_value;
		} else {
			all_buy_difference = buy_difference;
		}
		double all_sell_difference = 0;
		if (otherPrice != -1) {
			all_sell_difference = otherPrice - avg_sell_value;
		} else {
			all_sell_difference = sell_difference;
		}
		Trend trend = task.process.getTrend(task.source_currency, 12);

		System.out.println(all_buy_difference);
		System.out.println(all_sell_difference);
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

	private double averageBuyPrice(ArrayList<OfferFruit> bids, double filter) {
		double sumVolume = 0.0d;
		List<OfferFruit> tmp = null;
		if (bids.size() > analyze_limit) {
			tmp = bids.subList(0, analyze_limit);
		} else {
			tmp = bids;
		}
		int size = tmp.size();
		for (OfferFruit bid : tmp) {
			double sellValue = Double.parseDouble(LSystem.getNumber(
					bid.offer.bidQuality(), false));
			sumVolume += sellValue;
		}
		return sumVolume / size;
	}

	private double averageSellPrice(ArrayList<OfferFruit> asks, double filter) {
		double sumVolume = 0.0d;
		List<OfferFruit> tmp = null;
		if (asks.size() > analyze_limit) {
			tmp = asks.subList(0, analyze_limit);
		} else {
			tmp = asks;
		}
		int size = tmp.size();
		for (OfferFruit ask : tmp) {
			double sellValue = Double.parseDouble(LSystem.getNumber(
					ask.offer.askQuality(), false));
			sumVolume += sellValue;
		}
		return sumVolume / size;
	}

	private static void log(String mes) {
		System.out.println(mes);
	}

	static void callBuy(double srcValue, double dstValue, Task task) {
		log("testing...");
	}

	static void callSell(double srcValue, double dstValue, Task task) {
		log("testing...");
	}

	public boolean isRunning() {
		return _isRunning;
	}

	public boolean isPause() {
		return _isPause;
	}

	public boolean isResume() {
		return _isResume;
	}

	public boolean isDestroy() {
		return _isDestroy;
	}

	public void setFPS(long frames) {
		this._maxFrames = frames;
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

	private Updateable main() {
		Updateable updateable = new Updateable() {

			@Override
			public void action(Object o) {

				for (; _isRunning;) {
					_goalTimeMicros = _lastTimeMicros + 1000000L / _maxFrames;
					_currTimeMicros = _timer.sleepTimeMicros(_goalTimeMicros);
					_elapsedTimeMicros = _currTimeMicros - _lastTimeMicros
							+ _remainderMicros;
					_elapsedTime = MathUtils
							.max(0, (_elapsedTimeMicros / 1000));
					_remainderMicros = _elapsedTimeMicros - _elapsedTime * 1000;
					_lastTimeMicros = _currTimeMicros;
					_timerContext.millisSleepTime = _remainderMicros;
					_timerContext.timeSinceLastUpdate = _elapsedTime;
					runTaskTimer(_timerContext);
					if (_isPause) {
						pause(500);
					}
				}

			}
		};
		return updateable;
	}

	public void loop() {
		_isRunning = true;
		if (_timer == null) {
			_timer = new SystemTimer();
		}

		if (_mainLoop == null) {
			_mainLoop = new Thread() {
				public void run() {
					main().action(this);
				}
			};
			_mainLoop.start();
		}
	}

	private void runTaskTimer(LTimerContext context) {
		int size = _HFT_tasks.size();
		for (int i = 0; i < size; i++) {
			Task task = _HFT_tasks.get(i);
			task.update(context);

		}
	}

	private final void pause(long sleep) {
		try {
			Thread.sleep(sleep);
		} catch (InterruptedException ex) {
		}
	}

	final void resume() {
		synchronized (_synch) {
			if (_isRunning || _mainLoop != null) {
				_isRunning = false;
				if (_mainLoop != null) {
					_mainLoop.interrupt();
					_mainLoop = null;
				}
			}
			_isRunning = true;
			_isResume = true;
			loop();
		}
	}

	final void pause() {
		synchronized (_synch) {
			if (!_isRunning) {
				return;
			}
			_isRunning = false;
			_isPause = true;
			while (_isPause) {
				try {
					_synch.wait(4000);
				} catch (InterruptedException ignored) {
				}
			}
		}
	}

	final void destroy() {
		synchronized (_synch) {
			_isRunning = false;
			_isDestroy = true;
			while (_isDestroy) {
				try {
					_synch.wait();
				} catch (InterruptedException ex) {
				}
			}
		}
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
					double srcXrpValue = Double.parseDouble(LSystem
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
								double srcIouValue = Double.parseDouble(line
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
