package org.ripple.power.ui;

import java.awt.Window;

import javax.swing.SwingUtilities;

import org.ripple.power.i18n.LangConfig;
import org.ripple.power.ui.RPToast.Style;

public class UIMessage {

	public final static String ok = LangConfig.get(UIMessage.class, "ok", "OK");

	public final static String send = LangConfig.get(UIMessage.class, "send",
			"Send");

	public final static String open = LangConfig.get(UIMessage.class, "open",
			"Open");

	public final static String save = LangConfig.get(UIMessage.class, "save",
			"Save");

	public final static String cancel = LangConfig.get(UIMessage.class,
			"cancel", "Cancel");

	public final static String exit = LangConfig.get(UIMessage.class, "exit",
			"Exit");

	public final static String warning = LangConfig.get(UIMessage.class,
			"warning", "Warning");

	public final static String info = LangConfig.get(UIMessage.class, "info",
			"Info");

	public final static String error = LangConfig.get(UIMessage.class, "error",
			"Error");

	public final static String create = LangConfig.get(UIMessage.class,
			"create", "Create");

	public final static String notExist = LangConfig.get(UIMessage.class,
			"noexist", "This account or password does not exist !");

	public final static String completed = LangConfig.get(UIMessage.class,
			"sendc", "Send Completed");

	public final static String errAddress = LangConfig.get(UIMessage.class,
			"inaddress", "Invalid Ripple Address !");

	public final static String errMoney = LangConfig.get(UIMessage.class,
			"inmoney", "Invalid Money Amount !");

	public final static String errFee = LangConfig.get(UIMessage.class,
			"infee", "Invalid Fee Amount !");

	public final static String errNotMoney = LangConfig.get(UIMessage.class,
			"nomoney", "You do not have so much money !");

	public final static String errNotAddress = LangConfig.get(UIMessage.class,
			"noaddress",
			"Send failed, could not get the current Address data !");

	public final static String noselect = LangConfig.get(UIMessage.class,
			"noselect",
			"Please confirm that you want to trade gateway and currency !");

	public final static String plasetrust = LangConfig.get(UIMessage.class,
			"noselect", "Please trust %s !");

	public final static String sntr = LangConfig.get(UIMessage.class, "sntr",
			"Sorry, there are no transactions orders to %s");

	public final static String ydel = LangConfig.get(UIMessage.class, "ydel",
			"You want to delete %s ?");

	public final static String data_error = LangConfig.get(UIMessage.class,
			"data_error", "Data format error, the operation is invalid !");

	public final static String you_cancel_tx(String a, String b) {
		String result = String.format(LangConfig.get(UIMessage.class,
				"you_cancel_tx",
				"You are ready to use %s Swap %s, Are you sure ?"), a, b);
		return result;
	}

	public final static void infoMessage(final Window parent, final String text) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				RPToast.makeText(parent, text, Style.SUCCESS).display();
				parent.revalidate();
				parent.repaint();
			}
		});
	}

	public final static void alertMessage(final Window parent, final String text) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				RPToast.makeText(parent, text, Style.ERROR).display();
				parent.revalidate();
				parent.repaint();
			}
		});
	}
}
