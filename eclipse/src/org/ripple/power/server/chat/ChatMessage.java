package org.ripple.power.server.chat;

public class ChatMessage extends AMessage {

	private String msg;
	private short type;
	private String username;
	private String toUser;

	public ChatMessage() {
		super();
	}

	public ChatMessage(short type, String msg, String username, String toUser) {
		super();
		this.msg = msg;
		this.type = type;
		this.username = username;
		this.toUser = toUser;
	}

	@Override
	public short getMessageType() {
		return MessageType.CS_CHAT;
	}

	@Override
	public void readImpl() {
		this.type = readShort();
		this.msg = readString();
		this.username = readString();
		this.toUser = readString();
	}

	@Override
	public void writeImpl() {
		writeShort(type);
		writeString(msg);
		writeString(username);
		writeString(toUser);
	}

	public String getMsg() {
		return msg;
	}

	public short getType() {
		return type;
	}

	public String getUsername() {
		return username;
	}

	public String getToUser() {
		return toUser;
	}
}
