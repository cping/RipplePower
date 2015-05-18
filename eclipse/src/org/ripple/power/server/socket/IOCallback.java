package org.ripple.power.server.socket;

import org.json.JSONObject;

public interface IOCallback {

	void onDisconnect();

	void onConnect();

	void onMessage(String data, IOAcknowledge ack);

	void onMessage(JSONObject json, IOAcknowledge ack);

	void on(String event, IOAcknowledge ack, Object... args);

	void onError(SocketIOException socketIOException);
}
