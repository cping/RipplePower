package org.ripple.power.utils;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.Header;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.ripple.power.config.LSystem;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings({ "deprecation", "unused" })
public class HttpsUtils {

	public static class ResponseResult {
		private int statusCode = -1;
		private String result;

		private String uri;
		private String errorCode;
		private boolean error;

		public int getStatusCode() {
			return statusCode;
		}

		public void setStatusCode(int statusCode) {
			this.statusCode = statusCode;
		}

		public boolean ok() {
			return statusCode == HttpURLConnection.HTTP_OK;
		}

		public String getResult() {
			return result;
		}

		public void setResult(String result) {
			this.result = result;
		}

		public String getUri() {
			return uri;
		}

		public void setUri(String uri) {
			this.uri = uri;
		}

		public String getErrorCode() {
			return errorCode;
		}

		public void setErrorCode(String errorCode) {
			this.errorCode = errorCode;
		}

		public boolean isError() {
			return error;
		}

		public void setError(boolean error) {
			this.error = error;
		}
	}

	private static void abortConnection(final HttpRequestBase hrb, final HttpClient httpclient) {
		if (hrb != null) {
			try {
				hrb.abort();
			} catch (Exception e) {
			}
		}
		if (httpclient != null) {
			try {
				httpclient.getConnectionManager().shutdown();
			} catch (Exception e) {
			}
		}
	}

	private static void abortConnection(final HttpRequestBase hrb) {
		if (hrb != null) {
			try {
				hrb.abort();
			} catch (Exception e) {
			}
		}
	}

