package org.ripple.power.database.secrecy;

import java.util.Date;

import org.json.JSONObject;
import org.ripple.power.utils.DateUtils;

public class PasswordSecureData extends SecureData {

	private static final long serialVersionUID = 1L;
	private static final int DAYS_TO_EXPIRATION = 180;
	private String userName;
	private String password;
	private String url;
	private Date expirationTime;

	public PasswordSecureData(String title, String password) {
		super(title, "");
		this.userName = "";
		this.password = password;
		this.url = "";
		this.updateExpiration();
	}

	private void updateExpiration() {
		this.expirationTime = DateUtils.addDays(new Date(), DAYS_TO_EXPIRATION);
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.updateTime();
		this.userName = userName;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		updateExpiration();
		this.updateTime();
		this.password = password;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.updateTime();
		this.url = url;
	}

	public Date getExpirationTime() {
		return this.expirationTime;
	}

	@Override
	public JSONObject toJSON() {
		JSONObject obj = new JSONObject();
		JSONObject data = new JSONObject();
		data.put("Title", this.getTitle());
		data.put("UserName", this.userName);
		data.put("Password", this.password);
		data.put("URL", this.url);
		data.put("Tags", this.getTagString());
		obj.put("PasswordData", data);
		return obj;
	}

	@Override
	public String toXML() {
		StringBuilder xmlSnip = new StringBuilder();
		xmlSnip.append("\t<PasswordData>\n");
		xmlSnip.append("\t\t<Title>").append(this.getTitle()).append("</Title>\n");
		xmlSnip.append("\t\t<UserName>").append(this.userName).append("</UserName>\n");
		xmlSnip.append("\t\t<Password>").append(this.password).append("</Password>\n");
		xmlSnip.append("\t\t<URL>").append(this.url).append("</URL>\n");
		xmlSnip.append("\t\t<Tags>").append(this.getTagString()).append("</Tags>\n");
		xmlSnip.append("\t</PasswordData>\n");
		return xmlSnip.toString();
	}

}
