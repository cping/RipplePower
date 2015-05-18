package org.ripple.power.config.task;

public interface Task {

	String getId();

	void setTaskListener(TaskListener listener);

	void setTaskContext(TaskContext context);

	void execute();

}
