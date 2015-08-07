package org.ripple.power.ui.view.log;

public class EmptyMode implements LoggerMode {

	@Override
	public void setLevel(Level level) {

	}

	@Override
	public void debug(String message, Object... obj) {

	}

	@Override
	public void info(String message, Object... obj) {

	}

	@Override
	public void warn(String message, Object... obj) {

	}

	@Override
	public void fatal(String message, Object... obj) {

	}

	@Override
	public void error(String message, Object... obj) {

	}

	@Override
	public void Log(String tag, String msg, Object... obj) {

	}

}
