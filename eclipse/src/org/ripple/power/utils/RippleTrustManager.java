package org.ripple.power.utils;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class RippleTrustManager implements X509TrustManager {

	SSLContext mSslContext = null;
	CertificateList cerList = new CertificateList();

	public RippleTrustManager() {
	}

	public Certificate getCertificateFromInStream(InputStream cerIsputStream) {
		Certificate ca = null;
		InputStream caInput = null;

		try {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			caInput = new BufferedInputStream(cerIsputStream);
			ca = cf.generateCertificate(caInput);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				caInput.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ca;
	}

	TrustManager[] getTrustManager() {
		try {
			KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			keyStore.load(null, null);

			for (int i = 0; i < cerList.getCertificateCount(); i++) {
				Certificate c = getCertificateFromInStream(cerList
						.getCertificateInputStream(i));
				keyStore.setCertificateEntry(cerList.getCertificateName(i), c);
			}
			String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
			TrustManagerFactory tmf = TrustManagerFactory
					.getInstance(tmfAlgorithm);
			tmf.init(keyStore);
			return tmf.getTrustManagers();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public SSLContext getSSLContent() {
		if (mSslContext == null) {
			try {
				mSslContext = SSLContext.getInstance("TLSv1.2", "SunJSSE");
				mSslContext.init(null, getTrustManager(), new SecureRandom());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return mSslContext;
	}

	public void setCertificate(String cerName, InputStream cerIs) {
		cerList.addCertificate(cerName, cerIs);
	}

	private class CertificateList {
		private ArrayList<String> cerNameList = new ArrayList<String>();
		private ArrayList<InputStream> cerInputStream = new ArrayList<InputStream>();
		private int count = 0;

		public void addCertificate(String cerName, InputStream cerIs) {
			cerNameList.add(count, cerName);
			cerInputStream.add(count, cerIs);
			count++;
		}

		public String getCertificateName(int index) {
			return cerNameList.get(index);
		}

		public InputStream getCertificateInputStream(int index) {
			return cerInputStream.get(index);
		}

		public int getCertificateCount() {
			return count;
		}
	}

	final static TrustManager[] emptyTrustAllCerts = new TrustManager[] { new X509TrustManager() {

		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[0];
		}

		public void checkClientTrusted(X509Certificate[] chain, String authType) {

		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) {

		}
	} };

	static class LocalStoreX509TrustManager implements X509TrustManager {

		private X509TrustManager trustManager;

		public LocalStoreX509TrustManager() throws KeyStoreException {
			this(KeyStore.getInstance(KeyStore.getDefaultType()));
		}

		public LocalStoreX509TrustManager(KeyStore localTrustStore) {
			try {
				TrustManagerFactory tmf = TrustManagerFactory
						.getInstance(TrustManagerFactory.getDefaultAlgorithm());
				tmf.init(localTrustStore);

				trustManager = findX509TrustManager(tmf);
				if (trustManager == null) {
					throw new IllegalStateException(
							"Couldn't find X509TrustManager");
				}
			} catch (GeneralSecurityException e) {
				throw new RuntimeException(e);
			}

		}

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
			trustManager.checkClientTrusted(chain, authType);
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
			trustManager.checkServerTrusted(chain, authType);
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return trustManager.getAcceptedIssuers();
		}
	}

	static X509TrustManager findX509TrustManager(TrustManagerFactory tmf) {
		TrustManager tms[] = tmf.getTrustManagers();
		for (int i = 0; i < tms.length; i++) {
			if (tms[i] instanceof X509TrustManager) {
				return (X509TrustManager) tms[i];
			}
		}

		return null;
	}

	private X509TrustManager defaultTrustManager;
	private X509TrustManager localTrustManager;

	private X509Certificate[] acceptedIssuers;

	public RippleTrustManager(KeyStore localKeyStore) {
		try {
			TrustManagerFactory tmf = TrustManagerFactory
					.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init((KeyStore) null);

			defaultTrustManager = findX509TrustManager(tmf);
			if (defaultTrustManager == null) {
				throw new IllegalStateException("not found X509TrustManager");
			}

			localTrustManager = new LocalStoreX509TrustManager(localKeyStore);

			ArrayList<X509Certificate> allIssuers = new ArrayList<X509Certificate>();
			for (X509Certificate cert : defaultTrustManager
					.getAcceptedIssuers()) {
				allIssuers.add(cert);
			}
			for (X509Certificate cert : localTrustManager.getAcceptedIssuers()) {
				allIssuers.add(cert);
			}
			acceptedIssuers = allIssuers.toArray(new X509Certificate[allIssuers
					.size()]);
		} catch (GeneralSecurityException e) {
			throw new RuntimeException(e);
		}

	}

	public void checkClientTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
		try {
			defaultTrustManager.checkClientTrusted(chain, authType);
		} catch (CertificateException ce) {
			localTrustManager.checkClientTrusted(chain, authType);
		}
	}

	public void checkServerTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
		try {
			defaultTrustManager.checkServerTrusted(chain, authType);
		} catch (CertificateException ce) {
			localTrustManager.checkServerTrusted(chain, authType);
		}
	}

	public X509Certificate[] getAcceptedIssuers() {
		return acceptedIssuers;
	}

}
