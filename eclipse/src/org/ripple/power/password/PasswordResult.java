package org.ripple.power.password;

import java.util.regex.Pattern;

import org.ripple.power.wallet.Passphrase;

public class PasswordResult {

	public enum Expression {
		SINGLE_DIGIT_REGEX("([0-9])"), DIGITS_REGEX("[0-9]+"), POSITIVE_INTEGER(
				"[0-9]+([.][0-9]{1,2})?"), POST_ID_REGEX("([0-9]{3}-[0-9]{5})"), US_PHONE_NUMBER(
				"\\(([2-9][0-9][0-9])\\) ([2-9][0-9]{2})-([0-9]{4})"), WEB_SITE_URL(
				"((http|https)://)(www.)?([\\w\\-]+\\.)+([a-zA-Z]{2,4})"), FILE_NAME(
				"([a-zA-Z][\\w\\-]*.xml)(,[a-zA-Z][\\w\\-]*.xml)*"), TIME_REGEX(
				"(([0-1]*[0-9]|[2][0-3]):([0-5][0-9]))|(([2][4]):([0][0]))"), FROM_TIME_REGEX(
				"([0-1]*[0-9]|[2][0-3]):([0-5][0-9])"), TO_TIME_REGEX(
				"(([0]|[0][0]):([0-5][1-9]|[1-5][0-9]))|(([1-9]|[0-1][1-9]|[2][0-3]):([0-5][0-9]))|(([2][4]):([0][0]))"), LONGITUDE_REGEX(
				"(^\\+?1[0-7]\\d(\\.\\d+)?$)|(^\\+?([1-9])?\\d(\\.\\d+)?$)|(^-180$)|(^-1[1-7]\\d(\\.\\d+)?$)|(^-[1-9]\\d(\\.\\d+)?$)|(^\\-\\d(\\.\\d+)?$)"), LATITUDE_REGEX(
				"(^\\+?([1-8])?\\d(\\.\\d+)?$)|(^-90$)|(^-(([1-8])?\\d(\\.\\d+)?$))");

		private final String RegEx;

		private Expression(String regEx) {
			this.RegEx = regEx;
		}

		public String getRegEx() {
			return RegEx;
		}
	}

	private String _passwordString;

	public PasswordResult() {
		this("");
	}

	public PasswordResult(PasswordResult o) {
		this(o.toString());
	}

	public PasswordResult(String p) {
		this._passwordString = p;
	}

	public void setPassword(String p) {
		this._passwordString = p;
	}

	public Passphrase getPassphrase() throws Exception {
		return new Passphrase(this._passwordString);
	}

	public void cleanPassword() {
		this._passwordString = "";
	}

	public boolean hasPassword() {
		if (this._passwordString == null) {
			return false;
		}
		if (this._passwordString.length() == 0) {
			return false;
		}
		return true;
	}

	public String toString() {
		if (!hasPassword()) {
			return null;
		}
		return _passwordString;
	}

	public int length() {
		if (_passwordString != null) {
			return _passwordString.length();
		}
		return 0;
	}

	public boolean check(Expression exp) {
		return Pattern.matches(exp.RegEx, _passwordString);
	}

	public boolean compareTo(String other) {
		if (other == null) {
			return false;
		}
		if (this.hasPassword()) {
			return this.toString().equals(other);
		}
		return false;
	}
}
