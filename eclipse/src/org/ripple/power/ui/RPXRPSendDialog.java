package org.ripple.power.ui;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.Dictionary;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.json.JSONObject;
import org.ripple.power.config.LSystem;
import org.ripple.power.txns.PaymentSend;
import org.ripple.power.txns.Rollback;
import org.ripple.power.utils.BigDecimalUtil;
import org.ripple.power.utils.MathUtils;
import org.ripple.power.wallet.WalletCache;
import org.ripple.power.wallet.WalletItem;

public class RPXRPSendDialog extends JDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Variables declaration - do not modify
	private RPCButton jButton1;
	private RPCButton jButton2;
	private RPLabel jLabel1;
	private RPLabel jLabel2;
	private RPLabel jLabel3;
	private javax.swing.JSeparator jSeparator1;
	private RPTextBox jTextField1;
	private RPTextBox jTextField2;
	private RPTextBox jTextField3;

	// End of variables declaration

	public static void showDialog(String name, JFrame parent, WalletItem item) {
		try {
			RPXRPSendDialog dialog = new RPXRPSendDialog(name, parent, item,
					"", "1", "0.01");
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

		getContentPane().setBackground(new Color(36, 36, 36));
		jLabel1 = new RPLabel();
		jLabel2 = new RPLabel();
		jLabel3 = new RPLabel();
		jTextField1 = new RPTextBox();
		jTextField2 = new RPTextBox();
		jTextField3 = new RPTextBox();
		jSeparator1 = new javax.swing.JSeparator();
		jButton1 = new RPCButton();
		jButton2 = new RPCButton();

		setResizable(false);
		Dimension dim = new Dimension(395, 230);
		setPreferredSize(dim);
		setSize(dim);

		getContentPane().setLayout(null);

		jLabel1.setText("Fee");
		getContentPane().add(jLabel1);
		jLabel1.setBounds(10, 93, 54, 24);

		jLabel2.setText("Address");
		getContentPane().add(jLabel2);
		jLabel2.setBounds(10, 22, 54, 15);

		jLabel3.setText("Amount");
		getContentPane().add(jLabel3);
		jLabel3.setBounds(10, 55, 54, 24);

		jTextField1.setText(address);
		getContentPane().add(jTextField1);
		jTextField1.setBounds(82, 19, 297, 21);

		jTextField2.setText(amount);
		getContentPane().add(jTextField2);
		jTextField2.setBounds(82, 57, 152, 21);

		jTextField3.setText(fee);
		getContentPane().add(jTextField3);
		jTextField3.setBounds(82, 95, 99, 21);
		getContentPane().add(jSeparator1);
		jSeparator1.setBounds(0, 135, 389, 18);

		jButton1.setText("Send");
		jButton1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {

				final WaitDialog dialog = WaitDialog
						.showDialog(RPXRPSendDialog.this);

				try {
					String address = jTextField1.getText().trim();
					String amountValue = jTextField2.getText().trim();

					String feeValue = jTextField3.getText().trim();

					if (!address.startsWith("r") || address.length() < 31) {
						RPMessage.showErrorMessage(LSystem.applicationMain,
								"Error", "无效的Ripple地址!");
						return;
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

					BigDecimal number = new BigDecimal(amountValue);

					BigDecimal maxSend = new BigDecimal(item.getAmount());

					if (number.longValue() >= (maxSend.longValue() - 20)) {
						RPMessage.showErrorMessage(LSystem.applicationMain,
								"Error", "资金不足,无法发送.");
						return;
					}

					PaymentSend send = new PaymentSend();

					send.makePayment(item.getPublicKey(), item.getPrivateKey(),
							address, amountValue, feeValue, new Rollback() {

								@Override
								public void success(JSONObject res) {
									JSonLog.get().println(res.toString());
									WalletCache.get().reset();
									RPMessage.showInfoMessage(
											LSystem.applicationMain, "Info",
											"发送完毕.");
									dialog.closeDialog();
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
		getContentPane().add(jButton1);
		jButton1.setBounds(187, 159, 100, 23);

		jButton2.setText("Exit");
		jButton2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {

			}
		});
		getContentPane().add(jButton2);
		jButton2.setBounds(312, 159, 57, 23);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		setVisible(false);
		dispose();
	}

}