	public static ResponseResult getSSL(String url, String paramsCharset, String resultCharset, HttpClient httpClient) {
		if (url == null || "".equals(url)) {
			return null;
		}
		ResponseResult responseObject = null;
		String responseStr = null;
		HttpGet hg = null;
		try {
			hg = new HttpGet(url);
			HttpResponse response = httpClient.execute(hg);
			if (response != null) {
				responseObject = new ResponseResult();
				responseObject.setUri(hg.getURI().toString());
				responseObject.setStatusCode(response.getStatusLine().getStatusCode());
				if (response.getStatusLine().getStatusCode() == 200) {
					if (resultCharset == null || "".equals(resultCharset)) {
						responseStr = EntityUtils.toString(response.getEntity(), LSystem.encoding);
					} else {
						responseStr = EntityUtils.toString(response.getEntity(), resultCharset);
					}
				} else {
					responseStr = null;
				}
				responseObject.setResult(responseStr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			abortConnection(hg);
		}
		return responseObject;
	}

	public static ResponseResult getSSL(String url) throws Exception {
		return getSSL(url, LSystem.encoding, LSystem.encoding);
	}

	public static ResponseResult getSSL(String url, String paramsCharset, String resultCharset) throws Exception {
		if (url == null || "".equals(url)) {
			return null;
		}
		ResponseResult responseObject = null;
		String responseStr = null;
		HttpClient httpClient = null;
		HttpGet hg = null;
		try {
			httpClient = getNewHttpClient();
			hg = new HttpGet(url);
			HttpResponse response = httpClient.execute(hg);
			if (response != null) {
				responseObject = new ResponseResult();
				responseObject.setUri(hg.getURI().toString());
				responseObject.setStatusCode(response.getStatusLine().getStatusCode());
				if (response.getStatusLine().getStatusCode() == 200) {
					if (resultCharset == null || "".equals(resultCharset)) {
						responseStr = EntityUtils.toString(response.getEntity(), LSystem.encoding);
					} else {
						responseStr = EntityUtils.toString(response.getEntity(), resultCharset);
					}
				} else {
					responseStr = null;
				}
				responseObject.setResult(responseStr);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			abortConnection(hg, httpClient);
		}
		return responseObject;
	}

	public static ResponseResult getSSL(String url, Map<String, String> params) {
		return postSSL(url, params, LSystem.encoding, LSystem.encoding);
	}

	public static ResponseResult postSSL(String url, Map<String, String> params, String paramsCharset,
			String resultCharset) {
		if (url == null || "".equals(url)) {
			return null;
		}
		ResponseResult responseObject = null;
		String responseStr = null;
		HttpClient httpClient = null;
		HttpPost hp = null;
		try {
			httpClient = getNewHttpClient();
			hp = new HttpPost(url);
			if (params != null) {
				List<NameValuePair> formParams = new ArrayList<NameValuePair>();
				for (Map.Entry<String, String> entry : params.entrySet()) {
					formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
				hp.setEntity(new UrlEncodedFormEntity(formParams, LSystem.encoding));
			}
			HttpResponse response = httpClient.execute(hp);
			if (response != null) {
				responseObject = new ResponseResult();
				responseObject.setUri(hp.getURI().toString());
				responseObject.setStatusCode(response.getStatusLine().getStatusCode());
				if (response.getStatusLine().getStatusCode() == 200) {
					if (resultCharset == null || "".equals(resultCharset)) {
						responseStr = EntityUtils.toString(response.getEntity(), LSystem.encoding);
					} else {
						responseStr = EntityUtils.toString(response.getEntity(), resultCharset);
					}
				} else {
					responseStr = null;
				}
				responseObject.setResult(responseStr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			abortConnection(hp, httpClient);
		}
		return responseObject;
	}

	public static ResponseResult httpByPostXMLObjSSL(String url, Map<String, String> params, String paramsCharset,
			String resultCharset) {
		if (url == null || "".equals(url)) {
			return null;
		}
		ResponseResult responseObject = null;
		String responseStr = null;
		HttpClient httpClient = null;
		HttpPost hp = null;
		try {
			httpClient = getNewHttpClient();
			hp = new HttpPost(url);
			if (params != null && !params.isEmpty()) {
				String content = params.get("content");
				if (!StringUtils.isEmpty(content)) {
					StringEntity s = new StringEntity(content.toString(), LSystem.encoding);
					s.setContentEncoding("HTTP.UTF_8");
					s.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/xml"));
					hp.setEntity(s);
				}
			}
			HttpResponse response = httpClient.execute(hp);
			if (response != null) {
				responseObject = new ResponseResult();
				responseObject.setUri(hp.getURI().toString());
				responseObject.setStatusCode(response.getStatusLine().getStatusCode());
				if (response.getStatusLine().getStatusCode() == 200) {
					if (resultCharset == null || "".equals(resultCharset)) {
						responseStr = EntityUtils.toString(response.getEntity(), LSystem.encoding);
					} else {
						responseStr = EntityUtils.toString(response.getEntity(), resultCharset);
					}
				} else {
					responseStr = null;
				}
				responseObject.setResult(responseStr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			abortConnection(hp, httpClient);
		}
		return responseObject;
	}

	public static ResponseResult httpByPostJSONObjSSL(String url, Map<String, String> params, String paramsCharset,
			String resultCharset) {
		if (url == null || "".equals(url)) {
			return null;
		}
		ResponseResult responseObject = null;
		String responseStr = null;
		HttpClient httpClient = null;
		HttpPost hp = null;
		try {
			httpClient = getNewHttpClient();
			hp = new HttpPost(url);
			if (params != null && !params.isEmpty()) {
				String content = params.get("content");
				if (!StringUtils.isEmpty(content)) {
					StringEntity s = new StringEntity(content.toString(), LSystem.encoding);
					s.setContentEncoding("HTTP.UTF_8");
					s.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
					hp.setEntity(s);
				}
			}
			HttpResponse response = httpClient.execute(hp);
			if (response != null) {
				responseObject = new ResponseResult();
				responseObject.setUri(hp.getURI().toString());
				responseObject.setStatusCode(response.getStatusLine().getStatusCode());
				if (response.getStatusLine().getStatusCode() == 200) {
					if (resultCharset == null || "".equals(resultCharset)) {
						responseStr = EntityUtils.toString(response.getEntity(), LSystem.encoding);
					} else {
						responseStr = EntityUtils.toString(response.getEntity(), resultCharset);
					}
				} else {
					responseStr = null;
				}
				responseObject.setResult(responseStr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			abortConnection(hp, httpClient);
		}
		return responseObject;
	}

	public static ResponseResult postSSL(String url, Map<String, String> params, String paramsCharset,
			String resultCharset, HttpClient httpClient) {
		if (url == null || "".equals(url)) {
			return null;
		}
		ResponseResult responseObject = null;
		String responseStr = null;
		HttpPost hp = null;
		try {
			hp = new HttpPost(url);
			List<NameValuePair> formParams = new ArrayList<NameValuePair>();
			for (Map.Entry<String, String> entry : params.entrySet()) {
				formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			hp.setEntity(new UrlEncodedFormEntity(formParams, LSystem.encoding));
			HttpResponse response = httpClient.execute(hp);
			if (response != null) {
				responseObject = new ResponseResult();
				responseObject.setUri(hp.getURI().toString());
				responseObject.setStatusCode(response.getStatusLine().getStatusCode());
				if (response.getStatusLine().getStatusCode() == 200) {
					if (resultCharset == null || "".equals(resultCharset)) {
						responseStr = EntityUtils.toString(response.getEntity(), LSystem.encoding);
					} else {
						responseStr = EntityUtils.toString(response.getEntity(), resultCharset);
					}
				} else {
					responseStr = null;
				}
				responseObject.setResult(responseStr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			abortConnection(hp);
		}
		return responseObject;
	}

	public static class SSLSocketFactoryEx extends SSLSocketFactory implements ConnectionSocketFactory {
		public Socket createSocket(final HttpContext context) throws IOException {
			InetSocketAddress socksaddr = (InetSocketAddress) context.getAttribute("socks.address");
			Proxy proxy = new Proxy(Proxy.Type.SOCKS, socksaddr);
			return new Socket(proxy);
		}

		public Socket connectSocket(final int connectTimeout, final Socket socket, final HttpHost host,
				final InetSocketAddress remoteAddress, final InetSocketAddress localAddress, final HttpContext context)
				throws IOException, ConnectTimeoutException {
			Socket sock;
			if (socket != null) {
				sock = socket;
			} else {
				sock = createSocket(context);
			}
			if (localAddress != null) {
				sock.bind(localAddress);
			}
			try {
				sock.connect(remoteAddress, connectTimeout);
			} catch (SocketTimeoutException ex) {
				throw new ConnectTimeoutException(ex, host, remoteAddress.getAddress());
			}
			return sock;
		}

		SSLContext sslContext = SSLContext.getInstance("TLSv1.2");

		public SSLSocketFactoryEx(KeyStore truststore)
				throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
			super(truststore);
			TrustManager tm = new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
						throws java.security.cert.CertificateException {
				}

				public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
						throws java.security.cert.CertificateException {
				}
			};
			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
				throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}

	public static HttpClient getNewHttpClient() {
		try {

			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);
			SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			HttpParams params = new BasicHttpParams();

			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
			HttpProtocolParams.setUserAgent(params, HttpHeader.getUA());
			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
			DefaultHttpClient httpClient = new DefaultHttpClient(ccm, params);

			httpClient.setCookieStore(new BasicCookieStore());

			httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 25000);
			httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 25000);
			return httpClient;
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}

	private static class LocalConnectionSocket implements ConnectionSocketFactory {
		public Socket createSocket(final HttpContext context) throws IOException {
			InetSocketAddress socksaddr = (InetSocketAddress) context.getAttribute("socks.address");
			Proxy proxy = new Proxy(Proxy.Type.SOCKS, socksaddr);
			return new Socket(proxy);
		}

		public Socket connectSocket(final int connectTimeout, final Socket socket, final HttpHost host,
				final InetSocketAddress remoteAddress, final InetSocketAddress localAddress, final HttpContext context)
				throws IOException, ConnectTimeoutException {
			Socket sock;
			if (socket != null) {
				sock = socket;
			} else {
				sock = createSocket(context);
			}
			if (localAddress != null) {
				sock.bind(localAddress);
			}
			try {
				sock.connect(remoteAddress, connectTimeout);
			} catch (SocketTimeoutException ex) {
				throw new ConnectTimeoutException(ex, host, remoteAddress.getAddress());
			}
			return sock;
		}
	}
}
