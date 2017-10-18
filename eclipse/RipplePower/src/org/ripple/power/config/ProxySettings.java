package org.ripple.power.config;

import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.SocketAddress;
import java.util.Properties;

import org.ripple.power.utils.HttpRequest.Base64;
import org.ripple.power.utils.StringUtils;

public class ProxySettings {

	private int port;
	private String hostname;
	private String username;
	private String password;
	private boolean enabled;
	private Proxy.Type type = Proxy.Type.HTTP;

	private boolean isSocket = false;

	public ProxySettings(String server, int port) {
		this(null, null, server, port);
	}

	public ProxySettings(String username, String password, String server, int port) {
		this.username = username;
		this.password = password;
		this.hostname = server;
		this.port = port;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean e) {
		this.enabled = e;
		if (enabled) {
			Properties prop = System.getProperties();
			if (isSocket()) {
				prop.setProperty("socksProxyHost", hostname);
				prop.setProperty("socksProxyPort", String.valueOf(port));
			} else {
				prop.setProperty("http.proxyHost", hostname);
				prop.setProperty("http.proxyPort", String.valueOf(port));
				prop.setProperty("https.proxyHost", hostname);
				prop.setProperty("https.proxyPort", String.valueOf(port));
			}
		} else {
			Properties prop = System.getProperties();
			prop.remove("socksProxyHost");
			prop.remove("socksProxyPort");
			prop.remove("http.proxyHost");
			prop.remove("http.proxyPort");
			prop.remove("https.proxyHost");
			prop.remove("https.proxyPort");
		}
	}

	public boolean isProxyWithAuthentication() {
		return username != null && !username.isEmpty() && password != null && !password.isEmpty();
	}

	public String getProxyAuthentication() {
		String proxyAuth = null;
		if (isProxyEnabled() && isProxyWithAuthentication()) {
			proxyAuth = Base64.encode(this.username + ":" + this.password);
		}
		return proxyAuth;
	}

	public boolean isProxyEnabled() {
		boolean result = isEnabled() && hostname != null && !hostname.isEmpty() && port > 0;
		if (result) {
			Properties prop = System.getProperties();
			if (isSocket()) {
				prop.setProperty("socksProxyHost", hostname);
				prop.setProperty("socksProxyPort", String.valueOf(port));
			} else {
				prop.setProperty("http.proxyHost", hostname);
				prop.setProperty("http.proxyPort", String.valueOf(port));
				prop.setProperty("https.proxyHost", hostname);
				prop.setProperty("https.proxyPort", String.valueOf(port));
			}
		} else {
			Properties prop = System.getProperties();
			prop.remove("socksProxyHost");
			prop.remove("socksProxyPort");
			prop.remove("http.proxyHost");
			prop.remove("http.proxyPort");
			prop.remove("https.proxyHost");
			prop.remove("https.proxyPort");
		}
		return result;
	}

	public Proxy.Type getType() {
		return type;
	}

	public void setType(Proxy.Type type) {
		this.type = type;
	}

	public void setSocket(boolean flag) {
		this.isSocket = flag;
		this.type = Proxy.Type.SOCKS;
	}

	public boolean isSocket() {
		return isSocket || (this.type == Proxy.Type.SOCKS);
	}

	public Proxy getProxy() {
		Proxy proxy = null;
		if (isSocket) {
			this.type = Proxy.Type.SOCKS;
		}
		if (isProxyEnabled()) {
			if (!StringUtils.isEmpty(username) && !StringUtils.isEmail(password)) {
				Authenticator.setDefault(new Authenticator() {

					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password.toCharArray());
					}
				});
			}
			SocketAddress address = new InetSocketAddress(hostname, port);
			proxy = new Proxy(type, address);
		} else {
			proxy = Proxy.NO_PROXY;
		}
		return proxy;
	}

}
