package org.ripple.power.chart.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.Future;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.ripple.power.config.LSystem;
import org.ripple.power.password.PasswordEasy;
import org.ripple.power.password.PasswordGenerator;
import org.ripple.power.txns.RippleHistoryAPI;
import org.ripple.power.txns.IssuedCurrency;
import org.ripple.power.txns.NameFind;
import org.ripple.power.txns.RippleChartsAPI;
import org.ripple.power.utils.FileUtils;
import org.ripple.power.utils.HttpRequest;

import com.ripple.client.Client;
import com.ripple.client.transport.impl.JavaWebSocketTransportImpl;

public class TestNames {

	public static void main(String[] args) throws Exception {

		// History h=new History("rKiCet8SdvWxPXnAgYarFUXMh1zCPz432Y");
		// System.out.println(h.getUrl());
		System.out.println(NameFind.getAddress("ripplefox"));
		// System.out.println(NameFind.getAddress("baidutest"));
		// HttpRequest
		// req=HttpRequest.get("https://id.staging.ripple.com/v1/user/testUser");
		// System.out.println(req.cookies());
		// System.out.println(req.ok());
		/*
		 * KeyStore trustStore =
		 * KeyStore.getInstance(KeyStore.getDefaultType()); FileInputStream
		 * instream = new FileInputStream(new File("my.keystore")); try {
		 * trustStore.load(instream, "nopassword".toCharArray()); } finally {
		 * instream.close(); } // Trust own CA and all self-signed certs
		 * SSLContext sslcontext = SSLContexts.custom()
		 * .loadTrustMaterial(trustStore, new TrustSelfSignedStrategy())
		 * .build(); // Allow TLSv1 protocol only SSLConnectionSocketFactory
		 * sslsf = new SSLConnectionSocketFactory( sslcontext, new String[] {
		 * "TLSv1" }, null,
		 * SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
		 */

	}

}
