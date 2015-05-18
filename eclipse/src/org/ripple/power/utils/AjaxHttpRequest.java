package org.ripple.power.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;

public class AjaxHttpRequest {

	public static final int STATE_UNINITIALIZED = 0;
	public static final int STATE_LOADING = 1;
	public static final int STATE_LOADED = 2;
	public static final int STATE_INTERACTIVE = 3;
	public static final int STATE_COMPLETE = 4;

	public static final String DEFAULT_USERAGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.90 Safari/537.36";

	public static final String DEFAULT_AJAX_CHARSET = "UTF-8";
	public static final String DEFAULT_HTTP_CHARSET = "ISO-8859-1";

	public static final String DEFAULT_REQUEST_METHOD = "POST";
	private int readyState;
	private int status;
	private String statusText;
	private String responseHeaders;
	private byte[] responseBytes;
	@SuppressWarnings("rawtypes")
	private Map responseHeadersMap;
	@SuppressWarnings("rawtypes")
	private final Map requestHeadersMap;
	private ReadyStateChangeListener readyStateChangeListener;
	private boolean async;
	private boolean sent;
	private URLConnection connection;
	private String userAgent = DEFAULT_USERAGENT;
	private String postCharset = DEFAULT_AJAX_CHARSET;
	private Proxy proxy;
	private URL requestURL;
	protected String requestMethod;
	protected String requestUserName;
	protected String requestPassword;

	@SuppressWarnings("rawtypes")
	public AjaxHttpRequest() {
		requestHeadersMap = new LinkedHashMap();
		setRequestHeader("X-Requested-With", "XMLHttpRequest");
		setRequestHeader("Accept",
				"text/javascript, text/html, application/xml, application/json, text/xml, */*");
	}

	public synchronized int getReadyState() {
		return this.readyState;
	}

	public synchronized int getStatus() {
		return this.status;
	}

	public synchronized String getStatusText() {
		return this.statusText;
	}

	@SuppressWarnings("unchecked")
	public void setRequestHeader(String key, String value) {
		this.requestHeadersMap.put(key, value);
	}

	public void open(String method, String url, boolean async, String userName,
			String password) throws IOException {
		URL urlObj = createURL(null, url);
		open(method, urlObj, async, userName, password);
	}

	public void open(final String method, final URL url, boolean async,
			final String userName, final String password) throws IOException {
		this.abort();
		Proxy proxy = this.proxy;
		URLConnection c = proxy == null || proxy == Proxy.NO_PROXY ? url
				.openConnection() : url.openConnection(proxy);
		synchronized (this) {
			this.connection = c;
			this.async = async;
			this.requestMethod = method;
			this.requestURL = url;
			this.requestUserName = userName;
			this.requestPassword = password;
		}
		this.changeState(AjaxHttpRequest.STATE_LOADING, 0, null, null);
	}

	public void open(String url, boolean async) throws IOException {
		open(DEFAULT_REQUEST_METHOD, url, async, null, null);
	}

	public void open(String method, String url, boolean async)
			throws IOException {
		open(method, url, async, null, null);
	}

	@SuppressWarnings("rawtypes")
	public void send(Map parameters) throws IOException {
		Iterator keyItr = parameters.keySet().iterator();
		StringBuffer strb = new StringBuffer();
		while (keyItr.hasNext()) {
			Object key = keyItr.next();
			String keyStr = encode(key);
			String valueStr = encode(parameters.get(key));
			strb.append(keyStr).append("=").append(valueStr);
			strb.append("&");
		}
		send(strb.toString());
	}

	public void send(final String content) throws IOException {
		final URL url = this.requestURL;
		if (url == null) {
			throw new IOException("No URL has been provided.");
		}
		if (this.isAsync()) {
			new Thread("AjaxHttpRequest-" + url.getHost()) {
				@Override
				public void run() {
					try {
						sendSync(content);
					} catch (Throwable thrown) {
						log(Level.WARNING,
								"send(): Error in asynchronous request on "
										+ url, thrown);
					}
				}
			}.start();
		} else {
			sendSync(content);
		}
	}

	public synchronized String getResponseHeader(String headerName) {
		return this.responseHeadersMap == null ? null
				: (String) this.responseHeadersMap.get(headerName);
	}

