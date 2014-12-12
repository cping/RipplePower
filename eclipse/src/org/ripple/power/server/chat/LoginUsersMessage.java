package org.ripple.power.server.chat;

import java.util.ArrayList;
import java.util.List;


public class LoginUsersMessage extends AMessage {

	private List<String> users;

	public LoginUsersMessage() {
		super();
	}

	public LoginUsersMessage(List<String> list) {
		super();
		users = list;
	}

	@Override
	public short getMessageType() {
		return MessageType.CS_USER_NAME_LIST;
	}

	@Override
	public void readImpl() {
		users = new ArrayList<String>();
		short len = readShort();
		for (int i = 0; i < len; i++) {
			users.add(readString());
		}
	}

	@Override
	public void writeImpl() {
		writeShort((short) users.size());
		for (String name : users) {
			writeString(name);
		}
	}

	public List<String> getUsers() {
		return users;
	}

}
