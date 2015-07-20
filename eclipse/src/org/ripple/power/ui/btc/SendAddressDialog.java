package org.ripple.power.ui.btc;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

import org.ripple.power.txns.btc.Address;
import org.ripple.power.txns.btc.BTCLoader;
import org.ripple.power.txns.btc.BlockStoreException;
import org.ripple.power.ui.errors.ErrorLog;
import org.ripple.power.ui.table.AddressTable;
import org.ripple.power.ui.view.ButtonPane;

public class SendAddressDialog extends JDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Class<?>[] columnClasses = { String.class,
			String.class };

	private static final String[] columnNames = { "Name", "Address" };

	private static final int[] columnTypes = { AddressTable.NAME,
			AddressTable.ADDRESS };

	private final AddressTableModel tableModel;

	private final AddressTable table;

	private final JScrollPane scrollPane;

	public SendAddressDialog(JFrame parent) {
		super(parent, "Send Addresses", Dialog.ModalityType.DOCUMENT_MODAL);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		tableModel = new AddressTableModel(columnNames, columnClasses);
		table = new AddressTable(tableModel, columnTypes);
		table.setRowSorter(new TableRowSorter<>(tableModel));
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		scrollPane = new JScrollPane(table);

		JPanel tablePane = new JPanel();
		tablePane.setBackground(Color.WHITE);
		tablePane.add(Box.createGlue());
		tablePane.add(scrollPane);
		tablePane.add(Box.createGlue());

		JPanel buttonPane = new ButtonPane(this, 10, new String[] { "New",
				"new" }, new String[] { "Copy", "copy" }, new String[] {
				"Edit", "edit" }, new String[] { "Delete", "delete" },
				new String[] { "Done", "done" });
		buttonPane.setBackground(Color.white);

		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		contentPane.setOpaque(true);
		contentPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		contentPane.setBackground(Color.WHITE);
		contentPane.add(tablePane);
		contentPane.add(buttonPane);
		setContentPane(contentPane);
	}

	public static void showDialog(JFrame parent) {
		try {
			JDialog dialog = new SendAddressDialog(parent);
			dialog.pack();
			dialog.setLocationRelativeTo(parent);
			dialog.setVisible(true);
		} catch (Exception exc) {
			ErrorLog.logException("Exception while displaying dialog", exc);
		}
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		try {
			String action = ae.getActionCommand();
			if (action.equals("done")) {
				setVisible(false);
				dispose();
			} else if (action.equals("new")) {
				editAddress(null, -1);
			} else {
				int row = table.getSelectedRow();
				if (row < 0) {
					JOptionPane.showMessageDialog(this, "No entry selected",
							"Error", JOptionPane.ERROR_MESSAGE);
				} else {
					row = table.convertRowIndexToModel(row);
					Address addr = BTCLoader.addresses.get(row);
					switch (action) {
					case "copy":
						StringSelection sel = new StringSelection(
								addr.toString());
						Clipboard cb = Toolkit.getDefaultToolkit()
								.getSystemClipboard();
						cb.setContents(sel, null);
						break;
					case "edit":
						editAddress(addr, row);
						break;
					case "delete":
						BTCLoader.addresses.remove(row);
						BTCLoader.blockStore.deleteAddress(addr);
						tableModel.fireTableDataChanged();
						break;
					}
				}
			}
		} catch (BlockStoreException exc) {
			ErrorLog.logException("Unable to update blockStore database", exc);
		} catch (Exception exc) {
			ErrorLog.logException("Exception while processing action event",
					exc);
		}
	}

	private void editAddress(Address address, int row)
			throws BlockStoreException {
		Address addr = address;
		while (true) {
			addr = AddressEditDialog.showDialog(this, addr, true);
			if (addr == null)
				break;
			String label = addr.getLabel();
			boolean valid = true;
			synchronized (BTCLoader.lock) {
				for (Address chkAddr : BTCLoader.addresses) {
					if (chkAddr.equals(address))
						continue;
					if (chkAddr.getLabel().compareToIgnoreCase(label) == 0) {
						JOptionPane.showMessageDialog(this,
								"Duplicate name specified", "Error",
								JOptionPane.ERROR_MESSAGE);
						valid = false;
						break;
					}
				}
				if (valid) {
					if (row >= 0) {
						BTCLoader.addresses.remove(row);
					}
					boolean added = false;
					for (int i = 0; i < BTCLoader.addresses.size(); i++) {
						Address chkAddr = BTCLoader.addresses.get(i);
						if (chkAddr.getLabel().compareToIgnoreCase(label) > 0) {
							BTCLoader.addresses.add(i, addr);
							added = true;
							break;
						}
					}
					if (!added) {
						BTCLoader.addresses.add(addr);
					}
				}
			}
			if (valid) {
				if (row >= 0)
					BTCLoader.blockStore.setAddressLabel(addr);
				else
					BTCLoader.blockStore.storeAddress(addr);
				tableModel.fireTableDataChanged();
				break;
			}
		}
	}

	private class AddressTableModel extends AbstractTableModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private String[] columnNames;

		private Class<?>[] columnClasses;

		public AddressTableModel(String[] columnNames, Class<?>[] columnClasses) {
			super();
			if (columnNames.length != columnClasses.length) {
				throw new IllegalArgumentException(
						"Number of names not same as number of classes");
			}
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
			return BTCLoader.addresses.size();
		}

		@Override
		public Object getValueAt(int row, int column) {
			if (row >= BTCLoader.addresses.size()){
				throw new IndexOutOfBoundsException("Table row " + row
						+ " is not valid");
			}
			Object value;
			Address addr = BTCLoader.addresses.get(row);
			switch (column) {
			case 0:
				value = addr.getLabel();
				break;
			case 1:
				value = addr.toString();
				break;
			default:
				throw new IndexOutOfBoundsException("Table column " + column
						+ " is not valid");
			}
			return value;
		}
	}
}
