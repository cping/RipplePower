package org.ripple.power.config.task;

import java.util.LinkedList;

import org.ripple.power.ui.todo.Executor;

public final class TaskManager implements Task {

	private static TaskManager instance = null;

	public static TaskManager getInstance() {
		synchronized (TaskManager.class) {
			if (instance == null) {
				instance = new TaskManager();
			}
		}
		return instance;
	}

	private static String taskId = "TaskManager";

	private Executor executor = null;

	private LinkedList<Task> tasks;

	private Signaler sig;

	private Thread workThread;
	private boolean loop = true;

	private boolean inrun = false;

	private TaskManager() {
		try {
			executor = ExecutorPool.getInstance().getExecutor(0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		sig = new Signaler();

		tasks = new LinkedList<Task>();

		executor.setTask(this);
		executor.begin();

	}

	public String getId() {
		return taskId;
	}

	public int size() {
		synchronized (tasks) {
			return tasks.size() + (inrun ? 1 : 0);
		}
	}

	public void destroy() {
		loop = false;
		executor.cancel();

		workThread.interrupt();
		while (workThread.isAlive()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}

		tasks.clear();
		tasks = null;

		instance = null;
	}

	public void scheduleTask(Task task, long delay) {
		scheduleTask(task, delay, 0);
	}

	public void scheduleTask(Task task, long delay, long period) {
		if (task == null) {
			return;
		}

		if (delay < 0 || period < 0) {
			throw new IllegalArgumentException();
		}

		TimerTask t = new TimerTask(task);
		t.period = period;
		t.nextExecutionTime = System.currentTimeMillis() + delay;

		addTask(t);
	}

	public void cancelTask(String task_id) {
		synchronized (tasks) {
			int size = tasks.size();
			for (int i = 0; i < size; i++) {
				TimerTask t = (TimerTask) tasks.get(i);
				if (t.task.getId().equals(task_id)) {
					tasks.remove(i);
					if (i == 0) {
						workThread.interrupt();
					}
					break;
				}
			}
		}
	}

	public void execute() {
		workThread = Thread.currentThread();

		while (loop) {

			TimerTask task = peekFirstTask();
			if (task == null)
				continue;

			long sleepTime = task.nextExecutionTime
					- System.currentTimeMillis();

			if (sleepTime > 0) {
				int r = _sleep(sleepTime);
				if (r < 0)
					continue;
			}

			runTask(getFirstTask());

		}
	}

	public boolean has(Task task) {
		boolean exist = false;
		synchronized (tasks) {
			int size = tasks.size();
			for (int i = 0; i < size; i++) {
				Task current = tasks.get(i);
				if (current.getId() == null) {
					continue;
				}
				if (current.getId().equals(task.getId())) {
					exist = true;
				}
			}
		}
		return exist;
	}

	private TimerTask peekFirstTask() {
		if (tasks.size() == 0) {
			sig.waitSignal(0);
		}

		TimerTask task = null;
		synchronized (tasks) {
			try {
				task = (TimerTask) tasks.getFirst();
			} catch (Exception e) {
			}
		}

		return task;
	}

	private TimerTask getFirstTask() {
		TimerTask task = null;
		synchronized (tasks) {
			try {
				task = (TimerTask) tasks.remove(0);
			} catch (Exception e) {
			}
		}
		return task;
	}

	private void addTask(TimerTask task) {
		synchronized (tasks) {
			int size = tasks.size();
			if (size == 0) {
				tasks.add(task);
			} else {
				for (int i = size - 1; i >= 0; i--) {
					TimerTask t = (TimerTask) tasks.get(i);
					if (t.nextExecutionTime > task.nextExecutionTime) {
						if (i == 0) {
							tasks.add(0, task);
							workThread.interrupt();
							break;
						} else {
							continue;
						}
					} else {
						tasks.add(i + 1, task);
						break;
					}
				}
			}
		}

		sig.raise(Signaler.SIG1);
	}

	private int _sleep(long timeout) {
		try {
			Thread.sleep(timeout);
		} catch (InterruptedException e) {
			return -1;
		}
		return 0;
	}

	private void runTask(TimerTask task) {
		if (task == null) {
			return;
		}
		try {
			inrun = true;
			task.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			long period = task.period;
			if (period > 0) {
				task.nextExecutionTime = System.currentTimeMillis() + period;
				addTask(task);
			}
			inrun = false;
		}
	}

	public void setTaskContext(TaskContext context) {
	}

	public void setTaskListener(TaskListener listener) {
	}

	private class TimerTask implements Task {

		long nextExecutionTime;

		long period = 0;

		Task task;

		public TimerTask(Task task) {
			this.task = task;
		}

		public void execute() {
			task.execute();
		}

		public void setTaskContext(TaskContext context) {
		}

		public void setTaskListener(TaskListener listener) {
		}

		public String getId() {
			return null;
		}

	}

}
