package com.ripple.client.transport;

import java.net.Proxy;

import org.json.JSONObject;

public interface TransportEventHandler {
	void onMessage(JSONObject msg);

	void onConnecting(int attempt);

	void onDisconnected(boolean willReconnect);

	void onError(Exception error);

	void setProxy(Proxy proxy);
	
	void onConnected();
}
