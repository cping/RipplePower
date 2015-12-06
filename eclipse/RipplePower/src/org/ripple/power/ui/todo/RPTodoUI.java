package org.ripple.power.ui.todo;

import org.ripple.power.config.LSystem;
import org.ripple.power.txns.Updateable;
import org.ripple.power.ui.RPRippledMemoDialog;
import org.ripple.power.ui.RPSelectWalletDialog;
import org.ripple.power.ui.UIMessage;
import org.ripple.power.ui.UIRes;
import org.ripple.power.ui.graphics.LImage;
import org.ripple.power.ui.view.ABaseDialog;
import org.ripple.power.utils.SwingUtils;
import org.ripple.power.wallet.WalletItem;

import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ToolTipManager;

public class RPTodoUI extends ABaseDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private NoteNewTaskDialog newTaskDialog;

	private NotePreferenceDialog preferenceDialog;

	private JPopupMenu pmOnItem;

	private JMenuBar mbar;

	private JToolBar toolbar;

	private JTextField filter;

	private JScrollPane scroller;

	private MouseListener popupListener;

	private FilterableList ftodolist;

	public JMenuBar getMenubar() {
		return mbar;
	}

	public JToolBar getToolbar() {
		return toolbar;
	}

	public JTextField getFilter() {
		return filter;
	}

	public FilterableList getTodoList() {
		return ftodolist;
	}

	public Window getWindow() {
		return this;
	}

	public RPTodoUI(String title) {
		super(LSystem.applicationMain, title, ModalityType.MODELESS);
	}

	class ListItemListener extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				JList<?> list = (JList<?>) e.getSource();
				int index = list.locationToIndex(e.getPoint());
				TodoItem item = (TodoItem) list.getModel().getElementAt(index);
				NoteEditTaskDialog editTaskDialog = new NoteEditTaskDialog(
						RPTodoUI.this, "Edit exist task", item);
				editTaskDialog.setLocationRelativeTo(null);
				editTaskDialog.setVisible(true);
			}
		}
	}

	class PopupListener extends MouseAdapter implements ActionListener {
		JPopupMenu popupMenu;
		Component selected;
		Point point;

		PopupListener(JPopupMenu popupMenu) {
			this.popupMenu = popupMenu;
			initEventHandlers();
		}

		private void initEventHandlers() {
			Component[] menus = popupMenu.getComponents();
			for (Component item : menus) {
				if (!(item instanceof JPopupMenu.Separator)) {
					((JMenuItem) item).addActionListener(this);
				}
			}
		}

		public void mousePressed(MouseEvent e) {
			showPopupMenu(e);
		}

		public void mouseReleased(MouseEvent e) {
			showPopupMenu(e);
		}

		private void showPopupMenu(MouseEvent e) {
			if (e.isPopupTrigger()) {
				selected = e.getComponent();
				point = e.getPoint();
				if (selected instanceof JList) {
					JList<?> list = (JList<?>) selected;
					int index = list.locationToIndex(point);
					list.setSelectedIndex(index);
				}
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}

		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();

			JList<?> list = (JList<?>) selected;
			int index = list.locationToIndex(point);
			final TodoItem item = (TodoItem) list.getModel()
					.getElementAt(index);

			if (command.equals("Delete item")) {
				if (UIRes.showConfirmMessage(RPTodoUI.this, "Delete item",
						"Are you sure you want to delete item?", UIMessage.ok,
						UIMessage.cancel) == 0) {
					deleteItem(item);
				}
			} else if (command.equals("Edit item")) {
				NoteEditTaskDialog editTaskDialog = new NoteEditTaskDialog(
						RPTodoUI.this, "Edit exist task", item);
				editTaskDialog.setLocationRelativeTo(null);
				editTaskDialog.setVisible(true);
			} else if (command.equals("Mail this item")) {
				NoteNewMailDialog newMailDialog = new NoteNewMailDialog(
						RPTodoUI.this, "Send this item ", item);
				newMailDialog.setLocationRelativeTo(null);
				newMailDialog.setVisible(true);
			} else if (command.equals("Ripple this item")) {
				RPSelectWalletDialog.showDialog("Ripple this item",
						RPTodoUI.this, new Updateable() {

							@Override
							public void action(Object o) {
								if (o != null && o instanceof WalletItem) {
									WalletItem wallet_item = (WalletItem) o;
									RPRippledMemoDialog.showDialog(
											"Ripple TODO", RPTodoUI.this,
											wallet_item.getPublicKey(),
											item.toText());
								}
							}
						});
			}
		}
	}

	private String formatTooltip(TodoItem item) {
		StringBuffer formatted = new StringBuffer();

		formatted.append("<html>");
		formatted.append("<b>Description : </b>").append(item.getDesc())
				.append(", ");
		formatted.append("<b>Status : </b>").append(item.getStatus())
				.append(", ");
		formatted.append("<b>Timeout : </b>").append(item.getTimeout());
		formatted.append("</html>");

		return formatted.toString();
	}

	private void initTasks() {
		TodoDataBase ds = TodoDataBase.getInstance();
		TaskService ts = TaskService.getInstance();

		List<TodoItem> list = ds.getAllItems();
		for (TodoItem item : list) {
			ts.scheduleItem(item);
		}
	}

	private void initContentList() {
		TodoDataBase ds = TodoDataBase.getInstance();
		List<TodoItem> tlist = ds.getAllItems();

		initTasks();

		ftodolist = new FilterableList();
		FilterableListModel model = ftodolist.getContents();
		for (TodoItem item : tlist) {
			model.addElement(item);
		}

		ftodolist.setCellRenderer(new TodoListCellRenderer());
		ftodolist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scroller = new JScrollPane(ftodolist);

		ToolTipManager.sharedInstance().registerComponent(ftodolist);

		ftodolist.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				JList<?> list = (JList<?>) e.getSource();
				int index = list.locationToIndex(e.getPoint());
				if (index <= 0)
					return;
				TodoItem item = (TodoItem) list.getModel().getElementAt(index);
				list.setToolTipText(null);
				String tooltip = formatTooltip(item);
				list.setToolTipText(tooltip);
			}
		});

		ftodolist.addMouseListener(new ListItemListener());
		ftodolist.addMouseListener(popupListener);
	}

	private void initSearchBox() {
		toolbar = new JToolBar();
		filter = new JTextField();
		ftodolist.installFilterField(filter);
		toolbar.add(filter, BorderLayout.CENTER);
		toolbar.setVisible(false);

		KeyListener searchTrigger = new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_F
						&& e.getModifiers() == KeyEvent.CTRL_MASK
						|| e.getKeyCode() == KeyEvent.VK_SLASH) {
					if (toolbar.isVisible()) {
						toolbar.setVisible(false);
					} else {
						toolbar.setVisible(true);
						filter.requestFocus();
					}
				}
			}

			public void keyReleased(KeyEvent e) {
			}

			public void keyTyped(KeyEvent e) {
			}
		};

		KeyListener escapeTrigger = new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					if (toolbar.isVisible()) {
						filter.setText("");
						toolbar.setVisible(false);
						RPTodoUI.this.requestFocus();
					}
				}
			}

			public void keyReleased(KeyEvent e) {
			}

			public void keyTyped(KeyEvent e) {
			}
		};

		filter.addKeyListener(escapeTrigger);

		addKeyListener(searchTrigger);
		ftodolist.addKeyListener(searchTrigger);
	}

	private void initMenuBar() {
		mbar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		fileMenu.setIcon(UIRes.getImage("images/file.gif"));
		JMenuItem newTask = new JMenuItem("New task",
				UIRes.getImage("images/schedule_new.gif"));
		newTask.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				InputEvent.CTRL_MASK));
		newTask.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (newTaskDialog == null) {
					newTaskDialog = new NoteNewTaskDialog(RPTodoUI.this,
							"New Task");
				}
				newTaskDialog.setLocationRelativeTo(null);
				newTaskDialog.setVisible(true);
			}
		});

		JMenuItem exit = new JMenuItem("Exit",
				UIRes.getImage("images/exit.png"));
		exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4,
				InputEvent.ALT_MASK));
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int y = UIRes.showConfirmMessage(RPTodoUI.this, "Exit",
						"Confirm Exit Todo Frame ?", UIMessage.ok,
						UIMessage.cancel);
				if (y == 0) {
					SwingUtils.close(RPTodoUI.this);
				}
			}

		});

		JMenu export = new JMenu("Export...");
		export.setIcon(UIRes.getImage("images/export.gif"));

		JMenuItem exportHtml = new JMenuItem("Export HTML",
				UIRes.getImage("images/exptohtml.gif"));

		exportHtml.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				List<TodoItem> list = new LinkedList<TodoItem>();
				for (int i = 0; i < ftodolist.getContents().getSize(); i++) {
					list.add((TodoItem) ftodolist.getContents().getElementAt(i));
				}
				Exporter exporter = new HTMLExporter(list, "temp.html");
				exporter.store();
			}
		});

		export.add(exportHtml);

		fileMenu.add(newTask);
		fileMenu.add(export);
		fileMenu.add(exit);

		JMenu editMenu = new JMenu("Edit");
		editMenu.setIcon(UIRes.getImage("images/edit.gif"));

		JMenuItem settings = new JMenuItem("Preference",
				UIRes.getImage("images/customize.gif"));
		settings.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,
				InputEvent.CTRL_MASK));

		settings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (preferenceDialog == null) {
					preferenceDialog = new NotePreferenceDialog(RPTodoUI.this,
							"Preference");
				}
				preferenceDialog.setLocationRelativeTo(null);
				preferenceDialog.setVisible(true);
			}
		});

		editMenu.add(settings);

		JMenuItem search = new JMenuItem("Search",
				UIRes.getImage("images/filter.gif"));
		search.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,
				InputEvent.CTRL_MASK));
		search.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toolbar.setVisible(true);
			}
		});

		editMenu.add(search, 0);

		mbar.add(fileMenu);
		mbar.add(editMenu);

		setJMenuBar(mbar);
	}

	private void initPopupMenu() {
		JMenuItem delMenuItem = new JMenuItem("Delete item",
				UIRes.getImage("images/delete.gif"));
		JMenuItem editMenuItem = new JMenuItem("Edit item",
				UIRes.getImage("images/edit2.gif"));
		JMenuItem mailMenuItem = new JMenuItem("Mail this item",
				UIRes.getImage("images/mail.gif"));
		JMenuItem rippleMenuItem = new JMenuItem("Ripple this item",
				new ImageIcon(LImage.createImage("icons/ripple.png")
						.scaledInstance(16, 16).getBufferedImage()));

		pmOnItem = new JPopupMenu("Edit menu");
		pmOnItem.add(delMenuItem);
		pmOnItem.add(editMenuItem);
		pmOnItem.add(mailMenuItem);
		pmOnItem.add(rippleMenuItem);

		popupListener = new PopupListener(pmOnItem);
	}

	public void initUI() {
		setIconImage(UIRes.getIcon());
		initPopupMenu();
		initContentList();
		initMenuBar();
		initSearchBox();

		getContentPane().add(toolbar, BorderLayout.NORTH);
		getContentPane().add(scroller);
		Dimension dim = new Dimension(400, 650);
		setPreferredSize(dim);
		setSize(dim);

		setResizable(false);
		setLocationRelativeTo(null);
		setFocusable(true);
		setVisible(true);
	}

	public void deleteItem(TodoItem item) {
		FilterableListModel model = ftodolist.getContents();

		for (int i = 0; i < model.getSize(); i++) {
			TodoItem titem = (TodoItem) model.getElementAt(i);
			if (titem.getId().equals(item.getId())) {
				model.removeElement(i);
				break;
			}
		}

		TodoDataBase ds = TodoDataBase.getInstance();
		boolean res = ds.removeItem(item);

		TaskService as = TaskService.getInstance();
		if (as.isItemScheduled(item)) {
			as.cancelSchedule(item);
		}

		if (!res) {
			info(ds.getMessage());
		}
	}

	public void refreshModel(List<TodoItem> list) {
		FilterableListModel model = ftodolist.getContents();
		model.clear();
		for (TodoItem item : list) {
			model.addElement(item);
		}
	}

}
