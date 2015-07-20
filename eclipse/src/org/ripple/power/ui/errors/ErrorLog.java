package org.ripple.power.ui.errors;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.ripple.power.config.LSystem;

public class ErrorLog {

	private static Throwable deferredException;

	private static String deferredText;

	public static void logException(String text, Throwable exc) {
		if (SwingUtilities.isEventDispatchThread()) {
			StringBuilder string = new StringBuilder(512);
			string.append("<html><b>");
			string.append(text);
			string.append("</b><br><br>");
			string.append(exc.toString());
			string.append("<br>");
			StackTraceElement[] trace = exc.getStackTrace();
			int count = 0;
			for (StackTraceElement elem : trace) {
				string.append(elem.toString());
				string.append("<br>");
				if (++count == 25)
					break;
			}
			string.append("</html>");
			JOptionPane.showMessageDialog(LSystem.applicationMain, string,
					"Error", JOptionPane.ERROR_MESSAGE);
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
