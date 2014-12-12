package org.ripple.power.server.chat;

public class LoginMessage extends AMessage {

	private String username;
	
	public LoginMessage() {
		super();
	}

	public LoginMessage(String username) {
		super();
		this.username = username;
	}

	@Override
	public short getMessageType() {
		return MessageType.CS_LOGIN;
	}

	@Override
	public void readImpl() {
		this.username = readString();
	}

	@Override
	public void writeImpl() {
		writeString(username);
	}

	public String getUsername() {
		return username;
	}

}
