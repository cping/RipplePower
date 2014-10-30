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
import org.ripple.power.i18n.LangConfig;
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
	private javax.swing.JSeparator jSeparator1;
	private RPTextBox _addressText;
	private RPTextBox _amountText;
	private RPTextBox _feeText;

	public static void showDialog(String name, JFrame parent, WalletItem item) {
		try {
			RPXRPSendDialog dialog = new RPXRPSendDialog(name, parent, item,
					"", "1", LSystem.FEE);
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
		//addWindowListener(HelperWindow.get());
		_feeLabel = new RPLabel();
		_addressLabel = new RPLabel();
		_amountLabel = new RPLabel();
		_addressText = new RPTextBox();
		_amountText = new RPTextBox();
		_feeText = new RPTextBox();
		jSeparator1 = new javax.swing.JSeparator();
		_sendButton = new RPCButton();
		_exitButton = new RPCButton();

		setResizable(false);
		Dimension dim = new Dimension(395, 230);
		setPreferredSize(dim);
		setSize(dim);

		getContentPane().setLayout(null);

		java.awt.Font font = new java.awt.Font(LangConfig.fontName, 0, 14);

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
		getContentPane().add(jSeparator1);
		jSeparator1.setBounds(0, 135, 389, 18);

		_sendButton.setText(LangConfig.get(this, "send", "Send"));
		_sendButton.setFont(font);
		_sendButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {

				try {
					String address = _addressText.getText().trim();
					String amountValue = _amountText.getText().trim();

					String feeValue = _feeText.getText().trim();
					if (!address.startsWith("~")) {
						if (!address.startsWith("r") || address.length() < 31) {
							RPMessage.showErrorMessage(LSystem.applicationMain,
									"Error", "无效的Ripple地址!");
							return;
						}
					}
					if (!MathUtils.isNan(amountValue)) {
						RPMessage.showErrorMessage(LSystem.applicationMain,
								"Error", "无效的发币数量!");
						return;
					}
					if (!MathUtils.isNan(feeValue)) {
						RPMessage.showErrorMessage(LSystem.applicationMain,
								"Error", "无效的手续费数量!");
						return;
					}
					if (address.startsWith("~")) {
						try {
							address = NameFind.getAddress(address);
						} catch (Exception e1) {
							RPMessage.showWarningMessage(
									LSystem.applicationMain, "Warning",
									"发送失败,无法获得当前地址数据!");
						}
						if (address == null) {
							RPMessage.showWarningMessage(
									LSystem.applicationMain, "Warning",
									"发送失败,无法获得当前地址数据!");
						}
					}

					BigDecimal number = new BigDecimal(amountValue);

					BigDecimal maxSend = new BigDecimal(item.getAmount());

					if (number.longValue() >= (maxSend.longValue() - 20)) {
						RPMessage.showErrorMessage(LSystem.applicationMain,
								"Error", "资金不足,无法发送.");
						return;
					}

					final WaitDialog dialog = WaitDialog
							.showDialog(RPXRPSendDialog.this);

					Payment.sendXRP(item.getSeed(), address, amountValue,
							feeValue, new Rollback() {

								@Override
								public void success(JSONObject res) {
									JSonLog.get().println(res.toString());
									WalletCache.get().reset();
									dialog.closeDialog();
									RPMessage.showInfoMessage(
											LSystem.applicationMain, "Info",
											"发送完毕.");
								}

								@Override
								public void error(JSONObject res) {
									JSonLog.get().println(res.toString());
									dialog.closeDialog();
								}
							});

				} catch (Throwable ex) {
					ex.printStackTrace();
				}

			}
		});
		getContentPane().add(_sendButton);
		_sendButton.setBounds(187, 159, 100, 23);

		_exitButton.setText(LangConfig.get(this, "exit", "Exit"));
		_exitButton.setFont(font);
		_exitButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				SwingUtils.close(RPXRPSendDialog.this);
			}
		});
		getContentPane().add(_exitButton);
		_exitButton.setBounds(312, 159, 57, 23);
		getContentPane().setBackground(LSystem.dialogbackground);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		SwingUtils.close(this);
	}
}
