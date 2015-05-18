package org.ripple.power.server.socket;

import java.io.IOException;

interface IOTransport {

	void connect();

	void disconnect();

	void send(String text) throws Exception;

	boolean canSendBulk();

	void sendBulk(String[] texts) throws IOException;

	void invalidate();

	String getName();
}
