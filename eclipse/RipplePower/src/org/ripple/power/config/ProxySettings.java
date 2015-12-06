package org.ripple.power.config;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;

import org.ripple.power.utils.HttpRequest.Base64;

public class ProxySettings {

	private int port;
	private String hostname;
	private String username;
	private String password;
	private boolean enabled;
	private Proxy.Type type = Proxy.Type.HTTP;

	public ProxySettings(String server, int port) {
		this.hostname = server;
		this.port = port;
	}

	public ProxySettings(String username, String password, String server,
			int port) {
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

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isProxyWithAuthentication() {
		return username != null && !username.isEmpty() && password != null
				&& !password.isEmpty();
	}

	public String getProxyAuthentication() {
		String proxyAuth = null;
		if (isProxyEnabled() && isProxyWithAuthentication()) {
			proxyAuth = Base64.encode(this.username + ":" + this.password);
		}
		return proxyAuth;
	}

	public boolean isProxyEnabled() {
		return isEnabled() && hostname != null && !hostname.isEmpty()
				&& port > 0;
	}

	public Proxy.Type getType() {
		return type;
	}

	public void setType(Proxy.Type type) {
		this.type = type;
	}

	public Proxy getProxy() {
		Proxy proxy = null;
		if (isProxyEnabled()) {
			SocketAddress address = new InetSocketAddress(hostname, port);
			proxy = new Proxy(type, address);
		} else {
			proxy = Proxy.NO_PROXY;
		}
		return proxy;
	}

}
