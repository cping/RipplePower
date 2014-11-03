package org.ripple.power.hft;

import java.util.ArrayList;

import org.address.ripple.RippleSeedAddress;
import org.ripple.power.config.LSystem;
import org.ripple.power.timer.LTimerContext;
import org.ripple.power.timer.SystemTimer;
import org.ripple.power.txns.AccountFind;
import org.ripple.power.txns.AccountInfo;
import org.ripple.power.txns.AccountLine;
import org.ripple.power.txns.Updateable;
import org.ripple.power.utils.MathUtils;

public class Process extends TraderBase {

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
		CrazyBuyer, CrazySeller, UserSet, UserScript
	}

	public static class Error {
		public int code;
		public String message;
	}

	public static class Task {
		public RippleSeedAddress seed;
		public String currency = "unkown";
		public String issuer = "unkown";
		public double value = -1;
		public double real_max_value = -1;
		public double minWallVolume = -1;
		public double maxWallVolume = -1;
		public double minDifference = -1;
		public int orderId = -1;
		public double orderAmount = -1;
		public Model model = Model.UserSet;
		public ArrayList<Swap> swaps = new ArrayList<Swap>(10);
		public ArrayList<Error> errors = new ArrayList<Error>(10);
		public boolean stop;

		public void update(LTimerContext context) {

		}
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

	public void loop() {
		if (_mainLoop == null) {
			_mainLoop = new Thread() {
				public void run() {
					for (; _isRunning;) {
						if (_isRunning) {
							_goalTimeMicros = _lastTimeMicros + 1000000L
									/ _maxFrames;
							_currTimeMicros = _timer
									.sleepTimeMicros(_goalTimeMicros);
							_elapsedTimeMicros = _currTimeMicros
									- _lastTimeMicros + _remainderMicros;
							_elapsedTime = MathUtils.max(0,
									(_elapsedTimeMicros / 1000));
							_remainderMicros = _elapsedTimeMicros
									- _elapsedTime * 1000;
							_lastTimeMicros = _currTimeMicros;
							_timerContext.millisSleepTime = _remainderMicros;
							_timerContext.timeSinceLastUpdate = _elapsedTime;
							runTaskTimer(_timerContext);
						}
						if (_isPause) {
							pause(500);
						}
					}
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
			_isRunning = true;
			_isResume = true;
			_timer = new SystemTimer();
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

	private final void callTask(Task task) {
		_HFT_tasks.add(task);
	}

	public void execute(final Task task) {
		if (task.seed == null) {
			return;
		}
		String address = task.seed.getPublicKey();
		AccountFind find = new AccountFind();
		final AccountInfo info = new AccountInfo();
		if (LSystem.nativeCurrency.equalsIgnoreCase(task.currency)) {
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
							if (task.currency.equalsIgnoreCase(line
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
