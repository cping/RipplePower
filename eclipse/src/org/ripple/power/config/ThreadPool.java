package org.ripple.power.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {
	private ExecutorService _threadPool;
	private WorkQueue _queue;

	public ThreadPool(int threadCount) {
		_threadPool = Executors.newFixedThreadPool(threadCount);
		_queue = new WorkQueue();
	}

	public void addWork(Runnable work) {
		_queue.enqueue(work);
	}

	public void execute() {
		if (!_queue.isEmpty()) {
			Runnable runnable = _queue.dequeue();
			_threadPool.execute(runnable);
		}
	}

	public void executeAll() {
		while (!_queue.isEmpty()) {
			execute();
		}
	}

	public void destroy() {
		_threadPool.shutdown();
		_queue.clear();
	}
}
