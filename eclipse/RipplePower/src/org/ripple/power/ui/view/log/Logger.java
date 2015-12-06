package org.ripple.power.ui.view.log;

public class Logger implements LoggerMode {

	private LoggerMode mode;
	private DebugMode debugMode;
	private EmptyMode releaseMode;
	private String className;

	public Logger(String className) {
		this.className = className;
		this.setLogMode(true);
	}

	public void setLogMode(boolean isDebug) {
		if (isDebug) {
			if (debugMode == null) {
				debugMode = new DebugMode(className);
			}
			mode = debugMode;
		} else {
			if (releaseMode == null) {
				releaseMode = new EmptyMode();
			}
			mode = releaseMode;
		}
	}

	@Override
	public void Log(String tag, String msg, Object... obj) {
		mode.Log(tag, msg, obj);
	}

	@Override
	public void setLevel(Level level) {
		mode.setLevel(level);
	}

	@Override
	public void debug(String message, Object... obj) {
		mode.debug(message, obj);
	}

	@Override
	public void info(String message, Object... obj) {
		mode.info(message, obj);
	}

	@Override
	public void warn(String message, Object... obj) {
		mode.warn(message, obj);
	}

	@Override
	public void fatal(String message, Object... obj) {
		mode.fatal(message, obj);
	}

	@Override
	public void error(String message, Object... obj) {
		mode.error(message, obj);
	}
}
