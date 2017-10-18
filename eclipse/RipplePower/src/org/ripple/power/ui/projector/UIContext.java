package org.ripple.power.ui.projector;

import org.ripple.power.timer.SystemTimer;

public final class UIContext {

	public static int nextContextID = 0;

	private ThreadGroup threadGroup;

	private final UIView view;

	private SystemTimer timer;

	public UIContext(UIView view, SystemTimer timer) {
		this.view = view;
		this.timer = timer;
	}

	public ThreadGroup getThreadGroup() {
		if (threadGroup == null || threadGroup.isDestroyed()) {
			threadGroup = new ThreadGroup("LGame-View" + nextContextID);
			nextContextID++;
		}
		return threadGroup;
	}

	public Thread createThread(Runnable runnable) {
		while (true) {
			ThreadGroup currentGroup = getThreadGroup();
			synchronized (currentGroup) {
				if (getThreadGroup() != currentGroup) {
					continue;
				}
				Thread thread = new Thread(currentGroup, runnable, "LGame-View" + nextContextID);
				return thread;
			}
		}
	}

	public void setAnimationThread(Thread thread) {
		if (thread != null) {
			if (threadGroup == null || !threadGroup.parentOf(thread.getThreadGroup())) {
				threadGroup = thread.getThreadGroup();
			}
		}
	}

	public SystemTimer getTimer() {
		return timer;
	}

	public UIView getView() {
		return view;
	}

}
