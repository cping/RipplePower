package org.ripple.power.config.task;

public final class TaskEventImpl implements TaskEvent {

	private String task_id;
	private int type;

	private Object result;

	public TaskEventImpl(String task_id, int type) {
		this.task_id = task_id;
	}

	public String getTaskId() {
		return task_id;
	}

	public int getEventType() {
		return type;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

}
