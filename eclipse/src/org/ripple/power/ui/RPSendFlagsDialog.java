package org.ripple.power.ui;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JDialog;

import org.ripple.power.config.LSystem;
import org.ripple.power.helper.HelperWindow;
import org.ripple.power.txns.Currencies;
import org.ripple.power.txns.Gateway;
import org.ripple.power.txns.TransactionFlagMap;
import org.ripple.power.utils.SwingUtils;
import org.ripple.power.wallet.WalletItem;

public class RPSendFlagsDialog extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RPLabel _amountGatewayLabel;
	private RPLabel _amountLabel;
	private RPTextBox _amountText;
	private RPComboBox _amount_cur_item;
	private RPComboBox _amount_gateway_item;
	private RPLabel _amountcurLabel;
	private RPLabel _deliveredGatewayLabel;
	private RPLabel _deliveredLabel;
	private RPTextBox _deliveredText;
	private RPComboBox _delivered_cur_item;
	private RPComboBox _delivered_gateway_item;
	private RPLabel _deliveredcurLabel;
	private RPCButton _exitButton;
	private RPLabel _feeLabel;
	private RPTextBox _feeText;
	private RPComboBox _flagsItem;
	private RPLabel _flagsLabel;
	private RPLabel _idLabel;
	private RPTextBox _idText;
	private RPLabel _memoLabel;
	private RPTextBox _memoText;
	private RPLabel _recipientLabel;
	private RPTextBox _recipientText;
	private RPCButton _sendButton;
	private RPLabel _sendmaxGatewayLabel;
	private RPLabel _sendmaxLabel;
	private RPTextBox _sendmaxText;
	private RPComboBox _sendmax_cur_item;
	private RPComboBox _sendmax_gateway_item;
	private RPLabel _sendmaxcurLabel;
	private javax.swing.JSeparator _spLine;
	private RPLabel _tagLabel;
	private RPTextBox _tagText;

	private WalletItem _item;

	public static RPSendFlagsDialog showDialog(String title, Window parent,
			WalletItem item) {
		RPSendFlagsDialog dialog = new RPSendFlagsDialog(title, parent, item);
		dialog.pack();
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
		return dialog;
	}

	public RPSendFlagsDialog(String title, Window parent, WalletItem item) {
		super(parent, title, Dialog.ModalityType.DOCUMENT_MODAL);
		this._item = item;
		addWindowListener(HelperWindow.get());
		setResizable(false);
		Dimension dim = new Dimension(815, 565);
		setPreferredSize(dim);
		setSize(dim);
		initComponents();
	}

	private void initComponents() {

		_amountGatewayLabel = new RPLabel();
		_amountLabel = new RPLabel();
		_amountText = new RPTextBox();
		_amount_gateway_item = new RPComboBox();
		_amountcurLabel = new RPLabel();
		_amount_cur_item = new RPComboBox();
		_amount_cur_item.setEditable(true);
		_sendmaxGatewayLabel = new RPLabel();
		_flagsLabel = new RPLabel();
		_sendmaxText = new RPTextBox();
		_sendmax_gateway_item = new RPComboBox();
		_sendmaxcurLabel = new RPLabel();
		_flagsItem = new RPComboBox();
		_deliveredGatewayLabel = new RPLabel();
		_memoLabel = new RPLabel();
		_memoText = new RPTextBox();
		_delivered_gateway_item = new RPComboBox();
		_deliveredcurLabel = new RPLabel();
		_delivered_cur_item = new RPComboBox();
		_delivered_cur_item.setEditable(true);
		_deliveredLabel = new RPLabel();
		_deliveredText = new RPTextBox();
		_recipientLabel = new RPLabel();
		_recipientText = new RPTextBox();
		_sendmaxLabel = new RPLabel();
		_sendmax_cur_item = new RPComboBox();
		_sendmax_cur_item.setEditable(true);
		_tagLabel = new RPLabel();
		_tagText = new RPTextBox();
		_feeText = new RPTextBox();
		_feeLabel = new RPLabel();
		_idText = new RPTextBox();
		_idLabel = new RPLabel();
		_spLine = new javax.swing.JSeparator();
		_exitButton = new RPCButton();
		_sendButton = new RPCButton();

		getContentPane().setLayout(null);

		ArrayList<String> curList = Currencies.values();
		Object[] curlist = curList.toArray();

		ArrayList<String> gateways = Gateway.gatewayList();
		Object[] gatewayslist = gateways.toArray();
		
		ArrayList<String> flags = TransactionFlagMap.values();
		Object[] flagslist = flags.toArray();
		
		Font font = UIRes.getFont();

		_amountGatewayLabel.setFont(font); // NOI18N
		_amountGatewayLabel.setText("Gateway");
		getContentPane().add(_amountGatewayLabel);
		_amountGatewayLabel.setBounds(530, 60, 70, 33);

		_amountLabel.setFont(font); // NOI18N
		_amountLabel.setText("Amount");
		getContentPane().add(_amountLabel);
		_amountLabel.setBounds(10, 60, 60, 30);

		_amountText.setFont(font); // NOI18N
		getContentPane().add(_amountText);
		_amountText.setBounds(140, 60, 190, 30);

		_amount_gateway_item.setItemModel(gatewayslist);
		getContentPane().add(_amount_gateway_item);
		_amount_gateway_item.setBounds(600, 60, 200, 30);

		_amountcurLabel.setFont(font); // NOI18N
		_amountcurLabel.setText("Currency");
		getContentPane().add(_amountcurLabel);
		_amountcurLabel.setBounds(340, 60, 80, 33);

		_amount_cur_item.setItemModel(curlist);
		getContentPane().add(_amount_cur_item);
		_amount_cur_item.setBounds(420, 60, 100, 30);

		_sendmaxGatewayLabel.setFont(font); // NOI18N
		_sendmaxGatewayLabel.setText("Gateway");
		getContentPane().add(_sendmaxGatewayLabel);
		_sendmaxGatewayLabel.setBounds(530, 110, 70, 33);

		_flagsLabel.setFont(font); // NOI18N
		_flagsLabel.setText("Flags");
		getContentPane().add(_flagsLabel);
		_flagsLabel.setBounds(10, 310, 130, 30);

		_sendmaxText.setFont(font); // NOI18N
		getContentPane().add(_sendmaxText);
		_sendmaxText.setBounds(140, 110, 190, 30);

		_sendmax_gateway_item.setItemModel(gatewayslist);
		getContentPane().add(_sendmax_gateway_item);
		_sendmax_gateway_item.setBounds(600, 110, 200, 30);

		_sendmaxcurLabel.setFont(font); // NOI18N
		_sendmaxcurLabel.setText("Currency");
		getContentPane().add(_sendmaxcurLabel);
		_sendmaxcurLabel.setBounds(340, 110, 80, 33);

		_flagsItem.setItemModel(flagslist);
		_flagsItem.setSelectedItem("Empty");
		getContentPane().add(_flagsItem);
		_flagsItem.setBounds(140, 310, 250, 30);

		_deliveredGatewayLabel.setFont(font); // NOI18N
		_deliveredGatewayLabel.setText("Gateway");
		getContentPane().add(_deliveredGatewayLabel);
		_deliveredGatewayLabel.setBounds(530, 160, 70, 33);

		_memoLabel.setFont(font); // NOI18N
		_memoLabel.setText("Memo");
		getContentPane().add(_memoLabel);
		_memoLabel.setBounds(10, 360, 130, 30);

		_memoText.setFont(font); // NOI18N
		getContentPane().add(_memoText);
		_memoText.setBounds(140, 360, 660, 30);

		_delivered_gateway_item.setItemModel(gatewayslist);
		getContentPane().add(_delivered_gateway_item);
		_delivered_gateway_item.setBounds(600, 160, 200, 30);

		_deliveredcurLabel.setFont(font); // NOI18N
		_deliveredcurLabel.setText("Currency");
		getContentPane().add(_deliveredcurLabel);
		_deliveredcurLabel.setBounds(340, 160, 80, 33);

		_delivered_cur_item.setItemModel(curlist);
		getContentPane().add(_delivered_cur_item);
		_delivered_cur_item.setBounds(420, 160, 100, 30);

		_deliveredLabel.setFont(font); // NOI18N
		_deliveredLabel.setText("Delivered Amount");
		getContentPane().add(_deliveredLabel);
		_deliveredLabel.setBounds(10, 160, 130, 30);

		_deliveredText.setFont(font); // NOI18N
		getContentPane().add(_deliveredText);
		_deliveredText.setBounds(140, 160, 190, 30);

		_recipientLabel.setFont(font); // NOI18N
		_recipientLabel.setText("Recipient");
		getContentPane().add(_recipientLabel);
		_recipientLabel.setBounds(10, 10, 130, 30);

		_recipientText.setFont(font); // NOI18N
		getContentPane().add(_recipientText);
		_recipientText.setBounds(140, 10, 660, 30);

		_sendmaxLabel.setFont(font); // NOI18N
		_sendmaxLabel.setText("SendMax");
		getContentPane().add(_sendmaxLabel);
		_sendmaxLabel.setBounds(10, 110, 60, 30);

		_sendmax_cur_item.setItemModel(curlist);
		getContentPane().add(_sendmax_cur_item);
		_sendmax_cur_item.setBounds(420, 110, 100, 30);

		_tagLabel.setFont(font); // NOI18N
		_tagLabel.setText("DestinationTag");
		getContentPane().add(_tagLabel);
		_tagLabel.setBounds(10, 210, 130, 30);

		_tagText.setFont(font); // NOI18N
		_tagText.setText("0");
		getContentPane().add(_tagText);
		_tagText.setBounds(140, 210, 380, 30);

		_feeText.setFont(font); // NOI18N
		getContentPane().add(_feeText);
		_feeText.setBounds(140, 410, 190, 30);

		_feeLabel.setFont(font); // NOI18N
		_feeLabel.setText("Fee");
		getContentPane().add(_feeLabel);
		_feeLabel.setBounds(10, 410, 60, 30);

		_idText.setFont(font); // NOI18N
		getContentPane().add(_idText);
		_idText.setBounds(140, 260, 660, 30);

		_idLabel.setFont(font); // NOI18N
		_idLabel.setText("InvoiceID");
		getContentPane().add(_idLabel);
		_idLabel.setBounds(10, 260, 130, 30);
		getContentPane().add(_spLine);
		_spLine.setBounds(0, 460, 820, 10);

		_exitButton.setText(UIMessage.exit);
		_exitButton.setFont(font);
		getContentPane().add(_exitButton);
		_exitButton.setBounds(710, 480, 90, 40);
		_exitButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtils.close(RPSendFlagsDialog.this);	
			}
		});

		_sendButton.setText(UIMessage.send);
		_sendButton.setFont(font);
		getContentPane().add(_sendButton);
		_sendButton.setBounds(610, 480, 90, 40);

		_amountText.setText(LSystem.getMinSend());
		_feeText.setText(LSystem.getFee());
		getContentPane().setBackground(LSystem.dialogbackground);

		pack();
	}
}
