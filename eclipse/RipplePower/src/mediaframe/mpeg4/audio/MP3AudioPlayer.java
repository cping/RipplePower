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
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;

import javazoom.jlme.decoder.BitStream;
import javazoom.jlme.decoder.Decoder;
import javazoom.jlme.decoder.Header;
import javazoom.jlme.decoder.SampleBuffer;

/**
 * The <code>MP3AudioPlayer</code> class realizes an audio player that plays the
 * MP3 audio binary stream. It uses the external MP3 library to decode the audio
 * binary stream into the array of audio samples, which plays through the
 * available audio device (Java2 Sound API or Java1 compatible audio device).
 */
public final class MP3AudioPlayer extends AudioPlayer implements Runnable {

	/** The input audio binary stream. */
	private BitStream bitstream;

	/**
	 * Constructs an <code>MP3AudioPlayer</code> object using specified audio
	 * data input stream.
	 * 
	 * @param is
	 *            audio data input stream.
	 * @throws Exception
	 *             raises if there is an error occurs (in most cases if no
	 *             output audio devices have been found).
	 */
	public MP3AudioPlayer(InputStream is) throws Exception {
		super();
		bitstream = new BitStream(is);
		audioPlayerThread = new Thread(this, "Audio Player Thread");
		audioPlayerThread.start();
	}

	/**
	 * Decodes the audio binary stream using the external MP3 library into the
	 * array of audio samples, which plays through the available audio device.
	 */
	public void run() {
		try {
			Header header = null;
			Decoder decoder = null;
			while ((audioPlayerThread != null)
					&& ((header = bitstream.readFrame()) != null)) {
				if (decoder == null) {
					decoder = new Decoder(header, bitstream);
					// System.out.println("Audio: MPEG " + ((header.version() ==
					// Header.MPEG1) ? "1" : "2") + " LAYER " + header.layer() +
					// ' ' + header.frequency() + " kHz " +
					// Header.bitrate_str[header.version()][header.layer() -
					// 1][header.bitrate_index()] + " " + header.mode_string());
				}
				SampleBuffer sampleBuffer = decoder.decodeFrame();
				if (!audioDevice.isOpened()) {
					audioDevice.open(sampleBuffer.getSampleFrequency(),
							sampleBuffer.getChannelCount());
				}
				if (!readyToPlay && audioDevice.isReady()) {
					synchronized (this) {
						readyToPlay = true;
						notifyAll();
					}
				}
				if (sampleBuffer.size() > 0) {
					audioDevice.write(sampleBuffer.getBuffer(),
							sampleBuffer.size());
				}
				bitstream.closeFrame();
			}
		} catch (InterruptedIOException ioex) {
		} catch (EOFException ex) {
		} catch (IOException ex) {
			// ex.printStackTrace();
		} finally {
			decoding = false;
			readyToPlay = true;
			audioPlayerThread = null;
		}
		// System.out.println("Audio Stream is ended!");
	}
}
