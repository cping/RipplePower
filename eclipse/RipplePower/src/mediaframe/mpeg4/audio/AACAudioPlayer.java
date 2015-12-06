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

import java.io.EOFException;
import java.io.InputStream;
import java.io.InterruptedIOException;

import mediaframe.mpeg4.audio.AAC.BitStream;
import mediaframe.mpeg4.audio.AAC.AACDecoder;

/**
 * The <code>AACAudioPlayer</code> class realizes an audio player that plays the
 * AAC audio binary stream. It uses the external AAC library to decode the audio
 * binary stream into the array of audio samples, which plays through the
 * available audio device (Java2 Sound API or Java1 compatible audio device).
 */
public final class AACAudioPlayer extends AudioPlayer implements Runnable {

	/** Constant, the size of the buffer for audio samples. */
	private final static int BUFFER_SIZE = 15000;

	/** The input audio binary stream. */
	private BitStream bitstream;

	/**
	 * Constructs an <code>AACAudioPlayer</code> object using specified audio
	 * data input stream and the size of the audio header.
	 * 
	 * @param is
	 *            audio data input stream.
	 * @param audioHeaderSize
	 *            the size of the audio header.
	 * @throws Exception
	 *             raises if there is an error occurs (in most cases if no
	 *             output audio devices have been found).
	 */
	public AACAudioPlayer(InputStream is, int audioHeaderSize) throws Exception {
		super();
		bitstream = new BitStream(is, audioHeaderSize);
		audioPlayerThread = new Thread(this, "Audio Player Thread");
		audioPlayerThread.start();
	}

	/**
	 * Decodes the audio binary stream using the external AAC library into the
	 * array of audio samples, which plays through the available audio device.
	 */
	public void run() {
		try {
			byte[] buf = new byte[BUFFER_SIZE];
			AACDecoder decoder = null;
			while (audioPlayerThread != null) {
				if (decoder == null) {
					decoder = new AACDecoder(bitstream);
					// System.out.println("Audio: MPEG AAC " +
					// decoder.getAudioProfile() + ' ' +
					// decoder.getSampleFrequency() + " kHz " +
					// (decoder.getChannelCount() == 1 ? "Mono" :
					// (decoder.getChannelCount() == 2 ? "Stereo" :
					// decoder.getChannelCount() + " Channels")));
				}
				if (!audioDevice.isOpened()) {
					audioDevice.open(decoder.getSampleFrequency(),
							decoder.getChannelCount());
				}
				if (!readyToPlay && audioDevice.isReady()) {
					synchronized (this) {
						readyToPlay = true;
						notifyAll();
					}
				}
				int bufSize = decoder.decodeFrame(buf);
				if (bufSize > 0) {
					audioDevice.write(buf, bufSize);
				}
			}
		} catch (InterruptedIOException ioex) {
		} catch (EOFException ex) {
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			decoding = false;
			readyToPlay = true;
			audioPlayerThread = null;
		}
		// System.out.println("Audio Stream is ended!");
	}

}
