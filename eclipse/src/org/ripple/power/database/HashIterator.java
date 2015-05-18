package org.ripple.power.database;

import java.util.Iterator;

public class HashIterator implements Iterator<HashBytes> {

	private int position;
	private int size;
	private int iterations;
	private HashBytes currentBytes;
	private HashBytes[] bytestable;

	public HashIterator(HashBytes[] bytestable, int size) {
		this.bytestable = bytestable;
		this.size = size;
		this.position = -1;
	}

	@Override
	public boolean hasNext() {
		return iterations < size;
	}

	@Override
	public HashBytes next() {
		if (currentBytes == null || currentBytes.next == null) {
			while (bytestable[++position] == null)
				;
			currentBytes = bytestable[position];
		} else {
			currentBytes = currentBytes.next;
		}
		iterations++;
		return currentBytes;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Not supported.");
	}
}
