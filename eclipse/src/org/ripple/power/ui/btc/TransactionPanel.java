package org.ripple.power.ui.btc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

import org.ripple.power.config.LSystem;
import org.ripple.power.txns.btc.Address;
import org.ripple.power.txns.btc.BTCLoader;
import org.ripple.power.txns.btc.BlockStoreException;
import org.ripple.power.txns.btc.BlockTransaction;
import org.ripple.power.txns.btc.ECKey;
import org.ripple.power.txns.btc.ReceiveTransaction;
import org.ripple.power.txns.btc.SendTransaction;
import org.ripple.power.ui.UIConfig;
import org.ripple.power.ui.UIRes;
import org.ripple.power.ui.errors.ErrorLog;
import org.ripple.power.ui.table.AddressTable;
import org.ripple.power.ui.view.ButtonPane;

public class TransactionPanel extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Class<?>[] columnClasses = { Date.class, String.class,
			String.class, String.class, BigInteger.class, BigInteger.class,
			String.class, String.class };

	private static final String[] columnNames = { "Date", "Transaction ID",
			"Type", "Name/Address", "Amount", "Fee", "Location", "Status" };

	private static final int[] columnTypes = { AddressTable.DATE,
			AddressTable.ADDRESS, AddressTable.TYPE, AddressTable.ADDRESS,
			AddressTable.AMOUNT, AddressTable.AMOUNT, AddressTable.STATUS,
			AddressTable.STATUS };

	private final JLabel walletLabel;

	private final JLabel safeLabel;

	private final JLabel blockLabel;

	private final JScrollPane scrollPane;

	private final JTable table;

	private final TransactionTableModel tableModel;

	private BigInteger safeBalance;

	private BigInteger walletBalance;

	public TransactionPanel(JDialog parentFrame) {
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setOpaque(true);
		setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

		tableModel = new TransactionTableModel(columnNames, columnClasses);
		table = new AddressTable(tableModel, columnTypes);
		table.setRowSorter(new TableRowSorter<>(tableModel));
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		int frameHeight = 1200;
		if (LSystem.applicationMain != null) {
			frameHeight = LSystem.applicationMain.getHeight() + 50;
		}
		table.setPreferredScrollableViewportSize(new Dimension(table
				.getPreferredScrollableViewportSize().width,
				(frameHeight / table.getRowHeight()) * table.getRowHeight()));

		scrollPane = new JScrollPane(table);

		JPanel statusPane = new JPanel();
		statusPane.setLayout(new BoxLayout(statusPane, BoxLayout.X_AXIS));
		statusPane.setBackground(Color.WHITE);
		walletLabel = new JLabel(getWalletText(), SwingConstants.CENTER);
		statusPane.add(walletLabel);
		safeLabel = new JLabel(getSafeText(), SwingConstants.CENTER);
		statusPane.add(safeLabel);
		blockLabel = new JLabel(getBlockText(), SwingConstants.CENTER);
		statusPane.add(blockLabel);

		ButtonPane buttonPane = new ButtonPane(this, 20, new String[] {
				"New Address", "new address" }, new String[] { "Copy TxID",
				"copy txid" }, new String[] { "Move to Safe", "move to safe" },
				new String[] { "Move to Wallet", "move to blockStore" });

		buttonPane.setBackground(UIConfig.dialogbackground);

		add(statusPane);
		add(Box.createVerticalStrut(5));
		add(scrollPane);
		add(Box.createVerticalStrut(5));
		add(buttonPane);

		setBackground(UIConfig.dialogbackground);
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		try {
			int row = table.getSelectedRow();
			if (row < 0) {
				UIRes.showErrorMessage(this, "Error", "No transaction selected");
			} else {
				row = table.convertRowIndexToModel(row);
				String action = ae.getActionCommand();
				switch (action) {
				case "new address":

					break;
				case "copy txid":
					String address = (String) tableModel.getValueAt(row, 1);
					StringSelection sel = new StringSelection(address);
					Clipboard cb = Toolkit.getDefaultToolkit()
							.getSystemClipboard();
					cb.setContents(sel, null);
					break;
				case "move to safe":
					if (moveToSafe(row)) {
						tableModel.fireTableRowsUpdated(row, row);
						walletLabel.setText(getWalletText());
						safeLabel.setText(getSafeText());
					}
					break;
				case "move to blockStore":
					if (moveToWallet(row)) {
						tableModel.fireTableRowsUpdated(row, row);
						walletLabel.setText(getWalletText());
						safeLabel.setText(getSafeText());
					}
					break;
				}
			}
		} catch (BlockStoreException exc) {
			ErrorLog.logException("Unable to update blockStore", exc);
		} catch (Exception exc) {
			ErrorLog.logException("Exception while processing action event",
					exc);
		}
	}

	public void walletChanged() {
		int row = table.getSelectedRow();
		tableModel.walletChanged();
		if (row >= 0 && row < table.getRowCount()) {
			table.setRowSelectionInterval(row, row);
		}
		walletLabel.setText(getWalletText());
		safeLabel.setText(getSafeText());
	}

	public void statusChanged() {
		blockLabel.setText(getBlockText());
		tableModel.fireTableDataChanged();
	}

	private boolean moveToSafe(int row) throws BlockStoreException {
		BlockTransaction tx = tableModel.getTransaction(row);
		if (!(tx instanceof ReceiveTransaction)) {
			UIRes.showErrorMessage(this, "Error",
					"The safe contains coins that you have received and not spent");
			return false;
		}
		ReceiveTransaction rcvTx = (ReceiveTransaction) tx;
		if (rcvTx.inSafe()) {
			UIRes.showErrorMessage(this, "Error",
					"The transaction is already in the safe");
			return false;
		}
		if (rcvTx.isSpent()) {
			UIRes.showErrorMessage(this, "Error",
					"The coins have already been spent");
			return false;
		}
		BTCLoader.blockStore.setTxSafe(rcvTx.getTxHash(), rcvTx.getTxIndex(),
				true);
		rcvTx.setSafe(true);
		safeBalance = safeBalance.add(rcvTx.getValue());
		walletBalance = walletBalance.subtract(rcvTx.getValue());
		return true;
	}

	private boolean moveToWallet(int row) throws BlockStoreException {
		BlockTransaction tx = tableModel.getTransaction(row);
		if (!(tx instanceof ReceiveTransaction)) {
			UIRes.showErrorMessage(this, "Error",
					"The safe contains coins that you have received and not spent");
			return false;
		}
		ReceiveTransaction rcvTx = (ReceiveTransaction) tx;
		if (!rcvTx.inSafe()) {
			UIRes.showErrorMessage(this, "Error",
					"The transaction is not in the safe");
			return false;
		}
		BTCLoader.blockStore.setTxSafe(rcvTx.getTxHash(), rcvTx.getTxIndex(),
				false);
		walletBalance = walletBalance.add(rcvTx.getValue());
		safeBalance = safeBalance.subtract(rcvTx.getValue());
		rcvTx.setSafe(false);
		return true;
	}

	private String getWalletText() {
		return String.format("<html><h2>Wallet %s BTC</h2></html>",
				BTCLoader.satoshiToString(walletBalance));
	}

	private String getSafeText() {
		return String.format("<html><h2>Safe %s BTC</h2></html>",
				BTCLoader.satoshiToString(safeBalance));
	}

	private String getBlockText() {
		return String.format("<html><h2>Block %d</h2></html>",
				BTCLoader.blockStore.getChainHeight());
	}

	private class TransactionTableModel extends AbstractTableModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private String[] columnNames;

		private Class<?>[] columnClasses;

		private final List<BlockTransaction> txList = new LinkedList<>();

		public TransactionTableModel(String[] columnNames,
				Class<?>[] columnClasses) {
			super();
			if (columnNames.length != columnClasses.length)
				throw new IllegalArgumentException(
						"Number of names not same as number of classes");
			this.columnNames = columnNames;
			this.columnClasses = columnClasses;
			buildTxList();
		}

		private void buildTxList() {
			txList.clear();
			walletBalance = BigInteger.ZERO;
			safeBalance = BigInteger.ZERO;
			try {
				List<SendTransaction> sendList = BTCLoader.blockStore
						.getSendTxList();
				for (SendTransaction sendTx : sendList) {
					long txTime = sendTx.getTxTime();
					walletBalance = walletBalance.subtract(sendTx.getValue())
							.subtract(sendTx.getFee());
					boolean added = false;
					for (int i = 0; i < txList.size(); i++) {
						if (txList.get(i).getTxTime() <= txTime) {
							txList.add(i, sendTx);
							added = true;
							break;
						}
					}
					if (!added)
						txList.add(sendTx);
				}
				List<ReceiveTransaction> rcvList = BTCLoader.blockStore
						.getReceiveTxList();
				for (ReceiveTransaction rcvTx : rcvList) {
					if (rcvTx.isChange())
						continue;
					if (rcvTx.inSafe())
						safeBalance = safeBalance.add(rcvTx.getValue());
					else
						walletBalance = walletBalance.add(rcvTx.getValue());
					long txTime = rcvTx.getTxTime();
					boolean added = false;
					for (int i = 0; i < txList.size(); i++) {
						if (txList.get(i).getTxTime() <= txTime) {
							txList.add(i, rcvTx);
							added = true;
							break;
						}
					}
					if (!added) {
						txList.add(rcvTx);
					}
				}
			} catch (BlockStoreException exc) {
				ErrorLog.logException("Unable to build transaction list", exc);
			}
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
			return txList.size();
		}

		@Override
		public Object getValueAt(int row, int column) {
			if (row >= txList.size()) {
				throw new IndexOutOfBoundsException("Table row " + row
						+ " is not valid");
			}
			Object value;
			BlockTransaction tx = txList.get(row);
			switch (column) {
			case 0:
				value = new Date(tx.getTxTime() * 1000);
				break;
			case 1:
				value = tx.getTxHash().toString();
				break;
			case 2:
				if (tx instanceof ReceiveTransaction) {
					value = "Received with";
				} else {
					value = "Sent to";
				}
				break;
			case 3:
				value = null;
				Address addr = tx.getAddress();
				if (tx instanceof ReceiveTransaction) {
					for (ECKey chkKey : BTCLoader.keys) {
						if (Arrays.equals(chkKey.getPubKeyHash(),
								addr.getHash())) {
							if (chkKey.getLabel().length() > 0) {
								value = chkKey.getLabel();
							}
							break;
						}
					}
				} else {
					for (Address chkAddr : BTCLoader.addresses) {
						if (Arrays.equals(chkAddr.getHash(), addr.getHash())) {
							if (chkAddr.getLabel().length() > 0) {
								value = chkAddr.getLabel();
							}
							break;
						}
					}
				}
				if (value == null) {
					value = addr.toString();
				}
				break;
			case 4:
				value = tx.getValue();
				break;
			case 5:
				if (tx instanceof SendTransaction) {
					value = ((SendTransaction) tx).getFee();
				} else {
					value = null;
				}
				break;
			case 6:
				if (tx instanceof ReceiveTransaction) {
					if (((ReceiveTransaction) tx).inSafe()) {
						value = "Safe";
					} else {
						value = "Wallet";
					}
				} else {
					value = "";
				}
				break;
			case 7: // Status
				try {
					if (tx instanceof ReceiveTransaction
							&& ((ReceiveTransaction) tx).isSpent()) {
						value = "Spent";
					} else {
						int depth = BTCLoader.blockStore.getTxDepth(tx
								.getTxHash());
						if ((tx instanceof ReceiveTransaction)
								&& ((ReceiveTransaction) tx).isCoinBase()) {
							if (depth == 0) {
								value = "Pending";
							} else if (depth < BTCLoader.COINBASE_MATURITY) {
								value = "Immature";
							} else {
								value = "Mature";
							}
						} else if (depth == 0) {
							value = "Pending";
						} else if (depth < BTCLoader.TRANSACTION_CONFIRMED) {
							value = "Building";
						} else {
							value = "Confirmed";
						}
					}
				} catch (BlockStoreException exc) {
					ErrorLog.logException("Unable to get transaction depth",
							exc);
					value = "Unknown";
				}
				break;
			default:
				throw new IndexOutOfBoundsException("Table column " + column
						+ " is not valid");
			}
			return value;
		}

		public void walletChanged() {
			buildTxList();
			fireTableDataChanged();
		}

		public BlockTransaction getTransaction(int row) {
			return txList.get(row);
		}

	}
}
