package org.ripple.power.config.task;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.ripple.power.ui.todo.Executor;

public final class ExecutorPool {

	private static ExecutorPool instance;
	private static int cpus;

	static {
		cpus = Runtime.getRuntime().availableProcessors();
	}

	public static Class<?> getCallerClass(int level) {
		StackTraceElement[] stack = (new Throwable()).getStackTrace();
		Class<?> o = null;
		try {
			o = Class.forName(stack[level].getClassName());
		} catch (ClassNotFoundException e1) {

		}
		return o;
	}

	public static ExecutorPool getInstance() {
		synchronized (ExecutorPool.class) {
			if (instance == null) {
				instance = new ExecutorPool();
			}
		}
		return instance;
	}

	private String name;
	private int total;
	private List<Executor> executors;
	private Set<Executor> actives;

	private ExecutorPool() {
		int exp = 8;
		this.total = cpus * _exp2(exp);
		this.name = "task_pool";
		executors = new ArrayList<Executor>(total);
		actives = new HashSet<Executor>(total);
	}

	public int total() {
		return available() + active();
	}

	public int available() {
		synchronized (executors) {
			return executors.size();
		}
	}

	public int active() {
		synchronized (actives) {
			return actives.size();
		}
	}

	public Executor getExecutor(long timeout) throws TimeoutException {

		synchronized (executors) {

			ExecutorImpl E;

			if (!executors.isEmpty()) {
				E = (ExecutorImpl) executors.remove(0);
				synchronized (actives) {
					actives.add(E);
				}
				return E;
			} else {
				int count = active();
				if (count < total) {
					String _name = name + "." + count;
					E = new ExecutorImpl(_name, 0);
					synchronized (actives) {
						actives.add(E);
					}
					E.start();
					while (!E.isAlive()) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
						}
					}
					return E;
				}
			}

			if (timeout < 0) {
				return null;
			}

			long start = System.currentTimeMillis();
			try {
				executors.wait(timeout);
			} catch (InterruptedException e) {
				return null;
			}
			long now = System.currentTimeMillis();
			long timeSoFar = now - start;

			if (timeSoFar > timeout) {
				throw new TimeoutException();
			}

			return getExecutor(timeout - timeSoFar);

		}
	}

	private void putExecutor(Executor e) {
		synchronized (actives) {
			actives.remove(e);
		}

		synchronized (executors) {
			executors.add(e);
			executors.notifyAll();
		}
	}

	public void destroy() {
		Executor E;

		synchronized (actives) {
			Iterator<Executor> it = actives.iterator();
			while (it.hasNext()) {
				E = it.next();
				E.cancel();
				it.remove();
			}
		}
		actives = null;

		synchronized (executors) {
			while (!executors.isEmpty()) {
				E = executors.remove(0);
				E.cancel();
			}
		}
		executors = null;
		instance = null;
	}

	private class ExecutorImpl extends Thread implements Executor {

		private long timeout = 0;

		private Signaler loopEvent;
		private Signaler exitEvent;

		private Task task;

		public ExecutorImpl(String name) {
			super(name);
			loopEvent = new Signaler();
			exitEvent = new Signaler();
		}

		public ExecutorImpl(String name, long timeout) {
			this(name);
			this.timeout = timeout < 0 ? 0 : timeout;
		}

		public void setTask(Task task) {
			this.task = task;
		}

		public void begin() {
			loopEvent.raise(Signaler.SIG1);
		}

		public void cancel() {
			loopEvent.raise(Signaler.SIG0);
			exitEvent.waitSignal(1000);
			this.interrupt();
		}

		public void run() {
			boolean loop = true;
			while (loop) {
				int e = loopEvent.waitSignal(timeout);
				switch (e) {
				case Signaler.SIG0:
					loop = false;
					handleExitEvent();
					break;
				case Signaler.SIG1:
					handleWakeEvent();
					break;
				case Signaler.SIG2:
					handleTimeoutEvent();
					break;
				default:
					break;
				}
				Thread.yield();
			}

			exitEvent.raise(Signaler.SIG0);
		}

		private void handleWakeEvent() {
			if (task != null) {
				String name = super.getName();
				super.setName(name + "(" + task.getId() + ")");
				try {
					task.execute();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					task = null;
					super.setName(name);
					putExecutor(this);
				}
			}
		}

		private void handleExitEvent() {

		}

		private void handleTimeoutEvent() {

		}

	}

	private static int _exp2(int n) {
		int r = 1;
		for (int i = 0; i < n; i++) {
			r <<= 1;
		}
		return r;
	}

}
