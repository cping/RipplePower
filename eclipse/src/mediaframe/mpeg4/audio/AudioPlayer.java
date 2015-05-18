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

import java.io.InputStream;

import org.ripple.power.config.LSystem;

/**
 * The <code>AudioPlayer</code> is an abstract parent class for audio players
 * which able to play the audio binary stream. It contains the methods that
 * allows to control the playback of the audio stream:
 * <ul>
 * <li>play, stop, pause and continue the playback;
 * <li>set mute state and the volume of the audio stream).
 * </ul>
 * Also it contains the method that allows to check if the sound is supported by
 * the current system.
 */
public abstract class AudioPlayer {

	/** The main working thread of the <code>AudioPlayer</code>. */
	protected volatile Thread audioPlayerThread = null;

	/** The output audio device. */
	protected AudioDevice audioDevice;

	/**
	 * <tt>True</tt>, if the <code>AudioPlayer</code> is ready to play the audio
	 * stream.
	 */
	protected boolean readyToPlay = false;

	/**
	 * <tt>True</tt>, if the <code>AudioPlayer</code> is decoding the audio
	 * stream.
	 */
	protected boolean decoding = true;

	/**
	 * Constructs an <code>AudioPlayer</code> object.
	 * 
	 * @throws Exception
	 *             raises if there is an error occurs (in most cases if no
	 *             output audio devices have been found).
	 */
	public AudioPlayer() throws Exception {
		super();
		readyToPlay = false;
		if (audioDevice == null) {
			audioDevice = detectSoundDevice();
		}
		if (audioDevice == null) {
			throw new Exception("The Audio Device does not found!");
		}
	}

	/**
	 * Detects support of AAC or MP3 audio streams and returns the proper audio
	 * player.
	 * 
	 * @param is
	 *            the MP3 or AAC audio input stream.
	 * @param audioHeaderSize
	 *            the size of the audio header (normally zero for MP3 streams).
	 * @return the detected audio player or null if there are no available audio
	 *         players have been found.
	 */
	public static AudioPlayer createAudioPlayer(InputStream is,
			int audioHeaderSize) throws Exception {
		// true, if the player should try AAC support first
		boolean try_AAC_first = audioHeaderSize > 0;
		try {
			try {
				Class.forName(try_AAC_first ? "mediaframe.mpeg4.audio.AAC.AACDecoder"
						: "javazoom.jlme.decoder.Decoder");
				if (try_AAC_first) {
					return new AACAudioPlayer(is, audioHeaderSize);
				} else {
					return new MP3AudioPlayer(is);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} catch (Exception ex) {
			try {
				if (try_AAC_first) {
					return new MP3AudioPlayer(is);
				} else {
					return new AACAudioPlayer(is, 0);
				}
			} catch (Exception ex2) {
				ex.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Detects and returns the available audio device. On the first step detects
	 * support of the Java2 Sound API and then, if the first step fails, tries
	 * to use the Java1 compatible audio device.
	 * 
	 * @return the detected audio device or null if there are no available audio
	 *         devices have been found.
	 */
	protected static AudioDevice detectSoundDevice() {
		try {

			try {
				Class.forName("javax.sound.sampled.SourceDataLine");
				return new Java2AudioDevice();
			} catch (Exception ex) {
				return null;
			}
		} catch (Exception ex) {
			try {
				return new Java1AudioDevice();
			} catch (Exception ex2) {
				ex.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Stops the playback of the audio stream.
	 */
	public synchronized void stop() {
		if (audioPlayerThread != null) {
			Thread workThread = audioPlayerThread;
			audioPlayerThread = null;
			workThread.interrupt();
			try {
				workThread.join(2000);
			} catch (Exception ex) {
			}
		}
		if (audioDevice != null) {
			audioDevice.close();
		}
	}

	/**
	 * Starts to play the audio stream.
	 * 
	 * @throws InterruptedException
	 *             raises if the current thread has been interrupted.
	 */
	public void play() throws InterruptedException {
		if (audioDevice != null) {
			synchronized (this) {
				if (!readyToPlay) {
					// waits up to 5 seconds for the audio process
					wait(5000);
					// adds small pause for the audio channel
					Thread.sleep(LSystem.SECOND);
				}
			}
			audioDevice.play();
		}
	}

	/**
	 * Pauses the playback of the audio stream.
	 */
	public void pause() {
		if (audioDevice != null) {
			audioDevice.pause();
		}
	}

	/**
	 * Sets the mute state of the audio player.
	 * 
	 * @param mute
	 *            the mute state to set.
	 */
	public void setMute(boolean mute) {
		if (audioDevice != null) {
			audioDevice.setMute(mute);
		}
	}

	/**
	 * Sets the volume of the audio stream.
	 * 
	 * @param volume
	 *            the volume to set.
	 */
	public void setVolume(int volume) {
		if (audioDevice != null) {
			audioDevice.setVolume(volume);
		}
	}

	/**
	 * Returns <tt>true</tt>, if the player has been detected the audio device
	 * and is able to play the sound.
	 */
	public static boolean isSoundEnabled() {
		return detectSoundDevice() != null;
	}

	/**
	 * Returns <tt>true</tt>, if the <code>AudioPlayer</code> is decoding the
	 * audio stream.
	 */
	public boolean isDecoding() {
		return decoding;
	}

}
