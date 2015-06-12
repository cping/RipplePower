package org.ripple.power.ui;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.JDialog;

import org.ripple.power.helper.HelperWindow;
import org.ripple.power.i18n.LangConfig;
import org.ripple.power.txns.AccountFind;
import org.ripple.power.txns.TransactionTx;
import org.ripple.power.txns.Updateable;
import org.ripple.power.ui.RPToast.Style;
import org.ripple.power.utils.GraphicsUtils;
import org.ripple.power.utils.SwingUtils;

public class RPHashInfoDialog extends JDialog implements WindowListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RPTextBox _AmountText;
	private RPTextBox _DateText;
	private RPTextBox _DestinationText;
	private RPTextBox _TagText;
	private RPTextBox _FeeText;
	private RPTextBox _FlagsNameText;
	private RPTextBox _FlagsText;
	private RPTextBox _Ledger_index_Text;
	private RPTextArea _MetaText;
	private RPTextBox _OfferSequenceText;
	private RPTextBox _SendMaxText;
	private RPTextBox _SequenceText;
	private RPTextArea _SigningPubKeyText;
	private RPTextArea _TxnSignatureText;
	private RPTextBox _accountText;
	private RPCButton _exitButton;
	private RPTextBox _hashFindText;
	private RPTextBox _inLedgerText;
	private RPLabel _ledger_index_Text;
	private RPCButton _loadButton;
	private RPLabel _hashLabel;
	private RPLabel _signingPubKeyLabel;
	private RPLabel _txnSignatureLabel;
	private RPLabel _accountLabel;
	private RPLabel _destinationLabel;
	private RPLabel _meta_JSON_Label;
	private RPLabel _amountLabel;
	private RPLabel _sendMaxLabel;
	private RPLabel _feeLabel;
	private RPLabel _tagLabel;
	private RPLabel _offerSequenceLabel;
	private RPLabel _inLedgerLabel;
	private RPLabel _sequenceLabel;
	private RPLabel _dateLabel;
	private RPLabel _flagsLabel;
	private RPLabel _flagsNameLabel;
	private javax.swing.JScrollPane _panelOne;
	private javax.swing.JScrollPane _panelTwo;
	private javax.swing.JScrollPane _panelThree;

	private TransactionTx _trTx;

	private ArrayList<WaitDialog> _waitDialogs = new ArrayList<WaitDialog>(10);

	public static RPHashInfoDialog showDialog(Window parent) {
		return showDialog(parent, null);
	}

	public static RPHashInfoDialog showDialog(Window parent, TransactionTx tx) {
		RPHashInfoDialog dialog = new RPHashInfoDialog(parent, tx);
		dialog.pack();
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
		return dialog;
	}

	public RPHashInfoDialog(Window parent, TransactionTx tx) {
		super(parent, "Transaction Hash", Dialog.ModalityType.DOCUMENT_MODAL);
		this._trTx = tx;
		addWindowListener(HelperWindow.get());
		setIconImage(UIRes.getIcon());
		setResizable(false);
		Dimension dim = new Dimension(505, 750);
		setPreferredSize(dim);
		setSize(dim);
		initComponents();
	}

	private void initComponents() {

		_meta_JSON_Label = new RPLabel();
		_hashLabel = new RPLabel();
		_offerSequenceLabel = new RPLabel();
		_inLedgerLabel = new RPLabel();
		_dateLabel = new RPLabel();
		_flagsLabel = new RPLabel();
		_ledger_index_Text = new RPLabel();
		_sequenceLabel = new RPLabel();
		_flagsNameLabel = new RPLabel();
		_amountLabel = new RPLabel();
		_sendMaxLabel = new RPLabel();
		_accountLabel = new RPLabel();
		_signingPubKeyLabel = new RPLabel();
		_txnSignatureLabel = new RPLabel();
		_feeLabel = new RPLabel();
		_destinationLabel = new RPLabel();
		_tagLabel = new RPLabel();

		Font font = GraphicsUtils.getFont(Font.SANS_SERIF, 0, 12);

		_meta_JSON_Label.setFont(font);
		_hashLabel.setFont(font);
		_offerSequenceLabel.setFont(font);
		_inLedgerLabel.setFont(font);
		_dateLabel.setFont(font);
		_flagsLabel.setFont(font);
		_ledger_index_Text.setFont(font);
		_sequenceLabel.setFont(font);
		_flagsNameLabel.setFont(font);
		_amountLabel.setFont(font);
		_sendMaxLabel.setFont(font);
		_accountLabel.setFont(font);
		_signingPubKeyLabel.setFont(font);
		_txnSignatureLabel.setFont(font);
		_feeLabel.setFont(font);
		_destinationLabel.setFont(font);
		_tagLabel.setFont(font);

		_MetaText = new RPTextArea();
		_DateText = new RPTextBox();
		_hashFindText = new RPTextBox();
		_OfferSequenceText = new RPTextBox();
		_inLedgerText = new RPTextBox();
		_Ledger_index_Text = new RPTextBox();
		_loadButton = new RPCButton();
		_SigningPubKeyText = new RPTextArea();
		_FlagsText = new RPTextBox();
		_FlagsNameText = new RPTextBox();
		_FeeText = new RPTextBox();
		_SequenceText = new RPTextBox();
		_TxnSignatureText = new RPTextArea();
		_accountText = new RPTextBox();
		_exitButton = new RPCButton();
		_AmountText = new RPTextBox();
		_SendMaxText = new RPTextBox();
		_DestinationText = new RPTextBox();
		_TagText = new RPTextBox();

		_panelOne = new javax.swing.JScrollPane();
		_panelTwo = new javax.swing.JScrollPane();
		_panelThree = new javax.swing.JScrollPane();

		getContentPane().setLayout(null);

		_meta_JSON_Label.setText("Meta_JSON");
		getContentPane().add(_meta_JSON_Label);
		_meta_JSON_Label.setBounds(10, 560, 100, 20);

		_MetaText.setColumns(20);
		_MetaText.setRows(5);
		_panelOne.setViewportView(_MetaText);

		getContentPane().add(_panelOne);
		_panelOne.setBounds(110, 560, 380, 40);

		getContentPane().add(_DateText);
		_DateText.setBounds(340, 460, 150, 30);

		_hashLabel.setText("Transaction");
		getContentPane().add(_hashLabel);
		_hashLabel.setBounds(10, 10, 100, 20);
		getContentPane().add(_hashFindText);
		_hashFindText.setBounds(110, 10, 300, 30);
		_hashFindText.setFont(GraphicsUtils.getFont(10));

		_offerSequenceLabel.setText("OfferSequence");
		getContentPane().add(_offerSequenceLabel);
		_offerSequenceLabel.setBounds(10, 360, 100, 20);
		getContentPane().add(_OfferSequenceText);
		_OfferSequenceText.setBounds(110, 360, 130, 30);

		_inLedgerLabel.setText("inLedger");
		getContentPane().add(_inLedgerLabel);
		_inLedgerLabel.setBounds(10, 410, 100, 20);
		getContentPane().add(_inLedgerText);
		_inLedgerText.setBounds(110, 410, 130, 30);

		_ledger_index_Text.setText("ledger_index");
		getContentPane().add(_ledger_index_Text);
		_ledger_index_Text.setBounds(10, 460, 100, 20);
		getContentPane().add(_Ledger_index_Text);
		_Ledger_index_Text.setBounds(110, 460, 130, 30);

		_SigningPubKeyText.setColumns(20);
		_SigningPubKeyText.setRows(5);
		_panelTwo.setViewportView(_SigningPubKeyText);

		getContentPane().add(_panelTwo);
		_panelTwo.setBounds(110, 150, 380, 60);

		_dateLabel.setText("Date");
		getContentPane().add(_dateLabel);
		_dateLabel.setBounds(260, 460, 80, 20);

		_flagsLabel.setText("Flags");
		getContentPane().add(_flagsLabel);
		_flagsLabel.setBounds(10, 510, 100, 20);
		getContentPane().add(_FlagsText);
		_FlagsText.setBounds(110, 510, 130, 30);

		_sequenceLabel.setText("Sequence");
		getContentPane().add(_sequenceLabel);
		_sequenceLabel.setBounds(10, 310, 70, 20);

		_flagsNameLabel.setText("FlagsName");
		getContentPane().add(_flagsNameLabel);
		_flagsNameLabel.setBounds(260, 510, 80, 20);
		getContentPane().add(_FlagsNameText);
		_FlagsNameText.setBounds(340, 510, 150, 30);

		_amountLabel.setText("Amount");
		getContentPane().add(_amountLabel);
		_amountLabel.setBounds(260, 360, 80, 20);
		getContentPane().add(_FeeText);
		_FeeText.setBounds(340, 410, 150, 30);

		_signingPubKeyLabel.setText("SigningPubKey");
		getContentPane().add(_signingPubKeyLabel);
		_signingPubKeyLabel.setBounds(10, 150, 100, 20);
		getContentPane().add(_SequenceText);
		_SequenceText.setBounds(110, 310, 130, 30);

		_txnSignatureLabel.setText("TxnSignature");
		getContentPane().add(_txnSignatureLabel);
		_txnSignatureLabel.setBounds(10, 230, 100, 20);

		_TxnSignatureText.setColumns(20);
		_TxnSignatureText.setRows(5);
		_panelThree.setViewportView(_TxnSignatureText);

		getContentPane().add(_panelThree);
		_panelThree.setBounds(110, 230, 380, 60);

		_accountLabel.setText("Account");
		getContentPane().add(_accountLabel);
		_accountLabel.setBounds(10, 50, 100, 20);
		getContentPane().add(_accountText);
		_accountText.setBounds(110, 50, 380, 30);

		getContentPane().add(_AmountText);
		_AmountText.setBounds(340, 360, 150, 30);

		_sendMaxLabel.setText("SendMax");
		getContentPane().add(_sendMaxLabel);
		_sendMaxLabel.setBounds(260, 310, 80, 20);
		getContentPane().add(_SendMaxText);
		_SendMaxText.setBounds(340, 310, 150, 30);

		_feeLabel.setText("Fee");
		getContentPane().add(_feeLabel);
		_feeLabel.setBounds(260, 410, 80, 20);
		getContentPane().add(_DestinationText);
		_DestinationText.setBounds(110, 100, 380, 30);

		_destinationLabel.setText("Destination");
		getContentPane().add(_destinationLabel);
		_destinationLabel.setBounds(10, 100, 100, 20);

		_tagLabel.setText("DestinationTag");
		getContentPane().add(_tagLabel);
		_tagLabel.setBounds(10, 620, 100, 20);
		getContentPane().add(_TagText);
		_TagText.setBounds(110, 620, 380, 30);

		getContentPane().setBackground(UIConfig.dialogbackground);

		_loadButton.setText(LangConfig.get(this, "load", "Load"));
		_loadButton.setFont(UIRes.getFont());
		getContentPane().add(_loadButton);
		_loadButton.setBounds(421, 10, 70, 30);
		_loadButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				loadHash(_hashFindText.getText().trim());
			}
		});

		_exitButton.setText(LangConfig.get(this, "exit", "Exit"));
		_exitButton.setFont(UIRes.getFont());
		getContentPane().add(_exitButton);
		_exitButton.setBounds(400, 670, 90, 40);
		_exitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtils.close(RPHashInfoDialog.this);
			}
		});

		if (_trTx != null) {
			loadTx(_trTx);
		}

		pack();
	}

	private void loadTx(final TransactionTx tx) {
		if (tx.hash != null && tx.hash.length() != 0) {
			_hashFindText.setText(tx.hash);
		}
		_MetaText.setText(tx.meda);
		_DateText.setText(tx.date);
		_OfferSequenceText.setText(String.valueOf(tx.offersSequence));
		_inLedgerText.setText(String.valueOf(tx.inLedger));
		_Ledger_index_Text.setText(String.valueOf(tx.ledgerIndex));
		_SigningPubKeyText.setText(tx.signingPubKey);
		_FlagsText.setText(String.valueOf(tx.flags));
		_FlagsNameText.setText(tx.flagsName);
		_FeeText.setText(tx.fee);
		_SequenceText.setText(String.valueOf(tx.sequence));
		_TxnSignatureText.setText(tx.txnSignature);
		_accountText.setText(tx.account);
		if (tx.currency != null) {
			_AmountText.setText(tx.currency.toString());
		}
		if (tx.sendMax != null) {
			_SendMaxText.setText(tx.sendMax.toString());
		}
		_DestinationText.setText(tx.destination);
		_TagText.setText(String.valueOf(tx.destinationTag));
	}

	private void loadHash(final String hash) {
		if (!AccountFind.is256hash(hash)) {
			RPToast.makeText(RPHashInfoDialog.this,
					"Here only query transaction Hash !", Style.ERROR)
					.display();
			return;
		}
		if (_hashFindText.getText().trim().equals(hash.trim())) {
			return;
		}
		final WaitDialog wait = WaitDialog.showDialog(this);
		_waitDialogs.add(wait);
		final AccountFind find = new AccountFind();
		final TransactionTx tx = new TransactionTx();
		find.processTxHash(hash, tx, new Updateable() {

			@Override
			public void action(Object o) {
				loadTx(tx);
				wait.closeDialog();
			}
		});
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosing(WindowEvent e) {
		if (_waitDialogs != null) {
			for (WaitDialog wait : _waitDialogs) {
				if (wait != null) {
					wait.closeDialog();
				}
			}
		}
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub

	}
}
