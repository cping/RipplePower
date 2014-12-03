package org.ripple.power.utils;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_NOT_MODIFIED;
import static java.net.HttpURLConnection.HTTP_OK;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.security.AccessController;
import java.security.GeneralSecurityException;
import java.security.PrivilegedAction;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONObject;
import org.ripple.power.config.LSystem;
import org.ripple.power.utils.HttpsUtils.ResponseResult;

public class HttpRequest {

	public static String getHttps(String url) {
		ResponseResult result = HttpsUtils.getSSL(url);
		if (result.ok()) {
			return result.getResult();
		}
		return null;
	}

	public static boolean isValidRedirectUrl(String url) {
		return url != null && url.startsWith("/") || isAbsoluteUrl(url);
	}

	public static boolean isAbsoluteUrl(String url) {
		final Pattern ABSOLUTE_URL = Pattern.compile("\\A[a-z.+-]+://.*",
				Pattern.CASE_INSENSITIVE);
		return ABSOLUTE_URL.matcher(url).matches();
	}

	public static boolean url(String url) {
		if (url == null) {
			return false;
		}
		String regex = "^(https?|ftp|wss)://(-\\.)?([^\\s/?\\.#-]+\\.?)+(/[\\S ]*)?$";
		return Pattern.matches(regex, url);
	}

	public static final String CHARSET_UTF8 = "UTF-8";

	public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";

	public static final String CONTENT_TYPE_JSON = "application/json";

	public static final String ENCODING_GZIP = "gzip";

	public static final String HEADER_ACCEPT = "Accept";

	public static final String HEADER_ACCEPT_CHARSET = "Accept-Charset";

	public static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";

	public static final String HEADER_ACCEPT_LANGUAGE = "Accept-Language";

	public static final String HEADER_ACCEPT_HOST = "Host";

	public static final String HEADER_AUTHORIZATION = "Authorization";

	public static final String HEADER_CACHE_CONTROL = "Cache-Control";

	public static final String HEADER_CONTENT_ENCODING = "Content-Encoding";

	public static final String HEADER_CONTENT_LENGTH = "Content-Length";

	public static final String HEADER_CONTENT_TYPE = "Content-Type";

	public static final String HEADER_DATE = "Date";

	public static final String HEADER_ETAG = "ETag";

	public static final String HEADER_EXPIRES = "Expires";

	public static final String HEADER_IF_NONE_MATCH = "If-None-Match";

	public static final String HEADER_LAST_MODIFIED = "Last-Modified";

	public static final String HEADER_LOCATION = "Location";

	public static final String HEADER_PROXY_AUTHORIZATION = "Proxy-Authorization";

	public static final String HEADER_REFERER = "Referer";

	public static final String HEADER_SERVER = "Server";

	public static final String HEADER_USER_AGENT = "User-Agent";

	public static final String METHOD_DELETE = "DELETE";

	public static final String HEADER_COOKIE = "cookie";

	public static final String HEADER_SET_COOKIE = "set-cookie";

	public static final String METHOD_GET = "GET";

	public static final String METHOD_HEAD = "HEAD";

	public static final String METHOD_OPTIONS = "OPTIONS";

	public static final String METHOD_POST = "POST";

	public static final String METHOD_PUT = "PUT";

	public static final String METHOD_TRACE = "TRACE";

	public static final String PARAM_CHARSET = "charset";

	private static final String BOUNDARY = "00content0boundary00"
			+ System.currentTimeMillis();

	private static final String CONTENT_TYPE_MULTIPART = "multipart/form-data; boundary="
			+ BOUNDARY;

	private static final String CRLF = "\r\n";

	private static final String[] EMPTY_STRINGS = new String[0];

	private static SSLSocketFactory TRUSTED_FACTORY;

	private static HostnameVerifier TRUSTED_VERIFIER;

	private static String getValidCharset(final String charset) {
		if (charset != null && charset.length() > 0) {
			return charset;
		} else {
			return CHARSET_UTF8;
		}
	}

	private static SSLSocketFactory getTrustedFactory()
			throws HttpRequestException {
		if (TRUSTED_FACTORY == null) {
			final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0];
				}

				public void checkClientTrusted(X509Certificate[] chain,
						String authType) {

				}

