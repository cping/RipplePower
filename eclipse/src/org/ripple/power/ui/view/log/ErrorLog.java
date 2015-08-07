package org.ripple.power.ui.view.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import javax.swing.SwingUtilities;

import org.ripple.power.config.LSystem;
import org.ripple.power.ui.UIMessage;
import org.ripple.power.ui.UIRes;
import org.ripple.power.utils.DateUtils;

public class ErrorLog extends ErrorHtml {

	private boolean isEnabled;
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
	private final SimpleDateFormat timeFormat = new SimpleDateFormat(
			"HH:mm:ss.SSS");
	
	private static ErrorLog instance = null;
	
	public static ErrorLog get(){
		if(instance==null){
			instance = new ErrorLog();
		}
		return instance;
	}

	public ErrorLog()  {
		super("ErrorLog");

		isEnabled = true;

		StringBuilder sb = new StringBuilder();
		sb.append(ROW_START);
		sb.append("<TH WIDTH=\"80\">").append("Date").append(HEADER_END);
		sb.append("<TH WIDTH=\"120\">").append("Time").append(HEADER_END);
		sb.append("<TH WIDTH=\"130\">").append("Reporter").append(HEADER_END);
		sb.append(HEADER_START).append("Message").append(HEADER_END);
		sb.append(ROW_END);
		write(sb);

		StringBuilder startupMessage = new StringBuilder();
		startupMessage.append("New Report Started. ")
				.append(LSystem.applicationName).append(" version ")
				.append(LSystem.applicationVersion);
		report(LSystem.applicationName, startupMessage);
		TimeZone tz = TimeZone.getDefault();
		report(LSystem.applicationName,
				"All times will be reported in the local time zone: "
						+ tz.getID() + ", " + tz.getDisplayName());
	}

	public void disable() {
		isEnabled = false;
	}

	public void enable() {
		isEnabled = true;
	}

	private void report(String reporter, StringBuilder message) {
		Calendar date = getDate();
		StringBuilder s = new StringBuilder();
		s.append(ROW_START);
		s.append(FIELD_START).append(dateFormat.format(date.getTime())).append(FIELD_END);
		s.append(FIELD_START).append(timeFormat.format(date.getTime())).append(FIELD_END);
		s.append(FIELD_START).append(reporter).append(FIELD_END);
		s.append(FIELD_START).append(message).append(FIELD_END);
		s.append(ROW_END);
		write(s);
	}

	public void report(String reporter, String message) {
		if (isEnabled) {
			report(reporter, new StringBuilder(message));
		}
	}

	public void report(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		pw.close();
		report(LSystem.applicationName, new StringBuilder(sw.toString()));
	}

	private Calendar getDate() {
		return DateUtils.getUTCCalendar();
	}

	private Throwable deferredException;

	private String deferredText;

	public void logException(String text, Throwable exc) {
		if (SwingUtilities.isEventDispatchThread()) {
			StringBuilder strings = new StringBuilder(512);
			strings.append("<html><b>");
			strings.append(text);
			strings.append("</b><br><br>");
			strings.append(exc.toString());
			strings.append("<br>");
			StackTraceElement[] trace = exc.getStackTrace();
			int count = 0;
			for (StackTraceElement elem : trace) {
				strings.append(elem.toString());
				strings.append("<br>");
				if (++count == 25)
					break;
			}
			strings.append("</html>");
			UIRes.showErrorMessage(LSystem.applicationMain, UIMessage.error,
					strings.toString());
		} else if (deferredException == null) {
			deferredText = text;
			deferredException = exc;
			try {
				LSystem.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						logException(deferredText, deferredException);
						deferredException = null;
						deferredText = null;
					}
				});
			} catch (Exception ex) {
				System.err.println(ex.getMessage());
			}
		}
		exc.printStackTrace();
		report(exc);
	}

}
