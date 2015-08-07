package org.ripple.power.ui.view.log;

import org.ripple.power.config.LSystem;
import org.ripple.power.utils.DateUtils;

public class DebugMode implements LoggerMode {

	private Level level;

	private String[] tag;

	private void setTag(String className) {
		tag = new String[Level.MAX.getVal()];

		for (int i = 0; i < Level.MAX.getVal(); i++) {
			Level level = Level.values()[i];

			StringBuilder sb = new StringBuilder();
			sb.append(level.toString());

			sb.append(":");

			if (className != null) {
				sb.append(className);
			}

			tag[i] = sb.toString();
		}

	}

	public DebugMode(String className) {
		setTag(className);
		level = Level.ALL_LOG;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	private void log(Level checkLevel, String message, Object... obj) {
		if (checkLevel.getVal() >= level.getVal()) {
			Log(tag[checkLevel.getVal()], message, obj);
		}
	}

	public void debug(String message, Object... obj) {
		Level checkLevel = Level.DEBUG;
		log(checkLevel, message, obj);
	}

	public void info(String message, Object... obj) {
		Level checkLevel = Level.INFO;
		log(checkLevel, message, obj);
	}

	public void warn(String message, Object... obj) {
		Level checkLevel = Level.WARN;
		log(checkLevel, message, obj);
	}

	public void fatal(String message, Object... obj) {
		Level checkLevel = Level.FATAL;
		log(checkLevel, message, obj);
	}

	public void error(String message, Object... obj) {
		Level checkLevel = Level.ERROR;
		log(checkLevel, message, obj);
	}

	public void Log(String tag, String msg, Object... obj) {
		StringBuffer buf = new StringBuffer();
		buf.append(DateUtils.toDate());
		buf.append(" ");
		buf.append("[");
		buf.append(level);
		buf.append("]");
		buf.append(tag);
		buf.append(" - ");
		buf.append(String.format(msg, obj));
		buf.append(LSystem.LS);
		LogView.get().append(buf.toString());
		LogView.get().show();
	}
}