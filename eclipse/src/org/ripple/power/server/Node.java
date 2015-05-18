package org.ripple.power.server;

import io.netty.channel.Channel;

public class Node {

	public short msgType;
	public String username;
	public Channel channel;
	public Node next;

}