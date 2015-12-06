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

/**
 * The <code>AudioDevice</code> interface defines methods which allows to start,
 * pause/continue, stop the playback of the audio stream. They also allows to
 * control the mute state and the volume of the playback and to check the
 * current status of the Audio Device (if it is ready for playback and if it is
 * opened).
 */
public interface AudioDevice {

	/**
	 * Starts to play the audio stream.
	 */
	public void play();

	/**
	 * Opens an output audio device and initializes it with the specified sample
	 * frequency and the number of channels of the input audio stream.
	 * 
	 * @param sampleFrequency
	 *            the sample frequence of the audio stream.
	 * @param channelCount
	 *            the number of channels of the audio stream.
	 */
	public void open(int sampleFrequency, int channelCount);

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
	public void write(byte[] buffer, int size) throws InterruptedIOException;

	/**
	 * Pauses the playback of the audio stream.
	 */
	public void pause();

	/**
	 * Closes the audio device.
	 */
	public void close();

	/**
	 * Returns <tt>true</tt>, if the audio device is opened, <tt>false</tt>
	 * otherwise.
	 */
	public boolean isOpened();

	/**
	 * Returns <tt>true</tt>, if the audio device is ready to play the audio
	 * stream, <tt>false</tt> otherwise.
	 */
	public boolean isReady();

	/**
	 * Sets the mute state of the audio device.
	 * 
	 * @param mute
	 *            the mute state to set.
	 */
	public void setMute(boolean mute);

	/**
	 * Sets the volume of the audio stream.
	 * 
	 * @param volume
	 *            the volume to set.
	 */
	public void setVolume(int volume);
}
