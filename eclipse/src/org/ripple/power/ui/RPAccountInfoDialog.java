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

import org.joda.time.DateTime;
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
	private RPCButton jButton1;
	private RPCButton jButton2;
	private RPLabel jLabel1;
	private RPLabel jLabel2;
	private RPLabel jLabel3;
	private RPLabel jLabel4;
	private javax.swing.JTextField jTextField1;
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
				value = item.currency;
				break;
			case 1:
				value = item.issuer;
				break;
			case 2:
				value = item.amount;
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
				value = item.currency;
				break;
			case 1:
				value = item.issuer;
				break;
			case 2:
				value = item.amount;
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
		jLabel1 = new RPLabel();
		jTextField1 = new RPTextBox();
		jButton1 = new RPCButton();
		jButton2 = new RPCButton();
		jScrollPane1 = new javax.swing.JScrollPane();
		jScrollPane2 = new javax.swing.JScrollPane();
		jScrollPane3 = new javax.swing.JScrollPane();
		jSeparator1 = new javax.swing.JSeparator();
		jSeparator2 = new javax.swing.JSeparator();
		jLabel2 = new RPLabel();
		jLabel3 = new RPLabel();
		jLabel4 = new RPLabel();
		Class<?>[] columnClasses = { String.class, String.class, String.class };
		String[] columnNames = { "币种", "网关", "金额" };

		int[] columnTypes = { AddressTable.CUR, AddressTable.ADDRESS,
				AddressTable.AMOUNT };

		final AccountTableModel tableModel = new AccountTableModel(columnNames,
				columnClasses);
		jTable1 = new AddressTable(tableModel, columnTypes);
		jTable1.setFont(new Font("Dialog", 0, 14));
		jTable1.setRowSorter(new TableRowSorter<TableModel>(tableModel));
		jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		jScrollPane1.setViewportView(jTable1);

		getContentPane().add(jScrollPane1);
		jScrollPane1.setBounds(10, 90, 640, 120);

		final AccountTableModel2 tableModel2 = new AccountTableModel2(
				columnNames, columnClasses);
		jTable2 = new AddressTable(tableModel2, columnTypes);
		jTable2.setFont(new Font("Dialog", 0, 14));
		jTable2.setRowSorter(new TableRowSorter<TableModel>(tableModel));
		jTable2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		jScrollPane2.setViewportView(jTable2);

		getContentPane().add(jScrollPane2);
		jScrollPane2.setBounds(10, 250, 640, 100);

		getContentPane().add(jSeparator2);
		jSeparator2.setBounds(0, 540, 670, 10);

		jLabel4.setFont(new java.awt.Font("宋体", 0, 14)); // NOI18N
		jLabel4.setText("账簿");
		getContentPane().add(jLabel4);
		jLabel4.setBounds(20, 360, 90, 20);

		Class<?>[] columnClasses1 = { String.class };
		String[] columnNames1 = { "交易记录" };

		int[] columnTypes1 = { AddressTable.NAME };

		final AccountTableModel3 tableModel3 = new AccountTableModel3(
				columnNames1, columnClasses1);
		jTable3 = new AddressTable(tableModel3, columnTypes1);
		jTable3.setFont(new Font("Dialog", 0, 14));
		jTable3.setRowSorter(new TableRowSorter<TableModel>(tableModel3));
		jTable3.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		jScrollPane3.setViewportView(jTable3);

		getContentPane().add(jScrollPane3);
		jScrollPane3.setBounds(10, 390, 640, 130);

		final AccountInfo info = new AccountInfo();

		getContentPane().setLayout(null);

		jLabel1.setText("Address");
		getContentPane().add(jLabel1);
		jLabel1.setBounds(20, 25, 80, 15);
		getContentPane().add(jTextField1);
		jTextField1.setBounds(108, 22, 343, 21);

		if (address.trim().length() > 0) {
			jTextField1.setText(address.trim());
			call(info, tableModel, tableModel2, tableModel3);

		} else {
			jTextField1.setText("");
		}
		jButton1.setText("Load");
		jButton1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				call(info, tableModel, tableModel2, tableModel3);
			}
		});
		getContentPane().add(jButton1);
		jButton1.setBounds(513, 21, 125, 23);

		jButton2.setText("Exit");
		jButton2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				setVisible(false);
				dispose();
			}
		});
		getContentPane().add(jButton2);
		jButton2.setBounds(520, 550, 125, 30);

		getContentPane().add(jSeparator1);
		jSeparator1.setBounds(0, 60, 670, 10);
		jLabel2.setFont(new java.awt.Font("宋体", 0, 14));
		jLabel2.setText("资产");
		getContentPane().add(jLabel2);
		jLabel2.setBounds(20, 65, 90, 20);

		jLabel3.setFont(new java.awt.Font("宋体", 0, 14)); // NOI18N
		jLabel3.setText("发行款");
		getContentPane().add(jLabel3);
		jLabel3.setBounds(20, 220, 90, 20);

		pack();

	}// </editor-fold>

	public void call(final AccountInfo info,
			final AccountTableModel tableModel,
			final AccountTableModel2 tableModel2,
			final AccountTableModel3 tableModel3) {

		final WaitDialog dialog = WaitDialog.showDialog(this);
		final String address = jTextField1.getText().trim();

		AccountFind find = new AccountFind();

		Updateable update_info = new Updateable() {

			@Override
			public void action() {

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
			public void action() {
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
			public void action() {
				if (info.transactions.size() > 0) {
					synchronized (_accountLineItems3) {
						_accountLineItems3.clear();
						for (TransactionTx tx : info.transactions) {
							if ("Payment".equals(tx.clazz)) {

								_accountLineItems3.add("Date:"
										+ getTime(tx.date) + ",Mode:"
										+ tx.mode + ",Currency:" + tx.currency.toGatewayString()
										+ ",Fee:" + tx.fee);
							}
						}
					}
					tableModel3.update();
					dialog.closeDialog();
				}
			}

			public String getTime(DateTime dt) {
				int year = dt.getYear();
				int month = dt.getMonthOfYear();
				int day = dt.getDayOfMonth();
				int hour = dt.getHourOfDay();
				int min = dt.getMinuteOfHour();
				return year + "/" + (month < 10 ? "0" + month : month) + "/"
						+ (day < 10 ? "0" + day : day) + " "
						+ (hour < 10 ? "0" + hour : hour) + ":"
						+ (min < 10 ? "0" + min : min);
			}

		};

		find.processInfo(address, info, update_info);
		find.processLines(address, info, update_line);
		find.processTx(address, info, update_tx);

	}
}
