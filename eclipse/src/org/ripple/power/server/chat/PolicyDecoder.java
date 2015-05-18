package org.ripple.power.server.chat;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.util.CharsetUtil;

import java.util.List;

public class PolicyDecoder extends ReplayingDecoder<Void> {
	private final ByteBuf requestBuffer = Unpooled.copiedBuffer(
			"<policy-file-request/>", CharsetUtil.US_ASCII);

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buffer,
			List<Object> out) throws Exception {
		ByteBuf data = buffer.readBytes(requestBuffer.readableBytes());
		if (data.equals(requestBuffer)) {
			out.add(data);
		} else {
			data.release();
			ctx.channel().close();
		}
	}

	@Override
	protected void handlerRemoved0(ChannelHandlerContext ctx) {
		requestBuffer.release();
	}
}