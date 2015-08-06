package org.ripple.power.ui.btc;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.ripple.power.txns.btc.BTCLoader;
import org.ripple.power.txns.btc.ECException;
import org.ripple.power.txns.btc.ECKey;
import org.ripple.power.ui.UIRes;
import org.ripple.power.ui.errors.ErrorLog;
import org.ripple.power.ui.view.ButtonPane;

public class SignDialog extends JDialog implements ActionListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    private final JComboBox<Object> nameField;

    private final JTextArea messageField;

    private final JScrollPane scrollPane;

    private final JTextField signatureField;

    public SignDialog(JDialog parent) {
        super(parent, "Sign Message", Dialog.ModalityType.DOCUMENT_MODAL);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        String[] keyLabels = new String[BTCLoader.keys.size()];
        int index = 0;
        for (ECKey key : BTCLoader.keys){
            keyLabels[index++] = key.getLabel();
        }
        nameField = new JComboBox<Object>(keyLabels);
        nameField.setPreferredSize(new Dimension(200, 25));
        JPanel namePane = new JPanel();
        namePane.add(new JLabel("Key  ", JLabel.RIGHT));
        namePane.add(nameField);
        messageField = new JTextArea(10, 70);
        messageField.setLineWrap(true);
        messageField.setWrapStyleWord(true);
        messageField.setFont(nameField.getFont());
        scrollPane = new JScrollPane(messageField);
        JPanel messagePane = new JPanel();
        messagePane.add(new JLabel("Message  ", JLabel.RIGHT));
        messagePane.add(scrollPane);
        signatureField = new JTextField("", 70);
        signatureField.setEditable(false);
        JPanel signaturePane = new JPanel();
        signaturePane.add(new JLabel("Signature  ", JLabel.RIGHT));
        signaturePane.add(signatureField);
        JPanel buttonPane = new ButtonPane(this, 10, new String[] {"Sign", "sign"},
                                                     new String[] {"Copy", "copy"},
                                                     new String[] {"Done", "done"});
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.setOpaque(true);
        contentPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        contentPane.add(namePane);
        contentPane.add(Box.createVerticalStrut(15));
        contentPane.add(messagePane);
        contentPane.add(Box.createVerticalStrut(15));
        contentPane.add(signaturePane);
        contentPane.add(Box.createVerticalStrut(15));
        contentPane.add(buttonPane);
        setContentPane(contentPane);
    }

    public static void showDialog(JDialog parent) {
        try {
            JDialog dialog = new SignDialog(parent);
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
            switch (action) {
                case "sign":
                    String message = messageField.getText();
                    if (message.length() == 0) {
                        UIRes.showErrorMessage(this, "Error", "You must enter the message text to sign");
                    } else {
                        int index = nameField.getSelectedIndex();
                        ECKey key = BTCLoader.keys.get(index);
                        String signature = key.signMessage(message);
                        signatureField.setText(signature);
                    }
                    break;
                case "copy":
                    String signature = signatureField.getText();
                    StringSelection sel = new StringSelection(signature);
                    Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
                    cb.setContents(sel, null);
                    break;
                case "done":
                    setVisible(false);
                    dispose();
                    break;
            }
        } catch (ECException exc) {
            ErrorLog.get().logException("Unable to sign message", exc);
        } catch (Exception exc) {
            ErrorLog.get().logException("Exception while processing action event", exc);
        }
    }
}
