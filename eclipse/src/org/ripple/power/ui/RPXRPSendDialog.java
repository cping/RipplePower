package org.ripple.power.ui;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.math.BigDecimal;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.json.JSONObject;
import org.ripple.power.config.LSystem;
import org.ripple.power.helper.HelperWindow;
import org.ripple.power.i18n.LangConfig;
import org.ripple.power.txns.AccountFind;
import org.ripple.power.txns.CurrencyUtils;
import org.ripple.power.txns.NameFind;
import org.ripple.power.txns.Payment;
import org.ripple.power.txns.Rollback;
import org.ripple.power.utils.MathUtils;
import org.ripple.power.utils.SwingUtils;
import org.ripple.power.wallet.WalletCache;
import org.ripple.power.wallet.WalletItem;

public class RPXRPSendDialog extends JDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private RPCButton _sendButton;
	private RPCButton _exitButton;
	private RPLabel _feeLabel;
	private RPLabel _addressLabel;
	private RPLabel _amountLabel;
	private javax.swing.JSeparator _jSeparator;
	private RPTextBox _addressText;
	private RPTextBox _amountText;
	private RPTextBox _feeText;

	public static void showDialog(String name, JFrame parent, WalletItem item) {
		try {
			RPXRPSendDialog dialog = new RPXRPSendDialog(name, parent, item,
					"", LSystem.getMinSend(), LSystem.getFee());
			dialog.pack();
			dialog.setLocationRelativeTo(parent);
			dialog.setVisible(true);
		} catch (Exception exc) {
			exc.printStackTrace();

		}
	}

	public static void showDialog(String name, JFrame parent, WalletItem item,
			String address, String amount, String fee) {
		try {
			RPXRPSendDialog dialog = new RPXRPSendDialog(name, parent, item,
					address, amount, fee);
			dialog.pack();
			dialog.setLocationRelativeTo(parent);
			dialog.setVisible(true);
		} catch (Exception exc) {
			exc.printStackTrace();

		}
	}

	public RPXRPSendDialog(String text, JFrame parent, final WalletItem item,
			String address, String amount, String fee) {
		super(parent, text, Dialog.ModalityType.DOCUMENT_MODAL);
		addWindowListener(HelperWindow.get());
		_feeLabel = new RPLabel();
		_addressLabel = new RPLabel();
		_amountLabel = new RPLabel();
		_addressText = new RPTextBox();
		_amountText = new RPTextBox();
		_feeText = new RPTextBox();
		_jSeparator = new javax.swing.JSeparator();
		_sendButton = new RPCButton();
		_exitButton = new RPCButton();

		setResizable(false);
		Dimension dim = new Dimension(395, 230);
		setPreferredSize(dim);
		setSize(dim);

		getContentPane().setLayout(null);

		java.awt.Font font = UIRes.getFont();

		_addressLabel.setText(LangConfig.get(this, "address", "Address"));
		_addressLabel.setFont(font);
		getContentPane().add(_addressLabel);
		_addressLabel.setBounds(10, 22, 54, 15);

		_amountLabel.setText(LangConfig.get(this, "amount", "Amount"));
		_amountLabel.setFont(font);
		getContentPane().add(_amountLabel);
		_amountLabel.setBounds(10, 55, 54, 24);

		_feeLabel.setText(LangConfig.get(this, "fee", "Fee"));
		_feeLabel.setFont(font);
		getContentPane().add(_feeLabel);
		_feeLabel.setBounds(10, 93, 54, 24);

		_addressText.setText(address);
		getContentPane().add(_addressText);
		_addressText.setBounds(82, 19, 297, 21);

		_amountText.setText(amount);
		getContentPane().add(_amountText);
		_amountText.setBounds(82, 57, 297, 21);
		_amountText.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {
				String amount = _amountText.getText().trim();
				_feeText.setText(CurrencyUtils.toFee(amount));
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}
		});

		_feeText.setText(fee);
		getContentPane().add(_feeText);
		_feeText.setBounds(82, 95, 152, 21);
		getContentPane().add(_jSeparator);
		_jSeparator.setBounds(0, 135, 389, 18);

		_sendButton.setText(LangConfig.get(this, "send", "Send"));
		_sendButton.setFont(font);
		_sendButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {

				try {
					String address = _addressText.getText().trim();
					String amountValue = _amountText.getText().trim();

					String feeValue = _feeText.getText().trim();
					if (!MathUtils.isNan(amountValue)) {
						RPMessage.showErrorMessage(LSystem.applicationMain,
								UIMessage.error, UIMessage.errMoney);
						return;
					}
					if (!MathUtils.isNan(feeValue)) {
						RPMessage.showErrorMessage(LSystem.applicationMain,
								UIMessage.error, UIMessage.errFee);
						return;
					}
					if (address.startsWith("~")) {
						try {
							address = NameFind.getAddress(address);
						} catch (Exception ex) {
							RPMessage.showWarningMessage(
									LSystem.applicationMain, UIMessage.warning,
									UIMessage.errNotAddress);
							return;
						}
						if (address == null) {
							RPMessage.showWarningMessage(
									LSystem.applicationMain, UIMessage.warning,
									UIMessage.errNotAddress);
							return;
						}
					}
					if (!AccountFind.isRippleAddress(address)) {
						RPMessage.showErrorMessage(LSystem.applicationMain,
								UIMessage.error, UIMessage.errAddress);
						return;
					}
					BigDecimal number = new BigDecimal(amountValue);

					BigDecimal maxSend = new BigDecimal(item.getAmount());

					if (number.longValue() >= (maxSend.longValue() - 20)) {
						RPMessage.showErrorMessage(LSystem.applicationMain,
								UIMessage.error, UIMessage.errNotMoney);
						return;
					}

					final WaitDialog dialog = WaitDialog
							.showDialog(RPXRPSendDialog.this);

					Payment.sendXRP(item.getSeed(), address, amountValue,
							feeValue, new Rollback() {

								@Override
								public void success(JSONObject res) {
									RPJSonLog.get().println(res);
									WalletCache.get().reset();
									dialog.closeDialog();
									RPMessage.showInfoMessage(
											LSystem.applicationMain, UIMessage.info,
											UIMessage.completed);
								}

								@Override
								public void error(JSONObject res) {
									RPJSonLog.get().println(res);
									dialog.closeDialog();
								}
							});

				} catch (Throwable ex) {
					ex.printStackTrace();
				}

			}
		});
		getContentPane().add(_sendButton);
		_sendButton.setBounds(187, 159, 105, 23);

		_exitButton.setText(LangConfig.get(this, "exit", "Exit"));
		_exitButton.setFont(font);
		_exitButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				SwingUtils.close(RPXRPSendDialog.this);
			}
		});
		getContentPane().add(_exitButton);
		_exitButton.setBounds(302, 159, 77, 23);
		getContentPane().setBackground(LSystem.dialogbackground);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		SwingUtils.close(this);
	}
}
