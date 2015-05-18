package com.ripple.client.transport;

import org.json.JSONObject;

import java.net.Proxy;
import java.net.URI;

public interface WebSocketTransport {
    public abstract void setHandler(TransportEventHandler events);
    public abstract void sendMessage(JSONObject msg);
    public abstract void connect(URI url);

	public abstract void setProxy(Proxy proxy);
    /**
     * It's the responsibility of implementations to trigger
     * {@link com.ripple.client.transport.TransportEventHandler#onDisconnected}
     */
    public abstract void disconnect();
}
