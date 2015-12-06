package org.ripple.power.server.socket;

import org.json.JSONObject;

import javax.net.ssl.SSLContext;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

public class SocketIO {

	private IOCallback callback;

	private IOConnection connection;

	private String namespace;

	private Properties headers = new Properties();

	private URL url;

	public SocketIO() {

	}

	public SocketIO(final String url) throws MalformedURLException {
		if (url == null) {
			throw new RuntimeException("url may not be null.");
		}
		setAndConnect(new URL(url), null);
	}

	public SocketIO(final String url, Properties headers)
			throws MalformedURLException {
		if (url == null) {
			throw new RuntimeException("url may not be null.");
		}
		if (headers != null) {
			this.headers = headers;
		}

		setAndConnect(new URL(url), null);
	}

	public SocketIO(final String url, final IOCallback callback)
			throws MalformedURLException {
		connect(url, callback);
	}

	public SocketIO(final URL url, final IOCallback callback) {
		if (setAndConnect(url, callback) == false) {
			throw new RuntimeException("url and callback may not be null.");
		}
	}

	public SocketIO(final URL url) {
		setAndConnect(url, null);
	}

	public static void setDefaultSSLSocketFactory(SSLContext sslContext) {
		IOConnection.setSslContext(sslContext);
	}

	public void connect(final String url, final IOCallback callback)
			throws MalformedURLException {
		if (setAndConnect(new URL(url), callback) == false) {
			if (url == null || callback == null)
				throw new RuntimeException("url and callback may not be null.");
			else
				throw new RuntimeException(
						"connect(String, IOCallback) can only be invoked after SocketIO()");
		}
	}

	public void connect(URL url, IOCallback callback) {
		if (setAndConnect(url, callback) == false) {
			if (url == null || callback == null)
				throw new RuntimeException("url and callback may not be null.");
			else
				throw new RuntimeException(
						"connect(URL, IOCallback) can only be invoked after SocketIO()");
		}
	}

	public void connect(IOCallback callback) {
		if (setAndConnect(null, callback) == false) {
			if (callback == null)
				throw new RuntimeException("callback may not be null.");
			else if (this.url == null)
				throw new RuntimeException(
						"connect(IOCallback) can only be invoked after SocketIO(String) or SocketIO(URL)");
		}
	}

	private boolean setAndConnect(URL url, IOCallback callback) {
		if (this.connection != null)
			throw new RuntimeException(
					"You can connect your SocketIO instance only once. Use a fresh instance instead.");
		if ((this.url != null && url != null)
				|| (this.callback != null && callback != null))
			return false;
		if (url != null) {
			this.url = url;
		}
		if (callback != null) {
			this.callback = callback;
		}
		if (this.callback != null && this.url != null) {
			final String origin = this.url.getProtocol() + "://"
					+ this.url.getAuthority();
			this.namespace = this.url.getPath();
			if (this.namespace.equals("/")) {
				this.namespace = "";
			}
			this.connection = IOConnection.register(origin, this);
			return true;
		}
		return false;
	}

	public void emit(final String event, final Object... args) {
		this.connection.emit(this, event, null, args);
	}

	public void emit(final String event, final Object arg) {
		this.connection.emitX(this, event, null, arg);
	}

	public void emit(final String event, IOAcknowledge ack,
			final Object... args) {
		this.connection.emit(this, event, ack, args);
	}

	public IOCallback getCallback() {
		return this.callback;
	}

	public String getNamespace() {
		return this.namespace;
	}

	public void send(final JSONObject json) {
		this.connection.send(this, null, json);
	}

	public void send(IOAcknowledge ack, final JSONObject json) {
		this.connection.send(this, ack, json);
	}

	public void send(final String message) {
		this.connection.send(this, null, message);
	}

	public void send(IOAcknowledge ack, final String message) {
		this.connection.send(this, ack, message);
	}

	public void disconnect() {
		this.connection.unregister(this);
	}

	public void reconnect() {
		this.connection.reconnect();
	}

	public boolean isConnected() {
		return this.connection != null && this.connection.isConnected();
	}

	public String getTransport() {
		IOTransport transport = this.connection.getTransport();
		return transport != null ? transport.getName() : null;
	}

	public Properties getHeaders() {
		return headers;
	}

	void setHeaders(Properties headers) {
		this.headers = headers;
	}

	public SocketIO addHeader(String key, String value) {
		if (this.connection != null)
			throw new RuntimeException(
					"You may only set headers before connecting.\n"
							+ " Try to use new SocketIO().addHeader(key, value).connect(host, callback) "
							+ "instead of SocketIO(host, callback).addHeader(key, value)");
		this.headers.setProperty(key, value);
		return this;
	}

	public String getHeader(String key) {
		if (this.headers.contains(key))
			return this.headers.getProperty(key);
		return null;
	}
}
