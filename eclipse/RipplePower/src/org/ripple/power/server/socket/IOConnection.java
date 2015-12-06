package org.ripple.power.server.socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

class IOConnection implements IOCallback {

	static final Logger logger = Logger.getLogger("io.socket");

	public static final String FRAME_DELIMITER = "\ufffd";

	private static final int STATE_INIT = 0;

	private static final int STATE_HANDSHAKE = 1;

	private static final int STATE_CONNECTING = 2;

	private static final int STATE_READY = 3;

	private static final int STATE_INTERRUPTED = 4;

	private static final int STATE_INVALID = 6;

	private int state = STATE_INIT;

	public static final String SOCKET_IO_1 = "/socket.io/1/";

	private static SSLContext sslContext = null;

	private static HashMap<String, List<IOConnection>> connections = new HashMap<String, List<IOConnection>>();

	private URL url;

	private IOTransport transport;

	private int connectTimeout = 10000;

	private String sessionId;

	private long heartbeatTimeout;

	private long closingTimeout;

	private List<String> protocols;

	private ConcurrentLinkedQueue<String> outputBuffer = new ConcurrentLinkedQueue<String>();

	private HashMap<String, SocketIO> sockets = new HashMap<String, SocketIO>();

	private Properties headers;

	private SocketIO firstSocket = null;

	final private Timer backgroundTimer = new Timer("backgroundTimer");

	private String urlStr;

	private Exception lastException;

	private int nextId = 1;

	HashMap<Integer, IOAcknowledge> acknowledge = new HashMap<Integer, IOAcknowledge>();

	private boolean keepAliveInQueue;

	private HearbeatTimeoutTask heartbeatTimeoutTask;

	private class HearbeatTimeoutTask extends TimerTask {

		@Override
		public void run() {
			error(new SocketIOException(
					"Timeout Error. No heartbeat from server within life time of the socket. closing.",
					lastException));
		}
	}

	private ReconnectTask reconnectTask = null;

	private class ReconnectTask extends TimerTask {

		@Override
		public void run() {
			connectTransport();
			if (!keepAliveInQueue) {
				sendPlain("2::");
				keepAliveInQueue = true;
			}
		}
	}

	private class ConnectThread extends Thread {

		public ConnectThread() {
			super("ConnectThread");
		}

		@Override
		public void run() {
			if (IOConnection.this.getState() == STATE_INIT) {
				handshake();
			}
			connectTransport();
		}

	};

	public static void setSslContext(SSLContext sslContext) {
		IOConnection.sslContext = sslContext;
	}

	public static SSLContext getSslContext() {
		return sslContext;
	}

	static public IOConnection register(String origin, SocketIO socket) {
		List<IOConnection> list = connections.get(origin);
		if (list == null) {
			list = new LinkedList<IOConnection>();
			connections.put(origin, list);
		} else {
			synchronized (list) {
				for (IOConnection connection : list) {
					if (connection.register(socket))
						return connection;
				}
			}
		}

		IOConnection connection = new IOConnection(origin, socket);
		list.add(connection);
		return connection;
	}

	public synchronized boolean register(SocketIO socket) {
		String namespace = socket.getNamespace();
		if (sockets.containsKey(namespace))
			return false;
		sockets.put(namespace, socket);
		socket.setHeaders(headers);
		IOMessage connect = new IOMessage(IOMessage.TYPE_CONNECT,
				socket.getNamespace(), "");
		sendPlain(connect.toString());
		return true;
	}

	public synchronized void unregister(SocketIO socket) {
		sendPlain("0::" + socket.getNamespace());
		sockets.remove(socket.getNamespace());
		socket.getCallback().onDisconnect();

		if (sockets.size() == 0) {
			cleanup();
		}
	}

	private void handshake() {
		URL url;
		String response;
		URLConnection connection;
		try {
			setState(STATE_HANDSHAKE);
			url = new URL(IOConnection.this.url.toString() + SOCKET_IO_1);
			connection = url.openConnection();
			if (connection instanceof HttpsURLConnection) {
				((HttpsURLConnection) connection)
						.setSSLSocketFactory(sslContext.getSocketFactory());
			}
			connection.setConnectTimeout(connectTimeout);
			connection.setReadTimeout(connectTimeout);

			for (Entry<Object, Object> entry : headers.entrySet()) {
				connection.setRequestProperty((String) entry.getKey(),
						(String) entry.getValue());
			}

			InputStream stream = connection.getInputStream();
			Scanner in = new Scanner(stream);
			response = in.nextLine();
			String[] data = response.split(":");
			sessionId = data[0];
			heartbeatTimeout = Long.parseLong(data[1]) * 1000;
			closingTimeout = Long.parseLong(data[2]) * 1000;
			protocols = Arrays.asList(data[3].split(","));
			in.close();
		} catch (Exception e) {
			error(new SocketIOException("Error while handshaking", e));
		}
	}

