package org.ripple.power.collection;

import java.util.concurrent.ConcurrentHashMap;

public class LRUMap<K, V> extends ConcurrentHashMap<K, V> {

	private static final long serialVersionUID = 1L;

	protected final int maxEntries;

	public LRUMap(int initialEntries, int maxEntries) {
		super(initialEntries, 0.8f, 3);
		this.maxEntries = maxEntries;
	}

}