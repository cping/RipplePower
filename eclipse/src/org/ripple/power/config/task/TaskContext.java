package org.ripple.power.config.task;

public interface TaskContext {

	Object getObject(String key);

	void putObject(String key, Object o);

}
