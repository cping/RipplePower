package org.ripple.power.email;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.ripple.power.config.LSystem;
import org.ripple.power.ui.todo.Preference;
import org.ripple.power.ui.todo.Setting;

public class MailSender {
	private Setting setting;
	private SimpleTextMail mail;

	public MailSender(SimpleTextMail mail) {
		setting = new Setting();
		this.mail = mail;
	}

	@SuppressWarnings("deprecation")
	public boolean send() {
		boolean status;
		HtmlEmail email = new HtmlEmail();
		Preference preference = setting.getPreference();

		email.setHostName(preference.getMailHost());
		email.setSSL(preference.isUseSSL());
		email.setSslSmtpPort(preference.getMailPort());
		email.setAuthentication(preference.getUsername(),
				preference.getPassword());

		try {
			String[] tos = mail.getSendTo().split(";");
			for (String to : tos) {
				email.addTo(to, to);
			}
			String[] ccs = mail.getCcTo().split(";");
			for (String cc : ccs) {
				email.addCc(cc, cc.trim());
			}
			email.setFrom(preference.getUsername());
			email.setSubject(mail.getSubject());
			email.setHtmlMsg(mail.getContent());
			email.setCharset(LSystem.encoding);

			email.send();
			status = true;
		} catch (EmailException e) {
			e.printStackTrace();
			status = false;
		}
		return status;
	}
}
