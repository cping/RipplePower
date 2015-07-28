package org.ripple.power.ui.btc;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.ripple.power.txns.btc.Address;
import org.ripple.power.txns.btc.AddressFormatException;
import org.ripple.power.txns.btc.BTCLoader;
import org.ripple.power.txns.btc.BlockStoreException;
import org.ripple.power.txns.btc.ECException;
import org.ripple.power.txns.btc.InventoryItem;
import org.ripple.power.txns.btc.InventoryMessage;
import org.ripple.power.txns.btc.Message;
import org.ripple.power.txns.btc.ScriptException;
import org.ripple.power.txns.btc.SignedInput;
import org.ripple.power.txns.btc.Transaction;
import org.ripple.power.txns.btc.TransactionOutput;
import org.ripple.power.txns.btc.VerificationException;
import org.ripple.power.ui.UIRes;
import org.ripple.power.ui.errors.ErrorLog;
import org.ripple.power.ui.view.ButtonPane;

public class SendDialog extends JDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final JComboBox<Object> addressField;

	private final JTextField amountField;

	private final JTextField feeField;

	private Address sendAddress;

	private BigInteger sendAmount;

	private BigInteger sendFee;

	public SendDialog(JDialog parent) {
		super(parent, "Send Coins", Dialog.ModalityType.DOCUMENT_MODAL);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		if (BTCLoader.addresses.isEmpty()) {
			addressField = new JComboBox<>();
		} else {
			String[] addrList = new String[BTCLoader.addresses.size()];
			int index = 0;
			for (Address addr : BTCLoader.addresses) {
				addrList[index++] = addr.getLabel();
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

		JPanel buttonPane = new ButtonPane(this, 10, new String[] { "Send",
				"send" }, new String[] { "Done", "done" });

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

	public static void showDialog(JDialog parent) {
		try {
			JDialog dialog = new SendDialog(parent);
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
			switch (action) {
			case "send":
				if (checkFields()) {
					String confirmText = String.format(
							"Do you want to send %s BTC?",
							BTCLoader.satoshiToString(sendAmount));
					if (UIRes.showConfirmMessage(this, "Send Coins",
							confirmText, "YES", "NO") == 0) {
						sendCoins();
					}
				}
				break;
			case "done":
				setVisible(false);
				dispose();
				break;
			}
		} catch (NumberFormatException exc) {
			UIRes.showErrorMessage(this, "Error",
					"Invalid numeric value entered");
		} catch (AddressFormatException exc) {
			UIRes.showErrorMessage(this, "Error", "Send address is not valid");
		} catch (BlockStoreException exc) {
			ErrorLog.logException("Unable to process send request", exc);
		} catch (Exception exc) {
			ErrorLog.logException("Exception while processing action event",
					exc);
		}
	}

	private boolean checkFields() throws AddressFormatException,
			NumberFormatException {

		String sendString = (String) addressField.getSelectedItem();
		if (sendString == null) {
			UIRes.showErrorMessage(this, "Error",
					"You must enter a send address");
			return false;
		}
		int index = addressField.getSelectedIndex();
		if (index < 0) {
			sendAddress = new Address(sendString);
		} else {
			sendAddress = BTCLoader.addresses.get(index);
		}

		String amountString = amountField.getText();
		if (amountString.isEmpty()) {
			UIRes.showErrorMessage(this, "Error",
					"You must enter the amount to send");
			return false;
		}
		sendAmount = BTCLoader.stringToSatoshi(amountString);
		if (sendAmount.compareTo(BTCLoader.DUST_TRANSACTION) < 0) {
			UIRes.showErrorMessage(this, "ERROR", String.format(
					"The minimum amount you can send is %s BTC",
					BTCLoader.satoshiToString(BTCLoader.DUST_TRANSACTION)));
			return false;
		}

		String feeString = feeField.getText();
		if (feeString.isEmpty()) {
			UIRes.showErrorMessage(this, "Enter",
					"You must enter a transaction fee");
			return false;
		}
		sendFee = BTCLoader.stringToSatoshi(feeString);
		if (sendFee.compareTo(BTCLoader.MIN_TX_FEE) < 0) {
			UIRes.showErrorMessage(this, "Error", String.format(
					"The minimun transaction fee is %s BTC",
					BTCLoader.satoshiToString(BTCLoader.MIN_TX_FEE)));
			return false;
		}
		return true;
	}

	private void sendCoins() throws BlockStoreException {
		List<SignedInput> inputList = BuildInputList.buildSignedInputs();
		Transaction tx = null;
		for (;;) {
			BigInteger totalAmount = sendAmount.add(sendFee);
			List<SignedInput> inputs = new ArrayList<SignedInput>(
					inputList.size());
			for (SignedInput input : inputList) {
				inputs.add(input);
				totalAmount = totalAmount.subtract(input.getValue());
				if (totalAmount.signum() <= 0) {
					break;
				}
			}
			if (totalAmount.signum() > 0) {
				UIRes.showErrorMessage(this, "Error",
						"There are not enough confirmed coins available");
				break;
			}
			List<TransactionOutput> outputs = new ArrayList<>(2);
			outputs.add(new TransactionOutput(0, sendAmount, sendAddress));
			BigInteger change = totalAmount.negate();
			if (change.compareTo(BTCLoader.DUST_TRANSACTION) > 0) {
				outputs.add(new TransactionOutput(1, change,
						BTCLoader.changeKey.toAddress()));
			}

			try {
				tx = new Transaction(inputs, outputs);
			} catch (ECException | ScriptException | VerificationException exc) {
				throw new BlockStoreException("Unable to create transaction",
						exc);
			}

			int length = tx.getBytes().length;
			BigInteger minFee = BigInteger.valueOf(length / 1000 + 1).multiply(
					BTCLoader.MIN_TX_FEE);
			if (minFee.compareTo(sendFee) <= 0) {
				break;
			}
			sendFee = minFee;
			tx = null;
		}

		if (tx != null) {
			BTCLoader.databaseHandler.processTransaction(tx);
			List<InventoryItem> invList = new ArrayList<InventoryItem>(1);
			invList.add(new InventoryItem(InventoryItem.INV_TX, tx.getHash()));
			Message invMsg = InventoryMessage.buildInventoryMessage(null,
					invList);
			BTCLoader.networkHandler.broadcastMessage(invMsg);
			UIRes.showInfoMessage(
					this,
					"Transaction Broadcast",
					String.format("Transaction broadcast to peer nodes\n%s",
							tx.getHash()));
		}
	}
}
