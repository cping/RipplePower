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

import java.io.IOException;
import java.io.EOFException;
import java.io.InputStream;

/**
 * <code>DataStream</code>
 */
public final class DataStream {
	
	/** The MPEG4 input stream. */
	private InputStream is;
	/** The current offset (position) in the stream. */
	private long offset = 0;
	
	/**
	 * Constructs an <code>DataStream</code> object using the specified MPEG4 input stream.
	 * @param is the MPEG4 input stream.
	 */
	public DataStream(InputStream is) {
		super();
		this.is = is;
	}

	public long readBytes(int n) throws IOException {
		int c = -1;
		long result = 0;
		while((n-- > 0) && ((c = is.read()) != -1)) {
			result <<= 8;
			result += c & 0xff;
			offset ++;
		}
		if(c == -1) 
			throw new EOFException();
		return result;
	}

	public String readString(int n) throws IOException {
		char c = (char)-1;
		StringBuffer sb = new StringBuffer();
		while((n-- > 0) && ((c = (char)is.read()) != -1)) {
			sb.append(c);
			offset ++;
		}
		if(c == -1) {
			throw new EOFException();
		}
		return sb.toString();
	}
	
	public void skipBytes(long n) throws IOException {
		offset += n;
		is.skip(n);
	}

	public long getOffset() {
		return offset;
	}

}
