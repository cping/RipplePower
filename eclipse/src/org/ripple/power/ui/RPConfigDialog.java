package org.ripple.power.ui;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.ripple.power.config.LSystem;
import org.ripple.power.helper.HelperWindow;
import org.ripple.power.i18n.LangConfig;
import org.ripple.power.ui.RPToast.Style;
import org.ripple.power.utils.StringUtils;
import org.ripple.power.utils.SwingUtils;

public class RPConfigDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private RPCButton _blockchainButton;
	private RPCButton _exitButton;
	private RPTextBox _min_amount_text;
	private RPTextBox _min_fee_text;
	private RPCButton _proxyButton;
	private RPCButton _rippledButton;
	private RPCButton _saveButton;
	private RPLabel _nodeLabel;
	private RPLabel _amountLabel;
	private RPLabel _feeLabel;
	private RPLabel _proxyLabel;
	private RPLabel _blockLabel;
	private javax.swing.JSeparator jSeparator1;
	private javax.swing.JSeparator jSeparator2;

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
		setResizable(false);
		Dimension dim = new Dimension(325, 430);
		setPreferredSize(dim);
		setSize(dim);
		initComponents();
	}

	private void initComponents() {

		Font font = UIRes.getFont();

		addWindowListener(HelperWindow.get());
		_nodeLabel = new RPLabel();
		_min_fee_text = new RPTextBox();
		_min_fee_text.setText(LSystem.getFee());
		_amountLabel = new RPLabel();
		_min_amount_text = new RPTextBox();
		_min_amount_text.setText(LSystem.getMinSend());
		_rippledButton = new RPCButton();
		_exitButton = new RPCButton();
		jSeparator1 = new javax.swing.JSeparator();
		_feeLabel = new RPLabel();
		_saveButton = new RPCButton();
		_proxyLabel = new RPLabel();
		_proxyButton = new RPCButton();
		jSeparator2 = new javax.swing.JSeparator();
		_blockLabel = new RPLabel();
		_blockchainButton = new RPCButton();

		getContentPane().setLayout(null);

		_nodeLabel.setFont(font); // NOI18N
		_nodeLabel.setText("Rippled Node");
		getContentPane().add(_nodeLabel);
		_nodeLabel.setBounds(10, 180, 90, 30);

		_min_fee_text.setFont(font); // NOI18N
		getContentPane().add(_min_fee_text);
		_min_fee_text.setBounds(100, 70, 210, 22);

		_amountLabel.setFont(font); // NOI18N
		_amountLabel.setText(LangConfig.get(this, "amount", "Amount"));
		getContentPane().add(_amountLabel);
		_amountLabel.setBounds(10, 21, 90, 16);

		_min_amount_text.setFont(font); // NOI18N
		getContentPane().add(_min_amount_text);
		_min_amount_text.setBounds(100, 20, 210, 22);

		_rippledButton.setFont(font); // NOI18N
		_rippledButton.setText("Update");
		_rippledButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				RPSRippledDialog.showDialog(LangConfig.get(
						RPSRippledDialog.class, "update_node", "Rippled Node"),
						LSystem.applicationMain);
			}
		});
		getContentPane().add(_rippledButton);
		_rippledButton.setBounds(110, 180, 200, 30);

		_exitButton.setFont(font); // NOI18N
		_exitButton.setText(LangConfig.get(this, "exit", "Exit"));
		_exitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtils.close(RPConfigDialog.this);
			}
		});
		getContentPane().add(_exitButton);
		_exitButton.setBounds(240, 360, 70, 30);
		getContentPane().add(jSeparator1);
		jSeparator1.setBounds(0, 340, 330, 10);

		_feeLabel.setFont(font); // NOI18N
		_feeLabel.setText(LangConfig.get(this, "fee", "Fee"));
		getContentPane().add(_feeLabel);
		_feeLabel.setBounds(10, 72, 90, 16);

		_saveButton.setFont(font); // NOI18N
		_saveButton.setText(LangConfig.get(this, "save", "Save"));
		getContentPane().add(_saveButton);
		_saveButton.setBounds(160, 360, 70, 30);
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

		_proxyLabel.setFont(font); // NOI18N
		_proxyLabel.setText("Proxy");
		getContentPane().add(_proxyLabel);
		_proxyLabel.setBounds(10, 220, 90, 30);

		_proxyButton.setFont(font); // NOI18N
		_proxyButton.setText("Update");
		_proxyButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				RPProxyDialog
						.showDialog("ProxyConfig", LSystem.applicationMain);
			}
		});
		getContentPane().add(_proxyButton);
		_proxyButton.setBounds(110, 220, 200, 30);
		getContentPane().add(jSeparator2);
		jSeparator2.setBounds(0, 160, 330, 10);

		_blockLabel.setFont(font); // NOI18N
		_blockLabel.setText("Blockchain");
		getContentPane().add(_blockLabel);
		_blockLabel.setBounds(10, 260, 90, 30);

		_blockchainButton.setFont(font); // NOI18N
		_blockchainButton.setText("Update");
		getContentPane().add(_blockchainButton);
		_blockchainButton.setBounds(110, 260, 200, 30);

		getContentPane().setBackground(LSystem.dialogbackground);

		pack();
	}
}