	public synchronized String getAllResponseHeaders() {
		return this.responseHeaders;
	}

	public synchronized String getResponseText() {
		byte[] bytes = this.responseBytes;
		String encoding = getCharset(this.connection);
		if (encoding == null) {
			encoding = getPostCharset();
		}
		if (encoding == null) {
			encoding = DEFAULT_HTTP_CHARSET;
		}
		try {
			return bytes == null ? null : new String(bytes, encoding);
		} catch (UnsupportedEncodingException uee) {
			log(Level.WARNING, "getResponseText(): Charset '" + encoding
					+ "' did not work. Retrying with " + DEFAULT_HTTP_CHARSET
					+ ".", uee);
			try {
				return new String(bytes, DEFAULT_HTTP_CHARSET);
			} catch (UnsupportedEncodingException uee2) {
				return null;
			}
		}
	}

	@Deprecated
	public synchronized byte[] getResponseBody() {
		return this.getResponseBytes();
	}

	public synchronized byte[] getResponseBytes() {
		return this.responseBytes;
	}

	public void setReadyStateChangeListener(ReadyStateChangeListener listener) {
		this.readyStateChangeListener = listener;
	}

	public void abort() {
		URLConnection c = null;
		synchronized (this) {
			c = this.getConnection();
		}
		if (c instanceof HttpURLConnection) {
			((HttpURLConnection) c).disconnect();
		} else if (c != null) {
			try {
				c.getInputStream().close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	public boolean isAsync() {
		return async;
	}

	public boolean hasSent() {
		return sent;
	}

	protected void setSent(boolean sent) {
		this.sent = sent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getUserAgent() {
		return this.userAgent;
	}

	public String getPostCharset() {
		return this.postCharset;
	}

	public void setPostCharset(String postCharset) {
		this.postCharset = postCharset;
	}

	protected void sendSync(String content) throws IOException {
		if (hasSent()) {
			log(Level.WARNING, "This AjaxHttpRequest Object has sent", null);
			return;
		}
		try {
			URLConnection c;
			synchronized (this) {
				c = this.connection;
			}
			if (c == null) {
				log(Level.WARNING, "Please open AjaxHttpRequest first.", null);
				return;
			}
			setSent(true);
			initConnectionRequestHeader(c);
			int istatus;
			String istatusText;
			InputStream err;
			if (c instanceof HttpURLConnection) {
				HttpURLConnection hc = (HttpURLConnection) c;
				String method = this.requestMethod == null ? DEFAULT_REQUEST_METHOD
						: this.requestMethod;
				method = method.toUpperCase();
				hc.setRequestMethod(method);
				if ("POST".equals(method) && content != null) {
					hc.setDoOutput(true);
					byte[] contentBytes = content.getBytes(this
							.getPostCharset());
					hc.setFixedLengthStreamingMode(contentBytes.length);
					OutputStream out = hc.getOutputStream();
					try {
						out.write(contentBytes);
					} finally {
						out.flush();
					}
				}
				istatus = hc.getResponseCode();
				istatusText = hc.getResponseMessage();
				err = hc.getErrorStream();
			} else {
				istatus = 0;
				istatusText = "";
				err = null;
			}
			synchronized (this) {
				this.responseHeaders = getConnectionResponseHeaders(c);
				this.responseHeadersMap = c.getHeaderFields();
			}
			this.changeState(AjaxHttpRequest.STATE_LOADED, istatus,
					istatusText, null);
			InputStream in = err == null ? c.getInputStream() : err;
			int contentLength = c.getContentLength();
			this.changeState(AjaxHttpRequest.STATE_INTERACTIVE, istatus,
					istatusText, null);
			byte[] bytes = loadStream(in, contentLength == -1 ? 4096
					: contentLength);
			this.changeState(AjaxHttpRequest.STATE_COMPLETE, istatus,
					istatusText, bytes);
		} finally {
			synchronized (this) {
				this.connection = null;
				setSent(false);
			}
		}
	}

	protected void changeState(int readyState, int status,
			String statusMessage, byte[] bytes) {
		synchronized (this) {
			this.readyState = readyState;
			this.status = status;
			this.statusText = statusMessage;
			this.responseBytes = bytes;
		}
		if (this.readyStateChangeListener != null) {
			this.readyStateChangeListener.onReadyStateChange();
		}
	}

	protected String encode(Object str) {
		try {
			return URLEncoder.encode(String.valueOf(str), getPostCharset());
		} catch (UnsupportedEncodingException e) {
			return String.valueOf(str);
		}
	}

	@SuppressWarnings("rawtypes")
	protected void initConnectionRequestHeader(URLConnection c) {
		c.setRequestProperty("User-Agent", this.getUserAgent());
		Iterator keyItor = this.requestHeadersMap.keySet().iterator();
		while (keyItor.hasNext()) {
			String key = (String) keyItor.next();
			String value = (String) this.requestHeadersMap.get(key);
			c.setRequestProperty(key, value);
		}
	}

	public String getRequestHeader(String key) {
		return (String) this.requestHeadersMap.get(key);
	}

	public String removeRequestHeader(String key) {
		return (String) this.requestHeadersMap.remove(key);
	}

	public void removeAllRequestHeaders() {
		this.requestHeadersMap.clear();
	}

	@SuppressWarnings("rawtypes")
	public Map getAllRequestHeaders() {
		return this.requestHeadersMap;
	}

	public URLConnection getConnection() {
		return connection;
	}

	public void setConnection(URLConnection connection) {
		this.connection = connection;
	}

	public Proxy getProxy() {
		return proxy;
	}

	public void setProxy(Proxy proxy) {
		this.proxy = proxy;
	}

	public static void log(Level level, String msg, Throwable thrown) {
		System.err.println(level.getName() + " : " + thrown.getMessage()
				+ " ----- " + msg);
	}

	public static String getConnectionResponseHeaders(URLConnection c) {
		int idx = 0;
		String value;
		StringBuffer buf = new StringBuffer();
		while ((value = c.getHeaderField(idx)) != null) {
			String key = c.getHeaderFieldKey(idx);
			buf.append(key);
			buf.append(": ");
			buf.append(value);
			idx++;
		}
		return buf.toString();
	}

	public static String getCharset(URLConnection connection) {
		String contentType = connection == null ? null : connection
				.getContentType();
		if (contentType != null) {
			StringTokenizer tok = new StringTokenizer(contentType, ";");
			if (tok.hasMoreTokens()) {
				tok.nextToken();
				while (tok.hasMoreTokens()) {
					String assignment = tok.nextToken().trim();
					int eqIdx = assignment.indexOf('=');
					if (eqIdx != -1) {
						String varName = assignment.substring(0, eqIdx).trim();
						if ("charset".equalsIgnoreCase(varName)) {
							String varValue = assignment.substring(eqIdx + 1);
							return unquote(varValue.trim());
						}
					}
				}
			}
		}
		return null;
	}

	public static String unquote(String text) {
		if (text.startsWith("\"") && text.endsWith("\"")) {
			return text.substring(1, text.length() - 2);
		}
		return text;
	}

	protected static URL createURL(URL baseUrl, String relativeUrl)
			throws MalformedURLException {
		return new URL(baseUrl, relativeUrl);
	}

	protected static byte[] loadStream(InputStream in, int initialBufferSize)
			throws IOException {
		if (initialBufferSize == 0) {
			initialBufferSize = 1;
		}
		byte[] buffer = new byte[initialBufferSize];
		int offset = 0;
		for (;;) {
			int remain = buffer.length - offset;
			if (remain <= 0) {
				int newSize = buffer.length * 2;
				byte[] newBuffer = new byte[newSize];
				System.arraycopy(buffer, 0, newBuffer, 0, offset);
				buffer = newBuffer;
				remain = buffer.length - offset;
			}
			int numRead = in.read(buffer, offset, remain);
			if (numRead == -1) {
				break;
			}
			offset += numRead;
		}
		if (offset < buffer.length) {
			byte[] newBuffer = new byte[offset];
			System.arraycopy(buffer, 0, newBuffer, 0, offset);
			buffer = newBuffer;
		}
		return buffer;
	}

	public static abstract class ReadyStateChangeListener {
		public abstract void onReadyStateChange();
	}

}
