package org.ripple.power.collection;

import java.util.Iterator;

public class ArrayIterator implements Iterator<Object> {

	final private Object[] items;

	private int index;

	private int length;

	public ArrayIterator(Object[] items) {
		this.items = items;
		this.length = items.length;
	}

	public boolean hasNext() {
		return index < length;
	}

	public Object next() {
		return items[index++];
	}

	public void remove() {
		throw new RuntimeException("not support remove!");
	}

}
