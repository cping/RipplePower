package org.ripple.power.utils;

import org.ripple.power.config.LSystem;

public class HttpHeader {
	final static String[] HTTP_USER_AGENT = new String[] {
			"Mozilla/5.0 (Windows; Windows NT 6.3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.65 Safari/537.36",
			"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_8; en-us) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1 Safari/534.50",
			"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_0) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.56 Safari/535.11",
			"Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.8.1.11) Gecko/20071127 Firefox/2.0.0.11",
			"Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.6) Gecko/20070725 Firefox/2.0.0.6",
			"Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_3_3 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8J2 Safari/6533.18.5",
			"Mozilla/5.0 (Linux; U; Android 2.3.7; en-us; Nexus One AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1"};

	private String get;

	private String host;

	private String accept;

	private String referer;

	private String cookie;

	private String userAgent;

	private String userAgentValue;

	private String range;

	private String pragma;

	private String cacheControl;

	private String connection;

	public HttpHeader() {
		userAgentValue = HTTP_USER_AGENT[LSystem.random
				.nextInt(HTTP_USER_AGENT.length)];
		get = "GET ";
		host = "Host: ";
		accept = "Accept: */*\r\n";
		referer = "Referer: ";
		cookie = "Cookie: ";
		userAgent = "User-Agent: " + userAgentValue + "\r\n";
		range = "Range: bytes=0-\r\n";
		pragma = "Pragma: no-cache\r\n";
		cacheControl = "Cache-Control: no-cache\r\n";
		connection = "Connection: close\r\n\r\n";
	}
	
	public static String getUA(){
		return HTTP_USER_AGENT[LSystem.random
		       				.nextInt(HTTP_USER_AGENT.length)];
	}

	public void setGet(String g) {
		int t = get.indexOf(" ");
		get = get.substring(0, t + 1);
		get = get + g + "\r\n";
	}

	public void setHost(String h) {
		int t = host.indexOf(" ");
		host = host.substring(0, t + 1);
		host = host + h + "\r\n";
	}

	public void setRange(String r) {
		int t = range.indexOf("=");
		range = range.substring(0, t + 1);
		range = range + r + "\r\n";
	}

	public void setReferer(String ref) {
		int t = referer.indexOf(":");
		referer = referer.substring(0, t + 1);
		referer = referer + ref + "\r\n";
	}

	public void setCookie(String c) {
		int t = cookie.indexOf(":");
		cookie = cookie.substring(0, t + 1);
		cookie = cookie + c + "\r\n";
	}

	public String getUserAgentValue() {
		return userAgentValue;
	}

	public String getHeaderString() {
		return get + host + accept + userAgent + range + pragma + cacheControl
				+ connection;
	}

}
