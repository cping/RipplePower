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

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PipedOutputStream;

/**
 * The <code>Java1AudioDevice</code> class realizes the sound device (implements
 * the <code>AudioDevice</code> interface) by using of the
 * <code>sun.audio.AudioPlayer</code> class.
 */
public final class Java1AudioDevice implements AudioDevice, Runnable {

	/** The 8khz mono ulaw audio stream. */
	private AudioDataInputStream audioDataInputStream;
	/** The 16 bit linear source audio stream. */
	private PipedOutputStream audioDataOutputStream;

	/** <tt>True</tt>, if the audio device is opened. */
	private boolean opened = false;

	/**
	 * Constructs an <code>Java1AudioDevice</code> object and tests if the
	 * output audio channel is exist.
	 * 
	 * @throws Exception
	 *             raises if there is an error occurs (in most cases if the
	 *             output audio channel isn't exist).
	 */
	public Java1AudioDevice() throws Exception {
		super();
		try {
			Thread.sleep(100);
		} catch (Exception iex) {
		}
		/*
		 * if((AudioPlayer.player == null) || !AudioPlayer.player.isAlive()) {
		 * throw new Exception("Unable to find the Java 1 audio channel"); }
		 * audioDataOutputStream = new PipedOutputStream(); audioDataInputStream
		 * = new AudioDataInputStream(this, audioDataOutputStream);
		 */
	}

	/**
	 * Starts to play the audio stream.
	 */
	public void play() {
		audioDataInputStream.setPlaying(true);
		synchronized (this) {
			notifyAll();
		}
	}

	/**
	 * Starts the <code>sun.audio.AudioPlayer</code>.
	 */
	public void run() {
		try {
			// System.out.println("Java1AudioDevice.run() 1");
			// AudioPlayer.player.start(audioDataInputStream);
			// System.out.println("Java1AudioDevice.run() 2");
		} catch (Throwable ex) {
			ex.printStackTrace(System.out);
		}
	}

	/**
	 * Opens the audio device and initializes it with the specified sample
	 * frequency and the number of channels of the input audio stream.
	 * 
	 * @param sampleFrequency
	 *            the sample frequency of the audio stream.
	 * @param channelCount
	 *            the number of channels of the audio stream.
	 */
	public void open(int sampleFrequency, int channelCount) {
		try {
			audioDataInputStream.setSampleFrequency(sampleFrequency);
			audioDataInputStream.setChannelCount(channelCount);
			audioDataInputStream.init_stream();
			new Thread(this).start();
			opened = true;
		} catch (Exception ex) {
			System.out.println(ex);
			ex.printStackTrace(System.out);
		}
	}

	/**
	 * Writes the next portion of audio samples into the audio device.
	 * 
	 * @param buffer
	 *            the array with the audio samples' data.
	 * @param size
	 *            the size of the audio samples' data.
	 * @throws InterruptedIOException
	 *             raises if the current thread has been interrupted.
	 */
	public void write(byte[] buffer, int size) throws InterruptedIOException {
		try {
			// System.out.println("void write(byte[] buffer, int size) " +
			// size);
			audioDataOutputStream.write(buffer, 0, size);
		} catch (InterruptedIOException int_io_ex) {
			throw int_io_ex;
		} catch (Exception ex) {
			System.out.println(ex);
			ex.printStackTrace(System.out);
		}
	}

	/**
	 * Pauses the playback of the audio stream.
	 */
	public void pause() {
		audioDataInputStream.setPlaying(false);
	}

	/**
	 * Closes the audio device.
	 */
	public void close() {
		if (opened) {
			// System.out.println("Java1AudioDevice.close()");
			try {
				audioDataInputStream.close();
			} catch (IOException ex) {
			}
			// AudioPlayer.player.stop(audioDataInputStream);
			opened = false;
		}
	}

	/**
	 * Sets the mute state of the audio device.
	 * 
	 * @param mute
	 *            the mute state to set.
	 */
	public void setMute(boolean mute) {
		audioDataInputStream.setMute(mute);
	}

	/**
	 * Sets the volume of the audio stream.
	 * 
	 * @param volume
	 *            the volume to set.
	 */
	public void setVolume(int volume) {
		audioDataInputStream.setVolume(volume);
	}

	/**
	 * Returns <tt>true</tt>, if the audio device is opened, <tt>false</tt>
	 * otherwise.
	 */
	public boolean isOpened() {
		return opened;
	}

	/**
	 * Returns <tt>true</tt>, if the audio device is ready to play the audio
	 * stream, <tt>false</tt> otherwise.
	 */
	public boolean isReady() {
		return audioDataInputStream.isFirstLoop() == false;
	}

}
