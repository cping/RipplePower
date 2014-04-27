package org.ripple.power.ui;

import java.awt.Component;

import javax.swing.JOptionPane;

public class RPMessage {

	public static void showMessage(Component parentComponent, String message,
			String title) {
		JOptionPane.showMessageDialog(parentComponent, message, title,
				JOptionPane.INFORMATION_MESSAGE);
	}

	public static void showInfoMessage(Component parentComponent, String title,
			String content) {
		JOptionPane.showMessageDialog(parentComponent, content, title,
				JOptionPane.INFORMATION_MESSAGE);
	}

	public static void showWarningMessage(Component parentComponent,
			String title, String content) {
		JOptionPane.showMessageDialog(parentComponent, content, title,
				JOptionPane.WARNING_MESSAGE);
	}

	public static void showErrorMessage(Component parentComponent,
			String title, String content) {
		JOptionPane.showMessageDialog(parentComponent, content, title,
				JOptionPane.ERROR_MESSAGE);
	}

	public static int showConfirmMessage(Component parentComponent,
			String title, String content, String a, String b) {
		return JOptionPane.showOptionDialog(parentComponent, content, title,
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
				new Object[] { a, b }, JOptionPane.NO_OPTION);

	}
	
	public static int showConfirmMessage(Component parentComponent,
			String title, String content, Object[] objs) {
		return JOptionPane.showOptionDialog(parentComponent, content, title,
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
				objs, JOptionPane.NO_OPTION);

	}
}
