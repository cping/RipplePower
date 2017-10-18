package org.ripple.power.txns.btc;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class BriefLogFormatter extends Formatter {

	private static final MessageFormat messageFormat = new MessageFormat("{3,date,hh:mm:ss} {0} {1}.{2}: {4}\n{5}");

	private static final Logger logger = Logger.getLogger("");

	public static void init() {
		Handler[] handlers = logger.getHandlers();
		for (Handler handler : handlers) {
			handler.setFormatter(new BriefLogFormatter());
		}
	}

	@Override
	public String format(LogRecord logRecord) {
		Object[] arguments = new Object[6];
		arguments[0] = logRecord.getLevel().getName();
		String fullClassName = logRecord.getSourceClassName();
		int lastDot = fullClassName.lastIndexOf('.');
		String className = fullClassName.substring(lastDot + 1);
		arguments[1] = className;
		arguments[2] = logRecord.getSourceMethodName();
		arguments[3] = new Date(logRecord.getMillis());
		arguments[4] = logRecord.getMessage();
		if (logRecord.getThrown() != null) {
			Writer result = new StringWriter();
			logRecord.getThrown().printStackTrace(new PrintWriter(result));
			arguments[5] = result.toString();
		} else {
			arguments[5] = "";
		}

		return messageFormat.format(arguments);
	}
}
