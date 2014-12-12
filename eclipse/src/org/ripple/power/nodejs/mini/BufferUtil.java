package org.ripple.power.nodejs.mini;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class BufferUtil {
	
	private final JavascriptRuntime	runtime;

	public BufferUtil(final JavascriptRuntime runtime) {
		this.runtime = runtime;
	}

	public ByteBuf new_buffer(final int len) {
		return Unpooled.buffer(len, len);
	}
	
	public JavascriptRuntime getRuntime(){
		return runtime;
	}
}
