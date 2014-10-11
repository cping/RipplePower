package org.address;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class HttpUtil {
	public HttpUtil() {
	}

	private static boolean findPatternInStream(byte[] pattern, InputStream is)
			throws IOException {
		int patternOffset = 0;
		int len = pattern.length;
		int b = is.read();
		for (; b != -1;) {
			if (pattern[patternOffset] == ((byte) b)) {
				patternOffset++;
				if (patternOffset == len) {
					return true;
				}
			} else {
				patternOffset = 0;
			}
			b = is.read();
		}

		return false;
	}

	public static JSONArray sendHttp(String URL, JSONObject request)
			throws IOException {
		byte[] params = request.toString().getBytes(Charset.forName("UTF-8"));
		HttpURLConnection urlConnection = (HttpURLConnection) new URL(URL)
				.openConnection();
		urlConnection.setConnectTimeout(10000);
		urlConnection.setReadTimeout(10000);
		urlConnection.setRequestMethod("POST");
		urlConnection.setRequestProperty("Content-Type", "application/json"
				+ "; charset=utf-8");
		urlConnection.setRequestProperty("Content-Length", "" + params.length);
		urlConnection.setRequestProperty("Content-Language", "en-US");
		urlConnection.setUseCaches(false);
		urlConnection.setDoInput(true);
		urlConnection.setDoOutput(true);

		DataOutputStream writer = new DataOutputStream(
				urlConnection.getOutputStream());
		writer.write(params);
		writer.flush();
		writer.close();

		InputStream inputStream = urlConnection.getInputStream();
		/*
		 * ByteArrayOutputStream byteArrayOutputStream = new
		 * ByteArrayOutputStream(); byte[] buffer = new byte[1024]; int len;
		 * while ((len = inputStream.read(buffer)) > -1) {
		 * byteArrayOutputStream.write(buffer, 0, len); }
		 * byteArrayOutputStream.flush();
		 */
		JSONArray o = new JSONArray(new JSONTokener(inputStream));
		inputStream.close();
		return o;
	}

	public static boolean sendHttpPost(String url, String sData, int timeout,
			byte[] find) throws IOException {
		OutputStream ostream = null;
		InputStream istream = null;
		try {
			byte[] data = sData.getBytes();
			URL theUrl = new URL(url);
			URLConnection conn = theUrl.openConnection();
			conn.setDoOutput(true);
			ostream = conn.getOutputStream();
			ostream.write(data);
			ostream.flush();
			istream = conn.getInputStream();
			return findPatternInStream(find, istream);
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				if (istream != null) {
					istream.close();
				}
			} catch (Exception e) {
			}
			try {
				if (ostream != null) {
					ostream.close();
				}
			} catch (Exception e) {
			}
		}
	}

}
