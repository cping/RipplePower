package org.ripple.power.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import org.ripple.power.config.LSystem;
import org.ripple.power.ui.graphics.LColor;
import org.ripple.power.utils.SwingUtils;

public class RPDownloadDialog extends ABaseDialog implements Observer {

	public class Download extends Observable implements Runnable {

		public static final int READY = 0;
		public static final int DOWNLOADING = 1;
		public static final int PAUSED = 2;
		public static final int COMPLETE = 3;
		public static final int CANCELLED = 4;
		public static final int ERROR = 5;

		protected String errorMessage;

		protected int downloaded;

		protected int size;

		protected int state;

		protected URL url;

		protected String urlString;

		protected HttpURLConnection connection;

		protected RandomAccessFile file;

		protected InputStream stream;

		protected static final int MAX_BUFFER_SIZE = 1024;

		public Download(String urlString) {
			downloaded = 0;
			size = -1;
			this.urlString = urlString;
			errorMessage = "";
			initialize();
			state = READY;
		}

		public void initialize() {
			try {
				url = new URL(urlString);
				connection = (HttpURLConnection) url.openConnection();

				connection.setRequestProperty("Range", "bytes=" + downloaded
						+ "-");

				connection.connect();

				if (connection.getResponseCode() / 100 != 2) {
					state = ERROR;
					errorMessage = "Bad response code: "
							+ connection.getResponseCode() + ", "
							+ connection.getResponseMessage();
					stateChanged();
				}

				int contentLength = connection.getContentLength();
				if (contentLength < 1) {
					state = Download.ERROR;
					errorMessage = "Bad content length: " + contentLength;
					stateChanged();
				}
				if (size == -1) {
					size = contentLength;
				}

				file = new RandomAccessFile(LSystem.getCurrentDirectory()
						+ getFileName(), "rw");

				file.seek(downloaded);

				stream = connection.getInputStream();
			} catch (Exception e) {
				state = Download.ERROR;
				e.printStackTrace();
				errorMessage = "Exception: " + e.getMessage();
				stateChanged();
			}
		}

		public void download() {
			if (state == READY) {
				state = DOWNLOADING;
			}
			try {
				while (state == DOWNLOADING) {
					byte[] buffer;
					if (size - downloaded > MAX_BUFFER_SIZE) {
						buffer = new byte[MAX_BUFFER_SIZE];
					} else {
						buffer = new byte[size - downloaded];
					}
					int read = stream.read(buffer);
					if (read == -1) {
						break;
					}
					file.write(buffer, 0, read);
					downloaded += read;
					stateChanged();
				}

				if (state == DOWNLOADING) {
					state = COMPLETE;
				}
			} catch (Exception e) {
				state = Download.ERROR;
				e.printStackTrace();
				errorMessage = "Exception: " + e.getMessage();
				stateChanged();
			}
		}

		public void start() {
			state = DOWNLOADING;
			stateChanged();
			download();
		}

		public void pause() {
			state = PAUSED;
			stateChanged();
		}

		public void resume() {
			state = DOWNLOADING;
			stateChanged();
			download();
		}

		public void cancel() {
			state = CANCELLED;
			stateChanged();
		}

		public void error() {
			state = Download.ERROR;
			stateChanged();
		}

		public int getProgress() {
			return (int) ((float) downloaded / size * 100);
		}

		public int getState() {
			return state;
		}

		public String getStateName() {
			switch (state) {
			case READY:
				return "Ready";
			case DOWNLOADING:
				return "Downloading";
			case Download.ERROR:
				return "Error";
			case PAUSED:
				return "Paused";
			case COMPLETE:
				return "Complete";
			case CANCELLED:
				return "Cancelled";
			default:
				return "Unknown";
			}
		}

		public String getError() {
			return errorMessage;
		}

		public String getSize() {
			if (size / 1024 <= 0) {
				return size + " B";
			} else if (size / (1024 * 1024) <= 0) {
				return (size / 1024) + " KB";
			} else if (size / (1024 * 1024 * 1024) <= 0) {
				return (size / (1024 * 1024)) + " MB";
			} else {
				return (size / (1024 * 1024 * 1024)) + " GB";
			}
		}

		public String getFileName() {
			String fileName = url.getFile();
			return fileName.substring(fileName.lastIndexOf("/") + 1);
		}

		private void stateChanged() {
			setChanged();
			notifyObservers();
		}

		public void run() {
			initialize();
			download();
		}
	}

