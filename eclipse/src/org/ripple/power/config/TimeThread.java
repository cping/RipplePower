package org.ripple.power.config;

import java.util.Date;

public abstract class TimeThread {

	private final Thread thread;
	private final long sleepTime;

	public TimeThread(String name, Date dueDate) {
		this.sleepTime = Math.max(0,
				dueDate.getTime() - System.currentTimeMillis());
		this.thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					TimeThread.this.started();
					Thread.sleep(sleepTime);
					TimeThread.this.run();
				} catch (InterruptedException e) {
					TimeThread.this.interrupted();
				} finally {
					TimeThread.this.finished();
				}
			}
		}, name);
	}

	public TimeThread(String name, long timespan) {
		this(name, new Date(System.currentTimeMillis() + timespan));
	}

	public final TimeThread start() {
		this.thread.start();
		return this;
	}

	public void stop() {
		this.thread.interrupt();
	}

	public void started() {
	}

	public abstract void run();

	public void interrupted() {
	}

	public void finished() {
	}
}