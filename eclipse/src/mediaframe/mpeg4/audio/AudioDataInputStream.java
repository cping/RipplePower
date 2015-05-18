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
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * The <code>AudioDataInputStream</code> realizes the 8khz mono ulaw audio
 * stream which gets the data from connected by a pipe the mono or stereo 16 bit
 * linear source audio stream. It also implements the control of the playback
 * (start, pause/continue, stop), the mute on/off and the volume
 * functionalitities.
 */
public final class AudioDataInputStream extends PipedInputStream {

	/** Constant, the size of the piped buffer for audio samples. */
	protected final static int PIPED_SIZE = 1048576; // 1000K

	/** Constant, the zero value in the 8 bit ulaw format. */
	public final static int ULAW_ZERO = 127;
	/** Constant, the add-in bias for 16 bit samples. */
	private final static int BIAS = 0x84;
	/** Constant, the max value of the magnitude. */
	private final static int CLIP = 32635;
	/** The table that is used for look-up of the exponenta for a byte. */
	private final static int[] exp_lut = { 0, 0, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3,
			3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5,
			5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
			5, 5, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
			6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
			6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
			6, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
			7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
			7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
			7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
			7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
			7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
			7, 7 };

	/**
	 * The number of channels in the source audio stream (1 - mono or 2 -
	 * stereo).
	 */
	private int channelCount;
	/** The step between the samples in the source audio stream. */
	private int sampleStep = (8000 << 16) / 44100;
	/** The current sample position in the source audio stream. */
	private int samplePosition = 0;
	/** <tt>True</tt>, if the audio device plays the audio. */
	private boolean playing = false;
	/** The mute status of the audio device (equals <tt>true</tt> if no audio). */
	private boolean mute = false;
	/** The current volume of the audio device. */
	private int volume = 100;
	/**
	 * <tt>True</tt>, if the audio data stream is closed, <tt>false</tt>
	 * otherwise.
	 */
	private boolean closed = false;
	/**
	 * The source audio stream in the pipe with samples in the 16 bit linear
	 * format.
	 */
	private PipedOutputStream source;
	/** The reference to the <code>Java1AudioDevice</code> audio device. */
	private Java1AudioDevice audioDevice;
	/**
	 * <tt>True</tt>, if any samples haven't been read from the audio data
	 * stream, <tt>false</tt> otherwise.
	 */
	private boolean firstLoop = true;

	/**
	 * Constructs an <code>AudioDataInputStream</code> object and inits it using
	 * the specified <code>Java1AudioDevice</code> audio device and the source
	 * audio stream.
	 * 
	 * @param audioDevice
	 *            the <code>Java1AudioDevice</code> audio device.
	 * @param audioDataOutputStream
	 *            the source audio stream with samples in the 16 bit linear
	 *            format.
	 * @throws IOException
	 *             raises if there is an I/O error occurs.
	 */
	public AudioDataInputStream(Java1AudioDevice audioDevice,
			PipedOutputStream audioDataOutputStream) throws IOException {
		super(audioDataOutputStream);
		this.audioDevice = audioDevice;
		this.source = audioDataOutputStream;
		this.buffer = new byte[PIPED_SIZE];
	}

	/**
	 * Inits the piped stream (writes to the source audio stream and reads from
	 * the input audio data stream 1 byte).
	 * 
	 * @throws IOException
	 *             raises if there is an I/O error occurs.
	 */
	public void init_stream() throws IOException {
		source.write(0);
		super.read();
	}

	/**
	 * Reads the next audio sample in the 8 bit ulaw format of 8khz mono audio
	 * data stream. Implements the playback start, pause/continue, stop, the
	 * mute on/off and the volume functionalities.<br/>
	 * Converts the mono or stereo input audio stream from the 16 bit linear
	 * format into the 8khz mono format with 8 bit ulaw samples.
	 * 
	 * @return the next audio sample in the 8 bit ulaw format, or -1 the audio
	 *         data stream is closed.
	 * @throws IOException
	 *             raises if there is an I/O error occurs.
	 */
	public int read() throws IOException {
		int sample = 0;
		int currentSamplePosition = samplePosition >> 16;
		int sample_size = channelCount == 2 ? 4 : 2;
		int samples_count = 0;
		int samples_summa = 0;
		try {
			while (!playing && !closed) {
				synchronized (audioDevice) {
					audioDevice.wait();
				}
			}
		} catch (InterruptedException ex) {
			throw new InterruptedIOException(ex.getMessage());
		}
		int available = available();
		while (currentSamplePosition == (samplePosition >> 16)) {
			if (available < sample_size) {
				return ULAW_ZERO;
			}
			sample = (short) (super.read() + (super.read() << 8));
			if (channelCount == 2) {
				sample = (sample + (short) (super.read() + (super.read() << 8))) >> 1;
			}
			available -= sample_size;
			samples_count++;
			samples_summa += sample;
			samplePosition += sampleStep;
		}
		sample = samples_summa / samples_count;
		if (closed) {
			return -1;
		}
		if (mute) {
			return ULAW_ZERO;
		} else {
			sample = sample * volume / 100;
			return linear2ulaw(sample);
		}
	}

