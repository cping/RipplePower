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

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

/**
 * DataChannel
 */
public final class DataChannel extends InputStream {
	
	public final static int VIDEO_CHANNEL = 0;
	public final static int AUDIO_CHANNEL = 1;
	
	private DataBuffer dataBuffer = null;
	private Vector<?> channelSamples = null;
	private int channelSize = 0;
	private int currentSample = 0;
	private int currentSamplePos = 0;
	private DataSample currentDataSample = null;
	
	private int channelType;
	
	public DataChannel (int channelType, DataBuffer dataBuffer, Vector<?> channelSamples) throws Exception {
		super();
		if((channelSamples == null) || (channelSamples.size() == 0)) {
			throw new Exception ("The data channel is empty");
		}
		this.dataBuffer = dataBuffer;
		this.channelSamples = channelSamples;
		this.channelType = channelType;
		for(int i = 0; i < channelSamples.size(); i++) {
			DataSample dataSample = (DataSample)channelSamples.elementAt(i);
			channelSize+= dataSample.getSize(); 
		}
		currentDataSample = (DataSample)channelSamples.elementAt(0);
	}
	
	
	public synchronized int read() throws IOException {
		if((currentDataSample == null) || (dataBuffer == null)) {
			throw new EOFException();
		}
		while(currentSamplePos == currentDataSample.getSize()) {
			if(currentSample == (channelSamples.size() - 1)) {
				currentDataSample = null;
				return -1;
			}
			currentSamplePos = 0;
			currentDataSample = (DataSample)channelSamples.elementAt(++currentSample);
		}
		return dataBuffer.read((int)(currentDataSample.getOffset() + currentSamplePos++), channelType);
	}	

	public int read(byte[] buf, int off, int len) throws IOException {
		int c, i = 0;
		for(; (i < len) && ((c = read()) != -1); i++) {
			buf[off + i] = (byte)c;
		}
		if((i == 0) && (len > 0)) {
			return -1;
		}
		return i;
	}
	
	public long skip(long n) throws IOException {
		long i = 0;
		for(; (i < n) && (read() != -1); i++) {
		}
		return i;
	}


	public DataBuffer getDataBuffer() {
		return dataBuffer;
	}


	public void setDataBuffer(DataBuffer dataBuffer) {
		this.dataBuffer = dataBuffer;
	}


	public Vector<?> getChannelSamples() {
		return channelSamples;
	}


	public void setChannelSamples(Vector<?> channelSamples) {
		this.channelSamples = channelSamples;
	}


	public int getChannelSize() {
		return channelSize;
	}


	public void setChannelSize(int channelSize) {
		this.channelSize = channelSize;
	}


	public int getCurrentSample() {
		return currentSample;
	}


	public void setCurrentSample(int currentSample) {
		this.currentSample = currentSample;
	}


	public int getCurrentSamplePos() {
		return currentSamplePos;
	}


	public void setCurrentSamplePos(int currentSamplePos) {
		this.currentSamplePos = currentSamplePos;
	}


	public DataSample getCurrentDataSample() {
		return currentDataSample;
	}


	public void setCurrentDataSample(DataSample currentDataSample) {
		this.currentDataSample = currentDataSample;
	}


	public int getChannelType() {
		return channelType;
	}


	public void setChannelType(int channelType) {
		this.channelType = channelType;
	}
}
