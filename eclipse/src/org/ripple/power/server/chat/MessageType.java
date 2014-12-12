package org.ripple.power.server.chat;


public abstract class MessageType {

	public final static short GS_REGISTE = 1000;
	public final static short GS_CHAT_OPEN = 1001;
	public final static short GS_FORBID_USER = 1002;
	public final static short GS_KILL_USER = 1003;
	public final static short GS_REGISTE_SYSTEM = 1004;
	public final static short GS_DATA_MAP = 200;
	public final static short CS_USER_NAME_LIST = 104;
	public final static short CS_LOGIN = 101;
	public final static short CS_LOGIN_OUT = 103;
	public final static short SC_LOGIN_RESULT = 102;
	public final static short CS_CHAT = 105;

	public final static short CHANNEL_ALL = 1;
	public final static short CHANNEL_WORLD = 2;
	public final static short CHANNEL_CORPS = 3;
	public final static short CHANNEL_ACTIVITY = 4;
	public final static short CHANNEL_GM = 5;
	public final static short SC_WELLCOME = 600;
	public final static short SC_CHAT = 601;
}