	/**
	 * Reads up to <code>len</code> audio samples in the 8 bit ulaw format into
	 * the buffer <code>b</code> starting from the offset <code>off</code>.
	 * 
	 * @param b
	 *            the buffer to store the readed audio samples.
	 * @param off
	 *            the offset in the buffer to store the samples.
	 * @param len
	 *            the number of samples to read.
	 * @return the number of samples which have been read or -1 if any samples
	 *         haven't been read from the stream.
	 * @throws IOException
	 *             raises if there is an I/O error occurs.
	 */
	public int read(byte[] b, int off, int len) throws IOException {
		firstLoop = false;
		for (int i = 0; i < len; i++) {
			int sample = read();
			if (sample == -1) {
				if (i == 0) {
					return -1;
				} else {
					return i;
				}
			}
			b[i + off] = (byte) sample;
		}
		return len;
	}

	/*
	 * * Source: http://www.speech.cs.cmu.edu/comp.speech/Section2/Q2.7.html**
	 * This routine converts from linear to ulaw** Craig Reese:
	 * IDA/Supercomputing Research Center* Joe Campbell: Department of Defense*
	 * 29 September 1989** References:* 1) CCITT Recommendation G.711 (very
	 * difficult to follow)* 2) "A New Digital Technique for Implementation of
	 * Any* Continuous PCM Companding Law," Villeret, Michel,* et al. 1973 IEEE
	 * Int. Conf. on Communications, Vol 1,* 1973, pg. 11.12-11.17* 3)
	 * MIL-STD-188-113,"Interoperability and Performance Standards* for
	 * Analog-to_Digital Conversion Techniques,"* 17 February 1987** Input:
	 * Signed 16 bit linear sample* Output: 8 bit ulaw sample
	 */
	/*
	 * Constant, set to <tt>true</tt> if you want to turn on the trap as per the
	 * MIL-STD private final static boolean ZEROTRAP = true;
	 */

	/**
	 * Converts the audio sample from the signed 16 bit linear format to the 8
	 * bit ulaw format.
	 * 
	 * @param sample
	 *            the signed 16 bit linear sample.
	 * @return the 8 bit ulaw sample.
	 */
	private int linear2ulaw(int sample) {
		/* Get the sample into sign-magnitude. */
		int sign = (sample >> 8) & 0x80; /* set aside the sign */
		if (sign != 0)
			sample = -sample; /* get magnitude */
		if (sample > CLIP)
			sample = CLIP; /* clip the magnitude */

		/* Convert from 16 bit linear to ulaw. */
		sample = sample + BIAS;
		int exponent = exp_lut[(sample >> 7) & 0xFF];
		int mantissa = (sample >> (exponent + 3)) & 0x0F;
		int ulawbyte = (~(sign | (exponent << 4) | mantissa)) & 0xff;

		/*
		 * if(ZEROTRAP) { if (ulawbyte <= 0) ulawbyte = 0x02; // optional CCITT
		 * trap }
		 */

		return ulawbyte;
	}

	/**
	 * Sets the playing status of the audio device (<tt>true</tt>, if the audio
	 * device plays the audio).
	 * 
	 * @param playing
	 *            the playing status to set.
	 */
	public void setPlaying(boolean playing) {
		this.playing = playing;
	}

	/**
	 * Sets the mute state of the audio device.
	 * 
	 * @param mute
	 *            the mute state to set.
	 */
	public void setMute(boolean mute) {
		this.mute = mute;
	}

	/**
	 * Sets the volume of the audio stream.
	 * 
	 * @param volume
	 *            the volume to set.
	 */
	public void setVolume(int volume) {
		this.volume = volume;
	}

	/**
	 * Sets the number of channels of the input audio stream.
	 * 
	 * @param channelCount
	 *            the number of channels to set.
	 */
	public void setChannelCount(int channelCount) {
		this.channelCount = channelCount;
	}

	/**
	 * Sets the sample frequency of the input audio stream.
	 * 
	 * @param sampleFrequency
	 *            the sample frequency to set.
	 */
	public void setSampleFrequency(int sampleFrequency) {
		this.sampleStep = (8000 << 16) / sampleFrequency;
	}

	/**
	 * Closes the audio data stream.
	 */
	public void close() throws IOException {
		closed = true;
		synchronized (audioDevice) {
			audioDevice.notifyAll();
		}
		super.close();
	}

	/**
	 * Returns <tt>true</tt>, if any samples haven't been read from the audio
	 * data stream.
	 */
	public boolean isFirstLoop() {
		return firstLoop;
	}

}
