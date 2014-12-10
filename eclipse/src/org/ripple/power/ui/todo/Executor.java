package org.ripple.power.ui.todo;

import org.ripple.power.config.task.Task;


public interface Executor {

    void setTask(Task task);

    void begin();

    void cancel();

}
