package org.ripple.power.utils;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Cookie {

	private String name = null;
	private String value = null;
	private long expires = 0;
	private String domain = null;
	private String path = null;

	public Cookie(String name, String value, String expires, String domain,
			String path) {
		this.name = name;
		this.value = value;
		this.expires = dateToTimeStamp(expires);
		if (domain.startsWith(".")) {
			this.domain = "www" + domain;
		} else {
			this.domain = domain;
		}
		this.path = path;
	}

	public String getName() {
		checkLifetime();
		return name;
	}

	public String getValue() {
		checkLifetime();
		return value;
	}

	public long getExpires() {
		checkLifetime();
		return expires;
	}

	public String getDomain() {
		checkLifetime();
		return domain;
	}

	public String getPath() {
		checkLifetime();
		return path;
	}

	public boolean checkLifetime() {
		/*
		 * if (System.currentTimeMillis() > this.expires) { return false; }
		 */
		return true;
	}

	private long dateToTimeStamp(String date) {
		if ((date != "") && (date != null)) {
			DateFormatSymbols s = new DateFormatSymbols();
			String[] months = s.getShortMonths();
			String monthname = date.replaceAll("^\\w*, \\d\\d-", "");
			monthname = monthname.replaceAll("-[\\w|\\W]*", "");

			int monthvalue = arraySearch(monthname, months) + 1;
			String number = monthvalue + "";
			if (monthvalue < 10) {
				number = "0" + number;
			}
			date = date.replace(monthname, number);
			date = date.replaceAll("^\\w*, ", "");
			date = date.replace(" GMT", "");

			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
			Date d = new Date();
			try {
				d = sdf.parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
				return 0;
			}
			return d.getTime();
		}
		return (System.currentTimeMillis() * 5);
	}

	private int arraySearch(String needle, String[] haystack) {
		for (int i = 0; i < haystack.length; i++) {
			if (haystack[i].toLowerCase().equals(needle.toLowerCase())) {
				return i;
			}
		}
		return -1;
	}
}