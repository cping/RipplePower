package org.ripple.power.hft.bot;

import org.ripple.power.config.LSystem;

public abstract class TraderBase implements ITrader {

	private boolean _killSignal;
	protected int _intervalMs;
	protected boolean _cleanup;
	private BotLog _log;

	public TraderBase(BotLog log) {
		this._log = log;
	}

	public void startTrading() {
		do {
			try {
				check();
				LSystem.sleep(_intervalMs);
			} catch (Exception ex) {
			}
		} while (!_killSignal);
	}

	public void Kill() {
		_killSignal = true;
	}

	protected abstract void check();

	protected void log(String message, Object... args) {
		if (_log != null) {
			_log.mes(message, args);
		}
	}

}
