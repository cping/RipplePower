package org.ripple.power.email;

import org.json.JSONObject;

public class SimpleTextMail {
	private String sendTo;
	private String ccTo;
	private String subject;
	private String content;

	public String getSendTo() {
		return sendTo;
	}

	public void setSendTo(String sendTo) {
		this.sendTo = sendTo;
	}

	public String getCcTo() {
		return ccTo;
	}

	public void setCcTo(String ccTo) {
		this.ccTo = ccTo;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public static SimpleTextMail parse(String json) throws Exception {
		return _parse_(new JSONObject(json));
	}

	public static SimpleTextMail _parse_(JSONObject json) {
		SimpleTextMail mail = new SimpleTextMail();
		mail.setSendTo(json.optString("to"));
		mail.setCcTo(json.optString("cc"));
		mail.setSubject(json.optString("subject"));
		mail.setContent(json.optString("content"));
		return mail;
	}

}
