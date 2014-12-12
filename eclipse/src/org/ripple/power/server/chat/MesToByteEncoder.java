package org.ripple.power.server.chat;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MesToByteEncoder extends MessageToByteEncoder<AMessage>{


	@Override
	protected void encode(ChannelHandlerContext ctx, AMessage msg, ByteBuf out) throws Exception {
		msg.setByteBuf(out);
		msg.write();
	}

}