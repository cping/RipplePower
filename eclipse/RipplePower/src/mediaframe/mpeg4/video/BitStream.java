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
package mediaframe.mpeg4.video;

import java.io.IOException;
import java.io.EOFException;
import java.io.InputStream;

/**
 * The <code>BitStream</code> class implements 'bit' reading functions from the
 * MPEG4 input stream.
 */
public final class BitStream {

	/** The MPEG4 input stream. */
	private InputStream is;
	/** The local buffer of 64 bits. */
	private long buffer = 0;
	/** The pointer into the local buffer. */
	private byte pointer = 0;

	/**
	 * Constructs an <code>BitStream</code> object using the specified MPEG4
	 * input stream.
	 * 
	 * @param is
	 *            the MPEG4 input stream.
	 */
	public BitStream(InputStream is) {
		super();
		this.is = is;
	}

	/**
	 * Reads next bit from the input stream and return it as a boolean value.
	 * 
	 * @return <tt>true</tt> if the bit equals '1', <tt>false</tt> otherwise.
	 * @throws IOException
	 *             raises if an error occurs.
	 */
	public boolean next_bit() throws IOException {
		return next_bits(1) == 1;
	}

	/**
	 * Reads next 'n' (up to 64) bits from the input stream.
	 * 
	 * @param n
	 *            the number of bits to read.
	 * @return the 'n' bits from the input stream.
	 * @throws IOException
	 *             raises if an error occurs.
	 */
	public long next_bits(int n) throws IOException {
		if (n > 64) {
			throw new IOException("Wrong number of bits to read");
		}
		while ((pointer - n) < 0) {
			read_byte();
		}
		long mask = -1L >>> (64 - n);
		long result = (buffer >> (pointer - n)) & mask;
		/*
		 * System.out.println("n: " + n); System.out.println("pointer: " +
		 * pointer); System.out.println("Mask: " + mask);
		 * System.out.println("Result: " + result);
		 * System.out.println("Buffer: " + result);
		 */
		pointer -= n;
		return result;
	}

	/**
	 * Returns 'n' bits (up to 64) into the input stream.
	 * 
	 * @param n
	 *            the number of bits to return.
	 */
	public void unget_bits(int n) throws IOException {
		if ((pointer + n) > 64) {
			throw new IOException("Wrong number of bits to unget");
		}
		pointer += n;
	}

	/**
	 * Returns next 'n' (up to 64) bits from the input stream starting on byte
	 * boundary. Does't change the pointer into local buffer.
	 * 
	 * @param n
	 *            the number of bits to read.
	 * @return the 'n' bits from the input stream.
	 * @throws IOException
	 *             raises if an error occurs.
	 */
	public long nextbits_byteAligned(int n) throws IOException {
		int alignSkip = pointer % 8;
		if (alignSkip == 0) {
			alignSkip = 8;
		}
		skip_bits(alignSkip);
		long result = next_bits(n);
		unget_bits(alignSkip + n);
		return result;
	}

	/**
	 * Reads one byte from the input stream into the local buffer.
	 * 
	 * @throws IOException
	 *             raises if an error occurs.
	 * @throws EOFException
	 *             raises if the end of the file has been reached.
	 */
	private void read_byte() throws IOException {
		long c = is.read();
		if (c == -1)
			throw new EOFException();

		buffer = (buffer << 8) + (c & 0xff);
		pointer += 8;
	}

	/**
	 * Skips 'n' bits from the input stream.
	 * 
	 * @param n
	 *            the number of bits to skip.
	 * @throws IOException
	 *             raises if an error occurs.
	 */
	public void skip_bits(int n) throws IOException {
		while ((pointer - n) < 0) {
			read_byte();
		}
		pointer -= n;
	}

	/**
	 * Skips one bit from the input stream.
	 * 
	 * @throws IOException
	 *             raises if an error occurs.
	 */
	public void skip_bit() throws IOException {
		while (pointer <= 0) {
			read_byte();
		}
		pointer--;
	}

	/**
	 * Skips one 'marker' (equal to one) bit from the input stream.
	 * 
	 * @throws IOException
	 *             raises if an error occurs.
	 */
	public void marker_bit() throws IOException {
		while (pointer <= 0) {
			read_byte();
		}
		/*
		 * if(((buffer >> (pointer - 1)) & 1) != 1) { throw new
		 * IOException("Stream error: the market bit should be equal to '1'"); }
		 */
		pointer--;
	}

	/**
	 * Returns <tt>true</tt> if the current position of the bitstream is on byte
	 * boundary.
	 */
	public boolean isByteAligned() {
		return (pointer % 8) == 0;
	}

	/**
	 * Aligns the current position of the bitstream on byte boundary.
	 * 
	 * @throws IOException
	 *             raises if an error occurs.
	 */
	public void byteAlign() throws IOException {
		skip_bits(pointer % 8);
	}

	/**
	 * Finds the next start code (23 zeros followed by a single bit with the
	 * value one) in the bitstream.
	 * 
	 * @throws IOException
	 *             raises if an error occurs.
	 */
	public void next_start_code() throws IOException {
		boolean found = false;
		int skipped = pointer % 8;
		byteAlign();
		while (!found) {
			while (!(next_bits(8) == 0)) {
				skipped += 8;
			}
			skipped += 8;
			if (next_bits(16) == 1) {
				skipped += 16;
				found = true;
			} else {
				unget_bits(16);
			}
		}
		// System.out.println("next_start_code() skipped " + (skipped - 24) +
		// " bits");
	}

	/**
	 * Gets the next start code value in the bitstream.
	 * 
	 * @return the next start code value.
	 * @throws IOException
	 *             raises if an error occurs.
	 */
	public int get_next_start_code() throws IOException {
		next_start_code();
		return (int) next_bits(8);
	}

	public boolean is_data_in_next_byte() throws IOException {
		int alignBits = pointer % 8;
		if (alignBits == 0) {
			alignBits = 8;
		}
		long bitstring = next_bits(alignBits);
		unget_bits(alignBits);
		if (bitstring == ((-1 >>> (64 - alignBits)) >>> 1)) {
			return false;
		}
		return true;
	}

	/**
	 * Finds the next resync market (16 zeros followed by a single bit with the
	 * value one) in the bitstream.
	 * 
	 * @throws IOException
	 *             raises if an error occurs.
	 */
	public void next_resyncmarker() throws IOException {
		boolean found = false;
		byteAlign();
		while (!found) {
			while (!(next_bits(8) == 0)) {
			}
			if (next_bits(9) == 1) {
				found = true;
			} else {
				unget_bits(9);
			}
		}
	}

	public void print_next_bits(int n) throws IOException {
		String stream = Long.toBinaryString(next_bits(n));
		for (int i = stream.length(); i < n; i++) {
			stream = '0' + stream;
		}
		System.out.println("bitstream = " + stream);
		unget_bits(n);
	}
}
