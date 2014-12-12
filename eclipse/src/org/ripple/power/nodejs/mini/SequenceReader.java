package org.ripple.power.nodejs.mini;

import java.io.IOException;
import java.io.Reader;


public class SequenceReader extends Reader {

	private final Reader	first;
	private final Reader	later;
	private boolean			second	= false;

	public SequenceReader(final Reader first, final Reader later) {
		super();
		this.first = first;
		this.later = later;
	}

	@Override
	public void close() throws IOException {
		this.first.close();
		this.later.close();
	}

	@Override
	public int read(final char[] cbuf, final int off, final int len) throws IOException {
		if (this.second) {
			final int res = this.later.read(cbuf, off, len);
			if (res == -1) {
				this.later.close();
				return -1;
			}
			return res;
		}
		final int res = this.first.read(cbuf, off, len);
		if (res == -1) {
			this.first.close();
			this.second = true;
			return read(cbuf, off, len);
		}
		return res;
	}

}
