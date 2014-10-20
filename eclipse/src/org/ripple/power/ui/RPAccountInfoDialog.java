package org.ripple.power.ui;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.ripple.power.i18n.LangConfig;
import org.ripple.power.txns.AccountFind;
import org.ripple.power.txns.AccountInfo;
import org.ripple.power.txns.AccountLine;
import org.ripple.power.txns.TransactionTx;
import org.ripple.power.txns.Updateable;
import org.ripple.power.ui.table.AddressTable;

public class RPAccountInfoDialog extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private javax.swing.JSeparator jSeparator1;
	private javax.swing.JSeparator jSeparator2;
	private RPCButton _loadButton;
	private RPCButton _exitButton;
	private RPLabel _addressLabel;
	private RPLabel _assetsLabel;
	private RPLabel _issuedLabel;
	private RPLabel _booksLabel;
	private javax.swing.JTextField _addressText;
	private AddressTable jTable1;
	private AddressTable jTable2;
	private AddressTable jTable3;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JScrollPane jScrollPane3;
	private ArrayList<AccountLine> _accountLineItems = new ArrayList<AccountLine>();

	private ArrayList<AccountLine> _accountLineItems2 = new ArrayList<AccountLine>();

	private ArrayList<String> _accountLineItems3 = new ArrayList<String>();

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
				throw new IllegalArgumentException(
						"Number of names not same as number of classes");
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
				throw new IndexOutOfBoundsException("Table row " + row
						+ " is not valid");
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
				value = item.getAmount();
				break;
			default:
				throw new IndexOutOfBoundsException("Table column " + column
						+ " is not valid");
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
				throw new IllegalArgumentException(
						"Number of names not same as number of classes");
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
				throw new IndexOutOfBoundsException("Table row " + row
						+ " is not valid");
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
				value = item.getAmount();
				break;
			default:
				throw new IndexOutOfBoundsException("Table column " + column
						+ " is not valid");
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
				throw new IllegalArgumentException(
						"Number of names not same as number of classes");
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
				throw new IndexOutOfBoundsException("Table row " + row
						+ " is not valid");
			}
			Object value = null;
			String v = _accountLineItems3.get(row);
			switch (column) {
			case 0:
				value = v;
				break;
			default:
				throw new IndexOutOfBoundsException("Table column " + column
						+ " is not valid");
			}
			return value;
		}

		public void update() {
			fireTableDataChanged();
		}

	}

	public static RPAccountInfoDialog showDialog(JFrame parent, String text,
			String address) {
		RPAccountInfoDialog dialog = new RPAccountInfoDialog(parent, text,
				address);
		dialog.pack();
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
		return dialog;
	}

	public RPAccountInfoDialog(JFrame parent, String text, String address) {
		super(parent, text, Dialog.ModalityType.DOCUMENT_MODAL);

		setResizable(false);
		Dimension dim = new Dimension(668, 620);
		setPreferredSize(dim);
		setSize(dim);
		initComponents(address);

	}

	private void initComponents(String address) {
		getContentPane().setBackground(new Color(36, 36, 36));
		_addressLabel = new RPLabel();
		_addressText = new RPTextBox();
		_loadButton = new RPCButton();
		_exitButton = new RPCButton();
		jScrollPane1 = new javax.swing.JScrollPane();
		jScrollPane2 = new javax.swing.JScrollPane();
		jScrollPane3 = new javax.swing.JScrollPane();
		jSeparator1 = new javax.swing.JSeparator();
		jSeparator2 = new javax.swing.JSeparator();
		_assetsLabel = new RPLabel();
		_issuedLabel = new RPLabel();
		_booksLabel = new RPLabel();
		
		Font font = new Font(LangConfig.fontName, 0, 14);
		_loadButton.setFont(font);
		_exitButton.setFont(font);
		
		Class<?>[] columnClasses = { String.class, String.class, String.class };
		String[] columnNames = { LangConfig.get(this, "currency", "Currency"),
				LangConfig.get(this, "gateway", "Gateway"),
				LangConfig.get(this, "amount", "Amount") };

		int[] columnTypes = { AddressTable.CUR, AddressTable.ADDRESS,
				AddressTable.AMOUNT };

		final AccountTableModel tableModel = new AccountTableModel(columnNames,
				columnClasses);
		jTable1 = new AddressTable(tableModel, columnTypes);
		jTable1.setFont(new Font(LangConfig.fontName, 0, 14));
		jTable1.setRowSorter(new TableRowSorter<TableModel>(tableModel));
		jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		jScrollPane1.setViewportView(jTable1);

		getContentPane().add(jScrollPane1);
		jScrollPane1.setBounds(10, 90, 640, 120);

		final AccountTableModel2 tableModel2 = new AccountTableModel2(
				columnNames, columnClasses);
		jTable2 = new AddressTable(tableModel2, columnTypes);
		jTable2.setFont(new Font(LangConfig.fontName, 0, 14));
		jTable2.setRowSorter(new TableRowSorter<TableModel>(tableModel));
		jTable2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		jScrollPane2.setViewportView(jTable2);

		getContentPane().add(jScrollPane2);
		jScrollPane2.setBounds(10, 250, 640, 100);

		getContentPane().add(jSeparator2);
		jSeparator2.setBounds(0, 540, 670, 10);

		_booksLabel.setFont(new java.awt.Font(LangConfig.fontName, 0, 14)); // NOI18N
		_booksLabel.setText(LangConfig.get(this, "books", "Books"));
		getContentPane().add(_booksLabel);
		_booksLabel.setBounds(20, 360, 90, 20);

		Class<?>[] columnClasses1 = { String.class };
		String[] columnNames1 = { LangConfig.get(this, "tx",
				"Transaction Records") };

		int[] columnTypes1 = { AddressTable.NAME };

		final AccountTableModel3 tableModel3 = new AccountTableModel3(
				columnNames1, columnClasses1);
		jTable3 = new AddressTable(tableModel3, columnTypes1);
		jTable3.setFont(new Font(LangConfig.fontName, 0, 14));
		jTable3.setRowSorter(new TableRowSorter<TableModel>(tableModel3));
		jTable3.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		jScrollPane3.setViewportView(jTable3);

		getContentPane().add(jScrollPane3);
		jScrollPane3.setBounds(10, 390, 640, 130);

		final AccountInfo info = new AccountInfo();

		getContentPane().setLayout(null);

		_addressLabel.setText(LangConfig.get(this, "address", "Address"));
		_addressLabel.setFont(new Font(LangConfig.fontName, 1, 14));
		getContentPane().add(_addressLabel);
		_addressLabel.setBounds(20, 25, 80, 15);
		getContentPane().add(_addressText);
		_addressText.setBounds(108, 22, 343, 21);

		if (address.trim().length() > 0) {
			_addressText.setText(address.trim());
			call(info, tableModel, tableModel2, tableModel3);

		} else {
			_addressText.setText("");
		}
		_loadButton.setText(LangConfig.get(this, "load", "Load"));
		_loadButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				call(info, tableModel, tableModel2, tableModel3);
			}
		});
		getContentPane().add(_loadButton);
		_loadButton.setBounds(513, 21, 125, 23);

		_exitButton.setText(LangConfig.get(this, "exit", "Exit"));
		_exitButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				setVisible(false);
				dispose();
			}
		});
		getContentPane().add(_exitButton);
		_exitButton.setBounds(520, 550, 125, 30);

		getContentPane().add(jSeparator1);
		jSeparator1.setBounds(0, 60, 670, 10);
		_assetsLabel.setFont(new java.awt.Font(LangConfig.fontName, 0, 14));
		_assetsLabel.setText(LangConfig.get(this, "assets", "Assets"));
		getContentPane().add(_assetsLabel);
		_assetsLabel.setBounds(20, 65, 90, 20);

		_issuedLabel.setFont(new java.awt.Font(LangConfig.fontName, 0, 14)); // NOI18N
		_issuedLabel.setText(LangConfig.get(this, "issued", "Currency issued"));
		getContentPane().add(_issuedLabel);
		_issuedLabel.setBounds(20, 220, 190, 20);

		pack();

	}// </editor-fold>

	public void call(final AccountInfo info,
			final AccountTableModel tableModel,
			final AccountTableModel2 tableModel2,
			final AccountTableModel3 tableModel3) {

		final WaitDialog dialog = WaitDialog.showDialog(this);
		final String address = _addressText.getText().trim();

		AccountFind find = new AccountFind();

		Updateable update_info = new Updateable() {

			@Override
			public void action(Object res) {

				if (info.count < 2) {
					if (info.balance != null) {
						synchronized (_accountLineItems) {
							_accountLineItems.clear();
							_accountLineItems.add(new AccountLine(
									"RippleLabels", "XRP", info.balance));
							tableModel.update();
						}
					}
				}

			}
		};

		Updateable update_line = new Updateable() {

			@Override
			public void action(Object res) {
				if (info.lines.size() > 0) {
					synchronized (_accountLineItems) {
						_accountLineItems.clear();
						_accountLineItems.add(new AccountLine("RippleLabels",
								"XRP", info.balance));
						_accountLineItems.addAll(info.lines);
					}
					tableModel.update();
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
					tableModel2.update();
				}
			}
		};

		Updateable update_tx = new Updateable() {

			@Override
			public void action(Object res) {
				if (info.transactions.size() > 0) {
					synchronized (_accountLineItems3) {
						_accountLineItems3.clear();
						for (TransactionTx tx : info.transactions) {
							if ("Payment".equals(tx.clazz)) {
								_accountLineItems3.add(tx.date + " " + tx.mode
										+ " " + tx.counterparty + " "
										+ tx.currency.toGatewayString()
										+ ",Fee:" + tx.fee);
							}
						}
					}
					tableModel3.update();
					dialog.closeDialog();
				}
			}
		};

		find.processInfo(address, info, update_info);
		find.processLines(address, info, update_line);
		find.processTx(address, info, update_tx);

	}
}
