package org.ripple.power.ui;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;

import org.json.JSONObject;
import org.ripple.power.blockchain.RippleMemoDecodes;
import org.ripple.power.blockchain.RippleMemoEncode;
import org.ripple.power.config.LSystem;
import org.ripple.power.helper.HelperWindow;
import org.ripple.power.i18n.LangConfig;
import org.ripple.power.password.PasswordEasy;
import org.ripple.power.txns.AccountFind;
import org.ripple.power.txns.NameFind;
import org.ripple.power.txns.Payment;
import org.ripple.power.txns.Rollback;
import org.ripple.power.txns.Updateable;
import org.ripple.power.ui.RPToast.Style;
import org.ripple.power.utils.GraphicsUtils;
import org.ripple.power.utils.StringUtils;
import org.ripple.power.utils.SwingUtils;
import org.ripple.power.wallet.WalletItem;

import com.ripple.core.coretypes.Amount;

public class RPRippledMemoDialog extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Font font = UIRes.getFont();
	private RPTextBox _amountText;
	private RPCheckBox _encodeCheckBox;
	private RPTextBox _feeAmount;
	private RPTextBox _passwordText;
	private RPLabel _recipientLabel;
	private RPCButton _exitButton;
	private RPCButton _submitButton;
	private RPCButton _resetButton;
	private RPLabel _historyLabel;
	private RPLabel _my_messageLabel;
	private RPLabel _passwordLabel;
	private RPLabel _feeLabel;
	private RPLabel _amountLabel;
	private RPList _messageList;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JSeparator jSeparator1;
	private RPTextArea _messageText;
	private RPTextBox _recipientText;
	private WalletItem _item;
	private String _address, _message;

	public static void showDialog(String name, Window parent, String address,
			String message) {
		showDialog(name, parent, address, message, null);
	}

	public static void showDialog(String name, Window parent, WalletItem item) {
		showDialog(name, parent, null, null, item);
	}

	public static void showDialog(String name, Window parent, String address,
			String message, WalletItem item) {
		try {
			RPRippledMemoDialog dialog = new RPRippledMemoDialog(name, parent,
					address, message, item);
			dialog.pack();
			dialog.setLocationRelativeTo(parent);
			dialog.setVisible(true);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	public RPRippledMemoDialog(String text, Window parent, String address,
			String message, WalletItem item) {
		super(parent, text
				+ (item == null ? (address == null ? "" : "(" + address + ")")
						: "(" + item.getPublicKey() + ")"),
				Dialog.ModalityType.DOCUMENT_MODAL);
		if (item == null) {
			this._address = address;
		} else {
			this._address = item.getPublicKey();
		}
		this._message = message;
		this._item = item;
		this.addWindowListener(HelperWindow.get());
		setIconImage(UIRes.getIcon());
		this.setResizable(false);
		Dimension dim = new Dimension(518, 580);
		this.setPreferredSize(dim);
		this.setSize(dim);
		this.initComponents();
	}

	private void initComponents() {

		addWindowListener(HelperWindow.get());

		jScrollPane1 = new javax.swing.JScrollPane();
		_messageList = new RPList();
		_historyLabel = new RPLabel();
		_my_messageLabel = new RPLabel();
		jScrollPane2 = new javax.swing.JScrollPane();
		_messageText = new RPTextArea();
		_messageText.setEditable(true);
		_messageText.setFont(GraphicsUtils.getFont(15));
		_recipientLabel = new RPLabel();
		_passwordText = new RPTextBox();
		_passwordLabel = new RPLabel();
		_feeAmount = new RPTextBox();
		_encodeCheckBox = new RPCheckBox();
		_encodeCheckBox.setBackground(UIConfig.dialogbackground);
		_encodeCheckBox.setFont(font);
		_feeLabel = new RPLabel();
		_amountText = new RPTextBox();
		jSeparator1 = new javax.swing.JSeparator();
		_exitButton = new RPCButton();
		_submitButton = new RPCButton();
		_resetButton = new RPCButton();
		_amountLabel = new RPLabel();
		_recipientText = new RPTextBox();

		getContentPane().setLayout(null);

		_messageList.setFont(GraphicsUtils.getFont(Font.SANS_SERIF, 0, 12));
		_messageList.setCellRenderer(new HtmlRenderer());
		_messageList.setAutoscrolls(true);
		jScrollPane1.setViewportView(_messageList);

		getContentPane().add(jScrollPane1);
		jScrollPane1.setBounds(10, 28, 494, 170);

		_historyLabel.setText(LangConfig.get(this, "history", "History"));
		getContentPane().add(_historyLabel);
		_historyLabel.setFont(font);
		_historyLabel.setBounds(10, 5, 150, 20);

		_my_messageLabel
				.setText(LangConfig.get(this, "mmessage", "My Message"));
		getContentPane().add(_my_messageLabel);
		_my_messageLabel.setFont(font);
		_my_messageLabel.setBounds(10, 208, 150, 20);

		_messageText.setColumns(20);
		_messageText.setRows(5);
		jScrollPane2.setViewportView(_messageText);
		if (_message != null) {
			_messageText.setText(_message);
		} else {
			_messageText.setText("Hello Rippled Message!");
		}

		getContentPane().add(jScrollPane2);
		jScrollPane2.setBounds(10, 233, 494, 110);

		_recipientLabel.setFont(font); // NOI18N
		_recipientLabel.setText(LangConfig.get(this, "recipient", "Recipient"));
		getContentPane().add(_recipientLabel);
		_recipientLabel.setBounds(10, 360, 70, 20);
		getContentPane().add(_passwordText);
		_passwordText.setBounds(80, 440, 180, 20);

		_passwordLabel.setFont(font); // NOI18N
		_passwordLabel.setText(LangConfig.get(this, "password", "Password"));
		getContentPane().add(_passwordLabel);
		_passwordLabel.setBounds(10, 440, 70, 20);
		getContentPane().add(_feeAmount);
		_feeAmount.setBounds(340, 400, 160, 20);
		_feeAmount.setText(LSystem.getFee());

		_encodeCheckBox.setText(LangConfig.get(this, "mpassword",
				"Message Password"));
		getContentPane().add(_encodeCheckBox);
		_encodeCheckBox.setBounds(285, 440, 210, 23);
		if (!_encodeCheckBox.isSelected()) {
			_passwordText.setEnabled(false);
		}
		_encodeCheckBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!_encodeCheckBox.isSelected()) {
					_passwordText.setText("");
					_passwordText.setEnabled(false);
				} else {
					PasswordEasy easy = new PasswordEasy();
					_passwordText.setText(easy.pass(12, 18));
					_passwordText.setEnabled(true);
				}

			}
		});

		_feeLabel.setFont(font); // NOI18N
		_feeLabel.setText(LangConfig.get(this, "fee", "Fee"));
		getContentPane().add(_feeLabel);
		_feeLabel.setBounds(290, 400, 50, 20);
		getContentPane().add(_amountText);
		_amountText.setBounds(80, 400, 180, 20);
		_amountText.setText(LSystem.getMinSend());

		getContentPane().add(jSeparator1);
		jSeparator1.setBounds(0, 480, 520, 10);

		_exitButton.setText(UIMessage.exit);
		_exitButton.setFont(font);
		getContentPane().add(_exitButton);
		_exitButton.setBounds(420, 500, 81, 40);
		_exitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtils.close(RPRippledMemoDialog.this);

			}
		});

		_submitButton.setText(UIMessage.send);
		_submitButton.setFont(font);
		getContentPane().add(_submitButton);
		_submitButton.setBounds(320, 500, 81, 40);
		_submitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (_item == null) {
					return;
				}
				String address = _recipientText.getText().trim();
				if (address.startsWith("~")) {
					try {
						address = NameFind.getAddress(address);
					} catch (Exception ex) {
						UIRes.showWarningMessage(LSystem.applicationMain,
								UIMessage.warning, UIMessage.errNotAddress);
						return;
					}
				}
				if (AccountFind.isRippleAddress(address)) {
					String password = null;
					if (_encodeCheckBox.isSelected()) {
						password = _passwordText.getText();
						if (password.length() == 0) {
							password = null;
						}
					}
					String context = _messageText.getText();
					String amount = _amountText.getText().trim();
					String fee = _feeAmount.getText().trim();
					if (!StringUtils.isNumber(fee)) {
						RPToast.makeText(RPRippledMemoDialog.this,
								UIMessage.errFee, Style.ERROR).display();
						return;
					}
					Amount amountValue = Amount.fromString(amount);
					if (StringUtils.isNumber(amountValue.valueText())) {
						RippleMemoEncode encode = null;
						if (password == null) {
							encode = new RippleMemoEncode(
									RippleMemoEncode.Mode.BASE64, "MESSAGE",
									context, password);
						} else {
							encode = new RippleMemoEncode(
									RippleMemoEncode.Mode.ENCODE, "MESSAGE",
									context, password);
						}
						final WaitDialog wait = WaitDialog
								.showDialog(RPRippledMemoDialog.this);
						Payment.send(_item.getSeed(), amount, address, encode,
								fee, new Rollback() {

									@Override
									public void success(JSONObject res) {
										RPJSonLog.get().println(res);
										wait.closeDialog();
										RPToast.makeText(
												RPRippledMemoDialog.this,
												UIMessage.completed,
												Style.SUCCESS).display();
									}

									@Override
									public void error(JSONObject res) {
										RPJSonLog.get().println(res);
										wait.closeDialog();
									}
								});
					} else {
						RPToast.makeText(RPRippledMemoDialog.this,
								UIMessage.errMoney, Style.ERROR).display();
					}
				} else {
					RPToast.makeText(RPRippledMemoDialog.this,
							UIMessage.errAddress, Style.ERROR).display();
				}

			}
		});

		_resetButton.setText(LangConfig.get(this, "reset", "Reset"));
		_resetButton.setFont(font);
		getContentPane().add(_resetButton);
		_resetButton.setBounds(10, 500, 81, 40);
		_resetButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (_address != null) {
					loadMessages(_address, -1, 200);
				}
			}
		});

		_amountLabel.setFont(font); // NOI18N
		_amountLabel.setText(LangConfig.get(this, "amount", "Amount"));
		getContentPane().add(_amountLabel);
		_amountLabel.setBounds(10, 400, 70, 20);
		getContentPane().add(_recipientText);
		_recipientText.setBounds(80, 360, 420, 21);
		getContentPane().setBackground(UIConfig.dialogbackground);
		if (_address != null) {
			loadMessages(_address, -1, 200);
		}
		if (_item == null) {
			_submitButton.setEnabled(false);
			// _resetButton.setEnabled(false);
		}
		pack();
	}

	private void loadMessages(final String address, final int min, final int max) {

		Updateable update = new Updateable() {

			@Override
			public void action(Object o) {

				if (address != null && AccountFind.isRippleAddress(address)) {
					AccountFind find = new AccountFind();
					String password = null;
					if (_encodeCheckBox.isSelected()) {
						password = _passwordText.getText();
						if (password.length() == 0) {
							password = null;
						}
					}
					final WaitDialog wait = WaitDialog
							.showDialog(RPRippledMemoDialog.this);
					find.message(address, password, min, max, new Updateable() {

						@Override
						public void action(Object o) {
							if (o != null && o instanceof RippleMemoDecodes) {
								final RippleMemoDecodes decodes = (RippleMemoDecodes) o;
								if (decodes.size() > 0) {
									_messageList
											.setModel(new javax.swing.AbstractListModel<Object>() {
												private static final long serialVersionUID = 1L;

												public int getSize() {
													return decodes.size();
												}

												public Object getElementAt(int i) {
													return decodes.get(i)
															.toHTML();
												}
											});
								}
							}
							wait.closeDialog();
						}
					});
				}

			}
		};
		LSystem.postThread(update);
	}
}
