package org.ripple.power.ui;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.ripple.power.config.LSystem;
import org.ripple.power.wallet.WalletItem;

public class RPXRPSendDialog  extends JDialog implements ActionListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final JComboBox<Object> addressField;

    private final JTextField amountField;

    private final JTextField feeField;

    public RPXRPSendDialog(JFrame parent,WalletItem item) {
        super(parent, "Send Coins", Dialog.ModalityType.DOCUMENT_MODAL);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        if (LSystem.send_addresses.isEmpty()) {
            addressField = new JComboBox<>();
        } else {
            String[] addrList = new String[LSystem.send_addresses.size()];
            int index = 0;
            for (String addr : LSystem.send_addresses){
                addrList[index++] = addr;
            }
            addressField = new JComboBox<Object>(addrList);
        }
        addressField.setEditable(true);
        addressField.setSelectedIndex(-1);
        addressField.setPreferredSize(new Dimension(340, 25));
        JPanel addressPane = new JPanel();
        addressPane.add(new JLabel("Address  ", JLabel.RIGHT));
        addressPane.add(addressField);

        amountField = new JTextField("", 15);
        JPanel amountPane = new JPanel();
        amountPane.add(new JLabel("Amount  ", JLabel.RIGHT));
        amountPane.add(amountField);

        feeField = new JTextField("0.0001", 10);
        JPanel feePane = new JPanel();
        feePane.add(new JLabel("Fee  ", JLabel.RIGHT));
        feePane.add(feeField);
   
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));

        JButton button = new JButton("Send");
        button.setActionCommand("send");
        button.addActionListener(this);
        buttonPane.add(button);

        buttonPane.add(Box.createHorizontalStrut(10));

        button = new JButton("Done");
        button.setActionCommand("done");
        button.addActionListener(this);
        buttonPane.add(button);

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.setOpaque(true);
        contentPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        contentPane.add(addressPane);
        contentPane.add(Box.createVerticalStrut(15));
        contentPane.add(amountPane);
        contentPane.add(Box.createVerticalStrut(15));
        contentPane.add(feePane);
        contentPane.add(Box.createVerticalStrut(15));
        contentPane.add(buttonPane);
        setContentPane(contentPane);
    }

    public static void showDialog(JFrame parent,WalletItem item) {
        try {
            JDialog dialog = new RPXRPSendDialog(parent,item);
            dialog.pack();
            dialog.setLocationRelativeTo(parent);
            dialog.setVisible(true);
        } catch (Exception exc) {
           // Main.logException("Exception while displaying dialog", exc);
        }
    }


    @Override
    public void actionPerformed(ActionEvent ae) {
        try {
            String action = ae.getActionCommand();
            switch (action) {
                case "send":
                    if (checkFields()) {
                        String confirmText = String.format("Do you want to send %s BTC?",
                                                         "");
                        if (JOptionPane.showConfirmDialog(this, confirmText, "Send Coins", JOptionPane.YES_NO_OPTION,
                                                          JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
                            sendCoins();
                    }
                    break;
                case "done":
                    setVisible(false);
                    dispose();
                    break;
            }
        } catch (Exception exc) {
            JOptionPane.showMessageDialog(this, "Invalid numeric value entered", "Error",
                                          JOptionPane.ERROR_MESSAGE);
        } 
    }

    /**
     * Verify the fields
     *
     * @return                                  TRUE if the fields are valid
     * @throws      AddressFormatException      Send address is not valid
     * @throws      NumberFormatException       Invalid numeric value entered
     */
    private boolean checkFields() {
        //
        // Get the send address
        //
        String sendString = (String)addressField.getSelectedItem();
        if (sendString == null) {
            JOptionPane.showMessageDialog(this, "You must enter a send address", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        int index = addressField.getSelectedIndex();
        if (index < 0){
       //     sendAddress = new Address(sendString);
        }
        else{
       //     sendAddress = Parameters.addresses.get(index);
        }
        //
        // Get the send amount
        //
        String amountString = amountField.getText();
        if (amountString.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You must enter the amount to send", "Error",
                                          JOptionPane.ERROR_MESSAGE);
            return false;
        }
        /*sendAmount = Main.stringToSatoshi(amountString);
        if (sendAmount.compareTo(Parameters.DUST_TRANSACTION) < 0) {
            JOptionPane.showMessageDialog(this, String.format("The minimum amount you can send is %s BTC",
                                                              Main.satoshiToString(Parameters.DUST_TRANSACTION)),
                                                              "ERROR", JOptionPane.ERROR_MESSAGE);
            return false;
        }*/
        //
        // Get the fee amount
        //
        String feeString = feeField.getText();
        if (feeString.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You must enter a transaction fee", "Enter",
                                          JOptionPane.ERROR_MESSAGE);
            return false;
        }
    /*    sendFee = Main.stringToSatoshi(feeString);
        if (sendFee.compareTo(Parameters.MIN_TX_FEE) < 0) {
            JOptionPane.showMessageDialog(this, String.format("The minimun transaction fee is %s BTC",
                                                              Main.satoshiToString(Parameters.MIN_TX_FEE)),
                                                              "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }*/
        return true;
    }

    private void sendCoins()  {
    	
    }

}
