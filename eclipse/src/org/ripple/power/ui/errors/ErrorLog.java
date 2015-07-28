package org.ripple.power.ui.errors;

import javax.swing.SwingUtilities;

import org.ripple.power.config.LSystem;
import org.ripple.power.ui.UIMessage;
import org.ripple.power.ui.UIRes;

public class ErrorLog {

	private static Throwable deferredException;

	private static String deferredText;

	public static void logException(String text, Throwable exc) {
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
				javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
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
	}

}