	public class DownloadPrgBar extends JProgressBar implements
			TableCellRenderer {

		/**
	 * 
	 */
		private static final long serialVersionUID = 1L;

		public DownloadPrgBar(int min, int max) {
			super(min, max);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			if (value instanceof Integer) {
				setValue((int) value);
			} else {
				setValue((int) ((Float) value).floatValue());
			}
			return this;
		}

	}

	public static class DownloadTable extends AbstractTableModel implements
			Observer {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private static final String[] columns = { "Name", "Size", "State",
				"Progress" };
		private static final Class<?>[] colClasses = { String.class,
				String.class, String.class, JProgressBar.class };

		private static ArrayList<Download> downloads = new ArrayList<Download>();

		public void addDownload(Download newDownload) {
			newDownload.addObserver(this);
			downloads.add(newDownload);
			fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
		}

		public Download getDownload(int row) {
			return downloads.get(row);
		}

		public void removeDownload(int row) {
			downloads.remove(row);
			fireTableRowsDeleted(row, row);
		}

		@Override
		public String getColumnName(int row) {
			return columns[row];
		}

		@Override
		public int getRowCount() {
			return downloads.size();
		}

		@Override
		public int getColumnCount() {
			return columns.length;
		}

		public Class<?> getColumnClass(int col) {
			return colClasses[col];
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			switch (columnIndex) {
			case 0:
				return downloads.get(rowIndex).getFileName();
			case 1:
				return downloads.get(rowIndex).getSize();
			case 2:
				return downloads.get(rowIndex).getStateName();
			case 3:
				return downloads.get(rowIndex).getProgress();
			}
			return null;
		}

		@Override
		public void update(Observable o, Object arg) {
			int index = downloads.indexOf(arg);
			fireTableRowsUpdated(index, index);
		}

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private DownloadTable downloadTable;

	private RPCButton btnPause, btnRemove, btnResume, btnCancel, btnDownload;

	private Download selectedDownload;

	private RPTextBox txtAdd;

	private JTable table;

	private boolean isRemoving = false;

	public static void showDialog(Window parent) {
		try {
			RPDownloadDialog dialog = new RPDownloadDialog(parent);
			dialog.pack();
			dialog.setLocationRelativeTo(parent);
			dialog.setVisible(true);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	public RPDownloadDialog(Window parent) {
		super(parent, "Download", ModalityType.MODELESS);
		setIconImage(UIRes.getIcon());
		Dimension dim = new Dimension(590, 400);
		setPreferredSize(dim);
		setSize(dim);
		setResizable(false);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				SwingUtils.close(RPDownloadDialog.this);
			}
		});

		JPanel addPanel = new JPanel();
		txtAdd = new RPTextBox(30);
		addPanel.add(txtAdd);
		RPCButton addButton = new RPCButton("Add Download");
		addButton.setIcon(UIRes.getImage("images/add.png"));
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionAdd();
			}
		});
		addPanel.add(addButton);

		downloadTable = new DownloadTable();
		table = new JTable(downloadTable);
		table.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent e) {
						tableSelectionChanged();
					}
				});
		UIRes.addStyle(table);

		table.getTableHeader().setBackground(LColor.black);
		table.getTableHeader().setForeground(LColor.white);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		DownloadPrgBar progressBar = new DownloadPrgBar(0, 100);
		progressBar.setStringPainted(true);
		table.setDefaultRenderer(JProgressBar.class, progressBar);

		table.setRowHeight((int) progressBar.getPreferredSize().getHeight());

		JPanel downloadsPanel = new JPanel();
		TitledBorder border = BorderFactory.createTitledBorder("Downloads");
		downloadsPanel.setForeground(LColor.white);
		border.setTitleColor(LColor.white);
		downloadsPanel.setBorder(border);
		downloadsPanel.setLayout(new BorderLayout());
		downloadsPanel.add(new JScrollPane(table), BorderLayout.CENTER);

		JPanel buttonsPanel = new JPanel();

		btnPause = new RPCButton("Pause");
		btnPause.setIcon(UIRes.getImage("images/stop.png"));
		btnPause.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionPause();
			}
		});
		btnPause.setEnabled(false);
		btnResume = new RPCButton("Resume");
		btnResume.setIcon(UIRes.getImage("images/reset.png"));
		btnResume.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionResume();
			}
		});
		btnCancel = new RPCButton("Cancel");
		btnCancel.setIcon(UIRes.getImage("images/exit.png"));
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionCancel();
			}
		});
		btnCancel.setEnabled(false);
		btnRemove = new RPCButton("Remove");
		btnRemove.setIcon(UIRes.getImage("images/remove.png"));
		btnRemove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionRemove();
			}
		});
		btnDownload = new RPCButton("Download");
		btnDownload.setIcon(UIRes.getImage("images/download.png"));
		btnDownload.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionStart();
			}
		});
		btnDownload.setEnabled(false);
		buttonsPanel.add(btnPause);
		buttonsPanel.add(btnResume);
		buttonsPanel.add(btnCancel);
		buttonsPanel.add(btnRemove);
		buttonsPanel.add(btnDownload);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(addPanel, BorderLayout.NORTH);
		getContentPane().add(downloadsPanel, BorderLayout.CENTER);
		getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
		
		downloadsPanel.setBackground(UIConfig.dialogbackground);
		buttonsPanel.setBackground(UIConfig.dialogbackground);
		addPanel.setBackground(UIConfig.dialogbackground);
	}

	private void actionPause() {
		selectedDownload.pause();
		updateButtons();
	}

	private void actionResume() {
		selectedDownload.resume();
		updateButtons();
	}

	private void actionCancel() {
		selectedDownload.cancel();
		updateButtons();
	}

	private void actionRemove() {
		isRemoving = true;
		downloadTable.removeDownload(table.getSelectedRow());
		selectedDownload = null;
		updateButtons();
		isRemoving = false;
	}

	private void actionStart() {
		selectedDownload.start();
		updateButtons();
	}

	private void actionAdd() {
		String url = txtAdd.getText();
		if (tryURL(url)) {
			downloadTable.addDownload(new Download(url));
			txtAdd.setText("");
		} else {
			alert("Invalid URL");
		}
	}

	private void tableSelectionChanged() {
		if (selectedDownload != null) {
			selectedDownload.deleteObserver(this);
		}
		if (!isRemoving && table.getSelectedRow() != -1) {
			selectedDownload = downloadTable
					.getDownload(table.getSelectedRow());
			selectedDownload.addObserver(this);
			updateButtons();
		}
	}

	private void updateButtons() {
		if (selectedDownload != null) {
			switch (selectedDownload.state) {
			case Download.READY:
				btnPause.setEnabled(false);
				btnResume.setEnabled(false);
				btnCancel.setEnabled(false);
				btnRemove.setEnabled(true);
				btnDownload.setEnabled(true);
				break;
			case Download.DOWNLOADING:
				btnPause.setEnabled(true);
				btnResume.setEnabled(false);
				btnCancel.setEnabled(false);
				btnRemove.setEnabled(false);
				btnDownload.setEnabled(false);
				break;
			case Download.PAUSED:
				btnPause.setEnabled(false);
				btnResume.setEnabled(true);
				btnCancel.setEnabled(true);
				btnRemove.setEnabled(false);
				btnDownload.setEnabled(false);
				break;
			case Download.ERROR:
				btnPause.setEnabled(false);
				btnResume.setEnabled(false);
				btnCancel.setEnabled(false);
				btnRemove.setEnabled(true);
				btnDownload.setEnabled(false);
				break;
			case Download.COMPLETE:
				btnPause.setEnabled(false);
				btnResume.setEnabled(false);
				btnCancel.setEnabled(false);
				btnRemove.setEnabled(true);
				btnDownload.setEnabled(false);
				break;
			case Download.CANCELLED:
				btnPause.setEnabled(false);
				btnResume.setEnabled(false);
				btnCancel.setEnabled(false);
				btnRemove.setEnabled(true);
				btnDownload.setEnabled(false);
				break;
			default:
				btnPause.setEnabled(false);
				btnResume.setEnabled(false);
				btnCancel.setEnabled(false);
				btnRemove.setEnabled(false);
				btnDownload.setEnabled(false);
				break;
			}
		} else {
			btnPause.setEnabled(false);
			btnResume.setEnabled(false);
			btnCancel.setEnabled(false);
			btnRemove.setEnabled(false);
		}
	}

	private boolean tryURL(String url) {
		if (url.startsWith("http://") || url.startsWith("https://")) {
			return true;
		}
		return false;
	}

	@Override
	public void update(Observable o, Object arg) {
		if (selectedDownload != null && selectedDownload.equals(o)) {
			updateButtons();
		}
	}

}