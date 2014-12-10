package org.ripple.power.ui.todo;

import org.ripple.power.config.task.Task;
import org.ripple.power.config.task.TaskContext;
import org.ripple.power.config.task.TaskListener;
import org.ripple.power.config.task.TaskManager;

import javax.swing.*;
import java.text.DateFormat;
import java.text.ParseException;

public class WarningService {

	private static WarningService _warning;

	private static TaskManager _taskMgr;

	private WarningService() {
		_taskMgr = TaskManager.getInstance();
	}

	public static WarningService getInstance() {
		synchronized (WarningService.class) {
			if (_warning == null) {
				_warning = new WarningService();
			}
		}
		return _warning;
	}

	public void addTodoItem(TodoItem i) {
		Task nt = new Alert(i);
		long now = System.currentTimeMillis();
		long timeout = convert(i.getTimeout());
		_taskMgr.scheduleTask(nt, (timeout - now));
	}

	private long convert(String timeout) {
		long later = 0L;
		DateFormat format = DateFormat.getDateTimeInstance();
		try {
			later = format.parse(timeout).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return later;
	}

	public void cancelAlert(TodoItem i) {
		Task nt = new Alert(i);
		_taskMgr.cancelTask(nt.getId());
	}

	class Alert implements Task {
		private TodoItem _item;

		public Alert(TodoItem i) {
			this._item = i;
		}

		public void execute() {
			NoteWarningDialog dialog = new NoteWarningDialog(_item);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setAlwaysOnTop(true);
			dialog.setLocationRelativeTo(null);
			dialog.setSize(386, 160);
			dialog.setVisible(true);
		}

		public String getId() {
			return _item.getId();
		}

		public void setTaskContext(TaskContext context) {

		}

		public void setTaskListener(TaskListener listener) {

		}
	}
}
