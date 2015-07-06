package org.ripple.power.ui;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.ripple.power.RippleBlobObj;
import org.ripple.power.config.LSystem;
import org.ripple.power.helper.HelperWindow;
import org.ripple.power.i18n.LangConfig;
import org.ripple.power.txns.BTC2Ripple;
import org.ripple.power.txns.History;
import org.ripple.power.ui.view.RPLabel;
import org.ripple.power.ui.view.RPTextBox;
import org.ripple.power.ui.view.RPToast;
import org.ripple.power.ui.view.RPToast.Style;
import org.ripple.power.utils.StringUtils;
import org.ripple.power.utils.SwingUtils;

public class RPConfigDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private RPCButton _blockchainButton;
	private RPLabel _blockchainLabel;
	private RPLabel _btc2rippleLabel;
	private RPTextBox _btc2rippleText;
	private RPCButton _exitButton;
	private RPLabel _historyLabel;
	private RPTextBox _historyText;
	private RPLabel _min_amount_label;
	private RPTextBox _min_amount_text;
	private RPLabel _min_fee_label;
	private RPTextBox _min_fee_text;
	private RPLabel _onlineLabel;
	private RPTextBox _onlineText;
	private RPLabel _porxyLabel;
	private RPCButton _proxyButton;
	private RPCButton _rippledButton;
	private RPLabel _rippledFeeLabel;
	private RPLabel _rippledLabel;
	private RPTextBox _rippled_fee_Text;
	private RPCButton _saveButton;
	private javax.swing.JSeparator _spOne;
	private javax.swing.JSeparator _spTwo;

	public static void showDialog(String name, JFrame parent) {
		try {
			RPConfigDialog dialog = new RPConfigDialog(name, parent);
			dialog.pack();
			dialog.setLocationRelativeTo(parent);
			dialog.setVisible(true);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	public RPConfigDialog(String text, JFrame parent) {
		super(parent, text, Dialog.ModalityType.DOCUMENT_MODAL);
		addWindowListener(HelperWindow.get());
		setIconImage(UIRes.getIcon());
		setResizable(false);
		Dimension dim = new Dimension(445, 535);
		setPreferredSize(dim);
		setSize(dim);
		initComponents();
	}

	private void initComponents() {

		Font font = UIRes.getFont();

		addWindowListener(HelperWindow.get());

		_rippledLabel = new RPLabel();
		_min_fee_text = new RPTextBox();
		_min_amount_label = new RPLabel();
		_min_amount_text = new RPTextBox();
		_rippledButton = new RPCButton();
		_exitButton = new RPCButton();
		_spOne = new javax.swing.JSeparator();
		_min_fee_label = new RPLabel();
		_saveButton = new RPCButton();
		_porxyLabel = new RPLabel();
		_proxyButton = new RPCButton();
		_spTwo = new javax.swing.JSeparator();
		_blockchainLabel = new RPLabel();
		_blockchainButton = new RPCButton();
		_rippled_fee_Text = new RPTextBox();
		_onlineLabel = new RPLabel();
		_rippledFeeLabel = new RPLabel();
		_onlineText = new RPTextBox();
		_btc2rippleText = new RPTextBox();
		_btc2rippleLabel = new RPLabel();
		_historyLabel = new RPLabel();
		_historyText = new RPTextBox();

		getContentPane().setLayout(null);

		_rippledLabel.setFont(font); // NOI18N
		_rippledLabel.setText("Rippled Node");
		getContentPane().add(_rippledLabel);
		_rippledLabel.setBounds(10, 360, 120, 30);

		_min_fee_text.setFont(font); // NOI18N
		_min_fee_text.setText(LSystem.getFee());
		getContentPane().add(_min_fee_text);
		_min_fee_text.setBounds(320, 140, 110, 22);

		_min_amount_label.setFont(font); // NOI18N
		_min_amount_label.setText("Set "
				+ LangConfig.get(this, "amount", "Amount"));
		getContentPane().add(_min_amount_label);
		_min_amount_label.setBounds(10, 140, 90, 16);

		_min_amount_text.setFont(font); // NOI18N
		_min_amount_text.setText(LSystem.getMinSend());
		getContentPane().add(_min_amount_text);
		_min_amount_text.setBounds(130, 140, 110, 22);

		_rippledButton.setFont(font); // NOI18N
		_rippledButton.setText("Update");
		getContentPane().add(_rippledButton);
		_rippledButton.setBounds(130, 360, 110, 30);
		_rippledButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				RPSRippledDialog.showDialog(LangConfig.get(
						RPSRippledDialog.class, "update_node", "Rippled Node"),
						LSystem.applicationMain);
			}
		});

		_exitButton.setFont(font); // NOI18N
		_exitButton.setText(LangConfig.get(this, "exit", "Exit"));
		getContentPane().add(_exitButton);
		_exitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtils.close(RPConfigDialog.this);
			}
		});
		_exitButton.setBounds(350, 450, 80, 40);
		getContentPane().add(_spOne);
		_spOne.setBounds(0, 430, 460, 10);

		_min_fee_label.setFont(font); // NOI18N
		_min_fee_label.setText("Set " + LangConfig.get(this, "fee", "Fee"));
		getContentPane().add(_min_fee_label);
		_min_fee_label.setBounds(250, 140, 60, 16);

		_saveButton.setFont(font); // NOI18N
		_saveButton.setText(LangConfig.get(this, "save", "Save"));
		_saveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String amount = _min_amount_text.getText().trim();
				String fee = _min_fee_text.getText().trim();
				if (StringUtils.isNumber(amount) && StringUtils.isNumber(fee)) {
					LSystem.setMinAmountAndFee(amount, fee);
					RPToast.makeText(RPConfigDialog.this, "Save Completed",
							Style.SUCCESS).display();
				}
			}
		});
		getContentPane().add(_saveButton);
		_saveButton.setBounds(260, 450, 80, 40);

		_porxyLabel.setFont(font); // NOI18N
		_porxyLabel.setText("Proxy");
		getContentPane().add(_porxyLabel);
		_porxyLabel.setBounds(10, 260, 120, 30);

		_proxyButton.setFont(font); // NOI18N
		_proxyButton.setText("Update");
		getContentPane().add(_proxyButton);
		_proxyButton.setBounds(130, 260, 110, 30);
		_proxyButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				RPProxyDialog
						.showDialog("ProxyConfig", LSystem.applicationMain);
			}
		});
		getContentPane().add(_spTwo);
		_spTwo.setBounds(0, 240, 450, 20);

		_blockchainLabel.setFont(font); // NOI18N
		_blockchainLabel.setText("Blockchain");
		getContentPane().add(_blockchainLabel);
		_blockchainLabel.setBounds(10, 310, 120, 30);

		_blockchainButton.setFont(font); // NOI18N
		_blockchainButton.setText("Update");
		getContentPane().add(_blockchainButton);
		_blockchainButton.setBounds(130, 310, 110, 30);

		_rippled_fee_Text.setFont(font); // NOI18N
		getContentPane().add(_rippled_fee_Text);
		_rippled_fee_Text.setBounds(130, 180, 110, 22);
		_rippled_fee_Text.setEnabled(false);
		if ("unkown".equalsIgnoreCase(RPClient.ripple().getBaseFee())) {
			_rippled_fee_Text.setText(LSystem.getFee());
		} else {
			_rippled_fee_Text.setText(RPClient.ripple().getBaseFee());
		}

		_onlineLabel.setFont(font); // NOI18N
		_onlineLabel.setText("Online Wallet");
		getContentPane().add(_onlineLabel);
		_onlineLabel.setBounds(10, 20, 120, 20);

		_rippledFeeLabel.setFont(font); // NOI18N
		_rippledFeeLabel.setText("Node " + LangConfig.get(this, "fee", "Fee"));
		getContentPane().add(_rippledFeeLabel);
		_rippledFeeLabel.setBounds(10, 180, 90, 16);

		_onlineText.setFont(font); // NOI18N
		_onlineText.setText(RippleBlobObj.def_authinfo_url);
		getContentPane().add(_onlineText);
		_onlineText.setBounds(130, 20, 300, 22);

		_btc2rippleText.setFont(font); // NOI18N
		_btc2rippleText.setText(BTC2Ripple.def_bitcoin_bridge);
		getContentPane().add(_btc2rippleText);
		_btc2rippleText.setBounds(130, 100, 300, 22);

		_btc2rippleLabel.setFont(font); // NOI18N
		_btc2rippleLabel.setText("BTC2Ripple");
		getContentPane().add(_btc2rippleLabel);
		_btc2rippleLabel.setBounds(10, 100, 120, 20);

		_historyLabel.setFont(font); // NOI18N
		_historyLabel.setText("HistoryApi");
		getContentPane().add(_historyLabel);
		_historyLabel.setBounds(10, 60, 120, 20);

		_historyText.setFont(font); // NOI18N
		_historyText.setText(History.def_historyApi);
		getContentPane().add(_historyText);
		_historyText.setBounds(130, 60, 300, 22);
		getContentPane().setBackground(UIConfig.dialogbackground);

		pack();
	}
}
