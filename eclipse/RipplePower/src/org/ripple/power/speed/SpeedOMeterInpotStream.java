package org.ripple.power.speed;

import java.io.IOException;
import java.io.InputStream;

public class SpeedOMeterInpotStream extends InputStream {
	private InputStream mIs;
	private int mSize;
	private int mReaded;
	private boolean started = false;
	private long t1;
	private long t2;
	private SpeedListener mNetworkSpeedListener;
	private int mStreamIndex;

	public SpeedOMeterInpotStream(InputStream is, int size, int streamIndex, SpeedListener networkSpeedListener) {
		mIs = is;
		mSize = size;
		mStreamIndex = streamIndex;
		mNetworkSpeedListener = networkSpeedListener;
	}

	@Override
	public int read() throws IOException {
		checkInit();
		int aux = mIs.read();
		if (!isEnd(aux)) {
			mReaded++;
		}
		checkSize();
		return aux;
	}

	@Override
	public int read(byte[] buffer) throws IOException {
		checkInit();
		int aux = mIs.read(buffer);
		if (!isEnd(aux)) {
			mReaded += aux;
		}
		checkSize();
		return aux;
	}

	@Override
	public int read(byte[] buffer, int offset, int length) throws IOException {
		checkInit();
		int aux = mIs.read(buffer, offset, length);
		if (!isEnd(aux)) {
			mReaded += aux;
		}
		checkSize();
		return aux;
	}

	private void checkInit() {
		if (!started) {
			started = true;
			t1 = System.currentTimeMillis();
		}
	}

	private boolean isEnd(int result) {
		if (result == -1) {
			return true;
		} else {
			return false;
		}
	}

	private void checkSize() {
		if (mReaded >= (mSize - 100)) {
			t2 = System.currentTimeMillis();
			long ms = t2 - t1;
			float speed = (float) mReaded / ((float) ms / (float) 1000l);
			mNetworkSpeedListener.networkSpeed(0, mStreamIndex, speed);
		}
	}

	@Override
	public long skip(long byteCount) throws IOException {
		return mIs.skip(byteCount);
	}
}