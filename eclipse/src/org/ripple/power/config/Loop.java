package org.ripple.power.config;

import org.ripple.power.timer.LTimerContext;
import org.ripple.power.timer.SystemTimer;
import org.ripple.power.txns.Updateable;
import org.ripple.power.utils.MathUtils;

public abstract class Loop {

	private long _lastTimeMicros, _currTimeMicros, _goalTimeMicros,
			_elapsedTimeMicros, _remainderMicros, _elapsedTime;

	private long _maxFrames = 30;

	private Thread _mainLoop = null;

	private final Object _synch = new Object();

	private final LTimerContext _timerContext = new LTimerContext();

	private SystemTimer _timer;

	private boolean _isRunning, _isPause, _isDestroy, _isResume;

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

	private final void pause(long sleep) {
		try {
			Thread.sleep(sleep);
		} catch (InterruptedException ex) {
		}
	}

	public abstract void runTaskTimer(LTimerContext context);

	public void mainLoop() {
		for (; _isRunning;) {
			_goalTimeMicros = _lastTimeMicros + 1000000L / _maxFrames;
			_currTimeMicros = _timer.sleepTimeMicros(_goalTimeMicros);
			_elapsedTimeMicros = _currTimeMicros - _lastTimeMicros
					+ _remainderMicros;
			_elapsedTime = MathUtils.max(0, (_elapsedTimeMicros / 1000));
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

	public abstract Updateable main();

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
		} else if (_mainLoop != null) {
			_isRunning = false;
			try {
				_mainLoop.interrupt();
				_mainLoop = null;
			} catch (Exception ex) {
			}
			_isRunning = true;
			_mainLoop = new Thread() {
				public void run() {
					main().action(this);
				}
			};
			_mainLoop.start();
		}

	}

	public final void resume() {
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

	public final void pause() {
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

	protected void stop() {
		_isRunning = false;
	}

	public final void destroy() {
		synchronized (_synch) {
			_isRunning = false;
			_isDestroy = true;
		}
	}

}