	private synchronized void connectTransport() {
		if (getState() == STATE_INVALID)
			return;
		setState(STATE_CONNECTING);
		if (protocols.contains(WebsocketTransport.TRANSPORT_NAME))
			transport = WebsocketTransport.create(url, this);
		else if (protocols.contains(XHRTransport.TRANSPORT_NAME))
			transport = XHRTransport.create(url, this);
		else {
			error(new SocketIOException(
					"Server supports no available transports. You should reconfigure the server to support a available transport"));
			return;
		}
		transport.connect();
	}

	private IOAcknowledge remoteAcknowledge(IOMessage message) {
		String _id = message.getId();
		if (_id.equals(""))
			return null;
		else if (_id.endsWith("+") == false)
			_id = _id + "+";
		final String id = _id;
		final String endPoint = message.getEndpoint();
		return new IOAcknowledge() {
			@Override
			public void ack(Object... args) {
				JSONArray array = new JSONArray();
				for (Object o : args) {
					try {
						array.put(o == null ? JSONObject.NULL : o);
					} catch (Exception e) {
						error(new SocketIOException(
								"You can only put values in IOAcknowledge.ack() which can be handled by JSONArray.put()",
								e));
					}
				}
				IOMessage ackMsg = new IOMessage(IOMessage.TYPE_ACK, endPoint,
						id + array.toString());
				sendPlain(ackMsg.toString());
			}
		};
	}

	private void synthesizeAck(IOMessage message, IOAcknowledge ack) {
		if (ack != null) {
			int id = nextId++;
			acknowledge.put(id, ack);
			message.setId(id + "+");
		}
	}

