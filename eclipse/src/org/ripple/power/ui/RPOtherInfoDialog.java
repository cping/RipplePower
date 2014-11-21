package org.ripple.power.ui;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;

import org.ripple.power.config.LSystem;
import org.ripple.power.helper.HelperWindow;
import org.ripple.power.i18n.LangConfig;
import org.ripple.power.txns.TransactionTx;
import org.ripple.power.utils.SwingUtils;

public class RPOtherInfoDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RPTextBox _FlagsText;
	private RPTextBox _FlagsNameText;
	private RPTextArea _HashText;
	private RPTextArea _MetaText;
	private RPTextBox _OfferSequenceText;
	private RPTextBox _SequenceText;
	private RPTextBox _inLedgerText;
	private RPLabel _ledger_index_Label;
	private RPCButton _exitButton;
	private RPLabel _metaLabel;
	private RPLabel _hashLabel;
	private RPLabel _sequenceLabel;
	private RPLabel _offerSequenceLabel;
	private RPLabel _inLedgerLabel;
	private RPLabel _flagsLabel;
	private RPLabel _flagsNameLabel;
	private RPLabel _signingPubKeyLabel;
	private RPLabel _txnSignatureLabel;
	private RPTextArea _SigningPubKeyText;
	private RPTextArea _TxnSignatureText;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JScrollPane jScrollPane3;
	private javax.swing.JScrollPane jScrollPane4;
	private RPTextBox _ledger_index_Text;

	public static RPOtherInfoDialog showDialog(String text, JDialog parent,
			TransactionTx tx) {
		RPOtherInfoDialog dialog = new RPOtherInfoDialog(text, parent, tx);
		dialog.pack();
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
		return dialog;
	}

	public RPOtherInfoDialog(String text, JDialog parent, TransactionTx tx) {
		super(parent, text, Dialog.ModalityType.DOCUMENT_MODAL);
		setResizable(false);
		Dimension dim = new Dimension(510, 720);
		setPreferredSize(dim);
		setSize(dim);
		initComponents(tx);
	}

	private void initComponents(TransactionTx tx) {
		addWindowListener(HelperWindow.get());
		_metaLabel = new RPLabel();
		jScrollPane1 = new javax.swing.JScrollPane();
		_MetaText = new RPTextArea();
		_hashLabel = new RPLabel();
		_FlagsText = new RPTextBox();
		_FlagsNameText = new RPTextBox();
		_sequenceLabel = new RPLabel();
		_SequenceText = new RPTextBox();
		_offerSequenceLabel = new RPLabel();
		_OfferSequenceText = new RPTextBox();
		_inLedgerLabel = new RPLabel();
		_inLedgerText = new RPTextBox();
		_ledger_index_Label = new RPLabel();
		_ledger_index_Text = new RPTextBox();
		_exitButton = new RPCButton();
		jScrollPane2 = new javax.swing.JScrollPane();
		_HashText = new RPTextArea();
		_flagsLabel = new RPLabel();
		_flagsNameLabel = new RPLabel();
		_txnSignatureLabel = new RPLabel();
		_signingPubKeyLabel = new RPLabel();
		_TxnSignatureText = new RPTextArea();
		_SigningPubKeyText = new RPTextArea();
		jScrollPane3 = new javax.swing.JScrollPane();
		jScrollPane4 = new javax.swing.JScrollPane();
		
		getContentPane().setLayout(null);

		_metaLabel.setText("Meta_JSON");
		getContentPane().add(_metaLabel);
		_metaLabel.setBounds(10, 350, 100, 20);

		_MetaText.setColumns(20);
		_MetaText.setRows(5);
		jScrollPane1.setViewportView(_MetaText);
		_MetaText.setText(tx.meda);
		
		_flagsNameLabel.setText("Flags Name");
		getContentPane().add(_flagsNameLabel);
		_flagsNameLabel.setBounds(10, 425, 100, 20);

		getContentPane().add(_FlagsNameText);
		_FlagsNameText.setBounds(130, 430, 360, 30);
		_FlagsNameText.setText(tx.flagsName);
		
		_signingPubKeyLabel.setText("SigningPubKey");
		getContentPane().add(_signingPubKeyLabel);
		_signingPubKeyLabel.setBounds(10, 475, 100, 20);
		_SigningPubKeyText.setColumns(5);
		_SigningPubKeyText.setRows(5);
		jScrollPane3.setViewportView(_SigningPubKeyText);
		getContentPane().add(jScrollPane3);
		jScrollPane3.setBounds(130, 480, 360, 50);
		_SigningPubKeyText.setText(tx.signingPubKey);
		
		_txnSignatureLabel.setText("TxnSignature");
		getContentPane().add(_txnSignatureLabel);
		_txnSignatureLabel.setBounds(10, 550, 100, 20);
		_TxnSignatureText.setColumns(5);
		_TxnSignatureText.setRows(5);
		jScrollPane4.setViewportView(_TxnSignatureText);
		getContentPane().add(jScrollPane4);
		jScrollPane4.setBounds(130, 550, 360, 80);
		_TxnSignatureText.setText(tx.txnSignature);
		
		getContentPane().add(jScrollPane1);
		jScrollPane1.setBounds(130, 350, 360, 60);

		_hashLabel.setText("Hash");
		getContentPane().add(_hashLabel);
		_hashLabel.setBounds(10, 20, 70, 15);
		getContentPane().add(_FlagsText);
		_FlagsText.setBounds(130, 300, 360, 30);
		_FlagsText.setText(String.valueOf(tx.flags));

		_sequenceLabel.setText("Sequence");
		getContentPane().add(_sequenceLabel);
		_sequenceLabel.setBounds(10, 100, 70, 20);
		getContentPane().add(_SequenceText);
		_SequenceText.setBounds(130, 101, 360, 30);
		_SequenceText.setText(String.valueOf(tx.sequence));

		_offerSequenceLabel.setText("OfferSequence");
		getContentPane().add(_offerSequenceLabel);
		_offerSequenceLabel.setBounds(10, 150, 100, 20);
		getContentPane().add(_OfferSequenceText);
		_OfferSequenceText.setBounds(130, 150, 360, 30);
		_OfferSequenceText.setText(String.valueOf(tx.offersSequence));

		_inLedgerLabel.setText("inLedger");
		getContentPane().add(_inLedgerLabel);
		_inLedgerLabel.setBounds(10, 200, 100, 20);
		getContentPane().add(_inLedgerText);
		_inLedgerText.setBounds(130, 200, 360, 30);
		_inLedgerText.setText(String.valueOf(tx.inLedger));

		_ledger_index_Label.setText("ledger_index");
		getContentPane().add(_ledger_index_Label);
		_ledger_index_Label.setBounds(10, 250, 100, 20);
		getContentPane().add(_ledger_index_Text);
		_ledger_index_Text.setBounds(130, 250, 360, 30);
		_ledger_index_Text.setText(String.valueOf(tx.ledgerIndex));

		_exitButton.setText(LangConfig.get(this, "exit", "Exit"));
		_exitButton.setFont(UIRes.getFont());
		getContentPane().add(_exitButton);
		_exitButton.setBounds(410, 650, 81, 30);
		_exitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtils.close(RPOtherInfoDialog.this);
			}
		});

		_HashText.setColumns(5);
		_HashText.setRows(5);
		jScrollPane2.setViewportView(_HashText);
		_HashText.setText(tx.hash);

		getContentPane().add(jScrollPane2);
		jScrollPane2.setBounds(130, 20, 360, 60);

		_flagsLabel.setText("Flags");
		getContentPane().add(_flagsLabel);
		_flagsLabel.setBounds(10, 300, 100, 20);
		getContentPane().setBackground(LSystem.dialogbackground);
		pack();
	}
}
