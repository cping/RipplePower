package org.ripple.power.server.socket;

public class SocketIOException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SocketIOException(String message) {
		super(message);
	}

	public SocketIOException(String message, Exception ex) {
		super(message, ex);
	}
}
