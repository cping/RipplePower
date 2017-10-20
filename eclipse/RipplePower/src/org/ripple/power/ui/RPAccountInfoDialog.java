package org.ripple.power.ui;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.ripple.power.config.LSystem;
import org.ripple.power.helper.HelperDialog;
import org.ripple.power.helper.HelperWindow;
import org.ripple.power.i18n.LangConfig;
import org.ripple.power.txns.AccountFind;
import org.ripple.power.txns.AccountInfo;
import org.ripple.power.txns.AccountLine;
import org.ripple.power.txns.NameFind;
import org.ripple.power.txns.TransactionTx;
import org.ripple.power.txns.Updateable;
import org.ripple.power.ui.table.AddressTable;
import org.ripple.power.ui.view.ABaseDialog;
import org.ripple.power.ui.view.RPLabel;
import org.ripple.power.ui.view.RPTextBox;
import org.ripple.power.ui.view.RPToast;
import org.ripple.power.ui.view.WaitDialog;
import org.ripple.power.ui.view.RPToast.Style;
import org.ripple.power.utils.GraphicsUtils;
import org.ripple.power.utils.SwingUtils;

public class RPAccountInfoDialog extends ABaseDialog implements WindowListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private javax.swing.JSeparator _spOne;
	private javax.swing.JSeparator _spTwo;
	private RPCButton _loadButton;
	private RPCButton _memoButton;
	private RPCButton _hashButton;
	private RPCButton _exitButton;
	private RPLabel _addressLabel;
	private RPLabel _assetsLabel;
	private RPLabel _issuedLabel;
	private RPLabel _booksLabel;
	private RPTextBox _addressText;
	private RPTextBox _addressNameText;
	private AddressTable _tableOne;
	private AddressTable _tableTwo;
	private AddressTable _tableThree;
	private javax.swing.JScrollPane _panelOne;
	private javax.swing.JScrollPane _panelTwo;
	private javax.swing.JScrollPane _panelThree;
	private ArrayList<AccountLine> _accountLineItems = new ArrayList<AccountLine>();

	private ArrayList<AccountLine> _accountLineItems2 = new ArrayList<AccountLine>();

	private ArrayList<String> _accountLineItems3 = new ArrayList<String>();

	private AccountInfo _accountinfo = new AccountInfo();
	private JPopupMenu _popMenu = new JPopupMenu();

	private class infoMouseListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			if (e.isPopupTrigger()) {
				_popMenu.show((Component) e.getSource(), e.getX(), e.getY());
				// 双击触发
			} else if (e.getClickCount() > 1) {
				int size = _tableThree.getSelectedRow();
				if (_accountinfo.transactions.size() > 0 && size > -1) {
					UIMessage.infoMessage(RPAccountInfoDialog.this, "No:" + size);
					TransactionTx tx = _accountinfo.getTxs("Payment").get(size);
					RPHashInfoDialog.showDialog(RPAccountInfoDialog.this, tx);
				} else {
					RPHashInfoDialog.showDialog(RPAccountInfoDialog.this);
				}
			}
		}

		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger()) {
				_popMenu.show((Component) e.getSource(), e.getX(), e.getY());
			}
		}
	}

	class AccountTableModel extends AbstractTableModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private String[] columnNames;

		private Class<?>[] columnClasses;

		public AccountTableModel(String[] columnNames, Class<?>[] columnClasses) {
			super();
			if (columnNames.length != columnClasses.length)
				throw new IllegalArgumentException("Number of names not same as number of classes");
			this.columnNames = columnNames;
			this.columnClasses = columnClasses;
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public Class<?> getColumnClass(int column) {
			return columnClasses[column];
		}

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}

		@Override
		public int getRowCount() {
			return _accountLineItems.size();
		}

		@Override
		public Object getValueAt(int row, int column) {
			if (row > getRowCount()) {
				throw new IndexOutOfBoundsException("Table row " + row + " is not valid");
			}
			Object value = null;
			AccountLine item = (AccountLine) _accountLineItems.get(row);
			switch (column) {
			case 0:
				value = item.getCurrency();
				break;
			case 1:
				value = item.getIssuer();
				break;
			case 2:
				value = item.getBalance();
				break;
			default:
				throw new IndexOutOfBoundsException("Table column " + column + " is not valid");
			}
			return value;
		}

		public void update() {
			fireTableDataChanged();
		}

	}

	class AccountTableModel2 extends AbstractTableModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private String[] columnNames;

		private Class<?>[] columnClasses;

		public AccountTableModel2(String[] columnNames, Class<?>[] columnClasses) {
			super();
			if (columnNames.length != columnClasses.length)
				throw new IllegalArgumentException("Number of names not same as number of classes");
			this.columnNames = columnNames;
			this.columnClasses = columnClasses;
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public Class<?> getColumnClass(int column) {
			return columnClasses[column];
		}

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}

		@Override
		public int getRowCount() {
			return _accountLineItems2.size();
		}

		@Override
		public Object getValueAt(int row, int column) {
			if (row > getRowCount()) {
				throw new IndexOutOfBoundsException("Table row " + row + " is not valid");
			}
			if (_accountLineItems2.size() == 0) {
				return null;
			}
			Object value = null;
			AccountLine item = (AccountLine) _accountLineItems2.get(row);
			switch (column) {
			case 0:
				value = item.getCurrency();
				break;
			case 1:
				value = item.getIssuer();
				break;
			case 2:
				value = item.getBalance();
				break;
			default:
				throw new IndexOutOfBoundsException("Table column " + column + " is not valid");
			}
			return value;
		}

		public void update() {
			fireTableDataChanged();
		}

	}

	class AccountTableModel3 extends AbstractTableModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private String[] columnNames;

		private Class<?>[] columnClasses;

		public AccountTableModel3(String[] columnNames, Class<?>[] columnClasses) {
			super();
			if (columnNames.length != columnClasses.length)
				throw new IllegalArgumentException("Number of names not same as number of classes");
			this.columnNames = columnNames;
			this.columnClasses = columnClasses;
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public Class<?> getColumnClass(int column) {
			return columnClasses[column];
		}

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}

		@Override
		public int getRowCount() {
			return _accountLineItems3.size();
		}

		@Override
		public Object getValueAt(int row, int column) {
			if (row > getRowCount()) {
				throw new IndexOutOfBoundsException("Table row " + row + " is not valid");
			}
			Object value = null;
			String v = _accountLineItems3.get(row);
			switch (column) {
			case 0:
				value = v;
				break;
			default:
				throw new IndexOutOfBoundsException("Table column " + column + " is not valid");
			}
			return value;
		}

		public void update() {
			fireTableDataChanged();
		}

	}

	public static RPAccountInfoDialog showDialog(JFrame parent, String text, String address) {
		RPAccountInfoDialog dialog = new RPAccountInfoDialog(parent, text, address);
		dialog.pack();
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
		return dialog;
	}

	public RPAccountInfoDialog(JFrame parent, String text, String address) {
		super(parent, text, Dialog.ModalityType.DOCUMENT_MODAL);
		setIconImage(UIRes.getIcon());
		setResizable(false);
		Dimension dim = new Dimension(668, 630);
		setPreferredSize(dim);
		setSize(dim);
		initComponents(address);

	}

	private void initComponents(String address) {

		addWindowListener(HelperWindow.get());
		_addressLabel = new RPLabel();
		_addressText = new RPTextBox();
		_addressNameText = new RPTextBox();
		_loadButton = new RPCButton();
		_memoButton = new RPCButton();
		_hashButton = new RPCButton();
		_exitButton = new RPCButton();
		_panelOne = new javax.swing.JScrollPane();
		_panelTwo = new javax.swing.JScrollPane();
		_panelThree = new javax.swing.JScrollPane();
		_spOne = new javax.swing.JSeparator();
		_spTwo = new javax.swing.JSeparator();
		_assetsLabel = new RPLabel();
		_issuedLabel = new RPLabel();
		_booksLabel = new RPLabel();

		Font font = UIRes.getFont();
		_loadButton.setFont(font);
		_exitButton.setFont(font);

		Class<?>[] columnClasses = { String.class, String.class, String.class };
		String[] columnNames = { UIMessage.currency, UIMessage.gateway, UIMessage.amount };
		int[] columnTypes = { AddressTable.ICON, AddressTable.ADDRESS, AddressTable.AMOUNT };

		final AccountTableModel tableModel = new AccountTableModel(columnNames, columnClasses);
		_tableOne = new AddressTable(tableModel, columnTypes);
		_tableOne.setFont(UIRes.getFont());
		_tableOne.setRowSorter(new TableRowSorter<TableModel>(tableModel));
		_tableOne.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		_panelOne.setViewportView(_tableOne);

		getContentPane().add(_panelOne);
		_panelOne.setBounds(10, 90, 640, 120);

		final AccountTableModel2 tableModel2 = new AccountTableModel2(columnNames, columnClasses);
		_tableTwo = new AddressTable(tableModel2, columnTypes);
		_tableTwo.setFont(UIRes.getFont());
		_tableTwo.setRowSorter(new TableRowSorter<TableModel>(tableModel2));
		_tableTwo.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		_panelTwo.setViewportView(_tableTwo);

		getContentPane().add(_panelTwo);
		_panelTwo.setBounds(10, 250, 640, 100);

		getContentPane().add(_spTwo);
		_spTwo.setBounds(0, 540, 670, 10);

		_booksLabel.setFont(UIRes.getFont()); // NOI18N
		_booksLabel.setText(LangConfig.get(this, "books", "Books"));
		getContentPane().add(_booksLabel);
		_booksLabel.setBounds(20, 360, 90, 20);

		Class<?>[] columnClasses1 = { String.class };
		String[] columnNames1 = { LangConfig.get(this, "tx", "Transaction Records") };

		int[] columnTypes1 = { AddressTable.INFO };

		final AccountTableModel3 tableModel3 = new AccountTableModel3(columnNames1, columnClasses1);
		_tableThree = new AddressTable(tableModel3, columnTypes1);
		_tableThree.setFont(UIRes.getFont());
		_tableThree.setRowSorter(new TableRowSorter<TableModel>(tableModel3));
		_tableThree.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_tableThree.addMouseListener(new infoMouseListener());
		addMouseListener(new infoMouseListener());
		_panelThree.setViewportView(_tableThree);

		getContentPane().add(_panelThree);
		_panelThree.setBounds(10, 390, 640, 130);

		getContentPane().setLayout(null);

		_addressLabel.setText(LangConfig.get(this, "address", "Address"));
		_addressLabel.setFont(new Font(LangConfig.getFontName(), 1, 14));
		getContentPane().add(_addressLabel);
		_addressLabel.setBounds(20, 25, 80, 15);
		getContentPane().add(_addressText);
		_addressText.setBounds(95, 22, 300, 21);

		getContentPane().add(_addressNameText);
		_addressNameText.setBounds(405, 22, 100, 21);
		_addressNameText.setEditable(false);
		_addressNameText.setFont(GraphicsUtils.getFont(Font.SANS_SERIF, 0, 12));

		address = address.trim();
		if (address.length() > 0) {
			_addressText.setText(address);
			_accountinfo = new AccountInfo();
			try {
				call(_accountinfo, tableModel, tableModel2, tableModel3);
			} catch (Exception ex) {
				_accountinfo = new AccountInfo();
			}
		} else {
			_addressText.setText("");
			_addressNameText.setText("Unkown");
		}

		_loadButton.setText(LangConfig.get(this, "load", "Load"));
		_loadButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				_accountinfo = new AccountInfo();
				try {
					call(_accountinfo, tableModel, tableModel2, tableModel3);
				} catch (Exception ex) {
					_accountinfo = new AccountInfo();
				}
			}
		});
		getContentPane().add(_loadButton);
		_loadButton.setBounds(513, 21, 125, 23);

		_memoButton.setText("Memo Data");
		_memoButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				String address = _addressText.getText().trim();
				if (address.startsWith("~")) {
					try {
						address = NameFind.getAddress(address);
					} catch (Exception ex) {
						RPToast.makeText(RPAccountInfoDialog.this, UIMessage.errAddress, Style.ERROR).display();
						return;
					}
				}
				if (!AccountFind.isRippleAddress(address)) {
					RPToast.makeText(RPAccountInfoDialog.this, UIMessage.errAddress, Style.ERROR).display();
					return;
				}
				RPSendMemoDialog.showDialog(LangConfig.get(this, "send_memo", "Memo Send/Receive"),
						LSystem.applicationMain, address, null);
			}
		});
		getContentPane().add(_memoButton);
		_memoButton.setBounds(11, 555, 165, 30);

		_hashButton.setText("Hash Data");
		_hashButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int size = _tableThree.getSelectedRow();
				if (_accountinfo.transactions.size() > 0 && size > -1) {
					UIMessage.infoMessage(RPAccountInfoDialog.this, "No:" + size);
					TransactionTx tx = _accountinfo.getTxs("Payment").get(size);
					RPHashInfoDialog.showDialog(RPAccountInfoDialog.this, tx);
				} else {
					RPHashInfoDialog.showDialog(RPAccountInfoDialog.this);
				}
			}
		});
		getContentPane().add(_hashButton);
		_hashButton.setBounds(191, 555, 165, 30);

		_exitButton.setText(LangConfig.get(this, "exit", "Exit"));
		_exitButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				SwingUtils.close(RPAccountInfoDialog.this);
			}
		});
		getContentPane().add(_exitButton);
		_exitButton.setBounds(520, 555, 128, 30);

		getContentPane().add(_spOne);
		_spOne.setBounds(0, 60, 670, 10);
		_assetsLabel.setFont(UIRes.getFont());
		_assetsLabel.setText(LangConfig.get(this, "assets", "Assets"));
		getContentPane().add(_assetsLabel);
		_assetsLabel.setBounds(20, 65, 90, 20);

		_issuedLabel.setFont(UIRes.getFont()); // NOI18N
		_issuedLabel.setText(LangConfig.get(this, "issued", "Currency issued"));
		getContentPane().add(_issuedLabel);
		_issuedLabel.setBounds(20, 220, 190, 20);
		getContentPane().setBackground(UIConfig.dialogbackground);
		final String ddata = LangConfig.get(this, "ddata", "Detailed data");
		UIRes.addPopMenu(_popMenu, ddata, new Updateable() {

			@Override
			public void action(Object o) {
				int size = _tableThree.getSelectedRow();
				if (_accountinfo.transactions.size() > 0 && size > -1) {
					UIMessage.infoMessage(RPAccountInfoDialog.this, "No:" + size);
					TransactionTx tx = _accountinfo.getTxs("Payment").get(size);
					RPHashInfoDialog.showDialog(RPAccountInfoDialog.this, tx);
				} else {
					RPHashInfoDialog.showDialog(RPAccountInfoDialog.this);
				}
			}
		});
		pack();

		HelperDialog.setSystemHelperMessage(
				"View the Ripple address detail data [ if your using a Ripple Labs public node , please update node to the s2.ripple.com view all history , s1.ripple.com only save one month's transactions ] . ");

	}

	private final static ArrayList<Store> _storage = new ArrayList<Store>();

	private static class Store {
		public AccountInfo info;
		public String name;
		public long date = 0;

		public Store(String n, AccountInfo i) {
			this.name = n;
			this.info = i;
			this.date = System.currentTimeMillis();
		}

	}

	private static AccountInfo reset(String name) {
		for (Store s : _storage) {
			if (s.name.equals(name) && (System.currentTimeMillis() - s.date) <= (LSystem.MINUTE / 2)) {
				return s.info;
			} else if (s.name.equals(name)) {
				_storage.remove(s);
				return null;
			}
		}
		return null;
	}

	private static void addStorage(Store s) {
		_storage.add(s);
		if (_storage.size() > 100) {
			_storage.remove(0);
		}
	}

	private static void addList(ArrayList<String> list, TransactionTx tx, int count) {
		if ("Payment".equals(tx.clazz)) {
			if (tx.counterparty != null) {
				list.add("No:" + count + " in " + tx.date + "<br>" + "<font size=4 color=red>" + tx.mode + "</font>"
						+ " " + "<font size=4 color=blue>" + tx.counterparty + "</font>" + "<br>"
						+ tx.currency.toGatewayString() + "<br>" + "Fee:" + tx.fee);
			} else {
				list.add("No:" + count + " in " + tx.date + "<br>" + "<font size=4 color=red>" + tx.mode + "</font>"
						+ " " + tx.currency.toGatewayString() + "<br>" + "Fee:" + tx.fee);
			}
			count++;
		}

	}

	private ArrayList<WaitDialog> _waitDialogs = new ArrayList<WaitDialog>(10);

	private int countInfoError = 0, countTxError = 0;

	public void call(final AccountInfo info, final AccountTableModel tableModel, final AccountTableModel2 tableModel2,
			final AccountTableModel3 tableModel3) {
		LSystem.invokeLater(new Runnable() {

			@Override
			public void run() {

				String address = _addressText.getText().trim();

				AccountInfo info_tmp = reset(address);

				// 如果存在缓存数据
				if (info_tmp != null) {
					info.copy(info_tmp);
					if (info.balance != null) {
						synchronized (_accountLineItems) {
							_accountLineItems.clear();
							_accountLineItems.add(new AccountLine("RippleLabels", LSystem.nativeCurrency.toUpperCase(),
									info.balance));
							if (_accountLineItems.size() > 0) {
								tableModel.update();
							}
						}
					}
					_addressNameText.setText(info.address);
					if (info.lines.size() > 0) {
						synchronized (_accountLineItems) {
							_accountLineItems.clear();
							_accountLineItems.add(new AccountLine("RippleLabels", LSystem.nativeCurrency.toUpperCase(),
									info.balance));
							_accountLineItems.addAll(info.lines);
						}
						if (_accountLineItems.size() > 0) {
							tableModel.update();
						}
					}
					if (info.debt.size() > 0) {
						synchronized (_accountLineItems2) {
							_accountLineItems2.clear();
							for (String cur : info.debt.keySet()) {
								AccountLine line = new AccountLine(address, cur, String.valueOf(info.debt.get(cur)));
								_accountLineItems2.add(line);
							}
						}
						if (_accountLineItems2.size() > 0) {
							tableModel2.update();
						}
					}
					if (info.transactions.size() > 0) {
						synchronized (_accountLineItems3) {
							_accountLineItems3.clear();
							int count = 0;
							for (int i = info.transactions.size() - 1; i > -1; i--) {
								TransactionTx tx = info.transactions.get(i);
								if ("Payment".equals(tx.clazz)) {
									addList(_accountLineItems3, tx, count++);
								}
							}
						}
						if (_accountLineItems3.size() > 0) {
							tableModel3.update();
						}
					}
					return;
				}

				Updateable updateAll = new Updateable() {

					@Override
					public void action(Object o) {
						String addressTmp = _addressText.getText().trim();
						// revalidate();
						// repaint();
						final WaitDialog waitDialog = WaitDialog.showDialog(RPAccountInfoDialog.this);
						_waitDialogs.add(waitDialog);
						// revalidate();
						// repaint();
						if (addressTmp.startsWith("~")) {
							try {
								addressTmp = NameFind.getAddress(addressTmp);
							} catch (Exception ex) {
								RPToast.makeText(RPAccountInfoDialog.this, UIMessage.errAddress, Style.ERROR).display();
								return;
							}
						}
						if (!AccountFind.isRippleAddress(addressTmp)) {
							RPToast.makeText(RPAccountInfoDialog.this, UIMessage.errAddress, Style.ERROR).display();
							return;
						}
						final String address = addressTmp;
						final AccountFind find = new AccountFind();
						// revalidate();
						// repaint();
						Updateable update_info = new Updateable() {

							@Override
							public void action(Object res) {
								if (info.count < 2) {
									if (info.balance != null) {
										synchronized (_accountLineItems) {
											_accountLineItems.clear();
											_accountLineItems.add(new AccountLine("RippleLabels",
													LSystem.nativeCurrency.toUpperCase(), info.balance));
											if (_accountLineItems.size() > 0) {
												tableModel.update();
											}
										}
									}
									String name = null;
									try {
										name = NameFind.getName(address);
									} catch (Exception ex) {
										name = "Unkown";
									}
									if (name == null || name.equalsIgnoreCase(address)) {
										name = "Unkown";
									}
									_addressNameText.setText(name);
								} else if (countInfoError < 1) {
									countInfoError++;
									find.processInfo(address, info, this);
									return;
								}

								// RPAccountInfoDialog.this.revalidate();
								// RPAccountInfoDialog.this.repaint();
							}
						};

						Updateable update_line = new Updateable() {

							@Override
							public void action(Object res) {
								if (info.lines.size() > 0) {
									synchronized (_accountLineItems) {
										_accountLineItems.clear();
										_accountLineItems.add(new AccountLine("RippleLabels",
												LSystem.nativeCurrency.toUpperCase(), info.balance));
										_accountLineItems.addAll(info.lines);
									}
									if (_accountLineItems.size() > 0) {
										tableModel.update();
									}
								}
								if (info.debt.size() > 0) {
									synchronized (_accountLineItems2) {
										_accountLineItems2.clear();
										for (String cur : info.debt.keySet()) {
											AccountLine line = new AccountLine(address, cur,
													String.valueOf(info.debt.get(cur)));
											_accountLineItems2.add(line);
										}
									}
									if (_accountLineItems2.size() > 0) {
										tableModel2.update();
									}
								}
								// RPAccountInfoDialog.this.revalidate();
								// RPAccountInfoDialog.this.repaint();
							}
						};

						Updateable update_tx = new Updateable() {

							@Override
							public void action(Object res) {
								if (info.transactions.size() > 0) {
									synchronized (_accountLineItems3) {
										_accountLineItems3.clear();
										int count = 0;
										for (int i = info.transactions.size() - 1; i > -1; i--) {
											TransactionTx tx = info.transactions.get(i);
											if ("Payment".equals(tx.clazz)) {
												addList(_accountLineItems3, tx, count++);
											}
										}
									}
									if (_accountLineItems3.size() > 0) {
										tableModel3.update();
										addStorage(new Store(address, new AccountInfo().copy(info)));
									}
									// 如果一次没有读出来数据，则尝试继续加载2次
								} else if (countTxError < 1) {
									countTxError++;
									find.processTx(address, info, this);
									return;
								}

								waitDialog.closeDialog();
								/*
								 * RPAccountInfoDialog.this.revalidate();
								 * RPAccountInfoDialog.this.repaint(); if
								 * (LSystem.applicationMain != null) {
								 * LSystem.applicationMain.revalidate();
								 * LSystem.applicationMain.repaint(); }
								 */

							}

						};

						find.processInfo(address, info, update_info);
						find.processLines(address, info, update_line);
						find.processTx(address, info, update_tx);
						// revalidate();
						// repaint();
					}
				};
				LSystem.postThread(updateAll);
			}
		});
	}

	public AccountInfo getAccountinfo() {
		return _accountinfo;
	}

	@Override
	public void windowActivated(WindowEvent e) {

	}

	@Override
	public void windowClosed(WindowEvent e) {
		if (_waitDialogs != null) {
			for (WaitDialog wait : _waitDialogs) {
				if (wait != null) {
					wait.closeDialog();
				}
			}
		}
	}

	@Override
	public void windowClosing(WindowEvent e) {

	}

	@Override
	public void windowDeactivated(WindowEvent e) {

	}

	@Override
	public void windowDeiconified(WindowEvent e) {

	}

	@Override
	public void windowIconified(WindowEvent e) {

	}

	@Override
	public void windowOpened(WindowEvent e) {

	}

}
