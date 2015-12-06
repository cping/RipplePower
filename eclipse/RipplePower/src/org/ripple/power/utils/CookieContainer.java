package org.ripple.power.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class CookieContainer {
	private List<Cookie> cookieList = new LinkedList<Cookie>();

	public CookieContainer() {

	}

	public void add(Cookie cookie) {
		if (cookie.checkLifetime()) {
			cookieList.add(cookie);
		}
	}

	public void add(String name, String value, String expires, String domain,
			String path) {
		List<Cookie> deleteList = new LinkedList<Cookie>();
		for (int i = 0; i < cookieList.size(); i++) {
			Cookie c = cookieList.get(i);
			if (c.checkLifetime()) {
				if ((c.getDomain() == domain) && (c.getName() == name)
						&& (c.getPath() == path)) {
					deleteList.add(c);
				}
			} else {
				deleteList.add(c);
			}
		}
		cookieList.removeAll(deleteList);
		if ((name != null) && (value != null)) {
			add(new Cookie(name, value, expires, domain, path));
		}
	}

	public void add(String cookieString) {
		if ((cookieString != null) && (cookieString != "")) {
			String[] cookieProperties = cookieString.split("; ");
			String name = null;
			String value = null;
			String expires = null;
			String domain = null;
			String path = null;
			String val = null;
			String prop = null;

			for (int i = 0; i < cookieProperties.length; i++) {
				if (cookieProperties[i].contains("=")) {
					prop = cookieProperties[i].split("=")[0].toLowerCase();
					val = cookieProperties[i].split("=")[1];
					if ((prop.equals("expires")) || (prop.equals("max-age"))) {
						expires = val;
					} else if (prop.equals("path")) {
						path = val;
					} else if (prop.equals("domain")) {
						domain = val;
					} else if (name == null) {
						name = cookieProperties[i].split("=")[0];
						value = val;
					}
				}
			}
			add(name, value, expires, domain, path);
		}
	}

	public void add(List<String> cookies) {
		if (cookies != null) {
			for (int i = 0; i < cookies.size(); i++) {
				add(cookies.get(i));
			}
		}
	}

	public void clearSession() {
		cookieList.clear();
	}

	public void clearSession(String domain) {
		try {
			domain = new URL(domain).getHost();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		List<Cookie> deleteList = new LinkedList<Cookie>();

		for (int i = 0; i < cookieList.size(); i++) {
			if (cookieList.get(i).checkLifetime()) {
				if (cookieList.get(i).getDomain().equals(domain)) {
					deleteList.add(cookieList.get(i));
				}
			} else {
				deleteList.add(cookieList.get(i));
			}
		}
		cookieList.removeAll(deleteList);
	}

	public int size() {
		return cookieList.size();
	}

	public void tidyUp() {
		List<Cookie> deleteList = new LinkedList<Cookie>();
		for (int i = 0; i < cookieList.size(); i++) {
			if (cookieList.get(i).checkLifetime()) {
				cookieList.get(i).checkLifetime();
			} else {
				deleteList.add(cookieList.get(i));
			}
		}
		cookieList.removeAll(deleteList);
	}

	public List<Cookie> getCookieList(String queryurl) {
		URL url = null;
		try {
			url = new URL(queryurl);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		Cookie cookie = null;
		List<Cookie> matches = new LinkedList<Cookie>();
		List<String> names = new LinkedList<String>();
		List<Cookie> deleteList = new LinkedList<Cookie>();
		String domain = url.getHost();
		String path = url.getPath();

		int pathlength = path.split("/").length;

		for (int j = 0; j < pathlength; j++) {
			for (int i = 0; i < cookieList.size(); i++) {
				cookie = cookieList.get(i);
				if (cookie.checkLifetime()) {
					if ((cookie.getDomain().equals(domain))
							&& (cookie.getPath().equals(path))
							&& (!names.contains(cookie.getName()))) {
						matches.add(cookie);
						names.add(cookie.getName());
					}
				} else {
					deleteList.add(cookieList.get(i));
				}
			}
			path = path.replaceAll("/\\w*$", "");
		}
		cookieList.removeAll(deleteList);
		return matches;
	}
}
