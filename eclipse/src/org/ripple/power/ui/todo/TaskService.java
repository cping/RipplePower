package org.ripple.power.ui.todo;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.ripple.power.config.task.Task;
import org.ripple.power.config.task.TaskContext;
import org.ripple.power.config.task.TaskListener;
import org.ripple.power.config.task.TaskManager;
import org.ripple.power.email.MailSender;
import org.ripple.power.email.SimpleTextMail;
import org.ripple.power.utils.DateUtils;

public class TaskService {
	private TaskService() {
		taskMgr = TaskManager.getInstance();
	}

	private static TaskService alerm;
	private static TaskManager taskMgr;

	public static TaskService getInstance() {
		synchronized (TaskService.class) {
			if (alerm == null) {
				alerm = new TaskService();
			}
		}
		return alerm;
	}

	public void sendMail(SimpleTextMail mail) {
		Task carrier = new Carrier(mail);
		taskMgr.scheduleTask(carrier, 0);
	}

	public void scheduleItem(TodoItem item) {
		Task nt = new Alert(item);
		long now = System.currentTimeMillis();
		long timeout = DateUtils.convert(item.getTimeout());
		long delay = timeout - now;
		if (delay > 5000) {
			taskMgr.scheduleTask(nt, delay);
		}
	}

	public boolean isItemScheduled(TodoItem item) {
		return taskMgr.has(new Alert(item));
	}

	public void cancelSchedule(TodoItem item) {
		Task nt = new Alert(item);
		taskMgr.cancelTask(nt.getId());
	}

	class Carrier implements Task {
		private SimpleTextMail mail;

		public Carrier(SimpleTextMail mail) {
			this.mail = mail;
		}

		public void execute() {
			MailSender sender = new MailSender(mail);
			boolean st = sender.send();
			if (st) {
				JOptionPane.showMessageDialog(null, "mail is sent",
						"mail is sent!!", JOptionPane.INFORMATION_MESSAGE);
			}
		}

		public String getId() {
			return null;
		}

		public void setTaskContext(TaskContext context) {

		}

		public void setTaskListener(TaskListener listener) {

		}

	}

	class Alert implements Task {
		private TodoItem item;

		public Alert(TodoItem item) {
			this.item = item;
		}

		public void execute() {
			NoteWarningDialog dialog = new NoteWarningDialog(item);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setAlwaysOnTop(true);
			dialog.setLocationRelativeTo(null);
			dialog.setSize(396, 180);
			dialog.setVisible(true);
		}

		public String getId() {
			String id = item.getId();
			return id == null ? "" : id;
		}

		public void setTaskContext(TaskContext context) {

		}

		public void setTaskListener(TaskListener listener) {

		}
	}
}
