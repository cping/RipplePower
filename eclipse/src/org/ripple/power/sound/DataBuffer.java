/**
 * MediaFrame is an Open Source streaming media platform in Java 
 * which provides a fast, easy to implement and extremely small applet 
 * that enables to view your audio/video content without having 
 * to rely on external player applications or bulky plug-ins.
 * 
 * Copyright (C) 2004/5 MediaFrame (http://www.mediaframe.org).
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.ripple.power.sound;

import java.io.InputStream;
import java.io.IOException;
import java.io.InterruptedIOException;

/**
 * DataBuffer
 */
/**
 * 
 * Copyright 2008 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 *
 * @project loonframework
 * @author chenpeng  
 * @email：ceponline@yahoo.com.cn 
 * @version 0.1
 */
public final class DataBuffer extends InputStream implements Runnable {

	private InputStream is;

	private Thread bufferThread = null;

	private byte[] movieData = null;

	private int readed;

	private int writed;

	private int movieLength;

	private int wait_for_data;

	private IMpeg4 mpeg4;

	private int initMaxBufferSize;

	private int minBufferSize;

	private int maxBufferSize;

	private boolean buffering;

	private boolean reBuffering;

	private boolean encryptedStream;

	private int encryption_index;

	private byte[] encryption_key;


	public DataBuffer(IMpeg4 mpeg4, InputStream is, int movieLength,
			int maxBufferSize, boolean encryptedStream) {
		System.gc();
		this.mpeg4 = mpeg4;
		this.is = is;
		this.encryption_index = this.readed = this.writed = this.wait_for_data = 0;
		this.movieLength = movieLength;
		this.initMaxBufferSize = this.maxBufferSize = maxBufferSize;
		this.minBufferSize = maxBufferSize / 5;
		this.encryptedStream = encryptedStream;
		this.buffering = true;
		this.reBuffering = false;
		this.movieData = new byte[movieLength];
		if (encryptedStream) {
			try {
				encryption_key = (byte[]) Class.forName(
						"mediaframe.mpeg4.LicenseManager").getMethod(
						"getEncryptionKey", new Class[0]).invoke(null,
						new Object[0]);
			} catch (Throwable ex) {
				this.encryptedStream = false;
			}
		}
		bufferThread = new Thread(this, "Buffer Thread");
		bufferThread.start();
	}

	public void clear() {
		movieData = null;
		encryption_key = null;
	}

	/**
	 * Stops the buffering of the movie stream.
	 */
	public synchronized void stop() {
		if (bufferThread != null) {
			Thread workThread = bufferThread;
			bufferThread = null;
			workThread.interrupt();
		}
		try {
			super.close();
		} catch (Exception ex) {
		}
	}

	public void run() {
		try {
			int c = 0;
			while ((bufferThread != null) && ((c = is.read()) != -1)) {
				if (encryptedStream) {
					c ^= encryption_key[encryption_index++];
					if (encryption_index == encryption_key.length) {
						encryption_index = 0;
					}
				}
				synchronized (this) {
					movieData[writed++] = (byte) (c - 128);
					if (wait_for_data > 0) {
						notifyAll();
					}
				}
				if (buffering || reBuffering) {
					if (writed >= maxBufferSize) {
						if (reBuffering) {
							reBuffering = false;
							mpeg4.stopReBuffering();
						} else {
							buffering = false;
							mpeg4.stopBuffering();
						}
					}
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			movieLength = writed;
			try {
				if (is != null) {
					is.close();
					is = null;
				}
			} catch (Exception ex) {
			}
			bufferThread = null;
			try {
				if (buffering || reBuffering) {
					if (reBuffering) {
						reBuffering = false;
						mpeg4.stopReBuffering();
					} else {
						buffering = false;
						mpeg4.stopBuffering();
					}
				}
			} catch (Exception ex) {
			}
			synchronized (this) {
				if (wait_for_data > 0) {
					notifyAll();
				}
			}
		}
	}

	public long skip(long n) throws IOException {
		long skipped = 0;
		for (int i = 0; (i < n) && (read() != -1); i++) {
		}
		return skipped;
	}

	public synchronized int read() throws IOException {
		if (readed >= movieLength) {
			return -1;
		}
		while (readed >= writed) {
			wait_for_writer();
		}
		return movieData[readed++] + 128;
	}

	public synchronized int read(int position, int channelType)
			throws IOException {
		if (position >= movieLength) {
			return -1;
		}
		while ((position >= writed) && (position < movieLength)) {
			wait_for_writer();
		}
		if ((channelType == DataChannel.VIDEO_CHANNEL) && (buffering == false)
				&& (reBuffering == false)) {
			if ((writed != movieLength)
					&& ((writed - position) <= minBufferSize)) {
				reBuffering = true;
				mpeg4.startReBuffering();
				maxBufferSize = initMaxBufferSize + writed;
			}
		}
		return movieData[position] + 128;
	}

	private final synchronized void wait_for_writer() throws IOException {
		wait_for_data++;
		try {
			wait();
		} catch (InterruptedException ie) {
			throw new InterruptedIOException(ie.getMessage());
		} finally {
			wait_for_data--;
		}
	}
}
