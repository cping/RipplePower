package org.ripple.power.server.socket;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

class XHRTransport implements IOTransport {

	public static final String TRANSPORT_NAME = "xhr-polling";

	private IOConnection connection;

	private URL url;

	ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<String>();

	PollThread pollThread = null;

	private boolean connect;

	private boolean blocked;

	HttpURLConnection urlConnection;

	private class PollThread extends Thread {

		private static final String CHARSET = "UTF-8";

		public PollThread() {
			super(TRANSPORT_NAME);
		}

		@Override
		public void run() {
			connection.transportConnected();
			while (isConnect()) {
				try {
					String line;
					URL url = new URL(XHRTransport.this.url.toString() + "?t="
							+ System.currentTimeMillis());
					urlConnection = (HttpURLConnection) url.openConnection();
					SSLContext context = IOConnection.getSslContext();
					if(urlConnection instanceof HttpsURLConnection && context != null) {
						((HttpsURLConnection)urlConnection).setSSLSocketFactory(context.getSocketFactory());
					}
					if (!queue.isEmpty()) {
						urlConnection.setDoOutput(true);
						OutputStream output = urlConnection.getOutputStream();
						if (queue.size() == 1) {
							line = queue.poll();
							output.write(line.getBytes(CHARSET));
						} else {
							Iterator<String> iter = queue.iterator();
							while (iter.hasNext()) {
								String junk = iter.next();
								line = IOConnection.FRAME_DELIMITER + junk.length()
										+ IOConnection.FRAME_DELIMITER + junk;
								output.write(line.getBytes(CHARSET));
								iter.remove();
							}
						}
						output.close();
						InputStream input = urlConnection.getInputStream();
						byte[] buffer = new byte[1024];
						while(input.read(buffer) > 0) {
						}
						input.close();
					} else {
						setBlocked(true);
						InputStream plainInput = urlConnection.getInputStream();
						BufferedReader input = new BufferedReader(
								new InputStreamReader(plainInput, CHARSET));
						while ((line = input.readLine()) != null) {
							if (connection != null)
								connection.transportData(line);
						}
						setBlocked(false);
					}

				} catch (IOException e) {
					if (connection != null && interrupted() == false) {
						connection.transportError(e);
						return;
					}
				}
				try {
					sleep(100);
				} catch (InterruptedException e) {
				}
			}
			connection.transportDisconnected();
		}
	}


	public static IOTransport create(URL url, IOConnection connection) {
		try {
			URL xhrUrl = new URL(url.toString() + IOConnection.SOCKET_IO_1
					+ TRANSPORT_NAME + "/" + connection.getSessionId());
			return new XHRTransport(xhrUrl, connection);
		} catch (MalformedURLException e) {
			throw new RuntimeException(
					"Malformed Internal url. This should never happen. Please report a bug.",
					e);
		}

	}

	public XHRTransport(URL url, IOConnection connection) {
		this.connection = connection;
		this.url = url;
	}

	@Override
	public void connect() {
		this.setConnect(true);
		pollThread = new PollThread();
		pollThread.start();
	}

	@Override
	public void disconnect() {
		this.setConnect(false);
		pollThread.interrupt();
	}

	@Override
	public void send(String text) throws IOException {
		sendBulk(new String[] { text });
	}

	@Override
	public boolean canSendBulk() {
		return true;
	}

	@Override
	public void sendBulk(String[] texts) throws IOException {
		queue.addAll(Arrays.asList(texts));
		if (isBlocked()) {
			pollThread.interrupt();
			urlConnection.disconnect();
		}
	}

	@Override
	public void invalidate() {
		this.connection = null;
	}

	private synchronized boolean isConnect() {
		return connect;
	}

	private synchronized void setConnect(boolean connect) {
		this.connect = connect;
	}

	private synchronized boolean isBlocked() {
		return blocked;
	}

	private synchronized void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}

	@Override
	public String getName() {
		return TRANSPORT_NAME;
	}
}
