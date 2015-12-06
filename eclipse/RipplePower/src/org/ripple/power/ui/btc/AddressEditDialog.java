package org.ripple.power.ui.btc;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.ripple.power.txns.btc.Address;
import org.ripple.power.txns.btc.AddressFormatException;
import org.ripple.power.ui.UIRes;
import org.ripple.power.ui.view.ABaseDialog;
import org.ripple.power.ui.view.ButtonPane;
import org.ripple.power.ui.view.log.ErrorLog;

public class AddressEditDialog extends ABaseDialog implements ActionListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    private Address updatedAddress;

    private final JTextField nameField;

    private final JTextField addressField;

    public AddressEditDialog(JDialog parent, Address address, boolean editAddress) {
        super(parent, "Edit Address", Dialog.ModalityType.DOCUMENT_MODAL);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        JPanel editPane = new JPanel();
        editPane.setLayout(new BoxLayout(editPane, BoxLayout.X_AXIS));

        nameField = new JTextField(address!=null?address.getLabel():"", 32);
        addressField = new JTextField(address!=null?address.toString():"", 34);
        if (!editAddress)
            addressField.setEditable(false);

        JPanel namePane = new JPanel();
        namePane.add(new JLabel("Name:", JLabel.RIGHT));
        namePane.add(nameField);
        editPane.add(namePane);

        editPane.add(Box.createHorizontalStrut(10));

        JPanel addressPane = new JPanel();
        addressPane.add(new JLabel("Address:", JLabel.RIGHT));
        addressPane.add(addressField);
        editPane.add(addressPane);

        JPanel buttonPane = new ButtonPane(this, 10, new String[] {"Save", "save"},
                                                     new String[] {"Cancel", "cancel"});

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.setOpaque(true);
        contentPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        contentPane.add(editPane);
        contentPane.add(Box.createVerticalStrut(15));
        contentPane.add(buttonPane);
        setContentPane(contentPane);
    }

    public static Address showDialog(JDialog parent, Address address, boolean editAddress) {
        Address updatedAddress = null;
        try {
            AddressEditDialog dialog = new AddressEditDialog(parent, address, editAddress);
            dialog.pack();
            dialog.setLocationRelativeTo(parent);
            dialog.setVisible(true);
            updatedAddress = dialog.updatedAddress;
        } catch (Exception exc) {
        	ErrorLog.get().logException("Exception while displaying dialog", exc);
        }
        return updatedAddress;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        try {
            String action = ae.getActionCommand();
            switch (action) {
                case "save":
                    if (processFields()) {
                        setVisible(false);
                        dispose();
                    }
                    break;
                case "cancel":
                    updatedAddress = null;
                    setVisible(false);
                    dispose();
                    break;
            }
        } catch (Exception exc) {
        	ErrorLog.get().logException("Exception while processing action event", exc);
        }
    }

    private boolean processFields() {
        String name = nameField.getText();
        String addr = addressField.getText();
        if (name.isEmpty()) {
            UIRes.showErrorMessage(this, "Error", "You must specify a name");
            return false;
        }
        if (name.length() > 64) {
        	UIRes.showErrorMessage(this,"Error", "The name must be 64 characters or less");
            return false;
        }
        if (addr.isEmpty()) {
        	UIRes.showErrorMessage(this,  "Error","You must specify an address");
            return false;
        }
        boolean valid = true;
        try {
            updatedAddress = new Address(addr, name);
        } catch (AddressFormatException exc) {
        	UIRes.showErrorMessage(this, "Error", "Address is not valid");
            valid = false;
        }
        return valid;
    }
}
