package org.ripple.power.config.task;


public class GeneralTask implements Task {

    private String task_id;

    protected TaskListener listener;
    protected TaskContext context;

    public GeneralTask(String task_id) {
        this.task_id = task_id;
    }

    public String getId() {
        return task_id;
    }


    public final void setTaskListener(TaskListener listener) {
        this.listener = listener;
    }

    public final void setTaskContext(TaskContext context) {
        this.context = context;
    }

    public void execute() {
        if (listener != null) {
            listener.handleTaskEvent(new TaskEventImpl(task_id,
                    TaskEvent.TASK_FINISHED));
        }
    }

}