	private IOConnection(String url, SocketIO socket) {
		try {
			this.url = new URL(url);
			this.urlStr = url;
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		firstSocket = socket;
		headers = socket.getHeaders();
		sockets.put(socket.getNamespace(), socket);
		new ConnectThread().start();
	}

	private synchronized void cleanup() {
		setState(STATE_INVALID);
		if (transport != null)
			transport.disconnect();
		sockets.clear();
		synchronized (connections) {
			List<IOConnection> con = connections.get(urlStr);
			if (con != null && con.size() > 1)
				con.remove(this);
			else
				connections.remove(urlStr);
		}
		logger.info("Cleanup");
		backgroundTimer.cancel();
	}

	private void error(SocketIOException e) {
		for (SocketIO socket : sockets.values()) {
			socket.getCallback().onError(e);
		}
		cleanup();
	}

	private synchronized void sendPlain(String text) {
		if (getState() == STATE_READY)
			try {
				logger.info("> " + text);
				transport.send(text);
			} catch (Exception e) {
				logger.info("IOEx: saving");
				outputBuffer.add(text);
			}
		else {
			outputBuffer.add(text);
		}
	}

	private void invalidateTransport() {
		if (transport != null)
			transport.invalidate();
		transport = null;
	}

	private synchronized void resetTimeout() {
		if (heartbeatTimeoutTask != null) {
			heartbeatTimeoutTask.cancel();
		}
		if (getState() != STATE_INVALID) {
			heartbeatTimeoutTask = new HearbeatTimeoutTask();
			backgroundTimer.schedule(heartbeatTimeoutTask, closingTimeout
					+ heartbeatTimeout);
		}
	}

	private IOCallback findCallback(IOMessage message) throws SocketIOException {
		if ("".equals(message.getEndpoint()))
			return this;
		SocketIO socket = sockets.get(message.getEndpoint());
		if (socket == null) {
			throw new SocketIOException("Cannot find socket for '"
					+ message.getEndpoint() + "'");
		}
		return socket.getCallback();
	}

	public synchronized void transportConnected() {
		setState(STATE_READY);
		if (reconnectTask != null) {
			reconnectTask.cancel();
			reconnectTask = null;
		}
		resetTimeout();
		if (transport.canSendBulk()) {
			ConcurrentLinkedQueue<String> outputBuffer = this.outputBuffer;
			this.outputBuffer = new ConcurrentLinkedQueue<String>();
			try {

				String[] texts = outputBuffer.toArray(new String[outputBuffer
						.size()]);
				logger.info("Bulk start:");
				for (String text : texts) {
					logger.info("> " + text);
				}
				logger.info("Bulk end");

				transport.sendBulk(texts);
			} catch (IOException e) {
				this.outputBuffer = outputBuffer;
			}
		} else {
			String text;
			while ((text = outputBuffer.poll()) != null)
				sendPlain(text);
		}
		this.keepAliveInQueue = false;
	}

	public void transportDisconnected() {
		this.lastException = null;
		setState(STATE_INTERRUPTED);
		reconnect();
	}

	public void transportError(Exception error) {
		this.lastException = error;
		setState(STATE_INTERRUPTED);
		reconnect();
	}

	public void transportData(String text) {
		if (!text.startsWith(FRAME_DELIMITER)) {
			transportMessage(text);
			return;
		}

		Iterator<String> fragments = Arrays.asList(text.split(FRAME_DELIMITER))
				.listIterator(1);
		while (fragments.hasNext()) {
			int length = Integer.parseInt(fragments.next());
			String string = (String) fragments.next();

			if (length != string.length()) {
				error(new SocketIOException("Garbage from server: " + text));
				return;
			}

			transportMessage(string);
		}
	}

	public void transportMessage(String text) {
		logger.info("< " + text);
		IOMessage message;
		try {
			message = new IOMessage(text);
		} catch (Exception e) {
			error(new SocketIOException("Garbage from server: " + text, e));
			return;
		}
		resetTimeout();
		switch (message.getType()) {
		case IOMessage.TYPE_DISCONNECT:
			try {
				findCallback(message).onDisconnect();
			} catch (Exception e) {
				error(new SocketIOException(
						"Exception was thrown in onDisconnect()", e));
			}
			break;
		case IOMessage.TYPE_CONNECT:
			try {
				if (firstSocket != null && "".equals(message.getEndpoint())) {
					if (firstSocket.getNamespace().equals("")) {
						firstSocket.getCallback().onConnect();
					} else {
						IOMessage connect = new IOMessage(
								IOMessage.TYPE_CONNECT,
								firstSocket.getNamespace(), "");
						sendPlain(connect.toString());
					}
				} else {
					findCallback(message).onConnect();
				}
				firstSocket = null;
			} catch (Exception e) {
				error(new SocketIOException(
						"Exception was thrown in onConnect()", e));
			}
			break;
		case IOMessage.TYPE_HEARTBEAT:
			sendPlain("2::");
			break;
		case IOMessage.TYPE_MESSAGE:
			try {
				findCallback(message).onMessage(message.getData(),
						remoteAcknowledge(message));
			} catch (Exception e) {
				error(new SocketIOException(
						"Exception was thrown in onMessage(String).\n"
								+ "Message was: " + message.toString(), e));
			}
			break;
		case IOMessage.TYPE_JSON_MESSAGE:
			try {
				JSONObject obj = null;
				String data = message.getData();
				if (data.trim().equals("null") == false)
					obj = new JSONObject(data);
				try {
					findCallback(message).onMessage(obj,
							remoteAcknowledge(message));
				} catch (Exception e) {
					error(new SocketIOException(
							"Exception was thrown in onMessage(JSONObject).\n"
									+ "Message was: " + message.toString(), e));
				}
			} catch (JSONException e) {
				logger.warning("Malformated JSON received");
			}
			break;
		case IOMessage.TYPE_EVENT:
			try {
				JSONObject event = new JSONObject(message.getData());
				Object[] argsArray;
				if (event.has("args")) {
					JSONArray args = event.getJSONArray("args");
					argsArray = new Object[args.length()];
					for (int i = 0; i < args.length(); i++) {
						if (args.isNull(i) == false)
							argsArray[i] = args.get(i);
					}
				} else
					argsArray = new Object[0];
				String eventName = event.getString("name");
				try {
					findCallback(message).on(eventName,
							remoteAcknowledge(message), argsArray);
				} catch (Exception e) {
					error(new SocketIOException(
							"Exception was thrown in on(String, JSONObject[]).\n"
									+ "Message was: " + message.toString(), e));
				}
			} catch (JSONException e) {
				logger.warning("Malformated JSON received");
			}
			break;

		case IOMessage.TYPE_ACK:
			String[] data = message.getData().split("\\+", 2);
			if (data.length == 2) {
				try {
					int id = Integer.parseInt(data[0]);
					IOAcknowledge ack = acknowledge.get(id);
					if (ack == null)
						logger.warning("Received unknown ack packet");
					else {
						JSONArray array = new JSONArray(data[1]);
						Object[] args = new Object[array.length()];
						for (int i = 0; i < args.length; i++) {
							args[i] = array.get(i);
						}
						ack.ack(args);
					}
				} catch (NumberFormatException e) {
					logger.warning("Received malformated Acknowledge! This is potentially filling up the acknowledges!");
				} catch (JSONException e) {
					logger.warning("Received malformated Acknowledge data!");
				}
			} else if (data.length == 1) {
				sendPlain("6:::" + data[0]);
			}
			break;
		case IOMessage.TYPE_ERROR:
			try {
				findCallback(message).onError(
						new SocketIOException(message.getData()));
			} catch (SocketIOException e) {
				error(e);
			}
			if (message.getData().endsWith("+0")) {
				cleanup();
			}
			break;
		case IOMessage.TYPE_NOOP:
			break;
		default:
			logger.warning("Unkown type received" + message.getType());
			break;
		}
	}

	public synchronized void reconnect() {
		if (getState() != STATE_INVALID) {
			invalidateTransport();
			setState(STATE_INTERRUPTED);
			if (reconnectTask != null) {
				reconnectTask.cancel();
			}
			reconnectTask = new ReconnectTask();
			backgroundTimer.schedule(reconnectTask, 1000);
		}
	}

	public String getSessionId() {
		return sessionId;
	}

	public void send(SocketIO socket, IOAcknowledge ack, String text) {
		IOMessage message = new IOMessage(IOMessage.TYPE_MESSAGE,
				socket.getNamespace(), text);
		synthesizeAck(message, ack);
		sendPlain(message.toString());
	}

	public void send(SocketIO socket, IOAcknowledge ack, JSONObject json) {
		IOMessage message = new IOMessage(IOMessage.TYPE_JSON_MESSAGE,
				socket.getNamespace(), json.toString());
		synthesizeAck(message, ack);
		sendPlain(message.toString());
	}

	public void emit(SocketIO socket, String event, IOAcknowledge ack,
			Object... args) {
		try {
			JSONObject json = new JSONObject().put("name", event).put("args",
					new JSONArray(Arrays.asList(args)));
			IOMessage message = new IOMessage(IOMessage.TYPE_EVENT,
					socket.getNamespace(), json.toString());
			synthesizeAck(message, ack);
			sendPlain(message.toString());
		} catch (JSONException e) {
			error(new SocketIOException(
					"Error while emitting an event. Make sure you only try to send arguments, which can be serialized into JSON."));
		}

	}

	public void emitX(SocketIO socket, String event, IOAcknowledge ack,
			Object arg) {
		try {
			JSONObject json = new JSONObject().put("name", event).put("args",
					arg);
			String jsonStr = json.toString();
			logger.warning("jsonStr=" + jsonStr);
			jsonStr = jsonStr.replace("\"[", "[");
			logger.warning("jsonStr1=" + jsonStr);
			jsonStr = jsonStr.replace("]\"", "]");
			logger.warning("jsonStr2=" + jsonStr);
			jsonStr = jsonStr.replace("\\\"", "\"");
			logger.warning("jsonStr3=" + jsonStr);
			IOMessage message = new IOMessage(IOMessage.TYPE_EVENT,
					socket.getNamespace(), jsonStr);
			synthesizeAck(message, ack);
			String str = message.toString();
			logger.warning("message str=" + str);
			sendPlain(str);
		} catch (JSONException e) {
			error(new SocketIOException(
					"Error while emitting an event. Make sure you only try to send arguments, which can be serialized into JSON."));
		}

	}

	public boolean isConnected() {
		return getState() == STATE_READY;
	}

	private synchronized int getState() {
		return state;
	}

	private synchronized void setState(int state) {
		if (getState() != STATE_INVALID)
			this.state = state;
	}

	public IOTransport getTransport() {
		return transport;
	}

	@Override
	public void onDisconnect() {
		SocketIO socket = sockets.get("");
		if (socket != null)
			socket.getCallback().onDisconnect();
	}

	@Override
	public void onConnect() {
		SocketIO socket = sockets.get("");
		if (socket != null)
			socket.getCallback().onConnect();
	}

	@Override
	public void onMessage(String data, IOAcknowledge ack) {
		for (SocketIO socket : sockets.values())
			socket.getCallback().onMessage(data, ack);
	}

	@Override
	public void onMessage(JSONObject json, IOAcknowledge ack) {
		for (SocketIO socket : sockets.values())
			socket.getCallback().onMessage(json, ack);
	}

	@Override
	public void on(String event, IOAcknowledge ack, Object... args) {
		for (SocketIO socket : sockets.values())
			socket.getCallback().on(event, ack, args);
	}

	@Override
	public void onError(SocketIOException socketIOException) {
		for (SocketIO socket : sockets.values())
			socket.getCallback().onError(socketIOException);
	}
}
