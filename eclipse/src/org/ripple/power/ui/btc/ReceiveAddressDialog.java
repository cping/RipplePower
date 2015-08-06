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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

import org.ripple.power.txns.btc.Address;
import org.ripple.power.txns.btc.BTCLoader;
import org.ripple.power.txns.btc.ECKey;
import org.ripple.power.txns.btc.FilterLoadMessage;
import org.ripple.power.txns.btc.Message;
import org.ripple.power.txns.btc.BlockStoreException;
import org.ripple.power.ui.UIRes;
import org.ripple.power.ui.errors.ErrorLog;
import org.ripple.power.ui.table.AddressTable;
import org.ripple.power.ui.view.ButtonPane;

public class ReceiveAddressDialog extends JDialog implements ActionListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    private static final Class<?>[] columnClasses = {
        String.class, String.class};

    private static final String[] columnNames = {
        "Name", "Address"};

    private static final int[] columnTypes = {
        AddressTable.NAME, AddressTable.ADDRESS};

    private final AddressTableModel tableModel;

    private final JTable table;

    private final JScrollPane scrollPane;

    public ReceiveAddressDialog(JDialog parent) {
        super(parent, "Receive Addresses", Dialog.ModalityType.DOCUMENT_MODAL);
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
        JPanel buttonPane = new ButtonPane(this, 10, new String[] {"New", "new"},
                                                     new String[] {"Copy", "copy"},
                                                     new String[] {"Edit", "edit"},
                                                     new String[] {"Done", "done"});
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

    public static void showDialog(JDialog parent) {
        try {
            JDialog dialog = new ReceiveAddressDialog(parent);
            dialog.pack();
            dialog.setLocationRelativeTo(parent);
            dialog.setVisible(true);
        } catch (Exception exc) {
            ErrorLog.get().logException("Exception while displaying dialog", exc);
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
                ECKey key = new ECKey();
                editKey(key, -1);
            } else {
                int row = table.getSelectedRow();
                if (row < 0) {
                    UIRes.showErrorMessage(this,"Error", "No entry selected");
                } else {
                    row = table.convertRowIndexToModel(row);
                    switch (action) {
                        case "copy":
                            String address = (String)tableModel.getValueAt(row, 1);
                            StringSelection sel = new StringSelection(address);
                            Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
                            cb.setContents(sel, null);
                            break;
                        case "edit":
                            ECKey key = BTCLoader.keys.get(row);
                            editKey(key, row);
                            break;
                    }
                }
            }
        } catch (BlockStoreException exc) {
            ErrorLog.get().logException("Unable to update blockStore database", exc);
        } catch (Exception exc) {
        	ErrorLog.get().logException("Exception while processing action event", exc);
        }
    }

    private void editKey(ECKey key, int row) throws BlockStoreException {
        Address addr = key.toAddress();
        while (true) {
            addr = AddressEditDialog.showDialog(this, addr, false);
            if (addr == null)
                break;
            String label = addr.getLabel();
            boolean valid = true;
            synchronized(BTCLoader.lock) {
                for (ECKey chkKey : BTCLoader.keys) {
                    if (chkKey == key)
                        continue;
                    if (chkKey.getLabel().compareToIgnoreCase(label) == 0) {
                        UIRes.showErrorMessage(this,"Error", "Duplicate name specified");
                        valid = false;
                        break;
                    }
                }
                if (valid) {
                    if (row >= 0)
                        BTCLoader.keys.remove(row);
                    boolean added = false;
                    for (int i=0; i<BTCLoader.keys.size(); i++) {
                        ECKey chkKey = BTCLoader.keys.get(i);
                        if (chkKey.getLabel().compareToIgnoreCase(label) > 0) {
                            key.setLabel(label);
                            BTCLoader.keys.add(i, key);
                            added = true;
                            break;
                        }
                    }
                    if (!added) {
                        key.setLabel(label);
                        BTCLoader.keys.add(key);
                    }
                }
            }
            if (valid) {
                if (row >= 0) {
                    BTCLoader.blockStore.setKeyLabel(key);
                } else {
                    BTCLoader.blockStore.storeKey(key);
                    BTCLoader.bloomFilter.insert(key.getPubKey());
                    BTCLoader.bloomFilter.insert(key.getPubKeyHash());
                    Message filterMsg = FilterLoadMessage.buildFilterLoadMessage(null, BTCLoader.bloomFilter);
                    BTCLoader.networkHandler.broadcastMessage(filterMsg);
                }
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
            if (columnNames.length != columnClasses.length){
                throw new IllegalArgumentException("Number of names not same as number of classes");
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
            return BTCLoader.keys.size();
        }

        @Override
        public Object getValueAt(int row, int column) {
            if (row >= BTCLoader.keys.size()){
                throw new IndexOutOfBoundsException("Table row "+row+" is not valid");
            }
            Object value;
            ECKey key = BTCLoader.keys.get(row);
            switch (column) {
                case 0:
                    value = key.getLabel();
                    break;
                case 1:
                    value = key.toAddress().toString();
                    break;
                default:
                    throw new IndexOutOfBoundsException("Table column "+column+" is not valid");
            }
            return value;
        }
    }
}
