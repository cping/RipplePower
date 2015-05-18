package org.ripple.power.ui.todo;

import org.ripple.power.config.LSystem;
import org.ripple.power.config.Session;

public class Setting {

	private Preference preference;

	private Session session = new Session("todo_setting");

	public Setting() {
		preference = new Preference();
		try {
			session.set("useSSL", "true");
			session.set("username", "");
			session.set("proxy.autodetect", "true");
			session.set("mail.host", "smtp.gmail.com");
			session.set("proxy.host", "127.0.0.1");
			session.set("username", "xxxxxxxxxxx");
			session.set("password", "xxxxxxxxx");
			session.set("mail.port", "465");
			session.set("proxy.port", "8580");
			session.set("export.path", LSystem.getCurrentDirectory());
		} catch (Exception e) {
			e.printStackTrace();
		}
		initPreference();
	}

	private void initPreference() {
		preference.setProxyAutoDetect(Boolean.valueOf(session.getString(
				"proxy.autodetect", "true")));
		preference.setProxyHost(session.getString("proxy.host", ""));
		preference.setProxyPort(session.getString("proxy.port", ""));

		preference.setExportPath(session.getString("export.path", ""));

		preference.setUseSSL(Boolean.valueOf(session
				.getString("useSSL", "true")));
		preference.setMailHost(session.getString("mail.host", ""));
		preference.setMailPort(session.getString("mail.port", ""));
		preference.setUsername(session.getString("username", "xxxxxxxxxxx"));
		preference.setPassword(session.getString("password", "xxxxxxxxxxx"));
	}

	public Preference getPreference() {
		return preference;
	}

	public void savePreference(Preference preference) {
		session.set("proxy.autodetect",
				String.valueOf(preference.isProxyAutoDetect()));
		session.set("proxy.host", preference.getProxyHost());
		session.set("proxy.port", preference.getProxyPort());

		session.set("export.path", preference.getExportPath());

		session.set("useSSL", String.valueOf(preference.isUseSSL()));
		session.set("mail.host", preference.getMailHost());
		session.set("mail.port", preference.getMailPort());
		session.set("username", preference.getUsername());
		session.set("password", preference.getPassword());

		session.save();

	}
}
