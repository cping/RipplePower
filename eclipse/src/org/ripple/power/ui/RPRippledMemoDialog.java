package org.ripple.power.ui;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.address.password.PasswordEasy;
import org.json.JSONObject;
import org.ripple.power.blockchain.RippleMemoDecodes;
import org.ripple.power.blockchain.RippleMemoEncode;
import org.ripple.power.config.LSystem;
import org.ripple.power.helper.HelperWindow;
import org.ripple.power.i18n.LangConfig;
import org.ripple.power.txns.AccountFind;
import org.ripple.power.txns.NameFind;
import org.ripple.power.txns.Payment;
import org.ripple.power.txns.Rollback;
import org.ripple.power.txns.Updateable;
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
	
	private RPTextBox _amountText;
	private RPCheckBox _encodeCheckBox;
	private RPTextBox _feeAmount;
	private RPTextBox _passwordText;
	private RPLabel _recipientLabel;
	private RPCButton _exitButton;
	private RPCButton _submitButton;
	private RPCButton _resetButton;
	private RPLabel jLabel1;
	private RPLabel jLabel2;
	private RPLabel jLabel4;
	private RPLabel jLabel5;
	private RPLabel jLabel6;
	private RPList _messageList;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JSeparator jSeparator1;
	private RPTextArea _messageText;
	private RPTextBox _recipientText;
	private WalletItem _item;

	public static void showDialog(String name, JFrame parent, WalletItem item) {
		try {
			RPRippledMemoDialog dialog = new RPRippledMemoDialog(name, parent,
					item);
			dialog.pack();
			dialog.setLocationRelativeTo(parent);
			dialog.setVisible(true);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	public RPRippledMemoDialog(String text, JFrame parent, WalletItem item) {
		super(parent, text
				+ (item == null ? "" : "(" + item.getPublicKey() + ")"),
				Dialog.ModalityType.DOCUMENT_MODAL);
		this._item = item;
		this.addWindowListener(HelperWindow.get());
		this.setResizable(false);
		Dimension dim = new Dimension(518, 580);
		this.setPreferredSize(dim);
		this.setSize(dim);
		this.initComponents(_item.getPublicKey());
	}

	private void initComponents(String address) {

		Font font = GraphicsUtils.getFont(LangConfig.fontName, 0, 14);

		jScrollPane1 = new javax.swing.JScrollPane();
		_messageList = new RPList();
		jLabel1 = new RPLabel();
		jLabel2 = new RPLabel();
		jScrollPane2 = new javax.swing.JScrollPane();
		_messageText = new RPTextArea();
		_messageText.setEditable(true);
		_messageText.setFont(GraphicsUtils.getFont(15));
		_recipientLabel = new RPLabel();
		_passwordText = new RPTextBox();
		jLabel4 = new RPLabel();
		_feeAmount = new RPTextBox();
		_encodeCheckBox = new RPCheckBox();
		_encodeCheckBox.setBackground(LSystem.dialogbackground);
		jLabel5 = new RPLabel();
		_amountText = new RPTextBox();
		jSeparator1 = new javax.swing.JSeparator();
		_exitButton = new RPCButton();
		_submitButton = new RPCButton();
		_resetButton = new RPCButton();
		jLabel6 = new RPLabel();
		_recipientText = new RPTextBox();

		getContentPane().setLayout(null);

		_messageList.setFont(GraphicsUtils.getFont(Font.SANS_SERIF, 0, 12));
		_messageList.setCellRenderer(new HtmlRenderer());
		_messageList.setAutoscrolls(true);
		jScrollPane1.setViewportView(_messageList);

		getContentPane().add(jScrollPane1);
		jScrollPane1.setBounds(10, 28, 494, 170);

		jLabel1.setText("History");
		getContentPane().add(jLabel1);
		jLabel1.setBounds(10, 7, 150, 20);

		jLabel2.setText("My Message");
		getContentPane().add(jLabel2);
		jLabel2.setBounds(10, 208, 150, 20);

		_messageText.setColumns(20);
		_messageText.setRows(5);
		jScrollPane2.setViewportView(_messageText);
		_messageText.setText("Hello Rippled Message!");

		getContentPane().add(jScrollPane2);
		jScrollPane2.setBounds(10, 233, 494, 110);

		_recipientLabel.setFont(font); // NOI18N
		_recipientLabel.setText("Recipient");
		getContentPane().add(_recipientLabel);
		_recipientLabel.setBounds(10, 360, 70, 20);
		getContentPane().add(_passwordText);
		_passwordText.setBounds(80, 440, 180, 20);

		jLabel4.setFont(font); // NOI18N
		jLabel4.setText("Password");
		getContentPane().add(jLabel4);
		jLabel4.setBounds(10, 440, 70, 20);
		getContentPane().add(_feeAmount);
		_feeAmount.setBounds(340, 400, 160, 20);
		_feeAmount.setText(LSystem.FEE);

		_encodeCheckBox.setText("Message Password");
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

		jLabel5.setFont(font); // NOI18N
		jLabel5.setText("Fee");
		getContentPane().add(jLabel5);
		jLabel5.setBounds(290, 400, 50, 20);
		getContentPane().add(_amountText);
		_amountText.setBounds(80, 400, 180, 20);
		_amountText.setText(LSystem.MIN_AMOUNT);

		getContentPane().add(jSeparator1);
		jSeparator1.setBounds(0, 480, 520, 10);

		_exitButton.setText("Exit");
		_exitButton.setFont(font);
		getContentPane().add(_exitButton);
		_exitButton.setBounds(420, 500, 81, 40);
		_exitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtils.close(RPRippledMemoDialog.this);

			}
		});

		_submitButton.setText("Submit");
		_submitButton.setFont(font);
		getContentPane().add(_submitButton);
		_submitButton.setBounds(320, 500, 81, 40);
		_submitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String address = _recipientText.getText().trim();
				if (address.startsWith("~")) {
					try {
						address = NameFind.getAddress(address);
					} catch (Exception e1) {
						address = "";
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
										RPJSonLog.get().println(res.toString());
										wait.closeDialog();
									}

									@Override
									public void error(JSONObject res) {
										RPJSonLog.get().println(res.toString());
										wait.closeDialog();
									}
								});
					}
				}

			}
		});

		_resetButton.setText("Reset");
		_resetButton.setFont(font);
		getContentPane().add(_resetButton);
		_resetButton.setBounds(10, 500, 81, 40);
		_resetButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (_item != null) {
					loadMessages(_item.getPublicKey(), -1, 200);
				}
			}
		});

		jLabel6.setFont(font); // NOI18N
		jLabel6.setText("Amount");
		getContentPane().add(jLabel6);
		jLabel6.setBounds(10, 400, 70, 20);
		getContentPane().add(_recipientText);
		_recipientText.setBounds(80, 360, 420, 21);
		getContentPane().setBackground(LSystem.dialogbackground);
		loadMessages(address, -1, 200);
		pack();
	}

	private void loadMessages(String address, int min, int max) {
		if (address != null && AccountFind.isRippleAddress(address)) {
			AccountFind find = new AccountFind();
			String password = null;
			if (_encodeCheckBox.isSelected()) {
				password = _passwordText.getText();
				if (password.length() == 0) {
					password = null;
				}
			}
			final WaitDialog wait = WaitDialog.showDialog(this);
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
											return decodes.get(i).toHTML();
										}
									});
						}
					}
					wait.closeDialog();
				}
			});
		}
	}
}