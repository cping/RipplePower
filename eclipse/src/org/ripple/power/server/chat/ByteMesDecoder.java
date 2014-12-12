package org.ripple.power.server.chat;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;


public class ByteMesDecoder extends ByteToMessageDecoder {

	private IMessageRecognizer messageRecognizer;

	public ByteMesDecoder(IMessageRecognizer messageRecognizer) {
		this.messageRecognizer = messageRecognizer;
	}

	@Override
	protected void decode(ChannelHandlerContext chx, ByteBuf in, List<Object> out) throws Exception {
		if (in.readableBytes() < 6) {
			return;
		}
		in.markReaderIndex();
		int expectLen = in.getInt(in.readerIndex());
		int buffCurLen = in.readableBytes();
		if (buffCurLen < expectLen) {
			in.resetWriterIndex();
			return;
		}
		int msgLen = in.readInt();
		short msgType = in.readShort();
		AMessage msg = messageRecognizer.createMessage(msgType);
		if (msg == null) {
			int msgContentLen = msgLen - 6;
			if (0 < msgContentLen) {
				in.readBytes(msgContentLen);
			}
			return;
		}
		msg.setByteBuf(in);
		msg.read();
		out.add(msg);
	}
}