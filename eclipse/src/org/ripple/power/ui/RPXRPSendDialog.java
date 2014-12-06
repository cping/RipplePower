package org.ripple.power.ui;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.json.JSONObject;
import org.ripple.power.config.LSystem;
import org.ripple.power.txns.AccountFind;
import org.ripple.power.txns.CurrencyUtils;
import org.ripple.power.txns.NameFind;
import org.ripple.power.txns.Payment;
import org.ripple.power.txns.Rollback;
import org.ripple.power.ui.graphics.LColor;
import org.ripple.power.utils.MathUtils;
import org.ripple.power.utils.StringUtils;
import org.ripple.power.wallet.WalletCache;
import org.ripple.power.wallet.WalletItem;

public class RPXRPSendDialog extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private final JTextField _addressText;
	private final JTextField _amountText;
	private final JTextField _feeText;
	private static RPXRPSendDialog lock = null;

	private RPDialogTool tool;

	public static RPXRPSendDialog showDialog(String text, Window parent,
			WalletItem item, String address, String amount, String fee,
			boolean show) {
		if (show) {
			synchronized (RPXRPSendDialog.class) {
				if (lock == null) {
					return (lock = new RPXRPSendDialog(text, parent, item,
							address, amount, fee));
				} else {
					if (lock != null) {
						lock.closeDialog();
						lock = new RPXRPSendDialog(text, parent, item, address,
								amount, fee);
					}
					return lock;
				}
			}
		}
		return null;
	}

	public static RPXRPSendDialog showDialog(String text, Window parent,
			WalletItem item, String address, String amount, String fee) {
		return showDialog(text, parent, item, address, amount, fee, true);
	}

	public static RPXRPSendDialog showDialog(String name, JFrame parent,
			WalletItem item) {
		return new RPXRPSendDialog(name, parent, item, "",
				LSystem.getMinSend(), LSystem.getFee());
	}

	public RPDialogTool get() {
		return tool;
	}

	public void closeDialog() {
		synchronized (WaitDialog.class) {
			tool.close();
			lock = null;
		}
	}

	public RPXRPSendDialog(String text, Window parent, final WalletItem item,
			String address, String amount, String fee) {

		Dimension dim = new Dimension(500, 215);
		setPreferredSize(dim);
		setSize(dim);

		final String esc = "ESCAPE";
		KeyStroke stroke = KeyStroke.getKeyStroke(esc);
		Action actionListener = new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent actionEvent) {
				closeDialog();
			}
		};
		InputMap inputMap = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(stroke, esc);
		this.getActionMap().put(esc, actionListener);

		_addressText = new JTextField(34);
		_addressText.setText(address);
		UIRes.addStyle(_addressText, "Pay to: ",false);

		_amountText = new JTextField(18);
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
		UIRes.addStyle(_amountText, "Amount: ");

		_feeText = new JTextField(5);
		UIRes.addStyle(_feeText, "Fee: ");

		setBackground(LColor.white);
		setLayout(null);

		_addressText.setBounds(70, 30, 350, 45);
		add(_addressText);

		_amountText.setBounds(70, 80, 250, 45);
		add(_amountText);

		_feeText.setBounds(330, 80, 90, 45);
		add(_feeText);

		JLabel exitLabel = new JLabel(UIRes.exitIcon);
		exitLabel.setToolTipText("Cancel");
		exitLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

		exitLabel.setBounds(260, 145, 45, 45);
		add(exitLabel);
		exitLabel.setVisible(true);
		exitLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				closeDialog();
			}
		});

		JLabel submitLabel = new JLabel(UIRes.postIcon);
		submitLabel.setToolTipText("Submit transaction");
		submitLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

		submitLabel.setBounds(200, 145, 45, 45);
		add(submitLabel);
		submitLabel.setVisible(true);

		submitLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				try {
					String address = _addressText.getText().trim();
					String amountValue = _amountText.getText().trim();

					String feeValue = _feeText.getText().trim();
					if (!MathUtils.isNan(amountValue)) {
						UIMessage.alertMessage(get().getDialog(),
								UIMessage.errMoney);
						return;
					}
					if (!MathUtils.isNan(feeValue)) {
						UIMessage.alertMessage(get().getDialog(),
								UIMessage.errFee);
						return;
					}
					if (!AccountFind.isRippleAddress(address)) {
						try {
							address = NameFind.getAddress(address);
						} catch (Exception ex) {
							UIMessage.alertMessage(get().getDialog(),
									UIMessage.errNotAddress);
							return;
						}
						if (StringUtils.isEmpty(address)
								|| !AccountFind.isRippleAddress(address)) {
							UIMessage.alertMessage(get().getDialog(),
									UIMessage.errNotAddress);
							return;
						}
					}

					BigDecimal number = new BigDecimal(amountValue);
					BigDecimal maxSend = new BigDecimal(item.getAmount());

					if (number.longValue() >= (maxSend.longValue() - 20)) {
						UIMessage.alertMessage(get().getDialog(),
								UIMessage.errNotMoney);
						return;
					}

					final WaitDialog dialog = WaitDialog.showDialog(get()
							.getDialog());

					Payment.sendXRP(item.getSeed(), address, amountValue,
							feeValue, new Rollback() {

								@Override
								public void success(JSONObject res) {
									dialog.closeDialog();
									RPJSonLog.get().println(res, false);
									WalletCache.get().reset();
									UIMessage.infoMessage(get().getDialog(),
											UIMessage.completed);
								}

								@Override
								public void error(JSONObject res) {
									dialog.closeDialog();
									RPJSonLog.get().println(res);
								}
							});

				} catch (Throwable ex) {
					ex.printStackTrace();
				}

			}
		});

		_feeText.setText(fee);
		_amountText.setText(amount);

		this.tool = RPDialogTool.show(parent, text, this, -1, -1, false,
				LSystem.MINUTE);
		revalidate();
		repaint();
	}

}
