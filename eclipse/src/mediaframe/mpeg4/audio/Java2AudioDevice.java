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
package mediaframe.mpeg4.audio;

import java.io.InterruptedIOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * The <code>Java2AudioDevice</code> class realizes the sound device
 * (implements the <code>AudioDevice</code> interface) through Java2 Sound API. 
 */
public final class Java2AudioDevice implements AudioDevice {
	
	/** Constant, the size of the inner buffer with audio samples. */
	public final int BUFFER_SIZE = 102400; // 100K
	/** The audio line to output the audio data. */
	private SourceDataLine sourceDataLine = null;
	/** The current volume of the player. */
	private int volume = 100;
	/** <tt>True</tt>, if the audio device plays the audio. */
	private boolean playing = false;    
	/** The mute status of the audio device (equals <tt>true</tt> if no audio). */
	private boolean mute = false;
	/** <tt>True</tt>, if the audio device is opened. */
	private boolean opened = false;

	/**
	 * Constructs an <code>Java2AudioDevice</code> object 
	 * and checks if a free audio channel is exist.
	 * @throws Exception raises if there is an error occurs  
	 * (in most cases if no free audio channels have been found).   
	 */	
	public Java2AudioDevice() throws Exception {
		super();
		SourceDataLine sourceDataLine = getSourceDataLine(new AudioFormat(44100, 16, 2, true, false));
		sourceDataLine.open();
		sourceDataLine.close();
	}
	
	/**
	 * Gets an available audio channel (<code>SourceDataLine</code>) for specified audio stream's format.
	 * @param format the format of the audio stream.
	 * @return the available audio channel. 
	 * @throws LineUnavailableException raises if no available audio channels 
	 * have been found for the specified format. 
	 */
	private SourceDataLine getSourceDataLine(AudioFormat format) throws LineUnavailableException {
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		if(!AudioSystem.isLineSupported(info)) {
			throw new LineUnavailableException ("Unable to find the Java 2 audio channel");
		}
		return (SourceDataLine)AudioSystem.getLine(info);
	}
	
	/**
	 * Starts the playback of the audio stream. 
	 */
	public void play() {
		playing = true;
		if(sourceDataLine != null) {
			sourceDataLine.start();
		}
		synchronized(this) {
			notifyAll();
		}
	}

	/**
	 * Opens the output audio channel and initializes it with the specified sample frequency 
	 * and the number of channels of the audio stream.
	 * @param sampleFrequency the sample frequency of the audio stream.
	 * @param channelCount the number of channels of the audio stream.
	 */
	public void open(int sampleFrequency, int channelCount) {
		try {	
			if(sourceDataLine == null) {
				AudioFormat format = new AudioFormat(sampleFrequency, 16, channelCount, true, false);
				sourceDataLine = getSourceDataLine(format);
				sourceDataLine.open(format, BUFFER_SIZE);
				opened = true;
				setMute(mute);
				setVolume(volume);
				if(playing) {
					sourceDataLine.start();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Writes the next portion of audio samples into the audio device.
	 * @param buffer the array with the audio samples' data.
	 * @param size the size of the audio samples' data.
	 * @throws InterruptedIOException raises if the current thread has been interrupted.
	 */
	public void write(byte[] buffer, int size) throws InterruptedIOException {
		try {
			sourceDataLine.write(buffer, 0, size);
			synchronized(this) {
				while(playing == false) {
					wait();
				}
			}
		} catch (InterruptedException ex) {
			throw new InterruptedIOException(ex.getMessage());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Pauses the playback of the audio stream.
	 */
	public void pause() {
		playing = false;
		if(sourceDataLine != null) {
			sourceDataLine.stop();
		}
	}
	
	/**
	 * Closes the audio device.
	 */
	public void close() {	
		if(sourceDataLine != null) {
			sourceDataLine.close();
			sourceDataLine = null;
			opened = false;
		}
	}
	
	/**
	 * Sets the mute state of the audio device.
	 * @param mute the mute state to set.
	 */	
	public void setMute(boolean mute) {
		this.mute = mute;
		if(sourceDataLine != null) {
			BooleanControl muteCtrl = (BooleanControl)sourceDataLine.getControl(BooleanControl.Type.MUTE);			
			muteCtrl.setValue(mute);
			if(!mute) {
				setVolume(volume);
			}
		}
	}
	
	/**
	 * Sets the volume of the audio stream.
	 * @param volume the volume to set.
	 */	
	public void setVolume(int volume){
		this.volume = volume;
		if((sourceDataLine != null) && !mute) {
			FloatControl volumeCtrl = (FloatControl)sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
			float GainDb = (float)(20.d * Math.log(volume == 0 ? Double.MIN_VALUE: ((double)volume / 100.d)) / Math.log(10));      
			volumeCtrl.setValue(GainDb);
		}
	}
	
	/**
	 * Returns <tt>true</tt>, if the audio device is opened, <tt>false</tt> otherwise.
	 */
	public boolean isOpened() {
		return opened;
	}
	
	/**
	 * Returns <tt>true</tt>, if the audio device is ready to play the audio stream, <tt>false</tt> otherwise.
	 */
	public boolean isReady() {
		return opened;
	}

}

