package com.ripple.client.transport;

import org.json.JSONObject;

import java.net.Proxy;
import java.net.URI;

public interface WebSocketTransport {
	public abstract void setHandler(TransportEventHandler events);

	public abstract void sendMessage(JSONObject msg);
	
	public abstract void setProxy(Proxy proxy);

	public abstract void connect(URI url);

	public abstract void disconnect();
}
