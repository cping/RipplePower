package org.ripple.power.config;

public class ThreadPoolService {

	private static ThreadPool _threadPool = new ThreadPool(10);

	public static synchronized void addWork(Runnable runnable) {
		_threadPool.addWork(runnable);
	}

	public static synchronized void execute() {
		_threadPool.execute();
	}

	public static synchronized void exectueAll() {
		_threadPool.executeAll();
	}
}