				public void checkServerTrusted(X509Certificate[] chain,
						String authType) {

				}
			} };
			try {
				SSLContext context = SSLContext.getInstance("TLS");
				context.init(null, trustAllCerts, new SecureRandom());
				TRUSTED_FACTORY = context.getSocketFactory();
			} catch (GeneralSecurityException e) {
				IOException ioException = new IOException(
						"Security exception configuring SSL context");
				ioException.initCause(e);
				throw new HttpRequestException(ioException);
			}
		}

		return TRUSTED_FACTORY;
	}

	private static HostnameVerifier getTrustedVerifier() {
		if (TRUSTED_VERIFIER == null)
			TRUSTED_VERIFIER = new HostnameVerifier() {

				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};

		return TRUSTED_VERIFIER;
	}

	private static StringBuilder addPathSeparator(final String baseUrl,
			final StringBuilder result) {
		if (baseUrl.indexOf(':') + 2 == baseUrl.lastIndexOf('/'))
			result.append('/');
		return result;
	}

	private static StringBuilder addParamPrefix(final String baseUrl,
			final StringBuilder result) {
		final int queryStart = baseUrl.indexOf('?');
		final int lastChar = result.length() - 1;
		if (queryStart == -1)
			result.append('?');
		else if (queryStart < lastChar && baseUrl.charAt(lastChar) != '&')
			result.append('&');
		return result;
	}

	public interface ConnectionFactory {

		HttpURLConnection create(URL url) throws IOException;

		HttpURLConnection create(URL url, Proxy proxy) throws IOException;

		ConnectionFactory DEFAULT = new ConnectionFactory() {
			public HttpURLConnection create(URL url) throws IOException {
				return (HttpURLConnection) url.openConnection();
			}

			public HttpURLConnection create(URL url, Proxy proxy)
					throws IOException {
				return (HttpURLConnection) url.openConnection(proxy);
			}
		};
	}

	private static ConnectionFactory CONNECTION_FACTORY = ConnectionFactory.DEFAULT;

	public static void setConnectionFactory(
			final ConnectionFactory connectionFactory) {
		if (connectionFactory == null)
			CONNECTION_FACTORY = ConnectionFactory.DEFAULT;
		else
			CONNECTION_FACTORY = connectionFactory;
	}

	public static class Base64 {

		private final static byte EQUALS_SIGN = (byte) '=';

		private final static String PREFERRED_ENCODING = "US-ASCII";

		private final static byte[] _STANDARD_ALPHABET = { (byte) 'A',
				(byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F',
				(byte) 'G', (byte) 'H', (byte) 'I', (byte) 'J', (byte) 'K',
				(byte) 'L', (byte) 'M', (byte) 'N', (byte) 'O', (byte) 'P',
				(byte) 'Q', (byte) 'R', (byte) 'S', (byte) 'T', (byte) 'U',
				(byte) 'V', (byte) 'W', (byte) 'X', (byte) 'Y', (byte) 'Z',
				(byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e',
				(byte) 'f', (byte) 'g', (byte) 'h', (byte) 'i', (byte) 'j',
				(byte) 'k', (byte) 'l', (byte) 'm', (byte) 'n', (byte) 'o',
				(byte) 'p', (byte) 'q', (byte) 'r', (byte) 's', (byte) 't',
				(byte) 'u', (byte) 'v', (byte) 'w', (byte) 'x', (byte) 'y',
				(byte) 'z', (byte) '0', (byte) '1', (byte) '2', (byte) '3',
				(byte) '4', (byte) '5', (byte) '6', (byte) '7', (byte) '8',
				(byte) '9', (byte) '+', (byte) '/' };

		private Base64() {
		}

		private static byte[] encode3to4(byte[] source, int srcOffset,
				int numSigBytes, byte[] destination, int destOffset) {

			byte[] ALPHABET = _STANDARD_ALPHABET;

			int inBuff = (numSigBytes > 0 ? ((source[srcOffset] << 24) >>> 8)
					: 0)
					| (numSigBytes > 1 ? ((source[srcOffset + 1] << 24) >>> 16)
							: 0)
					| (numSigBytes > 2 ? ((source[srcOffset + 2] << 24) >>> 24)
							: 0);

			switch (numSigBytes) {
			case 3:
				destination[destOffset] = ALPHABET[(inBuff >>> 18)];
				destination[destOffset + 1] = ALPHABET[(inBuff >>> 12) & 0x3f];
				destination[destOffset + 2] = ALPHABET[(inBuff >>> 6) & 0x3f];
				destination[destOffset + 3] = ALPHABET[(inBuff) & 0x3f];
				return destination;

			case 2:
				destination[destOffset] = ALPHABET[(inBuff >>> 18)];
				destination[destOffset + 1] = ALPHABET[(inBuff >>> 12) & 0x3f];
				destination[destOffset + 2] = ALPHABET[(inBuff >>> 6) & 0x3f];
				destination[destOffset + 3] = EQUALS_SIGN;
				return destination;

			case 1:
				destination[destOffset] = ALPHABET[(inBuff >>> 18)];
				destination[destOffset + 1] = ALPHABET[(inBuff >>> 12) & 0x3f];
				destination[destOffset + 2] = EQUALS_SIGN;
				destination[destOffset + 3] = EQUALS_SIGN;
				return destination;

			default:
				return destination;
			}
		}

		public static String encode(String string) {
			byte[] bytes;
			try {
				bytes = string.getBytes(PREFERRED_ENCODING);
			} catch (UnsupportedEncodingException e) {
				bytes = string.getBytes();
			}
			return encodeBytes(bytes);
		}

		public static String encodeBytes(byte[] source) {
			return encodeBytes(source, 0, source.length);
		}

		public static String encodeBytes(byte[] source, int off, int len) {
			byte[] encoded = encodeBytesToBytes(source, off, len);
			try {
				return new String(encoded, PREFERRED_ENCODING);
			} catch (UnsupportedEncodingException uue) {
				return new String(encoded);
			}
		}

		public static byte[] encodeBytesToBytes(byte[] source, int off, int len) {

			if (source == null)
				throw new NullPointerException("Cannot serialize a null array.");

			if (off < 0)
				throw new IllegalArgumentException(
						"Cannot have negative offset: " + off);

			if (len < 0)
				throw new IllegalArgumentException(
						"Cannot have length offset: " + len);

			if (off + len > source.length)
				throw new IllegalArgumentException(
						String.format(
								"Cannot have offset of %d and length of %d with array of length %d",
								off, len, source.length));

			int encLen = (len / 3) * 4 + (len % 3 > 0 ? 4 : 0);

			byte[] outBuff = new byte[encLen];

			int d = 0;
			int e = 0;
			int len2 = len - 2;
			for (; d < len2; d += 3, e += 4)
				encode3to4(source, d + off, 3, outBuff, e);

			if (d < len) {
				encode3to4(source, d + off, len - d, outBuff, e);
				e += 4;
			}

			if (e <= outBuff.length - 1) {
				byte[] finalOut = new byte[e];
				System.arraycopy(outBuff, 0, finalOut, 0, e);
				return finalOut;
			} else
				return outBuff;
		}
	}

	public static class HttpRequestException extends RuntimeException {

		private static final long serialVersionUID = -1170466989781746231L;

		protected HttpRequestException(final IOException cause) {
			super(cause);
		}

		@Override
		public IOException getCause() {
			return (IOException) super.getCause();
		}
	}

	protected static abstract class Operation<V> implements Callable<V> {

		protected abstract V run() throws HttpRequestException, IOException;

		protected abstract void done() throws IOException;

		public V call() throws HttpRequestException {
			boolean thrown = false;
			try {
				return run();
			} catch (HttpRequestException e) {
				thrown = true;
				throw e;
			} catch (IOException e) {
				thrown = true;
				throw new HttpRequestException(e);
			} finally {
				try {
					done();
				} catch (IOException e) {
					if (!thrown)
						throw new HttpRequestException(e);
				}
			}
		}
	}

	protected static abstract class CloseOperation<V> extends Operation<V> {

		private final Closeable closeable;

		private final boolean ignoreCloseExceptions;

		protected CloseOperation(final Closeable closeable,
				final boolean ignoreCloseExceptions) {
			this.closeable = closeable;
			this.ignoreCloseExceptions = ignoreCloseExceptions;
		}

		@Override
		protected void done() throws IOException {
			if (closeable instanceof Flushable)
				((Flushable) closeable).flush();
			if (ignoreCloseExceptions)
				try {
					closeable.close();
				} catch (IOException e) {
				}
			else
				closeable.close();
		}
	}

	protected static abstract class FlushOperation<V> extends Operation<V> {

		private final Flushable flushable;

		protected FlushOperation(final Flushable flushable) {
			this.flushable = flushable;
		}

		@Override
		protected void done() throws IOException {
			flushable.flush();
		}
	}

	public static class RequestOutputStream extends BufferedOutputStream {

		private final CharsetEncoder encoder;

		public RequestOutputStream(final OutputStream stream,
				final String charset, final int bufferSize) {
			super(stream, bufferSize);

			encoder = Charset.forName(getValidCharset(charset)).newEncoder();
		}

		public RequestOutputStream write(final String value) throws IOException {
			final ByteBuffer bytes = encoder.encode(CharBuffer.wrap(value));

			super.write(bytes.array(), 0, bytes.limit());

			return this;
		}
	}

	public String host() {
		return host;
	}

	public static String encode(final CharSequence url)
			throws HttpRequestException {
		URL parsed;
		try {
			parsed = new URL(url.toString());
		} catch (IOException e) {
			throw new HttpRequestException(e);
		}

		String host = parsed.getHost();
		int port = parsed.getPort();
		if (port != -1)
			host = host + ':' + Integer.toString(port);

		try {
			String encoded = new URI(parsed.getProtocol(), host,
					parsed.getPath(), parsed.getQuery(), null).toASCIIString();
			int paramsStart = encoded.indexOf('?');
			if (paramsStart > 0 && paramsStart + 1 < encoded.length())
				encoded = encoded.substring(0, paramsStart + 1)
						+ encoded.substring(paramsStart + 1)
								.replace("+", "%2B");
			return encoded;
		} catch (URISyntaxException e) {
			IOException io = new IOException("Parsing URI failed");
			io.initCause(e);
			throw new HttpRequestException(io);
		}
	}

	public static String append(final CharSequence url, final Map<?, ?> params) {
		final String baseUrl = url.toString();
		if (params == null || params.isEmpty())
			return baseUrl;

		final StringBuilder result = new StringBuilder(baseUrl);

		addPathSeparator(baseUrl, result);
		addParamPrefix(baseUrl, result);

		Entry<?, ?> entry;
		Object value;
		Iterator<?> iterator = params.entrySet().iterator();
		entry = (Entry<?, ?>) iterator.next();
		result.append(entry.getKey().toString());
		result.append('=');
		value = entry.getValue();
		if (value != null)
			result.append(value);

		while (iterator.hasNext()) {
			result.append('&');
			entry = (Entry<?, ?>) iterator.next();
			result.append(entry.getKey().toString());
			result.append('=');
			value = entry.getValue();
			if (value != null)
				result.append(value);
		}

		return result.toString();
	}

	public static String append(final CharSequence url, final Object... params) {
		final String baseUrl = url.toString();
		if (params == null || params.length == 0)
			return baseUrl;

		if (params.length % 2 != 0)
			throw new IllegalArgumentException(
					"Must specify an even number of parameter names/values");

		final StringBuilder result = new StringBuilder(baseUrl);

		addPathSeparator(baseUrl, result);
		addParamPrefix(baseUrl, result);

		Object value;
		result.append(params[0]);
		result.append('=');
		value = params[1];
		if (value != null)
			result.append(value);

		for (int i = 2; i < params.length; i += 2) {
			result.append('&');
			result.append(params[i]);
			result.append('=');
			value = params[i + 1];
			if (value != null)
				result.append(value);
		}

		return result.toString();
	}

	public static HttpRequest get(final CharSequence url)
			throws HttpRequestException {
		return new HttpRequest(url, METHOD_GET);
	}

	public static HttpRequest get(final URL url) throws HttpRequestException {
		return new HttpRequest(url, METHOD_GET);
	}

	public static HttpRequest get(final CharSequence baseUrl,
			final Map<?, ?> params, final boolean encode) {
		String url = append(baseUrl, params);
		return get(encode ? encode(url) : url);
	}

	public static HttpRequest get(final CharSequence baseUrl,
			final boolean encode, final Object... params) {
		String url = append(baseUrl, params);
		return get(encode ? encode(url) : url);
	}

	public static HttpRequest post(final CharSequence url)
			throws HttpRequestException {
		return new HttpRequest(url, METHOD_POST);
	}

	public static HttpRequest post(final URL url) throws HttpRequestException {
		return new HttpRequest(url, METHOD_POST);
	}

	public static HttpRequest post(final CharSequence baseUrl,
			final Map<?, ?> params, final boolean encode) {
		String url = append(baseUrl, params);
		return post(encode ? encode(url) : url);
	}

	public static HttpRequest post(final CharSequence baseUrl,
			final boolean encode, final Object... params) {
		String url = append(baseUrl, params);
		return post(encode ? encode(url) : url);
	}

	public static HttpRequest put(final CharSequence url)
			throws HttpRequestException {
		return new HttpRequest(url, METHOD_PUT);
	}

	public static HttpRequest put(final URL url) throws HttpRequestException {
		return new HttpRequest(url, METHOD_PUT);
	}

	public static HttpRequest put(final CharSequence baseUrl,
			final Map<?, ?> params, final boolean encode) {
		String url = append(baseUrl, params);
		return put(encode ? encode(url) : url);
	}

	public static HttpRequest put(final CharSequence baseUrl,
			final boolean encode, final Object... params) {
		String url = append(baseUrl, params);
		return put(encode ? encode(url) : url);
	}

	public static HttpRequest delete(final CharSequence url)
			throws HttpRequestException {
		return new HttpRequest(url, METHOD_DELETE);
	}

	public static HttpRequest delete(final URL url) throws HttpRequestException {
		return new HttpRequest(url, METHOD_DELETE);
	}

	public static HttpRequest delete(final CharSequence baseUrl,
			final Map<?, ?> params, final boolean encode) {
		String url = append(baseUrl, params);
		return delete(encode ? encode(url) : url);
	}

	public static HttpRequest delete(final CharSequence baseUrl,
			final boolean encode, final Object... params) {
		String url = append(baseUrl, params);
		return delete(encode ? encode(url) : url);
	}

	public static HttpRequest head(final CharSequence url)
			throws HttpRequestException {
		return new HttpRequest(url, METHOD_HEAD);
	}

	public static HttpRequest head(final URL url) throws HttpRequestException {
		return new HttpRequest(url, METHOD_HEAD);
	}

	public static HttpRequest head(final CharSequence baseUrl,
			final Map<?, ?> params, final boolean encode) {
		String url = append(baseUrl, params);
		return head(encode ? encode(url) : url);
	}

	public static HttpRequest head(final CharSequence baseUrl,
			final boolean encode, final Object... params) {
		String url = append(baseUrl, params);
		return head(encode ? encode(url) : url);
	}

	public static HttpRequest options(final CharSequence url)
			throws HttpRequestException {
		return new HttpRequest(url, METHOD_OPTIONS);
	}

	public static HttpRequest options(final URL url)
			throws HttpRequestException {
		return new HttpRequest(url, METHOD_OPTIONS);
	}

	public static HttpRequest trace(final CharSequence url)
			throws HttpRequestException {
		return new HttpRequest(url, METHOD_TRACE);
	}

	public static HttpRequest trace(final URL url) throws HttpRequestException {
		return new HttpRequest(url, METHOD_TRACE);
	}

	public static void keepAlive(final boolean keepAlive) {
		setProperty("http.keepAlive", Boolean.toString(keepAlive));
	}

	public static void proxyHost(final String host) {
		setProperty("http.proxyHost", host);
		setProperty("https.proxyHost", host);
	}

	public static void proxyPort(final int port) {
		final String portValue = Integer.toString(port);
		setProperty("http.proxyPort", portValue);
		setProperty("https.proxyPort", portValue);
	}

	public static void nonProxyHosts(final String... hosts) {
		if (hosts != null && hosts.length > 0) {
			StringBuilder separated = new StringBuilder();
			int last = hosts.length - 1;
			for (int i = 0; i < last; i++)
				separated.append(hosts[i]).append('|');
			separated.append(hosts[last]);
			setProperty("http.nonProxyHosts", separated.toString());
		} else
			setProperty("http.nonProxyHosts", null);
	}

	private static String setProperty(final String name, final String value) {
		final PrivilegedAction<String> action;
		if (value != null)
			action = new PrivilegedAction<String>() {

				public String run() {
					return System.setProperty(name, value);
				}
			};
		else
			action = new PrivilegedAction<String>() {

				public String run() {
					return System.clearProperty(name);
				}
			};
		return AccessController.doPrivileged(action);
	}

	private HttpURLConnection connection = null;

	private final URL url;

	private final String requestMethod;

	private RequestOutputStream output;

	private boolean multipart;

	private boolean form;

	private boolean ignoreCloseExceptions = true;

	private boolean uncompress = false;

	private int bufferSize = 8192;

	private String httpProxyHost;

	private int httpProxyPort;

	String host;

	public HttpRequest(final CharSequence u, final String method)
			throws HttpRequestException {
		try {
			this.url = new URL(u.toString());
			this.host = url.getHost();
		} catch (MalformedURLException e) {
			throw new HttpRequestException(e);
		}
		this.requestMethod = method;
	}

	public HttpRequest(final URL url, final String method)
			throws HttpRequestException {
		this.url = url;
		this.requestMethod = method;
	}

	private Proxy createProxy() {
		return new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(httpProxyHost,
				httpProxyPort));
	}

	private HttpURLConnection createConnection() {
		try {
			final HttpURLConnection connection;
			if (LSystem.applicationProxy != null) {
				connection = CONNECTION_FACTORY.create(url,
						LSystem.applicationProxy.getProxy());
			} else {
				if (httpProxyHost != null) {
					connection = CONNECTION_FACTORY.create(url, createProxy());
				} else {
					connection = CONNECTION_FACTORY.create(url);
				}
			}
			connection.setRequestMethod(requestMethod);
			connection.setConnectTimeout(9000);
			connection.setReadTimeout(9000);
			if (LSystem.applicationProxy != null
					&& LSystem.applicationProxy.isProxyWithAuthentication()) {
				connection.setRequestProperty("Proxy-Authorization", "Basic "
						+ LSystem.applicationProxy.getProxyAuthentication());
			}
			return connection;
		} catch (IOException e) {
			throw new HttpRequestException(e);
		}
	}

	@Override
	public String toString() {
		return method() + ' ' + url();
	}

	public HttpURLConnection getConnection() {
		if (connection == null) {
			connection = createConnection();
		}
		return connection;
	}

	public HttpRequest ignoreCloseExceptions(final boolean ignore) {
		ignoreCloseExceptions = ignore;
		return this;
	}

	public boolean ignoreCloseExceptions() {
		return ignoreCloseExceptions;
	}

	public int code() throws IOException {

		closeOutput();
		return getConnection().getResponseCode();

	}

	public HttpRequest code(final AtomicInteger output)
			throws HttpRequestException, IOException {
		output.set(code());
		return this;
	}

	public boolean ok() throws HttpRequestException, IOException {
		return HTTP_OK == code();
	}

	public boolean created() throws HttpRequestException, IOException {
		return HTTP_CREATED == code();
	}

	public boolean serverError() throws HttpRequestException, IOException {
		return HTTP_INTERNAL_ERROR == code();
	}

	public boolean badRequest() throws HttpRequestException, IOException {
		return HTTP_BAD_REQUEST == code();
	}

	public boolean notFound() throws HttpRequestException, IOException {
		return HTTP_NOT_FOUND == code();
	}

	public boolean notModified() throws HttpRequestException, IOException {
		return HTTP_NOT_MODIFIED == code();
	}

	public String message() throws HttpRequestException {
		try {
			closeOutput();
			return getConnection().getResponseMessage();
		} catch (IOException e) {
			throw new HttpRequestException(e);
		}
	}

	public HttpRequest disconnect() {
		getConnection().disconnect();
		return this;
	}

	public HttpRequest chunk(final int size) {
		getConnection().setChunkedStreamingMode(size);
		return this;
	}

	public HttpRequest bufferSize(final int size) {
		if (size < 1)
			throw new IllegalArgumentException("Size must be greater than zero");
		bufferSize = size;
		return this;
	}

	public int bufferSize() {
		return bufferSize;
	}

	public HttpRequest uncompress(final boolean uncompress) {
		this.uncompress = uncompress;
		return this;
	}

	protected ByteArrayOutputStream byteStream() {
		final int size = contentLength();
		if (size > 0)
			return new ByteArrayOutputStream(size);
		else
			return new ByteArrayOutputStream();
	}

	public String body(final String charset) throws HttpRequestException {
		final ByteArrayOutputStream output = byteStream();
		try {
			copy(buffer(), output);
			return output.toString(getValidCharset(charset));
		} catch (IOException e) {
			throw new HttpRequestException(e);
		}
	}

	public String body() throws HttpRequestException {
		return body(charset());
	}

	public HttpRequest body(final AtomicReference<String> output)
			throws HttpRequestException {
		output.set(body());
		return this;
	}

	public HttpRequest body(final AtomicReference<String> output,
			final String charset) throws HttpRequestException {
		output.set(body(charset));
		return this;
	}

	public boolean isBodyEmpty() throws HttpRequestException {
		return contentLength() == 0;
	}

	public byte[] bytes() throws HttpRequestException {
		final ByteArrayOutputStream output = byteStream();
		try {
			copy(buffer(), output);
		} catch (IOException e) {
			throw new HttpRequestException(e);
		}
		return output.toByteArray();
	}

	public BufferedInputStream buffer() throws HttpRequestException,
			IOException {
		return new BufferedInputStream(stream(), bufferSize);
	}

	public InputStream stream() throws HttpRequestException, IOException {
		InputStream stream;
		if (code() < HTTP_BAD_REQUEST)
			try {
				stream = getConnection().getInputStream();
			} catch (IOException e) {
				throw new HttpRequestException(e);
			}
		else {
			stream = getConnection().getErrorStream();
			if (stream == null)
				try {
					stream = getConnection().getInputStream();
				} catch (IOException e) {
					throw new HttpRequestException(e);
				}
		}

		if (!uncompress || !ENCODING_GZIP.equals(contentEncoding()))
			return stream;
		else
			try {
				return new GZIPInputStream(stream);
			} catch (IOException e) {
				throw new HttpRequestException(e);
			}
	}

	public InputStreamReader reader(final String charset)
			throws HttpRequestException, IOException {
		try {
			return new InputStreamReader(stream(), getValidCharset(charset));
		} catch (UnsupportedEncodingException e) {
			throw new HttpRequestException(e);
		}
	}

	public InputStreamReader reader() throws HttpRequestException, IOException {
		return reader(charset());
	}

	public BufferedReader bufferedReader(final String charset)
			throws HttpRequestException, IOException {
		return new BufferedReader(reader(charset), bufferSize);
	}

	public BufferedReader bufferedReader() throws HttpRequestException,
			IOException {
		return bufferedReader(charset());
	}

	public HttpRequest receive(final File file) throws HttpRequestException {
		final OutputStream output;
		try {
			output = new BufferedOutputStream(new FileOutputStream(file),
					bufferSize);
		} catch (FileNotFoundException e) {
			throw new HttpRequestException(e);
		}
		return new CloseOperation<HttpRequest>(output, ignoreCloseExceptions) {

			@Override
			protected HttpRequest run() throws HttpRequestException,
					IOException {
				return receive(output);
			}
		}.call();
	}

	public HttpRequest receive(final OutputStream output)
			throws HttpRequestException {
		try {
			return copy(buffer(), output);
		} catch (IOException e) {
			throw new HttpRequestException(e);
		}
	}

	public HttpRequest receive(final PrintStream output)
			throws HttpRequestException {
		return receive((OutputStream) output);
	}

	public HttpRequest receive(final Appendable appendable)
			throws HttpRequestException, IOException {
		final BufferedReader reader = bufferedReader();
		return new CloseOperation<HttpRequest>(reader, ignoreCloseExceptions) {

			@Override
			public HttpRequest run() throws IOException {
				final CharBuffer buffer = CharBuffer.allocate(bufferSize);
				int read;
				while ((read = reader.read(buffer)) != -1) {
					buffer.rewind();
					appendable.append(buffer, 0, read);
					buffer.rewind();
				}
				return HttpRequest.this;
			}
		}.call();
	}

	public HttpRequest receive(final Writer writer)
			throws HttpRequestException, IOException {
		final BufferedReader reader = bufferedReader();
		return new CloseOperation<HttpRequest>(reader, ignoreCloseExceptions) {

			@Override
			public HttpRequest run() throws IOException {
				return copy(reader, writer);
			}
		}.call();
	}

	public HttpRequest readTimeout(final int timeout) {
		getConnection().setReadTimeout(timeout);
		return this;
	}

	public HttpRequest connectTimeout(final int timeout) {
		getConnection().setConnectTimeout(timeout);
		return this;
	}

	public HttpRequest header(final String name, final String value) {
		getConnection().setRequestProperty(name, value);
		return this;
	}

	public HttpRequest cookie(final String value) {
		return header(HEADER_COOKIE, value);
	}

	public HttpRequest header(final String name, final Number value) {
		return header(name, value != null ? value.toString() : null);
	}

	public HttpRequest headers(final Map<String, String> headers) {
		if (!headers.isEmpty())
			for (Entry<String, String> header : headers.entrySet())
				header(header);
		return this;
	}

	public HttpRequest header(final Entry<String, String> header) {
		return header(header.getKey(), header.getValue());
	}

	public String header(final String name) throws HttpRequestException {
		closeOutputQuietly();
		return getConnection().getHeaderField(name);
	}

	public Map<String, List<String>> headers() throws HttpRequestException {
		closeOutputQuietly();
		return getConnection().getHeaderFields();
	}

	public long dateHeader(final String name) throws HttpRequestException {
		return dateHeader(name, -1L);
	}

	public long dateHeader(final String name, final long defaultValue)
			throws HttpRequestException {
		closeOutputQuietly();
		return getConnection().getHeaderFieldDate(name, defaultValue);
	}

	public int intHeader(final String name) throws HttpRequestException {
		return intHeader(name, -1);
	}

	public int intHeader(final String name, final int defaultValue)
			throws HttpRequestException {
		closeOutputQuietly();
		return getConnection().getHeaderFieldInt(name, defaultValue);
	}

	public String[] headers(final String name) {
		final Map<String, List<String>> headers = headers();
		if (headers == null || headers.isEmpty())
			return EMPTY_STRINGS;

		final List<String> values = headers.get(name);
		if (values != null && !values.isEmpty())
			return values.toArray(new String[values.size()]);
		else
			return EMPTY_STRINGS;
	}

	public String parameter(final String headerName, final String paramName) {
		return getParam(header(headerName), paramName);
	}

	public Map<String, String> parameters(final String headerName) {
		return getParams(header(headerName));
	}

	protected Map<String, String> getParams(final String header) {
		if (header == null || header.length() == 0)
			return Collections.emptyMap();

		final int headerLength = header.length();
		int start = header.indexOf(';') + 1;
		if (start == 0 || start == headerLength)
			return Collections.emptyMap();

		int end = header.indexOf(';', start);
		if (end == -1)
			end = headerLength;

		Map<String, String> params = new LinkedHashMap<String, String>();
		while (start < end) {
			int nameEnd = header.indexOf('=', start);
			if (nameEnd != -1 && nameEnd < end) {
				String name = header.substring(start, nameEnd).trim();
				if (name.length() > 0) {
					String value = header.substring(nameEnd + 1, end).trim();
					int length = value.length();
					if (length != 0)
						if (length > 2 && '"' == value.charAt(0)
								&& '"' == value.charAt(length - 1))
							params.put(name, value.substring(1, length - 1));
						else
							params.put(name, value);
				}
			}

			start = end + 1;
			end = header.indexOf(';', start);
			if (end == -1)
				end = headerLength;
		}

		return params;
	}

	protected String getParam(final String value, final String paramName) {
		if (value == null || value.length() == 0)
			return null;

		final int length = value.length();
		int start = value.indexOf(';') + 1;
		if (start == 0 || start == length)
			return null;

		int end = value.indexOf(';', start);
		if (end == -1)
			end = length;

		while (start < end) {
			int nameEnd = value.indexOf('=', start);
			if (nameEnd != -1 && nameEnd < end
					&& paramName.equals(value.substring(start, nameEnd).trim())) {
				String paramValue = value.substring(nameEnd + 1, end).trim();
				int valueLength = paramValue.length();
				if (valueLength != 0)
					if (valueLength > 2 && '"' == paramValue.charAt(0)
							&& '"' == paramValue.charAt(valueLength - 1))
						return paramValue.substring(1, valueLength - 1);
					else
						return paramValue;
			}

			start = end + 1;
			end = value.indexOf(';', start);
			if (end == -1)
				end = length;
		}

		return null;
	}

	public String charset() {
		return parameter(HEADER_CONTENT_TYPE, PARAM_CHARSET);
	}

	public HttpRequest userAgent(final String userAgent) {
		return header(HEADER_USER_AGENT, userAgent);
	}

	public HttpRequest referer(final String referer) {
		return header(HEADER_REFERER, referer);
	}

	public HttpRequest useCaches(final boolean useCaches) {
		getConnection().setUseCaches(useCaches);
		return this;
	}

	public HttpRequest acceptEncoding(final String acceptEncoding) {
		return header(HEADER_ACCEPT_ENCODING, acceptEncoding);
	}

	public HttpRequest acceptLanguage(final String lang) {
		return header(HEADER_ACCEPT_LANGUAGE, lang);
	}

	public HttpRequest acceptHost(final String host) {
		return header(HEADER_ACCEPT_HOST, host);
	}

	public HttpRequest acceptGzipEncoding() {
		return acceptEncoding(ENCODING_GZIP);
	}

	public HttpRequest acceptCharset(final String acceptCharset) {
		return header(HEADER_ACCEPT_CHARSET, acceptCharset);
	}

	public String contentEncoding() {
		return header(HEADER_CONTENT_ENCODING);
	}

	public String server() {
		return header(HEADER_SERVER);
	}

	public long date() {
		return dateHeader(HEADER_DATE);
	}

	public String cacheControl() {
		return header(HEADER_CACHE_CONTROL);
	}

	public String eTag() {
		return header(HEADER_ETAG);
	}

	public long expires() {
		return dateHeader(HEADER_EXPIRES);
	}

	public long lastModified() {
		return dateHeader(HEADER_LAST_MODIFIED);
	}

	public String location() {
		return header(HEADER_LOCATION);
	}

	public HttpRequest authorization(final String authorization) {
		return header(HEADER_AUTHORIZATION, authorization);
	}

	public HttpRequest proxyAuthorization(final String proxyAuthorization) {
		return header(HEADER_PROXY_AUTHORIZATION, proxyAuthorization);
	}

	public HttpRequest basic(final String name, final String password) {
		return authorization("Basic " + Base64.encode(name + ':' + password));
	}

	public HttpRequest proxyBasic(final String name, final String password) {
		return proxyAuthorization("Basic "
				+ Base64.encode(name + ':' + password));
	}

	public HttpRequest ifModifiedSince(final long ifModifiedSince) {
		getConnection().setIfModifiedSince(ifModifiedSince);
		return this;
	}

	public HttpRequest ifNoneMatch(final String ifNoneMatch) {
		return header(HEADER_IF_NONE_MATCH, ifNoneMatch);
	}

	public HttpRequest contentType(final String contentType) {
		return contentType(contentType, null);
	}

	public HttpRequest contentType(final String contentType,
			final String charset) {
		if (charset != null && charset.length() > 0) {
			final String separator = "; " + PARAM_CHARSET + '=';
			return header(HEADER_CONTENT_TYPE, contentType + separator
					+ charset);
		} else
			return header(HEADER_CONTENT_TYPE, contentType);
	}

	public String contentType() {
		return header(HEADER_CONTENT_TYPE);
	}

	public int contentLength() {
		return intHeader(HEADER_CONTENT_LENGTH);
	}

	public HttpRequest contentLength(final String contentLength) {
		return contentLength(Integer.parseInt(contentLength));
	}

	public HttpRequest contentLength(final int contentLength) {
		getConnection().setFixedLengthStreamingMode(contentLength);
		return this;
	}

	public HttpRequest accept(final String accept) {
		return header(HEADER_ACCEPT, accept);
	}

	public HttpRequest acceptJson() {
		return accept(CONTENT_TYPE_JSON);
	}

	protected HttpRequest copy(final InputStream input,
			final OutputStream output) throws IOException {
		return new CloseOperation<HttpRequest>(input, ignoreCloseExceptions) {

			@Override
			public HttpRequest run() throws IOException {
				final byte[] buffer = new byte[bufferSize];
				int read;
				while ((read = input.read(buffer)) != -1) {
					output.write(buffer, 0, read);
				}
				return HttpRequest.this;
			}
		}.call();
	}

	protected HttpRequest copy(final Reader input, final Writer output)
			throws IOException {
		return new CloseOperation<HttpRequest>(input, ignoreCloseExceptions) {

			@Override
			public HttpRequest run() throws IOException {
				final char[] buffer = new char[bufferSize];
				int read;
				while ((read = input.read(buffer)) != -1)
					output.write(buffer, 0, read);
				return HttpRequest.this;
			}
		}.call();
	}

	protected HttpRequest closeOutput() throws IOException {
		if (output == null)
			return this;
		if (multipart)
			output.write(CRLF + "--" + BOUNDARY + "--" + CRLF);
		if (ignoreCloseExceptions)
			try {
				output.close();
			} catch (IOException ignored) {

			}
		else
			output.close();
		output = null;
		return this;
	}

	protected HttpRequest closeOutputQuietly() throws HttpRequestException {
		try {
			return closeOutput();
		} catch (IOException e) {
			throw new HttpRequestException(e);
		}
	}

	protected HttpRequest openOutput() throws IOException {
		if (output != null)
			return this;
		getConnection().setDoOutput(true);
		final String charset = getParam(
				getConnection().getRequestProperty(HEADER_CONTENT_TYPE),
				PARAM_CHARSET);
		output = new RequestOutputStream(getConnection().getOutputStream(),
				charset, bufferSize);
		return this;
	}

	protected HttpRequest startPart() throws IOException {
		if (!multipart) {
			multipart = true;
			contentType(CONTENT_TYPE_MULTIPART).openOutput();
			output.write("--" + BOUNDARY + CRLF);
		} else
			output.write(CRLF + "--" + BOUNDARY + CRLF);
		return this;
	}

	protected HttpRequest writePartHeader(final String name,
			final String filename) throws IOException {
		return writePartHeader(name, filename, null);
	}

	protected HttpRequest writePartHeader(final String name,
			final String filename, final String contentType) throws IOException {
		final StringBuilder partBuffer = new StringBuilder();
		partBuffer.append("form-data; name=\"").append(name);
		if (filename != null)
			partBuffer.append("\"; filename=\"").append(filename);
		partBuffer.append('"');
		partHeader("Content-Disposition", partBuffer.toString());
		if (contentType != null)
			partHeader(HEADER_CONTENT_TYPE, contentType);
		return send(CRLF);
	}

	public HttpRequest part(final String name, final String part) {
		return part(name, null, part);
	}

	public HttpRequest part(final String name, final String filename,
			final String part) throws HttpRequestException {
		return part(name, filename, null, part);
	}

	public HttpRequest part(final String name, final String filename,
			final String contentType, final String part)
			throws HttpRequestException {
		try {
			startPart();
			writePartHeader(name, filename, contentType);
			output.write(part);
		} catch (IOException e) {
			throw new HttpRequestException(e);
		}
		return this;
	}

	public HttpRequest part(final String name, final Number part)
			throws HttpRequestException {
		return part(name, null, part);
	}

	public HttpRequest part(final String name, final String filename,
			final Number part) throws HttpRequestException {
		return part(name, filename, part != null ? part.toString() : null);
	}

	public HttpRequest part(final String name, final File part)
			throws HttpRequestException {
		return part(name, null, part);
	}

	public HttpRequest part(final String name, final String filename,
			final File part) throws HttpRequestException {
		return part(name, filename, null, part);
	}

	public HttpRequest part(final String name, final String filename,
			final String contentType, final File part)
			throws HttpRequestException {
		final InputStream stream;
		try {
			stream = new BufferedInputStream(new FileInputStream(part));
		} catch (IOException e) {
			throw new HttpRequestException(e);
		}
		return part(name, filename, contentType, stream);
	}

	public HttpRequest part(final String name, final InputStream part)
			throws HttpRequestException {
		return part(name, null, null, part);
	}

	public HttpRequest part(final String name, final String filename,
			final String contentType, final InputStream part)
			throws HttpRequestException {
		try {
			startPart();
			writePartHeader(name, filename, contentType);
			copy(part, output);
		} catch (IOException e) {
			throw new HttpRequestException(e);
		}
		return this;
	}

	public HttpRequest partHeader(final String name, final String value)
			throws HttpRequestException {
		return send(name).send(": ").send(value).send(CRLF);
	}

	public HttpRequest send(final File input) throws HttpRequestException {
		final InputStream stream;
		try {
			stream = new BufferedInputStream(new FileInputStream(input));
		} catch (FileNotFoundException e) {
			throw new HttpRequestException(e);
		}
		return send(stream);
	}

	public HttpRequest send(final byte[] input) throws HttpRequestException {
		return send(new ByteArrayInputStream(input));
	}

	public HttpRequest send(final InputStream input)
			throws HttpRequestException {
		try {
			openOutput();
			copy(input, output);
		} catch (IOException e) {
			throw new HttpRequestException(e);
		}
		return this;
	}

	public HttpRequest send(final Reader input) throws HttpRequestException {
		try {
			openOutput();
		} catch (IOException e) {
			throw new HttpRequestException(e);
		}
		final Writer writer = new OutputStreamWriter(output,
				output.encoder.charset());
		return new FlushOperation<HttpRequest>(writer) {

			@Override
			protected HttpRequest run() throws IOException {
				return copy(input, writer);
			}
		}.call();
	}

	public HttpRequest send(final CharSequence value)
			throws HttpRequestException {
		try {
			openOutput();
			output.write(value.toString());
		} catch (IOException e) {
			throw new HttpRequestException(e);
		}
		return this;
	}

	public String send(JSONObject obj) {
		StringBuffer sbr = new StringBuffer();
		try {
			HttpURLConnection c = getConnection();
			String charEncoding = "iso-8859-1";
			c.setDoOutput(true);
			c.setUseCaches(false);
			c.setRequestMethod(this.requestMethod);
			c.setRequestProperty("Content-type", "application/json; charset="
					+ "UTF-8");
			if (this.requestMethod != "GET" && obj != null) {
				String data = obj.toString();
				c.setDoInput(true);
				c.setRequestProperty("Content-Length",
						Integer.toString(data.length()));
				c.getOutputStream().write(data.getBytes(charEncoding));
			}
			c.connect();
			int code = c.getResponseCode();
			if (code != 200) {
				c.disconnect();
				return String.valueOf(code);
			}
			try {
				BufferedReader rd = new BufferedReader(new InputStreamReader(
						c.getInputStream()));
				String line;
				while ((line = rd.readLine()) != null) {
					sbr.append(line);
				}
				rd.close();
			} catch (FileNotFoundException e) {
			} catch (NullPointerException e) {
			} finally {
				c.disconnect();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		String result = sbr.toString();
		return result;
	}

	public OutputStreamWriter writer() throws HttpRequestException {
		try {
			openOutput();
			return new OutputStreamWriter(output, output.encoder.charset());
		} catch (IOException e) {
			throw new HttpRequestException(e);
		}
	}

	public HttpRequest form(final Map<?, ?> values) throws HttpRequestException {
		return form(values, CHARSET_UTF8);
	}

	public HttpRequest form(final Entry<?, ?> entry)
			throws HttpRequestException {
		return form(entry, CHARSET_UTF8);
	}

	public HttpRequest form(final Entry<?, ?> entry, final String charset)
			throws HttpRequestException {
		return form(entry.getKey(), entry.getValue(), charset);
	}

	public HttpRequest form(final Object name, final Object value)
			throws HttpRequestException {
		return form(name, value, CHARSET_UTF8);
	}

	public HttpRequest form(final Object name, final Object value,
			String charset) throws HttpRequestException {
		final boolean first = !form;
		if (first) {
			contentType(CONTENT_TYPE_FORM, charset);
			form = true;
		}
		charset = getValidCharset(charset);
		try {
			openOutput();
			if (!first)
				output.write('&');
			output.write(URLEncoder.encode(name.toString(), charset));
			output.write('=');
			if (value != null)
				output.write(URLEncoder.encode(value.toString(), charset));
		} catch (IOException e) {
			throw new HttpRequestException(e);
		}
		return this;
	}

	public HttpRequest form(final Map<?, ?> values, final String charset)
			throws HttpRequestException {
		if (!values.isEmpty())
			for (Entry<?, ?> entry : values.entrySet())
				form(entry, charset);
		return this;
	}

	public HttpRequest trustAllCerts() throws HttpRequestException {
		final HttpURLConnection connection = getConnection();
		if (connection instanceof HttpsURLConnection)
			((HttpsURLConnection) connection)
					.setSSLSocketFactory(getTrustedFactory());
		return this;
	}

	public HttpRequest trustAllHosts() {
		final HttpURLConnection connection = getConnection();
		if (connection instanceof HttpsURLConnection)
			((HttpsURLConnection) connection)
					.setHostnameVerifier(getTrustedVerifier());
		return this;
	}

	public URL url() {
		return getConnection().getURL();
	}

	public String method() {
		return getConnection().getRequestMethod();
	}

	public HttpRequest useProxy(final String proxyHost, final int proxyPort) {
		if (connection != null)
			throw new IllegalStateException(
					"The connection has already been created. This method must be called before reading or writing to the request.");

		this.httpProxyHost = proxyHost;
		this.httpProxyPort = proxyPort;
		return this;
	}

	public HttpRequest followRedirects(final boolean followRedirects) {
		getConnection();
		HttpURLConnection.setFollowRedirects(followRedirects);
		return this;
	}

	public String cookies() {
		return header(HEADER_SET_COOKIE);
	}

	public HashMap<String, String> cookieMap() {
		HashMap<String, String> cookie = new HashMap<String, String>();
		String res = cookies();
		if (res != null) {
			StringTokenizer st = new StringTokenizer(res, ";");
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				String name = token.substring(0, token.indexOf("=") + 1).trim();
				String value = token.substring(token.indexOf("=") + 1,
						token.length()).trim();
				cookie.put(name, value);
			}
		}
		return cookie;
	}

	public CookieContainer addCookies(String url,
			CookieContainer cookieContainer) {
		String cookiestring = "";
		List<Cookie> cookies = cookieContainer.getCookieList(url);
		if (cookies.size() > 1) {
			for (int i = 0; i < (cookies.size() - 1); i++) {
				cookiestring += cookies.get(i).getName() + "="
						+ cookies.get(i).getValue() + "; ";
			}
		}
		if (cookies.size() > 0) {
			int i = cookies.size() - 1;
			cookiestring += cookies.get(i).getName() + "="
					+ cookies.get(i).getValue();
		}
		connection.setRequestProperty(HEADER_COOKIE, cookiestring);
		return cookieContainer;
	}

	public String getJsessionId() {
		String cookies = cookies();
		if (cookies != null) {
			final String jsessionIdPattern = "JSESSIONID=";
			final int jsessionIdIndex = cookies.indexOf(jsessionIdPattern);
			if (jsessionIdIndex > -1) {
				int jsessionIdEndIndex = cookies.indexOf(";",
						jsessionIdIndex + 1);
				if (jsessionIdEndIndex < 0) {
					jsessionIdEndIndex = cookies.length();
				}
				return cookies.substring(
						jsessionIdIndex + jsessionIdPattern.length(),
						jsessionIdEndIndex);
			}
		}
		return null;
	}

}
