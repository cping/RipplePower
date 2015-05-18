package org.ripple.power.config;

import java.util.LinkedList;
import java.util.NoSuchElementException;

public class WorkQueue {

	private LinkedList<Runnable> _queue;

	public WorkQueue() {
		_queue = new LinkedList<Runnable>();
	}

	public void enqueue(Runnable work) {
		_queue.add(work);
	}

	public Runnable dequeue() throws NoSuchElementException {
		return _queue.removeFirst();
	}

	public boolean isEmpty() {
		return _queue.isEmpty();
	}

	public int size() {
		return _queue.size();
	}

	public void clear() {
		_queue.clear();
	}
}
