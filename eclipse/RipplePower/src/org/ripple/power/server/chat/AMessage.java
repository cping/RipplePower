package org.ripple.power.server.chat;

import java.io.UnsupportedEncodingException;

import org.ripple.power.config.LSystem;

import io.netty.buffer.ByteBuf;

public abstract class AMessage {

	public static final int ERR_MESSAGE_LENGTH = 1024 * 300;

	private ByteBuf byteBuf;

	public abstract void writeImpl();

	public abstract void readImpl();

	public abstract short getMessageType();

	public void write() throws Exception {
		byteBuf.clear();
		byteBuf.markWriterIndex();
		byteBuf.writeInt(4 + 2);
		byteBuf.writeShort(getMessageType());
		writeImpl();
		int messageLength = byteBuf.writerIndex();
		if (messageLength > ERR_MESSAGE_LENGTH) {
			throw new Exception("size:" + messageLength + ",type: "
					+ this.getMessageType());
		}
		byteBuf.resetWriterIndex();
		byteBuf.writeInt(messageLength);
		byteBuf.setIndex(0, messageLength);
	}

	public void writeInt(int value) {
		byteBuf.writeInt(value);
	}

	public void writeLong(long value) {
		byteBuf.writeLong(value);
	}

	public void writeByte(byte value) {
		byteBuf.writeByte(value);
	}

	public void writeBytes(byte[] _bytes) {
		byteBuf.writeBytes(_bytes);
	}

	public void writeString(String value) {
		byte[] _bytes = null;
		try {
			_bytes = value.getBytes(LSystem.encoding);
		} catch (UnsupportedEncodingException e) {
			_bytes = value.getBytes();
		}
		byteBuf.writeShort(_bytes.length);
		byteBuf.writeBytes(_bytes);
	}

	public void writeShort(short value) {
		byteBuf.writeShort(value);
	}

	public void writeBoolean(boolean data) {
		this.byteBuf.writeBoolean(data);
	}

	public void writeFloat(float value) {
		byteBuf.writeFloat(value);
	}

	public void writeDouble(double value) {
		byteBuf.writeDouble(value);
	}

	public int readInt() {
		return byteBuf.readInt();
	}

	public long readLong() {
		return byteBuf.readLong();
	}

	public short readShort() {
		return byteBuf.readShort();
	}

	public byte readByte() {
		return byteBuf.readByte();
	}

	public float readFloat() {
		return byteBuf.readFloat();
	}

	public double readDouble() {
		return byteBuf.readDouble();
	}

	public String readString() {
		int _len = readShort();
		if (_len > 0) {
			byte[] _bytes = new byte[_len];
			byteBuf.readBytes(_bytes);
			try {
				return new String(_bytes, LSystem.encoding);
			} catch (UnsupportedEncodingException e) {
				return new String(_bytes);
			}
		}
		return null;
	}

	public ByteBuf getBuffer() {
		return this.byteBuf;
	}

	public void setByteBuf(ByteBuf byteBuf) {
		this.byteBuf = byteBuf;
	}

	public void read() {
		readImpl();
	}
}
