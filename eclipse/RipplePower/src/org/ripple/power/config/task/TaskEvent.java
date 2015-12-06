package org.ripple.power.config.task;

public interface TaskEvent {

	static final int TASK_FINISHED = 1 << 0;

	static final int TASK_NEWTASKS = 1 << 1;

	int getEventType();

	String getTaskId();

	Object getResult();
}
