package org.ripple.power.ui;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.json.JSONObject;
import org.ripple.power.config.LSystem;
import org.ripple.power.txns.AccountFind;
import org.ripple.power.txns.AccountInfo;
import org.ripple.power.txns.AccountLine;
import org.ripple.power.txns.IssuedCurrency;
import org.ripple.power.txns.NameFind;
import org.ripple.power.txns.Payment;
import org.ripple.power.txns.Rollback;
import org.ripple.power.txns.Updateable;
import org.ripple.power.ui.graphics.LColor;
import org.ripple.power.ui.view.RPJSonLog;
import org.ripple.power.ui.view.WaitDialog;
import org.ripple.power.utils.MathUtils;
import org.ripple.power.utils.StringUtils;
import org.ripple.power.wallet.WalletCache;
import org.ripple.power.wallet.WalletItem;

public class RPSendIOUDialog extends JPanel implements WindowListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final JComboBox<Object> _curList;
	private final JTextField _addressText;
	private final JTextField _amountText;
	private final JTextField _feeText;
	private final JTextField _destinationTag;
	private final JLabel _submitLabel;

	private static RPSendIOUDialog lock = null;
	private ArrayList<WaitDialog> _waitDialogs = new ArrayList<WaitDialog>(10);

	private RPDialogTool tool;

	public static RPSendIOUDialog showDialog(String text, Window parent, WalletItem item, String address, String amount,
			String fee, boolean show) {
		if (show) {
			synchronized (RPSendIOUDialog.class) {
				if (lock == null) {
					return (lock = new RPSendIOUDialog(text, parent, item, address, amount, fee));
				} else {
					if (lock != null) {
						lock.closeDialog();
						lock = new RPSendIOUDialog(text, parent, item, address, amount, fee);
					}
					return lock;
				}
			}
		}
		return null;
	}

	public static RPSendIOUDialog showDialog(String text, Window parent, WalletItem item, String address, String amount,
			String fee) {
		return showDialog(text, parent, item, address, amount, fee, true);
	}

	public static RPSendIOUDialog showDialog(String name, JFrame parent, WalletItem item) {
		return new RPSendIOUDialog(name, parent, item, "", LSystem.getMinSend(), LSystem.getFee());
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

	public RPSendIOUDialog(String text, Window parent, final WalletItem item, String address, String amount,
			String fee) {

		Dimension dim = new Dimension(500, 290);
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

		_curList = new JComboBox<Object>();
		_curList.setBackground(LColor.white);
		_curList.setEditable(true);
		_curList.setModel(new javax.swing.DefaultComboBoxModel<Object>(new Object[] { "Empty" }));
		UIRes.addStyle(_curList, "Currency from: ");

		_addressText = new JTextField(34);
		_addressText.setText(address);
		UIRes.addStyle(_addressText, "Pay to: ", false);

		_amountText = new JTextField(18);

		UIRes.addStyle(_amountText, "Amount: ");

		_feeText = new JTextField(5);
		UIRes.addStyle(_feeText, "Fee: ");

		_destinationTag = new JTextField(34);
		_destinationTag.setText(address);
		UIRes.addStyle(_destinationTag, "Destination Tag: ", false);

		setBackground(LColor.white);
		setLayout(null);

		_curList.setBounds(70, 30, 350, 45);
		add(_curList);

		_addressText.setBounds(70, 80, 350, 45);
		add(_addressText);

		_amountText.setBounds(70, 130, 250, 45);
		add(_amountText);

		_feeText.setBounds(330, 130, 90, 45);
		add(_feeText);

		_destinationTag.setText("");
		_destinationTag.setBounds(70, 180, 350, 45);
		add(_destinationTag);

		JLabel exitLabel = new JLabel(UIRes.exitIcon);
		exitLabel.setToolTipText("Cancel");
		exitLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

		exitLabel.setBounds(260, 235, 45, 45);
		add(exitLabel);
		exitLabel.setVisible(true);
		exitLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				closeDialog();
			}
		});

		_submitLabel = new JLabel(UIRes.postIcon);
		_submitLabel.setToolTipText("Submit transaction");
		_submitLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

		_submitLabel.setBounds(200, 235, 45, 45);
		add(_submitLabel);
		_submitLabel.setVisible(true);

		_submitLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String address = _addressText.getText().trim();
				String amount = _amountText.getText().trim();
				String fee = _feeText.getText().trim();
				Object o = _curList.getSelectedItem();
				String destinationTag = _destinationTag.getText().trim();
				if (!MathUtils.isNan(amount)) {
					UIMessage.alertMessage(get().getDialog(), UIMessage.errMoney);
					return;
				}
				if (!MathUtils.isNan(fee)) {
					UIMessage.alertMessage(get().getDialog(), UIMessage.errFee);
					return;
				}
				IssuedCurrency cur = null;
				if (o instanceof String) {
					cur = new IssuedCurrency((String) o);
				} else if (o instanceof IssuedCurrency) {
					cur = (IssuedCurrency) o;
				} else if (o instanceof AccountLine) {
					AccountLine line = (AccountLine) o;
					cur = new IssuedCurrency(amount, line.getIssuer(), line.getCurrency());
					Double a = Double.parseDouble(amount);
					Double b = Double.parseDouble(line.getBalance());
					if (a > b) {
						UIMessage.alertMessage(get().getDialog(), UIMessage.errNotMoney);
						return;
					}
				} else {
					UIMessage.alertMessage(get().getDialog(), UIMessage.errNotAddress);
					return;
				}
				if (!AccountFind.isRippleAddress(address)) {
					try {
						address = NameFind.getAddress(address);
					} catch (Exception ex) {
						UIMessage.alertMessage(get().getDialog(), UIMessage.errNotAddress);
						return;
					}
					if (StringUtils.isEmpty(address) || !AccountFind.isRippleAddress(address)) {
						UIMessage.alertMessage(get().getDialog(), UIMessage.errNotAddress);
						return;
					}
				}
				final WaitDialog dialog = WaitDialog.showDialog(get().getDialog());
				_waitDialogs.add(dialog);

				long tagNumber = StringUtils.isEmpty(destinationTag) ? MathUtils.randomLong(1, 999999999)
						: Integer.parseInt(destinationTag);
				Payment.send(item.getSeed(), address, cur, fee, tagNumber, new Rollback() {

					@Override
					public void success(JSONObject res) {
						dialog.closeDialog();
						RPJSonLog.get().println(res, false);
						WalletCache.get().reset();
						UIMessage.infoMessage(get().getDialog(), UIMessage.completed);
					}

					@Override
					public void error(JSONObject res) {
						dialog.closeDialog();
						RPJSonLog.get().println(res);
					}
				});
			}
		});

		_feeText.setText(fee);
		_amountText.setText(amount);
		calldisable();
		this.tool = RPDialogTool.show(parent, text, this, -1, -1, false, LSystem.MINUTE);
		if (item != null) {
			loadIOUs(item.getPublicKey());
		}
	}

	public void calldisable() {
		_submitLabel.setEnabled(false);
		_feeText.setEnabled(false);
		_addressText.setEnabled(false);
		_amountText.setEnabled(false);
	}

	public void callactivity() {
		_submitLabel.setEnabled(true);
		_feeText.setEnabled(true);
		_addressText.setEnabled(true);
		_amountText.setEnabled(true);
	}

	private void loadIOUs(final String address) {
		Updateable update = new Updateable() {

			@Override
			public void action(Object o) {
				final WaitDialog dialog = WaitDialog.showDialog(get().getDialog());
				_waitDialogs.add(dialog);
				final AccountInfo info = new AccountInfo();
				Updateable accountline = new Updateable() {
					@Override
					public void action(Object res) {
						if (info.lines.size() > 0) {
							_curList.setModel(new javax.swing.DefaultComboBoxModel<Object>(info.lines.toArray()));
							callactivity();
						} else {
							calldisable();
						}
						dialog.closeDialog();
					}
				};
				AccountFind find = new AccountFind();
				find.processLines(address, info, accountline);
			}
		};
		LSystem.postThread(update);
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
